package com.cloudfire.until;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class DateUtil {
	
	//��ȡ��ǰʱ���һ���Ժ�ʱ��
	public static String oneYear() {
		Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.add(Calendar.YEAR, +1);
        date = calendar.getTime();
        String format =  new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(date);
        return format;
	}
	
	//���ݴ����ʱ�����ӳ�ָ����ʱ��
	public static String addDate(String dateParam, long day) throws ParseException {
        Date date =  new SimpleDateFormat(("yyyy-MM-dd kk:mm:ss")).parse(dateParam);
        long time = date.getTime(); // �õ�ָ�����ڵĺ�����
        day = day*24*60*60*1000; // Ҫ���ϵ�����ת���ɺ�����
        time+=day; // ��ӵõ��µĺ�����
        Date date1 = new Date(time); // ��������ת��������
        String format =  new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(date1);
        return format;
    }
	
	//ǰ��Ĳ������ں���Ĳ������õ��Ľ������Ϊ����
	public static int countDay(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

	//String����תDate����
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
			System.out.println("���10��");
			
			
		}
	}
	
	public static List<String> removeRepeat(List<String> list) {   
	    HashSet<String> h = new HashSet<String>(list);   
	    list.clear();   
	    list.addAll(h);   
	    return list;   
	}  
}
