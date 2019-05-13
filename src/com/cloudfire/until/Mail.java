package com.cloudfire.until;


import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class Mail {
	
	/**
	 * �����ʼ���SMT
	 * @param email,���������ַ
	 * @param emailSub,�ʼ�����
	 * @param emailMsg,�ʼ�����
	 * @throws Exception
	 */
	public static void sendmail(String email,String emailSub,String emailMsg) throws Exception{
		 // 1.����һ���������ʼ��������Ự���� Session
		 Properties props = new Properties();
		 props.setProperty("mail.transport.protocol", "SMTP");
		 props.setProperty("mail.smtp.host", "smtp.163.com");
//		 props.setProperty("mail.smtp.host", "pop.163.com");
		 props.setProperty("mail.smtp.port", "25");
		 // ָ����֤Ϊtrue
		 props.setProperty("mail.smtp.auth", "true");
		 props.setProperty("mail.smtp.timeout","1000");
		 // ��֤�˺ż����룬������Ҫ�ǵ�������Ȩ��
		 Authenticator auth = new Authenticator() {
			 public PasswordAuthentication getPasswordAuthentication(){
			    return new PasswordAuthentication("18011715889@163.com", "119shouquan");
            }
        };
        
		 Session session = Session.getInstance(props, auth);
		 // ����debugģʽ�����Կ���������ϸ��������־
		 session.setDebug(true);

		// 2.����һ��Message�����൱�����ʼ�����
		Message message = new MimeMessage(session);
		// ���÷�����
		message.setFrom(new InternetAddress("18011715889@163.com"));
		// ���÷��ͷ�ʽ�������
		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
		// ��������
		message.setSubject(emailSub);
		// ��������
		message.setContent(emailMsg, "text/html;charset=utf-8");

		// 3.���� Transport���ڽ��ʼ�����
		Transport.send(message);
	}
	
	/**
	 * �����ʼ���SMT
	 * @param email,���������ַ
	 * @param emailSub,�ʼ�����
	 * @param emailMsg,�ʼ�����
	 * @throws Exception
	 */
	public static void sendmail() throws Exception{
		String email = "javawebonly@163.com";
		String emailMsg = "IpΪ119.29.159.138�ķ��������뵽��ʱ��ֻʣһ���£��뼰ʱ������ظ����˽������ѣ��������������������ʹ��";
		String emailSub = "��������������";
		
		 // 1.����һ���������ʼ��������Ự���� Session
		 Properties props = new Properties();
		 props.setProperty("mail.transport.protocol", "SMTP");
		 props.setProperty("mail.smtp.host", "smtp.163.com");
//		 props.setProperty("mail.smtp.host", "pop.163.com");
		 props.setProperty("mail.smtp.port", "25");
		 // ָ����֤Ϊtrue
		 props.setProperty("mail.smtp.auth", "true");
		 props.setProperty("mail.smtp.timeout","1000");
		 // ��֤�˺ż����룬������Ҫ�ǵ�������Ȩ��
		 Authenticator auth = new Authenticator() {
			 public PasswordAuthentication getPasswordAuthentication(){
			    return new PasswordAuthentication("18011715889@163.com", "119shouquan");
            }
        };
        
		 Session session = Session.getInstance(props, auth);
		 // ����debugģʽ�����Կ���������ϸ��������־
		 session.setDebug(true);

		// 2.����һ��Message�����൱�����ʼ�����
		Message message = new MimeMessage(session);
		// ���÷�����
		message.setFrom(new InternetAddress("18011715889@163.com"));
		// ���÷��ͷ�ʽ�������
		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
		// ��������
		message.setSubject(emailSub);
		// ��������
		message.setContent(emailMsg, "text/html;charset=utf-8");

		// 3.���� Transport���ڽ��ʼ�����
		Transport.send(message);
	}
	
	public static void  sendMailWithFile(String filePath) throws AddressException, MessagingException{
		String email = "javawebonly@163.com";
		String emailMsg = "IpΪ119.29.159.138�ķ��������뵽��ʱ��ֻʣһ���£��뼰ʱ������ظ����˽������ѣ��������������������ʹ��";
		String emailSub = "��������������";
		
		 // 1.����һ���������ʼ��������Ự���� Session
		 Properties props = new Properties();
		 props.setProperty("mail.transport.protocol", "SMTP");
		 props.setProperty("mail.smtp.host", "smtp.163.com");
//		 props.setProperty("mail.smtp.host", "pop.163.com");
		 props.setProperty("mail.smtp.port", "25");
		 // ָ����֤Ϊtrue
		 props.setProperty("mail.smtp.auth", "true");
		 props.setProperty("mail.smtp.timeout","1000");
		 // ��֤�˺ż����룬������Ҫ�ǵ�������Ȩ��
		 Authenticator auth = new Authenticator() {
			 public PasswordAuthentication getPasswordAuthentication(){
			    return new PasswordAuthentication("18011715889@163.com", "119shouquan");
            }
        };
        
		 Session session = Session.getInstance(props, auth);
		 // ����debugģʽ�����Կ���������ϸ��������־
		 session.setDebug(true);

		// 2.����һ��Message�����൱�����ʼ�����
		Message message = new MimeMessage(session);
		// ���÷�����
		message.setFrom(new InternetAddress("18011715889@163.com"));
		// ���÷��ͷ�ʽ�������
		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
		// ��������
		message.setSubject(emailSub);
//		// ��������
//		message.setContent(emailMsg, "text/html;charset=utf-8");

		
		Multipart mp = new MimeMultipart();
		MimeBodyPart mbpContent = new MimeBodyPart();
		mbpContent.setText(emailMsg);
		mp.addBodyPart(mbpContent);
		
		MimeBodyPart mbpFile = new MimeBodyPart();
		FileDataSource fds = new FileDataSource(filePath);
		mbpFile.setDataHandler(new DataHandler(fds));
		mbpFile.setFileName(fds.getName());
		mp.addBodyPart(mbpFile);
		message.setContent(mp);
		
		// 3.���� Transport���ڽ��ʼ�����
		Transport.send(message);
	}
	
	public static void main(String[] args) {
		String email = "18011715889@163.com";
		String email2 = "18312286056@139.com";
		String email3 = "1204899109@qq.com";
		String email4 = "javawebonly@163.com";
		String emailMsg = "IpΪ119.29.159.138�ķ��������뵽��ʱ��ֻʣһ���£��뼰ʱ������ظ����˽������ѣ��������������������ʹ��";
		String emailSub = "��������������";
		
		try {
//			sendmail(email4, emailSub, emailMsg);
			sendMailWithFile("E:\\�й�.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
