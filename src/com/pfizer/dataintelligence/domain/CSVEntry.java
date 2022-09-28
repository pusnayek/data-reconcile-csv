package com.pfizer.dataintelligence.domain;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pfizer.dataintelligence.config.ConfigBean;

public class CSVEntry {
	
	public Map<String, String> entries = new HashMap<String, String>();
	
	public CSVEntry(final List<String> headers, List<String> tokens) {
		List<String> ignoreColumns = ConfigBean.getInstance().ignoreColumns;
		
		for (int index = 0, length = headers.size(), _token = tokens.size(); index < length; index++) {
			String fieldName = headers.get(index);
			if(!ignoreColumns.contains(fieldName)) {
				//String token = (index < _token) ? tokens.get(index) : null;
				String token = tokens.get(index);
				entries.put(fieldName.trim(), token);
				
				if("MANDT".equalsIgnoreCase(fieldName) && !token.equalsIgnoreCase("226")) {
					throw new RuntimeException("Entry is not read well");
				}
			}
		}
		//-check keys
		this.checkKeys();
	}
	
	public void checkKeys() {
		List<String> keyColumns = ConfigBean.getInstance().keyColumns;
		/*
		for (Iterator iterator = entries.keySet().iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			for(int i = 0; i < string.length(); i++) {
				System.out.print((int)string.charAt(i) + "-");
			}
			System.out.println(" > " + string + " HashCode > " + string.hashCode() + " Length >" + string.length());
		}*/
		for (Iterator<String> iterator = keyColumns.iterator(); iterator.hasNext();) {
			String keyName = iterator.next();
			String thisFieldValue = this.getField(keyName.trim());			
			if(thisFieldValue == null || thisFieldValue.length() == 0) {
				throw new RuntimeException("Error as key is null > " + keyName + " HashCode > " + keyName.hashCode() + " Length > " + keyName.length());
			}
		}
	}
	
	public String getKeysConcatenated() {
		List<String> keyColumns = ConfigBean.getInstance().keyColumns;
		StringBuffer keyBuff = new StringBuffer();
		
		for (Iterator<String> iterator = keyColumns.iterator(); iterator.hasNext();) {
			String keyName = iterator.next();
			String thisFieldValue = this.getField(keyName);			
			if(thisFieldValue == null || thisFieldValue.length() == 0) {
				throw new RuntimeException("Error as key is null > " + keyName);
			}
//			System.out.print(keyName + " > " + thisFieldValue);
			keyBuff.append(thisFieldValue.replaceFirst("^0+(?!$)", ""));
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
		List<String> dateColumns = ConfigBean.getInstance().dateColumns;
		
		for (; columns.hasNext();) {
			String fieldName = (String) columns.next();
			String thisFieldValue = this.getField(fieldName);			
			String thatFieldValue = anotherEntry.getField(fieldName);

			thisFieldValue = thisFieldValue == null ? "" : thisFieldValue.replaceAll("^\"|\"$", "").trim();
			thatFieldValue = thatFieldValue == null ? "" : thatFieldValue.replaceAll("^\"|\"$", "").trim();
			
			if(!thisFieldValue.equalsIgnoreCase(thatFieldValue)) {
				//-further checks
				if(dateColumns.contains(fieldName)) {
					if(!dateEqual(thisFieldValue, thatFieldValue)) {
						System.out.println("Date > " + fieldName + " > " + thisFieldValue + " > " + thatFieldValue);
						equals = false;
					}
				} else if(ConfigBean.getInstance().numberPattern.matcher(thisFieldValue).matches()
							|| ConfigBean.getInstance().numberPattern.matcher(thatFieldValue).matches()) {
					try {
						double sourceDoubleValue = thisFieldValue.length() == 0 ? 0 : 
							ConfigBean.getInstance().numberFormat.parse(thisFieldValue).doubleValue();
						double targetDoubleValue = thatFieldValue.length() == 0 ? 0 : 
							ConfigBean.getInstance().numberFormat.parse(thatFieldValue).doubleValue();
						double valueDifference =  sourceDoubleValue - targetDoubleValue;
						if(valueDifference > 0.01) {
							System.out.println("Double > " + fieldName + " > " + sourceDoubleValue + " > " + targetDoubleValue 
										+ " Difference > "  + valueDifference);
							equals = false;
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("String > " + fieldName + " > " + thisFieldValue + " > " + thatFieldValue);
					equals = false;
				}
			}
		}
		
		return equals;		
	}
	
	public static boolean dateEqual(String sourceValue, String targetValue) {
		String sourceDateString = (sourceValue == null || sourceValue.trim().length() == 0 
				|| "00/00/0000".equalsIgnoreCase(sourceValue) 
				|| "99/99/9999".equalsIgnoreCase(sourceValue)) ? "" : sourceValue;
		String targetDateString = (targetValue == null || targetValue.trim().length() == 0 
				|| "9999-99-99".equalsIgnoreCase(targetValue)) ? "" : targetValue;
		
		if(sourceDateString.equalsIgnoreCase(targetDateString)) {
			return true;
		}

		if(sourceDateString.length() < 8 || targetDateString.length() < 8) {
			return false;
		}

		try {
			Date sourceDate = ConfigBean.getInstance().srcDateFormatter.parse(sourceDateString);
			Date targetDate = ConfigBean.getInstance().trgDateFormatter.parse(targetDateString);
//			System.out.println("Date > " + sourceDateString + " > " + sourceDate + " > " + targetDateString + " > " + targetDate);
			if(sourceDate.equals(targetDate)) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
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
