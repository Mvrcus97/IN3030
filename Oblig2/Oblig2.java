public class Oblig2 {
  public static void main(String[] args){

    if(args.length != 2) {
      System.out.println("Usage: java Oblig2 'Seed' 'Size'\n");
      System.out.println("The program will automatically record\ntimings of matrix multiplication of the\ngiven matrix dimension.");
      return;
    }
    Oblig2Precode matrixMaker = new Oblig2Precode();
    int seed = Integer.parseInt(args[0]);
    int size = Integer.parseInt(args[1]);


    double[][] a = matrixMaker.generateMatrixA(seed, size);
    double[][] b = matrixMaker.generateMatrixB(seed, size);
    double[][] c;
    MatrixMultiplier correcter = new MatrixMultiplier(a,b);
    MatrixMultiplier multiplier = new MatrixMultiplier(a,b);

    //Run Sequential Multiplication.
    double[] seqTime = new double[7];
    double start = 0 , stop = 0;
    System.out.println("Running Sequential Multiplication...");

    for( int i = 0; i <7; i++) {
      start = System.nanoTime();
      correcter.multiply();
      stop = System.nanoTime();
      seqTime[i] = (stop-start)/1000000.0;
      System.out.println((stop-start)/1000000.0 + "ms");
      if ( i != 6) {correcter = new MatrixMultiplier(a,b);} // Reset correcter.
    }
    System.out.println("Median time of Sequential Multiplication is: " + multiplier.median(seqTime) + "ms\n");







    //Run Parallel Multiplication.
    double[] paraTime = new double[7];
    start = 0;
    stop  = 0;
    System.out.println("Running Parallel Multiplication...");

    for( int i = 0; i <7; i++) {
      start = System.nanoTime();
      multiplier.multiplyPara();
      stop = System.nanoTime();
      paraTime[i] = (stop-start)/1000000.0;
      System.out.println((stop-start)/1000000.0 + "ms");
      if ( i != 6) {multiplier = new MatrixMultiplier(a,b);} // Reset multiplier.
    }
    c = multiplier.getC();
    correcter.checkMatch2D(c);
    System.out.println("Median time of Parallel Multiplication is: " + multiplier.median(paraTime) + "ms\n");







    //Run Sequential Multiplication, b transposed.
    multiplier = new MatrixMultiplier(a,b);
    seqTime = new double[7];
    start = 0;
    stop  = 0;
    System.out.println("Running Sequential Multiplication with b Transposed...");

    for( int i = 0; i <7; i++) {
      start = System.nanoTime();
      multiplier.transposeB();
      multiplier.multiply();
      stop = System.nanoTime();
      seqTime[i] = (stop-start)/1000000.0;
      System.out.println((stop-start)/1000000.0 + "ms");
      if ( i != 6) {multiplier = new MatrixMultiplier(a,b);} // Reset multiplier.
    }
    c = multiplier.getC();
    correcter.checkMatch2D(c);
    System.out.println("Median time of Sequential Multiplication with b Transposed is: " + multiplier.median(seqTime) + "ms\n");







    //Run Parallel Multiplication, b transposed.
    multiplier = new MatrixMultiplier(a,b);
    paraTime = new double[7];
    start = 0;
    stop  = 0;
    System.out.println("Running Parallel Multiplication with b Transposed...");

    for( int i = 0; i <7; i++) {
      start = System.nanoTime();
      multiplier.transposeB();
      multiplier.multiplyPara();
      stop = System.nanoTime();
      paraTime[i] = (stop-start)/1000000.0;
      System.out.println((stop-start)/1000000.0 + "ms");
      if ( i != 6) {multiplier = new MatrixMultiplier(a,b);} // Reset multiplier.
    }
    c = multiplier.getC();
    correcter.checkMatch2D(c);
    System.out.println("Median time of Parallel Multiplication with b Transposed is: " + multiplier.median(paraTime) + "ms\n");








    //Run Sequential Multiplication, a transposed.
    multiplier = new MatrixMultiplier(a,b);
    seqTime = new double[7];
    start = 0;
    stop  = 0;
    System.out.println("Running Sequential Multiplication with a Transposed...");

    for( int i = 0; i <7; i++) {
      start = System.nanoTime();
      multiplier.transposeA();
      multiplier.multiply();
      stop = System.nanoTime();
      seqTime[i] = (stop-start)/1000000.0;
      System.out.println((stop-start)/1000000.0 + "ms");
      if ( i != 6) {multiplier = new MatrixMultiplier(a,b);} // Reset multiplier.
    }
    c = multiplier.getC();
    correcter.checkMatch2D(c);
    System.out.println("Median time of Sequential Multiplication with a Transposed is: " + multiplier.median(seqTime) + "ms\n");








    //Run Parallel Multiplication, a transposed.
    multiplier = new MatrixMultiplier(a,b);
    paraTime = new double[7];
    start = 0;
    stop  = 0;
    System.out.println("Running Parallel Multiplication with a Transposed...");

    for( int i = 0; i <7; i++) {
      start = System.nanoTime();
      multiplier.transposeA();
      multiplier.multiplyPara();
      stop = System.nanoTime();
      paraTime[i] = (stop-start)/1000000.0;
      System.out.println((stop-start)/1000000.0 + "ms");
      if ( i != 6) {multiplier = new MatrixMultiplier(a,b);} // Reset multiplier.
    }
    c = multiplier.getC();
    correcter.checkMatch2D(c);
    System.out.println("Median time of Parallel Multiplication with a Transposed is: " + multiplier.median(paraTime) + "ms\n");










  }
}
