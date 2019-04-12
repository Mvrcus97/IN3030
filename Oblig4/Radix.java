import java.lang.Math.*;
import java.util.*;
import java.util.concurrent.*;

public class Radix{
  int[] a, b;
  int[] thread_max;
  int[] bit; //bit.length = Amount of different digits. bit[i] = digitlength of digit i
  int DIGIT_BITS;
  int n, cores, maxNumberBits, globalMax;
  Thread[] workersA, workersB;

  int[][] allCount;
  int[] sumCount;
  int[][] pointers;
  int shift, mask, maskLen;

  CyclicBarrier barrierA, barrierB, barrierD;
  boolean finalRun;


  public Radix(int n, int k, int digit_bits ){
    this.n = n;
    this.DIGIT_BITS = digit_bits;

    this.cores = ((k==0) ? Runtime.getRuntime().availableProcessors(): k);
    this.thread_max = new int[cores];
    this.workersA = new Thread[cores];
    this.workersB = new Thread[cores];
    this.barrierA = new CyclicBarrier(cores+1);
    this.barrierB = new CyclicBarrier(cores+1);
    this.barrierD = new CyclicBarrier(cores+1);

    this.finalRun = false;
  }// end Radix()


  public int[] radix(int[] a){
    this.a = a;
    stegA();

    //Count how many bits needed for the largest number
    this.maxNumberBits = findBits(globalMax);
    this.allCount = new int[cores][(1 << (maxNumberBits+1))];
    this.sumCount = new int[(1 << (maxNumberBits+1))];

    //How many digits we work on
    int numDigits = Math.max(1, maxNumberBits / DIGIT_BITS);
    this.bit = new int[numDigits];
    int rest = maxNumberBits % DIGIT_BITS;

    //Divide the parts that we sort on equally.
    for (int i = 0; i < bit.length; i++ ) {
      bit[i] = maxNumberBits / numDigits;
      if (rest-- > 0) bit[i]++;
    }

    int[] temp = this.a;
    this.b = new int[n];
    int sum = 0; //Used for shifting to the digit we are working on in radixSort

    createThreads();

    for (int i = 0;  i < bit.length; i++) {
      //Sorting on digit i.
      radixSort(bit[i], sum);
      sum += bit[i];
      //Swap the arrays.
      temp = this.a;
      this.a = this.b;
      this.b = temp;

      if(i == bit.length-1) finalRun = true;
      try{barrierB.await();} //iteration done
      catch(Exception e){}
    }

    //If the end result ends up in the b array, copy it to the a array
    if ((bit.length & 1 ) != 0) {
        System.arraycopy(a, 0, b, 0, a.length);
    }
    return this.a;
  }//end Radix()



  public void radixSort(int maskLen, int shift){
    int acumVal = 0; //Used later for making "pointers"
    int temp;
    this.maskLen = maskLen;
    this.shift = shift;
    this.mask = (1 << maskLen) -1; //a bitstring of 1's with the length we need.

    this.allCount = new int[cores][mask+1];
    this.sumCount = new int[mask+1];
    this.pointers = new int[cores][mask+1];

    //Part B: Counting occurences of digits
    stegB();

    //Part C: Making "pointers" so we know where to put the numbers from a to b
    for (int i = 0; i <= mask; i++) {
      temp = sumCount[i];
      sumCount[i] = acumVal;
      acumVal += temp;
    }

    acumVal = 0;
    //C2) Create pointers for each thread.
    for(int i = 0; i <=mask; i++){
      for(int j = 0; j<cores; j++){
        pointers[j][i] = acumVal;   //Set thread's pointer
        acumVal += allCount[j][i];  // Update acumVal to thread's count
      }
    }

    //Part D: Move numbers from a to b.
    stegD();

  }//end radixSort()


  //Find max.
  public void stegA(){
    int start = 0;
    int stop = 0;
    int part = n/cores;

    for(int i = 0; i < cores-1; i++){
      start = stop;
      stop = start + part;
      workersA[i] = new Thread(new WorkerA(i, start, stop));
      workersA[i].start();
    }
    start = stop;
    stop = n;
    workersA[cores-1] = new Thread(new WorkerA(cores-1, start, stop));
    workersA[cores-1].start();

    try{ barrierA.await();}
    catch(Exception e){}

    this.globalMax = 0;
    for(int i = 0; i < cores; i++){
      if(thread_max[i] > globalMax) globalMax = thread_max[i];
    }

    //System.out.println("Found biggest value: " + globalMax);
  }//end stegA()


  public void stegB(){
    try{
      barrierB.await();//Threads begin
      barrierB.await();//Threads copy to allCount
      barrierB.await();//Threads done updating to sumCount
    }
    catch(Exception e){}
  }//end stegB()


  public void stegD(){
    try{
      barrierD.await();//Threads begin
      barrierD.await();//Threads done.
    }
    catch(Exception e){}
  }//end stegD()

  //Create threads for part B and D. These threads will await untill
  //Told to begin working.
  public void createThreads(){
    //Create B - threads
    for(int i = 0; i < cores; i++){
      workersB[i] = new Thread(new WorkerB(i));
      workersB[i].start();
    }
  }//end createThreads()



  public int findBits(int value){
    int count = 0;
    while (value > 0) {
      count++;
      value = value >> 1;
    }
    return count;
  }





  /*
  *   Inner class WorkerA represents a thread.
  *   Each thread will traverse through their part of the array,
  *   and find the max value. Upload this max value into an array of max_values,
  *  Where the main thread will locate the largest values of them all.
  */
  public class WorkerA implements Runnable{
    int id;
    int start, stop;
    int curr_max;

    public WorkerA(int id, int start, int stop){
        this.id = id;
        this.start = start;
        this.stop = stop;
        this.curr_max = 0;
    }


      //This method is called once Thread.start() is initialized.
      public void run() {
        for(int i = start; i < stop; i++){
          if(a[i] > curr_max){
            curr_max = a[i];
          }
        }

        thread_max[id] = curr_max;
        try{barrierA.await();}
        catch( Exception e){}
      }//End run
    }//End workerA


    /*
    *   Inner class WorkerB represents a thread.
    *   Each thread will traverse through their part of the array,
    *   and find the max value. Upload this max value into an array of max_values,
    *  Where the main thread will locate the largest values of them all.
    */
    public class WorkerB implements Runnable{
      int id;
      int start, stop;
      int[] local_count;

      public WorkerB(int id){
          this.id = id;
      }

      public void execute(){

        this.start = n/ cores;
        this.start *= this.id;
        this.stop = ((n/cores) * (this.id+1));
        if(id == cores-1) this.stop = n;

        this.local_count = new int[mask+1];
        for(int i = start; i < stop; i++){
          local_count[a[i] >> shift & mask] ++;
        }

        allCount[id] = local_count;

        try{barrierB.await();}
        catch(Exception e){}

        this.start = sumCount.length/ cores;
        this.start *= this.id;
        this.stop = ((sumCount.length/cores) * (this.id+1));
        if(id == cores-1) this.stop = sumCount.length;

        int sum = 0;
        for(int i = start; i < stop; i++){
          for(int j = 0; j < cores; j++){
            sum += allCount[j][i];
          }
          sumCount[i] = sum;
          sum = 0;
        }

        try{barrierB.await();}// B done
        catch(Exception e){}

        //BEGIN D

        try{barrierD.await();}
        catch(Exception e){}

        this.start = n/ cores;
        this.start *= this.id;
        this.stop = ((n/cores) * (this.id+1));
        if(id == cores-1) this.stop = n;

        for(int i = start; i < stop; i++){
          b[pointers[id][(a[i] >>> shift) & mask]++] = a[i];
        }

         try{barrierD.await();}//done signal
         catch(Exception e){}

      }//end execute()


        //This method is called once Thread.start() is initialized.
        public void run() {
          while(!finalRun){
            try{barrierB.await();}//Wait for begin signal
            catch(Exception e){}
            if(finalRun) break;
            execute();
            try{barrierB.await();}//Wait for current iteration to be done
            catch(Exception e){}
          }
        }//End run
      }//End workerB

}//end Radix
