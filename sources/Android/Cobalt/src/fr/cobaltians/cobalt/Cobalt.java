package fr.cobaltians.cobalt;

public class Cobalt {

	private static String sResourcePath = "www/";
	
	public static String getResourcePath() {
		return sResourcePath;
	}
	
	public static void setResourcePath(String resourcePath) {
		if (resourcePath != null) {
			sResourcePath = resourcePath;
		}
	}
}
