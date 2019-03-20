
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
		double start = 0;
		double stop = 0;


		Sieve sieve = new Sieve(n);
		start = System.nanoTime();
		int[] primes1 = sieve.findPrimes();
		stop = (System.nanoTime()-start)/1000000.0;

		System.out.println("Seq time: " + stop);

		//for(int i = 0; i < primes.length; i++){
			//System.out.printf("Prime %d : %d\n",i, primes[i]);
	//	}

		SievePara para = new SievePara(n, k);


		int counter = 0;
		start = System.nanoTime();
		int[] primes2 = para.getPrimes();
		stop = (System.nanoTime()-start)/1000000.0;
		System.out.println("para time: " + stop);
		for(int i = 0; i < primes1.length; i++){
			//System.out.println("primes1["+i+"] : "+ primes1[i] + ", primes2["+i+"]: "+primes2[i]);
			if(primes1[i] != primes2[i]){
				 //System.out.println("NO MATCH");
				  counter ++;
			 }
	}
	System.out.println("Errors: " + counter);





	}
}
