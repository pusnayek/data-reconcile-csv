package com.pfizer.dataintelligence.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigBean {
	
	static final String ignoreColumns_ = "IGNORE_COLUMNS";
	static final String keyColumns_ = "KEY_COLUMNS";
	static final String updateColumns_ = "UPDATE_COLS";
	static final String sourceDirRelativePath_ = "SOURCE_DIR";
	static final String targetDirRelativePath_ = "TARGET_DIR";
	
	public List<String> ignoreColumns;
	public List<String> keyColumns;
	public List<String> updateColumns;
	public String sourceDirRelativePath;
	public String targetDirRelativePath;

	private static ConfigBean INSTANCE = new ConfigBean();

	private static boolean propertiesLoaded = false;
	
	public static ConfigBean getInstance() {
		if(propertiesLoaded == false) {
			throw new RuntimeException("Properties not laoded yet..");
		}
		return INSTANCE;
	}
	
	private ConfigBean() {
//		
	}
	
	public static void loadProperties(String path) throws IOException {
		String propertiesFilePath = path.concat("//config.properties");

		Properties props = new Properties();
		props.load(new FileInputStream(new File(propertiesFilePath)));

		INSTANCE.ignoreColumns = Arrays.asList(props.getProperty(ignoreColumns_).split(","));
		INSTANCE.keyColumns = Arrays.asList(props.getProperty(keyColumns_).split(","));
		INSTANCE.updateColumns = Arrays.asList(props.getProperty(updateColumns_).split(","));
		INSTANCE.sourceDirRelativePath = props.getProperty(sourceDirRelativePath_);
		INSTANCE.targetDirRelativePath = props.getProperty(targetDirRelativePath_);
		
		propertiesLoaded = true;
		
		System.out.println("-------------------------------------------------------");		
		System.out.println("Ignore Columns > " + props.getProperty(ignoreColumns_));
		System.out.println("Key Columns > " + props.getProperty(keyColumns_));
		System.out.println("Update Columns > " + props.getProperty(updateColumns_));
		System.out.println("Source Dir Relative Path > " + props.getProperty(sourceDirRelativePath_));
		System.out.println("Target Dir Relative Path > " + props.getProperty(targetDirRelativePath_));
		System.out.println("-------------------------------------------------------");		
		
	}
	
	
}
