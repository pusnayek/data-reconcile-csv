package com.pfizer.dataintelligence.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.pfizer.dataintelligence.domain.CSVEntry;

public class ReadFile {
	
	List<CSVEntry> records = new ArrayList<CSVEntry>(); 

	public List<CSVEntry> getRecords(String directory) {		
		File [] files = new File(directory).listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File path) {
		        if(path.isFile()) {
		        	System.out.println("File selected for reading > " + path.getAbsolutePath());
		        	ReadFile.readEntrysFromCSV(path, records);
		        	return true;
		        }
		        return false;
		    }
		}); 
		
		return records;
	}
	
	private static List<CSVEntry> readEntrysFromCSV(File file, List<CSVEntry> entries) { 
		Path pathToFile = Paths.get(file.getAbsolutePath()); 
		
		// create an instance of BufferedReader 
		// using try with resource, Java 7 feature to close resources 
		int lineNum = 0;
		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8)) { 
			// read the first line from the text file 
			String headerLine = br.readLine(); 
			headerLine = headerLine.replaceAll("/[^0-9A-Za-z,/_]/g", "");
			List<String> headerStrings = (headerLine != null) ? Arrays.asList(headerLine.split(",")) : null;
			if(headerStrings == null) {
				System.err.println("No entry in file > " + file.getAbsolutePath());
				return entries;
			}
			
			List<String> headers = new ArrayList<>();
			for (Iterator iterator = headerStrings.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				headers.add(string.trim());
			}
			
			// loop until all lines are read 
			String line = br.readLine(); 
			while (line != null) { 
				lineNum++;
				//System.out.print("Reading line .." + lineNum);
				// use string.split to load a string array with the values from 
				// each line of // the file, using a comma as the delimiter 
				String[] attributes = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); 
				// adding Entry into ArrayList 
				try {
					CSVEntry entry = CSVEntry.create(headers, Arrays.asList(attributes));
					entries.add(entry);  
				} catch(IllegalArgumentException e) {
					System.out.println("Error at line  " + lineNum + ": " + e.getMessage());
				}
				
				// if end of file reached, line would be null 
				try {
					line = br.readLine();
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
//					e.printStackTrace();
					System.out.println("Issue found with " + lineNum + " continuing with next lines >");
					line = br.readLine();
				} 
			} 
		} catch(Exception e) { 
//			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
			throw new RuntimeException("Cannot continue due to error .. please fix before contnuing ..");
		}
		return entries; 
	} 
	
}
