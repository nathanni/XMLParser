package com.atrium.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class XmlFileDownload extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		this.doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String path = req.getParameter("fileName");
//		System.out.println(path);
		File file = new File(path);
	    String fileName = file.getName();
	    InputStream fis = null;
	    OutputStream os = null;
	    try {
	    	fis = new BufferedInputStream(new FileInputStream(path));
	    	byte[] buffer = new byte[fis.available()];
		    fis.read(buffer);
		    fis.close();
		    resp.reset();
		    
		    resp.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
		    resp.addHeader("Content-Length", "" + file.length());
		    os = new BufferedOutputStream(resp.getOutputStream());
		    resp.setContentType("application/octet-stream");
		    os.write(buffer);
		    os.flush();
		    os.close();
	    } catch (Exception e) {
	    	req.getRequestDispatcher("retranslate.jsp").forward(req, resp);
	    } finally {
	    	
		    // remove the files on the server...
		    file.delete();
	    }
	    
	}
}
