package com.cloudfire.until;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class LogInfo {

    public static String getCurrentDate() {
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sm.format(new Date());
    }

    public static String strRight(String value) {
        return value.substring(value.length() - 2, value.length());
    }

    public static void appendLog(String newLog) {
        Scanner sc = null;
        PrintWriter pw = null;
        Calendar c = new GregorianCalendar();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
	    String od = sm.format(System.currentTimeMillis());
        File log = new File("D:\\repeaters\\logs\\loginfo" + String.valueOf(c.get(c.YEAR))
                + strRight("00" + String.valueOf(c.get(c.MONTH)+1)) + strRight("00" + String.valueOf(c.get(c.DAY_OF_MONTH))) + ".log");
        try {
            if (!log.exists())//����ļ�������,���½�.
            {
                File parentDir = new File(log.getParent());
                if (!parentDir.exists())//�������Ŀ¼������,���½�.
                {
                    parentDir.mkdirs();
                }
                log.createNewFile();
            }
            sc = new Scanner(log);
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine())//�ȶ������ļ�����,���ݴ�sb��;
            {
                sb.append(sc.nextLine());
                sb.append("\r\n");//���з���Ϊ���,ɨ������������,���Ҫ�Լ����.
            }
            sc.close();

            pw = new PrintWriter(new FileWriter(log), true);
            /*
             * A.
             */
            pw.println(sb.toString());//,д����ļ�����.
   /*
             * B.
             */
            pw.println(newLog + "  [" + getCurrentDate() + "]");//д������־.
   /*
             * �����д��A,�����־���ļ����. ������д��B,�����־���ļ���ǰ.
             */
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void appendLog(String newLog,String logFileName) {
        Scanner sc = null;
        PrintWriter pw = null;
        Calendar c = new GregorianCalendar();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
	    String recordTime = sm.format(System.currentTimeMillis());
	    String slash=File.separator; 
	    String path = System.getProperty("catalina.base")+slash+"logs"+slash+logFileName+slash+recordTime+".log";
//        File log = new File("D:\\repeaters\\logs\\"+logFileName+"\\"+recordTime+ ".log");
	    File log = new File(path);
        try {
            if (!log.exists())//����ļ�������,���½�.
            {
                File parentDir = new File(log.getParent());
                if (!parentDir.exists())//�������Ŀ¼������,���½�.
                {
                    parentDir.mkdirs();
                }
                log.createNewFile();
            }
            sc = new Scanner(log);
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine())//�ȶ������ļ�����,���ݴ�sb��;
            {
                sb.append(sc.nextLine());
                sb.append("\r\n");//���з���Ϊ���,ɨ������������,���Ҫ�Լ����.
            }
            sc.close();

            pw = new PrintWriter(new FileWriter(log), true);

            pw.println(sb.toString());//,д����ļ�����.

            pw.println(newLog);//д������־.

            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
