package com.cloudfire.until;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class DateUtil {
	
	//获取当前时间的一年以后时间
	public static String oneYear() {
		Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.add(Calendar.YEAR, +1);
        date = calendar.getTime();
        String format =  new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(date);
        return format;
	}
	
	//根据传入的时间再延长指定的时间
	public static String addDate(String dateParam, long day) throws ParseException {
        Date date =  new SimpleDateFormat(("yyyy-MM-dd kk:mm:ss")).parse(dateParam);
        long time = date.getTime(); // 得到指定日期的毫秒数
        day = day*24*60*60*1000; // 要加上的天数转换成毫秒数
        time+=day; // 相加得到新的毫秒数
        Date date1 = new Date(time); // 将毫秒数转换成日期
        String format =  new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(date1);
        return format;
    }
	
	//前面的参数大于后面的参数，得到的结果才能为正数
	public static int countDay(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

	//String类型转Date类型
	public static Date formatDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = format.parse(date);
        return date1;
    }
	
	public static String now() {
		return  new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(new Date(System.currentTimeMillis()));
	}
	
	
	public static void main(String[] args) throws ParseException {
		System.out.println(now());
		if(DateUtil.countDay(DateUtil.formatDate(now()), DateUtil.formatDate("2018-09-4 15:28:58")) == 10) {
			System.out.println("相差10天");
			
			
		}
	}
	
	public static List<String> removeRepeat(List<String> list) {   
	    HashSet<String> h = new HashSet<String>(list);   
	    list.clear();   
	    list.addAll(h);   
	    return list;   
	}  
}
