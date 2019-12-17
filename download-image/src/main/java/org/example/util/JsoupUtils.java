package org.example.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 *  jsoup工具类
 * @author 19102
 *
 */
public class JsoupUtils {

	/**
	 * GET请求
	 * 
	 */
	public static Document getJsoupDocGet(String url) {

		//三次试错
		final int max = 3;
		int time = 0;
		Document doc = null;
		while (time < max) {
			try {
				doc = Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).timeout(1000 * 30).userAgent(
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
						.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
						.header("accept-encoding", "gzip, deflate, br").header("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7").get();
				return doc;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				time++;
			}
		}
		return doc;
	}

	/**
	 * GET请求 ，带cookie
	 *
	 */
	public static Document getJsoupDocGet(String url, Map<String, String> cookieMap) {

		//三次试错
		final int max = 3;
		int time = 0;
		Document doc = null;
		while (time < max) {
			try {
				doc = Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).timeout(1000 * 30).userAgent(
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
						.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
						.header("accept-encoding", "gzip, deflate, br").header("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
						.cookies(cookieMap).get();
				return doc;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				time++;
			}
		}
		return doc;
	}

	/**
	 * POST请求
	 */
	public static Document getJsoupDocPost(String url) {
		//三次试错
		final int max = 3;
		int time = 0;
		Document doc = null;
		while (time < max) {
			try {
				doc = Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).timeout(1000 * 15).userAgent(
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
						.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
						.header("accept-encoding", "gzip, deflate, br").header("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7").post();
				return doc;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				time++;
			}
		}
		return doc;
	}

	/**
	 * POST请求
	 */
	public static Document getJsoupDocPost(String url, Map<String, String> cookieMap) {
		//三次试错
		final int max = 3;
		int time = 0;
		Document doc = null;
		while (time < max) {
			try {
				doc = Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).timeout(1000 * 5).userAgent(
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
						.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
						.header("accept-encoding", "gzip, deflate, br").header("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
						.cookies(cookieMap).post();
				return doc;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				time++;
			}
		}
		return doc;
	}

	/**
	 * POST请求，带cookie
	 * 
	 */
	public static Document getJsoupDocPost(String url, Map<String, String> paramMap, Map<String, String> cookieMap) {
		//三次试错
		final int max = 3;
		int time = 0;
		Document doc = null;
		while (time < max) {
			try {
				doc = Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).timeout(1000 * 30).userAgent(
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
						.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
						.header("accept-encoding", "gzip, deflate, br").header("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
						.data(paramMap).cookies(cookieMap).post();

				return doc;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				time++;
			}
		}
		return doc;
	}
}
