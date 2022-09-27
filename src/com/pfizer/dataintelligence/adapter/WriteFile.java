package com.pfizer.dataintelligence.adapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;

import com.pfizer.dataintelligence.domain.CSVEntry;

public class WriteFile {
	
	public static void write(String fileRootPath, List<CSVEntry> matchedEntries, List<CSVEntry> unmatchedEntries) {
		try {
			write(fileRootPath.concat("//").concat("Matched Enties.csv"), matchedEntries);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			write(fileRootPath.concat("//").concat("Unmatched Enties.csv"), unmatchedEntries);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void write(String filePath, List<CSVEntry> entries) throws IOException {
	    File file = new File(filePath);
	    BufferedWriter bufWriter = null;
	    boolean firstTime = true;
	    try {
		    bufWriter = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		    
		    for (Iterator<CSVEntry> iterator = entries.iterator(); iterator.hasNext();) {
				 CSVEntry csvEntry = iterator.next();
				 if(firstTime) {
					 bufWriter.write(csvEntry.getHeaders());
					 bufWriter.newLine();
					 firstTime = false;
				 }
				 bufWriter.write(csvEntry.toString());
				 bufWriter.newLine();
			}
	    }
	    finally {
	    	if(bufWriter != null) {
	    		bufWriter.close();
	    	}
	    }		
	}
	
}
