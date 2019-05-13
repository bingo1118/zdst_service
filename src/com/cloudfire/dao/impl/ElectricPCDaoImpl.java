package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.dao.ElectricPCDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.BQEletricAVGData;
import com.cloudfire.entity.ElectricPCBean;
import com.cloudfire.entity.MyElectricInfo;
import com.cloudfire.entity.SearchDto;

public class ElectricPCDaoImpl implements ElectricPCDao {

	public List<ElectricPCBean> getAllValue(String type) {
		String sql = "select electricValue1,electricValue2,electricValue3,electricValue4,electricTime,electricType "
				+ "from electricinfo where electricTime BETWEEN '2017-01-23' and '2017-02-10' and electricType = 6 order by electricTime DESC";
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager
				.prepare(connection, sql);
		ResultSet rs = null;
		List<ElectricPCBean> list = new ArrayList<ElectricPCBean>();
		try {
			rs = prepare.executeQuery();
			while (rs.next()) {
				ElectricPCBean electricPCBean = new ElectricPCBean();
				electricPCBean.setElectricValue1(rs.getString(1));
				electricPCBean.setElectricValue2(rs.getString(2));
				electricPCBean.setElectricValue3(rs.getString(3));
				electricPCBean.setElectricValue4(rs.getString(4));
				electricPCBean.setElectricTime(rs.getString(5));
				list.add(electricPCBean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(connection);
		}
		return list;
	}

	public List<ElectricPCBean> getAllValue(String type, String mac) {
		String mysql = "SELECT electricValue1,electricValue2,electricValue3,electricValue4,electricTime,electricType FROM electricinfo WHERE electricType = ? and electricinfo.smokeMac=? ORDER BY electricTime desc LIMIT 10";

		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(connection,
				mysql);
		ResultSet rs = null;
		List<ElectricPCBean> list = new ArrayList<ElectricPCBean>();
		try {
			prepare.setInt(1, Integer.parseInt(type));
			prepare.setString(2, mac);
			rs = prepare.executeQuery();
			while (rs.next()) {
				ElectricPCBean electricPCBean = new ElectricPCBean();
				electricPCBean.setElectricValue1(rs.getString(1));
				electricPCBean.setElectricValue2(rs.getString(2));
				electricPCBean.setElectricValue3(rs.getString(3));
				electricPCBean.setElectricValue4(rs.getString(4));
				electricPCBean.setElectricTime(rs.getString(5));
				list.add(electricPCBean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(connection);
		}
		return list;
	}

	// 2017.3.11 14:08
	/*
	 * public List<MyElectricInfo> getAllValue1(String type,String mac) { String
	 * mysql =
	 * "SELECT electricValue1,electricValue2,electricValue3,electricValue4,electricTime,electricType FROM electricinfo WHERE electricType = ? and electricinfo.smokeMac=? ORDER BY electricTime asc LIMIT 10"
	 * ;
	 * 
	 * Connection connection = DBConnectionManager.getConnection();
	 * PreparedStatement prepare = DBConnectionManager.prepare(connection,
	 * mysql); ResultSet rs = null; List<MyElectricInfo> list = new
	 * ArrayList<MyElectricInfo>(); try { prepare.setInt(1,
	 * Integer.parseInt(type)); prepare.setString(2, mac); rs =
	 * prepare.executeQuery(); String[] array1=new String[8]; while (rs.next())
	 * { MyElectricInfo myElectricInfo = new MyElectricInfo(); List<String>
	 * valueList = new ArrayList<>(); for(int i=1;i<=3;i++){ if (i==1) {
	 * myElectricInfo.setName("电压1");
	 * valueList.add(rs.getString("electricValue1"));
	 * //valueList.add(rs.getString("electricValue1")); }if (i==2) {
	 * myElectricInfo.setName("电压2");
	 * valueList.add(rs.getString("electricValue2")); }if (i==3) {
	 * myElectricInfo.setName("电压3");
	 * valueList.add(rs.getString("electricValue3")); } }
	 * 
	 * list.add(myElectricInfo); } } catch (SQLException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } return list; }
	 */

	// 2017.3.13 8:56
	public List<String> getAllValue2(String type, String mac) {
		String mysql = "SELECT electricValue1,electricValue2,electricValue3,electricValue4,electricTime,electricType FROM electricinfo WHERE electricType = ? and electricinfo.smokeMac=? ORDER BY electricTime asc LIMIT 10";

		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(connection,
				mysql);
		ResultSet rs = null;
		List<String> list = new ArrayList<>();
		try {
			prepare.setInt(1, Integer.parseInt(type));
			prepare.setString(2, mac);
			rs = prepare.executeQuery();

			while (rs.next()) {
				list.add(rs.getString("electricValue1"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(connection);
		}
		return list;

	}

	// 2017.3.13 13:14
	public List<MyElectricInfo> getAllValue4(String type, String mac) {
		String mysql = "SELECT electricValue1,electricValue2,electricValue3,electricValue4,electricTime,electricType FROM electricinfo WHERE  electricinfo.smokeMac=? ";
		switch(type){
		case "6":
			mysql += " and electricType in (6,61,62,63) ";
			break;
		case "7":
			mysql += " and electricType in (7,71,72,73) ";
			break;
		case "8":
			mysql += " and electricType in (8,81,82,83) ";
			break;
		case "9":
			mysql += " and electricType in (9,91,92,93) ";
			break;
		}
		
		mysql +=	"ORDER BY electricTime desc";

		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(connection,
				mysql);
		ResultSet rs = null;
		List<MyElectricInfo> list = new ArrayList<>();
		try {
//			prepare.setInt(1, Integer.parseInt(type));
			prepare.setString(1, mac);
			rs = prepare.executeQuery();
			MyElectricInfo info = null;

			while (rs.next()) {
				info = new MyElectricInfo();
				List<String> strings1 = new ArrayList<>();
				strings1.add((rs.getString("electricValue1") == null || rs
						.getString("electricValue1") == "") ? "0.00" : rs
						.getString("electricValue1"));
				strings1.add((rs.getString("electricValue2") == null || rs
						.getString("electricValue2") == "") ? "0.00" : rs
						.getString("electricValue2"));
				strings1.add((rs.getString("electricValue3") == null || rs
						.getString("electricValue3") == "") ? "0.00" : rs
						.getString("electricValue3"));
				strings1.add((rs.getString("electricValue4") == null || rs
						.getString("electricValue4") == "") ? "0.00" : rs
						.getString("electricValue4"));
				strings1.add((rs.getString("electricTime") == null || rs
						.getString("electricTime") == "") ? "0.00" : rs
						.getString("electricTime"));
				info.setList1(strings1);
				list.add(info);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(connection);
		}
		return list;

	}

	@Override
	public int getAllValue4Count(String type, String mac) {
		String mysql = "SELECT count(id) FROM electricinfo WHERE  electricinfo.smokeMac=? ";
		switch(type){
		case "6":
			mysql += " and electricType in (6,61,62,63) ";
			break;
		case "7":
			mysql += " and electricType in (7,71,72,73) ";
			break;
		case "8":
			mysql += " and electricType in (8,81,82,83) ";
			break;
		case "9":
			mysql += " and electricType in (9,91,92,93) ";
			break;
		}

		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(connection,
				mysql);
		ResultSet rs = null;
		int totalCount =0;
		try {
//			prepare.setInt(1, Integer.parseInt(type));
			prepare.setString(1, mac);
			rs = prepare.executeQuery();

			while (rs.next()) {
				totalCount = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(connection);
		}
		return  totalCount;

	}
	
	@Override
	public List<MyElectricInfo> getAllValue4(String type, String mac,Integer pageNo,Integer pageSize) {
		String mysql = "SELECT electricValue1,electricValue2,electricValue3,electricValue4,electricTime,electricType FROM electricinfo WHERE  electricinfo.smokeMac=? ";
		switch(type){
		case "6":
			mysql += " and electricType in (6,61,62,63) ";
			break;
		case "7":
			mysql += " and electricType in (7,71,72,73) ";
			break;
		case "8":
			mysql += " and electricType in (8,81,82,83) ";
			break;
		case "9":
			mysql += " and electricType in (9,91,92,93) ";
			break;
		}
		
		mysql +=	"ORDER BY id desc ";
		mysql += " limit "+ (pageNo-1)*pageSize+","+pageSize;

		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(connection,
				mysql);
		ResultSet rs = null;
		List<MyElectricInfo> list = new ArrayList<>();
		try {
//			prepare.setInt(1, Integer.parseInt(type));
			prepare.setString(1, mac);
			rs = prepare.executeQuery();
			MyElectricInfo info = null;

			while (rs.next()) {
				info = new MyElectricInfo();
				List<String> strings1 = new ArrayList<>();
				strings1.add((rs.getString("electricValue1") == null || rs
						.getString("electricValue1") == "") ? "0.00" : rs
						.getString("electricValue1"));
				strings1.add((rs.getString("electricValue2") == null || rs
						.getString("electricValue2") == "") ? "0.00" : rs
						.getString("electricValue2"));
				strings1.add((rs.getString("electricValue3") == null || rs
						.getString("electricValue3") == "") ? "0.00" : rs
						.getString("electricValue3"));
				strings1.add((rs.getString("electricValue4") == null || rs
						.getString("electricValue4") == "") ? "0.00" : rs
						.getString("electricValue4"));
				strings1.add((rs.getString("electricTime") == null || rs
						.getString("electricTime") == "") ? "0.00" : rs
						.getString("electricTime"));
				info.setList1(strings1);
				list.add(info);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(connection);
		}
		return list;

	}
	
	@Override
	public List<BQEletricAVGData> getBqMonthAVGDatas(List<String> list,
			SearchDto dto) {
		String ids = new String();
		if (list.size() < 0) {
			ids = "a.areaId";
		} else {
			for (int i = 0; i < list.size(); i++) {
				if (i == list.size() - 1) {
					ids += list.get(i);
					break;
				} else {
					ids += list.get(i);
					ids += ",";
				}
			}
		}
		String mysql = "";
		if (dto != null) {
			String sql = "";
			if (!StringUtils.isBlank(dto.getFire1())) {
				sql += " and e.electricTime>='" + dto.getFire1()+"'";
			}
			if (!StringUtils.isBlank(dto.getFire2())) {
				sql += " and e.electricTime<='" + dto.getFire2()+"'";
			}
			mysql = "select DATE_FORMAT(e.electricTime,'%Y-%m-%d') createTime,"
					+ "case when et.electricType=43 then '电压' when et.electricType=45 then '电流' "
					+ "when et.electricType=44 then '欠压' when et.electricType=46 then '漏电压'  else '温度' end as Type ,"
					+ "ROUND(AVG(e.electricValue1),1) AVG1,ROUND(AVG(e.electricValue2),1) AVG2, ROUND(AVG(e.electricValue3),1) AVG3, ROUND(AVG(e.electricValue4),1) AVG4"
					+ " from electricinfo e,smoke s,areaidarea a ,electrictypeval et "
					+ "where e.smokeMac=s.mac and s.areaId=a.areaId and e.electricType=et.electricAttr "
					+ sql + " and a.areaId in(" + ids
					+ ") GROUP BY createTime ,e.electricType";
		} else {
			mysql = "select DATE_FORMAT(e.electricTime,'%m') Month,"
					+ "case when et.electricType=43 then '电压' when et.electricType=45 then '电流' "
					+ "when et.electricType=44 then '欠压' when et.electricType=46 then '漏电压'  else '温度' end as Type ,"
					+ "ROUND(AVG(e.electricValue1),1) AVG1,ROUND(AVG(e.electricValue2),1) AVG2, ROUND(AVG(e.electricValue3),1) AVG3, ROUND(AVG(e.electricValue4),1) AVG4"
					+ " from electricinfo e,smoke s,areaidarea a ,electrictypeval et "
					+ "where e.smokeMac=s.mac and s.areaId=a.areaId and e.electricType=et.electricAttr  and a.areaId in("
					+ ids + ") GROUP BY Month ,e.electricType";
		}
		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(connection,
				mysql);
		ResultSet rs = null;
		List<BQEletricAVGData> datas = new ArrayList<BQEletricAVGData>();
		try {
			rs = prepare.executeQuery();
			while (rs.next()) {
				BQEletricAVGData data = new BQEletricAVGData();
				data.setTime(rs.getString(1));
				data.setType(rs.getString("Type"));
				data.setAvg1(rs.getString("AVG1"));
				data.setAvg1(rs.getString("AVG2"));
				data.setAvg1(rs.getString("AVG3"));
				data.setAvg1(rs.getString("AVG4"));
				datas.add(data);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(connection);
		}
		return datas;
	}

	@Override
	public List<MyElectricInfo> getAllThreeValue4(int type, String mac) {
		String sql = "SELECT smokeImei,smokeImsi,voltageA,voltageB,voltageC,electricityA,electricityB,electricityC,surplusElectri,currentMaximum,runAlarmState,runGateState,runCauseState,heartime from threeelectricinfo where smokeImei = ? ORDER BY  heartime desc";

		Connection connection = DBConnectionManager.getConnection();
		PreparedStatement prepare = DBConnectionManager.prepare(connection,sql);
		ResultSet rs = null;
		List<MyElectricInfo> list = new ArrayList<>();
		try {
			prepare.setString(1, mac);
			rs = prepare.executeQuery();
			MyElectricInfo info = null;

			while (rs.next()) {
				info = new MyElectricInfo();
				List<String> strings1 = new ArrayList<>();
				switch(type){
				case 96:
					strings1.add(StringUtils.isBlank(rs.getString("voltageA")) ? "0.00" : rs.getString("voltageA"));
					strings1.add(StringUtils.isBlank(rs.getString("voltageB")) ? "0.00" : rs.getString("voltageB"));
					strings1.add(StringUtils.isBlank(rs.getString("voltageC")) ? "0.00" : rs.getString("voltageC"));
					break;
				case 97:
					strings1.add(StringUtils.isBlank(rs.getString("electricityA")) ? "0.00" : rs.getString("electricityA"));
					strings1.add(StringUtils.isBlank(rs.getString("electricityB")) ? "0.00" : rs.getString("electricityB"));
					strings1.add(StringUtils.isBlank(rs.getString("electricityC")) ? "0.00" : rs.getString("electricityC"));
					break;
				case 98:
					strings1.add(StringUtils.isBlank(rs.getString("surplusElectri")) ? "0.00" : rs.getString("surplusElectri"));
					break;
				}
				strings1.add(rs.getString("heartime"));
				info.setList1(strings1);
				list.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(prepare);
			DBConnectionManager.close(connection);
		}
		return list;
	}
}
