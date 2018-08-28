package com.ldx.conversationbase.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.ldx.conversationbase.R;
import com.ldx.conversationbase.common.XICacheResourceManager;
import com.ldx.conversationbase.common.XIChatConst;

public class XIPictureUtil {

	/**
	 * computer image scale
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/**
	 * get bitmap with compress
	 * 
	 * @param
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 320, 480);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}
	
	/**
	 * compress big image
	 * @param srcPath
	 * @return
	 */
	public static Bitmap compressSizeImage(String srcPath) {  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);
          
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;
        float hh = 800f;
        float ww = 480f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
        return compressImage(bitmap);
    }

	/**
	 * compress big image
	 * @param srcPath
	 * @return
	 */
	public static Bitmap compressSizeImage(String srcPath ,int maxSize) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 1024;
		float ww = 768;
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap,maxSize);
	}

	public static Bitmap compressImage(Bitmap image ,int maxSize) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while ( baos.toByteArray().length > maxSize) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}

	public static Bitmap compressImage(Bitmap image) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;  
        while ( baos.toByteArray().length > XIChatConst.XICONVERSATION_IMAGE_MAXSIZE) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;  
    }  
	
	 /** 
	 * get bitmap rotate
	 * @param bitmap
	 * @param path
	 */  
	public static Bitmap reviewPicRotate(Bitmap bitmap,String path){  
	    int degree = getPicRotate(path);  
	    if(degree!=0){  
	        Matrix m = new Matrix();    
	        int width = bitmap.getWidth();    
	        int height = bitmap.getHeight();    
	        m.setRotate(degree);
	        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,m, true);
			return bitmap;
		}  else{
			return null;
		}
	}  
	  
	/**
	 * @param path
	 * @return
	 */  
	public static int getPicRotate(String path) {  
	    int degree  = 0;  
	    try {  
	        ExifInterface exifInterface = new ExifInterface(path);  
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
	        switch (orientation) {  
	        case ExifInterface.ORIENTATION_ROTATE_90:  
	            degree = 90;  
	            break;  
	        case ExifInterface.ORIENTATION_ROTATE_180:  
	            degree = 180;  
	            break;  
	        case ExifInterface.ORIENTATION_ROTATE_270:  
	            degree = 270;  
	            break;  
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	    return degree;  
	}

	public static boolean saveImageToGallery(Context context, String path) {
		File file = new File(path);
		String fileName = file.getName();
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

			Uri uri = Uri.fromFile(file);
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean saveImageToSDPath(Context context, String origiPath, String savePath) {
		Bitmap bitmap = XIPictureUtil.getBitmap(origiPath);
		try {
			XIFileSaveUtil.saveBitmap(bitmap, savePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ((new File(savePath)).exists()) {
				try {
					File file = new File(savePath);
					MediaStore.Images.Media.insertImage(context.getContentResolver(),
							savePath,file.getName(), null);
					Uri data = Uri.fromFile(file);
					context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if ((new File(savePath)).exists()) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Get bitmap from specified image path
	 *
	 * @param imgPath
	 * @return
	 */
	public static Bitmap getBitmap(String imgPath) {
		// Get bitmap through image path
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = false;
		newOpts.inPurgeable = true;
		newOpts.inInputShareable = true;
		// Do not compress
		newOpts.inSampleSize = 1;
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(imgPath, newOpts);
	}

	/**
	 * Store bitmap into specified image path
	 *
	 * @param bitmap
	 * @param outPath
	 * @throws FileNotFoundException
	 */
	public static boolean storeImage(Bitmap bitmap, String outPath) throws FileNotFoundException {
		try {
			File f = new File(outPath);
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Compress image by pixel, this will modify image width/height.
	 * Used to get thumbnail
	 *
	 * @param imgPath image path
	 * @param pixelW target pixel of width
	 * @param pixelH target pixel of height
	 * @return
	 */
	public static Bitmap ratio(String imgPath, float pixelW, float pixelH) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
		// Get bitmap info, but notice that bitmap is null now
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath,newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = pixelH;
		float ww = pixelW;

		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) be = 1;
		newOpts.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
		return bitmap;
	}

	/**
	 * Compress image by size, this will modify image width/height.
	 * Used to get thumbnail
	 *
	 * @param image
	 * @param pixelW target pixel of width
	 * @param pixelH target pixel of height
	 * @return
	 */
	public static  Bitmap ratio(Bitmap image, float pixelW, float pixelH,int maxSize) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, os);
		// scale
		int options = 100;
		// Store the bitmap into output stream(no compress)
		image.compress(Bitmap.CompressFormat.JPEG, options, os);
		// Compress by loop
		while ( os.toByteArray().length / 1024 > maxSize) {
			// Clean up os
			os.reset();
			// interval 10
			options -= 10;
			image.compress(Bitmap.CompressFormat.JPEG, options, os);
		}

		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = pixelH;
		float ww = pixelW;

		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) be = 1;
		newOpts.inSampleSize = be;
		is = new ByteArrayInputStream(os.toByteArray());
		bitmap = BitmapFactory.decodeStream(is, null, newOpts);
		return bitmap;
	}

	/**
	 * Compress by quality,  and generate image to the path specified
	 *
	 * @param image
	 * @param outPath
	 * @param maxSize target will be compressed to be smaller than this size.(kb)
	 * @throws IOException
	 */
	public static void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// scale
		int options = 100;
		// Store the bitmap into output stream(no compress)
		image.compress(Bitmap.CompressFormat.JPEG, options, os);
		// Compress by loop
		while ( os.toByteArray().length / 1024 > maxSize) {
			// Clean up os
			os.reset();
			// interval 10
			options -= 10;
			image.compress(Bitmap.CompressFormat.JPEG, options, os);
		}

		// Generate compressed image file
		FileOutputStream fos = new FileOutputStream(outPath);
		fos.write(os.toByteArray());
		fos.flush();
		fos.close();
	}

	/**
	 * Compress by quality,  and generate image to the path specified
	 *
	 * @param imgPath
	 * @param outPath
	 * @param maxSize target will be compressed to be smaller than this size.(kb)
	 * @param needsDelete Whether delete original file after compress
	 * @throws IOException
	 */
	public static void compressAndGenImage(String imgPath, String outPath, int maxSize, boolean needsDelete) throws IOException {
		compressAndGenImage(getBitmap(imgPath), outPath, maxSize);

		// Delete original file
		if (needsDelete) {
			File file = new File (imgPath);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * Ratio and generate thumb to the path specified
	 *
	 * @param image
	 * @param outPath
	 * @param pixelW target pixel of width
	 * @param pixelH target pixel of height
	 * @throws FileNotFoundException
	 */
	public static boolean ratioAndGenThumb(Bitmap image, String outPath, float pixelW, float pixelH,int maxSize) throws FileNotFoundException {
		Bitmap bitmap = ratio(image, pixelW, pixelH,maxSize);
		return storeImage( bitmap, outPath);
	}

	/**
	 * Ratio and generate thumb to the path specified
	 *
	 * @param
	 * @param outPath
	 * @param pixelW target pixel of width
	 * @param pixelH target pixel of height
	 * @param needsDelete Whether delete original file after compress
	 * @throws
	 */
	public  static void ratioAndGenThumb(String imgPath, String outPath, float pixelW, float pixelH, boolean needsDelete) throws FileNotFoundException {
		Bitmap bitmap = ratio(imgPath, pixelW, pixelH);
		storeImage( bitmap, outPath);

		// Delete original file
		if (needsDelete) {
			File file = new File (imgPath);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	public static String getImgSavePath(Context context){
		String dir = XIFileSaveUtil.getBasePath(context) + XIChatConst.XIIMAGEDATAPATH;
		try{
			dir = XICacheResourceManager.getImagePath(context);
		}catch(Exception e){
			e.printStackTrace();
		}

		try {
			XIFileSaveUtil.createSDDirectory(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileName = String.valueOf(System.currentTimeMillis() + ".png");
		return dir + fileName;
	}

	public static String getSaveToSDImgPath(){
		String dir = XIFileSaveUtil.SD_CARD_PATH;
		try {
			XIFileSaveUtil.createSDDirectory(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileName = String.valueOf(System.currentTimeMillis() + ".png");
		return dir + fileName;
	}

	public static  String getOriginalImgSavePicPath(Context context) {
		String dir = XIFileSaveUtil.getBasePath(context)+ XIChatConst.XIORIGINALIMAGEDATAPATH;
		try{
			dir = XICacheResourceManager.getOriginalImagePath(context);
		}catch(Exception e){
			e.printStackTrace();
		}

		try {
			XIFileSaveUtil.createSDDirectory(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileName = String.valueOf(System.currentTimeMillis() + ".png");
		return dir + fileName;
	}

	public static void genThumPic(final String path, final String savePath, int maxSize){
		File file = new File(path);
		if (file.exists()) {
			int size = XIImageCheckoutUtil.getImageSize(XIImageCheckoutUtil.getLoacalBitmap(path));
			if (size > maxSize) {
				downAndCompress(path,savePath);
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Bitmap bitmap = XIPictureUtil.getBitmap(path);
						try {
							XIFileSaveUtil.saveBitmap(bitmap, savePath);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		} else {

		}
	}


	public static void  downAndCompress(final String path, final String savePath) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isSave =false;
				try {
					Bitmap bitmap = XIPictureUtil.getBitmap(path);
					if (bitmap != null){
						try {
							int [] origianl = XIFileSaveUtil.getImageWidthHeight(path);
							boolean issuccess =  XIPictureUtil.ratioAndGenThumb(bitmap, path, origianl[0], origianl[1],1024);
							if (issuccess){
								isSave = true;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					Bitmap tempBitmap = XIPictureUtil.reviewPicRotate(bitmap, path);
					if (tempBitmap != null ){
						isSave = XIFileSaveUtil.saveBitmap(
								XIPictureUtil.reviewPicRotate(bitmap, path),
								savePath);
					}
					/*File file = new File(path);
					if (file.exists() && isSave) {

					}*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
