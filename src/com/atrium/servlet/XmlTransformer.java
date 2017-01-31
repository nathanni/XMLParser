package com.atrium.servlet;

import java.io.BufferedReader;
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;  
import java.io.InputStreamReader;
import java.util.Iterator;  
import java.util.List;  

import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.fileupload.FileItem;  
import org.apache.commons.fileupload.FileItemFactory;  
import org.apache.commons.fileupload.FileUploadException;  
import org.apache.commons.fileupload.disk.DiskFileItemFactory;  
import org.apache.commons.fileupload.servlet.ServletFileUpload;  

import com.atrium.bean.ParserXml;

import java.util.Calendar;
import java.text.SimpleDateFormat;


public class XmlTransformer extends HttpServlet {
	
	public static final String DATE_FORMAT_NOW = "HHmmMMddyyyy";
	private ParserXml px = null;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
        req.setCharacterEncoding("UTF-8");  
        px = new ParserXml();
        try {
			fileControl(req, resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }  
	
	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
   
    public void fileControl(HttpServletRequest req, HttpServletResponse resp) throws Exception {  
    	
//        boolean isMultipart = ServletFileUpload.isMultipartContent(req);  
        FileItemFactory factory = new DiskFileItemFactory();
        String fileUpload = "c:\\";
  
        File tempFile = new File(req.getRealPath("/temp"));  
        // set cache...
        ((DiskFileItemFactory) factory).setSizeThreshold(1024*1024);  
        ((DiskFileItemFactory) factory).setRepository(tempFile);
        ServletFileUpload upload = new ServletFileUpload(factory);  
  
        List items = null;  
        try {  
            items = upload.parseRequest(req);  
        }  
        catch (FileUploadException e) {  
            e.printStackTrace();  
        }  
  
        Iterator iter = items.iterator();  
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();  
            // it's ordinary format  
            if (item.isFormField()) {  
                String fieldName = item.getFieldName();  
                String value = item.getString();  
                req.setAttribute(fieldName, value);  
            }  
            // it's file format
            else {  
            	String outFileName = "IN-086135830001-099999999" + now();
            	
                String fileName = item.getName();  
                int index = fileName.lastIndexOf("\\");  
                fileName = fileName.substring(index + 1);  
                req.setAttribute("realFileName", fileName);  
  
                File file = new File(fileUpload, fileName);  
                try {  
                    item.write(file);  
                    upload.setSizeMax(1024*1024*50);  
                    upload.setFileSizeMax(1024*1024*6); 
                }  
                catch (Exception e) {  
                    e.printStackTrace();  
                }
                
                if (null == fileName || "".equals(fileName)) {
                	break;
                } else {
                	try {
                		px.staxParser(fileUpload + "\\" + fileName);
                		px.outputTxt(req.getRealPath("/") + outFileName + ".edi.txt");
                    	req.setAttribute("ediFileName", outFileName + ".edi.txt");

                    	req.getRequestDispatcher("/download.jsp").forward(req, resp);  
                	} catch (Exception ex) {
                		ex.printStackTrace();
                		resp.sendRedirect("error.jsp");
                	} finally {
                		File xmlFile = new File("c:\\" + fileName);
                        xmlFile.delete();
                	}
                }
            }
        }  
    }  
}
