package com.nbpt.video.fileserver;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

public class HttpServer extends NanoHTTPD {
    private static final String TAG = "Http";

    public HttpServer(int port) {
        super(port);
    }
    @Override
    public Response serve(IHTTPSession session) {

        try {
//            for (int i = 0; i < DatabaseSelectUpload.name_.size(); i++) {  //for 循环文件名 小于name的个数
//                session.parseBody(new HashMap<String, String>());
//                final String choose = DatabaseSelectUpload.name_.get(i);//获取循环到的文件名
//                String strDBPath = MyApplication.GetApp().getExternalFilesDir(null) + "/TIS-Smarthome/" + choose + "/" + (choose + ".db3");//数据库地址
//

            String uri = session.getUri();
            //文件输入流
            FileInputStream fis = new FileInputStream(uri);

              //  FileInputStream fis = new FileInputStream(strDBPath);

                return Response.newFixedLengthResponse(Status.OK, "application/octet-stream", fis, fis.available());


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
