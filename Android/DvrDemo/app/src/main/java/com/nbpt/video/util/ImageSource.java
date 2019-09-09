package com.nbpt.video.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class ImageSource {
	private String TAG="ImageSource";
	public static List<Map<String, String>> c = null;
	public static int GESHU = 0;

	public static String[] getImageName(ArrayList allImgFile) {
		c = new ArrayList<Map<String, String>>();

		//ArrayList allImgFile = FileUtils.getAllPicFile();
		GESHU = allImgFile.size();
		System.out.println("allimagefile.size - - - - - =" + allImgFile.size());
		String[] files002 = new String[allImgFile.size()];//开辟一个对应大小的缓存控件
		for (int i = 0; i < allImgFile.size(); i++) {
			Log.d("ImageSource", "getImage path: "+allImgFile.get(i).toString());//完整路径
			File file002 = new File(allImgFile.get(i).toString());//File 类 可以使用url
			files002[i] = file002.getName();
			Map<String, String> item = new HashMap<String, String>();
			System.out.println("file002====key:"+ String.valueOf(i)+ "value:"+file002.getName());

			//item.put(String.valueOf(i), file002.getName());
			item.put(String.valueOf(i), allImgFile.get(i).toString());

			c.add(item);

		}
		return files002;
	}
}