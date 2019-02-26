import java.util.*;
import java.util.concurrent.*;


public class Run{
  public static void main(String[] arguments){
    if(arguments.length != 2) {
      System.out.println("Usage: java Run 'array-size' 'k-value'");
      return;
    }
    int length = Integer.parseInt(arguments[0]);
    int k = Integer.parseInt(arguments[1]);
    Sorter sort = new Sorter(length, k);

    //Run Java-sort.
    double[] javaTime = new double[7];
    double start = 0 , stop = 0;
    System.out.println("Running Java-Sort...");

    for( int i = 0; i <7; i++) {
      start = System.nanoTime();
      sort.execute(true);
      stop = System.nanoTime();
      javaTime[i] = (stop-start)/1000000.0;
      System.out.println((stop-start)/1000000.0 + "ms");
      if ( i != 6) {sort = new Sorter(length, k);} // Reset Sorter.
    }

     System.out.println("Median time of java-sort is: " + sort.median(javaTime) + "ms\n");

     //Run sequential
     double[] seqTime = new double[7];
     start = 0;
     stop = 0;
     System.out.println("Running Sequential Sort...");

     for( int i = 0; i <7; i++) {
       start = System.nanoTime();
       sort.execute(false);
       stop = System.nanoTime();
       seqTime[i] = (stop-start)/1000000.0;
       System.out.println((stop-start)/1000000.0 + "ms");
       if ( i != 6) {sort = new Sorter(length, k);} // Reset Sorter.
     }
      sort.checkMatch();
      System.out.println("Median time of Sequential Sort is: " + sort.median(seqTime) + "ms\n");

      //Run Parallel
      double[] paraTime = new double[7];
      start = 0;
      stop = 0;
      System.out.println("Running Parallel Sort...");

      for( int i = 0; i <7; i++) {
        start = System.nanoTime();
        sort.executeParallel();
        stop = System.nanoTime();
        paraTime[i] = (stop-start)/1000000.0;
        System.out.println((stop-start)/1000000.0 + "ms");
        if ( i != 6) {sort = new Sorter(length, k);} // Reset Sorter.
      }
       sort.checkMatch();
       System.out.println("Median time of Parallel Sort is: " + sort.median(paraTime) + "ms\n");

       double speedupS = sort.median(javaTime)/sort.median(seqTime);
       double speedupP = sort.median(javaTime)/sort.median(paraTime);
       System.out.println("-----------K = " +k + ", Length = " + length+ "------");
       System.out.println("Speedup sequential sort: " + speedupS);
       System.out.println("Speedup parallel sort  : " + speedupP);
       System.out.println("------------------------------------------");





  }// end Main
}// end Run
