package com.nbpt.video.fileserver.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileUtils {
    public static String TAG = "FileUtils";

    public static Uri mExtsdPathTreeUri;
    public static String mExtsdPath = "";
    public static String mVideoPath = "/RecordCamera/CameraVideo/";
    public static String mLockVideoPath = "/RecordCamera/CameraLockVideo/";
    public static String mPicturePath = "/RecordCamera/CameraImage/";
    public static String mPictureFolderPath = "/RecordCamera/Folder/";
    public static String mThumbnailPath = "/RecordCamera/Thumbnail/";
    public static String mLockThumbnailPath = "/RecordCamera/LockThumbnail/";
//	public static String mBackThumbnailPath = "/RecordCamera/BackThumbnail/";
//	public static String mBackVideo = "/RecordCamera/BackVideo/";

//	public static String getmBackVideo(){
//		return getmExtsdPath()+mBackVideo;
//	}
//	
//	public static String getmBackThumbnailPath(){
//		return getmExtsdPath()+mBackThumbnailPath;
//	}

    public static String getmLockThubnailPath() {
        return getmExtsdPath() + mLockThumbnailPath;
    }

    public static String getmVideoPath() {
        return getmExtsdPath() + mVideoPath;
    }

    public static String getmLockVideoPath() {
        return getmExtsdPath() + mLockVideoPath;
    }

    public static String getmPicturePath() {
        return getmExtsdPath() + mPicturePath;
    }

    public static String getmPictureFolderPath() {
        return getmExtsdPath() + mPictureFolderPath;
    }

    public static String getmThumbnailPath() {
        //Log.d(TAG, "getmThumbnailPath: "+getmExtsdPath());
        return getmExtsdPath() + mThumbnailPath;
    }


    public static String getmExtsdPath() {
        return mExtsdPath;
    }

    public static Uri getmExtsdPathTreeUri() {
        return mExtsdPathTreeUri;
    }

    public static void setmExtsdPath(String mExtsdPath) {

        FileUtils.mExtsdPath = mExtsdPath;
    }

    public static void setmExtsdPathTreeUri(Uri mExtsdPathTreeUri) {
        FileUtils.mExtsdPathTreeUri = mExtsdPathTreeUri;
    }


    public static ArrayList getAllFile(String thum) {
        //缩略图文件列表
        Log.d(TAG, "getAllFilepath: " + getmThumbnailPath());
        ArrayList list = new ArrayList();//
        // String VedioPath = "/storage/emulated/0/RecordCamera/Thumbnail/";
        File file = new File(getmThumbnailPath());
        //Log.d(TAG, "getAllFile file: "+file);
        if (!file.exists()) {
            Log.d(TAG, "getAllFile: ThumbnailPath 路径不存在");
            file.mkdirs();
        }
        File[] files = file.listFiles();//获取路径下的所有文件
        Log.d(TAG, "get Thumbnail File list: " + files.toString());
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (files[i].length() != 0) {
                if (thum.equals("front")) {
                    if (files[i].getName().startsWith("f")) {

                        list.add(getmThumbnailPath() + files[i].getName().toString());
                    }
                } else if (thum.equals("back")) {
                    if (files[i].getName().startsWith("b")) {

                        list.add(getmThumbnailPath() + files[i].getName().toString());
                    }
                }

            } else {
                files[i].delete();
            }

        }
        return list;
    }


    public static ArrayList getAllLockFile(String thum) {
        ArrayList list = new ArrayList();
        // String VedioPath = "/mnt/extsd/RecordCamera/Thumbnail/";
        Log.d(TAG, "getAllLockFile path: " + getmLockThubnailPath());
        File file = new File(getmLockThubnailPath());
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (files[i].length() != 0) {
                if (thum.equals("front")) {
                    if (files[i].getName().startsWith("f")) {

                        list.add(getmLockThubnailPath() + files[i].getName().toString());
                    }
                } else if (thum.equals("back")) {
                    if (files[i].getName().startsWith("b")) {

                        list.add(getmLockThubnailPath() + files[i].getName().toString());
                    }
                }

            } else {
                files[i].delete();
            }

        }
        return list;
    }

    public static ArrayList getAllImageFile(String thum) {
        ArrayList list = new ArrayList();
        // String VedioPath = "/mnt/extsd/RecordCamera/Thumbnail/";
        File file = new File(getmPictureFolderPath());
        Log.d(TAG, "file: " + file);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        Log.d(TAG, "files: " + files);
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (files[i].length() != 0) {
                if (thum.equals("front")) {
                    if (files[i].getName().startsWith("f")) {

                        list.add(getmPictureFolderPath() + files[i].getName().toString());
                    }
                } else if (thum.equals("back")) {
                    if (files[i].getName().startsWith("b")) {

                        list.add(getmPictureFolderPath() + files[i].getName().toString());
                    }
                }

            } else {
                files[i].delete();
            }

        }
        return list;
    }

    public static ArrayList getAllPicFile() {
        ArrayList list = new ArrayList();
        // String VedioPath = "/mnt/extsd/RecordCamera/Thumbnail/";
        File file = new File(getmPictureFolderPath());
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            list.add(getmPictureFolderPath() + files[i].getName().toString());
        }
        return list;
    }

    static class compratorByLastModified implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            // TODO Auto-generated method stub
            long diff = lhs.lastModified() - rhs.lastModified();
            if (diff > 0) {
                return 1;
            } else if (diff == 0) {
                return 0;
            } else {

                return -1;
            }
        }

    }

    /**
     * ��ȡ֡����ͼ�����������ĸ߿��������
     *
     * @param filePath
     * @return
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {

            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        if (bitmap == null)
            return null;
        // Scale down the bitmap if it's too large.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // int pWidth = getWidth();// �������
        // int pHeight = getHeight();// �����߶�

        int pWidth = 1024;// �������
        int pHeight = 600;// �����߶�
        // ��ȡ��߸����������Ƚ�С�ı������Դ�Ϊ��׼��������
        float scale = Math
                .min((float) width / pWidth, (float) height / pHeight);
        int w = Math.round(scale * pWidth);
        int h = Math.round(scale * pHeight);
        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        return bitmap;
    }

    public static String[] getAllVedioMonth(int select) {
        String[] string;
        String name = "";
        String nameflag = "";
        if (select == 0) {
            nameflag = getmThumbnailPath();
        } else if (select == 1) {
            nameflag = getmLockThubnailPath();
        } else if (select == 2) {
            nameflag = getmPictureFolderPath();
        }

        ArrayList list = new ArrayList();
        // String VedioPath = "/mnt/extsd/RecordCamera/Thumbnail/";
        File file = new File(nameflag);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        // int[] inittime = inittime();
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (!name.equals(files[i].getName().substring(4, 6))) {
                name = files[i].getName().substring(4, 6);
                list.add(name);
            }
        }
        string = new String[list.size()];
        for (int k = 0; k < list.size(); k++) {
            string[k] = (String) list.get(k);
        }
        return string;
    }

    public static ArrayList getAllMonthFile(int select, String month) {

//		if(Integer.valueOf(month)<10){
//			month = "0"+month;
//		}


        ArrayList list = new ArrayList();
        // String VedioPath = "/mnt/extsd/RecordCamera/Thumbnail/";
        String nameflag = "";
        if (select == 0) {
            nameflag = getmThumbnailPath();
        } else if (select == 1) {
            nameflag = getmLockThubnailPath();
        } else if (select == 2) {
            nameflag = getmPictureFolderPath();
        }
        File file = new File(nameflag);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().substring(4, 6).equals(month)) {
                list.add(nameflag + files[i].getName().toString());
            }
        }
        return list;
    }

    public static ArrayList getAllDayFile(int select, String month, String day) {
//		if(Integer.valueOf(month)<10){
//			month = "0"+month;
//		}
//		if(Integer.valueOf(day)<10){
//			day = "0"+day;
//		}

        ArrayList list = new ArrayList();
        String nameflag = "";
        if (select == 0) {
            nameflag = getmThumbnailPath();
        } else if (select == 1) {
            nameflag = getmLockThubnailPath();
        } else if (select == 2) {
            nameflag = getmPictureFolderPath();
        }
        // String VedioPath = "/mnt/extsd/RecordCamera/Thumbnail/";
        File file = new File(nameflag);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().substring(4, 8).equals(month + day)) {
                list.add(nameflag + files[i].getName().toString());
            }
        }
        return list;
    }

    public static String[] getAllVedioDay(int select, String month) {

//		if(Integer.valueOf(month)<10){
//			month = "0"+month;
//		}

        String[] string;
        String name = "";
        String nameflag = "";
        ArrayList list = new ArrayList();
        // String VedioPath = "/mnt/extsd/RecordCamera/Thumbnail/";


        if (select == 0) {
            nameflag = getmThumbnailPath();
        } else if (select == 1) {
            nameflag = getmLockThubnailPath();
        } else if (select == 2) {
            nameflag = getmPictureFolderPath();
        }

        File file = new File(nameflag);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        // int[] inittime = inittime();
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().substring(4, 6).equals(month)) {
                if (!name.equals(files[i].getName().substring(6, 8))) {
                    name = files[i].getName().substring(6, 8);
                    list.add(name);
                }
            }
        }
        string = new String[list.size()];
        for (int k = 0; k < list.size(); k++) {
            string[k] = (String) list.get(k);
        }
        return string;
    }

    public static String[] getAllVedioTime(int select, String month, String day) {
//		if(Integer.valueOf(month)<10){
//			month = "0"+month;
//		}
//		if(Integer.valueOf(day)<10){
//			day = "0"+day;
//		}


        String[] string;
        String name = "";
        String nameflag = "";
        ArrayList list = new ArrayList();
        // String VedioPath = "/mnt/extsd/RecordCamera/Thumbnail/";
        if (select == 0) {
            nameflag = getmThumbnailPath();
        } else if (select == 1) {
            nameflag = getmLockThubnailPath();
        } else if (select == 2) {
            nameflag = getmPictureFolderPath();
        }
        File file = new File(nameflag);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        // int[] inittime = inittime();
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().substring(4, 8).equals(month + day)) {
                if (!name.equals(files[i].getName().substring(8, 10))) {
                    name = files[i].getName().substring(8, 10);
                    list.add(name);
                }
            }
        }
        string = new String[list.size()];
        for (int k = 0; k < list.size(); k++) {
            string[k] = (String) list.get(k);
        }
        return string;
    }

    public static ArrayList getAllTimeFile(int select, String month, String day, String time) {
//		if(Integer.valueOf(month)<10){
//			month = "0"+month;
//		}
//		if(Integer.valueOf(day)<10){
//			day = "0"+day;
//		}
//		if(Integer.valueOf(time)<10){
//			time = "0"+time;
//		}


        String string = "";
        if (select == 0) {
            string = getmThumbnailPath();
        } else if (select == 1) {
            string = getmLockThubnailPath();
        } else if (select == 2) {
            string = getmPictureFolderPath();
        }

        ArrayList list = new ArrayList();
        File file = new File(string);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().substring(4, 10).equals(month + day + time)) {
                list.add(string + files[i].getName().toString());
            }
        }
        return list;
    }


    public static List<String> getFilesAllName(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e("error", "空目录");
            return null;
        }
        List<String> s = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            s.add(files[i].getAbsolutePath());
        }
        return s;
    }

    public static ArrayList getFilesAllName(File file) {
        ArrayList list = new ArrayList();//是一个可变大小的数组 可动态add
        File[] files = file.listFiles();//
        //Log.d(TAG, "getFilesAllName length: "+files.length);
        if (files != null) {
            if (files[0].isDirectory()) {
                //Log.d(TAG, "getFilesAllName: 列表是目录");
            } else {
                //Log.d(TAG, "getFilesAllName: 列表是文件 进行排序");
                Arrays.sort(files, new compratorByLastModified());
            }
            for (File fs : files) {
                list.add(fs.getPath());
                //Log.d(TAG, "fileList: "+fs.getPath());
               // getFilesAllName(fs);//只取同层目录 不需要地柜
            }
        }
        return list;
    }

}
