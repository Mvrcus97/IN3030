import java.util.*;
import java.util.concurrent.*;


/*  Summation of args amount of integers.
*  sequential vs. Parallell solutions.
* Author: Marcus Tierney.
*/

public class Opg1{
  int[] numbers;
  int seed = 1337;
  Random random = new Random(seed);
  Thread[] threads = new Thread[4];
  int[] part_sum = new int[4];
  CyclicBarrier b = new CyclicBarrier(5);


  // Create an array of random numbers in span 0-1000
  void createArray(int n) {
    numbers = new int[n];
    for ( int i = 0; i < n; i++){
      numbers[i] = random.nextInt(1000);
    }
  }


  void sequential() {
    int sum = 0;
    for( int i : numbers){
      sum += i;
    }
    System.out.println("Sum is: " + sum);
  }


  void parallell(){
    int start = 0, end = 0, part = numbers.length/4;
    int sum = 0;

    for( int i = 0; i < 4; i++) {
      start = end;
      end = start + part;

      threads[i] = new Thread(new Para(i,start,end));
      threads[i].start();
    }
    try{b.await();} catch(Exception e){return;}


    for (int i : part_sum){
      sum += i;
    }
    System.out.println("Parallell sum is: " + sum);
  }

  /*Cheeky function of getting median of UNSORTED* list
  * of EVEN* amount of elements. */
  double median(double[] a){
    int length = a.length;
    double temp;

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
    double median = (a[length/2] + a[(length/2)+1])/2;
    return median;
  }




  public static void main(String[] arguments){
    if(arguments.length != 1) {
      System.out.println("Usage: java Opg1 'amount'");
      return;
    }

    Opg1 opg1 = new Opg1();
    int length = Integer.parseInt(arguments[0]);
    opg1.createArray(length);

    double[] p_tider = new double[10];
    double[] s_tider = new double[10];
    long t;
    double tid;
    //TEST sequential 10 times.
    for( int i = 0; i < 10; i++){
      t = System.nanoTime();
      opg1.sequential();
      tid = (System.nanoTime() - t) / 1000000.0;
      System.out.println("Sekvensiell tid: " + tid + " ms\n");
      s_tider[i] = tid;
    }

    // TEST Parallell 10 times.
    for (int i = 0; i < 10; i++) {
      t = System.nanoTime();
      opg1.parallell();
      tid =  (System.nanoTime() - t) / 1000000.0;
      System.out.println("parallell tid : " + tid + " ms\n");
      p_tider[i]  = tid;
    }

    // Results + conclusion.
    double median_s = opg1.median(s_tider);
    double median_p = opg1.median(p_tider);
    double speed_up = ((median_s/median_p) -1)*100;

    System.out.println("-------------------------------------------------------------------");
    System.out.println("Summation of " + arguments[0] + " random integers.");
    System.out.println("Results after 10 runs on both sequential and parallell computation:");
    System.out.println("Median Sekvensiell: " + median_s + " ms\nMedian Parallell: " + median_p +" ms");
    System.out.println("Speed Up: " + String.format("%.2f", speed_up) + "%");
    System.out.println("-------------------------------------------------------------------");






  } //End Main.


  class Para implements Runnable {
    int id, start, end;
    public Para(int id, int start, int end){
      this.id = id;
      this.start = start;
      this.end = end;
    }
    public void run(){
      int sum = 0;
      //Each thread do:

      for( int i = start; i < end; i++){
        sum += numbers[i];
      }

      part_sum[id] = sum;
      try{
        b.await();

      }catch(Exception e){return;}
    }


  } // End Para

}// End Opg1
