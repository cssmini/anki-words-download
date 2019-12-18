package org.example;

/**
 * @author: hxl
 * @create: 2019-12-18 16:42
 **/import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Description:
 * @ClassName: DownFile
 * @Project: base-info
 * @Author: zxf
 * @Date: 2011-7-13
 */
public class DownFile {

    /**
     * 下载
     *
     * @throws Exception
     * String realPath = "D:/image";
     *
     */
    public void down(String fileSavePath,String imageName, String urlPath, int threadnum) throws Exception {
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10 * 1000);
        conn.setRequestMethod("GET");
        // 获得网络文件的长度
        int length = conn.getContentLength();
        // 每个线程负责下载的文件大小
        int block = (length % threadnum) == 0 ? length / threadnum : length
                / threadnum + 1;
        //从http相应消息获取的状态码，200:OK;401:Unauthorized
        if (conn.getResponseCode() == 200) {
            for (int i = 0; i < threadnum; i++) {
                // 开启线程下载
                new DownThread(i, new File(realPath(fileSavePath),imageName), block, url).start();
            }
        }
    }

    /**
     * 文件的下载目录
     * @return
     */
    public String realPath(String realPath){

        File file = new File(realPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return realPath;
    }

    /**
     * 获取文件名
     * @param path
     * @return
     */
    public String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static void main(String[] args) {
        /*DownFile test = new DownFile();
        String path ="http://www.baidu.com/img/baidu_sylogo1.gif";
        int threadnum = 3;
        try {
            test.down(path, threadnum);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}