package com.nbpt.video.fileserver.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageSource {
	public static List<Map<String, String>> c = null;
	public static int GESHU = 0;

	public static String[] getImageName(ArrayList allImgFile) {
		c = new ArrayList<Map<String, String>>();

		//ArrayList allImgFile = FileUtils.getAllPicFile();
		GESHU = allImgFile.size();
		System.out.println("allimagefile.size - - - - - =" + allImgFile.size());
		String[] files002 = new String[allImgFile.size()];
		for (int i = 0; i < allImgFile.size(); i++) {
			File file002 = new File(allImgFile.get(i).toString());
			files002[i] = file002.getName();
			System.out.println("file002====" + file002.getName());

			Map<String, String> item = new HashMap<String, String>();

			item.put(String.valueOf(i), file002.getName());

			c.add(item);

		}
		return files002;
	}
}