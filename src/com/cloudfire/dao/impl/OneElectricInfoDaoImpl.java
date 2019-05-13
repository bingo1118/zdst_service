package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.dao.OneElectricInfoDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.ChuangAnBean;
import com.cloudfire.entity.ElectricEnergyEntity;
import com.cloudfire.entity.OneChuangAnEntity;
import com.cloudfire.entity.OneElectricEntity;
import com.cloudfire.entity.OneTHDeviceInfoEntity;
import com.cloudfire.entity.ThreePhaseElectricEntity;

public class OneElectricInfoDaoImpl implements OneElectricInfoDao{
	
	

	public OneElectricEntity getOneElectricInfo(String smokeMac) {
		//获取电气设备的阈值
//		Map<Integer, List<String>> map = getThresholdValues(smokeMac);
		Map<Integer,String> map=getThresholdValues2(smokeMac);
		OneElectricEntity mOneElectricEntity = new OneElectricEntity();
		List<OneElectricEntity.ElectricBean> listElectricBean=new ArrayList<OneElectricEntity.ElectricBean>();
		OneElectricEntity.ElectricBean mElectricBean6 = new OneElectricEntity.ElectricBean();
		OneElectricEntity.ElectricBean mElectricBean7 = new OneElectricEntity.ElectricBean();
		OneElectricEntity.ElectricBean mElectricBean8 = new OneElectricEntity.ElectricBean();
		OneElectricEntity.ElectricBean mElectricBean9 = new OneElectricEntity.ElectricBean();
		List<OneElectricEntity.ElectricBean.ElectricValueBean> listBean6=new ArrayList<OneElectricEntity.ElectricBean.ElectricValueBean>();
		List<OneElectricEntity.ElectricBean.ElectricValueBean> listBean7=new ArrayList<OneElectricEntity.ElectricBean.ElectricValueBean>();
		List<OneElectricEntity.ElectricBean.ElectricValueBean> listBean8=new ArrayList<OneElectricEntity.ElectricBean.ElectricValueBean>();
		List<OneElectricEntity.ElectricBean.ElectricValueBean> listBean9=new ArrayList<OneElectricEntity.ElectricBean.ElectricValueBean>();
		
		String sql = "select electricValue1,electricValue2,electricValue3,electricValue4,electricTime,electricType from (" +
				" select electricValue1,electricValue2,electricValue3,electricValue4,electricTime,electricType " +
				" from electricinfo where smokeMac = ? " +
				"order by id desc limit 0,20) t group by electricType  ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				int electricType = rs.getInt(6);
				OneElectricEntity.ElectricBean.ElectricValueBean mElectricValueBean2 = null;
				switch (electricType) {
				case 6:
					for(int i=1;i<=3;i++){
						OneElectricEntity.ElectricBean.ElectricValueBean mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
						mElectricValueBean.setId(i);
						mElectricValueBean.setValue(rs.getString(i));
						mElectricValueBean.setElectricThreshold(map.get(6));
						listBean6.add(mElectricValueBean);
					}
					mElectricBean6.setElectricTime(rs.getString(5));
					
					break;
				case 7:
					for(int i=1;i<=4;i++){
						OneElectricEntity.ElectricBean.ElectricValueBean mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
						mElectricValueBean.setId(i);
						mElectricValueBean.setValue(rs.getString(i));
						mElectricValueBean.setElectricThreshold(map.get(7));
						listBean7.add(mElectricValueBean);
					}
					mElectricBean7.setElectricTime(rs.getString(5));
					mElectricBean7.setElectricType(7);
					break;
				case 8:
					for(int i=1;i<=1;i++){
						OneElectricEntity.ElectricBean.ElectricValueBean mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
						mElectricValueBean.setId(i);
						mElectricValueBean.setValue(rs.getString(i));
						mElectricValueBean.setElectricThreshold(map.get(8));
						listBean8.add(mElectricValueBean);
					}
					mElectricBean8.setElectricTime(rs.getString(5));
					mElectricBean8.setElectricType(8);
					break;
				case 9:
					for(int i=1;i<=4;i++){
						OneElectricEntity.ElectricBean.ElectricValueBean mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
						mElectricValueBean.setId(i);
						mElectricValueBean.setValue(rs.getString(i));
						mElectricValueBean.setElectricThreshold(map.get(9));
						listBean9.add(mElectricValueBean);
					}
					mElectricBean9.setElectricTime(rs.getString(5));
					mElectricBean9.setElectricType(9);
					break;
				case 61:
					mElectricValueBean2 = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean2.setId(1);
					mElectricValueBean2.setValue(rs.getString(1));
					mElectricValueBean2.setElectricThreshold(map.get(6));
					listBean6.add(mElectricValueBean2);
					mElectricBean6.setElectricTime(rs.getString(5));
					mElectricBean6.setElectricType(6);
					break;
				case 62:
					mElectricValueBean2 = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean2.setId(2);
					mElectricValueBean2.setValue(rs.getString(2));
					mElectricValueBean2.setElectricThreshold(map.get(6));
					listBean6.add(mElectricValueBean2);
					mElectricBean6.setElectricTime(rs.getString(5));
					mElectricBean6.setElectricType(6);
					break;
				case 63:
					mElectricValueBean2 = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean2.setId(3);
					mElectricValueBean2.setValue(rs.getString(3));
					mElectricValueBean2.setElectricThreshold(map.get(6));
					listBean6.add(mElectricValueBean2);
					mElectricBean6.setElectricTime(rs.getString(5));
					mElectricBean6.setElectricType(6);
					break;
				case 71:
					mElectricValueBean2 = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean2.setId(1);
					mElectricValueBean2.setValue(rs.getString(1));
					mElectricValueBean2.setElectricThreshold(map.get(7));
					listBean7.add(mElectricValueBean2);
					mElectricBean7.setElectricTime(rs.getString(5));
					mElectricBean7.setElectricType(7);
					break;
				case 72:
					mElectricValueBean2 = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean2.setId(2);
					mElectricValueBean2.setValue(rs.getString(2));
					mElectricValueBean2.setElectricThreshold(map.get(7));
					listBean7.add(mElectricValueBean2);
					mElectricBean7.setElectricTime(rs.getString(5));
					mElectricBean7.setElectricType(7);
					break;
				case 73:
					mElectricValueBean2 = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean2.setId(3);
					mElectricValueBean2.setValue(rs.getString(3));
					mElectricValueBean2.setElectricThreshold(map.get(7));
					listBean7.add(mElectricValueBean2);
					mElectricBean7.setElectricTime(rs.getString(5));
					mElectricBean7.setElectricType(7);
					break;
				case 81:
					mElectricValueBean2 = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean2.setId(1);
					mElectricValueBean2.setValue(rs.getString(1));
					mElectricValueBean2.setElectricThreshold(map.get(8));
					listBean8.add(mElectricValueBean2);
					mElectricBean8.setElectricTime(rs.getString(5));
					mElectricBean8.setElectricType(8);
					break;
				}
			}

			mElectricBean6.setElectricType(6);
			mElectricBean6.setElectricThreshold(map.get(6));
			mElectricBean6.setElectricValue(listBean6);
			listElectricBean.add(mElectricBean6);

			mElectricBean7.setElectricType(7);
			mElectricBean7.setElectricThreshold(map.get(7));
			mElectricBean7.setElectricValue(listBean7);
			listElectricBean.add(mElectricBean7);

			mElectricBean8.setElectricType(8);
			mElectricBean8.setElectricThreshold(map.get(8));
			mElectricBean8.setElectricValue(listBean8);
			listElectricBean.add(mElectricBean8);

			mElectricBean9.setElectricType(9);
			mElectricBean9.setElectricThreshold(map.get(9));
			mElectricBean9.setElectricValue(listBean9);
			listElectricBean.add(mElectricBean9);
			
			mOneElectricEntity.setElectric(listElectricBean);
			mOneElectricEntity.setErrorCode(0);
			mOneElectricEntity.setError("获取单个电气设备成功");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}

		return mOneElectricEntity;
	}

	public int getElectricNum(String smokeMac) {
		// TODO Auto-generated method stub
		String sql="select COUNT(DISTINCT(electricType)) from electricinfo e where smokeMac= ?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		int count=0;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return count;
	}

	public Map<Integer, List<String>> getThresholdValues(String smokeMac) {
		String sql="select alarmthreshold1,alarmthreshold2,alarmthreshold3,alarmthreshold4,alarmFamily " +
				"from alarmthreshold where smokeMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		Map<Integer, List<String>> map = null;
		String str44 = null;
		String str43 = null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(map==null){
					map = new HashMap<Integer, List<String>>();
				}
				int alarmFamily = rs.getInt(5);
				switch (alarmFamily) {
					case 43:
						str43 = rs.getString(1);
						break;
					case 44:
						str44 = rs.getString(1);
						break;
					case 45:
						List<String> list45 = new ArrayList<String>();
						list45.add(rs.getString(1));
						map.put(7, list45);
						break;
					case 46:
						List<String> list46 = new ArrayList<String>();
						list46.add(rs.getString(1));
						map.put(8, list46);
						break;
					case 47:
						List<String> list47 = new ArrayList<String>();
						for(int i=1;i<=4;i++){
							list47.add(rs.getString(i));
						}
						map.put(9, list47);
						break;
				}
			}
			if(map!=null){
				List<String> listPressure = new ArrayList<String>();
				StringBuffer pressure=new StringBuffer();
				if(str44!=null&&str44.length()>0&&str43!=null&&str43.length()>0){
					pressure.append(str44).append("\\").append(str43);
				}else if(str44!=null&&str44.length()>0){
					pressure.append(str44).append("\\").append("242");
				}else if(str43!=null&&str43.length()>0){
					pressure.append("176").append("\\").append(str43);
				}else{
					pressure.append("176"+"\\"+"242");
				}
				listPressure.add(pressure.toString());
				map.put(6, listPressure);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return map;
	}
	
	public Map<Integer,String> getThresholdValues2(String smokeMac) {
		Map<Integer, String> map = new HashMap<Integer,String>();
		String str44 = "";
		String str43 = "";
		String str45 = "";
		String str46 = "";
		String str47 = "";
		
		String sql="select alarmthreshold1,alarmthreshold2,alarmthreshold3,alarmthreshold4,alarmFamily " +
				"from alarmthreshold where smokeMac=?";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
		ResultSet rs = null;
		
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				int alarmFamily = rs.getInt(5);
				switch (alarmFamily) {
					case 43://过压
						str43 = rs.getString(1);
						break;
					case 44://欠压
						str44 = rs.getString(1);
						break;
					case 45://过流
						str45 = rs.getString(1);
						break;
					case 46://漏电流
						str46 = rs.getString(1);
						break;
					case 47://温度
						str47 = rs.getString(1);
						break;
				}
			}
			
			StringBuffer pressure=new StringBuffer();
			if(StringUtils.isNotBlank(str44)&&StringUtils.isNotBlank(str43)){
				pressure.append(str44).append("\\").append(str43);
			}else if(StringUtils.isNotBlank(str44)){
				pressure.append(str44).append("\\").append("242");
			}else if(StringUtils.isNotBlank(str43)){
				pressure.append("176").append("\\").append(str43);
			}else{
				pressure.append("176"+"\\"+"242");
			}
			map.put(6, pressure.toString());
			
			if (StringUtils.isNotBlank(str45)){
				map.put(7, str45);
			} else {
				map.put(7, "6");
			}
			
			if (StringUtils.isNotBlank(str46)){
				map.put(8, str46);
			} else {
				map.put(8, "500");
			}
			
			if (StringUtils.isNotBlank(str47)){
				map.put(9, str47);
			} else {
				map.put(9, "60");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return map;
	}
	
	public OneChuangAnEntity getOneChuangAnEntity(String userId,
			String privilege, String smokeMac) {
			OneChuangAnEntity mOneChuangAnEntity = null;
//			Map<Integer, List<String>> map = getThresholdValues(smokeMac);
			String sql = "SELECT * from (SELECT * from chuangan_info where mac=? ORDER BY time desc) a  GROUP BY line";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
			ResultSet rs = null;
			List<ChuangAnBean> listElectricBean=null;
			try {
				ps.setString(1, smokeMac);
				rs = ps.executeQuery();
				List<ChuangAnBean> listBean=new ArrayList<ChuangAnBean>();
				while(rs.next()){
					if(mOneChuangAnEntity==null){
						mOneChuangAnEntity = new OneChuangAnEntity();
						listElectricBean = new ArrayList<ChuangAnBean>();
					}				
					ChuangAnBean mElectricValueBean = new ChuangAnBean();
					mElectricValueBean.setId(rs.getInt(5));
					mElectricValueBean.setValue(rs.getString(3));
					mElectricValueBean.setTime(rs.getString(4));
					mElectricValueBean.setState(rs.getString(6));
					listBean.add(mElectricValueBean);
					
						
				if(listElectricBean!=null){
					mOneChuangAnEntity.setElectric(listBean);
					mOneChuangAnEntity.setErrorCode(0);
					mOneChuangAnEntity.setError("获取成功");
				}
			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}

//		}
		return mOneChuangAnEntity;
	}
	
	
	public OneTHDeviceInfoEntity getOneTHDevEntity(String smokeMac) {
		OneTHDeviceInfoEntity mOneChuangAnEntity = null;
//			Map<Integer, List<String>> map = getThresholdValues(smokeMac);
			String sql = "SELECT * from th_info where mac=? ORDER BY time desc limit 1";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
			ResultSet rs = null;
//			List<ChuangAnBean> listElectricBean=null;
			try {
				ps.setString(1, smokeMac);
				rs = ps.executeQuery();
//				List<ChuangAnBean> listBean=new ArrayList<ChuangAnBean>();
				while(rs.next()){
					if(mOneChuangAnEntity==null){
						mOneChuangAnEntity = new OneTHDeviceInfoEntity();
					}		
					mOneChuangAnEntity.setTemperature(rs.getString(2));
					mOneChuangAnEntity.setHumidity(rs.getString(3));
					mOneChuangAnEntity.setTime(rs.getString(4));
					mOneChuangAnEntity.setErrorCode(0);
					mOneChuangAnEntity.setError("获取成功");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}

//		}
		return mOneChuangAnEntity;
	}

	@Override
	public ThreePhaseElectricEntity getThreePhaseElectricEntity(String smokeMac) {
		ThreePhaseElectricEntity tpe = null;
		String sql = "SELECT smokeImei,smokeImsi,voltageA,voltageB,voltageC,electricityA,electricityB,electricityC,surplusElectri,currentMaximum,runAlarmState,runGateState,runCauseState,heartime from threeelectricinfo where smokeImei = ? ORDER BY  heartime desc LIMIT 1";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(tpe == null){
					tpe = new ThreePhaseElectricEntity();
				}
				tpe.setHeartime(rs.getString("heartime"));
				tpe.setSurplusElectri(rs.getString("surplusElectri"));
				tpe.setElectricityA(rs.getString("electricityA"));
				tpe.setElectricityB(rs.getString("electricityB"));
				tpe.setElectricityC(rs.getString("electricityC"));
				tpe.setVoltageA(rs.getString("voltageA"));
				tpe.setVoltageB(rs.getString("voltageB"));
				tpe.setVoltageC(rs.getString("voltageC"));
				tpe.setImsiStr(rs.getString("smokeImsi"));
				tpe.setImeiStr(rs.getString("smokeImei"));
			}
		} catch (Exception e) {
//			throw new RuntimeException("ThreePhaseElectricEntity 468 错误!");
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return tpe;
	}
	
	public OneElectricEntity getOneElectricInfo(String smokeMac,String devType) {
		// TODO Auto-generated method stub
		OneElectricEntity mOneElectricEntity = null;
			Map<Integer, List<String>> map = getThresholdValues(smokeMac);
			
			String sql = "SELECT smokeImei,smokeImsi,voltageA,voltageB,voltageC,electricityA,electricityB,electricityC,surplusElectri,currentMaximum,runAlarmState,runGateState,runCauseState,heartime,tempValueA,tempValueB,tempValueC,tempValueD from threeelectricinfo where smokeImei = ? ORDER BY  heartime desc LIMIT 1";
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sql);
			ResultSet rs = null;
			List<OneElectricEntity.ElectricBean> listElectricBean=null;
			try {
				ps.setString(1, smokeMac);
				rs = ps.executeQuery();
				OneElectricEntity.ElectricBean mElectricBean6 = new OneElectricEntity.ElectricBean();
				OneElectricEntity.ElectricBean mElectricBean7 = new OneElectricEntity.ElectricBean();
				OneElectricEntity.ElectricBean mElectricBean8 = new OneElectricEntity.ElectricBean();
				OneElectricEntity.ElectricBean mElectricBean9 = new OneElectricEntity.ElectricBean();
				List<OneElectricEntity.ElectricBean.ElectricValueBean> listBean6=new ArrayList<OneElectricEntity.ElectricBean.ElectricValueBean>();
				List<OneElectricEntity.ElectricBean.ElectricValueBean> listBean7=new ArrayList<OneElectricEntity.ElectricBean.ElectricValueBean>();
				List<OneElectricEntity.ElectricBean.ElectricValueBean> listBean8=new ArrayList<OneElectricEntity.ElectricBean.ElectricValueBean>();
				List<OneElectricEntity.ElectricBean.ElectricValueBean> listBean9=new ArrayList<OneElectricEntity.ElectricBean.ElectricValueBean>();
				while(rs.next()){
					if(mOneElectricEntity==null){
						mOneElectricEntity = new OneElectricEntity();
						listElectricBean = new ArrayList<OneElectricEntity.ElectricBean>();
					}
					OneElectricEntity.ElectricBean.ElectricValueBean mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean.setId(1);
					mElectricValueBean.setValue(rs.getString("voltageA"));
					if(map!=null&&map.containsKey(6)){
						mElectricValueBean.setElectricThreshold(map.get(6).get(0));
					}else{
						mElectricValueBean.setElectricThreshold("176"+"\\"+"242");
					}
					listBean6.add(mElectricValueBean);
					 mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean.setId(2);
					mElectricValueBean.setValue(rs.getString("voltageB"));
					if(map!=null&&map.containsKey(6)){
						mElectricValueBean.setElectricThreshold(map.get(6).get(0));
					}else{
						mElectricValueBean.setElectricThreshold("176"+"\\"+"242");
					}
					listBean6.add(mElectricValueBean);
					mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean.setId(3);
					mElectricValueBean.setValue(rs.getString("voltageC"));
					if(map!=null&&map.containsKey(6)){
						mElectricValueBean.setElectricThreshold(map.get(6).get(0));
					}else{
						mElectricValueBean.setElectricThreshold("176"+"\\"+"242");
					}
					listBean6.add(mElectricValueBean);
					mElectricBean6.setElectricTime(rs.getString("heartime"));
					mElectricBean6.setElectricType(6);
					mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean.setId(1);
					mElectricValueBean.setValue(rs.getString("electricityA"));
					if(map!=null&&map.containsKey(7)){
						mElectricValueBean.setElectricThreshold(map.get(7).get(0));
					}else{
						mElectricValueBean.setElectricThreshold("6");  //电流若没有阈值则默认为6
					}
					listBean7.add(mElectricValueBean);
					mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean.setId(2);
					mElectricValueBean.setValue(rs.getString("electricityB"));
					if(map!=null&&map.containsKey(7)){
						mElectricValueBean.setElectricThreshold(map.get(7).get(0));
					}else{
						mElectricValueBean.setElectricThreshold("6");  //电流若没有阈值则默认为6
					}
					listBean7.add(mElectricValueBean);
					mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean.setId(3);
					mElectricValueBean.setValue(rs.getString("electricityC"));
					if(map!=null&&map.containsKey(7)){
						mElectricValueBean.setElectricThreshold(map.get(7).get(0));
					}else{
						mElectricValueBean.setElectricThreshold("6");  //电流若没有阈值则默认为6
					}
					listBean7.add(mElectricValueBean);
					mElectricBean7.setElectricTime(rs.getString("heartime"));
					mElectricBean7.setElectricType(7);
					mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
					mElectricValueBean.setId(1);
					mElectricValueBean.setValue(rs.getString("surplusElectri"));
					if(map!=null&&map.containsKey(8)){
						mElectricValueBean.setElectricThreshold(map.get(8).get(0));
					}else{
						mElectricValueBean.setElectricThreshold("500");
					}
					listBean8.add(mElectricValueBean);
					mElectricBean8.setElectricTime(rs.getString("heartime"));
					mElectricBean8.setElectricType(8);
					
					if(devType.equals("80")||devType.equals("81")){
						mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
						mElectricValueBean.setId(1);
						mElectricValueBean.setValue(rs.getString("tempValueA"));
						if(map!=null&&map.containsKey(9)){
							mElectricValueBean.setElectricThreshold(map.get(9).get(0));
						}else{
							mElectricValueBean.setElectricThreshold("100");  //温度若没有阈值则默认为100
						}
						listBean9.add(mElectricValueBean);
						mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
						mElectricValueBean.setId(2);
						mElectricValueBean.setValue(rs.getString("tempValueB"));
						if(map!=null&&map.containsKey(9)){
							mElectricValueBean.setElectricThreshold(map.get(9).get(0));
						}else{
							mElectricValueBean.setElectricThreshold("100");  //温度若没有阈值则默认为100
						}
						listBean9.add(mElectricValueBean);
						mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
						mElectricValueBean.setId(3);
						mElectricValueBean.setValue(rs.getString("tempValueC"));
						if(map!=null&&map.containsKey(9)){
							mElectricValueBean.setElectricThreshold(map.get(9).get(0));
						}else{
							mElectricValueBean.setElectricThreshold("100");  //温度若没有阈值则默认为100
						}
						listBean9.add(mElectricValueBean);
						mElectricValueBean = new OneElectricEntity.ElectricBean.ElectricValueBean();
						mElectricValueBean.setId(4);
						mElectricValueBean.setValue(rs.getString("tempValueD"));
						if(map!=null&&map.containsKey(9)){
							mElectricValueBean.setElectricThreshold(map.get(9).get(0));
						}else{
							mElectricValueBean.setElectricThreshold("100");  //温度若没有阈值则默认为100
						}
						listBean9.add(mElectricValueBean);
						
						mElectricBean9.setElectricTime(rs.getString("heartime"));
						mElectricBean9.setElectricType(9);
					}
				}
				if(listElectricBean!=null){
					if(listBean6.size()>0){
						mElectricBean6.setElectricValue(listBean6);
						listElectricBean.add(mElectricBean6);
					}
					if(listBean7.size()>0){
						mElectricBean7.setElectricValue(listBean7);
						listElectricBean.add(mElectricBean7);
					}
					if(listBean8.size()>0){
						mElectricBean8.setElectricValue(listBean8);
						listElectricBean.add(mElectricBean8);
					}
					if(listBean9.size()>0){
						mElectricBean9.setElectricValue(listBean9);
						listElectricBean.add(mElectricBean9);
					}
					mOneElectricEntity.setElectric(listElectricBean);
					mOneElectricEntity.setErrorCode(0);
					mOneElectricEntity.setError("获取单个电气设备成功");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}

		return mOneElectricEntity;
	}

	@Override
	public ElectricEnergyEntity getElectricEnergyEntity(String smokeMac) {
		String sql = "SELECT imei,ActivePowerA,ActivePowerB,ActivePowerC,ReactivePowerA,ReactivePowerB,ReactivePowerC,ApparentPowerA,ApparentPowerB,ApparentPowerC,PowerFactorA,PowerFactorB,PowerFactorC,ActiveEnergyA,ActiveEnergyB,ActiveEnergyC,ReactiveEnergyA,ReactiveEnergyB,ReactiveEnergyC,ApparentEnergyA,ApparentEnergyB,ApparentEnergyC,shunt,shuntLink,shuntTemp,shuntCuPer,shuntRelevanceTime,HearTime from electricenergy where imei = ? ORDER BY HearTime desc LIMIT 1";
		ElectricEnergyEntity eee = null;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				eee = new ElectricEnergyEntity();
				eee.setImei(rs.getString("imei"));
				eee.setActivePowerA(rs.getString("ActivePowerA"));
				eee.setActivePowerB(rs.getString("ActivePowerB"));
				eee.setActivePowerC(rs.getString("ActivePowerC"));
				eee.setReactivePowerA(rs.getString("ReactivePowerA"));
				eee.setReactivePowerB(rs.getString("ReactivePowerB"));
				eee.setReactivePowerC(rs.getString("ReactivePowerC"));
				eee.setApparentPowerA(rs.getString("ApparentPowerA"));
				eee.setApparentPowerB(rs.getString("ApparentPowerB"));
				eee.setApparentPowerC(rs.getString("ApparentPowerC"));
				eee.setPowerFactorA(rs.getString("PowerFactorA"));
				eee.setPowerFactorB(rs.getString("PowerFactorB"));
				eee.setPowerFactorC(rs.getString("PowerFactorC"));
				eee.setActiveEnergyA(rs.getString("ActiveEnergyA"));
				eee.setActiveEnergyB(rs.getString("ActiveEnergyB"));
				eee.setActiveEnergyC(rs.getString("ActiveEnergyC"));
				eee.setReactiveEnergyA(rs.getString("ReactiveEnergyA"));
				eee.setReactiveEnergyB(rs.getString("ReactiveEnergyB"));
				eee.setReactiveEnergyC(rs.getString("ReactiveEnergyC"));
				eee.setApparentEnergyA(rs.getString("ApparentEnergyA"));
				eee.setApparentEnergyB(rs.getString("ApparentEnergyB"));
				eee.setApparentEnergyC(rs.getString("ApparentEnergyC"));
				eee.setShunt(rs.getInt("shunt"));
				eee.setShuntLink(rs.getInt("shuntLink"));
				eee.setShuntTemp(rs.getInt("shuntTemp"));
				eee.setShuntCuPer(rs.getInt("shuntCuPer"));
				eee.setShuntRelevanceTime(rs.getInt("shuntRelevanceTime"));
				eee.setDataTime(rs.getString("HearTime"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return eee;
	}

	@Override
	public ElectricEnergyEntity getElectricEnergyEntitys(String smokeMac, String page) {
		// TODO Auto-generated method stub
		return null;
	}

}
