package com.cloudfire.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.StatisticDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.CountValue;
import com.cloudfire.entity.FireBean;
import com.cloudfire.entity.StatisticAnalysisEntity;
import com.cloudfire.entity.StatisticBean;
import com.cloudfire.until.Constant;
import com.cloudfire.until.MyUtils;

public class StatisticDaoImpl implements StatisticDao{


	/*
	 * deviceType 代表设备所属系统，1 火警 2 水系统（10，15，18，19）3电气（5）
	 * @see com.cloudfire.dao.StatisticDao#getStatistic(int)
	 */
	@Override
	public StatisticBean getStatistic(int deviceType,List<String> areaIds) {
		StatisticBean sb = null;
		CountValue cv = null;
		String sql = "";
		String allSql = "";
		
		sql += "and deviceType = " + deviceType+" ";
		
		int len = areaIds.size();
		if (len == 1)
			sql += " and areaId in (" + areaIds.get(0) +") "; 
		else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					sql += " and areaId in (" + areaIds.get(0);
				} else if (i == len - 1) {
					sql += " ,"+areaIds.get(i) + ") ";
				} else {
					sql += " ,"+areaIds.get(i);
				}
			}
		}
		
		
		allSql += "select total,onlineNum,offLineNum,alarmNum from " + 
			" (select count(*) as total from smoke where 1 = 1 "+sql+" ) a, " +
			" (select count(*) as onlineNum from smoke where netState = 1 " + sql +" ) b, " +
			" (select count(*) as offlineNum from smoke where netState = 0 " + sql + " ) c, " +
			" (select count(*)  as alarmNum from smoke  where ifAlarm = 0  " + sql +" ) d ";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSql);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			if (rs.next()) {
				cv = new CountValue();
				cv.setMacNum(rs.getInt("total"));  //设备总数
				cv.setNetStaterNum(rs.getInt("onlineNum")); //在线数
				cv.setNoNetStater(rs.getInt("offlineNum")); //离线数
				cv.setIfDealNum(rs.getInt("alarmNum")); //报警次数
			}
			
			sb = new StatisticBean();
			sb.setDeviceId(deviceType);
			DeviceDao dd = new DevicesDaoImpl();
			sb.setDeviceName(dd.getDeviceName(deviceType));
			sb.setDeviceNum(cv.getMacNum());
			sb.setCv(cv);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return sb;
	}
	
	//获取所有设备的在线，离线，报警数统计
	public CountValue getStatistic(List<String> areaIds) {
		CountValue cv = null;
		String sql = "";
		String allSql = "";
		
		int len = areaIds.size();
		if (len == 1)
			sql += " and areaId in (" + areaIds.get(0) +") "; 
		else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					sql += " and areaId in (" + areaIds.get(0);
				} else if (i == len - 1) {
					sql += " ,"+areaIds.get(i) + ") ";
				} else {
					sql += " ,"+areaIds.get(i);
				}
			}
		}
		String addSql = "and deviceType in(select id from devices) ";
		
		allSql += "select total,onlineNum,offLineNum,lowPowerNum from " + 
			" (select count(*) as total from smoke where 1 = 1 "+sql+ addSql + " ) a, " +
			" (select count(*) as onlineNum from smoke where netState = 1 " + sql + addSql + " ) b, " +
			" (select count(*) as offlineNum from smoke where netState = 0 " + sql +addSql+ " ) c, " +
			" (select count(*) as lowPowerNum from (select * from (select smokeMac,alarmType from alarm where smokeMac in (select mac from smoke where ifalarm=0 "+ sql + addSql + " ) and alarmType in (105,107,193) order by id  desc) mid group by smokeMac)temp) d ";// +
//			" (select count(*) as  repeaterNum from (select * from (select repeaterMac,faultType from faultInfo fi,smoke s  where fi.repeaterMac = s.mac  "+sql+addSql+" and dealTime = '' order by faultime desc) mid  group by repeaterMac ) tem where  faultType like '%故障' ) e";
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSql);
		System.out.println("count:" +allSql);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			if (rs.next()) {
				cv = new CountValue();
				cv.setMacNum(rs.getInt("total"));  //设备总数
				cv.setNetStaterNum(rs.getInt("onlineNum")); //在线数
				cv.setNoNetStater(rs.getInt("offlineNum")); //离线数
				cv.setIfDealNum(rs.getInt("lowPowerNum")); //低电量数量
				cv.setOtherNum(0);
//				cv.setOtherNum(rs.getInt("repeaterNum")); //故障数量
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return cv;
	}
	
	//获取指定区域的统计数据
	@Override
	public StatisticBean getStatistic2(String areaId) {
		StatisticBean sb = null;
		StringBuffer strbuf =new StringBuffer();
		String sql = "";
		String allSql = "";
		sql += " and areaid = " + areaId +" ";
		strbuf.append("select total,onlineNum,offLineNum,alarmNum from " );
		strbuf.append("(select count(*) as total from smoke where 1 = 1 ");
		strbuf.append(sql);
		strbuf.append(" ) a,");
		strbuf.append("(select count(*) as onlineNum from smoke where netState = 1 ");
		strbuf.append(sql);
		strbuf.append(" ) b, ");
		strbuf.append("(select count(*) as offlineNum from smoke where netState = 0 ");
		strbuf.append(sql);
		strbuf.append(" ) c,"); 
		strbuf.append(" (select count(*)  as alarmNum from smoke  where ifAlarm = 0  ");
		strbuf.append(sql);
		strbuf.append(" )  d ");
		
//		allSql += "select total,onlineNum,offLineNum,alarmNum from " + 
//			" (select count(*) as total from smoke where 1 = 1 "+sql+" ) a, " +
//			" (select count(*) as onlineNum from smoke where netState = 1 " + sql +" ) b, " +
//			" (select count(*) as offlineNum from smoke where netState = 0 " + sql + " ) c, " +
//			" (select count(*)  as alarmNum from smoke  where ifAlarm = 0  " + sql +" )  d ";
		allSql = new String(strbuf);
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, allSql);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			CountValue cv = null;
			if (rs.next()) {
				cv = new CountValue();
				cv.setMacNum(rs.getInt("total"));  //设备总数
				cv.setNetStaterNum(rs.getInt("onlineNum")); //在线数
				cv.setNoNetStater(rs.getInt("offlineNum")); //离线数
				cv.setIfDealNum(rs.getInt("alarmNum")); //报警次数
			}
			AreaDao ad = new AreaDaoImpl();
			sb = new StatisticBean();
			sb.setAreaId(areaId);
			sb.setAreaName(ad.getAreaNameById(areaId));
			sb.setCv(cv);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return sb;
	}

	//获取今天、昨天的电气设备的报警数
	@Override
	public List<Integer> getStatistic3(Calendar c,int flag,List<String> areaIds) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd ");
		String format = sdf.format(c.getTime());
		String begin = format+"00";
		String end ="";
		if (flag == 0) {
			end = format +"24";
		} else {
			end = format+(c.get(Calendar.HOUR_OF_DAY)+1);
		}
		StatisticDaoImpl sd = new StatisticDaoImpl();
		List<Integer> lstELec = sd.getElecStatistic(begin,end,areaIds);
		return  lstELec;

	}
	
	//获取指定时间段电气设备的报警次数
	public int getAlarmCount(String start,String stop,List<String> areaIds) {
		int alarmNum = 0;
		String sql = "";
		
		sql += "select count(*) as alarmNum from alarm ,smoke  where smokeMac = mac and deviceType = 5 ";
		sql += " and alarmTime > '" + start + "' and alarmTime < '" + stop + "' ";
		int len = areaIds.size();
		if (len == 1)
			sql += " and areaId in (" + areaIds.get(0) +") "; 
		else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					sql += " and areaId in (" + areaIds.get(0);
				} else if (i == len - 1) {
					sql += " ,"+areaIds.get(i) + ") ";
				} else {
					sql += " ,"+areaIds.get(i);
				}
			}
		}
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			if (rs.next()) {
				alarmNum = rs.getInt("alarmNum");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return alarmNum;
	}
	
	
	//获取一天内指定时间段按小时统计的电气设备的报警数
	public List<Integer> getElecStatistic(String begin,String end,List<String> areaIds) {
		List<Integer> lstElec =  new ArrayList<Integer>();
		String sql = "select alarmTime2,count(*) from smoke,(select smokeMac,SUBSTR(alarmTime,1,13)as alarmTime2 from alarm) alarm2  " +
				"where alarmTime2 >= '" +begin+"' and alarmTime2 < '" + end + "' and smokeMac = mac and deviceType = 5 ";
		
		int len = areaIds.size();
		if (len == 1)
			sql += " and areaId in (" + areaIds.get(0) +") "; 
		else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					sql += " and areaId in (" + areaIds.get(0);
				} else if (i == len - 1) {
					sql += " ,"+areaIds.get(i) + ") ";
				} else {
					sql += " ,"+areaIds.get(i);
				}
			}
		}
		
		sql += "group by alarmTime2 order by alarmTime2" ; 
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		try {
			rs = ppst.executeQuery();
			int i = 0;
			while (rs.next()) {
				String time = rs.getString("alarmTime2");
				int num = Integer.parseInt(time.substring(11));
				if(num > i) {
					while (num > i) {
						lstElec.add(0);
						i++;
					}
				} 
				lstElec.add(rs.getInt("count(*)"));
				i++;
			}
			
			for(int k=lstElec.size();k<Integer.parseInt(end.substring(11, 13));k++){
				lstElec.add(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		return lstElec;
	}
	
//	public static void main(String[] args) {
////		AreaDao areaDao = new AreaDaoImpl();
////		List<String> areaIds = areaDao.getAreaStr("18011715889", "4");  //获取用户所管理的区域id
////		StatisticDaoImpl sd = new StatisticDaoImpl();
////		List<FireBean> lstFB = sd.getAlarmCountByDeviceType(areaIds);
////		System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
//		StatisticDaoImpl sdi = new StatisticDaoImpl();
//		Connection conn = DBConnectionManager.getConnection();
//		 String sql = "{call test1(?)}";
////		String sql = "{call sta_area()}";
//		 CallableStatement statement = null;
//		try {
//			statement = conn.prepareCall(sql);
//			statement.setInt(1, 1);
//			statement.execute();
//			ResultSet rs =statement.getResultSet();
//			if(rs !=null && rs.next()) {
//				int total = rs.getInt(1);
//				int online = rs.getInt(2);
//				System.out.println(total+"-"+online);
//			}
////			boolean hadResults = statement.execute();  
////		      int i=0;  
////		      while (hadResults) {  
////		          System.out.println("result No:----"+(++i));  
////		          ResultSet rs = statement.getResultSet();  
////		          while (rs != null && rs.next()) {  
////		             int id1 = rs.getInt(1);  
////		             int count = rs.getInt(2);
//////		             String name1 = rs.getString(2);  
////		             System.out.println(id1 + ":" + count);  
////		          }  
////		          hadResults = statement.getMoreResults(); //检查是否存在更多结果集  
////		      }  
////			statement.registerOutParameter(1, Types.INTEGER);
////			statement.execute();
////			int msg=statement.getInt(1); 
////			System.out.println(msg); 
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				statement.close();
//				conn.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
//		 
//	}
	
	public  List<FireBean> getAlarmCountByDeviceType(List<String> areaIds){
//		Date d0 =new Date();
		List<FireBean> lstFB = new ArrayList<FireBean>();
//		String sql = "select count(*) as alarmCount,alarmName from alarm a,alarmType al,smoke s " +
//				"where s.mac = a.smokemac and a.alarmType = al.alarmId and s.deviceType= " +deviceType ;
		
		String sql = "select count(*) as alarmCount,alarmName,deviceType from alarm a,alarmType al,smoke s " +
		"where s.mac = a.smokemac and a.alarmType = al.alarmId " +
		"and s.deviceType not in (5,10,15,18,19) " ;   //非电气和水系统设备
		int len = areaIds.size();
		if (len == 1)
			sql += " and areaId in (" + areaIds.get(0) +") "; 
		else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					sql += " and areaId in (" + areaIds.get(0);
				} else if (i == len - 1) {
					sql += " ,"+areaIds.get(i) + ") ";
				} else {
					sql += " ,"+areaIds.get(i);
				}
			}
		}
		
		/*统计最近一个月的报警数*/
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MONTH, -3);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(cal.getTime());
		sql += " and alarmTime > '"+time+"' ";
		
		sql += " group by deviceType,alarmName order by deviceType ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		
		try {
			rs=ppst.executeQuery();
			FireBean fireBean = null;
			int d = 0;  //用来判断是否是同一种设备
			while(rs.next()) {
				if (fireBean == null || d != rs.getInt("deviceType") ) {
					if (fireBean != null) {
						lstFB.add(fireBean);
					}
					fireBean = new FireBean();
					Map<String,Integer> alarmMap = new HashMap<String,Integer>();
					fireBean.setAlarmMap(alarmMap);
				}
				d = rs.getInt("deviceType"); 
				DeviceDao dd = new DevicesDaoImpl();
				fireBean.setDeviceName(dd.getDeviceName(d));
				fireBean.setDeviceType(d);
				String alarmName = rs.getString("alarmName");
				int alarmCount = rs.getInt("alarmCount");
				fireBean.getAlarmMap().put(alarmName, alarmCount);
			}
			lstFB.add(fireBean);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
//		Date d3 = new Date();
//		System.out.println("FirecostTime1:"+(d3.getTime()-d0.getTime()));
		return lstFB;
	}

	@Override
	public List<StatisticBean> getStatistic2(List<String> areaIds) {
		List<StatisticBean> lstSb = new ArrayList<StatisticBean>();
		if (areaIds == null) {
			return null;
		}
		
		Connection conn = DBConnectionManager.getConnection();
		CallableStatement statement = null;
		 String sql = "{call test1(?)}";
		CountValue cv =null;
		StatisticBean sb =null;
		try {
			statement = conn.prepareCall(sql);
			for (String areaId : areaIds) {
					statement.setInt(1, Integer.parseInt(areaId));
					Date d1 = new Date();
					statement.execute();
					Date d2 = new Date();
					System.out.println("costTime:"+(d2.getTime()-d1.getTime()));
					ResultSet rs =statement.getResultSet();
					if(rs !=null && rs.next()) {
						cv = new CountValue();
						cv.setMacNum(rs.getInt(1));  //设备总数
						cv.setNetStaterNum(rs.getInt(2)); //在线数
						cv.setNoNetStater(rs.getInt(1)-rs.getInt(2)); //离线数
	//					cv.setIfDealNum(rs.getInt("alarmNum")); //报警次数
					}
					AreaDao ad = new AreaDaoImpl();
					sb = new StatisticBean();
					sb.setAreaId(areaId);
					sb.setAreaName(ad.getAreaNameById(areaId));
					sb.setCv(cv);
					lstSb.add(sb);
	//					int total = rs.getInt(1);
	//					int online = rs.getInt(2);
	//					System.out.println(total+"-"+online);
					
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 finally {
			try {
				statement.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		return lstSb;
	}

	@Override
	public  List<StatisticAnalysisEntity> getStatistNum(List<String> areaIds,String parentId) {
		StringBuilder sql = new StringBuilder();
		List<StatisticAnalysisEntity> analyList = new ArrayList<StatisticAnalysisEntity>();
		sql.append("SELECT * from (");
		sql.append(" SELECT s.areaid,a.area,a.parentId,s.number,s.netState from (");
		sql.append(" SELECT areaid,area,parentId from areaidarea) a");
		sql.append(" LEFT JOIN (");
		sql.append(" SELECT areaid,netState,count(mac) as number from smoke GROUP BY areaid,netState");
		sql.append(" ) s on s.areaId = a.areaid");
		if(StringUtils.isNotBlank(parentId)){
			sql.append(" and a.parentid = "+parentId);
		}
		if(areaIds!=null){
			sql.append(" and a.areaid in(0");
			for (int i = 0; i < areaIds.size(); i++) {
				sql.append(","+areaIds.get(i));
			}
			sql.append(")");
		}
		
		sql.append(" ORDER BY a.areaid");
		sql.append(" ) as a where a.areaid is not null");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		
		try {
			rs = ps.executeQuery();
			int areaid = 0;
			int onNetstate = 0;
			int lossNetstate = 0;
			int onNetstates = 0;
			int lossNetstates = 0;
			StatisticAnalysisEntity sae = null;
			while(rs.next()){
				if(areaid != (rs.getInt("areaId"))){
					if(sae!=null){
						analyList.add(sae);
					}
					sae = new StatisticAnalysisEntity();
					onNetstate = 0;
					lossNetstate = 0;
				}
				areaid = rs.getInt("areaId");
				if(rs.getInt("netState")==1){
					onNetstate = rs.getInt("number");
					onNetstates = onNetstates + onNetstate;
				}else {
					lossNetstate = rs.getInt("number");
					lossNetstates = lossNetstates + lossNetstate;
				}
				sae.setParentId(rs.getInt("parentId"));
				sae.setAreaid(rs.getInt("areaid"));
				sae.setAreaName(rs.getString("area"));
				sae.setOnNetState(onNetstate);
				sae.setLossNetState(lossNetstate);
				if(rs.isLast()){
					analyList.add(sae);
					sae = new StatisticAnalysisEntity();
					sae.setParentId(99999);
					sae.setAreaid(99999);
					sae.setAreaName("总数");
					sae.setOnNetState(onNetstates);
					sae.setLossNetState(lossNetstates);
					sae.setAreaSum(onNetstates+lossNetstates);
					analyList.add(sae);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return analyList;
	}
	
	/*
	 * 使用须知：areaIds != null
	 */
	@Override
	public  List<StatisticAnalysisEntity> getStatistNum2(List<String> areaIds) {
		StringBuilder sql = new StringBuilder();
		List<StatisticAnalysisEntity> analyList = new ArrayList<StatisticAnalysisEntity>();
		sql.append("SELECT a.areaid,a.area,a.parentId,s.number,s.netState from ");
		sql.append("(SELECT areaid,area,parentId from areaidarea where areaid in ");
		sql.append(MyUtils.getStringByList(areaIds));
		sql.append(") as a LEFT JOIN ");
		sql.append("(SELECT areaid,netState,count(mac) as number from smoke where areaid in ");
		sql.append(MyUtils.getStringByList(areaIds));
		sql.append(" GROUP BY areaid,netState ) as s ");
		sql.append(" On s.areaId = a.areaid");
		sql.append(" ORDER BY a.areaid");
		
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql.toString());
		ResultSet rs = null;
		
		try {
			rs = ps.executeQuery();
			int areaid = 0;
			int onNetstate = 0;
			int lossNetstate = 0;
			int onNetstates = 0;
			int lossNetstates = 0;
			StatisticAnalysisEntity sae = null;
			while(rs.next()){
				if(areaid != (rs.getInt("areaId"))){
					if(sae!=null){
						analyList.add(sae);
					}
					sae = new StatisticAnalysisEntity();
					onNetstate = 0;
					lossNetstate = 0;
				}
				areaid = rs.getInt("areaId");
				if(rs.getInt("netState")==1){
					onNetstate = rs.getInt("number");
					onNetstates = onNetstates + onNetstate;
				}else {
					lossNetstate = rs.getInt("number");
					lossNetstates = lossNetstates + lossNetstate;
				}
				sae.setParentId(rs.getInt("parentId"));
				sae.setAreaid(rs.getInt("areaid"));
				sae.setAreaName(rs.getString("area"));
				sae.setOnNetState(onNetstate);
				sae.setLossNetState(lossNetstate);
				if(rs.isLast()){
					analyList.add(sae);
					sae = new StatisticAnalysisEntity();
					sae.setParentId(99999);
					sae.setAreaid(99999);
					sae.setAreaName("总数");
					sae.setOnNetState(onNetstates);
					sae.setLossNetState(lossNetstates);
					sae.setAreaSum(onNetstates+lossNetstates);
					analyList.add(sae);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return analyList;
	}


}
