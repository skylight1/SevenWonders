package com.faganphotos.sevenwonders.view;

/**
 * High performance number parsing methods.
 * No argument checking and limited formats accepted.
 * 
 * Temporarily copied from SkylightOpenGL library
 * because the library version was broken for models with large coordinate numbers.
 *
 */
public class QuickParseUtil {

	private static final int FLOAT_DIGITS = 24;
	
	private static final int FLOAT_PLACES = FLOAT_DIGITS * 2;
	
	private static final int DIGIT_RANGE = 10;
	
	private final static float[][] FLOAT_PLACE_VALUES = new float[FLOAT_PLACES][DIGIT_RANGE];
	
	static {
		double power = FLOAT_DIGITS;
		for (int place = 0; place < FLOAT_PLACES; place++) {			
			for (int value = 0; value < DIGIT_RANGE; value++) {
				FLOAT_PLACE_VALUES[place][value] = (float) (Math.pow(10d, power) * value);
			}
			power--;
		}
	}
	
	/**
	 * Parse string containing a float.
	 * <p>
	 * Current implementation took 25ms to parse 1000 test strings,
	 * Float.parseFloat took 400ms to parse the same strings.
	 * <p>
	 * Assumes input not null, trimmed, not infinity, not NaN, not using exponent representation.
	 * Handles only: [-+]?\d+(.\d+)?
	 * 
	 * @param input String containing a float number
	 * @return parsed float
	 */
	public static float quickParseFloat(final String input) {
			
		//Check for sign.
		int inputIndex = 0;
		boolean isNegative = false;
		switch( input.charAt(0) ) {
			case '-':
				isNegative = true;
				//Fall through.
			case '+':
				inputIndex++;
		}
		
		//Determine what place value number starts at.
		int decimalIndex = input.indexOf('.');
		if ( -1 == decimalIndex ) {
			decimalIndex = input.length();
		}
		int place = FLOAT_DIGITS - (decimalIndex - inputIndex) + 1;
		
		//Add value for each place in the number.
		float result = 0;
		for( ; inputIndex < input.length(); inputIndex++) {
			if ( inputIndex == decimalIndex ) continue;
			
			result += FLOAT_PLACE_VALUES[place][input.charAt(inputIndex) - '0'];
			place++;
		}
	
		if ( isNegative ) {
			result *= -1f;
		}	
		return result;
	}
	

	private final static int[][] INTEGER_DECIMAL_VALUES = new int[4][10];

	static {
		for (int decimalPlace = 0; decimalPlace < INTEGER_DECIMAL_VALUES.length; decimalPlace++) {
			for (int decimalValue = 0; decimalValue < 10; decimalValue++) {
				INTEGER_DECIMAL_VALUES[decimalPlace][decimalValue] = (int) (Math.pow(10d, decimalPlace) * decimalValue);
			}
		}
	}

	public static int quickParseInteger(final String aStringRepresentationOfAnInteger) {
		final int startOfDigits;
		final int sign;
		if (aStringRepresentationOfAnInteger.charAt(0) == '-') {
			startOfDigits = 1;
			sign = -1;
		} else {
			startOfDigits = 0;
			sign = 1;
		}
		int result = 0;
		int decimalPlace = -1;
		final int stringLength = aStringRepresentationOfAnInteger.length();
		for (int i = stringLength - 1; i >= startOfDigits; i--) {
			decimalPlace++;
			result += INTEGER_DECIMAL_VALUES[decimalPlace][aStringRepresentationOfAnInteger.charAt(i) - '0'];
		}
		return sign * result;
	}
	
}
