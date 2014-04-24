package fr.cobaltians.cobalt.database;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.graphics.Bitmap;
import android.util.Base64;

public class CobaltImageCache {

	private static CobaltImageCache sInstance;
	
	private static Map<String, Bitmap> mMapImages;
	
	private CobaltImageCache() {
		mMapImages = new HashMap<String, Bitmap> ();
	}

	public static CobaltImageCache getInstance() {
		if (sInstance  == null) {
			sInstance = new CobaltImageCache();
		}
		
		return sInstance;
	}
	
	public static void setImage(String id, Bitmap image) {
		mMapImages.put(id, image);
	}
	
	public static Bitmap getImage(String id) {
		Bitmap image = null;
		if (mMapImages.containsKey(id)) {
			image = mMapImages.get(id);	
		}
		return image;
	}
	
	public static String toBase64(String id) {
		String encodeImage;
		Bitmap bitmap = mMapImages.get(id);
		
		// compressing the image
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		// encode image
		encodeImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT | Base64.NO_WRAP);
		
		return encodeImage;
	}
}
