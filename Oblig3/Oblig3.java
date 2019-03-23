
public class Oblig3 {

	public static void main(String[] args) {

		if(args.length != 2) {
			System.out.printf("Need two arguments, [n] [k].\n");
			return;
		}

		int n = Integer.parseInt(args[0]);
		int k = Integer.parseInt(args[1]);
		if(n < 16){
			System.out.println("Error. 'n' ("+n+") must be at least 16. ");
			return;
		}

		SievePara para = new SievePara(n, k);
		Sieve sieve = new Sieve(n);
		double start = 0;
		double stop = 0;

		System.out.println("----------------------");

		start = System.nanoTime();
		int[] primes1 = sieve.findPrimes();
		stop = (System.nanoTime()-start)/1000000.0;
		System.out.println("Seq time: " + stop + "ms");


		int error = 0;
		int nonzero = 0;
		start = System.nanoTime();
		int[] primes2 = para.getPrimes();
		stop = (System.nanoTime()-start)/1000000.0;
		System.out.println("para time: " + stop + "ms");


		for(int i = 0; i < primes1.length; i++){
			if (primes1[i] != 0) nonzero ++;
			if(primes1[i] != primes2[i])  error ++;
			//System.out.println("primes1["+i+"] : "+ primes1[i] + ", primes2["+i+"]: "+primes2[i]);
		 }
	System.out.println("----------------------");
	System.out.println("Errors: " + error);
	System.out.println("Primes: " + nonzero);
	System.out.println("----------------------");

	System.out.println("Factorization times:");
	start = System.nanoTime();
	para.factorizeSeq();
	stop = (System.nanoTime() - start )/1000000.0;
	System.out.println("Sequential:" + stop + "ms");


	start = System.nanoTime();
	para.factorizePara();
	stop = (System.nanoTime() - start )/1000000.0;

	System.out.println("Parallel: "  + stop + "ms");

	}
}
