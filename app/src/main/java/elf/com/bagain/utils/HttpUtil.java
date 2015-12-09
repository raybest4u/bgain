package elf.com.bagain.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.util.Log;

public class HttpUtil {
	public static String cookieName = "";
	public static String cookieValue = "";
	public static String hostBase = "";
	
	public static String getHtmlString(String urlString) {  
	    try {
	        URL url = new URL(urlString);  
	        URLConnection ucon = url.openConnection();  
	        InputStream instr = ucon.getInputStream();  
	        BufferedInputStream bis = new BufferedInputStream(instr); 
	       // ByteArrayBuffer baf = new ByteArrayBuffer(500);
	        int current = 0;  
	        while ((current = bis.read()) != -1) {  
	          //  baf.append((byte) current);
	        }  
	        return "";//EncodingUtils.getString(baf.toByteArray(), "utf-8");
	    } catch (Exception e) {
	    	
	    	Log.d("win","lllll"+e.toString());
	        return "";  
	    }  
	} 


}
