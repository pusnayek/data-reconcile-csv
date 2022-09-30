package com.pfizer.dataintelligence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pfizer.dataintelligence.adapter.WriteFile;
import com.pfizer.dataintelligence.domain.CSVEntry;

public class ExecutionController {
	
	public static Map<String, CSVEntry> reduce(List<CSVEntry> entries, IReduce reducer) {
		Map<String, CSVEntry> map = new HashMap<String, CSVEntry>();
		
		for (Iterator<CSVEntry> iterator = entries.iterator(); iterator.hasNext();) {
			CSVEntry entry = iterator.next();
			reducer.reduce(entry, map);
		}
		
		return map;
	}
	
	public static void compare(Map<String, CSVEntry> source, Map<String, CSVEntry> target, String fileRootPath) {
		List<CSVEntry> matchedEntries = new ArrayList<>();
		List<CSVEntry> unmatchedEntries = new ArrayList<>();
		
		for (Iterator<Map.Entry<String, CSVEntry>> iterator = source.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, CSVEntry> sourceMapEntry = iterator.next();
			String sourceKey = sourceMapEntry.getKey();
			CSVEntry sourceEntry = sourceMapEntry.getValue();			
			CSVEntry targetEntry = target.get(sourceKey);
			
			if(sourceEntry != null && targetEntry == null) {
				unmatchedEntries.add(sourceEntry);
			} 
			
			if(sourceEntry != null && targetEntry != null) {
				try {
					if(sourceEntry.compare(targetEntry)) {
						matchedEntries.add(sourceEntry);
					}
				} catch(Exception e) {
					System.out.println("Not matched for key (" + sourceKey + ") > " + e.getMessage());
					unmatchedEntries.add(sourceEntry);				
				} finally {
					target.remove(sourceKey);
				}
			}
		}
		
		System.out.println("Number of entries in target and not found in source > " + target.entrySet().size());
		for (Iterator<Map.Entry<String, CSVEntry>> iterator = target.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, CSVEntry> targetMapEntry = iterator.next();
			String sourceKey = targetMapEntry.getKey();
			CSVEntry targetEntry = targetMapEntry.getValue();			
			unmatchedEntries.add(targetEntry);
		}
		
		System.out.println("Number of entries matched > " + matchedEntries.size());
		System.out.println("Number of entries did not match > " + unmatchedEntries.size());
		//-write entries
		WriteFile.write(fileRootPath, matchedEntries, unmatchedEntries);
	}
	
}
