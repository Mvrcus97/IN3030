import java.util.Arrays;

public class Main {

	public static void main(String[] args) {


		// Get the commandline parameters
		if(args.length != 2) {
			System.out.println("Start program with two arguments <n> and <seed>");
			return;
		}

		int n = Integer.parseInt(args[0]);
		int seed = Integer.parseInt(args[1]);
		double start = 0;
		double stop = 0;
		start = System.nanoTime();

		// Get the array to sort
		int[] arr = Oblig4Precode.generateArray(n, seed);
		int[] arr2 = new int[arr.length];
		System.arraycopy(arr, 0, arr2, 0, arr.length);
		stop = (System.nanoTime()-start)/1000000.0;
		System.out.println("Creation Precode: " + stop + "ms");
		Radix radix = new Radix(n, 0, 4);


		start = System.nanoTime();
		// Sort it (replace this with Radix)
		arr = radix.radix(arr);
		stop = (System.nanoTime()-start)/1000000.0;
		System.out.println("Para: " + stop + "ms");
		start = System.nanoTime();
		Arrays.sort(arr2);
		stop = (System.nanoTime()-start)/1000000.0;
		System.out.println("Seq: " + stop + "ms");

		System.out.println("After sort: ");
		int ctr = 0;
		for(int i = 0; i <arr.length; i++){
			//System.out.println(arr[i]);
			if(arr[i] != arr2[i]) {
				ctr++;
				System.out.println("arr["+i+"]: "+ arr[i] + " !=  arr2["+i+"]: "+arr2[i]);
			}
		}
		System.out.println("ERRORS: " + ctr);



		// Save the result
		Oblig4Precode.saveResults(Oblig4Precode.Algorithm.SEQ, seed, arr);

	}

}
