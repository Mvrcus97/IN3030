import java.util.*;
import java.util.concurrent.*;



public class Opg1{
  int[] array;
  int seed = 1337;
  Random random = new Random(seed);
  int k = 10;

  public static void main(String[] arguments){
    if(arguments.length != 1) {
      System.out.println("Usage: java Opg1 'amount'");
      return;
    }
    int length = Integer.parseInt(arguments[0]);
    Opg1 opg1 = new Opg1(length);
    //opg1.printArray();
    opg1.seqSort(5);
    //opg1.printArray();
    opg1.sortReplace();
    //opg1.printArray();
    opg1.printTopX(10);
  }

  /* Constructor */
  public Opg1(int n){
    this.array = new int[n];
    //Initialize array with n numbers.
    for( int i = 0; i<n;i++){
      array[i] = random.nextInt(1000000);
    }
  }

  /* This method implemens the sequential
  *  Sorting algorithm.
  */
  public void seqSort(int k){
    //sort the k- first elements.
    insertSortDec(0, k);





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
        System.out.println("Swapping: " + array[i] + " and " + array[k]);
        swap(i,k);
        sortNewValue();
      }
    }// end for i
  }// end sortReplaces


  /*
  This method puts the new a[k]-value
  into correct position.
  */
  //TODO check if this works. lul.
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



}
