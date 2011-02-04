package wave;

public class Constants {

	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	static {
		System.out.println("System temp directory is: " + TMP_DIR);
	}
	
	public static final String OUT_DIR = "out";
	static {
		System.out.println("Output directory is: " + OUT_DIR);
	}
	
}
