import java.util.concurrent.CyclicBarrier;

  /* Oblig 5 - Marcusti */



public class Oblig5{
  int[] x, y;
  int n, MAX_X, MAX_Y, cores;
  NPunkter p;
  IntList omkrets = new IntList();

  public Oblig5(int n, int cores){
    this.n = n;
    this.cores = cores;
    this.x = new int[n];
    this.y = new int[n];
    this.p = new NPunkter(n);
    p.fyllArrayer(x,y);

    int tmpx = 0;
    int tmpy = 0;
    for(int i = 0; i < n; i++){
      if(x[i] > tmpx) tmpx = x[i];
      if(y[i] > tmpy) tmpy = y[i];
    }
    this.MAX_X = tmpx;
    this.MAX_Y = tmpy;
  }


  public void printPairs(){
    for(int i = 0; i < n; i++){
      System.out.println("["+i+"]: ("+x[i]+", " +y[i]+")");
    }
  }

  public void TegnUt(){
    TegnUt tu = new TegnUt (this, omkrets);
  }


  //This method is called to run a sequential solution.
  public void seqRun(){
    this.omkrets = new IntList();
    //Find maxX and minX
    int minIdx = 0; int maxIdx = 0;
    for(int i = 0; i < n; i++){
      if(x[i] >= x[maxIdx]) maxIdx = i;
      if(x[i] <= x[minIdx]) minIdx = i;
    }

    //Create top and bottom list.
    IntList top = new IntList();
    IntList bot = new IntList();

    for(int i = 0; i < n; i++){
      if( i == maxIdx || i == minIdx)  continue;
      if(getDistance(i, maxIdx, minIdx) <= 0) top.add(i);
      if(getDistance(i, maxIdx, minIdx) >=0) bot.add(i);
    }

    omkrets.add(maxIdx);
    // System.out.println("\n\nSize top:" + top.size());
    seqRec(maxIdx, minIdx, getLargestDistance(maxIdx, minIdx, top), top, omkrets);

    //Top Circumference found. Now we can add the left-most-point and begin from bottom half.
    omkrets.add(minIdx);

    //System.out.println("\n\nSize bottom: " + bot.size());
    seqRec(minIdx, maxIdx, getLargestDistance(minIdx, maxIdx, bot), bot, omkrets);
  }//end seqRun




  /*This is the recursive method used in both sequential and parallel solution.
   *It works by collecting all outer-points for both right and left hand side.
   * Followed by finding which point is furthest away from the new created lines.
   */
  public void seqRec( int p1, int p2, int p3, IntList m, IntList hull){
    //Start with RIGHT SIDE
    IntList outerList = getOuterList(p1, p3, m);
    int point = getLargestDistance(p1, p3, outerList);

    if( point != -1){
      //Recursion keep going
      seqRec(p1, p3, point, outerList, hull);
    }
    hull.add(p3); //Add point

    //RIGHT SIDE COMPLETE. GO LEFT
    outerList = getOuterList(p3, p2, m);
    point = getLargestDistance(p3, p2 , outerList);

    if( point != -1 ){
      //Recursion keep going
      seqRec(p3, p2, point, outerList, hull);
    }
  }//end seqRec()



  //Create a new list of all points on the outer side of p1->p2.
  public IntList getOuterList(int p1, int p2, IntList list){
    IntList newList = new IntList();
    int curr;
    for(int i = 0; i < list.size(); i++){
      curr = list.get(i);
      if( curr == p1 || curr == p2) continue; // Avoid duplicates.
      if(getDistance(curr, p1, p2) <= 0) newList.add(curr);
    }
    return newList;
  }//end getOuterList

  //Get distance from p_new to line p1 -> p2.
  public int getDistance(int p_new, int p1, int p2) {
    int a = y[p1] - y[p2];
    int b = x[p2] - x[p1];
    int c = y[p2] * x[p1] - y[p1] * x[p2];
    return a * x[p_new] + b * y[p_new] + c;

  }//end getDistance

  //Print the omkrets found.
  public void printOmkrets(){
    System.out.println("Omkrets:\n");
    for(int i = 0; i < omkrets.size(); i++){
      System.out.print(omkrets.get(i)+", ");
    }
    System.out.println();
  }//end printOmkrets



  //Check distance to all outer points, return the one point which is furthest away.
  public int getLargestDistance(int p1, int p2, IntList list) {
    int p_tmp, d_tmp;
    int p_new = -1; int d_new = 1;

    for (int i = 0; i < list.size(); i++) {
      p_tmp = list.get(i);
      d_tmp = getDistance(p_tmp, p1, p2);

      if (d_tmp < d_new) {
        if (d_tmp == 0 && !checkBetween(p_tmp, p1, p2)) continue;
        p_new = p_tmp;
        d_new = d_tmp;
      }
    }
    return p_new;
  }

  //Check if a point is between the line p1-p2
  public boolean checkBetween(int p_new, int p1, int p2){
    boolean ret = false;
    double d1 = Math.sqrt( Math.pow((x[p2] - x[p1]), 2) + Math.pow((y[p2] - y[p1]), 2));
    double d2 = Math.sqrt( Math.pow((x[p_new] - x[p1]), 2) + Math.pow((y[p_new] - y[p1]), 2));
    double d3 = Math.sqrt( Math.pow((x[p_new] - x[p2]), 2) + Math.pow((y[p_new] - y[p2]), 2));

    if (d1 > d2 && d1 > d3) {
      ret =  true;
    }
    return ret;
   }


   public IntList getOmkrets(){return this.omkrets;}

   /*Cheeky function of getting median of UNSORTED* list
   of ODD or EVEN* amount of elements. */
   double median(Double[] a){
     int length = a.length;
     double temp;
     boolean odd = false;
     if (length%2 != 0){
       odd = true;
     }

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
     double median = odd? a[(length/2)] : (a[length/2] + a[(length/2)+1])/2;
     return median;
   }// end Median


   // ------------- Parallel Solution ---------------------------

   /* This method is used to initiate a parallel run.
    *   I)  Divide the entire region into *cores* amount of sub-regions.
    *  II)  For each region, find the outermost points.
    * III) Sequentially combine all sub-regions, and find the true othermost points.
   */
   void paraRun(){
     this.omkrets = new IntList();
     IntList[] subRegions = new IntList[cores];
     IntList totalRegion = new IntList();
     Thread[] threads = new Thread[cores];

     //Start each thread with their own points of the region.
     for(int i = 0 ; i < cores; i++){
       subRegions[i] = new IntList();
       threads[i] = new Thread(new Worker(i, subRegions[i]));
       threads[i].start();
     }

     //Let the Threads find, and then combine the subRegions
     //Which consists of all outerpoints found.
     for(int i = 0; i < cores; i++){
       try{
         threads[i].join();
         totalRegion.append(subRegions[i]);
       }catch(InterruptedException e){
         System.out.println("Thread join Error");
       }
     }

     //We now have a list of all potential outer points. Sequentially find the correct
     //Answer by runnning the sequential solution on this new list, consisting of realtivetly
     //Few points compared to the initial problem.

     //Find maxX and minX
     int minIdx = 0; int maxIdx = 0; int curr;
     for(int i = 0; i < totalRegion.size(); i++){
       curr = totalRegion.get(i);
       if(x[curr] >= x[maxIdx]) maxIdx = curr;
       if(x[curr] <= x[minIdx]) minIdx = curr;
     }

     //Create top and bot lists.
     IntList top = new IntList();
     IntList bot = new IntList();

     for(int i = 0; i < totalRegion.size(); i++){
       curr = totalRegion.get(i);
       if( curr == maxIdx || curr == minIdx)  continue;
       if(getDistance(curr, maxIdx, minIdx) <= 0) top.add(curr);
       if(getDistance(curr, maxIdx, minIdx) >=0) bot.add(curr);
     }

     omkrets.add(maxIdx);
     seqRec(maxIdx, minIdx, getLargestDistance(maxIdx, minIdx, top), top, omkrets);

     //Top Circumference found. Now we can add the left-most-point and begin from bottom half.
     omkrets.add(minIdx);
     seqRec(minIdx, maxIdx, getLargestDistance(minIdx, maxIdx, bot), bot, omkrets);
   }//end paraRun()


   /* Each Thread has their own sub-region in which they all should find
   *  the outer points. This way each thread has to check each thir own unique region*/
   public class Worker implements Runnable{
     int id;
     IntList omkrets;

     Worker(int id, IntList omkrets){
       this.id = id;
       this.omkrets = omkrets;
     }

     public void run(){
       //Each thread find the max in their region. Handle each region
       //By checking each their own index of all points.
       int maxIdx = 0; int minIdx = 0;
       for(int i = id; i<n; i+=cores){
         if(x[i] > x[maxIdx]) maxIdx = i;
         if(x[i] < x[minIdx]) minIdx = i;
       }

     //Create top and bottom lists
     IntList top = new IntList();
     IntList bot = new IntList();
     for(int i = id; i<n; i+= cores){
       if( i == minIdx || i == maxIdx) continue;

       if(getDistance(i, maxIdx, minIdx) <= 0) top.add(i);
       if(getDistance(i, maxIdx, minIdx) >= 0) bot.add(i);
     }
     //Each thread run the "Sequential" algorithm for each their own sub-region.
     omkrets.add(maxIdx);
     seqRec(maxIdx, minIdx, getLargestDistance(maxIdx, minIdx, top), top, omkrets);
     omkrets.add(minIdx);
     seqRec(minIdx, maxIdx, getLargestDistance(minIdx, maxIdx, bot), bot, omkrets);
     //We now have *cores* amount of sub-regions, which we should combine into one region sequentially.

   }//end run
 }//end Worker
}//end Oblig5
