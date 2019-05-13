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
	 * 发送邮件，SMT
	 * @param email,接收邮箱地址
	 * @param emailSub,邮件主题
	 * @param emailMsg,邮件内容
	 * @throws Exception
	 */
	public static void sendmail(String email,String emailSub,String emailMsg) throws Exception{
		 // 1.创建一个程序与邮件服务器会话对象 Session
		 Properties props = new Properties();
		 props.setProperty("mail.transport.protocol", "SMTP");
		 props.setProperty("mail.smtp.host", "smtp.163.com");
//		 props.setProperty("mail.smtp.host", "pop.163.com");
		 props.setProperty("mail.smtp.port", "25");
		 // 指定验证为true
		 props.setProperty("mail.smtp.auth", "true");
		 props.setProperty("mail.smtp.timeout","1000");
		 // 验证账号及密码，密码需要是第三方授权码
		 Authenticator auth = new Authenticator() {
			 public PasswordAuthentication getPasswordAuthentication(){
			    return new PasswordAuthentication("18011715889@163.com", "119shouquan");
            }
        };
        
		 Session session = Session.getInstance(props, auth);
		 // 开启debug模式，可以看到更多详细的输入日志
		 session.setDebug(true);

		// 2.创建一个Message，它相当于是邮件内容
		Message message = new MimeMessage(session);
		// 设置发送者
		message.setFrom(new InternetAddress("18011715889@163.com"));
		// 设置发送方式与接收者
		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
		// 设置主题
		message.setSubject(emailSub);
		// 设置内容
		message.setContent(emailMsg, "text/html;charset=utf-8");

		// 3.创建 Transport用于将邮件发送
		Transport.send(message);
	}
	
	/**
	 * 发送邮件，SMT
	 * @param email,接收邮箱地址
	 * @param emailSub,邮件主题
	 * @param emailMsg,邮件内容
	 * @throws Exception
	 */
	public static void sendmail() throws Exception{
		String email = "javawebonly@163.com";
		String emailMsg = "Ip为119.29.159.138的服务器距离到期时间只剩一个月，请及时提醒相关负责人进行续费，否则服务器将不能正常使用";
		String emailSub = "服务器到期提醒";
		
		 // 1.创建一个程序与邮件服务器会话对象 Session
		 Properties props = new Properties();
		 props.setProperty("mail.transport.protocol", "SMTP");
		 props.setProperty("mail.smtp.host", "smtp.163.com");
//		 props.setProperty("mail.smtp.host", "pop.163.com");
		 props.setProperty("mail.smtp.port", "25");
		 // 指定验证为true
		 props.setProperty("mail.smtp.auth", "true");
		 props.setProperty("mail.smtp.timeout","1000");
		 // 验证账号及密码，密码需要是第三方授权码
		 Authenticator auth = new Authenticator() {
			 public PasswordAuthentication getPasswordAuthentication(){
			    return new PasswordAuthentication("18011715889@163.com", "119shouquan");
            }
        };
        
		 Session session = Session.getInstance(props, auth);
		 // 开启debug模式，可以看到更多详细的输入日志
		 session.setDebug(true);

		// 2.创建一个Message，它相当于是邮件内容
		Message message = new MimeMessage(session);
		// 设置发送者
		message.setFrom(new InternetAddress("18011715889@163.com"));
		// 设置发送方式与接收者
		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
		// 设置主题
		message.setSubject(emailSub);
		// 设置内容
		message.setContent(emailMsg, "text/html;charset=utf-8");

		// 3.创建 Transport用于将邮件发送
		Transport.send(message);
	}
	
	public static void  sendMailWithFile(String filePath) throws AddressException, MessagingException{
		String email = "javawebonly@163.com";
		String emailMsg = "Ip为119.29.159.138的服务器距离到期时间只剩一个月，请及时提醒相关负责人进行续费，否则服务器将不能正常使用";
		String emailSub = "服务器到期提醒";
		
		 // 1.创建一个程序与邮件服务器会话对象 Session
		 Properties props = new Properties();
		 props.setProperty("mail.transport.protocol", "SMTP");
		 props.setProperty("mail.smtp.host", "smtp.163.com");
//		 props.setProperty("mail.smtp.host", "pop.163.com");
		 props.setProperty("mail.smtp.port", "25");
		 // 指定验证为true
		 props.setProperty("mail.smtp.auth", "true");
		 props.setProperty("mail.smtp.timeout","1000");
		 // 验证账号及密码，密码需要是第三方授权码
		 Authenticator auth = new Authenticator() {
			 public PasswordAuthentication getPasswordAuthentication(){
			    return new PasswordAuthentication("18011715889@163.com", "119shouquan");
            }
        };
        
		 Session session = Session.getInstance(props, auth);
		 // 开启debug模式，可以看到更多详细的输入日志
		 session.setDebug(true);

		// 2.创建一个Message，它相当于是邮件内容
		Message message = new MimeMessage(session);
		// 设置发送者
		message.setFrom(new InternetAddress("18011715889@163.com"));
		// 设置发送方式与接收者
		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
		// 设置主题
		message.setSubject(emailSub);
//		// 设置内容
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
		
		// 3.创建 Transport用于将邮件发送
		Transport.send(message);
	}
	
	public static void main(String[] args) {
		String email = "18011715889@163.com";
		String email2 = "18312286056@139.com";
		String email3 = "1204899109@qq.com";
		String email4 = "javawebonly@163.com";
		String emailMsg = "Ip为119.29.159.138的服务器距离到期时间只剩一个月，请及时提醒相关负责人进行续费，否则服务器将不能正常使用";
		String emailSub = "服务器到期提醒";
		
		try {
//			sendmail(email4, emailSub, emailMsg);
			sendMailWithFile("E:\\中国.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
