import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;


public class Sorter{
  int[] array;
  int seed = 1337, k, cores;
  Random random = new Random(seed);
  ReentrantLock lock = new ReentrantLock();
  Thread[] workers;
  CyclicBarrier barrier;

  /* Constructor */
  public Sorter(int n, int k){
    this.k = k;
    this.array = new int[n];
    this.cores = Runtime.getRuntime().availableProcessors();
    //Initialize array with n numbers.
    for( int i = 0; i<n;i++){
      array[i] = random.nextInt(n*10);
    }
  }

  /*
  This method executes the entire algorithm.
  */
  public void execute(boolean java){
    if(java){
      executeJavaSort();
      return;
    }
    seqSort();
    sortReplace();
    //printTopX(20);
  }

  public void executeJavaSort(){
    Arrays.sort(array);
  }

  /* This function begins CORES* amount of Threads and lets
  them sort the array. */
  public void executeParalell(){
    this.workers = new Thread[cores];
    this.barrier = new CyclicBarrier(cores+1);
    int start = 0, end = 0, part = array.length/cores;

    for( int i = 0; i < cores; i++) {
      start = end;
      end = start + part;

      workers[i] = new Thread(new Worker(i,start,end-1));
      workers[i].start();
    }

    //Await for all threads to do their job..
    try{
      barrier.await();
    }catch(Exception e){return;}
    // and finally, compare what the threads found and store the largest.
    int compared = 0;
    double t1, t2;

    for ( int i = 1; i<cores; i++){
      for( int j = i*part; j<(i*part+k); j++){
        if( array[j] > array[k-1]){
          swap(j,k-1);
          sortNewValue();
        }
      }
    }

  }//end executeParalell


  /* This method implemens the sequential
  *  Sorting algorithm.
  */
  public void seqSort(){
    //sort the k- first elements.
    insertSortDec(0, this.k);
  }

  /*
  This Method is the insertSort.
  Sorts the elements of array in ascending order,
  Only sort between array[a] to array[b].
  */
  public void insertSort(int a, int b){
    int i, t;

    for(int k = a; k<b-1; k++){
        t = array[k+1];
        i=k;
        while(i>=a && array[i]>t){
            array[i+1] = array[i];
            i--;
        }
        array[i+1] = t;
    } // end for k.
  }// end insertSort


/*
This method is a Decending insertSort
Made by simply switching > to < in while statement.
*/
public void insertSortDec(int a, int b){
  int i, t;

  for(int k = a; k<b-1; k++){
      t = array[k+1];
      i=k;
      while(i>=a && array[i]<t){
          array[i+1] = array[i];
          i--;
      }
      array[i+1] = t;
    } // end for k.
  } // end insertSortDec

  /*
  This method is called once a[0..k-1] is sorted.
  Looks through the rest of the array, a[k ... n-1]
  and checks if any value is larger than a[k-1].
  */
  public void sortReplace(){

    for (int i = k+1; i<array.length; i++){
      if ( array[i] > array[k]){
        //System.out.println("Swapping: " + array[i] + " and " + array[k]);
        swap(i,k);
        sortNewValue();
      }
    }// end for i
  }// end sortReplaces


  /*
  This method puts the new a[k]-value
  into correct position.
  */
  public void sortNewValue(){
    for ( int i = k; i>0; i--){
        if (array[i] > array[i-1]) swap(i, i-1);
    }//end for i
  }// end sortNewValue

  // Method for performing basic swap.
  public void swap(int a, int b){
    int temp = array[a];
    array[a] = array[b];
    array[b] = temp;
  }

  //Method does just that.
  public void printArray(){
    System.out.println("Printing array....");
    for( int i = 0; i < array.length; i++){
      System.out.println(array[i]);
    }
    System.out.println();
  }


  public void printTopX(int x){
    for( int i = 0; i <x; i++) {
      System.out.println(array[i]);
    }
  }


  /*Cheeky function of getting median of UNSORTED* list
  of ODD or EVEN* amount of elements. */
  double median(double[] a){
    int length = a.length;
    double temp;
    boolean odd = false;
    if (length%2 != 0){
      odd = true;
    }

    //sort array
    for( int i = 0; i < length; i++){
      for (int j = i+1; j < length; j++){
        if (a[j] < a[i]) {
          temp = a[i];
          a[i] = a[j];
          a[j] = temp;
        }
      }
    }
    double median = odd? a[(length/2)] : (a[length/2] + a[(length/2)+1])/2;
    return median;
  }


  /*
  The Thread-class which wil run in paralell
  */
  class Worker implements Runnable{
    int id, start, stop;

    public Worker(int id, int start, int stop){
      this.id = id;
      this.start = start;
      this.stop = stop;
    }
    /* Same as sortNewValue() only for internal array.
    This method sorts respective INDEX. example: array[inner_k[0]] = largest found.  */
    public void sortNewInnerValue(){
      for ( int i = start+k; i>start; i--){
          if (array[i] > array[i-1]) swap(i, i-1);
      }//end for i
    }// end sortNewInnerValue

    public void printInner(){
      for( int i = start; i<=start+k;i++){
        System.out.println(array[i] + " " + this.id);
      }
    }



    //Each Thread do:
    public void run(){
      insertSortDec(start,start+k-1); //Sort the top of this Threads part.
      int temp;

      for( int i = start+k; i<=stop; i++){
        if(array[i] > array[start+k-1]){
          /*Found a big value, move it in place. */
          swap(i, start+k-1);
          sortNewInnerValue();
        }
      }//end for i

      //Finally, let everyone know this thread is done.
      try{barrier.await();
      }catch(Exception e){return;}
      //printInner();

    }// end run
  }// end Worker
} // end Sorter
