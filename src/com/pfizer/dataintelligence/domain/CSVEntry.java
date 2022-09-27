package com.pfizer.dataintelligence.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pfizer.dataintelligence.config.ConfigBean;

public class CSVEntry {
	
	public Map<String, String> entries = new HashMap<String, String>();
	
	public CSVEntry(List<String> headers, List<String> tokens) {
		List<String> ignoreColumns = ConfigBean.getInstance().ignoreColumns;
		
		for (int index = 0, length = headers.size(), _token = tokens.size(); index < length; index++) {
			String fieldName = headers.get(index);
			if(!ignoreColumns.contains(fieldName)) {
				String token = (index < _token) ? tokens.get(index) : null;
				entries.put(fieldName, token);
			}
		}
	}
	
	public String getKeysConcatenated() {
		List<String> keyColumns = ConfigBean.getInstance().keyColumns;
		StringBuffer keyBuff = new StringBuffer();
		
		for (Iterator<String> iterator = keyColumns.iterator(); iterator.hasNext();) {
			String thisFieldValue = this.getField(iterator.next());			
			keyBuff.append(thisFieldValue);
		}
		
		return keyBuff.toString();
	}

	public Map<String, String> updateColumnValues() {
		List<String> updateColumns = ConfigBean.getInstance().updateColumns;
		Map<String, String> map = new HashMap<String, String>();
		
		for (Iterator<String> iterator = updateColumns.iterator(); iterator.hasNext();) {
			String fieldName = iterator.next();
			String thisFieldValue = this.getField(fieldName);			
			map.put(fieldName, thisFieldValue);
		}
		
		return map;
	}
	
	private String getField(String fieldName) {
		String fieldValue = entries.get(fieldName);
		fieldValue = fieldValue == null ? "" : fieldValue;
		return fieldValue;
	}
	
	public boolean compareKeys(CSVEntry anotherEntry) {
		return compareFields(anotherEntry, ConfigBean.getInstance().keyColumns.iterator());
	}
	
	public boolean compare(CSVEntry anotherEntry) {
		return compareFields(anotherEntry, this.entries.keySet().iterator());
	}
	
	private boolean compareFields(CSVEntry anotherEntry, Iterator<String> columns) {
		boolean equals = true;
		for (; columns.hasNext();) {
			String fieldName = (String) columns.next();
			String thisFieldValue = this.getField(fieldName);			
			String thatFieldValue = anotherEntry.getField(fieldName);

			thisFieldValue = thisFieldValue == null ? "" : thisFieldValue;
			thatFieldValue = thatFieldValue == null ? "" : thatFieldValue;
			
			if(!thatFieldValue.equalsIgnoreCase(thisFieldValue)) {
				equals = false;
			}
		}
		
		return equals;		
	}
	
	public String getHeaders() {
		StringBuffer buf = new StringBuffer();
		for (Iterator<String> columns = this.entries.keySet().iterator(); columns.hasNext();) {
			buf.append(columns.next());
			buf.append(",");
		}
		return buf.toString();
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Iterator<String> columns = this.entries.keySet().iterator(); columns.hasNext();) {
			String thisFieldValue = this.getField(columns.next());		
			buf.append(thisFieldValue);
			buf.append(",");
		}
		return buf.toString();
	}
	
}
