
public class Oblig3 {

	public static void main(String[] args) {

		if(args.length != 2) {
			System.out.printf("Need two arguments, [n] [k].\n");
			return;
		}

		int n = Integer.parseInt(args[0]);
		int k = Integer.parseInt(args[1]);
		k = (k==0) ? Runtime.getRuntime().availableProcessors(): k;
		if(n < 16){
			System.out.println("Error. 'n' ("+n+") must be at least 16. ");
			return;
		}
		System.out.println("---------Threads Available--------");
		System.out.println("                 " + k);

		SievePara para = new SievePara(n, k);
		Sieve sieve = new Sieve(n);

		double start = 0;
		double stop = 0;

		Double[] seqTimes = new Double[7];
		Double[] paraTimes = new Double[7];

		int[] primes1 = null;
		int[] primes2 = null;

		System.out.println("---------Sequential Sieve-------------");
		for( int i = 0; i < 7; i++){
			start = System.nanoTime();
			primes1 = sieve.findPrimes();
			stop = (System.nanoTime()-start)/1000000.0;
			System.out.println(stop + "ms");
			seqTimes[i] = stop;
			sieve = new Sieve(n);
		}
		double medianSeq = para.median(seqTimes);
		System.out.println("--------------------------------------");
		System.out.println("      Median Time Sequential          \n           "+ medianSeq + "ms      ");
		System.out.println("--------------------------------------");
		System.out.println("----------Parallel Sieve--------------");

		for( int i = 0; i < 7; i++){
			start = System.nanoTime();
			primes2 = para.getPrimes();
			stop = (System.nanoTime()-start)/1000000.0;
			System.out.println(stop + "ms");
			paraTimes[i] = stop;
			para = new SievePara(n, k);
		}
		double medianPara = para.median(paraTimes);
		System.out.println("--------------------------------------");
		System.out.println("      Median Time Parallel          \n           "+ medianPara + "ms      ");
		System.out.println("--------------------------------------");


		double sDp = para.median(seqTimes)/para.median(paraTimes);
		System.out.println("--------------------------------------");
		System.out.println("      Speed Up S/P          \n          "+ String.format("%.4f", sDp) + "        ");
		System.out.println("--------------------------------------");
		int error = 0;
		int nonzero = 0;
		for(int i = 0; i < primes1.length; i++){
			if (primes1[i] != 0) nonzero ++;
			if(primes1[i] != primes2[i])  error ++;
			//System.out.println("primes1["+i+"] : "+ primes1[i] + ", primes2["+i+"]: "+primes2[i]);
		 }
		System.out.println("--------------------------------------");
		System.out.println("Errors: " + error);
		System.out.println("Primes: " + nonzero);
		System.out.println("--------------------------------------");



	System.out.println("-------Sequential Factoriazation------");

	for(int i = 0; i <7; i++){
		para = new SievePara(n,k);
		para.setPrimes(primes2);
		start = System.nanoTime();
		para.factorizeSeq();
		stop = (System.nanoTime() - start )/1000000.0;
		seqTimes[i] = stop;
		System.out.println(stop + "ms");
	}
	medianSeq = para.median(seqTimes);
	System.out.println("--------------------------------------");
	System.out.println("      Median Time Sequential          \n           "+ medianSeq + "ms      ");
	System.out.println("--------------------------------------");




	System.out.println("-------Parallel Factoriazation------");

	for(int i = 0; i <7; i++){
		para = new SievePara(n,k);
		para.setPrimes(primes2);
		start = System.nanoTime();
		para.factorizePara();
		stop = (System.nanoTime() - start )/1000000.0;
		paraTimes[i] = stop;
		System.out.println(stop + "ms");
	}

	medianPara = para.median(paraTimes);
	System.out.println("--------------------------------------");
	System.out.println("      Median Time Parallel          \n           "+ medianPara + "ms      ");
	System.out.println("--------------------------------------");


	sDp = para.median(seqTimes)/para.median(paraTimes);
	System.out.println("--------------------------------------");
	System.out.println("      Speed Up S/P          \n           "+ sDp + "        ");
	System.out.println("--------------------------------------");


	//System.out.println("SpeedUp, S/P: " + factorTimes[0]/factorTimes[1]);
	}
}
