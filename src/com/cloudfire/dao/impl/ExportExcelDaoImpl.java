package com.cloudfire.dao.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.cloudfire.dao.ExportExcelDao;
import com.cloudfire.entity.SmartControlEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.Utils;

public class ExportExcelDaoImpl implements ExportExcelDao {

	@Override
	public void exportExcelByAllDevice(String filePath,
			List<SmartControlEntity> smartList) {
		 //1
		 HSSFWorkbook wb = new HSSFWorkbook(); 
		 //2
		 HSSFSheet sheet = wb.createSheet("sheet1");
		 //3
		 HSSFRow row = sheet.createRow((int) 0);  
		 //4--
		 HSSFCellStyle style = wb.createCellStyle();  
	     style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
	     setSheetHeader1(row, style);
	     //5--
	     insertDatasToSheet1(sheet, smartList); 
	     //6
	     writeExcelToDisk(filePath, wb);
		
	}
	
	@Override
	public void exportExcel(String filePath,List<SmokeBean> smokeList) {
		 //1
		 HSSFWorkbook wb = new HSSFWorkbook(); 
		 //2
		 HSSFSheet sheet = wb.createSheet("sheet1");
		 //3
		 HSSFRow row = sheet.createRow((int) 0);  
		 //4
		 HSSFCellStyle style = wb.createCellStyle();  
	     style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
	     setSheetHeader(row, style);
	     //5
	     insertDatasToSheet(sheet, smokeList); 
	     //6
	     writeExcelToDisk(filePath, wb);
	}
	
	private void writeExcelToDisk(String filePath, HSSFWorkbook wb) {  
        try {
        	File file = new File(filePath);
        	
            FileOutputStream fout = null;
            if(!file.exists()){
        		file.getParentFile().mkdir();
        		file.createNewFile();
        	}
            fout = new FileOutputStream(file);  
            wb.write(fout);  
            fout.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
	
	private void setSheetHeader(HSSFRow row, HSSFCellStyle style) {  
        HSSFCell cell = null;
//        String sqlstr = "SELECT named,address,principal1,principal1Phone,placeTypeId,characterId,area FROM smoke";
        String sqlstr = "SELECT 名称,地址,联系人,电话,商场类型,单位性质,区域 FROM smoke";
        List<String> columns = Utils.getColumnsBySql(sqlstr);
        for(int i = 0;i<columns.size();i++){
        	cell = row.createCell((short) i);  
            cell.setCellStyle(style);  
            // 这个输出编码必须设置，否则汉字会乱码  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(columns.get(i));
        }
    } 
	
	private void setSheetHeader1(HSSFRow row, HSSFCellStyle style) {  
        HSSFCell cell = null;
//        String sqlstr = "select area,mac,named,address,placeTypeId,deviceType,netState,time,repeater,floor from smoke";
        String sqlstr = "select 区域,设备MAC,名称,地址,商业类型,设备类型,状态,时间,中继器MAC,楼层 from smoke";
        List<String> columns = Utils.getColumnsBySql(sqlstr);
        for(int i = 0;i<columns.size();i++){
        	cell = row.createCell((short) i);  
            cell.setCellStyle(style);  
            // 这个输出编码必须设置，否则汉字会乱码  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(columns.get(i));
        }
    } 
	
	private void insertDatasToSheet(HSSFSheet sheet, List<SmokeBean> list) {
        HSSFCell cell = null;  
        HSSFRow row = null;  
        for (int i = 0; i < list.size(); i++) {  
            row = sheet.createRow((int) i + 1);  
            SmokeBean ab = (SmokeBean) list.get(i);  
            // 创建单元格，并设置各个列中实际数据的值  
            cell = row.createCell((short) 0);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getName());  
  
            cell = row.createCell((short) 1);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getAddress());
            
            cell = row.createCell((short) 2);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getPrincipal1());
            
            cell = row.createCell((short) 3);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getPrincipal1Phone());
            
            cell = row.createCell((short) 4);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getAreaName());
            
            cell = row.createCell((short) 5);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getPlaceTypeName());
            
            cell = row.createCell((short) 6);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getCharacterName());
        }  
    }  
	
	private void insertDatasToSheet1(HSSFSheet sheet, List<SmartControlEntity> list) {
        HSSFCell cell = null;  
        HSSFRow row = null;  
        for (int i = 0; i < list.size(); i++) {  
            row = sheet.createRow((int) i + 1);  
            SmartControlEntity ab = (SmartControlEntity) list.get(i);  
            // 创建单元格，并设置各个列中实际数据的值  
            cell = row.createCell((short) 0);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getCompany());  
  
            cell = row.createCell((short) 1);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getSmokeMac());
            
            cell = row.createCell((short) 2);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getEnterprise());
            
            cell = row.createCell((short) 3);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getAddress());
            
            cell = row.createCell((short) 4);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getPlaceTypeName());
            
            cell = row.createCell((short) 5);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getDevType());
            
            cell = row.createCell((short) 6);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getDevState());
            cell = row.createCell((short) 7);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getStateTime());
            cell = row.createCell((short) 8);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getRepeaterMac());
            cell = row.createCell((short) 9);  
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);  
            cell.setCellValue(ab.getFloor());
        }  
    } 

}
