package com.cloudfire.until;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpUtil {
	private static String OS_NAME = null;
    /**
     * ��ѯ��������IP��վ
     */
    private static final String getWebIP = 
//    		"http://2017.ip138.com/ic.asp"; //outerIp��[]��Χ��
    		"http://www.3322.org/dyndns/getip";  //ֱ�ӷ���outerIp
    	
    /**
     * Ĭ��ֵ
     */
    private static String IP = "δ֪";
    static {
        
        OS_NAME = System.getProperty("os.name");
        System.out.println("��ʼ����ȡϵͳ����..."+OS_NAME);
    }
	
	 public static String getIP(int queryFlag) {
	        if (queryFlag == 1) {
	            // ��ѯ����IP
	            switch (IpUtil.getOsType()) {
	            case 1:
	                IP = IpUtil.getWinOuterIP();
	                break;
	            case 2:
	                IP = IpUtil.getWinOuterIP();
	                break;
	            default:
	                break;
	            }
	        } else {
	            // ��ѯ����IP
	            switch (IpUtil.getOsType()) {
	            case 1:
	                IP = IpUtil.getWinInnerIP();
	                break;
	            case 2:
	                IP = IpUtil.getLinuxInnerIP();
	                break;
	            default:
	                break;
	            }
	        }

	        return IP;
	    }

	    /**
	     * ��ȡwindowƽ̨������IP
	     * 
	     * @return IP
	     */
	    private static String getWinOuterIP() {
	        try {
	            URL url = new URL(getWebIP);
	            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	            String s = "";
	            StringBuffer sb = new StringBuffer("");
	            String webContent = "";
	            while ((s = br.readLine()) != null) {
	                //System.err.println("---"+s);
	                sb.append(s);
	            }
	            br.close();
	            webContent = sb.toString();
//	            int start = webContent.indexOf("[") + 1;
//	            int end = webContent.indexOf("]");
//	            webContent = webContent.substring(start, end);
	            //��֤ip����Ч��
	            Pattern pattern = Pattern.compile("(((25[0-5])|(2[0-4]\\d)|(1\\d{2})|([1-9]?\\d)).){3}((25[0-5])|(2[0-4]\\d)|(1\\d{2})|([1-9]?\\d))");
	            Matcher isIp = pattern.matcher(webContent);
	            if (!isIp.matches()) {
	            	return getWinOuterIP();
	            }
	            
	            return webContent;
	        } catch (Exception e) {
	            //e.printStackTrace();
	            System.err.println("��ȡ����IP��վ����ʧ�ܣ�");
	            return getWinOuterIP();
	        }

	    }

	    /**
	     * ��ȡwindowƽ̨������IP
	     * 
	     * @return IP
	     */
	    private static String getWinInnerIP() {
	        InetAddress[] inetAdds;
	        try {
	            inetAdds = InetAddress.getAllByName(InetAddress.getLocalHost()
	                    .getHostName());
	        } catch (UnknownHostException e) {
	            e.printStackTrace();
	            return IP;
	        }
	        return inetAdds[0].getHostAddress();
	    }

	    /**
	     * ��ȡlinux�µ�IP
	     * @param queryFlag
	     * 1��ʾ��ѯ����IP 2��ʾ��ѯ����IP
	     * @return IP
	     * @throws IOException 
	     */
	    private static String getLinuxInnerIP() {
	    	 String ip = "";
	         try {
	             for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	                 NetworkInterface intf = en.nextElement();
	                 String name = intf.getName();
	                 if (!name.contains("docker") && !name.contains("lo")) {
	                     for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                         InetAddress inetAddress = enumIpAddr.nextElement();
	                         if (!inetAddress.isLoopbackAddress()) {
	                             String ipaddress = inetAddress.getHostAddress().toString();
	                             if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
	                                 ip = ipaddress;
	                                 System.out.println(ipaddress);
	                             }
	                         }
	                     }
	                 }
	             }
	         } catch (SocketException ex) {
	             System.out.println("��ȡip��ַ�쳣");
	             ip = "127.0.0.1";
	             ex.printStackTrace();
	         }
	         System.out.println("IP:"+ip);
	         return ip;
	    }
	    /**
	     * Ŀǰֻ֧��window��linux����ƽ̨
	     * 
	     * @return 1 window 2 linux -1:δ֪
	     */
	    public static int getOsType() {
	        // ����ȡ����ϵͳ��������תΪȫ��Сд
	        OS_NAME = OS_NAME.toLowerCase();
	        if (OS_NAME.startsWith("win")) {
	            return 1;
	        }
	        if (OS_NAME.startsWith("linux")) {
	            return 2;
	        }
	        return -1;
	    }

	    /**
	     * ���Է���
	     * 
	     * @param args
	     * @throws IOException 
	     */
	    public static void main(String[] args) throws IOException {
//	        System.out.println("����ϵͳΪ��"+SystemOperate.fromCode(IpUtil.getOsType()+""));
//	        System.out.println("����IPΪ��"+IpUtil.getIP(2));
//	        System.out.println("����IPΪ��"+IpUtil.getIP(1));
	        
	        String webContent = "139.159.220.138";
	        String webContent2= "59.42.129.229";
	        Pattern pattern = Pattern.compile("(((25[0-5])|(2[0-4]\\d)|(1\\d{2})|([1-9]?\\d)).){3}((25[0-5])|(2[0-4]\\d)|(1\\d{2})|([1-9]?\\d))");
            Matcher isIp = pattern.matcher(webContent);
            Matcher isIp2 = pattern.matcher(webContent2);
            System.out.println(isIp.matches());
            System.out.println(isIp2.matches());
	    }
	    
	    /**
	     * ����ϵͳ����ö��
	     * Ŀǰֻ��windows �� linuxϵͳ
	     * @author Relieved
	     *
	     */
	    enum SystemOperate{
	        WINDOWS(1,"windowsϵͳ"),LINUX(2,"linuxϵͳ"),UNKNOWN(3,"δ֪ϵͳ");

	        private int operateType;
	        private String operateName;

	        private SystemOperate(int operateType, String operateName) {
	            this.operateType = operateType;
	            this.operateName = operateName;
	        }
	        public int getOperateType() {
	            return operateType;
	        }
	        public void setOperateType(int operateType) {
	            this.operateType = operateType;
	        }
	        public String getOperateName() {
	            return operateName;
	        }
	        public void setOperateName(String operateName) {
	            this.operateName = operateName;
	        }
	        private static final Map<String, SystemOperate> lookup = new HashMap<String, SystemOperate>();
	        static {
	            for (SystemOperate cp : values()) {
	                lookup.put(cp.getOperateType()+"", cp);
	            }
	        }
	        public static String fromCode(String code) {
	            return lookup.get(code).operateName;
	        }
	    }

}
