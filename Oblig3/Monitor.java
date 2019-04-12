import java.util.concurrent.locks.*;
public class Monitor {
  long n; // number of primes
  int n_given, n_done;
  long currentBase;
  long currentNum;
  int threads;
  int miss_counter;


  final Lock lock = new ReentrantLock();
  final Condition notGiven = lock.newCondition();   // kø for Master
  final Condition notFactorized = lock.newCondition(); // kø for Workers

  Oblig3Precode precode;

  public Monitor(long n, int precode, int threads ){
    this.n = n;
    this.threads = threads;
    this.n_given = 0;
    this.n_done = 0;
    this.currentNum = 1;
    this.currentBase = 1;
    this.precode = new Oblig3Precode(precode);
    this.miss_counter = 0;
  }

  public boolean putNum(long newNum) throws InterruptedException{
    lock.lock();
    try{
      while( n_given < n && currentNum != 1){
        notGiven.await(); // wait while workers factorize.
      }
      if(n_given < n ){
        //Give a new number to factorize.
        currentBase = newNum;
        currentNum = newNum;
        n_given ++;
        miss_counter = 0;
        notFactorized.signalAll(); // Wake up all threads
        return true;
      }else if( n_given == n){
        //System.out.println("Given all numbers!");
        notFactorized.signalAll();
        notGiven.signalAll();
      }
      return false;
    }finally{lock.unlock();}
  }//end putNum


  public long getNum(long current_thread_num) throws InterruptedException{
    long ret;
    lock.lock();
    try{
      while( n_done < n && currentNum == 1 ) {
        ////System.out.println("sleeping... factor found:" + factor_found  + " misses: " + not_found_counter);
        notFactorized.await(); // Wait for a new base.
      }
      while( current_thread_num == currentNum){
        miss_counter ++;
        if(miss_counter == threads){
          //FOUND NEW PRIME.
          //System.out.println("Adding new prime:" + currentNum);
          if(currentNum != 1) precode.addFactor(currentBase, currentNum);
          n_done ++;
          currentNum = 1;
          notGiven.signalAll();
          if(n_done == n){
            //System.out.println("ALL DONE");
            currentNum = -1;
            notGiven.signalAll();
            notFactorized.signalAll();
            precode.writeFactors();
          }
        }
        notFactorized.await();
      }


      if(n_done < n){
        ret = currentNum;
      }else{
        notFactorized.signalAll();
        notGiven.signalAll();
        ret = -1;
      }
      return ret;
    }finally{ lock.unlock();}
  }//end getNum


  public void updateNum(long factor, long current_thread_num) throws InterruptedException{
    lock.lock();
    try{
      if(currentNum/factor == 1){
        //Done with this base.
        n_done ++;
        //System.out.println("---Done with base: " + currentBase + " , last factor: " + factor +", n_done: " + n_done + "-----");
        precode.addFactor(currentBase, factor);
        if(n_done == n){
          //System.out.println("ALL DONE");
          currentNum = -1;
          notGiven.signalAll();
          notFactorized.signalAll();
          precode.writeFactors();
        }
        currentNum = 1;
        notGiven.signalAll(); // Signal we need a new base!

      }else{
        //Regular update num.
        miss_counter = 0;
        //System.out.println("Found factor for " + currentNum + " , factor: " + factor );
        precode.addFactor(currentBase, factor);
        currentNum = currentNum/factor;
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
