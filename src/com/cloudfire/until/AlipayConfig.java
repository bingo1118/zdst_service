package com.cloudfire.until;
import java.io.FileWriter;
import java.io.IOException;

/* *
 *������AlipayConfig
 *���ܣ�����������
 *��ϸ�������ʻ��й���Ϣ������·��
 *�޸����ڣ�2017-04-05
 *˵����
 *���´���ֻ��Ϊ�˷����̻����Զ��ṩ���������룬�̻����Ը����Լ���վ����Ҫ�����ռ����ĵ���д,����һ��Ҫʹ�øô��롣
 *�ô������ѧϰ���о�֧�����ӿ�ʹ�ã�ֻ���ṩһ���ο���
 */
public class AlipayConfig {
//�����������������������������������Ļ�����Ϣ������������������������������

	// Ӧ��ID,����APPID���տ��˺ż�������APPID��Ӧ֧�����˺�
	public static String app_id = "2016091800536782";
	
	// �̻�˽Կ������PKCS8��ʽRSA2˽Կ
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDiLA7jRgIhLhKx0nx3fBwOsaIdg4hKmsTs0xXAYG189T2FNN8bV5kS1I4eKDg0Ts1BNjqFE9duQdRSFSOYVRe1Akb93EyXjtijizZPpqSB9zN6t6To0uGR6CTZEM1U4n66WFdCtxdS6PjeRuX42Oavpc5rbKS78c/pxJJA4awTruOipjsjA8K566DoQtiWFKBYhxTm+gc9+6o8q21Cty7jnd+wYlpSo99joJYrPeS0JVMYmrBfKPY3diZaZ5/8s6ODIOjTOA1pmxkUew4aCp42bp/qJiJuzFJogjm+5X61JDmBVRABprrZYhOFl3rD6zbG2lSboQ7kf/H7DQUUlVlrAgMBAAECggEABO9dK8xdQ4gT1Fl5acoFerSZivdRw/74iQpaESaJfQ6E1yE+NQSGTIjiyPmrsEh/5gpe4IR8UOWS0m+vpSVGLSbtkaB681ukknz5JGfIcyWHtAfJy2K+lUZxd2MMd+uxRDOlQ8lm8uIue/skDXf+IJsHlAR827T9CmbFojDKZmuB/JjYOF3r07/FGY/C6wTmopEQflSXweQQ98rVXeN2hq0LgG/1l4mnzamXDbKcP8YMCOgj4gXU20PzsE6MSjhduMubPMPBsjZqiOMflDAMol2nJFLnaw4TzO0J+0j68ObyZI1P6uw7HYxr3lqAvvVHSgxMbufi7DBJGg+rofCxUQKBgQD8tR9NwY1ckfyZEndfQpY7f7erJU58IDSr8Pl/yrtiCKB2zp5HfnnTvGlcULojyFWXmJ1lbgMp8DtHYLVlAeB4mx/a+eqCp4dsvD1YBqQ03SrNCQH0AOl1rOzl/98aJXnYHDmQy7r8fIy8yLLdOPtNhn0qquaLh15i2yg2W3e1JQKBgQDlHm4TeW0OIv5GQQ2gNoZVMDnred4C+UniCSKA2kvtaZE/zLzoT9LNHjG6KPxsWsBe9FJdh+832j2QhJL5UhHV16eJqjixbqIKRsIac2v+OEYPZIjWW7di0aGyNhSsjHaXMOLXmtyy8dyaoIqMz61FY4acTFSAfGwoqZoIFFu3TwKBgQDO3y77JmGk0FWqsbVLJGV727ftwSKY9nxn7WMyPDJdtJY37DkiaeUtcmYGDofy1wNKkML4z7CDWogMjJyyTXEHCmZvintx7dctKoRFVVqDdDKfmqogp8Z2cNOWDXsEGn2+boypfj5ah1NnEB1sgEMpWdmJXlJREsYvwx0OMp6yiQKBgQDa05psbR+0TcWvUbhy2UCpojrfZPCexHtFMFZM3LGB2b7JQYD3HC4IjBsQYs7bqj1rM0xrWkYmgQElwwkgqLqyE5tHB1XIWRLYX8ZJPw3YquR3FR0AJm7bKb6Wl8JdHnaxZhZxt82r9wYshMJvRHaH/Et3ggt+/9bJa1ra9p62TwKBgDae3kjcTQxr6q3mPbi8O6iaR99XuGGbNpuUu7DMlE8ds0p3AUMLSG7LR813KYN+9kE/oc9MpYlOpuP1Ya5cg4YDZ82FPyWSnS7+ccqmMycp+PrzvanRlSg4bo6cdyVwlWnj2gA4fVSISVvaz0/1yi5x5VP0mj85B7VWruH+pmWf";
	
	//Ӧ�ù�Կ
    // public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4iwO40YCIS4SsdJ8d3wcDrGiHYOISprE7NMVwGBtfPU9hTTfG1eZEtSOHig4NE7NQTY6hRPXbkHUUhUjmFUXtQJG/dxMl47Yo4s2T6akgfczerek6NLhkegk2RDNVOJ+ulhXQrcXUuj43kbl+Njmr6XOa2yku/HP6cSSQOGsE67joqY7IwPCueug6ELYlhSgWIcU5voHPfuqPKttQrcu453fsGJaUqPfY6CWKz3ktCVTGJqwXyj2N3YmWmef/LOjgyDo0zgNaZsZFHsOGgqeNm6f6iYibsxSaII5vuV+tSQ5gVUQAaa62WIThZd6w+s2xtpUm6EO5H/x+w0FFJVZawIDAQAB";
    // ֧������Կ,�鿴��ַ��https://openhome.alipay.com/platform/keyManage.htm ��ӦAPPID�µ�֧������Կ��
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxPL0Yu1z3CWQaSVep+P2giZi1JQC4UoiGu+8B57U+xqX5PIC5NSI5M7s4BV6hJRp0HJViNJ0/DjC67mlilpxu7RODrhvoqXV3K4heatdMA7gzhqbTiA7BNkai4JyeSbv5fy8A1vFxiPzo/3oeZasDv3ydAMZG6EtGnj9KZNkkv5INHcZoU1DgVCr/11DwgVqUmX5+NUVmvA/Gn8bpFZ+J7K5UcDQBEjnio5cA3ZYfG6OUDHlhbdBlJ0nuvMDG1CJR4EqrcjwY/iWeb2mzFk2dpogtrlQcYnzRNMSE8PbLYDQs0pOpYAn1yjyCn2LyTtjFg5TobErgu49ny573X4VtQIDAQAB";
    // �������첽֪ͨҳ��·��  ��http://��ʽ������·�������ܼ�?id=123�����Զ����������������������������
	public static String notify_url = "http://localhost:8080/fireSystem/notify_url.do";

	// ҳ����תͬ��֪ͨҳ��·�� ��http://��ʽ������·�������ܼ�?id=123�����Զ����������������������������
	public static String return_url = "http://localhost:8080/fireSystem/return_url.do";

	// ǩ����ʽ
	public static String sign_type = "RSA2";
	
	// �ַ������ʽ
	public static String charset = "utf-8";
	
	// ֧��������
	//��������
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	//��ʽ����
	//public static String gatewayUrl = "https://openapi.alipay.com/gateway.do";
	
	// ֧��������
	public static String log_path = "C:\\";


//�����������������������������������Ļ�����Ϣ������������������������������

    /** 
     * д��־��������ԣ�����վ����Ҳ���ԸĳɰѼ�¼�������ݿ⣩
     * @param sWord Ҫд����־����ı�����
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

