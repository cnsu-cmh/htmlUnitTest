package com.xqx.htmlunit;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class HtmlunitTesPic {

	private static final String BASE_URL = "http://pic.sogou.com";
	private static final String KEY_WORD = "张学友";
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52);
		webClient.getOptions().setCssEnabled(Boolean.TRUE);
		webClient.getOptions().setJavaScriptEnabled(Boolean.TRUE);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(Boolean.FALSE);  
        webClient.setAjaxController(new NicelyResynchronizingAjaxController()); 
        webClient.waitForBackgroundJavaScript(600*1000);    
//        new SimpleConectionListener(webClient);
		try {
			HtmlPage page = webClient.getPage(BASE_URL);
			//System.out.println(page.asXml());
			
			HtmlForm searchForm = page.getFormByName("searchForm");
			HtmlTextInput keyWordInput = searchForm.getInputByName("query");
			keyWordInput.setValueAttribute(KEY_WORD);
			HtmlSubmitInput submitInput = (HtmlSubmitInput) searchForm.getByXPath("//div/input[@type=\"submit\"]").get(0);
			HtmlPage returnPage = submitInput.click();
			webClient.waitForBackgroundJavaScript(6*1000);    
			//System.out.println(returnPage.asXml());
			
			
			HtmlAnchor imgDivA = (HtmlAnchor) returnPage.getByXPath("//div[@class='seachOptionHint']/ul/li/a[@class='picContainer']").get(0);  
			String imgQueryUrl = imgDivA.getAttribute("href");
			String queryUrl = imgQueryUrl.substring(0, imgQueryUrl.length()-1);
			for (int i = 1; i < 10; i++) {
				HtmlPage imgPage = webClient.getPage(BASE_URL+queryUrl+i);
				HtmlImage image = (HtmlImage)imgPage.getByXPath("//div[@id=\"thumbBox\"]/a[@id=\"imageBox\"]/img").get(0);
				String str = image.getAttribute("src");
				if(str.contains(".jpg")){
					int end = str.indexOf(".jpg")+4;
					str = str.substring(0, end);
					download(URLDecoder.decode(str),"E:\\htmlutil\\pic\\");
				}
				System.out.println(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private static void download(String url, String path) {
        File file= null;  
        File dirFile=null;  
        FileOutputStream fos=null;  
        HttpURLConnection httpCon = null;  
        URLConnection  con = null;  
        URL urlObj=null;  
        InputStream in =null;  
        byte[] size = new byte[1024];  
        int num=0;  
        try {  
            String downloadName = url.substring(url.lastIndexOf("/")+1);  
            dirFile = new File(path);  
            if(!dirFile.exists()){  
                if(dirFile.mkdir()){  
                    if(path.length()>0){  
                        System.out.println("creat document file \""+path.substring(0,path.length()-1)+"\" success...\n");  
                    }  
                }  
            }else{  
            file = new File(path+downloadName);  
            fos = new FileOutputStream(file);  
            if(url.startsWith("http")){  
                urlObj = new URL(url);  
                con = urlObj.openConnection();  
                httpCon =(HttpURLConnection) con;  
                in = httpCon.getInputStream();  
                while((num=in.read(size)) != -1){  
                    for(int i=0;i<num;i++)   
                       fos.write(size[i]);  
                }  
            }  
            }  
              
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally{  
            try {  
                fos.close();  
                in.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
	}
	
}
