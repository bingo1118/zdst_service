package com.cloudfire.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ���л��������л�������
 * ����ת��byte[]�Ͷ���
 * Version: 1.0 </br>
*/

public class SerializeUtil {
    
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // ���л�
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {  
            close(oos);  
            close(baos);  
        } 
        return null;
    }

    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            // �����л�
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {  
            close(bais);  
            close(ois);  
        } 
        return null;
    }
    
    /** 
     * ���л� list ���� 
     *  
     * @param list 
     * @return 
     */  
    public static byte[] serializeList(List<?> list) {  
  
        if (list == null || list.size() <= 0) {  
            return null;  
        }  
        ObjectOutputStream oos = null;  
        ByteArrayOutputStream baos = null;  
        byte[] bytes = null;  
        try {  
            baos = new ByteArrayOutputStream();  
            oos = new ObjectOutputStream(baos);  
            for (Object obj : list) {  
                oos.writeObject(obj);  
            }  
            bytes = baos.toByteArray();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            close(oos);  
            close(baos);  
        }  
        return bytes;  
    }  
  
    /** 
     * �����л� list ���� 
     *  
     * @param lb 
     * @return 
     */  
    public static List<?> unserializeList(byte[] bytes) {  
        if (bytes == null) {  
            return null;  
        }  
  
        List<Object> list = new ArrayList<Object>();  
        ByteArrayInputStream bais = null;  
        ObjectInputStream ois = null;  
        try {  
            // �����л�  
            bais = new ByteArrayInputStream(bytes);  
            ois = new ObjectInputStream(bais);  
            while (bais.available() > 0) {  
                Object obj = (Object) ois.readObject();  
                if (obj == null) {  
                    break;  
                }  
                list.add(obj);  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            close(bais);  
            close(ois);  
        }  
        return list;  
    }  
    
    /** 
     * �ر�io������ 
     *  
     * @param closeable 
     */  
    public static void close(Closeable closeable) {  
        if (closeable != null) {  
            try {  
                closeable.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}
