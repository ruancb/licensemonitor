package Control;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;


public class Processa {
	ArrayList<String> reportContent = new ArrayList<String>();
	static FileOperations fileOperation = new FileOperations();
	ArrayList<String> licenseUsageList = new ArrayList<String>();
	ArrayList<String> runTimeList = new ArrayList<String>();
	ArrayList<ReportMetadata> reportMetadataList = new ArrayList<ReportMetadata>();
	boolean licenseUsage = false, licenseUser = false;
	StringBuilder outputContent = new StringBuilder("Name,Package,Date," + Constants.newLine);
	int totalLicenses =0, totalLicensesInUse=0, petrelLicensesTotal =0, petrelLicensesInUse=0, eclipseLicensesInUse=0, eclipseLicensesTotal=0;
	boolean firstTime = true;
	int  runInterval;//in minutes
	int  runTime;//in hours
	//File input = new File("input.txt");
	
	public Processa() throws IOException{
		
		
		ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
		TimerTask rodar = new TimerTask() {                             
            public void run() {
            	try {
					execBAT(Constants.pathsBAT);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            	licenseUsage =  licenseUser = false;
            	reportMetadataList = new ArrayList<ReportMetadata>();
            	licenseUsageList = new ArrayList<String>();
            	totalLicenses = totalLicensesInUse= petrelLicensesTotal = petrelLicensesInUse=0;
            	outputContent = new StringBuilder("Name,Package,Date," + Constants.newLine);
            	if(new File("status.log").exists()){
        			getMetadata("status.log");
        		}else{
        			if(new File("status.txt").exists()){
        				getMetadata("status.txt");	
        			}else{
        				JOptionPane.showMessageDialog(null, "Arquivo não encontrado...\nFavor colocar um arquivo status.txt ou status.log\nno mesmo diretório do programa.");
        				System.exit(0);
        			}
        		}
        					
        		try {
					createOutputFile("", "LicensesInUse" );
					firstTime = false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
            	
            }
		};
		runTimeList = FileOperations.readFileintoArray("input.txt");
		System.out.println("Run Time: "+runTime+"\nInterval: "+ runInterval);
    	runTime = Integer.parseInt(runTimeList.get(0).replaceAll(" ", ""));
    	runInterval = Integer.parseInt(runTimeList.get(1).replaceAll(" ", ""));
    	System.out.println("Run Time: "+runTime+"\nInterval: "+ runInterval);
		timer.scheduleAtFixedRate(rodar, 0, runInterval,TimeUnit.MINUTES);
		try { timer.awaitTermination(runTime, TimeUnit.HOURS); //roda de 15 em 15 min ao longo de 4 horas(8 às 12)
        } 
        catch (InterruptedException ie) { 
                ie.printStackTrace(); 
        }
		timer.shutdown(); // só posso dar shutdown após o processamento
		
		
			

	}
	
	//retorna o mnemonico de uma linha
	private void getMetadata(String delisuReport){
		String phrase, currName, currPckg = "", currDate, tempPckg;
		int i=0, j=0;;
		reportContent = FileOperations.readFileintoArray(delisuReport);		
		totalLicenses =0; 
		totalLicensesInUse=0; 
		petrelLicensesTotal =0; 
		petrelLicensesInUse=0;
		eclipseLicensesInUse=0; 
		eclipseLicensesTotal=0;
		//System.out.println("Zerei? "+ petrelLicensesInUse);
		while (i < reportContent.size()){
			phrase = reportContent.get(i);
			if(phrase.contains(Constants.licenseUsageIdentifier)){
				licenseUsage = true;
				licenseUser = false;
				licenseUsageList.add(phrase.replaceAll(";  Total of ", ", Used: ").replaceAll("Users of ", "")
						.replaceAll(" licenses issued", "").replaceAll(" licenses in use", "").replaceAll("Total of", "Total:")
						.replaceAll(" license in use", "").replaceAll(" license issued", "").replace("  ", " "));
				
				
				
			}
			if(phrase.contains(Constants.licenseUserIdentifier)){
				licenseUsage = false;
				licenseUser = true;
				currPckg =  phrase.substring((phrase.indexOf(Constants.licenseUserIdentifier) + 1),phrase.indexOf(Constants.licenseUserIdentifier + " "));
				
			}
			
			if(licenseUser && phrase.contains(Constants.userStartDateId)){
				//System.out.println(phrase.indexOf(Constants.userStartDateId));
				currDate = transformDate(phrase.substring((phrase.indexOf(Constants.userStartDateId)+Constants.userStartDateId.length()),phrase.length()));
				//System.out.println(phrase.substring(Constants.spaceUntilUser +1,phrase.length()).indexOf(" "));
				currName = phrase.substring(Constants.spaceUntilUser, (Constants.spaceUntilUser + phrase.substring(Constants.spaceUntilUser +1,phrase.length()).indexOf(" ")+1));
				reportMetadataList.add(new ReportMetadata(currName, currPckg, currDate));
			}
			
	 		i++;
	 	}
	 	i=0;
	 	/**
	 	 * licenseUsageList actual format:
	 	 * <nome do pacote da licenca>: (<Total de licencas:XX>,<Licencas em uso:XX>)
	 	 * EXEMPLO: 
	 	 * eclipse: (Total: 24, Used: 6)
	 	 */
	 	while (i< licenseUsageList.size()){
	 		tempPckg = licenseUsageList.get(i).substring(0, licenseUsageList.get(i).indexOf(":"));
	 		/**
	 		 * New format of licenseUsageList aggregates the old list with user using the licenses.
	 		 * Example:
	 		 * Petrel_22225777_MAAAIAAADEQQA: (Total: 14, Used: 11)
  					CSHC, 11/11
  					CTQ8, 11/11
  					CTQ8, 11/11
	 		 */
	 		while(j < reportMetadataList.size()){
	 			//System.out.println(tempPckg+ "," + reportMetadataList.get(j).getLicPackage());
	 			if(tempPckg.equals(reportMetadataList.get(j).getLicPackage())){
	 				licenseUsageList.set(i, (licenseUsageList.get(i) + Constants.newLine + "  "+reportMetadataList.get(j).toStringNameDate())); 
	 				System.out.println((licenseUsageList.get(i) + Constants.newLine + "  "+reportMetadataList.get(j).toStringNameDate()));
	 			}
	 			
	 			j++;
	 		}
	 		j=0;
	 		i++;
	 	}
	 	i=0;
	 	
	 	

	}
	
	private void totalLicenses(String contents){
		int temp;
		if(contents.contains("Total: ")){
			temp = Integer.parseInt(contents.substring((contents.indexOf("Total: ")+7), contents.indexOf(",")));
			totalLicenses =  totalLicenses + temp;
			if(contents.contains("Petrel")){
				petrelLicensesTotal = petrelLicensesTotal + temp;
			}

			
			if(contents.startsWith("eclipse:")||contents.startsWith("gaslift:")||contents.startsWith("lgr:")||contents.startsWith("networks:")
					||contents.startsWith("compositional:")||contents.startsWith("parallel:")||contents.startsWith("frontsim:")||contents.startsWith("multisegwells:")
					||contents.startsWith("rescoupling:")||contents.startsWith("polymers:")||contents.startsWith("foam:")||contents.startsWith("surfactant:")
					||contents.startsWith("solvents:")){
				System.out.println(" Conteudo atual:"+contents);
				eclipseLicensesTotal = eclipseLicensesTotal+ temp;
				
			}
			
			temp=0;		 
		}

	}
	
	private void computeLicensesInUse(String contents){
		int temp;
		
		if(contents.contains("Used: ")){
			temp =  Integer.parseInt(contents.substring(contents.indexOf("Used: ")+6, contents.indexOf(")")));
			totalLicensesInUse = totalLicensesInUse + temp;
			if(contents.contains("Petrel")){
				petrelLicensesInUse = petrelLicensesInUse+ temp;
				
			}
				
			if(contents.startsWith("eclipse:")||contents.startsWith("gaslift:")||contents.startsWith("lgr:")||contents.startsWith("network:")
					||contents.startsWith("compositional:")||contents.startsWith("parallel:")||contents.startsWith("frontsim:")||contents.startsWith("multisegwells:")
					||contents.startsWith("rescoupling:")||contents.startsWith("polymer:")||contents.startsWith("foam:")||contents.startsWith("surfactant:")
					||contents.startsWith("solvents:")){
				eclipseLicensesInUse = eclipseLicensesInUse+ temp;
				
			}
			temp=0;	
								
		}

	}
	
	private boolean createOutputFile(String outPath, String outFile) throws IOException{
	 	int i=0;
	 	while(i<licenseUsageList.size()){
	 		computeLicensesInUse(licenseUsageList.get(i));	 		
	 		totalLicenses(licenseUsageList.get(i));
	 		i++;
	 		
	 	}
	 	i=0;
	 	System.out.println("\nTotal Atual: "+petrelLicensesInUse);
	 	
	 	if(firstTime){
	 		fileOperation.writeCsv("Period,Total,Used,Petrel Lic, Petrel Used,Eclipse Lic, Eclipse Used," + Constants.newLine + currentDate("MM/dd/yyyy","HH:mm:ss") + ","+ totalLicenses+"," + totalLicensesInUse+ ","+ petrelLicensesTotal+ ","+ petrelLicensesInUse+","+ eclipseLicensesTotal+ ","+ eclipseLicensesInUse, "", currentDateSummary()+ "_RunSummary");		 	
	 	}else{
	 		fileOperation.writeCsv(currentDate("MM/dd/yyyy","HH:mm:ss") + ","+ totalLicenses+"," + totalLicensesInUse+ ","+ petrelLicensesTotal+ ","+ petrelLicensesInUse+","+ eclipseLicensesTotal+ ","+ eclipseLicensesInUse, "", (currentDateSummary()+ "_RunSummary"));	 		
	 	}
	 	
	 	while(i<reportMetadataList.size()){
	 		outputContent.append(reportMetadataList.get(i).toCSVString()  + Constants.newLine);
	 		//System.out.println("LICENSE USERS:"+ reportMetadataList.get(i).toString()+ "\n");
	 		i++;
	 	}
	 	i=0;
	 	//outputContent.append(Constants.newLine+ "==== END OF RUN ====" + Constants.newLine);
	 	//System.out.println(outputContent.toString());
	 
	 	fileOperation.writeCsv(outputContent.toString(), "", (currentDate("yyyy-MM-dd","HH:mm:ss")+"_" + outFile));
	 	outputContent = new StringBuilder();
	 	//Preenchimento de outputContent com a Listagem de Licenca em Uso
	 	while(i<licenseUsageList.size()){
	 		outputContent.append(licenseUsageList.get(i)  + Constants.newLine);
	 		i++;
	 	}
	 	i=0;
	 	fileOperation.writeFile(outputContent.toString(),  "LicenseSummary.txt");
	 	outputContent = new StringBuilder();
	 	//System.out.println("Criado o arquivo de saida: " + outFile );	 	
	 	System.gc();
 		return true;	
		
		
	}
	
	private void execBAT(String path) throws IOException{
		String line;  
		Process p = Runtime.getRuntime().exec(path);  
		BufferedReader input =new BufferedReader(new InputStreamReader(p.getInputStream()));  
		while ((line = input.readLine()) != null) {  
		  System.out.println(line);  
		}  
		input.close();  
	}

	private String currentDate(String dateFormat, String hourFormat){
		String dataAtual = new SimpleDateFormat(dateFormat).format( new Date(System.currentTimeMillis()));  
		String horaAtual  = new SimpleDateFormat(hourFormat).format( new Date(System.currentTimeMillis()));  
		return dataAtual + "_" + horaAtual;
		
	}
	
	private String transformDate(String originalDate){
		System.out.println("originalDate "+ originalDate);
		//String transformedDate = new SimpleDateFormat("dd/MM").format( new Date(Integer.parseInt(originalDate)));
		String transformedDate = originalDate.substring(4, 6).replace(" ", "") + "/" + originalDate.substring(7, 9).replace(" ", "");
		return transformedDate;
		
	}
	
 	private String currentDateSummary(){
		String dataAtual = new SimpleDateFormat("yyyy-MM-dd").format( new Date(System.currentTimeMillis()));  
		return dataAtual ;
	}
	

}
