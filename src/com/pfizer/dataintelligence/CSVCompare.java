package com.pfizer.dataintelligence;

import java.util.List;
import java.util.Map;

import com.pfizer.dataintelligence.adapter.ReadFile;
import com.pfizer.dataintelligence.adapter.WriteFile;
import com.pfizer.dataintelligence.config.ConfigBean;
import com.pfizer.dataintelligence.domain.CSVEntry;

public class CSVCompare {

	public static String fileRootPath = "D:\\sap-di\\data-reconcile";
	
	public static void main(String[] args) throws Exception {
		
		//fileRootPath = new java.io.File(".").getCanonicalPath();
		System.out.println(fileRootPath);
		
		//--load properties
		System.out.println("Reading properties file..");		
		ConfigBean.loadProperties(fileRootPath);

		System.out.println("-------------------------------------------------------");		
		//-read files
		List<CSVEntry> sourceEntries = new ReadFile().getRecords(fileRootPath.concat("//").concat(ConfigBean.getInstance().sourceDirRelativePath));
		System.out.println("Source entries > " + sourceEntries.size());
		List<CSVEntry> targetEntries = new ReadFile().getRecords(fileRootPath.concat("//").concat(ConfigBean.getInstance().targetDirRelativePath));
		System.out.println("Target entries > " + targetEntries.size());

//		WriteFile.write(fileRootPath, sourceEntries, targetEntries);

		System.out.println("Reduce called..");		
		Map<String, CSVEntry> source = ExecutionController.reduce(sourceEntries, new SLTReduce());
		System.out.println("Source entries after reduction > " + source.size());
		Map<String, CSVEntry> target = ExecutionController.reduce(targetEntries, new SLTReduce());
		System.out.println("Target entries after reduction > " + target.size());
		
		System.out.println("Comparing..");		
		ExecutionController.compare(source, target, fileRootPath);
	}

}
