package com.btpb.servive;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MUTEWorkspaceHandler {

	private static String workspacePath;

	public MUTEWorkspaceHandler(String path) {
		workspacePath = path;
	}

	public void addCheckpoints(String ssnFile) {

		String content = "START_TC  \"Checkpoint\"\r\n" + "PRINT:\r\n"
				+ "    LOG,                              ,       , "
				+ "        ,                    ,         ,         "
				+ "                 , \"This checkpoint marks start of scenario.\"\r\n"
				+ "DELAY,                            ,       ,         ,                    ,        1,   PASSED,\r\n"
				+ "END_TC";

		LogFileHandler.writeFile(content, workspacePath + "\\Testcases\\smd\\Logger.smd");

		File ssnDirectory = new File(workspacePath + "\\Testcases\\ssn\\");

		for (File ssn : ssnDirectory.listFiles()) {

			ArrayList<String> ssnLines;
			try {
				ssnLines = LogFileHandler.readFile(ssn);
			} catch (IOException e) {
				
				e.printStackTrace();
				continue;
			}

			for (int index = 0; index < ssnLines.size(); index++) {

				String line = ssnLines.get(index);

				if (line.contains("CONFIG") || line.contains("INIT"))
					continue;

				if (line.trim().endsWith(".snf") && !line.trim().startsWith("%")) {

					ArrayList<String> snfLines;
					try {
						snfLines = LogFileHandler
								.readFile(new File(workspacePath + "\\Testcases\\snf\\" + line));
					} catch (IOException e) {
						
						e.printStackTrace();
						continue;
					}

					ArrayList<String> addLines = new ArrayList<String>();

					if ((snfLines.size() > 0 && snfLines.get(0).contains("Logger.smd")))
						continue;

					Boolean smdPresent = false;

					for (String snfLine : snfLines) {

						if (snfLine.contains("C_RLS"))
							continue;

						if (snfLine.trim().endsWith(".smd") && !snfLine.trim().startsWith("%")) {
							smdPresent = true;
							break;
						} else if (snfLine.trim().endsWith(".snf") && !snfLine.trim().startsWith("%")) {
							addLines.add(snfLine);
						}
					}

					if (smdPresent) {

						snfLines.add(0, "Logger.smd");

						LogFileHandler.writeFile(String.join("\r\n", snfLines),
								(workspacePath + "\\Testcases\\snf\\" + line));

					} else {
						ssnLines.addAll(addLines);
					}

				}

			}
		}

	}

	public void clearCheckpoints() {

		File logger = new File(workspacePath + "\\Testcases\\smd\\Logger.smd");

		logger.delete();

		File snfDirectory = new File(workspacePath + "\\Testcases\\snf");

		File[] snfList = snfDirectory.listFiles();

		for (int index = 0; index < snfList.length; index++) {

			String line = snfList[index].getName();
			if (line.trim().endsWith(".snf") && !line.trim().startsWith("%")) {

				ArrayList<String> snfLines;
				try {
					snfLines = LogFileHandler.readFile(snfList[index]);
				} catch (IOException e) {

					e.printStackTrace();
					continue;
				}

				if (snfLines.size() > 0 && snfLines.get(0).contains("Logger.smd")) {
					snfLines.remove(0);
					LogFileHandler.writeFile(String.join("\r\n", snfLines),
							(workspacePath + "\\Testcases\\snf\\" + line));

				}

			}

		}

	}

}
