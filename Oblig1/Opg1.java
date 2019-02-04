import java.util.*;
import java.util.concurrent.*;


public class Opg1{
  public static void main(String[] arguments){
    if(arguments.length != 2) {
      System.out.println("Usage: java Opg1 'array-size' 'k-value'");
      return;
    }
    System.out.println("Do you want to use Java's Arrays.sort() or the custom algorithm?");
    System.out.println("y -- Java");
    System.out.println("n -- Custom");
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
    Sorter opg1 = new Sorter(length, k);

    double[] timeArray = new double[7];
    double start, stop;
    for( int i = 0; i <7; i++){
      start = System.nanoTime();
      opg1.execute(java);
      stop  = System.nanoTime();
      timeArray[i] = (stop-start)/1000000;

      opg1 = new Sorter(length, k); // Important to reset the sorted aray.
    }


    System.out.println("Times recorded:");
    for( double d : timeArray){
      System.out.println(d + "ms");
    }

    System.out.println("Median is: " + opg1.median(timeArray));

    opg1.execute(false);
    opg1.printTopX(20);

  }// end Main.
}// end Opg1
