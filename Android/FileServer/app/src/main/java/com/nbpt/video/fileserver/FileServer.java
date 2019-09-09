package com.nbpt.video.fileserver;


import android.util.Log;

import com.nbpt.video.fileserver.util.FileUtils;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileServer extends NanoHTTPD {
    public static final String TAG = FileServer.class.getSimpleName();
    //public static final int DEFAULT_SERVER_PORT = 8000;//为8080
    //根目录
    private static final String REQUEST_ROOT = "/storage/emulated/0/RecordCamera/";///storage/emulated/0/RecordCamera/Thumbnail/
    //private List<String> fileList;//用于分享的文件列表
    //private ArrayList frontList;//存放文件列表
    private List frontList;//存放文件列表

    public FileServer(int port) {
        super(port);
        Log.d(TAG, "FileServer: 监听端口" + port);
        //frontList = FileUtils.getAllFile("front");//获取所有的文件列表
        //fileList=FileUtils.getFilesAllName(REQUEST_ROOT);
        //Log.d(TAG, "FileServer: "+fileList.size());

    }

    //当接受到连接时会调用此方法
    @Override
    public Response serve(IHTTPSession session) {
        String url = session.getUri();
        //Method method = session.getMethod();
        //可判断是post 还是get请求
        if (Method.POST.equals(session.getMethod())) {

        } else if (Method.GET.equals(session.getMethod())) {

        }
        //接收不到post参数的问题http://blog.csdn.net/obguy/article/details/53841559
        try {
            session.parseBody(new HashMap<String, String>());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
        Map<String, String> parms = session.getParms();

        /*获取header信息，NanoHttp的header不仅仅是HTTP的header，还包括其他信息。*/
        Map<String, String> header = session.getHeaders();
        /*这句尤为重要就是将将body的数据写入files中，大家可以看看parseBody具体实现，倒现在我也不明白为啥这样写。*/
        //session.parseBody(files);
        /*看就是这里，POST请教的body数据可以完整读出*/
        //String body = session.getQueryParameterString();
        /*这里是从header里面获取客户端的IP地址。NanoHttpd的header包含的东西不止是HTTP heaer的内容*/
        Log.d(TAG, "serve client ip: " + header.get("http-client-ip").toString());

        //Log.d(TAG, "serve: 请求方法"+method);
        Log.d(TAG, "serve: 请求参数" + session.getParameters());

        if(header!=null)
        {
            Log.d(TAG, "serve: header: "+header.toString());
        }
        if(parms!=null){
            Log.d(TAG, "serve: parms: "+parms.toString());
        }

        Log.d(TAG, "serve 请求链接: " + url);

        if (new File(url).isDirectory()) {
            Log.d(TAG, "serve: 请求目录");
            return responseRootPage(session);//根目录 返回文件列表
        } else if (new File(url).isFile()) {
            Log.d(TAG, "serve: 请求文件");
            return responseFile(session);
        } else {
            Log.d(TAG, "serve: 请求其他");
            return response404(session, null);
        }
//
//        if(REQUEST_ROOT.equals(session.getUri())||session.getUri().equals("")){
//            return responseRootPage(session);//根目录 返回文件列表
//        }
//        return responseFile(session);
    }

    //对于请求根目录的，返回分享的文件列表
    public Response responseRootPage(IHTTPSession session) {
        Log.d(TAG, "responseRootPage:请求根目录 " + session.getUri());
        String path = session.getUri();//请求的url
        //每次根据请求的路径进行获取路径下的文件列表
        frontList = FileUtils.getFilesAllName(new File(path));//根据请求的url 获取全部文件
        //返回一个html页面
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPER html><html><body>");
        builder.append("<ol>");
        Log.d(TAG, "responseRootPage size: " + frontList.size());
        for (int i = 0, len = frontList.size(); i < len; i++) {
            //frontList
            File file = new File(frontList.get(i).toString());
            //Log.d(TAG, "responseRootPage111: "+file.getAbsolutePath());
            //Log.d(TAG, "responseRootPage222: "+file.getPath());
            if (file.exists()) {
                //文件及下载文件的链接，定义了一个文件类，这里使用getPath方法获得路径，使用getName方法获得文件名
                builder.append("<li> <a href=\"" + file.getPath() + "\">" + file.getName() + "</a></li>");
            }
        }
        builder.append("<li>分享文件数量：  " + frontList.size() + "</li>");
        builder.append("</ol>");
        builder.append("</body></html>\n");
        //回送应答
        return Response.newFixedLengthResponse(String.valueOf(builder));

    }

    //对于请求文件的，返回下载的文件
    public Response responseFile(IHTTPSession session) {
        //不下载  直接预览，确认下载的时候 再下载 根据参数确认是否下载
        try {
            //uri：用于标示文件资源的字符串，这里即是文件路径
            String uri = session.getUri();
            String key;
            String value_s;
            Map<String, List<String>> parms = session.getParameters();
            Log.d(TAG, "responseFile:操作文件url " + uri);
            String[] split = uri.split("/");//分隔文件名称
            String filetype = split[5];//取出文件夹类别
            Log.d(TAG, "responseFile 路径长度: " + split.length + filetype);
            Log.d(TAG, "responseFile 路径PATH: " + (REQUEST_ROOT + filetype + "/" + split[6]));
            if (parms != null) {
                for (Map.Entry<String, List<String>> entry : parms.entrySet()) {
                    key = entry.getKey();
                    Log.d(TAG, "serve key : " + key);
                    List<String> value = entry.getValue();
                    for (Object obj : value) {
                        value_s = (String) obj;
                        Log.d(TAG, "serve value: " + value_s);
                        if (value_s.equals("download")) {
                            Log.d(TAG, "responseFile: 下载文件名:" + split[6]);
                            //FileInputStream fis = new FileInputStream(uri);
                            FileInputStream fis = new FileInputStream(REQUEST_ROOT + filetype + "/" + split[6]);
                            Response response = Response.newChunkedResponse(Status.OK, "application/octet-stream", fis);
                            response.addHeader("Content-Disposition", "attachment; filename=" + split[6]);
                            return response;

                        }
                    }
                }
            }
            FileInputStream fis = new FileInputStream(uri);
            // 返回OK，同时传送文件，为了安全这里应该再加一个处理，即判断这个文件是否是我们所分享的文件，避免客户端访问了其他个人文件
            if (filetype.equals("CameraVideo") || filetype.equals("CameraLockVideo")) {
                return Response.newFixedLengthResponse(Status.OK, "video/mp4", fis, fis.available());//视频预览

            } else if (filetype.equals("CameraImage") || filetype.equals("Thumbnail") || filetype.equals("Folder") || filetype.equals("LockThumbnail")) {
                return Response.newFixedLengthResponse(Status.OK, "image/jpeg", fis, fis.available());//图片预览
            } else {
                return Response.newFixedLengthResponse(Status.OK, "application/octet-stream", fis, fis.available());//下载
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response404(session, null);
    }

    //页面不存在，或者文件不存在时
    public Response response404(IHTTPSession session, String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html>body>");
        builder.append("Sorry,Can't Found" + url + " !");
        builder.append("</body></html>\n");
        return Response.newFixedLengthResponse(builder.toString());
    }


}
