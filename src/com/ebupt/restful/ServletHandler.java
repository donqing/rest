package com.ebupt.restful;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*
 * 接口处理
 */
public class ServletHandler extends HttpServlet {
    
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void getUser( PrintWriter writer,HttpServletRequest req, HttpServletResponse resp){
        writer.write("This is from getUser");
    }
    
    public void getTaskFlag(PrintWriter writer,HttpServletRequest req, HttpServletResponse resp){
        writer.write("This is from getTaskFlag");
    }
    
    public void setTaskFlag(PrintWriter writer, HttpServletRequest req, HttpServletResponse resp){
        
    }
   
    

}
