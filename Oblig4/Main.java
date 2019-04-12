import java.util.Arrays;

public class Main {
	public static void main(String[] args) {

		// Get the commandline parameters
		if(args.length != 4) {
			System.out.println("Start program with four arguments <n>, <k>, <DIGIT_BITS> and <seed>");
			return;
		}

		int n = Integer.parseInt(args[0]);
		int k = Integer.parseInt(args[1]);
		k = (k==0) ? Runtime.getRuntime().availableProcessors(): k;
		int seed = Integer.parseInt(args[2]);
		int DIGIT_BITS = Integer.parseInt(args[3]);
		double start = 0;
		double stop = 0;
		start = System.nanoTime();

		// Get the array to sort
		int[] arr = Oblig4Precode.generateArray(n, seed);
		int[] fasit = new int[arr.length];
		int[] tmp = new int[arr.length];
		System.arraycopy(arr, 0, tmp, 0, arr.length);
		System.arraycopy(arr, 0, fasit, 0, arr.length);

		stop = (System.nanoTime()-start)/1000000.0;
		System.out.println("Creation Precode: " + stop + "ms");

		Radix radix = new Radix(n, k, DIGIT_BITS);
		Double[] seqTimes = new Double[7];
		Double[] paraTimes = new Double[7];

		System.out.println("---------Sequential Sorting-------------");
		for( int i = 0; i < 7; i++){
			start = System.nanoTime();
			RadixSeq.radix(fasit);
			stop = (System.nanoTime()-start)/1000000.0;
			System.out.println(stop + "ms");
			seqTimes[i] = stop;

			//Test if it is sorted
			for(int j = 0; j < arr.length-2; j+=2){
				if(fasit[j] > fasit[j+1]){
					System.out.println("Error Sequential, Index: " + j);
				}
			}
			if(i<6) System.arraycopy(arr, 0, fasit, 0, arr.length); //reset fasit
		}

		double medianSeq = radix.median(seqTimes);
		System.out.println("--------------------------------------");
		System.out.println("      Median Time Sequential          \n           "+ medianSeq + "ms      ");
		System.out.println("--------------------------------------");


		System.out.println("---------Parallell Sorting-------------");
		for( int i = 0; i < 7; i++){
			radix = new Radix(n,k,DIGIT_BITS);
			start = System.nanoTime();
			tmp = radix.radix(tmp);
			stop = (System.nanoTime()-start)/1000000.0;
			System.out.println(stop + "ms");
			paraTimes[i] = stop;

			//Test if it is sorted
			for(int j = 0; j < arr.length-2; j+=2){
				if(tmp[j] > tmp[j+1]){
					System.out.println("Error Parallell, Index: " + j);
				}
			}
			if(i < 6) System.arraycopy(arr, 0, tmp, 0, arr.length); //reset tmp
		}

		double medianPara = radix.median(paraTimes);
		System.out.println("--------------------------------------");
		System.out.println("      Median Time Parallel          \n           "+ medianPara + "ms      ");
		System.out.println("--------------------------------------");


		double sDp = medianSeq/medianPara;
		System.out.println("--------------------------------------");
		System.out.println("      Speed Up S/P          \n          "+ String.format("%.4f", sDp) + "        ");
		System.out.println("--------------------------------------");

		start = System.nanoTime();
		// Sort it (replace this with Radix)

		int ctr = 0;
		for(int i = 0; i <arr.length; i++){
			//System.out.println(arr[i]);
			if(fasit[i] != tmp[i]) {
				ctr++;
				System.out.println("fasit["+i+"]: "+ fasit[i] + " !=  tmp["+i+"]: "+tmp[i]);
			}
		}
		if(ctr == 0)System.out.println("Sequential and Parallell solution generated same result.");
		else System.out.println("Sequential and parallel not equal. Errors: " + ctr);


		// Save the result
		Oblig4Precode.saveResults(Oblig4Precode.Algorithm.SEQ, seed, fasit);

	}

}
