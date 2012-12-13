package ch.hsr.hsrlunch.util;

public class CheapEncoder {
	private static final String WHATEVER = "LuQmksn09978$1M+hs65asf";

	/**
	 * 
	 * @param inputString
	 * @return encrypted String
	 */
	public static String cheapEncode(CharSequence inputString) {
		StringBuilder output = new StringBuilder();
		inputString = inputString + WHATEVER;
		for (int i = 0; i < inputString.length(); i++) {
			char c = inputString.charAt(i);
			if ((c < 45) || (c > 123)) {
				output.append(c);
			} else {
				c += 42;
				if (c > 123) {
					c -= ((123 - 45) + 1);
				}
				output.append(c);
			}
		}
		return output.toString();
	}

	/**
	 * 
	 * @param inputString
	 *            encrypted
	 * @return decrypted String from simple encrypted String
	 */
	public static String cheapDecode(CharSequence inputString) {
		if (inputString.length() <= WHATEVER.length()) {
			// somethings wrong - can't be an encrypted input
			return inputString.toString();
		}
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < inputString.length(); i++) {
			char c = inputString.charAt(i);
			if ((c < 45) || (c > 123)) {
				output.append(c);
			} else {
				c -= 42;
				if (c < 45) {
					c += ((123 - 45) + 1);
				}
				output.append(c);
			}
		}
		try {
			output = new StringBuilder(output.subSequence(0, output.length()
					- WHATEVER.length()));
		} catch (StringIndexOutOfBoundsException e) {
		}
		return output.toString();
	}
}
