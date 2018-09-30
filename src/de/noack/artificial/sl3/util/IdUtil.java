package de.noack.artificial.sl3.util;

public abstract class IdUtil {

	private static int ID_COUNTER = 0;

	public static int createNewId() {
		return ID_COUNTER++;
	}
}
