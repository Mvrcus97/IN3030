import java.util.*;
import java.util.concurrent.*;


public class Opg2{

  public static void main(String[] arguments){
    if(arguments.length != 2) {
      System.out.println("Usage: java Opg2 'array-size' 'k-value'");
      return;
    }
    int length = Integer.parseInt(arguments[0]);
    int k = Integer.parseInt(arguments[1]);
    Sorter opg2 = new Sorter(length, k);


    double[] timeArray = new double[7];
    double start = 0 , stop = 0;
    System.out.println("Times recorded:");

    for( int i = 0; i <7; i++) {
      start = System.nanoTime();
      opg2.executeParallel();
      stop = System.nanoTime();
      timeArray[i] = (stop-start)/1000000.0;
      System.out.println((stop-start)/1000000.0 + "ms");

      opg2 = new Sorter(length, k); // Reset Sorter.
    }


    System.out.println("\nMedian is: " + opg2.median(timeArray) + "ms");
    opg2.executeParallel();
    System.out.println("\nTop 20 findings:");
    opg2.printTopX(20);


  }// end Main
}// end Opg2
