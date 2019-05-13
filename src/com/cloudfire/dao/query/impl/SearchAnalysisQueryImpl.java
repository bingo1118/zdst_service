package com.cloudfire.dao.query.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.JDKKeyFactory.RSA;

import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.dao.query.SearchAnalysisQuery;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.query.SearchAnalysisInfo;
import com.cloudfire.entity.query.SearchAnalysisinfo1;
import com.cloudfire.until.Utils;

/**
 * @author cheng
 *2017-4-14
 *下午5:17:36
 */
public class SearchAnalysisQueryImpl implements SearchAnalysisQuery{
	private SmartControlDao mSmartControlDao;
	
	/**初始化报警次数统计图的数据 */
	public Integer getAlarmCountLg(List<String> areaIds,List<String> listalarmType,String type,SearchAnalysisinfo1 query){ //areaIds没有使用。
		String basicSQL = "select  count(*) as alarmcount from (  "+
	     "select"+
		 "  alarm.alarmTime,smoke.mac from "+
		"alarm,smoke"+
		" where alarm.smokeMac=smoke.mac and alarmType in( ";  //不是alarmFamily,而是alarmType
		if(listalarmType!=null){
			for(int i=0;i<listalarmType.size();i++){
				if(i==listalarmType.size()-1){
					basicSQL +=listalarmType.get(i)+")";
				}else{basicSQL +=listalarmType.get(i)+",";}
				
			}
			
		}
		
		if (query!=null) {
			if (!StringUtils.isBlank(type)) {
				basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
			}
			if (!StringUtils.isBlank(query.getYear())) {
				basicSQL +=" and YEAR(alarmTime)= '"+query.getYear()+"'";
			}
			if (!StringUtils.isBlank(query.getMonth())) {
				basicSQL +=" and MONTH(alarmTime)='"+query.getMonth()+"'";
			}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
				basicSQL += " and smoke.areaId=" + query.getAreaId();
			}
		}
		basicSQL +=" ) aa ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		int alarmCount = 0;
		try {
			rs = ppst.executeQuery();
			while(rs.next()){
				 alarmCount = rs.getInt("alarmCount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
			
		}
		
		return alarmCount;
	}
	
	
	/** 通过用户的id查询他所以拥有的设备 */
	public List<SearchAnalysisInfo> getAnalysisInfoData(List<String> areaIds,String type){
		List<SearchAnalysisInfo> list = new ArrayList<>();
		int smokeNumber = getSmoke(areaIds, type);
		int lossNumber = getLossSmoke(areaIds, type);
		int onLineNumber = smokeNumber-lossNumber;
		mSmartControlDao = new SmartControlDaoImpl();
		List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterAndSmokeNumber(areaIds, type);
		SearchAnalysisInfo info = new SearchAnalysisInfo();
			//info.setAreaName(areaIds.get(i));
			info.setSmokeNumber(smokeNumber);
			info.setLossNumber(lossNumber);
			info.setOnLineNumber(onLineNumber);
			info.setList2(areaAndRepeaters);
			list.add(info);
		
		return list;
	}
	
	public List<SearchAnalysisInfo> getAnalysisInfoData(List<String> areaIds,CountValue cv){
		List<SearchAnalysisInfo> list = new ArrayList<>();
		int smokeNumber = cv.getMacNum();
		int lossNumber = cv.getNoNetStater();
		int onLineNumber = smokeNumber-lossNumber;
		mSmartControlDao = new SmartControlDaoImpl();
		List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterAndSmoke(areaIds);
		SearchAnalysisInfo info = new SearchAnalysisInfo();
			//info.setAreaName(areaIds.get(i));
			info.setSmokeNumber(smokeNumber);
			info.setLossNumber(lossNumber);
			info.setOnLineNumber(onLineNumber);
			info.setList2(areaAndRepeaters);
			list.add(info);
		
		return list;
	}
	
	public List<SearchAnalysisInfo> getAnalysisInfoData(List<String> areaIds){
		List<SearchAnalysisInfo> list = new ArrayList<>();
		mSmartControlDao = new SmartControlDaoImpl();
		List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndSmokeInfo(areaIds);
		AreaAndRepeater cv = areaAndRepeaters.get(areaAndRepeaters.size()-1);
		int lossNumber = cv.getLossSmokeNumbers();
		int onLineNumber = cv.getOnLineSmokeNumbers();
		int smokeNumber = cv.getSmokeNumbers();
		SearchAnalysisInfo info = new SearchAnalysisInfo();
			//info.setAreaName(areaIds.get(i));
			info.setSmokeNumber(smokeNumber);
			info.setLossNumber(lossNumber);
			info.setOnLineNumber(onLineNumber);
			info.setList2(areaAndRepeaters);
			list.add(info);
		
		return list;
	}
	
	public int getSmoke(List<String> areaIds,String type){
		int smokeNumber = 0;
		/** 区域的代码 */
		StringBuffer sqlStr = new StringBuffer();
		int len = 0;
		if(areaIds!=null){
			len = areaIds.size();
		}
		if (len==0) {
//			return (Integer) null;
		}if (len == 1) {
			sqlStr.append(" and areaId in (?)");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					sqlStr.append(" and areaId in (?, ");
				}else if(i==(len-1)){
					sqlStr.append(" ?) ");
				}else{
					sqlStr.append(" ?, ");
				}
			}
		}
		
		String basicSQL = "select count(*) as number from smoke where deviceType= ? ";
		
		String allSQL = new String(basicSQL+sqlStr.toString());
		int deviceType = Integer.parseInt(type);
		 Connection conn = DBConnectionManager.getConnection();
		 PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		 ResultSet rs = null;
		 try {
			ppst.setInt(1, deviceType);
			for(int i=1;i<=len;i++){
				ppst.setInt(i+1, Integer.parseInt(areaIds.get(i-1)));
			}
			rs = ppst.executeQuery();
			while(rs.next()){
				smokeNumber = rs.getInt("number");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return smokeNumber;
	}
	
	public int getSmoke(List<String> areaIds){
		int smokeNumber = 0;
		/** 区域的代码 */
		StringBuffer sqlStr = new StringBuffer();
		int len = areaIds.size();
		if (len==0) {
			return (Integer) null;
		}if (len == 1) {
			sqlStr.append(" and areaId in (?)");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					sqlStr.append(" and areaId in (?, ");
				}else if(i==(len-1)){
					sqlStr.append(" ?) ");
				}else{
					sqlStr.append(" ?, ");
				}
			}
		}
		
		String basicSQL = "select count(*) as number from smoke where 1 = 1 ";
		
		String allSQL = new String(basicSQL+sqlStr.toString());
		 Connection conn = DBConnectionManager.getConnection();
		 PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		 ResultSet rs = null;
		 try {
			for(int i=1;i<=len;i++){
				ppst.setInt(i, Integer.parseInt(areaIds.get(i-1)));
			}
			rs = ppst.executeQuery();
			while(rs.next()){
				smokeNumber = rs.getInt("number");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return smokeNumber;
	}
	
	
	//获得掉线的数量
	public int getLossSmoke(List<String> areaIds,String type){
		int lossSmokeNumber = 0;
		/** 区域的代码 */
		StringBuffer sqlStr = new StringBuffer();
		int len = areaIds.size();
		if (len==0) {
			return (Integer) null;
		}if (len == 1) {
			sqlStr.append(" and areaId in (?)");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					sqlStr.append(" and areaId in (?, ");
				}else if(i==(len-1)){
					sqlStr.append(" ?) ");
				}else{
					sqlStr.append(" ?, ");
				}
			}
		}
		
		String basicSQL = "select count(*) as lossNumber from smoke where netState=0 and deviceType= ? ";
		
		String allSQL = new String(basicSQL+sqlStr.toString());
		int deviceType = Integer.parseInt(type);
		 Connection conn = DBConnectionManager.getConnection();
		 PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		 ResultSet rs = null;
		 try {
			ppst.setInt(1, deviceType);
			for(int i=1;i<=len;i++){
				ppst.setInt(i+1, Integer.parseInt(areaIds.get(i-1)));
			}
			rs = ppst.executeQuery();
			while(rs.next()){
				lossSmokeNumber = rs.getInt("lossNumber");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return lossSmokeNumber;
	}
	
	public int getLossSmoke(List<String> areaIds){
		int lossSmokeNumber = 0;
		/** 区域的代码 */
		StringBuffer sqlStr = new StringBuffer();
		int len = areaIds.size();
		if (len==0) {
			return (Integer) null;
		}if (len == 1) {
			sqlStr.append(" and areaId in (?)");
		}else{
			for(int i=0;i<len;i++){
				if(i==0){
					sqlStr.append(" and areaId in (?, ");
				}else if(i==(len-1)){
					sqlStr.append(" ?) ");
				}else{
					sqlStr.append(" ?, ");
				}
			}
		}
		
		String basicSQL = "select count(*) as lossNumber from smoke where netState=0";
		
		String allSQL = new String(basicSQL+sqlStr.toString());
		 Connection conn = DBConnectionManager.getConnection();
		 PreparedStatement ppst = DBConnectionManager.prepare(conn, allSQL);
		 ResultSet rs = null;
		 try {
			for(int i=1;i<=len;i++){
				ppst.setInt(i, Integer.parseInt(areaIds.get(i-1)));
			}
			rs = ppst.executeQuery();
			while(rs.next()){
				lossSmokeNumber = rs.getInt("lossNumber");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return lossSmokeNumber;
	}
	
	/**查询所有的年份 */
	public List<String> getAllYear(){
		List<String> list = new ArrayList<>();
		String basicSQL="select DISTINCT (DATE_FORMAT(alarmTime,'%Y')) as m from alarm ORDER BY m desc";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			while(rs.next()){
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return list;
	}
	
	/**初始化报警次数统计图的数据 */
	public Integer getAlarmCount202(List<String> areaIds,String type,SearchAnalysisinfo1 query){
		String basicSQL = "select  count(*) as alarmcount from (  "+
	     "select"+
		 "  alarm.alarmTime,smoke.mac from "+
		"alarm,smoke"+
		" where alarm.smokeMac=smoke.mac and alarmType=202 ";
		if (query!=null) {
			if (!StringUtils.isBlank(type)) {
				basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
			}
			if (!StringUtils.isBlank(query.getYear())) {
				basicSQL +=" and YEAR(alarmTime)= '"+query.getYear()+"'";
			}
			if (!StringUtils.isBlank(query.getMonth())) {
				basicSQL +=" and MONTH(alarmTime)='"+query.getMonth()+"'";
			}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
				basicSQL += " and smoke.areaId=" + query.getAreaId();
			}
		}
		basicSQL +=" ) aa ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
		ResultSet rs = null;
		int alarmCount = 0;
		try {
			rs = ppst.executeQuery();
			while(rs.next()){
				 alarmCount = rs.getInt("alarmCount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
			
		}
		
		return alarmCount;
	}
	
	public Integer getAlarmCount193(String type ,SearchAnalysisinfo1 query){
		String basicSQL = "select count(*) as alarmCount from ( "+
			     "select"+
				 "  alarm.alarmTime,smoke.mac from "+
				"alarm,smoke"+
				" where alarmType=193 and alarm.smokeMac=smoke.mac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(alarmTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(alarmTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +=" ) aa ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				int alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("alarmCount");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	
	public Integer getAlarmCount43(String type ,SearchAnalysisinfo1 query){
		String basicSQL = "select count(*) as alarmCount from ( "+
			     "select"+
				 "  alarm.alarmTime,smoke.mac from "+
				"alarm,smoke"+
				" where alarmFamily=43 and alarm.smokeMac=smoke.mac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(alarmTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(alarmTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +=" ) aa ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				int alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("alarmCount");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	
	public Integer getAlarmCount36(List<String> areaIds,String type,SearchAnalysisinfo1 query){
		int alarmCount = 0;
		
		String basicSQL = "select  count(*) as alarmcount from (  "+
			     "select"+
				 "  alarm.alarmTime,smoke.mac from "+
				"alarm,smoke"+
				" where alarm.smokeMac=smoke.mac and alarmType=36 ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(alarmTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(alarmTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +=" ) aa ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("alarmCount");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
		
		return alarmCount;
	}
	
	public Integer getAlarmCount44(String type ,SearchAnalysisinfo1 query){
		String basicSQL = "select count(*) as alarmCount from ( "+
			     "select"+
				 "  alarm.alarmTime,smoke.mac from "+
				"alarm,smoke"+
				" where alarmFamily=44 and alarm.smokeMac=smoke.mac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(alarmTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(alarmTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +=" ) aa ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				int alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("alarmCount");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	
	public Integer getAlarmCount45(String type ,SearchAnalysisinfo1 query){
		String basicSQL = "select count(*) as alarmCount from ( "+
			     "select"+
				 "  alarm.alarmTime,smoke.mac from "+
				"alarm,smoke"+
				" where alarmFamily=45 and alarm.smokeMac=smoke.mac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(alarmTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(alarmTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +=" ) aa ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				int alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("alarmCount");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	
	public Integer getAlarmCount46(String type ,SearchAnalysisinfo1 query){
		String basicSQL = "select count(*) as alarmCount from ( "+
			     "select"+
				 "  alarm.alarmTime,smoke.mac from "+
				"alarm,smoke"+
				" where alarmFamily=46 and alarm.smokeMac=smoke.mac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(alarmTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(alarmTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +=" ) aa ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				int alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("alarmCount");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	
	public Integer getAlarmCount47(String type ,SearchAnalysisinfo1 query){
		String basicSQL = "select count(*) as alarmCount from ( "+
			     "select"+
				 "  alarm.alarmTime,smoke.mac from "+
				"alarm,smoke"+
				" where alarmFamily=47 and alarm.smokeMac=smoke.mac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(alarmTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(alarmTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +=" ) aa ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				int alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("alarmCount");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	
	public Integer getAlarmCount36(String type ,SearchAnalysisinfo1 query){
		String basicSQL = "select count(*) as alarmCount from ( "+
			     "select"+
				 "  alarm.alarmTime,smoke.mac from "+
				"alarm,smoke"+
				" where alarmFamily=36 and alarm.smokeMac=smoke.mac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(alarmTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(alarmTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +=" ) aa ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				int alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("alarmCount");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	
	/** *************************下面是个各的平均数据 *****************/
	public Float getAlarmCount43and44avg(String type ,SearchAnalysisinfo1 query){
		String basicSQL = 
			     "select"+
				 "  FORMAT( avg(electricinfo.electricValue1),3) as num from "+
				"electricinfo,smoke"+
				" where electricinfo.electricType=6 and smoke.mac = electricinfo.smokeMac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(electricinfo.electricTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(electricinfo.electricTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +="  ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				float alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("num");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	
	public Float getAlarmCount45avg(String type ,SearchAnalysisinfo1 query){
		String basicSQL = 
			     "select"+
				 "  FORMAT( avg(electricinfo.electricValue1),3) as num from "+
				"electricinfo,smoke"+
				" where electricinfo.electricType=7 and smoke.mac = electricinfo.smokeMac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(electricinfo.electricTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(electricinfo.electricTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +="  ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				float alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("num");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	
	public Float getAlarmCount46avg(String type ,SearchAnalysisinfo1 query){
		String basicSQL = 
			     "select"+
				 "  FORMAT( avg(electricinfo.electricValue1),3) as num from "+
				"electricinfo,smoke"+
				" where electricinfo.electricType=8 and smoke.mac = electricinfo.smokeMac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(electricinfo.electricTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(electricinfo.electricTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +="  ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				float alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("num");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	
	public Float getAlarmCount47avg(String type ,SearchAnalysisinfo1 query){
		String basicSQL = 
			     "select"+
				 "  FORMAT( avg(electricinfo.electricValue1),3) as num from "+
				"electricinfo,smoke"+
				" where electricinfo.electricType=9 and smoke.mac = electricinfo.smokeMac ";
				if (query!=null) {
					if (!StringUtils.isBlank(type)) {
						basicSQL +=" and smoke.deviceType="+Integer.parseInt(type);
					}
					if (!StringUtils.isBlank(query.getYear())) {
						basicSQL +=" and YEAR(electricinfo.electricTime)= '"+query.getYear()+"'";
					}
					if (!StringUtils.isBlank(query.getMonth())) {
						basicSQL +=" and MONTH(electricinfo.electricTime)='"+query.getMonth()+"'";
					}if (!StringUtils.isBlank(String.valueOf(query.getAreaId()))) {
						basicSQL += " and smoke.areaId=" + query.getAreaId();
					}
				}
				basicSQL +="  ";
				Connection conn = DBConnectionManager.getConnection();
				PreparedStatement ppst = DBConnectionManager.prepare(conn, basicSQL);
				ResultSet rs = null;
				float alarmCount = 0;
				try {
					rs = ppst.executeQuery();
					while(rs.next()){
						 alarmCount = rs.getInt("num");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DBConnectionManager.close(rs);
					DBConnectionManager.close(ppst);
					DBConnectionManager.close(conn);
					
				}
				
				return alarmCount;
	}
	

}


































