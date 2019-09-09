package com.nbpt.video.fileserver.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FileStorage {
	private String TAG="FileStorage";
	FileStorage(Context context) {

	}

	public static String CheckExternalStorage(Context context) {

		String extSdCard = "";
		StorageManager sm = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		// 获取sdcard的路径：外置和内置
		Class<?> storageVolumeClazz = null;
		try {
			storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
			//String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);
			Method getVolumeList = sm.getClass().getMethod("getVolumeList");
			Object resultObject = getVolumeList.invoke(sm);
			final int length = Array.getLength(resultObject);
			//Log.d("FileStorage", "CheckExternalStorage: "+sm.getClass());
			Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
			//String[] paths = (String[]) sm.getClass().getMethod("getPath" ).invoke(sm, null);
			//Log.d("FileStorage", "-------------------CheckExternalStorage: "+paths);
			String esd = Environment.getExternalStorageDirectory().getPath();
			//Log.d("FileStorage", "esd: "+esd);
			for (int i = 0; i < length; i++) {
				Object storageVolumeElement = Array.get(resultObject, i);
				String path = (String) getPath.invoke(storageVolumeElement);
				//Log.d("FileStorage", "path: "+path);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable) {
                    extSdCard = path;
                }
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Log.d("FileStorage", "CheckExternalStorage!!!!!!!!!!!!!!!: "+extSdCard);
		return extSdCard;

	}

	public static boolean ExtSDStorage(Context context) {
		StorageManager sm = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		// 获取sdcard的路径：外置和内置
		try {
			String[] paths = (String[]) sm.getClass()
					.getMethod("getVolumePaths", null).invoke(sm, null);
			//Log.d("ExtSDStorage", "ExtSDStorage: "+paths);
			String esd = Environment.getExternalStorageDirectory().getPath();
			Log.d("ExtSDStorage", "getPath: "+esd);
			for (int i = 0; i < paths.length; i++) {
				Log.d("ExtSDStorage", "paths: "+paths[i]);
				if (paths[i].equals(esd)) {
					continue;//0是本机内存
				}
				//File sdFile = new File(paths[i]);
				File sdFile = new File(paths[i],"test.txt");
				Log.d("ExtSDStorage", "sdFile: "+sdFile);
				if (sdFile.canWrite()) {
					// extSdCard = paths[i];
					return true;
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;

	}
	/**
	 * @param context
	 * @return true
	 * **/

	public static boolean getExtsdTure(Context context) {
		String extSdCard = "";
		StorageManager sm = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		// 获取sdcard的路径：外置和内置
		try {
			String[] paths = (String[]) sm.getClass()
					.getMethod("getVolumePaths", null).invoke(sm, null);
			String esd = Environment.getExternalStorageDirectory().getPath();

			for (int i = 0; i < paths.length; i++) {
				if (paths[i].equals(esd)) {
					continue;
				}
				File sdFile = new File(paths[i]);
				if (sdFile.canWrite()) {
					if (getSDFreeSize() > 10) {
						return true;
					}

				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static float getSDFreeSize() {
		// 取得SD卡文件路径
		//File path = new File(Environment.getExternalStorageDirectory()
		//		.getPath());
		File path = new File("/mnt/extsd");
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		// long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		// long blockFree = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
//		long availableSpare = (long) (sf.getBlockSize() * ((long) sf
//				.getAvailableBlocks() - 4));
		if(sf !=null){
			return sf.getAvailableBlocks()*(sf.getBlockSize()/(1024f*1024f));
		}
		
	
		return 0.0f;
	}
	
	public static void CreateFilePath(){
		
	}

}
