public class Oblig2 {
  public static void main(String[] args){

  Oblig2Precode matrixMaker = new Oblig2Precode();
  double[][] a = matrixMaker.generateMatrixA(1337, 1000);
  double[][] b = matrixMaker.generateMatrixB(1337, 1000);

  //double[][] a = {{1,2,3},{4,5,6},{7,8,9}};
  //double[][] b = {{7,8,9},{4,5,6}, {1,2,3}};
  MatrixMultiplier multiplier = new MatrixMultiplier(a,b);
  MatrixMultiplier multiplier2 = new MatrixMultiplier(a,b);
  System.out.println("Sequential1...");
  multiplier.multiply();
  //System.out.println("Parallel...");
  //multiplier2.multiplyPara();
  System.out.println("transposeA...");
  multiplier2.transposeA();
  System.out.println("...");
  multiplier2.multiplyPara();
  double[][] c = multiplier.getC();
  double[][] c2 = multiplier2.getC();

  multiplier.checkMatch2D(c2);






  matrixMaker.saveResult(1337, Oblig2Precode.Mode.SEQ_NOT_TRANSPOSED, a);
  matrixMaker.saveResult(1338, Oblig2Precode.Mode.SEQ_NOT_TRANSPOSED, b);
  matrixMaker.saveResult(1233, Oblig2Precode.Mode.SEQ_NOT_TRANSPOSED, c);
  matrixMaker.saveResult(1234, Oblig2Precode.Mode.SEQ_NOT_TRANSPOSED, c2);
  }
}
