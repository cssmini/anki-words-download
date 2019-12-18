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
import java.util.ArrayList;

import java.io.*;
import java.util.*;
import org.dom4j.*;
import org.dom4j.io.*;

/**
 * download images
 *
 */
public class newApp {

    /**
     * 音频地址
     */
    private final static String AUDIO_DOWNLOAD_URI = "http://media.shanbay.com/audio/us/";
    /**
     * 图片下载地址
     */
    private final static String IMAGE_DOWNLOAD_URI = "https://image.sogou.com/pics?mode=1&start=48&reqType=ajax&reqFrom=result&tn=0&query=";
    /**
     * 图片文件保存路径
     */
    private final static  String IMAGE_DIR_PATH = "D:/image";
    /**
     * 音频文件保存路径
     */
    private final static  String AUDIO_DIR_PATH = "";

    /**
     * 有道 xml 文件路径
     */
    private final static String YOU_DAO_XML_FILE = "D:/temp/有道单词.xml";

    /**
     * 导出的文件，保存路径
     */
    private final static String FILE_OUT_PATH = "D:/temp/outputXML.txt";

    /**
     * 文件名前缀
     */
    private final static String FILE_PREFIX = "temp-";
    /**
     * 分隔符
     */
    private final static String SEPARATOR = "|";

    public static void main(String[] args) {
         // 单词
        String keyword = "dog";
        // 文件名称
        String fileName = newApp.FILE_PREFIX + keyword;


        //读取从有道导出的 xml 格式文件
        List<Map<String, String>> listMap = newApp.YouDoXmlFile2Map(YOU_DAO_XML_FILE);
        newApp.exportTxtPaper(listMap);

        // 下载图片
        //downloadImageFile(keyword);

        // 下载音频
        //downloadAduioFile(keyword);
        System.out.println("");
    }

    public static void  exportTxtPaper(List<Map<String, String>> listMap) {

        Writer out;
        StringBuilder sb = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newApp.FILE_OUT_PATH,true), "utf-8"), 10240);
            for (Map<String, String> map : listMap) {
                // 格式：单词|音标|释义|发音|例句|例句翻译|图片
                sb = new StringBuilder();
                sb.append(map.get("word")).append(SEPARATOR);
                sb.append(map.get("phonetic")).append(SEPARATOR);
                sb.append(map.get("trans")).append(SEPARATOR);
                sb.append(map.get("audioTAG")).append(SEPARATOR);
                // 例句 map.get("example")
                sb.append("  ").append(SEPARATOR);
                // 例句翻译 map.get("exampleTrans")
                sb.append("  ").append(SEPARATOR);
                sb.append(map.get("imageTAG")).append(SEPARATOR);
                sb.append("\r\n");
                /*
                out.write();
                out.write("\r\n");*/
            }
            out.write(sb.toString());
            out.flush();
            out.close();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /**
     *   格式：
     *   <item>
     *      <word>is</word>
     *      <trans><![CDATA[v. 是（be的三单形式）n. 存在]]></trans>
     *      <phonetic><![CDATA[[ɪz]]]></phonetic>
     *      <tags>基础单词</tags>
     *      <progress>3</progress>
     *   </item>
     *
     * @description 将 有道导出的xml 转换成map
     * @param xmlPath
     * @return Map
     */
    public static List<Map<String, String>> YouDoXmlFile2Map(String xmlPath) {
        List<Map<String, String>> list = new ArrayList<>(16);
        Map<String, String> map = new HashMap<String, String>(16);
        org.dom4j.Document doc = null;
        try {
            SAXReader reader = new SAXReader();
            doc = reader.read(new FileInputStream(xmlPath));
            Element rootElt = doc.getRootElement();
            System.out.println("根节点：" + rootElt.getName());

            // 遍历节点
            Iterator iter = rootElt.elementIterator("item");
            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                String word = recordEle.elementTextTrim("word");
                String trans = recordEle.elementTextTrim("trans");
                String phonetic = recordEle.elementTextTrim("phonetic");
                String tags = recordEle.elementTextTrim("tags");
                String progress = recordEle.elementTextTrim("progress");
                // 音频标签
                String audioTAG = "[sound:"+word+".mp3]";
                // 图片标签
                String imageTAG = "<img src=\""+word+".jpg\">";
                map.put("word", word);
                map.put("trans", trans);
                map.put("phonetic", phonetic);
                map.put("tags", tags);
                map.put("progress", progress);
                map.put("audioTAG", audioTAG);
                map.put("imageTAG", imageTAG);
                list.add(map);

                System.out.println("word:" + word);
                System.out.println("trans:" + trans);
                System.out.println("phonetic:" + phonetic);
                System.out.println("tags:" + tags);
                System.out.println("progress:" + progress);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void downloadImageFile(String keyword){
        // file path
        String path = newApp.IMAGE_DIR_PATH +"/"+ keyword;
        Document doc = JsoupUtils.getJsoupDocGet(newApp.IMAGE_DOWNLOAD_URI + keyword);
        String json = doc.body().text();
        JSONObject jsonObject = JSON.parseObject(json);
        String string = jsonObject.getString("items");
        JSONArray jsonArray = JSON.parseArray(string);
        for (Object o : jsonArray) {
            JSONObject temp =(JSONObject) o;
            System.out.println(temp.get("pic_url"));
            // 图片名称
            String name =  newApp.FILE_PREFIX + IdUtil.fastSimpleUUID()+".jpg";
            newApp.downloadFileByUrl(temp.getString("pic_url"),name, path);
        }
    }

    public static void downloadAduioFile(String keyword){
        // file path
        String path = newApp.AUDIO_DIR_PATH;
        // 音频名称
        String audioName = newApp.FILE_PREFIX + keyword + ".mp3";
        // 下载音频
        newApp.downloadFileByUrl(newApp.AUDIO_DOWNLOAD_URI + audioName,audioName,path);
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
