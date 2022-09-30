package com.pfizer.dataintelligence.domain;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pfizer.dataintelligence.config.ConfigBean;

public class CSVEntry {
	
	public Map<String, String> entries = new HashMap<String, String>();
	
	public static CSVEntry create(final List<String> headers, List<String> tokens) throws IllegalArgumentException {
		CSVEntry entry = new CSVEntry(headers, tokens);
		entry.checkKeys();
		return entry;
	}
	
	public CSVEntry(final List<String> headers, List<String> tokens) {
		List<String> ignoreColumns = ConfigBean.getInstance().ignoreColumns;
		
		for (int index = 0, length = headers.size(), _token = tokens.size(); index < length; index++) {
			String fieldName = headers.get(index);
			if(!ignoreColumns.contains(fieldName)) {
				//String token = (index < _token) ? tokens.get(index) : null;
//				if(index == _token) {
//					System.out.println("Index out of bounds > " + this.getKeysConcatenated());
//					break;
//				}
				String token = tokens.get(index);
				entries.put(fieldName.trim(), token);
			}
		}
		//-check keys
		this.checkKeys();
	}
	//-check primary keys
	public void checkKeys()  throws IllegalArgumentException {
		List<String> keyColumns = ConfigBean.getInstance().keyColumns;
		for (Iterator<String> iterator = keyColumns.iterator(); iterator.hasNext();) {
			String keyName = iterator.next();
			String thisFieldValue = this.getField(keyName.trim());			
			if(thisFieldValue == null || thisFieldValue.length() == 0) {
				throw new IllegalArgumentException("Error as key is null > " + keyName);
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
//				throw new RuntimeException("Error as key is null > " + keyName);
			}
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
	
	public String getField(String fieldName) {
		String fieldValue = entries.get(fieldName);
		fieldValue = fieldValue == null ? "" : fieldValue;
		return fieldValue;
	}
	
	public boolean compareKeys(CSVEntry anotherEntry) throws Exception {
		return compareFields(anotherEntry, ConfigBean.getInstance().keyColumns.iterator());
	}
	
	public boolean compare(CSVEntry anotherEntry) throws Exception {
		return compareFields(anotherEntry, this.entries.keySet().iterator());
	}
	
	private boolean compareFields(CSVEntry anotherEntry, Iterator<String> columns) throws Exception{
		List<String> dateColumns = ConfigBean.getInstance().dateColumns;
		List<String> timeColumns = ConfigBean.getInstance().timeColumns;
		
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
						throw new Exception("Date comparison for field name ( " + fieldName + " )> Source value: " + thisFieldValue + " & Target value: " + thatFieldValue);
					}
				} else if(timeColumns.contains(fieldName)) {
					if(!timeEqual(thisFieldValue, thatFieldValue)) {
						throw new Exception("Time comparison for field name ( " + fieldName + " )> Source value: " + thisFieldValue + " & Target value: " + thatFieldValue);
					}
				} else if(ConfigBean.getInstance().numberPattern.matcher(thisFieldValue).matches()
							|| ConfigBean.getInstance().numberPattern.matcher(thatFieldValue).matches()) {
					double sourceDoubleValue = thisFieldValue.length() == 0 ? 0 : 
						ConfigBean.getInstance().numberFormat.parse(thisFieldValue).doubleValue();
					double targetDoubleValue = thatFieldValue.length() == 0 ? 0 : 
						ConfigBean.getInstance().numberFormat.parse(thatFieldValue).doubleValue();
					double valueDifference =  sourceDoubleValue - targetDoubleValue;
					if(valueDifference > 0.01) {
						throw new Exception("Numeric comparison for field name ( " + fieldName + " )> Source value: " + thisFieldValue + " & Target value: " + thatFieldValue);
					}
				} else {
					throw new Exception("String comparison for field name ( " + fieldName + " )> Source value: " + thisFieldValue + " & Target value: " + thatFieldValue);
				}
			}
		}
		
		return true;		
	}
	
	public static boolean dateEqual(String sourceValue, String targetValue) throws ParseException {
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

		Date sourceDate = ConfigBean.getInstance().srcDateFormatter.parse(sourceDateString);
		Date targetDate = ConfigBean.getInstance().trgDateFormatter.parse(targetDateString);
		if(sourceDate.equals(targetDate)) {
			return true;
		}

		return false;
	}

	public static boolean timeEqual(String sourceValue, String targetValue) throws ParseException {
		String sourceDateString = (sourceValue == null || sourceValue.trim().length() == 0  
				|| "99:99:99".equalsIgnoreCase(sourceValue)) ? "00:00:00" : sourceValue;
		String targetDateString = (targetValue == null || targetValue.trim().length() == 0   
				|| "99:99:99.999".equalsIgnoreCase(targetValue)) ? "00:00:00.000" : targetValue;
		
		if(sourceDateString.equalsIgnoreCase(targetDateString)) {
			return true;
		}

		//-source time and target time
		String sourceTImeFormatted = ConfigBean.timeFormatter.format(ConfigBean.getInstance().srcTimeFormatter.parse(sourceDateString));
		String targetTImeFormatted = ConfigBean.timeFormatter.format(ConfigBean.getInstance().trgTimeFormatter.parse(targetDateString));
		if(sourceTImeFormatted.equals(targetTImeFormatted)) {
			return true;
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
	
	public String getFieldsInfo(Iterator<String> headers) {
		StringBuffer buf = new StringBuffer();
		for (; headers.hasNext();) {
			String keyName = headers.next();
			String thisFieldValue = this.getField(keyName);	
			buf.append("\"");
			buf.append(thisFieldValue);
			buf.append("\"");
			buf.append(",");
		}
		return buf.toString();
	}	
	
	public Set<String> getHeaderFields() {
		return this.entries.keySet();
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Iterator<String> columns = this.entries.keySet().iterator(); columns.hasNext();) {
			String keyName = columns.next();
			String thisFieldValue = this.getField(keyName);	
			buf.append(keyName);
			buf.append("=");
			buf.append("\"");
			buf.append(thisFieldValue);
			buf.append("\"");
			buf.append(",");
		}
		return buf.toString();
	}
	
}
