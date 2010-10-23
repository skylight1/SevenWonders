package skylight1.sevenwonders;

public class TestClass {

	public static void main(String[] args) {
		int count = 0;
		final int hereGoes = count++;
		String message = "hello " + hereGoes;
		for (String string : args) {
			System.out.println(message + string + hereGoes);
		}
		System.out.println(count);
	}
}
