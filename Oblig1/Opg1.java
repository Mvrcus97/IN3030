import java.util.*;
import java.util.concurrent.*;


public class Opg1{
  int[] array;
  int seed = 1337;
  Random random = new Random(seed);
  int k;

  public static void main(String[] arguments){
    if(arguments.length != 2) {
      System.out.println("Usage: java Opg1 'array-size' 'k-value'");
      return;
    }
    System.out.println("Do you want to use Java's Arrays.sort() or my algorithm?");
    System.out.println("y -- Java");
    System.out.println("n -- My own");
    Scanner s = new Scanner(System.in);
    String input = s.nextLine();
    boolean java;

    if(input.equals("y")){
      java = true;
    }else if ( input.equals("n")){
      java = false;
    }else{
      System.out.println("Error. exiting..");
      return;
    }
    System.out.println("Running algorithm.. [Java = "+java+"]");

    int length = Integer.parseInt(arguments[0]);
    int k = Integer.parseInt(arguments[1]);
    Opg1 opg1 = new Opg1(length, k);

    double[] timeArray = new double[7];
    double start, stop;
    for( int i = 0; i <7; i++){
      start = System.nanoTime();
      opg1.execute(java);
      stop  = System.nanoTime();
      timeArray[i] = (stop-start)/1000000;

      opg1 = new Opg1(length, k); // Important to reset the sorted aray.
    }

    System.out.println("Times recorded:");
    for( double d : timeArray){
      System.out.println(d + "ms");
    }

    System.out.println("Median is: " + opg1.median(timeArray));

  }// end Main.

  /* Constructor */
  public Opg1(int n, int k){
    this.k = k;
    this.array = new int[n];
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



}
