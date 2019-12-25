package com.cssmini.web;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 首页 控制器
 *
 * @author: hxl
 * @create: 2019-11-17 15:21
 **/
@Controller
public class IndexController {

    @Value("${YOUDAO_URL}")
    private  String YOUDAO_URL;

    @Value("${APP_KEY}")
    private  String APP_KEY;

    @Value("${APP_SECRET}")
    private  String APP_SECRET;

    @Value("${AUDIO_DIR_PATH}")
    private   String AUDIO_DIR_PATH;

    @Value("${FILE_OUT_PATH}")
    private  String FILE_OUT_PATH;

    @Value("${READER_FILE}")
    private  String READER_FILE;

    @Value("${AUDIO_DOWNLOAD_URI}")
    private  String AUDIO_DOWNLOAD_URI;

    @Value("${outputAudio}")
    private  Boolean outputAudio;

    @Value("${outputImage}")
    private  Boolean outputImage;

    private  String FILE_PREFIX = "";

    private  String SEPARATOR = "\t";

    private  String TAG_BR = "<br/>";

    private  List<String> errorList = new ArrayList<>();

    @RequestMapping("/index")
    public String index(Locale locale, Model model) {

        return "index";
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public Object query(String text)throws IOException{
        List<Map<String, String>> listMap = new ArrayList<>();
        String[] arr = text.split("-");
        for (String word : arr) {
            Map<String, String> map = requestForHttp(YOUDAO_URL, initYouDaoParam(word));
            if (map.size() > 0){
                listMap.add(map);
                if (outputAudio){
                    downloadAduioFile(word);
                }
            }
        }
        exportTxtPaper(listMap);
        System.out.println("音频下载失败的单词：");
        for (String a : errorList) {
            System.out.println(a);
        }
        System.out.println("共 "+errorList.size()+ " 个单词下载失败");
        System.out.println("文件导出地址：" + FILE_OUT_PATH);
        return "ok";
    }

    public  void  exportTxtPaper(List<Map<String, String>> listMap) {
        Writer out;
        StringBuilder sb = new StringBuilder();
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_OUT_PATH,false), "utf-8"), 10240);
            for (Map<String, String> map : listMap) {
                // 格式：单词|音标|释义|发音|例句|例句翻译|图片
                sb.append(map.get("word")).append(SEPARATOR);
                sb.append(map.get("phonetic")).append(SEPARATOR);
                sb.append(map.get("trans")).append(SEPARATOR);
                if (outputAudio){
                    sb.append(map.get("audioTAG")).append(SEPARATOR);
                }else{
                    sb.append("  ").append(SEPARATOR);
                }
                if (outputImage){
                    sb.append(map.get("imageTAG")).append(SEPARATOR);
                }else {
                    sb.append("  ").append(SEPARATOR);
                }
                // 例句 map.get("example")
                sb.append("  ").append(SEPARATOR);
                // 例句翻译 map.get("exampleTrans")
                sb.append("  ").append(SEPARATOR);
                sb.append("\r\n");
            }
            out.write(sb.toString());
            out.flush();
            out.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public  void downloadAduioFile(String keyword){
        String path = AUDIO_DIR_PATH;
        String audioName = keyword + ".mp3";
        downloadFileByUrl(MessageFormat.format(AUDIO_DOWNLOAD_URI,keyword),audioName,path);
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
    public  void downloadFileByUrl(String url, String fileName, String savePath) {
        URL urlObj = null;
        URLConnection conn = null;
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        OutputStream outputStream = null;
        BufferedOutputStream bos = null;
        fileName = FILE_PREFIX + fileName;
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
            /*System.out.println("世界上最遥远的距离就是没有网，检查设置");
            System.out.println("info:" + url + " download failure");
            e.printStackTrace();*/
            errorList.add(url);
        } catch (IOException e) {
           /* System.out.println("您的网络连接打开失败，请稍后重试！");
            System.out.println("info:" + url + " download failure");
            e.printStackTrace();*/
            errorList.add(url);
        }catch (Exception e){
            /* e.printStackTrace();*/
            errorList.add(url);
        }finally {// 关闭流
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

    public  Map<String, String> initYouDaoParam(String word){
        Map<String,String> params = new HashMap<String,String>();
        String q = word;
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("from", "en");
        params.put("to", "zh-CHS");
        params.put("signType", "v3");
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("curtime", curtime);
        String signStr = APP_KEY + truncate(q) + salt + curtime + APP_SECRET;
        String sign = getDigest(signStr);
        params.put("appKey", APP_KEY);
        params.put("q", q);
        params.put("salt", salt);
        params.put("sign", sign);
        return params;
    }
    public  Map<String, String> requestForHttp(String url, Map<String, String> params) throws IOException {
        String word = params.get("q");
        Map<String, String> map = new HashMap<>(16);
        /** 创建HttpClient */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        /** httpPost */
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        Iterator<Map.Entry<String,String>> it = params.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,String> en = it.next();
            String key = en.getKey();
            String value = en.getValue();
            paramsList.add(new BasicNameValuePair(key,value));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try{
            Header[] contentType = httpResponse.getHeaders("Content-Type");
            System.out.println("Content-Type:" + contentType[0].getValue());
            if("audio/mp3".equals(contentType[0].getValue())){
                //如果响应是wav
                HttpEntity httpEntity = httpResponse.getEntity();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(baos);
                byte[] result = baos.toByteArray();
                EntityUtils.consume(httpEntity);
                if(result != null){//合成成功
                    String file = "合成的音频存储路径"+System.currentTimeMillis() + ".mp3";
                    byte2File(result,file);
                }
            }else{
                /** 响应不是音频流，直接显示结果 */
                HttpEntity httpEntity = httpResponse.getEntity();
                String json = EntityUtils.toString(httpEntity,"UTF-8");
                EntityUtils.consume(httpEntity);

                JSONObject jsonObject = JSONObject.parseObject(json);
                JSONObject basic = jsonObject.getJSONObject("basic");
                JSONArray explains = basic.getJSONArray("explains");
                String trans = "";
                for (Object explain : explains) {
                    String s = explain.toString();
                    trans += s + TAG_BR;
                }

                String phonetic = basic.getString("us-phonetic");
                //String tags = basic.getString("");
                //String progress = basic.getString("");
                String audioTAG = "[sound:"+word+".mp3]";
                String imageTAG = "<img src=\""+word+".jpg\">";
                map.put("word", word);
                map.put("trans",trans.substring(0,trans.lastIndexOf(TAG_BR)));
                map.put("phonetic", phonetic);
                map.put("tags", "");
                map.put("progress", "");
                map.put("audioTAG", audioTAG);
                map.put("imageTAG", imageTAG);
            }
        }finally {
            try{
                if(httpResponse!=null){
                    httpResponse.close();
                }
            }catch(IOException e){
                System.out.println("## release resouce error ##" + e);
            }
        }
        return map;
    }

    /**
     * 生成加密字段
     */
    public  String getDigest(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     *
     * @param result 音频字节流
     * @param file 存储路径
     */
    private  void byte2File(byte[] result, String file) {
        File audioFile = new File(file);
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(audioFile);
            fos.write(result);

        }catch (Exception e){
            System.out.println(e.toString());
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public  String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        String result;
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }
}
