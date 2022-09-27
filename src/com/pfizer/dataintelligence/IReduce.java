package com.pfizer.dataintelligence;

import java.util.Map;

import com.pfizer.dataintelligence.domain.CSVEntry;

public interface IReduce {

	public void reduce(CSVEntry entry, Map<String, CSVEntry> map);
}
