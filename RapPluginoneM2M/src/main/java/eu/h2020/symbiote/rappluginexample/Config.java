package eu.h2020.symbiote.rappluginexample;

public class Config {
	
	public  static String originator="Cae-admin";
	public final static String key="changeme";

	
	public final static String cseProtocol="http";
	
	public static String cseIp = "127.0.0.1";
	public static int csePort = 8080; 
	
	
	public static String cseId = "server";
	public static String cseName = "server";
	
	public static String contentType="application/json";
	
	public static String csePoa = cseProtocol+"://"+cseIp+":"+csePort+"/~/"+cseId+"/"+cseName+"/"; 
	
	public static double longitude = 1.501385;
    	public static double latitude = 43.549468;
    
   	public static String place = "Sensinov"; 
    	public static String town = "City of Toulouse"; 

}
