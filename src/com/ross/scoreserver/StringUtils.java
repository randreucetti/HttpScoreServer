package com.ross.scoreserver;

import java.util.Random;

/**
 * Simple Utility class for generating SessionIds, could be improved using UUID
 * most likely. Nice for the moment
 * 
 * @author Ross Andreucetti
 * @since 25 Nov 2014
 *
 */
public class StringUtils {

	static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd = new Random();

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

}
