import java.util.*;
import java.io.*;


public class ZipTester2 {

	public static final String FILE_NAME = "Kafka.txt"; //Assumes .txt extension


    public static boolean compareFiles (String file1, String file2) {
    	BufferedReader bReader = null;
    	BufferedReader bReader2 = null;
    	int line = 1;
    	try {
			bReader = new BufferedReader(new FileReader(file1));
			bReader2 = new BufferedReader(new FileReader(file2));
			Scanner in = new Scanner(bReader);
			Scanner in2 = new Scanner(bReader2);
			while (in.hasNextLine() && in2.hasNextLine()) {
				boolean lineEqual = true;
				String input = in.nextLine();
				String input2 = in2.nextLine();
				lineEqual = input.equals(input2);
				if (!lineEqual) {
					System.out.println("First difference between "+file1+" and "+file2+" found on line " + line);
					System.out.println(file1+":"+input);
					System.out.println(file2+":"+input2);
					return false;
				}
			}
			if (in.hasNextLine() || in2.hasNextLine()) {
				System.out.println(file1 + " and " + file2 + " have different lengths");
				return false;
			}
    	} catch (IOException ex) {
    		System.out.println("File cannot be read.");
    		return false;
		} finally {
			try{
				if (bReader != null) bReader.close();
				if (bReader2 != null) bReader2.close();
			} catch(IOException ex) {
				System.out.println("File failed to close.");
				return false;
			}
		}
		return true;
    }



	public static void main(String[] args) {

		FileZip3 zipper = new FileZip3();

		// File splitting
		if(!(new File(FILE_NAME).exists())) {
			System.out.println("File does not exist. Quitting.");
			System.exit(1);
		}
		int i = FILE_NAME.lastIndexOf(".");
		String extension = "";
		String testFile = FILE_NAME;
		if (i >= 0) {
			extension = testFile.substring(i);
			testFile = testFile.substring(0,i);
		}

		System.out.println("TESTING: Do your readFile and writeFile produce exact copies?");
		try {
			String data = zipper.readFileAsASCIIString(testFile+extension);
			zipper.writeFileAsASCIIString(data,testFile+"2"+extension);
		} catch (Exception e) {
			System.out.println("FileZip's read or write threw an exception!");
			e.printStackTrace();
		}
		boolean equal1 = compareFiles(testFile+extension,testFile+"2"+extension);
		System.out.println("Is the file the same after a read, then a write? " + equal1);
		System.out.println();


		System.out.println("TESTING: How long does it take to zip and unzip?");
		long startTime = System.currentTimeMillis();
		FileZip3 zipper1 = new FileZip3();
		try {
			zipper1.zip(testFile+extension, testFile+".zip");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileZip3 zipper2 = new FileZip3();
		zipper2.unzip(testFile+".zip", testFile+"3"+extension);
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		System.out.println("The run time (nanoseconds): " + time);
		System.out.println();


		System.out.println("TESTING: Is your unzipped file exactly equal to the original?");
		boolean equal2 = compareFiles(testFile+extension,testFile+"3"+extension);
		System.out.println("Is the file the same after zip, then a unzip? " + equal2);
		System.out.println();


		System.out.println("TESTING: What compression ratio do you achieve?");
    	File first = new File(testFile+extension);
    	File second = new File(testFile+".zip");
    	double ratio = (double)second.length() / first.length();
    	System.out.println("The compression ratio: " + ratio);

	}


}
















