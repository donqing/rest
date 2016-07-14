package com.ebupt.restful;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/BaseServlet/*")
public class BaseServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 7325249840967437618L;
    private static PrintWriter writer;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
       // resp.setContentType("application/json; charset=utf-8");
        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();
        writer = resp.getWriter();

        //writer.print(path + "\n");
        //writer.print(pathInfo);
        System.out.println("ServletPath: " + path + ", PathInfo: " + pathInfo);
        Pattern pattern = Pattern.compile("/");
        String[] apiName = pattern.split(pathInfo);
        try {
            for(int i=1; i<apiName.length;i++){
                String methodEveryName = apiName[i];
                Method methodEvery = ServletHandler.class.getDeclaredMethod(methodEveryName,
                        new Class[] { PrintWriter.class, HttpServletRequest.class, HttpServletResponse.class });
            }
            String methodName = apiName[apiName.length - 1];
            Method method = ServletHandler.class.getDeclaredMethod(methodName,
                    new Class[] { PrintWriter.class, HttpServletRequest.class, HttpServletResponse.class });
            
            method.invoke(ServletHandler.class.newInstance(), writer, req, resp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            String error = "{\"name\":"+"\""+e+"\""+",\"value\":\"no such method\"}";
            writer.print(error);
        }

        //resp.setHeader("token", "this is a token");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(req, resp);
    }

    // This is a test
//    @Deprecated
//    protected void getToken(HttpServletRequest req, HttpServletResponse resp) {
//        writer.write("this is a token2");
//    }

}
