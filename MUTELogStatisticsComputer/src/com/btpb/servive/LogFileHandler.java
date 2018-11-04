package com.btpb.servive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.JProgressBar;

public class LogFileHandler {
	
	public static JProgressBar jb;

	public static ArrayList<String> readFile(File file) throws IOException {

		BufferedReader br;


			br = new BufferedReader(new FileReader(file));
			Object o[] = br.lines().map(String::toString).toArray();
			br.close();
			return (new ArrayList<String>(Arrays.asList(Arrays.copyOf(o, o.length, String[].class))));



	}

	public static HashMap<String, ArrayList<ArrayList<String>>> segregateScenarios(File directory) {

		File[] listOfLogFiles = new File[5];
		if(directory.isDirectory()){
			listOfLogFiles = directory.listFiles();
		}else{
			listOfLogFiles[0] = directory;
		}
		HashMap<String, ArrayList<ArrayList<String>>> scenarioSets = new HashMap<String, ArrayList<ArrayList<String>>>();
		Boolean init = true, prevInit = true;
		for (int i = 0; i < listOfLogFiles.length; i++) {
			
			if(!listOfLogFiles[i].getName().contains("SMM.log"))
				continue;
			
			ArrayList<String> lines;
			try {
				lines = readFile(listOfLogFiles[i]);
			} catch (IOException e) {
				
				continue;
			}

			for (int j = 0; j < lines.size(); j++) {

				if (lines.get(j).contains("Logger")) {

					int bt = j;
					while (!lines.get(bt).contains(".snf"))
						bt--;
					String name = lines.get(bt).substring(lines.get(bt).lastIndexOf("[") + 1,
							lines.get(bt).lastIndexOf("]"));
					if (!scenarioSets.containsKey(name)) {
						scenarioSets.put(name, new ArrayList<ArrayList<String>>());

					}
					ArrayList<String> set = new ArrayList<String>();
					bt = j+1;
					while (bt<lines.size()&&!lines.get(bt).contains("Logger")) {

						bt++;
					}

					while (bt<lines.size()&&!lines.get(bt).contains(".snf")) {

						bt--;
					}
					
					while (j < bt) {
						
						if(lines.get(j).contains("LE_INIT_5.0_MT_INF1_MT_INF2.snf")){
							init = true;
							do{
								if(lines.get(j).contains("FAILED")||lines.get(j).contains("INCONCLUSIVE")){
									init = false;
								}
							}while((++j)<lines.size()&&!lines.get(j).contains("C_SEM_ALL_INF2_BV_"));
							
							if(j<lines.size()&&(lines.get(j).contains("FAILED")||lines.get(j).contains("INCONCLUSIVE"))){
								init = false;
							}
							j++;
							continue;
							
						}
						
						
						
						set.add(lines.get(j));
						
						j++;
					}
					
					set.add(""+prevInit);
					prevInit = init;
					if(name.contains("LE_DDI_ADV_SCN_CR_BV_004.snf")){
						System.out.println(name);
					}
					scenarioSets.get(name).add(set);
					j--;
				}

			}
		}

		LogFileHandler.jb.setValue(LogFileHandler.jb.getValue()+25);
		
		return scenarioSets;

	}
	
	public static HashMap<String,String> checkScenarios(HashMap<String, ArrayList<ArrayList<String>>> scenarioSets){
		
		HashMap<String,String> checkScenario = new HashMap<String,String>();
		
		for(String scenario : scenarioSets.keySet()){

			checkScenario.put(scenario, "");
			for(ArrayList<String> logSet: scenarioSets.get(scenario)){
				
				if(checkScenario.get(scenario).equals("PASSED"))
					continue;

				for(int i = 0; i < logSet.size(); i++){
										
					if((logSet.get(i).contains("LC.snf")
							||logSet.get(i).contains("AEC.snf")
							||logSet.get(i).contains("PRIVACY_AE.snf")
							||logSet.get(i).contains("PRIVACY.snf"))){
						while((++i)<logSet.size()&&!logSet.get(i).contains("C_RLS"));
						continue;
						
					}
					
					if((logSet.get(i).contains("FAILED")||logSet.get(i).contains("INCONCLUSIVE"))&&logSet.get(logSet.size()-1).equals(""+false)){
						checkScenario.put(scenario, "STUCK");                     
						break;
					}else if((logSet.get(i).contains("FAILED")||logSet.get(i).contains("INCONCLUSIVE"))){
						checkScenario.put(scenario, "FAILED");
						break;
					}else if(logSet.get(i).contains("PASSED")
							&&(checkScenario.get(scenario).equals("")||checkScenario.get(scenario).equals("FAILED"))){
						checkScenario.put(scenario, "PASSED");
					}
					
				}
				
				
				
			}
			
			if(checkScenario.get(scenario).equals("")){
				checkScenario.put(scenario, "DID NOT RUN");
			}
			
			
			
		}
		
		LogFileHandler.jb.setValue(LogFileHandler.jb.getValue()+25);
		
		return checkScenario;
		
	}
	
	public static String generateStatistics(HashMap<String,String> checkScenario){
		
		int pass=0, fail=0, stuck=0, norun=0;
		
		String outputFile = "<html>"
				+ "<head>"
				+ "<title>MUTE Log Statistics</title>"
				+ "<STYLE>"
				+ "body  { FONT-FAMILY: Verdana;    FONT-SIZE:10pt; COLOR:blue;}"
				+ "table { FONT-FAMILY: Verdana;    FONT-SIZE: xx-small; COLOR:black;}"
				+ "</STYLE>"
				+ "</head>"
				+ "<body>"
				+ "<table  border=\"1\" width=\"100%\">"
				+ "<tr>"
				+ "<th align=\"center\"><b>Scenario name</b></th>"
				+ "<th align=\"center\"><b>Result</b></th>"
				+ "</tr>";
		
		Object o[] = checkScenario.keySet().toArray();
		List<String> keyList = Arrays.asList(Arrays.copyOf(o, o.length, String[].class));
		Collections.sort(keyList);
		for(String name:keyList){

			
			String color = "#ABEBC6";
			
			if(checkScenario.get(name).equals("PASSED"))
				pass++;
			else if(checkScenario.get(name).equals("FAILED")){
				fail++;
				color = "#E6B0AA";
			}
			else if(checkScenario.get(name).equals("STUCK")){
				stuck++;
				color = "#C2C2C2";
			}
			else{
				norun++;
				color = "#C2C2C2";
			}
			outputFile += "<tr bgcolor=\""+color+"\">"
					+ "<td>"+name+"</td>"
					+ "<td>"+checkScenario.get(name)+"</td>"
					+ "</tr>";
			
		}
		
		outputFile +="</table>\r\n"+
				"<table width=\"100%\" style=\"font-size:20px; border: 1px solid black\">"+
				"<tr><th>Log Statistics Report</th></tr>\r\n"+
				"<tr bgcolor=\"#EEEEE0\">\r\n"+
				"<td>Total </td>\r\n"+
				"<td>"+(pass+fail+stuck+norun)+"</td>\r\n"+
				"</tr>\r\n"+
				"<tr bgcolor=\"#EEEEE0\">\r\n"+
				"<td>Passed </td>\r\n"+
				"<td>"+pass+"</td>\r\n"+
				"</tr>\r\n"+
				"<tr bgcolor=\"#EEEEE0\">\r\n"+
				"<td>Failed </td>\r\n"+
				"<td>"+fail+"</td>\r\n"+
				"</tr>\r\n"+
				"<tr bgcolor=\"#EEEEE0\">\r\n"+
				"<td>Stuck </td>\r\n"+
				"<td>"+stuck+"</td>\r\n"+
				"</tr>\r\n"+
				"</table>\r\n</body></html>";
		
		LogFileHandler.jb.setValue(LogFileHandler.jb.getValue()+25);
		
		return outputFile;
				
	}
	
	public static void writeFile(String content, String path){
		
		try {
			FileWriter fw = new FileWriter(new File(path));
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LogFileHandler.jb.setValue(LogFileHandler.jb.getValue()+15);
	}
	
	
}
