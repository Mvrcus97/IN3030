import java.util.*;
import java.util.concurrent.*;


public class Opg2{

  public static void main(String[] arguments){
    double start = 0  , stop = 0;


    Sorter opg2 = new Sorter(100, 10);

    /*
    for( int i = 0; i <7; i++) {
      start = System.nanoTime();
      stop = System.nanoTime();
      opg2 = new Sorter(100000000, 100);
      System.out.println("Sek time: " + (stop-start)/1000000.0);
    }
    */

  //  for( int i = 0; i <7; i++) {
      start = System.nanoTime();
      opg2.executeParalell();
      stop = System.nanoTime();
      opg2 = new Sorter(1000, 10);
      System.out.println("Paralell time: " + (stop-start)/1000000.0);
    //}

    opg2.execute(false);



    opg2.printTopX(10);
  }// end Main

}// end Opg2
