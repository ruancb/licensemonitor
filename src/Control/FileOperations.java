package Control;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class FileOperations {
	
	
	public FileOperations() {
		
		
		
	}
	
	/**
	 * Read a file and returns your contents into a ArrayList of String
	 * @param source
	 * Location of file which will read
	 * @return
	 */
	public static ArrayList<String> readFileintoArray (String sourcePath){
		FileReader readerSource = null;
 		BufferedReader leitorSource = null;
	 	String linha = null;
	 	File source = new File(sourcePath);
	 	ArrayList<String> output = new ArrayList<String>();
	 	int linhaAtual = 0;	
	 		 	
	 	try {
			readerSource = new FileReader(source);

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("Arquivo não encontrado.");
			return null;
		}
 		leitorSource = new BufferedReader(readerSource,1*1024*1024);
 		do{
 			try {
				linha=leitorSource.readLine();
				linhaAtual++;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
 			if(linha!=null && !linha.startsWith("#")){
 				output.add(linha);
 	 			
 			}
 			
 		}
 		while(linha!=null);
		 			
 		try {
 			System.gc();
			readerSource.close();
			leitorSource.close();
		} catch (IOException e3) {
			e3.printStackTrace();
			System.out.println("Problema ao fechar o reader do arquivo");
		}
 		source = null;
 		linha = "";
 		return output;					
	}
	
	/**
	 * Creates a new file
	 * @param contents
	 * Contents of file
	 * @param path
	 * Location where file will be created
	 */
	public void writeFile(String contents, String path){
		try {
			String dataAtual = new SimpleDateFormat("yyyy-MM-dd").format( new Date(System.currentTimeMillis()));  
			String horaAtual  = new SimpleDateFormat("HHmmss").format( new Date(System.currentTimeMillis()));  
			int tam = contents.getBytes("UTF8").length;
			File f = new File(dataAtual+"_"+horaAtual+"_"+  path);
			if(!f.exists()){
				f.createNewFile();
			}
		    FileChannel fcg = new RandomAccessFile(f, "rw").getChannel(); 	    
		    ByteBuffer bg = fcg.map(FileChannel.MapMode.READ_WRITE, 0, tam);
		    byte [] b = contents.getBytes("UTF8"); 
		    bg.put(b);
		    fcg.close();
		    bg.clear();
		    b = null;
		    System.gc();
	
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("Falha ao criar arquivo");
			}
	}
	
	public ArrayList<File> catchFilesInFolder (ArrayList<File> folderList){		
		int folderInList =0;
		File currentFolder;
		ArrayList<File> files = new ArrayList<File>();
		// If it is a directory, empty it first System.out.println("Deleting: "+p_file.getPath()); 
		if(folderList.size() == 0){
			return null;
		}
		while(folderInList < folderList.size()){
			currentFolder = folderList.get(folderInList);
			getFilesInFolder(currentFolder);
			folderInList++;
		}
		
		
		
		return files;
		
	}
	
	public static ArrayList<File> getFilesInFolder( File currentFolder){
		String foldHier = "";
		ArrayList<File> filesInFolder = new ArrayList<File>();
		//AssociatedDocuments tempAD;
		int i =0; 
		if(currentFolder.isDirectory()) {	
			String[] dirList = currentFolder.list();  
		    while(i< currentFolder.list().length){   
		    	//crio um File com o primeiro arq/pasta do diretorio que estou
		    	File nextFile = new File(currentFolder.getPath()+Constants.FILE_SEPARATOR+dirList[i]);  
		        if(nextFile.isDirectory()) {
		    		foldHier = foldHier +Constants.FILE_SEPARATOR+ nextFile.getName();
		    		getFilesInFolder(nextFile); //passo da recursão   
		        } 
				foldHier = foldHier +Constants.FILE_SEPARATOR+ nextFile.getName();
		        if(!nextFile.isDirectory()){
		        	filesInFolder.add(nextFile);
		        }	              		        
		        foldHier = currentFolder.getPath();       
		        i++;
		        }
		      }else{
		    	  return null;
		      }
		return filesInFolder;
			
		}
	
public boolean writeCsv(String str, String path, String name) throws IOException{
		
		//String dataAtual = new SimpleDateFormat("yyyy-MM-dd").format( new Date(System.currentTimeMillis()));  
		//String horaAtual  = new SimpleDateFormat("HHmmss").format( new Date(System.currentTimeMillis()));  
		String destinyPath;
		destinyPath = (path + /*dataAtual+"_"+ horaAtual+"_" +*/ name+".csv");     
		System.out.println("Destino: " + destinyPath);
		destinyPath = destinyPath.replaceAll(":", "");
		BufferedWriter bfw = null;                         
		try {     
			if(!new File(destinyPath).exists()){
				bfw = new BufferedWriter( new FileWriter(destinyPath, false));
				bfw.write(str);  
				bfw.close();
			}else{
				bfw = new BufferedWriter( new FileWriter(destinyPath, true));
				bfw.write(Constants.newLine+ str);  
				bfw.close();
				
			}
			} catch( FileNotFoundException e ) {    
				System.out.println( "File not created. Please verify if this file already exists and is open." );   
				return false;
			} catch( IOException e ) {   
				System.out.println( "Access denied." );  
				return false;
			} catch( NullPointerException e ) {
				System.out.println("File not created. Please verify if this file already exists and is open." ); 
				return false;
			}
		System.out.println( "File created at " + new File(destinyPath).getAbsolutePath()); 
		return true;
		
	}	
		
}
	
