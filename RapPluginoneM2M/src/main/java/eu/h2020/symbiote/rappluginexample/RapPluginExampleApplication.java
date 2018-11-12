package eu.h2020.symbiote.rappluginexample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import obix.Obj;
import obix.io.ObixDecoder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.h2020.symbiote.model.cim.Location;
import eu.h2020.symbiote.model.cim.Observation;
import eu.h2020.symbiote.model.cim.ObservationValue;
import eu.h2020.symbiote.model.cim.Property;
import eu.h2020.symbiote.model.cim.UnitOfMeasurement;
import eu.h2020.symbiote.model.cim.WGS84Location;
import eu.h2020.symbiote.rapplugin.domain.Capability;
import eu.h2020.symbiote.rapplugin.domain.Parameter;
import eu.h2020.symbiote.rapplugin.messaging.rap.ActuatingResourceListener;
import eu.h2020.symbiote.rapplugin.messaging.rap.InvokingServiceListener;
import eu.h2020.symbiote.rapplugin.messaging.rap.RapPlugin;
import eu.h2020.symbiote.rapplugin.messaging.rap.RapPluginException;
import eu.h2020.symbiote.rapplugin.messaging.rap.ReadingResourceListener;


@SpringBootApplication
public class RapPluginExampleApplication implements CommandLineRunner {
 public static final Logger LOG = LoggerFactory.getLogger(RapPluginExampleApplication.class);

 @Autowired
 RapPlugin rapPlugin;
 public static void main(String[] args) {
  SpringApplication.run(RapPluginExampleApplication.class, args);
 }

 @Override
 public void run(String...args) throws Exception {

  rapPlugin.registerReadingResourceListener(new ReadingResourceListener() {

   @Override
   public List < Observation > readResourceHistory(String resourceId) {
    try {
     createHistory(resourceId);
    } catch (IOException e1) {
     throw new RapPluginException(404, "Sensor not found.");
    }
   }

   @Override
   public Observation readResource(String resourceId) {
    try {
     createObservation(resourceId);
    } catch (IOException e1) {
     throw new RapPluginException(404, "Sensor not found.");
    }

   }
  });

  rapPlugin.registerActuatingResourceListener(new ActuatingResourceListener() {
   @Override
   public void actuateResource(String resourceId, Map < String, Capability > parameters) {
    String value = null;
    System.out.println("Called actuation for resource " + resourceId);
    for (Capability capability: parameters.values()) {
     System.out.println("Capability: " + capability.getName());
     for (Parameter parameter: capability.getParameters().values()) {
      System.out.println(" " + parameter.getName() + " = " + parameter.getValue());
      value = parameter.getValue().toString();
     }
    }

    System.out.println("value to execute : " + value);

    try {
     executeCommand(resourceId, value);
    } catch (IOException e1) {
     throw new RapPluginException(404, "Actuating entity not found.");
    }
   }






  });

  rapPlugin.registerInvokingServiceListener(new InvokingServiceListener() {

   @Override
   public Object invokeService(String resourceId, Map < String, Parameter > parameters) {
    System.out.println("In invoking service of resource " + resourceId);
    for (Parameter p: parameters.values())
     System.out.println(" Parameter - name: " + p.getName() + " value: " + p.getValue());
    if ("isen9".equals(resourceId)) {
     return "ok";
    } else {
     throw new RapPluginException(404, "Service not found.");
    }
   }
  });



 }

 public Observation createObservation(String resourceId) {

  String parts[] = resourceId.split("__");
  String AEName = parts[0];
  String CntName = parts[1];


  String url = Config.csePoa + AEName + "/" + CntName + "/DATA/la";
  System.out.println(url);

  System.out.println("AEName = " + AEName);
  System.out.println("CntName = " + CntName);
  System.out.println(url);

  HttpClient client = HttpClientBuilder.create().build();
  HttpGet request = new HttpGet(url);
  // add request header
  request.addHeader("X-M2M-Origin", Config.originator);

  request.addHeader("Content-Type", Config.contentType);

  HttpResponse response = null;
  try {
   response = client.execute(request);
  } catch (ClientProtocolException e1) {
   e1.printStackTrace();
  } catch (IOException e1) {
   e1.printStackTrace();
  }

  System.out.println("Response Code : " +
   response.getStatusLine().getStatusCode());
  String responseContent = null;
  try {
   responseContent = EntityUtils.toString(
    response.getEntity(), StandardCharsets.UTF_8.name());
  } catch (ParseException e1) {
   // TODO Auto-generated catch block
   e1.printStackTrace();
  } catch (IOException e1) {
   // TODO Auto-generated catch block
   e1.printStackTrace();
  }

  System.out.println("Response Body : " +
   responseContent);
  System.out.println("Response : " +
   response);

  String value = "";
  String lt = "";
  String et = "";

  try {

   JSONObject jsonObj1 = new JSONObject(responseContent);
   System.out.println("json content  = " + jsonObj1);

   //System.out.println(jsonObj1.keys());
   System.out.println(jsonObj1.get("m2m:cin"));
   //System.out.println("cs : " +new JSONObject(jsonObj1.get("m2m:cin").toString()).get("cs"));
   System.out.println("ri : " + new JSONObject(jsonObj1.get("m2m:cin").toString()).get("ri"));
   System.out.println("lt : " + new JSONObject(jsonObj1.get("m2m:cin").toString()).get("lt"));
   System.out.println("con : " + new JSONObject(jsonObj1.get("m2m:cin").toString()).get("con"));

   lt = (String) new JSONObject(jsonObj1.get("m2m:cin").toString()).get("lt");

   JSONObject cin = jsonObj1.getJSONObject("m2m:cin");
   String con = cin.getString("con");
   System.out.println(con);
   Obj obix = ObixDecoder.fromString(con);
   value = obix.get("value").getStr();
  }
  System.out.println(value);
 } catch (JSONException e1) {
  // TODO Auto-generated catch block
  e1.printStackTrace();
 }

 //Observation creation
 Location loc = new WGS84Location(43.549468, 1.501385, 15, "Sensinov", Arrays.asList("City of Toulouse"));

 TimeZone zoneUTC = TimeZone.getTimeZone("UTC");
 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
 dateFormat.setTimeZone(zoneUTC);
 Date date = new Date();
 String timestamp = dateFormat.format(date);

 long ms = date.getTime() - 1000;
 date.setTime(ms);
 String samplet = dateFormat.format(date);

 ObservationValue obsval = null;

 obsval = new ObservationValue(
  value,
  new Property("Sensor", "last modified time  " + lt.substring(0, 4) + "-" + lt.substring(4, 6) + "-" + lt.substring(6, 8), Arrays.asList("Sensing")),
  null);

 ArrayList < ObservationValue > obsList = new ArrayList < > ();
 obsList.add(obsval);

 Observation obs = new Observation(resourceId, loc, timestamp, samplet, obsList);

 try {
  LOG.debug("Observation: \n{}", new ObjectMapper().writeValueAsString(obs));
 } catch (JsonProcessingException e) {
  LOG.error("Can not convert observation to JSON", e);
 }

 return obs;
}

public List < Observation > createHistory(String resourceId) {

 // This is the place to put reading history data of sensor.

 String parts[] = resourceId.split("__");
 String AEName = parts[0];
 String CntName = parts[1];


 String url = Config.csePoa + AEName + "/" + CntName + "/DATA?rcn=4";
 System.out.println(url);

 System.out.println("AEName = " + AEName);
 System.out.println("CntName = " + CntName);
 System.out.println(url);


 HttpClient client = HttpClientBuilder.create().build();
 HttpGet request = new HttpGet(url);
 // add request header
 request.addHeader("X-M2M-Origin", Config.originator);

 request.addHeader("Content-Type", Config.contentType);
 HttpResponse response = null;
 try {
  response = client.execute(request);
 } catch (ClientProtocolException e1) {
  e1.printStackTrace();
 } catch (IOException e1) {
  e1.printStackTrace();
 }

 System.out.println("Response Code : " +
  response.getStatusLine().getStatusCode());
 String responseContent = null;

 try {
  responseContent = EntityUtils.toString(
   response.getEntity(), StandardCharsets.UTF_8.name());
 } catch (ParseException | IOException e) {
  // TODO Auto-generated catch block
  e.printStackTrace();
 }


 System.out.println("Response Body : " + responseContent);
 System.out.println("Response : " + response);


 //Récupération du json 
 JSONObject jsonObj = null;

 try {
  jsonObj = new JSONObject(responseContent);
 } catch (JSONException e) {
  // TODO Auto-generated catch block
  e.printStackTrace();
 }
 System.out.println(jsonObj);


 JSONArray json = null;
 try {
  json = (JSONArray) new JSONObject(jsonObj.get("m2m:cnt").toString()).get("cin");
  System.out.println("noow" + json);
 } catch (JSONException e1) {
  // TODO Auto-generated catch block
  e1.printStackTrace();
 }

 ArrayList < Observation > alist = new ArrayList < Observation > ();

 String con = null;
 String lt = null;
 String value = "";

 for (int i = 0; i < json.length(); i++) {
  //récupérer un element sous cin (représentation d'un contentInstance)
  JSONObject el = null;
  try {
   el = json.getJSONObject(i);
  } catch (JSONException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  // récupérer le JSONObject Distance qui contient deux items


  try {
   lt = el.getString("lt");
   con = el.getString("con");
   Obj obix = ObixDecoder.fromString(con);

   value = obix.get("value").getStr();

   System.out.println(value);

   System.out.println("\n" + lt);

  } catch (JSONException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }


  Location loc = new WGS84Location(43.549468, 1.501385, 15, "Sensinov", Arrays.asList("City of Toulouse"));

  TimeZone zoneUTC = TimeZone.getTimeZone("UTC");
  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  dateFormat.setTimeZone(zoneUTC);
  Date date = new Date();
  String timestamp = dateFormat.format(date);

  long ms = date.getTime() - 1000;
  date.setTime(ms);
  String samplet = dateFormat.format(date);



  ObservationValue obsval = null;

  obsval = new ObservationValue(
   value,
   new Property("Sensor", "last modified time  " + lt.substring(0, 4) + "-" + lt.substring(4, 6) + "-" + lt.substring(6, 8), Arrays.asList("Sensing")),
   null);


  ArrayList < ObservationValue > obsList = new ArrayList < > ();
  obsList.add(obsval);

  Observation obs = new Observation(resourceId, loc, timestamp, samplet, obsList);

  try {
   LOG.debug("Observation: \n{}", new ObjectMapper().writeValueAsString(obs));
  } catch (JsonProcessingException e) {
   LOG.error("Can not convert observation to JSON", e);
  }

  alist.add(obs);




 }

 alist.toArray();
 System.out.println("gloglo" + alist);


 return new ArrayList < > (alist);
}

public void executePost(String resourceId, String value) {

 //faire un bloc case par rapport à resourceId

 String parts[] = resourceId.split("__");
 String AEName = parts[0];

 int intValue = Integer.parseInt(value);
 String url = null;

 
 if (intValue > 0) {
  url = Config.csePoa + AEName + "?1=1";  
 } else {
  url = Config.csePoa + AEName + "?1=0"; 
 }

 System.out.println("url " + url);
 HttpClient client = HttpClientBuilder.create().build();
 HttpPost post = new HttpPost(url);
 post.addHeader("X-M2M-Origin", Config.originator);
 post.addHeader("Content-Type", Config.contentType);

 HttpResponse response = null;
 try {
  response = client.execute(post);
 } catch (ClientProtocolException e1) {
  e1.printStackTrace();
 } catch (IOException e1) {
  e1.printStackTrace();
 }

 System.out.println("Response Code : " +
  response.getStatusLine().getStatusCode());
 String responseContent = null;
 try {
  responseContent = EntityUtils.toString(
   response.getEntity(), StandardCharsets.UTF_8.name());
 } catch (ParseException e1) {
  // TODO Auto-generated catch block
  e1.printStackTrace();
 } catch (IOException e1) {
  // TODO Auto-generated catch block
  e1.printStackTrace();
 }

 System.out.println("Response Body : " +
  responseContent);
 System.out.println("Response : " +
  response);


}

public void executeCommand(String resourceId, String value) {

 executePost(resourceId, value);
 System.out.println(resourceId + "is actuated");
 return;
}
}
