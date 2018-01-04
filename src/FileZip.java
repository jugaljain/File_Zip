import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileZip {
	
	
	String readFileAsASCIIString(String filePath) {
	    FileInputStream reader = null;
	    String result = null;

	    try {
	    	byte[] buffer = new byte[(int) new File(filePath).length()];
		    reader = new FileInputStream(filePath);
		    
		    reader.read(buffer);
		    result = new String(buffer,"ISO-8859-1");
	    } catch (IOException ex) {
	    	System.out.println("File cannot be read.");
	    	return null;
	    } finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException ex) {
				System.out.println("File cannot be closed.");
				return null;
			}
		}
		return result;
	}

	void writeFileAsASCIIString(String data, String filePath) {
	    FileOutputStream writer = null;

	    try {
		    byte[] buffer = data.getBytes("ISO-8859-1");
		    writer = new FileOutputStream(filePath);
		    writer.write(buffer);
		    writer.flush();
	    } catch (IOException ex) {
	    	System.out.println("File cannot be written.");
	    } finally {
	    	try {
				if (writer != null) writer.close();
			} catch (IOException ex) {
				System.out.println("File cannot be closed.");
			}
	    }

	}
	
	public void zip(String inputFileName, String newFileName) throws FileNotFoundException{
		
		String input = readFileAsASCIIString(inputFileName);
		String newData="";
		int codesize=2;
		ArrayList<String> dictionary=new ArrayList<String>(5000);
		for (char i=0; i<256; i++){
	        dictionary.add(""+i);
		}
		String work="";
		for(int i=0; i<input.length();i++ ){
			char c= input.charAt(i);
			if(dictionary.contains(""+work+c)){
				work+=c;
			}
			else{
				if(dictionary.size()==65535) codesize=3;
				newData+=getCode(dictionary.indexOf(work),codesize);
				dictionary.add(""+work+c);
				work=""+c;
			}
		}
		System.out.println("Dictionary:" + dictionary.size());
		newData+=getCode(dictionary.indexOf(work), codesize);		
		//System.out.println("\n \n --------Encoded data------------ \n\n"+newData);
		//System.out.println(dictionary.toString());
		writeFileAsASCIIString(newData, newFileName);
		
		/*FileInputStream reader = null;
	    ArrayList<String> dictionary=new ArrayList<String>(5000);
		String newData = "";
		String work="";
		for (char i=0; i<256; i++)
	        dictionary.add(""+i);
		

	    try {
	
		    reader = new FileInputStream(inputFileName);
		    byte[] buffer = new byte[1]; 
			int d = reader.read(buffer);
			while (d != -1) {
				char c= (char) buffer[0];
				if(dictionary.contains(""+work+c)){
					work+=c;
				}
				else{
					newData+=getCode(dictionary.indexOf(work));
					System.out.println(dictionary.indexOf(work)+" " + work);
					if(!work.equals(""+c)){
						dictionary.add(""+work+c);
					}
					work=""+c;
				}
				d=reader.read(buffer);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	    finally{
	    	try {
				if (reader != null) reader.close();
			} catch (IOException ex) {
				System.out.println("File cannot be closed.");
			}
	    }
	    newData+=getCode(dictionary.indexOf(work));
	    writeFileAsASCIIString(newData, newFileName);*/	

	}
	
	private String getCode(int indexOf, int codesize) {
		/*char code1= (char) (indexOf % 256);
		char code2=(char) (indexOf/256);*/
		char code1 =  (char) (indexOf & 0x00FF);
		char code2 = (char) ((indexOf >>> 8) & 0x00FF);
		if(codesize==3){
			char code3= (char)((indexOf>>>16) & 0x00FF);
			return ""+code3+code2+code1;
		}else{
			return ""+code2+code1;
		}
	}
	
	private int decode(byte[] codes){
//		int decode2= (int) (code2*256);
//		int decode1= (int) (code1);
		int decode=0;
		if(codes.length==2){
			int decode1 = (int)(0x00FF & codes[1]);
			int decode2 = ((int)(0x00FF & codes[0]) << 8);
			decode = decode2 + decode1;
		}
		else if (codes.length==3){
			int decode1 = (int)(0x00FF & codes[2]);
			int decode2 = ((int)(0x00FF & codes[1]) << 8); 
			int decode3= (int)((0x00FF & codes[0])<<16);
			decode=decode3+decode2+decode1;
		}
		return decode;
	}

	
	public void unzip(String inputFileName, String newFileName){
	    FileInputStream reader = null;
	    ArrayList<String> dictionary=new ArrayList<String>(5000);
		String unzipData = "";
		int codesize=2;
		for (char i=0; i<256; i++)
	        dictionary.add(""+i);
		

	    try {
	
		    reader = new FileInputStream(inputFileName);
		    
		    byte[] buffer = new byte[codesize]; 
			int d = reader.read(buffer);
			int indexOf = decode(buffer);
			String work = "" + (char)indexOf;
			unzipData += work;
			d = reader.read(buffer);
			char c=work.charAt(0);
			while (d != -1) {
				indexOf = decode(buffer);
				String entry="";
				if(dictionary.size()<=indexOf){
					entry=work+c;
					
				}
				else{
					entry =  dictionary.get(indexOf);
				}
				unzipData += entry;
				c=entry.charAt(0);
				work+=c;
				dictionary.add(work);
				if(dictionary.size()==65535)codesize=3;
				buffer = new byte[codesize];
				d = reader.read(buffer);
				work=entry;
			}
			
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    	System.out.println("File cannot be read.");
	    }catch(IndexOutOfBoundsException e){
	    	System.out.println(unzipData);
	    	e.printStackTrace();
	    }
	    finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException ex) {
				System.out.println("File cannot be closed.");
			}
		}
	    /*System.out.println(dictionary.toString());
		*/
	    writeFileAsASCIIString(unzipData, newFileName);
	}

}
