import java.lang.Math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
public class SievePara{
  int n, cores;
  int[] primes;
  int currentPrimeIndex = 0;
  byte[] sieve;
  Thread[] threads;
  CyclicBarrier barrier;
  ReentrantLock lock;


public SievePara(int n, int k){
  this.n = n;
  this.sieve = new byte[n / 16 + 1];
  this.cores = ((k==0) ? Runtime.getRuntime().availableProcessors(): k);
  this.threads = new Thread[cores]; //TODO k?
  this.barrier = new CyclicBarrier(cores+1);
  this.lock = new ReentrantLock();
  System.out.println("Cores available: " + this.cores);

  // 1 is defined as not being a prime.
  crossOut(1);
  //TODO too large
  primes = new int[n/3];

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
  boolean parallel;
  if(n > 100000){
    sqrtN = (int) Math.sqrt(n);
    parallel = true;
  }else{
    sqrtN = n;
    parallel = false;
  }

  //We now need to find the first couple of primes.
  System.out.println("sqrt is: " + sqrtN);
  findFirstPrimes(sqrtN);

  /*System.out.println("Primes after firstPrimes ");
  for(int i = 0; i < currentPrimeIndex; i++){
  System.out.println(primes[i]);
}*/

  //We have now found all primes up to sqrtN. Let's split the primes we found
  //Amoung a number of threads, and let them traverse further.
  if(parallel) createThreads(1,currentPrimeIndex); // Start from 1 to avoid traverse primes[0] = 2

  //Threads are done. Now save their result.
  collectPrimes(primes[currentPrimeIndex-1]+2, n);
  System.out.println("n = " + n);


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
     for(int j = 0; j<threads.length; j++){
       threadWork[j][pos] = primes[i];
       //System.out.println("["+j+"]["+pos+"]: "+primes[i]);
       i ++;
     }
     pos ++;
   }//end While

   //We now know what thread should do what work. Let them begin.
   for( i = 0; i < cores; i++){
     threads[i] = new Thread(new Worker(i, threadWork[i], n));
     threads[i].start();
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
        System.out.println("Thread: " + id + " merging..");
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
      System.out.println("Thread: " + id + " is done. ");
      try{barrier.await();
      }catch(Exception e){return;}
    }//End run
  }//End worker


}// End SievePara
