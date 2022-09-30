package com.pfizer.dataintelligence.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

public class ConfigBean {
	
	static final String ignoreColumns_ = "IGNORE_COLUMNS";
	static final String keyColumns_ = "KEY_COLUMNS";
	static final String updateColumns_ = "UPDATE_COLS";
	static final String sourceDirRelativePath_ = "SOURCE_DIR";
	static final String targetDirRelativePath_ = "TARGET_DIR";
	
	static final String dateColumns_ = "DATE_COLS";
	static final String timeColumns_ = "TIME_COLS";
	static final String srcDateFormat_ = "SRC_DATE_FORMAT";
	static final String trgDateFormat_ = "TRG_DATE_FORMAT";
	static final String srcTimeFormat_ = "SRC_TIME_FORMAT";
	static final String trgTimeFormat_ = "TRG_TIME_FORMAT";
	
	public static final Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d+)?");	
	public static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
	public static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");;
	public static final SimpleDateFormat timeFormatterLong = new SimpleDateFormat("HH:mm:ss.SSS");;
	
	public List<String> ignoreColumns;
	public List<String> keyColumns;
	public List<String> updateColumns;
	public List<String> dateColumns;
	public List<String> timeColumns;
	public String sourceDirRelativePath;
	public String targetDirRelativePath;
	public String srcDateFormat;
	public String trgDateFormat;
	public String srcTimeFormat;
	public String trgTimeFormat;
	
	public SimpleDateFormat srcDateFormatter;
	public SimpleDateFormat trgDateFormatter;
	public SimpleDateFormat srcTimeFormatter;
	public SimpleDateFormat trgTimeFormatter;

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
		INSTANCE.dateColumns = Arrays.asList(props.getProperty(dateColumns_).split(","));
		INSTANCE.timeColumns = Arrays.asList(props.getProperty(timeColumns_).split(","));
		INSTANCE.sourceDirRelativePath = props.getProperty(sourceDirRelativePath_);
		INSTANCE.targetDirRelativePath = props.getProperty(targetDirRelativePath_);
		INSTANCE.srcDateFormat = props.getProperty(srcDateFormat_);
		INSTANCE.trgDateFormat = props.getProperty(trgDateFormat_);
		INSTANCE.srcTimeFormat = props.getProperty(srcTimeFormat_);
		INSTANCE.trgTimeFormat = props.getProperty(trgTimeFormat_);
		INSTANCE.srcDateFormatter = new SimpleDateFormat(INSTANCE.srcDateFormat);
		INSTANCE.trgDateFormatter = new SimpleDateFormat(INSTANCE.trgDateFormat);
		INSTANCE.srcTimeFormatter = new SimpleDateFormat(INSTANCE.srcTimeFormat);
		INSTANCE.trgTimeFormatter = new SimpleDateFormat(INSTANCE.trgTimeFormat);
		propertiesLoaded = true;
		
		System.out.println("-------------------------------------------------------");		
		System.out.println("Ignore Columns > " + props.getProperty(ignoreColumns_));
		System.out.println("Key Columns > " + props.getProperty(keyColumns_));
		System.out.println("Update Columns > " + props.getProperty(updateColumns_));
		System.out.println("Source Dir Relative Path > " + props.getProperty(sourceDirRelativePath_));
		System.out.println("Target Dir Relative Path > " + props.getProperty(targetDirRelativePath_));
		
	}
	
	public static void main(String[] args) {
		System.out.println(numberPattern.matcher("0").matches());
		double value = 0.0;
		System.out.println((value < 0.01));
	}
	
}
