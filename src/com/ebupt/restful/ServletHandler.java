package com.ebupt.restful;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * 接口处理器
 */
public class ServletHandler extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    //注册信息
    private static Map<String, String> mapRegister = new HashMap<String,String>();

    //url中body信息
    private static Map<String, String> mapBody = new HashMap<String, String>();
    public void getUser(PrintWriter writer, HttpServletRequest req, HttpServletResponse resp) {
        writer.write("This is from getUser");
    }

    public void getTaskFlag(PrintWriter writer, HttpServletRequest req, HttpServletResponse resp) {
        writer.write("This is from getTaskFlag");
    }

    public void setTaskFlag(PrintWriter writer, HttpServletRequest req, HttpServletResponse resp) {

    }

    // OAuth register, return client_id and client_secret
    //当前授权类型为client_credentials
    public void register(PrintWriter writer, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String grant_type = getGrantType(writer,req,resp);
        System.out.println("grant_type=" + mapRegister.get("grant_type") + "&client_name=" + mapRegister.get("client_name"));
        switch(grant_type){
        case "client_credentials": produceCredentialsByGrant(writer,req,resp);break;
        default : System.out.println("no grant_type");;
        }
        
    }

    //获得访问令牌
    public void getAccessToken(PrintWriter writer, HttpServletRequest req, HttpServletResponse resp) throws IOException{
        //读取body中client_id和client_secret
        doGetMessageFromBody(writer,req, resp);
        String client_id = mapBody.get("client_id");
        String client_secret = mapBody.get("client_secret");
        String access_token="";
      //判断是否与mapRegister中相同,若相同，授予accessToken和expires
        if(client_id.equals(mapRegister.get("client_id"))&client_secret.equals(mapRegister.get("client_secret"))){
            access_token=UUID.nameUUIDFromBytes((client_id+client_secret)
                    .getBytes()).toString().replace("-", "");
            mapRegister.put("access_token", access_token);
            //有效期60s
            mapRegister.put("expires", "60000");
            mapRegister.put("timestamp", System.currentTimeMillis()+"");
            writer.write("access_token="+access_token+"&expires="+mapRegister.get("expires"));
        }else{
            writer.write("get access_token failied");
        }
        
    }
    
    public void getLimitedResource(PrintWriter writer, HttpServletRequest req, HttpServletResponse resp) throws IOException{
        //需要根据access_token才能获取,并且access_token在有效期内
        if(isAuthenticated(writer,req,resp)){
            writer.write("good,get limited resource succeed. ");
        }else{
            writer.write("sorry,get limited resource failed. ");
        }
    }
    
    private boolean isAuthenticated(PrintWriter writer, HttpServletRequest req, HttpServletResponse resp) throws IOException{
      
        doGetMessageFromBody(writer,req,resp);
        String client_id = mapBody.get("client_id")+"";
        String client_secret=mapBody.get("client_secret")+"";
        if(mapRegister.get("client_id")==null||!mapRegister.get("client_id").equals(client_id)){
            writer.write("Please registe first. ");
            return false;
        }
        if(!mapRegister.get("client_secret").equals(client_secret)){
            writer.write("Your secret is wrong. ");
            return false;
        }
        String access_token=mapBody.get("access_token")+"";
        if(mapRegister.get("access_token")==null){
            writer.write("Please get token first. ");
            return false;
        }
        if(!mapRegister.get("access_token").equals(access_token)){
            writer.write("Your token is wrong. ");
            return false;
        }
        String expires = mapRegister.get("expires");
        long sum = Long.valueOf(expires)+Long.valueOf(mapRegister.get("timestamp"));
        long current = System.currentTimeMillis();
        if(current>sum){
            writer.write("Your token is expired. ");
            return false;
        }
        return true;
    }
    
    
    //获取授权类型
    private String getGrantType(PrintWriter writer,HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGetMessage(writer,req, resp);
        String grant_type = mapRegister.get("grant_type") + "";
        return grant_type;
    }

    private void produceCredentialsByGrant(PrintWriter writer, HttpServletRequest req, HttpServletResponse resp) {
        String grant_type = mapRegister.get("grant_type");
        String client_name = mapRegister.get("client_name");
        UUID client_uuid = UUID.nameUUIDFromBytes(grant_type.getBytes());
        UUID client_secretid = UUID.nameUUIDFromBytes(client_name.getBytes());
        String client_id = client_uuid.toString().replace("-", "");
        String client_secret = client_secretid.toString().replace("-", "");
        System.out.println("client_id=" + client_id + "&client_secret=" + client_secret);
        mapRegister.put("client_id", client_id);
        mapRegister.put("client_secret",client_secret);
        writer.write("client_id=" + client_id + "&client_secret=" + client_secret);
    }
    
    //从body中获取信息,放入mapRegister中
    private void doGetMessage(PrintWriter writer,HttpServletRequest req, HttpServletResponse resp) throws IOException{
//        String client_id = "";
        ServletInputStream input = req.getInputStream();

        int size = req.getContentLength();
        if(size<=0){
            writer.write("You body is wrong. ");
            return;
        }
        byte[] body = new byte[size];
        input.read(body, 0, size);
        String str = new String(body);
        input.close();
        Pattern pattern1 = Pattern.compile("&");
        Pattern pattern2 = Pattern.compile("=");
        String[] params = pattern1.split(str);
        if(params.length<1){
            writer.write("You body is wrong. ");
            return;
        }
        for (String param : params) {
            String[] strs = pattern2.split(param);
            if(strs.length<2){
                writer.write("You body is wrong. ");
                return;
            }
            mapRegister.put(strs[0], strs[1]);
        }
    }
    
    //从body中获取信息存放到mapBody中
    private void doGetMessageFromBody(PrintWriter writer,HttpServletRequest req, HttpServletResponse resp) throws IOException{
//      String client_id = "";
      ServletInputStream input = req.getInputStream();

      int size = req.getContentLength();
      if(size<=0){
          writer.write("You body is wrong. ");
          return;
      }
      byte[] body = new byte[size];
      input.read(body, 0, size);
      String str = new String(body);
      input.close();
      Pattern pattern1 = Pattern.compile("&");
      Pattern pattern2 = Pattern.compile("=");
      String[] params = pattern1.split(str);
      if(params.length<1){
          writer.write("You body is wrong. ");
          return;
      }
      for (String param : params) {
          String[] strs = pattern2.split(param);
          if(strs.length<2){
              writer.write("You body is wrong. ");
              return;
          }
          mapBody.put(strs[0], strs[1]);
      }
  }
    

}
