/**
 *
 * CobaltImageCache
 * Cobalt
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Cobaltians
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package fr.cobaltians.cobalt.database;

import fr.cobaltians.cobalt.Cobalt;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

public class CobaltImageCache {

    private static final String TAG = CobaltImageCache.class.getSimpleName();

    private static CobaltImageCache sInstance;

    private Hashtable<String, Bitmap> mMapImages;
	
	private CobaltImageCache() {
        mMapImages = new Hashtable<String, Bitmap> ();
	}

	public static CobaltImageCache getInstance() {
		if (sInstance  == null) {
			sInstance = new CobaltImageCache();
		}
		
		return sInstance;
	}
	
	public void setImage(String id, Bitmap image) {
        if (id != null
            && image != null) {
            mMapImages.put(id, image);
        }
        else if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - setImage: id and image could not be null!");
	}
	
	public Bitmap getImage(String id) {
        if (id != null) {
            return mMapImages.get(id);
        }
        else {
            if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - getImage: id could not be null!");
            return null;
        }
	}
	
	public String toBase64(String id) {
        String encodeImage = null;

        if (id != null) {
            Bitmap bitmap = mMapImages.get(id);

            if (bitmap != null) {
                // compressing the image
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                // encode image
                encodeImage = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
            }
        }
        else if (Cobalt.DEBUG) Log.e(Cobalt.TAG, TAG + " - toBase64: id could not be null!");

        return encodeImage;
	}
}
