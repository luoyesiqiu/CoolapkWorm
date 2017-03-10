package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {

	//浏览器UA
	private static String UA="Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
	//主机地址
	private static final String HOST="http://coolapk.com";
	//头像本地保存地址
	private static final String SAVE_PAYH="D:/coolapk/";
	//指示UserThread是否在运行
	private static boolean isRun=false;
	//用户中心界面队列
	private  static MyQueue<String> userUrlQueue=new MyQueue<>();
	//用户头像链接队列
	private static MyQueue<String> userHeadUrlQueue=new MyQueue<>();
	//用户名队列
	private static MyQueue<String> userNameQueue=new MyQueue<>();
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		userUrlQueue.put("http://coolapk.com/u/408649/contacts");//入口链接
		java.io.File f=new java.io.File(SAVE_PAYH);
		//如果文件夹不存在，则创建
		if(!f.exists())
		{
			f.mkdirs();
		}
		start();
	}

	/**
	 * 开始
	 */
	private static void start()
	{
		new UserThread().start();
		new HeadThread().start();
	}
	
	/**
	 * 获取相关的链接
	 * @throws Exception
	 */
	private static void getUserUrl() throws Exception {
		String url=userUrlQueue.poll();
		if(url!=null){
			isRun=true;
			Connection connection=Jsoup.connect(url);
			connection.userAgent(UA);
			Document document=connection.get();
			
			Element ulElement=document.getElementById("dataList");
			org.jsoup.select.Elements liElements=ulElement.getElementsByTag("li");
			if (liElements==null) {
				return;
			}
			for(Element li:liElements){
				if(li==null)
					continue;
				//获取用户头像链接
				String userHeadUrl=li.getElementsByTag("img").first().attr("src");
				//获取一个用户的粉丝列表的url
				String userUrl=HOST+li.getElementsByTag("h4").first()
						.getElementsByTag("a").first()
						.attr("href")+"/contacts";
				//获取一个用户的用户名
				String userName=li.getElementsByTag("h4").first()
						.getElementsByTag("a").first().text();
				//本地已保存就不再加入队列
				if(!new File(SAVE_PAYH+userName+".jpg").exists()){
					userUrlQueue.put(userUrl);
					userHeadUrlQueue.put(userHeadUrl);
					userNameQueue.put(userName);
					System.out.println(userUrl);
				}
			}
			//队列空了，isRun=false
			isRun=false;
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
	/**
	 * 获取链接线程
	 * @author zyw
	 *
	 */
	public static class UserThread  extends Thread{
	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//如果队列userUrlQueue不为空
			while (!userUrlQueue.isEmpty()) {
				try {
					getUserUrl();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	/**
	 * 获取头像线程
	 * @author zyw
	 *
	 */
	public static class HeadThread  extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//如果队列userHeadUrlQueue不为空，或者userUrlQueue不为空
			while (!userHeadUrlQueue.isEmpty()||isRun) {
				try {
					String imgUrl=userHeadUrlQueue.poll();
					imgUrl=imgUrl.replaceAll("middle", "big");
					String userName=userNameQueue.poll();
					if(imgUrl==null||userName==null)
						continue;
					getImage(imgUrl, SAVE_PAYH+userName+".jpg");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
}
