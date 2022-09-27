package com.pfizer.dataintelligence;

import java.util.Map;

import com.pfizer.dataintelligence.domain.CSVEntry;

public class SLTReduce implements IReduce {
	
	private static String UPDATE_COLUMN = "IUUC_OPERATION";
	private static String DELETE = "D";
	private static String UPDATE = "U";
	private static String INSERT = "I";

	@Override
	public void reduce(CSVEntry entry, Map<String, CSVEntry> map) {

		String key = entry.getKeysConcatenated();		
		CSVEntry storedEntry = map.get(key);
		
		if(storedEntry != null) {
			String operation = this.getOperation(entry);
			if(DELETE.equalsIgnoreCase(operation)) {
				map.remove(key);
				return;
			}
		}

		map.put(key, entry);
		
	}
	
	private String getOperation(CSVEntry entry) {
		Map<String, String> map = entry.updateColumnValues();
		String iuucOperation = map.get(UPDATE_COLUMN);
		return (iuucOperation == null) ? "" : iuucOperation.trim();
	}

}
