package com.nbpt.video.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.nbpt.video.userfragment.PhotoFragment;
import com.nbpt.video.userfragment.VideoFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class FileUtils {
    public static String TAG = "FileUtils";
    public static String httprooturl = "http://192.168.43.1:8000/storage/emulated/0";
    public static String httpurl = "http://192.168.43.1:8000";

    public static Uri mExtsdPathTreeUri;
    public static String mExtsdPath = "";
    public static String mVideoPath = "/RecordCamera/CameraVideo/";
    public static String mVideoPathDownload = "/RecordCamera/CameraVideo/DownLoad/";
    public static String mLockVideoPath = "/RecordCamera/CameraLockVideo/";
    public static String mPicturePath = "/RecordCamera/CameraImage/";
    public static String mPicturePathDownload = "/RecordCamera/CameraImage/DownLoad/";
    public static String mPictureFolderPath = "/RecordCamera/Folder/";
    public static String mThumbnailPath = "/RecordCamera/Thumbnail/";
    public static String mLockThumbnailPath = "/RecordCamera/LockThumbnail/";
//	public static String mBackThumbnailPath = "/RecordCamera/BackThumbnail/";
//	public static String mBackVideo = "/RecordCamera/BackVideo/";



    public static String mHttpResult = "";

//    public static ArrayList mImageList = new ArrayList();
//    public static ArrayList mVideoList = new ArrayList();
//    public static ArrayList mVideoListPlay = new ArrayList();

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
    public static ArrayList getDownLoadImageFile(String thum) {
        //缩略图文件列表
        String videoPath=Environment.getExternalStorageDirectory().getAbsolutePath()+mPicturePathDownload;
        Log.d(TAG, "getDownLoadImageFile: " + videoPath);
        ArrayList list = new ArrayList();//
        // String VedioPath = "/storage/emulated/0/RecordCamera/Thumbnail/";
        File file = new File(videoPath);
        //Log.d(TAG, "getAllFile file: "+file);
        if (!file.exists()) {
            Log.d(TAG, "getAllFile: 路径不存在");
            file.mkdirs();
        }
        File[] files = file.listFiles();//获取路径下的所有文件
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (files[i].length() != 0) {
                if (thum.equals("front")) {
                    if (files[i].getName().startsWith("f")) {

                        list.add(videoPath + files[i].getName().toString());
                    }
                } else if (thum.equals("back")) {
                    if (files[i].getName().startsWith("b")) {

                        list.add(videoPath + files[i].getName().toString());
                    }
                }

            } else {
                files[i].delete();
            }

        }
        return list;
    }


    public static ArrayList getDownLoadVideoFile(String thum) {
        //缩略图文件列表
        String videoPath=Environment.getExternalStorageDirectory().getAbsolutePath()+mVideoPathDownload;
        Log.d(TAG, "getDownLoadVideoFile: " + videoPath);
        ArrayList list = new ArrayList();//
        // String VedioPath = "/storage/emulated/0/RecordCamera/Thumbnail/";
        File file = new File(videoPath);
        //Log.d(TAG, "getAllFile file: "+file);
        if (!file.exists()) {
            Log.d(TAG, "getAllFile: ThumbnailPath 路径不存在");
            file.mkdirs();
        }
        File[] files = file.listFiles();//获取路径下的所有文件
        Arrays.sort(files, new compratorByLastModified());
        for (int i = 0; i < files.length; i++) {
            if (files[i].length() != 0) {
                if (thum.equals("front")) {
                    if (files[i].getName().startsWith("f")) {

                        list.add(videoPath + files[i].getName().toString());
                    }
                } else if (thum.equals("back")) {
                    if (files[i].getName().startsWith("b")) {

                        list.add(videoPath + files[i].getName().toString());
                    }
                }

            } else {
                files[i].delete();
            }

        }
        return list;
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

    public static ArrayList getAllHttpImageFile(String thum) {
        final ArrayList mImageList1 = new ArrayList();
        //开启线程，发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                //解析xml
                try {
                    Document document = Jsoup.connect(httprooturl + mPicturePath).get();
                    Elements links = document.select("a[href]"); //带有href属性的a元素
                    for (Element li : links) {
                        String liHref = li.attr("href");
                        String liText = li.text();
                        //Log.d(TAG, "run: " + liHref + "=======" + liText);
                        mImageList1.add(httpurl + liHref);
                    }

//            Elements lis = document.getElementsByTag("li");
//            for (Element li : lis) {
//
//                Log.d(TAG, "run li: " + li.text());
//                //Element element=li.getElementsByTag("a");
//                String liHref = li.attr("href");
//                String liText = li.text();
//                Log.d(TAG, "run: " + liHref + "--------" + liText);
//            }
                    Log.d(TAG, "run: mImageList:" + mImageList1.size() + "file:" + mImageList1.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return mImageList1;
    }
    public static ArrayList getAllHttpVideoFile(String thum) {
        //final ArrayList list = new ArrayList();
        // String VedioPath = "/mnt/extsd/RecordCamera/Thumbnail/";
//		httpsend(httprooturl+mPicturePath);//请求图片文件
//		String ret=mHttpResult;
//		Log.d(TAG, "getAllHttpImageFile: "+ret);

        final ArrayList mVideoList1 = new ArrayList();
        //开启线程，发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                //解析xml
                try {
                    Document document = Jsoup.connect(httprooturl + mThumbnailPath).get();
                    Elements links = document.select("a[href]"); //带有href属性的a元素
                    for (Element li : links) {
                        String liHref = li.attr("href");
                        String liText = li.text();
                        //Log.d(TAG, "run: " + liHref + "=======" + liText);
                        mVideoList1.add(httpurl + liHref);
                    }
                    Log.d(TAG, "run: mVideoList:" + mVideoList1.size() + "file:" + mVideoList1.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return mVideoList1;
    }

    public static ArrayList getAllHttpVideoFilePlay(String thum) {
        final ArrayList mVideoListPlay1 = new ArrayList();
        //开启线程，发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                //解析xml
                try {
                    Document document = Jsoup.connect(httprooturl + mVideoPath).get();
                    Elements links = document.select("a[href]"); //带有href属性的a元素
                    for (Element li : links) {
                        String liHref = li.attr("href");
                        String liText = li.text();
                        //Log.d(TAG, "run: " + liHref + "=======" + liText);
                        mVideoListPlay1.add(httpurl + liHref);
                    }
                    Log.d(TAG, "run: mVideoListPlay:" + mVideoListPlay1.size() + "file:" + mVideoListPlay1.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return mVideoListPlay1;
    }
    public static void httpsend(final String requesturl, final Handler handler) {
        //开启线程，发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                //BufferedReader reader = null;
                File filedownloadpath = null;
                int type=0;
                try {
                    //Log.d(TAG, "run: path"+httprooturl+mPicturePath);
                    Log.d(TAG, "run: path:" + requesturl);
                    URL url = new URL(requesturl);
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("GET");
                    //设置连接超时时间（毫秒）
                    connection.setConnectTimeout(5000);
                    //设置读取超时时间（毫秒）
                    connection.setReadTimeout(5000);
                    // 设置是否使用缓存  默认是true
                    connection.setUseCaches(true);
                    // 开始连接
                    connection.connect();
                    // 判断请求是否成功
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        // 获取返回的数据
                        //获取请求的内容总长度
                        int contentLength = connection.getContentLength();//设置总进度条
                        Log.d(TAG,"文件大小为："+contentLength+"btye");
                        //得到链接地址中的file路径
                        //String urlFilePath = connection.getURL().getFile();
                        //String urlFile = connection.getURL().getFile();
                        //Log.d(TAG, "run url: "+connection.getURL());
                        String fileName=new File(connection.getURL().toString()).getName();
                        Log.d(TAG, "fileName: "+fileName);
                        String SdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
                        //Log.d(TAG, "SdPath: " + SdPath);
                        //======================================================
                        //判断是图片还是视频
                        if(fileName.endsWith(".jpg")) {
                            filedownloadpath = new File(SdPath + mPicturePathDownload);
                            PhotoFragment.download_progress.setMax(contentLength);
                            type=1;
                        }else  if(fileName.endsWith(".mp4")){
                            filedownloadpath = new File(SdPath + mVideoPathDownload);
                            VideoFragment.download_progress.setMax(contentLength);


                            type=2;
                        }
                        Log.d(TAG, "run filedownloadpath: "+filedownloadpath.getPath());
                        if (!filedownloadpath.exists()) {
                            Log.d(TAG, "run: 路径不存在,创建路径");
                            filedownloadpath.mkdir();
                        }
                        //========================================================================
                        //创建一个文件输出流
                        FileOutputStream outputStream = new FileOutputStream(filedownloadpath+"/"+fileName);
                //得到服务器响应的输入流
                        InputStream inputStream = connection.getInputStream();
                        //创建缓冲输入流对象，相对于inputStream效率要高一些
                        BufferedInputStream bfi = new BufferedInputStream(inputStream);
                        //BufferedReader bfi = new BufferedReader(new InputStreamReader(inputStream));
                        //此处的len表示每次循环读取的内容长度
                        int len;
                        //已经读取的总长度
                        int totle = 0;
                        //bytes是用于存储每次读取出来的内容
                        //byte[] bytes = new byte[1024];

                        byte[]   bytes = new byte[contentLength];

                        //while ((len = inputStream.read(bytes)) != -1)
                        while ((len = bfi.read(bytes)) != -1)
                        {
                            //每次读取完了都将len累加在totle里
                            totle += len;
                            //每次读取的都更新一次progressBar
                            if(type==1) {
                                PhotoFragment.download_progress.setProgress(totle);
                            }else  if(type==2) {
                                VideoFragment.download_progress.setProgress(totle);
                                Message msg = handler.obtainMessage();//获取主线程上的Message
                                msg.what = 13; // 消息标识
                                msg.arg1 =totle ; // 消息内容存放
                                msg.arg2 =contentLength ; // 消息内容存放
                                handler.sendMessage(msg);
                                //将进度值作为消息的参数进行封装，使其进度自加一
                            }
                            //更是进度条
                            //通过文件输出流写入从服务器中读取的数据
                            outputStream.write(bytes, 0, len);
                        }
                        //关闭打开的流对象
                        outputStream.close();
                        inputStream.close();
                        bfi.close();
                        Log.d(TAG, "run: 下载完成");
                        handler.sendEmptyMessage(12);

                        //创建一个文件对象用于存储下载的文件 此次的getFilesDir()方法只有在继承至Context类的类中

                        // 可以直接调用其他类中必须通过Context对象才能调用，得到的是内部存储中此应用包名下的文件路径

                        //如果使用外部存储的话需要添加文件读写权限，5.0以上的系统需要动态获取权限 此处不在不做过多说明。

                        //Log.e(TAG, "Get方式请求成功，result--->" +result);
                    } else {
                        Log.e(TAG, "Get方式请求失败");
                    }

//                    //读取输入流
//                    reader = new BufferedReader(new InputStreamReader(in));
//                    StringBuilder result = new StringBuilder();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        result.append(line);
//                    }
//                    mHttpResult = result.toString();
//                    Log.d(TAG, "run ret: " + mHttpResult);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                    if (reader != null) {
//                        try {
//                            reader.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    if (connection != null) {//关闭连接
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    public static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public static boolean HttpDownload(String path, Handler handler){
        httpsend(path,handler);
        return false;
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

//    public static String getCachedDirs(App app) {
//        String path= "/storage/emulated/0/RecordCamera/";
//        File file = new File(path);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//
//        return path;
//    }

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

}
