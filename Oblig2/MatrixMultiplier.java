import java.util.*;
import java.util.concurrent.*;

public class MatrixMultiplier {
  //Common data
  double[][] a,aT,b,bT,c;
  boolean wrong_matrix_dimension = false;
  int cores;
  Thread[] workers;
  CyclicBarrier barrier;
  boolean a_transposed = false;
  boolean b_transposed = false;

  //Constructor
  public MatrixMultiplier(double[][] a, double[][] b) {
    //Only allow a.columns = b.rows matrices.
    if(b.length != a[0].length) {
      System.out.println("Error. Matrix dimension missmatch.");
      wrong_matrix_dimension = true;
    }
    this.a       = a;
    this.b       = b;
    this.c       = new double[a.length][b.length];
    this.cores   = Runtime.getRuntime().availableProcessors();
    this.workers = new Thread[cores];
    this.barrier = new CyclicBarrier(cores+1);
  }//end Constructor

  //This method transposes the a-matrix.
  public void transposeA(){
    int m = a.length;
    int n = a[0].length;
    double aT[][] = new double[n][m];

    for( int i = 0; i < n; i++){
      for( int j = 0; j < m; j++){
          aT[j][i] = a[i][j];
      }
    }
    this.a_transposed = true;
    this.aT = aT;
  }// end transposeA

  //This method transposes the b-matrix.
  public void transposeB(){
    int m = b.length;
    int n = b[0].length;
    double bT[][] = new double[n][m];

    for( int i = 0; i < n; i++){
      for( int j = 0; j < m; j++){
          bT[j][i] = b[i][j];
      }
    }
    this.b_transposed = true;
    this.bT = bT;
  }// end transposeB


  /* Method for performing sequential matrix multiplication in-place. */
  public void multiply(){
    if(a_transposed) multiplyTransposeA();
    else if(b_transposed) multiplyTransposeB();
    else{
      int n = this.a.length;
      int m = this.b.length;
      if(wrong_matrix_dimension) {
        System.out.println("Error. Matrix dimension missmatch.");
        return;
      }
      double[][] c = new double[n][m];
      for( int i = 0; i <n; i++){
        for ( int j = 0; j<m; j++){
          for ( int k = 0; k<m; k++){
            c[i][j] += a[i][k] * b[k][j];
          }
        }
      }
      this.c = c;
    }//end else
  }// End multiply


  /* Method for computing the multiplication aT*b
     We here need to remember that a is transposed, and to get
     equal results we need to perform the multiplication a[k][i] * b[k][j]  */
  public void multiplyTransposeA(){
    int n = this.aT[0].length;
    int m = this.b.length;

    double[][] c = new double[n][m];
    for( int i = 0; i <n; i++){
      for ( int j = 0; j<m; j++){
        for ( int k = 0; k<m; k++){
          c[i][j] += aT[k][i] * b[k][j];
        }
      }
    }
    this.c = c;
  }// end multiplyTransposeA

  /* Method for computing the multiplication a*bT
     We here need to remember that b is transposed
     so we need to do: a[i][k] * b[j][k]  */
  public void multiplyTransposeB(){
    int n = this.a.length;
    int m = this.bT[0].length;

    double[][] c = new double[n][m];
    for( int i = 0; i <n; i++){
      for ( int j = 0; j<m; j++){
        for ( int k = 0; k<m; k++){
          c[i][j] += a[i][k] * bT[j][k];
        }
      }
    }
    this.c = c;
  }// end multiplyTransposeB




  /*  Method for performing parallel matrix multiplication.
      This method cannot be done in place because of the infamouse i++ problem
      of parallelization. */
  public void multiplyPara(){
    if(wrong_matrix_dimension){
      System.out.println("Error. wrong matrix dimention.");
      return;
    }
    int rowStart = 0;
    int rowEnd   = 0;
    int part     = a.length/cores;


    for( int i = 0; i < cores; i++) {
      rowStart = rowEnd;
      rowEnd   = rowStart + part;
      if(i == cores-1){
        //TEMPORARY: Force last thread to do all "rest" rows of work..
        //TODO
        int rest = a.length - (rowEnd-1);
        System.out.println("THERE IS A REST OF " + rest + " ROWS !!\nForcing last thread to deal with it..");
        rowEnd = a.length;
      }
      workers[i] = new Thread(new Worker(i, rowStart, rowEnd-1, a.length));
      workers[i].start();
    }

    //Await for all threads to do their job..
    try{
      barrier.await();
    }catch(Exception e){return;}
    // The workers has now calculated the c -matrix.
    System.out.println("Workers done. ");
  }//end multiplyPara




  public void checkMatch2D(double[][] c2){
    boolean error = false;
    for (int i = 0; i < c.length; i++){
      for (int j = 0; j < c[0].length; j++){
        if(c[i][j] != c2[i][j]) {
          error = true;
          System.out.println("Error ["+i+"]["+j+"]| c:" + c[i][j] + ", c2: " + c2[i][j]);
        }
      }
    }
    if(!error) System.out.println("------------------------\nMatrix Multiplication Successfull.");
  }//end checkMatch2D

  //Getters. Don't really need any setters here.
  public double[][] getA(){return this.a;}
  public double[][] getB(){return this.b;}
  public double[][] getC(){return this.c;}

  /*The Thread-class which wil run in parallel*/
  class Worker implements Runnable{
    int id;
    int rowStart, rowEnd, dimension;
    double[][] localC;
    int rowAmount;

    // Constructer.
    public Worker( int id, int rowStart, int rowEnd, int dimension){
      this.id        = id;
      this.rowStart  = rowStart;
      this.rowEnd    = rowEnd;
      this.dimension = dimension;
      this.rowAmount = rowEnd - rowStart + 1;
      this.localC    = new double[rowAmount][dimension];
      //System.out.println("id: " + id + ", rowStart: " + rowStart + ", rowEnd : " + rowEnd + ", dimension: " + dimension);
    }

    /* Begin Multiplication on this worker's area.*/
    public void runMultiplication(){

      for (int i = 0; i <rowAmount; i++){
        for (int j = 0; j<dimension; j++){
          for (int k = 0; k<dimension; k++){
            localC[i][j] += a[i+rowStart][k] * b[k][j];
          }
        }
      }
    }//end runMultiplication

    /* Begin Multiplication, but A is transposed.  */
    public void runMultiplicationA(){
      for (int i = 0; i <rowAmount; i++){
        for (int j = 0; j<dimension; j++){
          for (int k = 0; k<dimension; k++){
            localC[i][j] += aT[k][i+rowStart] * b[k][j];
          }
        }
      }
    }//end runMultiplicationA

    /* Begin Multiplication, but B is transposed.  */
    public void runMultiplicationB(){
      for (int i = 0; i <rowAmount; i++){
        for (int j = 0; j<dimension; j++){
          for (int k = 0; k<dimension; k++){
            localC[i][j] += a[i+rowStart][k] * bT[j][k];
          }
        }
      }
    }//end runMultiplicationA



    /*This method is used to update the result matrix, c.
      Because each thread is working on each their own index of the c-matrix,
      there is no need to synchronize! */
    public void updateResult(){
      for (int i = rowStart; i < rowEnd+1; i++){
        for (int j = 0; j < dimension; j++){
          c[i][j] = localC[i-rowStart][j];
        }
      }
    }// end updateResult

    //for testing.
    public void printLocalC(){
      for (int i = 0; i < localC.length; i++){
        for (int j = 0; j < localC[0].length; j++){
          System.out.println("Thread["+id+"]localC["+i+"]["+j+"]: " + localC[i][j]);
        }
      }
    }// end printLocalC

    //This method is called once Thread.start() is initialized.
    public void run() {
      if(a_transposed) runMultiplicationA();
      else if(b_transposed) runMultiplicationB();
      else runMultiplication();

      updateResult();

      //Finally, let everyone know this thread is done.
      try{barrier.await();
      }catch(Exception e){return;}
    }// end run
  }// end Worker


}// End MatrixMultiplier
