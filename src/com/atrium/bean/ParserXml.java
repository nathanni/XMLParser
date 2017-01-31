package com.atrium.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class ParserXml {
	
	private static final String  QUESTION_PRIFIX = "\\PID*X**ZZ*QUESTION*"; 
	private static final String  ANSWER_PRIFIX = "\\PID*X**ZZ*ANSWER*"; 
	private static final String  ITEM_SEPERATOR = "ID*00000000000000000000000000000000000000000000000000000000000000000000000000000000";
	
	private static final String  QUESTION_NODE = "cfg_qst_txt"; 
	private static final String  ANSWER_NODE = "cfg_ans_txt"; 
	//private static final String  ITEM_NODE = "cfg_cmo_inf";
	private static final String  ITEM_COUNT_NODE = "prj_dtl_itm";
	private static final String  ITEM_NUM_NODE = "itm_nbr";
	private static final String  ITEM_PRICE_NODE = "acl_itm_sel_amt";
	private static final String  ITEM_QTY_NODE = "ord_qty";
	private static final String  ITEM_DESCRIPTION_NODE = "itm_des";
	private static final String  ITEM_PRDCODE_NODE = "cmo_prd_idf";
	
	private String Header;
	private String Trailer;
	private int line;
	private int qty;
	private double price;
	private int itemNumber;
	private String ITEM_INFO;  
	private String DESCRIPTION;
	private String PRDCODE;
    private StringBuffer parserVal;
    
    public ParserXml() {
    	this.parserVal = new StringBuffer("");
    	this.line = 0;
    	generateHeader();
    	generateTrailer();
    }
    
    public void generateHeader() {
    	this.Header = "ISA*00*          *00*          *08*6135830001     *12*2146300847P    *140513*1501*U*00400*999999999*0*P\\GS*PO*6135830001*2146300847P*20140101*0101*999999999*X*004010\\ST*850*999999999\\BEG*00*OS*888888888**20140101\\CUR*SE*USD\\REF*IA*33972\\REF*JB*410029365\\REF*SA*0000123623\\PER*OC*GAILES, ARON*TE*5047800114\\FOB*PP\\DTM*002*20140202\\";
    }
    
    public void generateTrailer() {
    	this.Trailer = "\\CTT*1\\SE*50*999999999\\GE*1*999999999\\IEA*1*999999999\\";
    	
    }
    
    public void setHeader() {
    	this.parserVal.append(this.Header);
    }
    
    public void setTrailer() {
    	this.parserVal.append(this.Trailer);
    }
    
	
	public void staxParser(String sourceFilePath) throws Exception {
		  // First, create a new XMLInputFactory
	      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	      // Setup a new eventReader
	      File  f = new File(sourceFilePath); 
	      InputStream in = new FileInputStream(f);
	      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
	      
	      setHeader();
	      
	      while (eventReader.hasNext()) {
	    	  XMLEvent event = eventReader.nextEvent();	    	  
	    	  if(event.isStartElement()) {
	    		  StartElement startElement = event.asStartElement();
	    		  
	    		  
	    		  if(startElement.getName().getLocalPart().equals(ITEM_COUNT_NODE)) {
	    			  event = eventReader.nextEvent();	
	    			  this.line++;
	    		  }
	    		  
	    		  if(startElement.getName().getLocalPart().equals(ITEM_NUM_NODE)) {
	    			  event = eventReader.nextEvent();	
	    			  this.itemNumber = Integer.parseInt(event.asCharacters().getData());
	    		  }
	    		  
	    		  if(startElement.getName().getLocalPart().equals(ITEM_PRICE_NODE)) {
	    			  event = eventReader.nextEvent();	
	    			  this.price = Double.parseDouble(event.asCharacters().getData());
	    		  }
	    		  
	    		  if(startElement.getName().getLocalPart().equals(ITEM_QTY_NODE)) {
	    			  event = eventReader.nextEvent();	
	    			  this.qty = Integer.parseInt(event.asCharacters().getData());
	    		  }
	    		  
	    		  if(startElement.getName().getLocalPart().equals(ITEM_DESCRIPTION_NODE)) {
	    			  //event = eventReader.getElementText();
	    			  DESCRIPTION = "\\PID*F****" + eventReader.getElementText();
	    			  //System.out.println(DESCRIPTION);
	    		  }
	    		  
	    		  
	    		  //Generated the Item Description
	    		  if(startElement.getName().getLocalPart().equals(ITEM_PRDCODE_NODE)) {
	    			  event = eventReader.nextEvent();	
	    			  this.PRDCODE = "\\PID*X**ZZ*PRODUCT CODE*" + event.asCharacters().getData() + "\\PID*X**ZZ*COMPOSITE";
	    			  this.ITEM_INFO = "\\PO1*" + Integer.toString(this.line) + "*" + Integer.toString(this.qty) + "*ZZ*" + Double.toString(this.price) + "*QT*CB*" + Integer.toString(this.itemNumber) + "*VC*NA";  
	    			  this.parserVal.append(this.ITEM_INFO).append(this.DESCRIPTION).append(this.PRDCODE).append(this.ITEM_SEPERATOR);
	    		  }
	    		  /*
	    		  
	    		  if(startElement.getName().getLocalPart().equals(ITEM_NODE)) {
	    			  event = eventReader.nextEvent();
	    			  ITEM_INFO = "\\PO1*" + Integer.toString(line) + "*" + Integer.toString(qty) + "*ZZ*" + Double.toString(price) + "*QT*CB*" + Integer.toString(itemNumber) + "*VC*NA";  
	    			  parserVal.append(ITEM_INFO).append(DESCRIPTION).append(PRDCODE).append(ITEM_SEPERATOR).append("\n");
	    		  }
	    		 */
	    		  if(startElement.getName().getLocalPart().equals(QUESTION_NODE)){
	    			  event = eventReader.nextEvent();
	    			  if(event.isCharacters()){
	    				  String value =  event.asCharacters().getData();
	    				  this.parserVal.append(QUESTION_PRIFIX).append(value);
	    				  //System.out.println("QUESTION="+value);
	    			  }
	    		  }
	    		  if(startElement.getName().getLocalPart().equals(ANSWER_NODE)){
	    			  event = eventReader.nextEvent();
	    			  if(event.isCharacters()){
	    				  String value =  event.asCharacters().getData();
	    				  this.parserVal.append(ANSWER_PRIFIX).append(value);
	    				  //System.out.println("ANSWER="+value);
	    			  }
	    		  }
	    		  
	    		  
	    	  }

		}
	      
	      setTrailer();
	}
	
	public boolean outputTxt(String outFilePath) throws IOException  {

			File outFile =  new File(outFilePath);
			FileOutputStream outputStream = new FileOutputStream(outFile);
			byte[] b = this.parserVal.toString().getBytes();
			outputStream.write(b);
			outputStream.close();
			return true;
			
	}
	
}
