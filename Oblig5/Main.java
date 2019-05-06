public class Main {
	public static void main(String[] args) {

		// Get the commandline parameters
		if(args.length != 2) {
			System.out.println("Start program with two arguments <n> <k>");
			return;
		}

		int n = Integer.parseInt(args[0]);
		int k = Integer.parseInt(args[1]);
		k = (k==0) ? Runtime.getRuntime().availableProcessors(): k;

		Double[] seqTimes = new Double[7];
		Double[] paraTimes = new Double[7];

		IntList list = new IntList(10);
		Oblig5 o5 = new Oblig5(n, k);

		double stop = 0;
		double start = 0;
		IntList seqRes, paraRes;

		System.out.println("Sequential Algorithm Running...");
		for(int i = 0; i < 7; i++){
			start = System.nanoTime();
			o5.seqRun();
			stop = (System.nanoTime()-start)/1000000.0;
			System.out.println("      "+stop + "ms");
			seqTimes[i] = stop;
		}
		seqRes = o5.getOmkrets();

		double medianSeq = o5.median(seqTimes);
		System.out.println("--------------------------------------");
		System.out.println("      Median Time Sequential          \n           "+ medianSeq + "ms      ");
		System.out.println("--------------------------------------");



		System.out.println("Parallel Algorithm Running...");
		for(int i = 0; i < 7; i++){
			start = System.nanoTime();
			o5.paraRun();
			stop = (System.nanoTime()-start)/1000000.0;
			System.out.println("      "+ stop + "ms");
			paraTimes[i] = stop;
		}
		paraRes = o5.getOmkrets();

		double medianPara = o5.median(paraTimes);
		System.out.println("--------------------------------------");
		System.out.println("      Median Time Parallel          \n           "+ medianPara + "ms      ");
		System.out.println("--------------------------------------");


		double sDp = medianSeq/medianPara;
		System.out.println("--------------------------------------");
		System.out.println("      Speed Up S/P          \n          "+ String.format("%.4f", sDp) + "        ");
		System.out.println("--------------------------------------");

		int error = 0;
		int[] found = new int[seqRes.size()];
		int[] taken = new int[paraRes.size()];



		//The parallel and sequential solution finds different maxX and minX values.
		//This results in the same Omkrets, however one might be shifted. To counteract
		//The shifting, this code makes sure to check if all values in seqRes also exist in ParaRes.

		for(int i = 0; i < seqRes.size(); i++){
			for(int j = 0; j <paraRes.size(); j++){
				if(seqRes.get(i) == paraRes.get(j)){
					if(found[i] == 0  && taken[j] == 0 )
					found[i] = 1;
					taken[j] = 1;
					continue;
				}
			}
		}

		for(int i = 0; i < seqRes.size(); i++){
			if (found[i] == 0 || taken[i] == 0){
				System.out.println("Error: ["+i+"]");
				error ++;
			}

		}

		if(error == 0){
			System.out.println("--------------------------------------");
			System.out.println(" The Sequential and Parallel solutions\n generated the same results.");
			System.out.println("--------------------------------------");
		}


		o5.printOmkrets();
		if(n < 100000) o5.TegnUt();


  }
}
