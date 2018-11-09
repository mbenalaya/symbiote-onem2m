package eu.h2020.symbiote.rappluginexample;

public class Config {
	
	public  static String originator="admin:admin";
	public final static String key="admin";

	
	public final static String cseProtocol="http";
	
	public static String cseIp = "127.0.0.1";
	public static int csePort = 8080; 
	
	
	public static String cseId = "gateway-1";
	public static String cseName = "gateway-1";
	
	public static String contentType="application/json";
	
	public static String csePoa = cseProtocol+"://"+cseIp+":"+csePort+"/~/"+cseId+"/"+cseName+"/"; 
	
	public static double longitude = 1.501385;
    	public static double latitude = 43.549468;
    
    	public static String place = "Sensinov"; 
    	public static String town = "City of Toulouse"; 

}
