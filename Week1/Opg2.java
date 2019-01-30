import java.util.concurrent.*;
import java.util.*;

public class Opg2 {
  final int THREADS =  Runtime.getRuntime().availableProcessors();
  CyclicBarrier b = new CyclicBarrier(THREADS+1);
  Thread[] threads = new Thread[THREADS];
  int[] numbers;
  int[] t_max = new int[THREADS];


  void createArray(int n) {
    this.numbers = new int[n];
    Random random = new Random();

    for (int i = 0; i < n; i++) {
      numbers[i] = random.nextInt(n);
    }
  }

  void para(){
    int max = 0;
    int start = 0, end = 0;
    int part = numbers.length/THREADS;

    for(int i = 0; i < THREADS; i++){
      start = end;
      end = start + part;
      threads[i] = new Thread(new Para(i, start, end));
      threads[i].start();
    }
  }

  void findMax(){


    int max = 0;
    for ( int i :  t_max ){
      if( i > max) {max = i;}
    }
    System.out.println("Max found: " + max);

  }



  int seq() {
    int max = 0;
    for (  int i : numbers) {
      if( i > max) {
        max = i;
      }
    }
    return max;
  }

  public static void main(String[] arguments){
    if(arguments.length != 1) {
      System.out.println("Usage: java Opg2 'array_length'");
      return;
    }

    Opg2 opg2 = new Opg2();

    int length = Integer.parseInt(arguments[0]);
    opg2.createArray(length);



    System.out.println("-------- Sekvensiell --------");
    for (int i = 0; i < 5; i++ ) {
      long t = System.nanoTime();
      int max = opg2.seq();
      double tid = (System.nanoTime() - t) / 1000000.0;
      System.out.println("Sekvensiell tid: " + tid + " ms\nMax: " +max);
    }
    System.out.println("-----------------------------\n\n");




    System.out.println("----------- Parallell ---------");
    for (int i = 0; i < 5; i++ ) {
      long t = System.nanoTime();
      opg2.para();
      opg2.findMax();
      double tid = (System.nanoTime() - t) / 1000000.0;
      System.out.println("Parallell tid: " + tid + " ms");
    }
    System.out.println("-----------------------------------\n\n");


  }

  class Para implements Runnable{
    int start, end, max = 0;
    int id;
    public Para(int id, int start, int end){
      this.start = start;
      this.end = end;
      this.id = id;
    }
    public void run(){
      //when thread has started.
      for( int i = start; i < end; i++) {
        if( numbers[i] > max) max = i;
      }
      t_max[id] = max;
      try {
        b.await();

      } catch (Exception e) { return;}
    }

  }//end Para
}//end Opg1
