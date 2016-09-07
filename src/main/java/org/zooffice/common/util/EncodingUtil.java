package org.zooffice.common.util;

import java.io.IOException;
import java.nio.charset.Charset;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/**
 * Encoding detection utility from byte array.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public abstract class EncodingUtil {

	private static final int MINIMAL_CONFIDENCE_LEVEL = 70;

	/**
	 * Decode the byte array with auto encoding detection feature.
	 * @param data byte array
	 * @param defaultEncoding the default encoding if no encoding is sure.
	 * @return decoded string
	 * @throws IOException occurs when the decoding is failed.
	 */
	public static String getAutoDecodedString(byte[] data, String defaultEncoding) throws IOException {
		return new String(data, detectEncoding(data, defaultEncoding));
	}

	/**
	 * Detect encoding of given data.
	 * @param data byte array
	 * @param defaultEncoding the default encoding if no encoding is sure.
	 * @return encoding name detected encoding name
	 * @throws IOException  occurs when the detection is failed.
	 */
	public static String detectEncoding(byte[] data, String defaultEncoding) throws IOException {
		CharsetDetector detector = new CharsetDetector();
		detector.setText(data);
		CharsetMatch cm = detector.detect();
		String estimatedEncoding = cm.getName();
		boolean isReliable = Charset.isSupported(estimatedEncoding) && cm.getConfidence() >= MINIMAL_CONFIDENCE_LEVEL;
		return isReliable ? estimatedEncoding : defaultEncoding;
	}
}
