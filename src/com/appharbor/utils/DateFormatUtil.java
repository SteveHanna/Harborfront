package com.appharbor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	
	public static Date parse(String date) throws ParseException{
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
	}

	public static String parseAndFormatJsonDate(String date) throws ParseException{
		Date parsedDate = parse(date);
		return new SimpleDateFormat("MMM d").format(parsedDate);
	}
}
