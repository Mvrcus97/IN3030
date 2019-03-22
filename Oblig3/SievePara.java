import java.lang.Math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
public class SievePara{
  int n,nn, cores;
  int[] primes;
  int currentPrimeIndex = 0;
  byte[] sieve;

  Thread[] workers, fWorkers;
  CyclicBarrier barrier, internBarrier;
  ReentrantLock lock;
  Oblig3Precode precode;
  int currentFactor;
  Monitor monitor;



  public SievePara(int n, int k){
    this.n = n;
    this.nn = n*n;
    this.sieve = new byte[nn / 16 + 1];
    this.cores = ((k==0) ? Runtime.getRuntime().availableProcessors(): k);
    this.workers = new Thread[cores];
    this.fWorkers = new Thread[cores];
    this.barrier = new CyclicBarrier(cores+1);
    this.internBarrier = new CyclicBarrier(cores);
    this.lock = new ReentrantLock();

    System.out.println("Threads available: " + this.cores);
    // 1 is defined as not being a prime.
    crossOut(1);
    //TODO too large
    int size = n/3;
    if(n < 100){
      size = 25;
    }
    primes = new int[size];

    //Hardcode 2 as prime, the only Even prime number.
    primes[currentPrimeIndex++] = 2;
  }//end Constructor

  /**
   * This method returns primes up to N
   *
   *
   * @return Array of primes, with blank cells at the end
   */
  public int[] getPrimes(){
    int currentPrime = 3;
    int sqrtN;
    if(n > 100000){
      sqrtN = (int) Math.sqrt(n);
    }else{
      sqrtN = n;
    }

    //We now need to find the first couple of primes.
    findFirstPrimes(sqrtN);

    /*System.out.println("Primes after firstPrimes ");
    for(int i = 0; i < currentPrimeIndex; i++){
    System.out.println(primes[i]);
  }*/

    //We have now found all primes up to sqrtN. Let's split the primes we found
    //Amoung a number of threads, and let them traverse further.
    createThreads(1,currentPrimeIndex); // Start from 1 to avoid traverse primes[0] = 2

    //Threads are done. Now save their result.
    collectPrimes(primes[currentPrimeIndex-1]+2, n);


    /*System.out.println("Primes after thread work: ");
    for(int i = 0; i < currentPrimeIndex; i++){
      System.out.println(primes[i]);
    }*/
    return primes;
   }// end getPrimes

 public void findFirstPrimes(int stop){
   if(stop % 2 == 1){
     // Find up to but not including stop, will result in error if stop IS A PRIME!
     stop ++;
   }
   int currentPrime = 3;
   while(currentPrime <= stop && currentPrime != -1){
     traverse(currentPrime, stop);
     primes[currentPrimeIndex++] = currentPrime;
     currentPrime = getNextPrime(currentPrime+2, stop);
   }
 }//end findFirstPrimes

 private void traverse(int currentPrime, int stop) {
   if(currentPrime % 2 == 0){
     System.out.printf("ERROR Traverse with a even number %d\n", currentPrime);
     currentPrime ++;
   }

   // Remember assumption 2 and 3
   for(int i=currentPrime * currentPrime; i <stop ; i += currentPrime * 2){
     crossOut(i);
   }
 }// End traverse


 private void createThreads(int from, int to){
   //Round Robin / Card Dealer . This is to deal with the load imbalance of the earlier primes.
   int[][] threadWork = new int[cores][((currentPrimeIndex-1)/cores)+1]; // 2D array , [k][j] -> thread k append prime j.
   int pos = 0;
   int i = from; // only traverse at the given region of primes[i].
   while(i < to){
     for(int j = 0; j<workers.length; j++){
       threadWork[j][pos] = primes[i];
       //System.out.println("["+j+"]["+pos+"]: "+primes[i]);
       i ++;
     }
     pos ++;
   }//end While

   //We now know what thread should do what work. Let them begin.
   for( i = 0; i < cores; i++){
     workers[i] = new Thread(new Worker(i, threadWork[i], n));
     workers[i].start();
   }
   //Wait for threads to do their job.
   try{barrier.await();
   }catch(Exception e){}
 }//End createThreads

  //Go through the sieve, save all leftover primes.
  private void collectPrimes(int searchFrom, int searchTo){
    if(searchFrom % 2 == 0){
      System.out.println("Error. collecting from an even number");
      searchFrom ++;
    }
    for( int i=searchFrom; i<searchTo; i+=2){
      if(isPrime(i)){
        primes[currentPrimeIndex++] = i;
        //System.out.println("found prime: " + i);
      }
    }
  }//end collectPrimes


  private int getNextPrime(int searchFrom, int searchTo) {
    if(searchFrom % 2 == 0){
      System.out.printf("ERROR Searching with a even number %d\n", searchFrom);
      searchFrom ++;
    }

    for(int i=searchFrom; i < searchTo; i += 2) {
      if(isPrime(i)) {
        return i;
      }
    }
    // The loop above ran to completion, which means that we found no prime
    // less than searchTo, and we're done
    return -1;
  }




  // Cross out a given number from the sieve
  private void crossOut(int number) {

    // Again not considering even numbers gives 16 numbers per array cell
    // Spreading the 8 odd number over 8 bits per byte
    int cell = number / 16;
    int bit = (number / 2) % 8;

    //System.out.printf("Cross out %d (byte %d) before OR with bit %d : %s\n",number, cell,bit, bitString(sieve[cell]));
        // | is bitwise OR
        // |= is the same as += just with bitwise OR instead of addition

        // 1 << bit shifts a binary 1 to the right position in the byte
        // Example: 1 << 2 becomes 00000100 in binary
    sieve[cell] |= (1 << bit);
    //System.out.printf("Cross out %d (byte %d) after OR with bit %d : %s\n",number, cell,bit, bitString(sieve[cell]));
  }

  private boolean isPrime(int number) {
    int cell = number / 16;
    int bit = (number / 2) % 8;
    // See the previous method
    // If this bit hasn't been crossed out the & operation (bitwise AND) will result
    // in the bits 00000000 which equals 0
    return (sieve[cell] & (1 << bit)) == 0;
  }


  private String bitString(byte b) {
    return Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
  }


  /*
  *   Inner class Worker represents a thread.
  *   Each thread will traverse through the existing primes,
  *   removing all multiples of the primes.
  */
  public class Worker implements Runnable{
    int id;
    int[] primesLocal;
    int currentPrimeIndexLocal;
    byte[] primesByteLocal;
    int n;

    public Worker(int id, int primes[], int n){
        this.id = id;
        this.primesLocal = primes;
        this.currentPrimeIndexLocal = 0;
        this.primesByteLocal= new byte[n / 16 + 1];
        this.n = n;
        crossOutLocal(1);

        /*System.out.printf("id: %d, n: %d\n", id,n);
        for(int i = 0; i < primes.length; i++){
        System.out.println("id: " + id + ", primes: " + primes[i]);
      }*/
    }


    private void traverseLocal(int currentPrime){
        if(currentPrime % 2 == 0){
    			System.out.printf("ERROR TraverseLocal with an even number %d, thread: %d. \n", currentPrime, this.id);
        }
        // Remember assumption 2 and 3
    		for(int i=currentPrime * currentPrime; i <this.n ; i += currentPrime * 2){
          crossOutLocal(i);
          //System.out.println("Crossing out " + i);
        }
    }

      private void crossOutLocal(int number){
        // Again not considering even numbers gives 16 numbers per array cell
        // Spreading the 8 odd number over 8 bits per byte
        int cell = number / 16;
        int bit = (number / 2) % 8;

      //System.out.printf("Cross out %d (byte %d) before OR with bit %d : %s\n",number, cell,bit, bitString(primesByteLocal[cell]));

            // | is bitwise OR
            // |= is the same as += just with bitwise OR instead of addition

            // 1 << bit shifts a binary 1 to the right position in the byte
            // Example: 1 << 2 becomes 00000100 in binary

        primesByteLocal[cell] |= (1 << bit);

       //System.out.printf("Cross out %d (byte %d) after OR with bit %d : %s\n",number, cell,bit, bitString(primesByteLocal[cell]));
      }// End crossOutLocal

      //This method merges the local sieve with the shared sieve, by storing OR of each byte.
      public void mergeSieves(){
        lock.lock();
        try{
          int a, b, c;
          //System.out.println("Thread: " + id + " merging..");
          for(int i = 0; i<primesByteLocal.length; i++){
            a = sieve[i];
            b = primesByteLocal[i];
            c = a | b;
            sieve[i] = (byte) c;
          }
        }
        finally{lock.unlock();}
      }

      //This method is called once Thread.start() is initialized.
      public void run() {
        //For each prime, cross out.
        for( int i = 0; i < primesLocal.length; i++){
          if (primesLocal[i] == 0) break; // Done
          if (primesLocal[i] == 2) continue; //One thread has to deal with 2... just skip this traverse.
          traverseLocal(primesLocal[i]);
        }

        mergeSieves();
        //Finally, let everyone know this thread is done.
        //System.out.println("Thread: " + id + " is done. ");
        try{barrier.await();
        }catch(Exception e){return;}
      }//End run
    }//End worker

    //We need to expand these because we now need to find all primes up to N*N instead of just N.
    public void expandPrimes(){
      long size1,size2;
      size1 = sieve.length*n;
      size2 = primes.length*n;

      byte[] sieve2 = new byte[(int) size1];
      int[] primes2 = new int[(int) size2];

      System.arraycopy( sieve, 0, sieve2, 0, sieve.length );
      System.arraycopy( primes, 0, primes2, 0, primes.length );

      this.sieve = sieve2;
      this.primes = primes2;
    }


  public void factorizeSeq(){
    //We only have primes upto N. need to crossout up to N*N.
    double start, stop;
    start = System.nanoTime();
    expandPrimes();
    traverse((primes[currentPrimeIndex-1]+2), nn);
    stop = (System.nanoTime() -start)/1000000.0;
    System.out.println("Time doing shit seq :  " + stop);

    this.precode = new Oblig3Precode(n);
    for(int i = nn-1; i >= nn-100; i--){
      getFactors(i);
    }
    precode.writeFactors();
  }

  public void getFactors(int num){
    int currNum = num;
    for(int i =0; i < currentPrimeIndex; i++){
      //System.out.println("i: " + i + ", primes[i]: " + primes[i]);
      if( (currNum % primes[i]) == 0){
        precode.addFactor(num, primes[i]);
        currNum = currNum/primes[i];
        i = -1; // to get primes[0] next iteration
      }
    }
  }

  public void factorizePara(){
    //We only have primes upto N. need to crossout u to N*N
    expandPrimes();
    createThreads((currentPrimeIndex-1)+2, nn);
    int nn = n*n;
    int amount_to_factorize = 100;
    monitor = new Monitor(amount_to_factorize, nn);

    //Round Robin / Card Dealer . This is to deal with the load imbalance of the earlier primes being used more frequently.
    int[][] threadWork = new int[cores][((currentPrimeIndex-1)/cores)+1]; // 2D array , [k][j] -> thread k append prime j.
    int pos = 0;
    int i = 0;

    while(i < currentPrimeIndex){
      for(int j = 0; j<workers.length; j++){
        threadWork[j][pos] = primes[i];
        //System.out.println("["+j+"]["+pos+"]: "+primes[i]);
        i ++;
      }
      pos ++;
    }//end While

    //We now know what thread should do what work. Let them begin.
    for(i = 0; i < cores; i++){
      fWorkers[i] = new Thread(new FactoryWorker(i, threadWork[i]));
      fWorkers[i].start();
    }

    //Update monitor with each base.
    for( i = nn-1; i >= nn-100; i--){
      try{
        System.out.println("master putting : "+ i);
        monitor.putNum(i);
      }catch (Exception e){return;}
    }
  }//End factorizePara


  //Thread for factorizing
  public class FactoryWorker implements Runnable{
    int id;
    int[] primesLocal;

    public FactoryWorker(int id, int[] primes){
        this.id = id;
        this.primesLocal = primes;
    }//end Constructor

      //This method factorizses a number given by a monitor.
      public void getLocalFactors() throws Exception{
        int currNum = monitor.getNum();
        int base = monitor.getBase(); // Already synchronized because of line above

        while(currNum != -1){
          for(int i = 0; i <primesLocal.length; i++){
            if(primesLocal[i] == 0) break; // checked through all local primes.
            if((currNum % primesLocal[i]) == 0){
              //Found a factor.
              System.out.println("id: " + id + "found factor: " + primesLocal[i] + " for currNum:" + currNum + " and base: " + base);
              monitor.updateNum(currNum/primesLocal[i], primesLocal[i], currNum);
              break;
            }
          }//end for
          currNum = monitor.getNum(); // Ask monitor for current number.
          base = monitor.getBase(); // Don't need to syncrhonize this, as line above will sync for us
          System.out.println("currnum: " + currNum + " currbase  " + base);
        }//end while
      }//end getLocalFactors


      public void run(){
        try{
          getLocalFactors();
        }catch (Exception e){return;}
      }//end run
    }//end FactoryWorker


}// End SievePara
