package main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Worm {
	private static final String SAVE_PAYH="D:/coolapk1/";
	private final static String URL="http://www.coolapk.com/u/";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		java.io.File f=new java.io.File(SAVE_PAYH);
		//如果文件夹不存在，则创建
		if(!f.exists())
		{
			f.mkdirs();
		}
		getHeads();
	}
	/**
	 * 获取头像
	 * @throws IOException 
	 */
	private static void getHeads() throws IOException {
		for(int i=10001;i<870000;i++)
		{
			try{
			Connection connection=Jsoup.connect(URL+i);
			Document doc=connection.get();
			Elements elements=doc.select(".username");
			Element element=elements.first();
			String username=element.html();
			String html=doc.html();
			Pattern pattern=Pattern.compile("http://avatar.coolapk.com/data/([^\"]*)");
			Matcher matcher=pattern.matcher(html);
			if(matcher.find())
			{
				String imgUrl=matcher.group(0);
				imgUrl=imgUrl.replaceAll("middle", "big");
				getImage(imgUrl, SAVE_PAYH+i+"_"+username+".jpg");
			}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取图片并保存到本地
	 * @param imgUrl
	 * @param localPath
	 * @throws Exception
	 */
	private static void getImage(String imgUrl,String localPath) throws Exception {
		//System.out.println(imgUrl);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget= new HttpGet(imgUrl);
		CloseableHttpResponse resp=httpclient.execute(httpget);
		InputStream inputStream=resp.getEntity().getContent();
		FileOutputStream fileOutputStream=new FileOutputStream(localPath);
		byte[] buf=new byte[1024];
		int len=0;
		while ((len=inputStream.read(buf))!=-1) {
			fileOutputStream.write(buf, 0, len);
			fileOutputStream.flush();
		}
		inputStream.close();
		fileOutputStream.close();
		
	}
}
