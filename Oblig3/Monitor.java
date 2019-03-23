import java.util.concurrent.locks.*;
public class Monitor {
  long n; // number of primes
  int n_given, n_done;
  long currentBase;
  long currentNum;
  int threads;
  int not_found_counter;
  boolean factor_found;


  final Lock lock = new ReentrantLock();
  final Condition notGiven = lock.newCondition();   // kø for Master
  final Condition notFactorized = lock.newCondition(); // kø for Workers

  final Condition notCurrentDone = lock.newCondition(); //kø for Workers, rapportere om de fant faktor..
  final Condition notAllReported = lock.newCondition(); //kø for master, vent til alle har fortalt om di fant en faktor.
  Oblig3Precode precode;

  public Monitor(long n, int precode, int threads ){
    this.n = n;
    this.threads = threads;
    this.n_given = 0;
    this.n_done = 0;
    this.currentNum = 1;
    this.currentBase = 1;
    this.precode = new Oblig3Precode(precode);
    this.not_found_counter = 0;
  }

  public void putNum(long newNum) throws InterruptedException{
    lock.lock();
    try{
      while( n_given < n && currentNum != 1){
        notGiven.await(); // wait while workers factorize.
      }
      if(n_given < n ){
        //Give a new number to factorize.
        currentBase = newNum;
        currentNum = newNum;
        factor_found = true;
        not_found_counter = 0;
        n_given ++;
        notFactorized.signalAll();
      }else if( n_given == n){
        System.out.println("Given all numbers!");
      }
    }finally{lock.unlock();}
  }//end putNum


  public long getNum(long current_thread_num) throws InterruptedException{
    long ret;
    lock.lock();
    try{
      if(current_thread_num == currentNum) not_found_counter ++;
      else factor_found = true;

      if(not_found_counter == threads){
        //none found a factor. we found a new prime!
        //System.out.println("No threads found factor. MONITOR Found new prime: " +  currentNum);
        precode.addFactor(currentBase, currentNum);
        n_done ++;
        if(n_done == n){
          precode.writeFactors();
          System.out.println("----FINISHED.----");
          notGiven.signalAll();
          notFactorized.signalAll();
          currentNum = 1;
          return -1;

        }
        not_found_counter = 0;
        currentNum = 1;
        notGiven.signal();
        factor_found = true;
      }
      while( (n_done < n && currentNum == 1) || !(factor_found) ) {
        //System.out.println("sleeping... factor found:" + factor_found  + " misses: " + not_found_counter);
        notFactorized.await(); // Wait for a new base.
      }

      if(n_done < n){
        ret = currentNum;
      }else{
        notFactorized.signalAll();
        ret = -1;
      }
      return ret;
    }finally{ lock.unlock();}
  }//end getNum


  public void updateNum(long new_num, long factor, long current_thread_num) throws InterruptedException{
    lock.lock();
    try{
      if(current_thread_num != currentNum){
        //Someone else already found a factor for currentNum..
        return;
      }
      if(new_num == 1){
        //Done with this base.
        n_done ++;
        //System.out.println("---Done with base: " + currentBase + " -----");
        precode.addFactor(currentBase, factor);
        if(n_done == n){
          System.out.println("ALL DONE");
          currentNum = 1;
          notFactorized.signalAll();

          precode.writeFactors();
        }
        currentNum = new_num;
        notGiven.signal(); // Signal we need a new base!
      }else{
        //Regular update num.
        precode.addFactor(currentBase, factor);
        currentNum = new_num;
        notFactorized.signalAll();
      }



    }finally{lock.unlock();}
  }//end updateNum


  public long getBase()throws InterruptedException{
    lock.lock();
    try{
      if(n_done < n){
        return currentBase;
      }
      return 0;
    } finally{lock.unlock();}
  }//end getBase





}//end Monitor
