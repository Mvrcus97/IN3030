import java.util.*;
import java.util.concurrent.*;


public class Opg2{

  public static void main(String[] arguments){
    System.out.println("test:");

    Sorter opg2 = new Sorter(1000, 10);
    opg2.executeParalell();
    opg2.printTopX(10);
  }// end Main

}// end Opg2
