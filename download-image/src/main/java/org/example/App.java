package org.example;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.example.util.JsoupUtils;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        Integer pageNumber = 1;
        String userName = "272688";
        String password = "123456";
        String keyword = "dog";
        String url = "https://image.sogou.com/pics?mode=1&start=48&reqType=ajax&reqFrom=result&tn=0&query=";
        //Map<String, String> cookies = new UrspRepositoryLoginServiceImpl().loginChonQing(userName, password);

        Document doc = JsoupUtils.getJsoupDocGet(url + keyword);
        String json = doc.body().text();
        JSONObject jsonObject = JSON.parseObject(json);
        String string = jsonObject.getString("items");
        JSONArray jsonArray = JSON.parseArray(string);
        for (Object o : jsonArray) {
            JSONObject temp =(JSONObject) o;
            System.out.println(temp.get("pic_url"));
            String dir = "D:/image";
            String word = "/dog";
            String name =  IdUtil.fastSimpleUUID()+".jpg";
            App.downloadFileByUrl(temp.getString("pic_url"),name, dir + word);
        }
        System.out.println(json);
    }

    public static void download(String urlString, String filename) throws Exception {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        // 输入流
        InputStream is = con.getInputStream();
        // 1K的数据缓冲
        byte[] bs = new byte[1024 * 1024 * 2];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        OutputStream os = new FileOutputStream(filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }

    /**
     * 根据网络URL下载文件
     * @param url
     *            文件所在地址
     * @param fileName
     *            指定下载后该文件的名字
     * @param savePath
     *            文件保存根路径
     */
    public static void downloadFileByUrl(String url, String fileName, String savePath) {
        URL urlObj = null;
        URLConnection conn = null;
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        OutputStream outputStream = null;
        BufferedOutputStream bos = null;
        try {
            // 1.建立网络连接
            urlObj = new URL(url);
            // 2.打开网络连接
            conn = urlObj.openConnection();
            // 设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            // 3.得到输入流
            inputStream = conn.getInputStream();
            bis = new BufferedInputStream(inputStream);

            // 文件保存位置
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            // 文件的绝对路径
            String filePath = savePath + File.separator + fileName;
            File file = new File(filePath);
            // 4.
            outputStream = new FileOutputStream(file);
            bos = new BufferedOutputStream(outputStream);
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            System.out.println("info:" + url + " download success,fileRename=" + fileName);
        } catch (MalformedURLException e) {
            System.out.println("世界上最遥远的距离就是没有网，检查设置");
            System.out.println("info:" + url + " download failure");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("您的网络连接打开失败，请稍后重试！");
            System.out.println("info:" + url + " download failure");
            e.printStackTrace();
        } finally {// 关闭流
            try {
                if (bis != null) {// 关闭字节缓冲输入流
                    bis.close();
                }

                if (inputStream != null) {// 关闭字节输入流
                    inputStream.close();
                }
                if (bos != null) {// 关闭字节缓冲输出流
                    bos.close();
                }
                if (outputStream != null) {// 关闭字节输出流
                    outputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
