package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mortbay.log.Log;

import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AllElectricInfoEntity;
import com.cloudfire.entity.ElectricBean;
import com.cloudfire.entity.ElectricEnergyEntity;
import com.cloudfire.entity.ElectricInfo;
import com.cloudfire.entity.ElectricPCBean;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.entity.ProofGasHistoryEntity;
import com.cloudfire.entity.ThreePhaseElectricEntity;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Utils;
import com.sun.org.apache.regexp.internal.recompile;

public class ElectricTypeInfoDaoImpl implements ElectricTypeInfoDao{

	public ElectricInfo<ElectricBean> getElectricTypeInfo(String userId,
			String privilege, String smokeMac, String electricType,
			String electricNum, String page) {
		StringBuffer strSql = new StringBuffer();
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*6;
				int endNum = 6;
				strSql.append(" order by electricTime desc limit "+startNum+" , "+endNum);
			}
		}else{
			strSql.append(" order by electricTime desc ");
		}
		
		String sql= null;
		int num =0;
		if(Utils.isNumeric(electricNum)){
			num = Integer.parseInt(electricNum);
		}else{
			return null;
		}
		switch (num) {
		case 1:
			sql = "select electricTime,electricValue1 from electricinfo where smokeMac=? and electricType=? ";
			break;
		case 2:
			
			sql = "select electricTime,electricValue2 from electricinfo where smokeMac=? and electricType=? ";		
			
			break;
		case 3:
			sql = "select electricTime,electricValue3 from electricinfo where smokeMac=? and electricType=? ";
			break;
		case 4:
			sql = "select electricTime,electricValue4 from electricinfo where smokeMac=? and electricType=? ";
			break;
		default:
			return null;
		}
		String sqlStr = new String(sql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		ElectricInfo<ElectricBean> aeie = null;
		List<ElectricBean> list= null;
		try {
			ps.setString(1, smokeMac);
			RePeaterDataDao redao = new RePeaterDataDaoImpl(); //根据mac查询设型号
			int devTypeNum = redao.getDeviceTypeNum(smokeMac);
			if(devTypeNum==3){
				ps.setString(2, electricType+electricNum);
			}else{
				ps.setString(2, electricType);
			}//@@2018.01.22
//			ps.setString(2, electricType);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<ElectricBean>();
				}
				ElectricBean mElectricBean = new ElectricBean();
				mElectricBean.setElectricTime(rs.getString(1));
				String value = rs.getString(2);
				if(value!=null&&value.length()>0){
					mElectricBean.setElectricValue(value);
				}
				list.add(mElectricBean);
			}
			if(list!=null&&list.size()>0){
				aeie = new ElectricInfo<ElectricBean>();
				aeie.setElectric(list);
				aeie.setErrorCode(0);
				aeie.setError("获取电气属性信息成功");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aeie;
	}
	
	//@@2017.12.15 获取水压历史信息
	public ElectricInfo<ElectricBean> getWaterHistoryInfo(String userId,
			String privilege, String smokeMac, String page) {
		StringBuffer strSql = new StringBuffer();
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*6;
				int endNum = 6;
				strSql.append(" order by time desc limit "+startNum+" , "+endNum);
			}
		}else{
			strSql.append(" order by time desc ");
		}
		
		String sql= "select time,value from waterinfo where waterMac=? ";
		
		String sqlStr = new String(sql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		ElectricInfo<ElectricBean> aeie = null;
		List<ElectricBean> list= null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<ElectricBean>();
				}
				ElectricBean mElectricBean = new ElectricBean();
				mElectricBean.setElectricTime(rs.getString(1));
				String value = rs.getString(2);
				if(value!=null&&value.length()>0){
					mElectricBean.setElectricValue(value);
				}
				list.add(mElectricBean);
			}
			if(list!=null&&list.size()>0){
				aeie = new ElectricInfo<ElectricBean>();
				aeie.setElectric(list);
				aeie.setErrorCode(0);
				aeie.setError("获取信息成功");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aeie;
	}
	
	//@@2018.04.24 获取温湿度历史信息
		public ElectricInfo<ElectricBean> getTHDevInfoHistoryInfo(String smokeMac, String page,String type) {
			StringBuffer strSql = new StringBuffer();
			if(page!=null&&page.length()>0){
				int pageInt= Integer.parseInt(page);
				if(pageInt>0){
					int startNum = (pageInt-1)*6;
					int endNum = 6;
					strSql.append(" order by time desc limit "+startNum+" , "+endNum);
				}
			}else{
				strSql.append(" order by time desc ");
			}
			
			String sql= "select time,value from th_info where mac=? ";
			if(type.equals("1")){
				sql="select time,temperature from th_info where mac=? ";
			}else{
				sql="select time,humidity from th_info where mac=? ";
			}
			
			String sqlStr = new String(sql+strSql.toString());
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
			ResultSet rs = null;
			ElectricInfo<ElectricBean> aeie = null;
			List<ElectricBean> list= null;
			try {
				ps.setString(1, smokeMac);
				rs = ps.executeQuery();
				while(rs.next()){
					if(list==null){
						list = new ArrayList<ElectricBean>();
					}
					ElectricBean mElectricBean = new ElectricBean();
					mElectricBean.setElectricTime(rs.getString(1));
					String value = rs.getString(2);
					if(value!=null&&value.length()>0){
						mElectricBean.setElectricValue(value);
					}
					list.add(mElectricBean);
				}
				if(list!=null&&list.size()>0){
					aeie = new ElectricInfo<ElectricBean>();
					aeie.setElectric(list);
					aeie.setErrorCode(0);
					aeie.setError("获取信息成功");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
			return aeie;
		}
	
	//@@2018.04.10 获取创安燃气历史信息
		public ElectricInfo<ElectricBean> getChuanganHistoryInfo(String userId,
				String privilege, String smokeMac, String page,String electricNum) {
			StringBuffer strSql = new StringBuffer();
			if(page!=null&&page.length()>0){
				int pageInt= Integer.parseInt(page);
				if(pageInt>0){
					int startNum = (pageInt-1)*6;
					int endNum = 6;
					strSql.append(" order by time desc limit "+startNum+" , "+endNum);
				}
			}else{
				strSql.append(" order by time desc ");
			}
			
			String sql= "select time,value from chuangan_info where mac=? and line=? and state=0 ";
			
			String sqlStr = new String(sql+strSql.toString());
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
			ResultSet rs = null;
			ElectricInfo<ElectricBean> aeie = null;
			List<ElectricBean> list= null;
			try {
				ps.setString(1, smokeMac);
				ps.setString(2, electricNum);
				rs = ps.executeQuery();
				while(rs.next()){
					if(list==null){
						list = new ArrayList<ElectricBean>();
					}
					ElectricBean mElectricBean = new ElectricBean();
					mElectricBean.setElectricTime(rs.getString(1));
					String value = rs.getString(2);
					if(value!=null&&value.length()>0){
						mElectricBean.setElectricValue(value);
					}
					list.add(mElectricBean);
				}
				if(list!=null&&list.size()>0){
					aeie = new ElectricInfo<ElectricBean>();
					aeie.setElectric(list);
					aeie.setErrorCode(0);
					aeie.setError("获取信息成功");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(rs);
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
			return aeie;
		}
	
	public ElectricInfo<ElectricPCBean> getElectricPCTypeInfo(String userId,
			String privilege, String smokeMac, String electricType,
			String electricNum, String page,String electricDate) {
		if(!Utils.isNullStr(electricDate)){
			return null;
		}
		StringBuffer strSql = new StringBuffer();
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*6;
				int endNum = 6;
				strSql.append(" order by electricTime desc limit "+startNum+" , "+endNum);
			}
		}else{
			strSql.append(" order by electricTime desc ");
		}
		
		String sql = "select electricTime,electricValue1,electricValue2,electricValue3,electricValue4 from electricinfo where smokeMac=? " +
				"and electricType=? and electricTime like ? ";

		String sqlStr = new String(sql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		ElectricInfo<ElectricPCBean> aeie = null;
		List<ElectricPCBean> list= null;
		try {
			ps.setString(1, smokeMac);
			RePeaterDataDao redao = new RePeaterDataDaoImpl(); //根据mac查询设型号
			int devTypeNum = redao.getDeviceTypeNum(smokeMac);
			if(devTypeNum==3){
				ps.setString(2, electricType+electricNum);
			}else{
				ps.setString(2, electricType);
			}//@@2018.01.22
			
			if(Utils.isNullStr(electricDate)){
				ps.setString(3, electricDate+"%");
			}
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<ElectricPCBean>();
				}
				ElectricPCBean mElectricBean = new ElectricPCBean();
				mElectricBean.setElectricTime(rs.getString(1));
				mElectricBean.setElectricValue1(rs.getString(2));
				mElectricBean.setElectricValue2(rs.getString(3));
				mElectricBean.setElectricValue3(rs.getString(4));
				mElectricBean.setElectricValue4(rs.getString(5));
				list.add(mElectricBean);
			}
			if(list!=null&&list.size()>0){
				aeie = new ElectricInfo<ElectricPCBean>();
				aeie.setElectric(list);
				aeie.setErrorCode(0);
				aeie.setError("获取电气属性信息成功");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aeie;
	}

	//list6 电压 list7电流 list8漏电流 list9 温度
	public void addElectricInfo(List<String> list6, List<String> list7,
			String list8, List<String> list9, String electricMac,
			String repeaterMac, int electricDevType) {
		String sql = "insert into electricinfo (smokeMac,electricValue1,electricValue2,electricValue3,electricValue4," +
				"electricTime,repeaterMac,electricType,electricDevType) values(?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps=null;
		String addTime = GetTime.ConvertTimeByLong();
		System.out.println("====>>>>>>>>>addTime:"+addTime+"    nowTime:"+System.currentTimeMillis()+"<<<<<<<<<<=========");
		try {
			conn.setAutoCommit(false);
			ps = DBConnectionManager.prepare(conn,sql);
			switch (electricDevType) {
			case 1://Bq200 的electricDevType 2, Bq100 1，诚佳电气 3.
				if(Utils.isNullStr(list8)){
					for(int i=0;i<3;i++){  // update by lzo at 2017-6-27
						ps.setString(1, electricMac);
						if(i==0){
							ps.setString(2, list6.get(0));
							ps.setInt(8, 6);
						}else if(i == 1){
							ps.setString(2, list7.get(0));
							ps.setInt(8, 7);
						}else{
							ps.setString(2,list8);
							ps.setInt(8, 8);
						}
						ps.setString(3, "");
						ps.setString(4, "");
						ps.setString(5, "");
						ps.setString(6, addTime);
						ps.setString(7, repeaterMac);
						ps.setInt(9, 1);
						ps.addBatch();
					}
				}else{
					for(int i=0;i<2;i++){
						ps.setString(1, electricMac);
						if(i==0){
							ps.setString(2, list6.get(0));
							ps.setInt(8, 6);
						}else{
							ps.setString(2, list7.get(0));
							ps.setInt(8, 7);
						}
						ps.setString(3, "");
						ps.setString(4, "");
						ps.setString(5, "");
						ps.setString(6, addTime);
						ps.setString(7, repeaterMac);
						ps.setInt(9, 1);
						ps.addBatch();
						
					}
				}
				break;
			case 2://Bq100
				for(int i=0;i<4;i++){
					ps.setString(1, electricMac);
					switch (i) {
						case 0:
							int len6 = list6.size();
							int count6 = 4-len6;
							for(int j=0;j<len6;j++){
								ps.setString(j+2, list6.get(j));
							}
							for(int g=0;g<count6;g++){
								ps.setString(g+2+len6, "");
							}
							ps.setInt(8, 6);
							break;
						case 1:
							int len7 = list7.size();
							int count7 = 4-len7;
							for(int j=0;j<len7;j++){
								ps.setString(j+2, list7.get(j));
							}
							for(int g=0;g<count7;g++){
								ps.setString(g+2+len7, "");
							}
							ps.setInt(8, 7);
							break;
						case 2:
							ps.setString(2, list8);
							ps.setString(3, "");
							ps.setString(4, "");
							ps.setString(5, "");
							ps.setInt(8, 8);
							break;
						case 3:
							int len9 = list9.size();
							int count9 = 4-len9;
							for(int j=0;j<len9;j++){
								ps.setString(j+2, list9.get(j));
							}
							for(int g=0;g<count9;g++){
								ps.setString(g+2+len9, "");
							}
							ps.setInt(8, 9);
							break;
					}
					ps.setString(6, addTime);
					ps.setString(7, repeaterMac);
					ps.setInt(9, 2);
					ps.addBatch();
				}
				break;
			case 3: //诚佳电气
				ps.setString(1, electricMac);
				ps.setString(2, list7.get(0));
				ps.setString(3,"");
				ps.setString(4, "");
				ps.setString(5, "");
				ps.setString(6, addTime);
				ps.setString(7, repeaterMac);
				ps.setInt(8, 7);
				ps.setInt(9, 3);
				ps.addBatch();
				break;
			}
			int[] result = ps.executeBatch(); 
	        conn.commit(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	public void addAlarmElectric(List<String> list, String electricMac,
			String repeaterMac, int electricDevType, int electricType) {
		String sql = "insert into electricinfo (smokeMac,electricValue1,electricValue2,electricValue3,electricValue4," +
				"electricTime,repeaterMac,electricType,electricDevType) values(?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps= DBConnectionManager.prepare(conn,sql);
		String addTime = GetTime.ConvertTimeByLong();
		try {
			ps.setString(1, electricMac);
			int len9 = list.size();
			int count9 = 4-len9;
			for(int j=0;j<len9;j++){
				ps.setString(j+2, list.get(j));
			}
			for(int g=0;g<count9;g++){
				ps.setString(g+2+len9, "");
			}
			ps.setString(6, addTime);
			ps.setString(7, repeaterMac);
			ps.setInt(8, electricType);
			ps.setInt(9, electricDevType);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	
	public void addThreePhaseElectricInfo(String data, String electricMac,
			String repeaterMac, int data_type,int data_num) {
		String sql = "insert into electricinfo (smokeMac,electricValue1,electricValue2,electricValue3,electricValue4," +
				"electricTime,repeaterMac,electricType,electricDevType) values(?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps=null;
		String addTime = GetTime.ConvertTimeByLong();
		try {
			conn.setAutoCommit(false);
			ps = DBConnectionManager.prepare(conn,sql);
			ps.setString(1, electricMac);
			ps.setString(2, "");
			ps.setString(3,"");
			ps.setString(4, "");
			ps.setString(5, "");
			switch(data_num){
				case 1:
					ps.setString(2, data);
					break;
				case 2:
					ps.setString(3, data);
					break;
				case 3:
					ps.setString(4, data);
					break;
			}
			ps.setString(6, addTime);
			ps.setString(7, repeaterMac);
			ps.setInt(8, data_type);
			ps.setInt(9, 5);//@@5 三相电表
			ps.addBatch();
			ps.executeBatch(); 
	        conn.commit(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}
	
	
	public ProofGasHistoryEntity getProofGasHistoryInfo(String userId,
			String privilege, String smokeMac, String page) {
		StringBuffer strSql = new StringBuffer();
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*6;
				int endNum = 6;
				strSql.append(" order by proofGasTime desc limit "+startNum+" , "+endNum);
			}else if(pageInt==0){
				strSql.append(" order by proofGasTime desc limit 1 ");
			}
		}else{
			strSql.append(" order by proofGasTime desc ");
		}
		
		String sql= "select * from g_proofgas where proofGasMac=? ";
		
		String sqlStr = new String(sql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		ProofGasHistoryEntity aeie = null;
		List<ProofGasEntity> list= null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<ProofGasEntity>();
				}
				ProofGasEntity mProofGasBean = new ProofGasEntity();
				mProofGasBean.setProofGasMac(rs.getString("proofGasMac"));
				mProofGasBean.setProofGasType(rs.getString("proofGasType"));
				mProofGasBean.setProofGasMmol(rs.getString("proofGasMmol"));
				mProofGasBean.setProofGasTemp(rs.getString("proofGasTemp"));
				mProofGasBean.setProofGasModel(rs.getString("proofGasModel"));
				mProofGasBean.setProofGasUnit(rs.getString("proofGasUnit"));
				mProofGasBean.setProofGasState(rs.getString("proofGasState"));
				mProofGasBean.setProofGasTime(rs.getString("proofGasTime"));
				
				list.add(mProofGasBean);
			}
			if(list!=null&&list.size()>0){
				aeie = new ProofGasHistoryEntity();
				aeie.setHistory(list);
				aeie.setErrorCode(0);
				aeie.setError("获取信息成功");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aeie;
	}

	@Override
	public void addThreePhaseElectricInfo(ThreePhaseElectricEntity threeElectric) {
		if(threeElectric==null){
			return;
		}
		String imsi = threeElectric.getImsiStr();
		if(StringUtils.isBlank(imsi)){
			imsi = threeElectric.getImeiStr();
		}
		String smokeImei = threeElectric.getImeiStr();
		String voltageA = threeElectric.getVoltageA();
		String voltageB = threeElectric.getVoltageB();
		String voltageC = threeElectric.getVoltageC();
		String electricityA = threeElectric.getElectricityA();
		String electricityB = threeElectric.getElectricityB();
		String electricityC = threeElectric.getElectricityC();
		String surplusElectri = threeElectric.getSurplusElectri();
		String currentMaximum = "0";
		String runAlarmState = threeElectric.getRunAlarmState();
		String runGateState = threeElectric.getRunGateState();
		String runCauseState = threeElectric.getRunCauseState();
		String heartime = threeElectric.getHeartime();
		if(StringUtils.isBlank(heartime)){
			heartime = GetTime.ConvertTimeByLong();
		}
		String tempValueA = threeElectric.getTempValueA();
		String tempValueB = threeElectric.getTempValueB();
		String tempValueC = threeElectric.getTempValueC();
		String tempValueD = threeElectric.getTempValueD();
		String sql = "insert into threeelectricinfo(smokeImei,smokeImsi,voltageA,voltageB,voltageC,electricityA,electricityB,electricityC,surplusElectri,currentMaximum,runAlarmState,runGateState,runCauseState,heartime,tempValueA,tempValueB,tempValueC,tempValueD) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(18, tempValueD);
			ps.setString(17, tempValueC);
			ps.setString(16, tempValueB);
			ps.setString(15, tempValueA);
			ps.setString(14, heartime);
			ps.setString(13, runCauseState);
			ps.setString(12, runGateState);
			ps.setString(11, runAlarmState);
			ps.setString(10, "0");
			ps.setString(9, surplusElectri);
			ps.setString(8, electricityC);
			ps.setString(7, electricityB);
			ps.setString(6, electricityA);
			ps.setString(5, voltageC);
			ps.setString(4, voltageB);
			ps.setString(3, voltageA);
			ps.setString(2, imsi);
			ps.setString(1, smokeImei);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

	@Override
	public ElectricInfo getThreeElectricTypeInfo(String userId,
			String privilege, String smokeMac, String electricType,
			String electricNum, String page,String devType) {
		StringBuffer strSql = new StringBuffer();
		if(page!=null&&page.length()>0){
			int pageInt= Integer.parseInt(page);
			if(pageInt>0){
				int startNum = (pageInt-1)*6;
				int endNum = 6;
				strSql.append(" order by heartime desc limit "+startNum+" , "+endNum);
			}
		}else{
			strSql.append(" order by heartime desc ");
		}
		
		String sql= null;
		int num =0;
		if(Utils.isNumeric(electricNum)){
			num = Integer.parseInt(electricNum);
		}else{
			return null;
		}
		switch (electricType) {
		case "6":
			sql = "SELECT voltageA,voltageB,voltageC,heartime from threeelectricinfo where smokeImei = ? ";
			break;
		case "7":
			sql = "SELECT electricityA,electricityB,electricityC,heartime from threeelectricinfo where smokeImei = ? ";		
			break;
		case "8":
			sql = "SELECT surplusElectri,heartime from threeelectricinfo where smokeImei = ? ";
			break;
		case "9":
			sql = "SELECT tempValueA,tempValueB,tempValueC,tempValueD,heartime from threeelectricinfo where smokeImei = ? ";
			break;
		default:
			return null;
		}
		String sqlStr = new String(sql+strSql.toString());
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlStr);
		ResultSet rs = null;
		ElectricInfo<ElectricBean> aeie = null;
		List<ElectricBean> list= null;
		try {
			ps.setString(1, smokeMac);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list = new ArrayList<ElectricBean>();
				}
				ElectricBean mElectricBean = new ElectricBean();
				mElectricBean.setElectricTime(rs.getString("heartime"));
				String value = "";rs.getString(2);
				switch(num){
				case 1:
					value = rs.getString(1);
					break;
				case 2:
					value = rs.getString(2);
					break;
				case 3:
					value = rs.getString(3);
					break;
				case 4:
					value = rs.getString(4);
					break;
				}
				
				if(value!=null&&value.length()>0){
					mElectricBean.setElectricValue(value);
				}
				list.add(mElectricBean);
			}
			if(list!=null&&list.size()>0){
				aeie = new ElectricInfo<ElectricBean>();
				aeie.setElectric(list);
				aeie.setErrorCode(0);
				aeie.setError("获取电气属性信息成功");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return aeie;
	}

	public boolean saveElectricEnergyEntity(ElectricEnergyEntity eee){
		boolean result = false;
		String sql = "insert into electricenergy(imei,ActivePowerA,ActivePowerB,ActivePowerC,ReactivePowerA,ReactivePowerB,ReactivePowerC,ApparentPowerA,ApparentPowerB,ApparentPowerC,PowerFactorA,PowerFactorB,PowerFactorC,ActiveEnergyA,ActiveEnergyB,ActiveEnergyC,ReactiveEnergyA,ReactiveEnergyB,ReactiveEnergyC,ApparentEnergyA,ApparentEnergyB,ApparentEnergyC,shunt,shuntLink,shuntTemp,shuntCuPer,shuntRelevanceTime,HearTime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		String HearTime = GetTime.ConvertTimeByLong();
		try {
			ps.setString(28, HearTime);
			ps.setInt(27, eee.getShuntRelevanceTime());
			ps.setInt(26, eee.getShuntCuPer());
			ps.setInt(25, eee.getShuntTemp());
			ps.setInt(24, eee.getShuntLink());
			ps.setInt(23, eee.getShunt());
			ps.setString(22, eee.getApparentEnergyC());
			ps.setString(21, eee.getApparentEnergyB());
			ps.setString(20, eee.getApparentEnergyA());
			ps.setString(19, eee.getReactiveEnergyC());
			ps.setString(18, eee.getReactiveEnergyB());
			ps.setString(17, eee.getReactiveEnergyA());
			ps.setString(16, eee.getActiveEnergyC());
			ps.setString(15, eee.getActiveEnergyB());
			ps.setString(14, eee.getActiveEnergyA());
			ps.setString(13, eee.getPowerFactorC());
			ps.setString(12, eee.getPowerFactorB());
			ps.setString(11, eee.getPowerFactorA());
			ps.setString(10, eee.getApparentPowerC());
			ps.setString(9, eee.getApparentPowerB());
			ps.setString(8, eee.getApparentPowerA());
			ps.setString(7, eee.getReactivePowerC());
			ps.setString(6, eee.getReactivePowerB());
			ps.setString(5, eee.getReactivePowerA());
			ps.setString(4, eee.getActivePowerC());
			ps.setString(3, eee.getActivePowerB());
			ps.setString(2, eee.getActivePowerA());
			ps.setString(1, eee.getImei());
			int ret = ps.executeUpdate();
			if(ret>0){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}
	
	public boolean saveElectricEnergyEntity(ElectricEnergyEntity eee,String hearTime){
		boolean result = false;
		String sql = "insert into electricenergy(imei,ActivePowerA,ActivePowerB,ActivePowerC,ReactivePowerA,ReactivePowerB,ReactivePowerC,ApparentPowerA,ApparentPowerB,ApparentPowerC,PowerFactorA,PowerFactorB,PowerFactorC,ActiveEnergyA,ActiveEnergyB,ActiveEnergyC,ReactiveEnergyA,ReactiveEnergyB,ReactiveEnergyC,ApparentEnergyA,ApparentEnergyB,ApparentEnergyC,shunt,shuntLink,shuntTemp,shuntCuPer,shuntRelevanceTime,HearTime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		if(StringUtils.isBlank(hearTime)){
			hearTime = GetTime.ConvertTimeByLong();
		}
		try {
			ps.setString(28, hearTime);
			ps.setInt(27, eee.getShuntRelevanceTime());
			ps.setInt(26, eee.getShuntCuPer());
			ps.setInt(25, eee.getShuntTemp());
			ps.setInt(24, eee.getShuntLink());
			ps.setInt(23, eee.getShunt());
			ps.setString(22, eee.getApparentEnergyC());
			ps.setString(21, eee.getApparentEnergyB());
			ps.setString(20, eee.getApparentEnergyA());
			ps.setString(19, eee.getReactiveEnergyC());
			ps.setString(18, eee.getReactiveEnergyB());
			ps.setString(17, eee.getReactiveEnergyA());
			ps.setString(16, eee.getActiveEnergyC());
			ps.setString(15, eee.getActiveEnergyB());
			ps.setString(14, eee.getActiveEnergyA());
			ps.setString(13, eee.getPowerFactorC());
			ps.setString(12, eee.getPowerFactorB());
			ps.setString(11, eee.getPowerFactorA());
			ps.setString(10, eee.getApparentPowerC());
			ps.setString(9, eee.getApparentPowerB());
			ps.setString(8, eee.getApparentPowerA());
			ps.setString(7, eee.getReactivePowerC());
			ps.setString(6, eee.getReactivePowerB());
			ps.setString(5, eee.getReactivePowerA());
			ps.setString(4, eee.getActivePowerC());
			ps.setString(3, eee.getActivePowerB());
			ps.setString(2, eee.getActivePowerA());
			ps.setString(1, eee.getImei());
			int ret = ps.executeUpdate();
			if(ret>0){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public boolean getElectricEnergyEntity(ElectricEnergyEntity eee) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getElectricEnergyEntitys(ElectricEnergyEntity eee) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
