package com.airbus.retex.service.impl.util;

import java.util.ArrayList;
import java.util.Arrays;


public class MediaUtil {
    /**
	 * private contructor
	 */
	private MediaUtil() {

	}

	/**
	 * @return
	 */
    public static String getFileNameWithoutPath(String filename) {
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(filename.split("/")));

        return parts.get(parts.size() - 1);
    }

    /**
     * @return
     */
    private static String getFilePath(String filename) {
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(filename.split("/")));
        parts.remove(parts.size() - 1);

        return String.join("/", parts);
    }

    /**
     * @param format
     * @return
     */
    public static String getFilenameForFormat(String format, String filename) {
        if (null == format || format.isEmpty()) {
            return filename;
        }

        return getFilePath(filename) + "/" + format + "-" + getFileNameWithoutPath(filename);
    }
}
