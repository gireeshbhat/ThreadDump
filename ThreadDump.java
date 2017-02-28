package com.dh.dump;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadDump {
	public static void main(String[] args) throws IOException {
		ThreadDump td = new ThreadDump();
		System.out.println("argument0 "+ args[0]);
		System.out.println("argument1 "+ args[1]);
		td.doThreadDump(args[0], args[1]);

	}

	private void doThreadDump(String currentDir, String tdPath) {
		
		String pid = getPscPid(currentDir);

		if (pid != null) {
			String command[] = { "cmd", "/c", currentDir + "jre\\bin\\java -classpath ", currentDir + "tools.jar",
					"sun.tools.jstack.JStack", "-l", pid };
			ProcessBuilder process = new ProcessBuilder(command);
			Process p = null;
			BufferedWriter destfile = null;
			BufferedReader srcfile = null;
			try {
				p = process.start();
				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy_kk-mm");
				String dateformat = sdf.format(new Date());
				String absPath = tdPath + File.separator + "ThreadDump_" + dateformat + ".txt";
				File file = new File(absPath);

				if (!file.exists()) {
					file.createNewFile();
				}
				destfile = new BufferedWriter(new FileWriter(file));
				srcfile = new BufferedReader(new InputStreamReader(p.getInputStream()));

				int index = 0;
				char[] data = new char[1024 * 8];
				while ((index = srcfile.read(data, 0, data.length)) != -1) {
					destfile.write(data, 0, index);
					destfile.flush();
				}
				System.out.println(" \n ******************************************************");
				System.out.println("\n  Thread dump is created successfully in the below Path: \n" + absPath);
				System.out.println(" ******************************************************");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					destfile.close();
					srcfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
		}
		else {
			System.out.println("\nPSC is not running, hence failed to take thread dump.\n");
		}
	}

	private String getPscPid(String currentDir) {

		String command[] = { "cmd", "/c", currentDir+"jre\\bin\\java -classpath ", currentDir + "tools.jar", "sun.tools.jps.Jps" };

		ProcessBuilder process = new ProcessBuilder(command);
		Process p = null;

		BufferedReader srcfile = null;

		try {
			p = process.start();
			srcfile = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";

			while ((line = srcfile.readLine()) != null) {
				String[] processInfo = line.split(" ");
				if (processInfo != null && processInfo.length > 1) {
					System.out.println(processInfo[0] + " " + processInfo[1]);

					if (processInfo[1].equals("ClientSystemLoaderMinimizer")) {
						return processInfo[0];
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {

				srcfile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
