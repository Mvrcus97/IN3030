import java.util.concurrent.*;

public class Opg1 {
  CyclicBarrier b;
  final int THREADS =  Runtime.getRuntime().availableProcessors();
  Thread[] array = new Thread[THREADS];

  void execute(){
    for (int i = 0; i < THREADS; i++) {
      array[i]  = new Thread(new Para(i));
      array[i].start();
    }
  }

  public static void main(String[] arguments){
    Opg1 opg1 = new Opg1();
    opg1.execute();
    System.out.println("MAIN HERE");
  }


  class Para implements Runnable{
    int id;
    public Para(int id){
      this.id = id;
    }
    public void run(){
      //when thread has started.
      System.out.println("Traad nr:" + id+" sier hei");

      try {
        Thread.sleep(1000);
        System.out.println("Traad nr:" + id+" sier hei etter å ha ventet ett sekund") ;
      } catch (Exception e) { return;}
    }
  }//end Para
}//end Opg1
