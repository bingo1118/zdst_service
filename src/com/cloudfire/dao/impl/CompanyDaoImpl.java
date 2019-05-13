package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cloudfire.dao.CompanyDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.CompanyEntity;
import com.cloudfire.until.Utils;

public class CompanyDaoImpl implements CompanyDao {

	@Override
	public List<CompanyEntity> getAllCompanyBy(String phone) {
		String sqlFire = "select id,comanyName,comanyNature,person,registration,workers,phone,policeStation,email,floorArea,buildingArea," +
				"storageArea,foundTime,adress,longitude,latitude,involved,areaId,marks,telephone,characterId," +
				"dangerous,maxdanger,fighting,firelane,safetyexit,extincteur,elevator,staircase,frontimage1,frontimage2," +
				"frontimage3,frontimage4,positions,cardid from company " +"where phone = ? ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,sqlFire);
		ResultSet rs = null;
		List<CompanyEntity> lists=null;
		try {
			ps.setString(1, phone);
			rs = ps.executeQuery();
			while(rs.next()){
				if(lists==null){
					lists=new ArrayList<CompanyEntity>();
				}
				CompanyEntity company = new CompanyEntity();
				company.setId(Integer.parseInt(rs.getString("id")));
				company.setComanyName(rs.getString("comanyName"));
				company.setComanyNature(rs.getString("comanyNature"));
				company.setPerson(rs.getString("person"));
				company.setRegistration(rs.getString("registration"));
				company.setWorkers(Integer.parseInt(rs.getString("workers")));
				company.setPhone(rs.getString("phone"));
				company.setPoliceStation(rs.getString("policeStation"));
				company.setEmail(rs.getString("email"));
				company.setFloorArea(rs.getString("floorArea"));
				company.setBuildingArea(rs.getString("buildingArea"));
				company.setStorageArea(rs.getString("storageArea"));
				company.setFoundTime(rs.getString("foundTime"));
				company.setAdress(rs.getString("adress"));
				company.setLongitude(rs.getString("longitude"));
				company.setLatitude(rs.getString("latitude"));
				company.setInvolved(rs.getString("involved"));
				company.setAreaId(Integer.parseInt(rs.getString("areaId"))); //ÇøÓò
				company.setMarks(rs.getString("marks"));
				company.setTelephone(rs.getString("telephone"));
				company.setCharacterId(Integer.parseInt(rs.getString("characterId")));//Íâ¼ü
				company.setDangerous(rs.getString("dangerous"));
				company.setMaxdanger(rs.getString("maxdanger"));
				company.setFighting(rs.getString("fighting"));
				company.setFirelane(Integer.parseInt(rs.getString("firelane")));
				company.setSafetyexit(Integer.parseInt(rs.getString("safetyexit")));
				company.setExtincteur(Integer.parseInt(rs.getString("extincteur")));
				company.setElevator(Integer.parseInt(rs.getString("elevator")));
				company.setStaircase(Integer.parseInt(rs.getString("staircase")));
				company.setFrontimage1(rs.getString("frontimage1"));
				company.setFrontimage2(rs.getString("frontimage2"));
				company.setFrontimage3(rs.getString("frontimage3"));
				company.setFrontimage4(rs.getString("frontimage4"));
				company.setPositions(rs.getString("positions"));
				company.setCardid(rs.getString("cardid"));
				lists.add(company);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lists;
	}

	@Override
	public boolean removed(int id) {
		boolean bl = false ;
		String hql = "DELETE FROM company WHERE id = ?"; 
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn,hql);
		try {
			ps.setInt(1, id);			
			int i = ps.executeUpdate();
			if(i >0	){
				bl = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally{
			DBConnectionManager.close(ps);
			//DBConnectionManager.close(rs);
			DBConnectionManager.close(conn);
		}		
		return bl;
	}

	@Override
	public boolean companyInsert(CompanyEntity ce) {
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO company (comanyName,comanyNature,person,registration,workers,phone,policeStation,email,floorArea,buildingArea," +
				"storageArea,foundTime,adress,longitude,latitude,involved,areaId,image,marks,telephone,characterId," +
				"dangerous,maxdanger,fighting,firelane,safetyexit,extincteur,elevator,staircase,frontimage1,frontimage2," +
				"frontimage3,frontimage4,positions,cardid)");
		sb.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		int rs = -1;
		boolean result = false;
		String sqlstr = sb.toString();
		if(ce !=null){
			Connection conn = DBConnectionManager.getConnection();
			PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
			try {
				ps.setString(1, ce.getComanyName());
				ps.setString(2, ce.getComanyNature());
				ps.setString(3, ce.getPerson());
				ps.setString(4, ce.getRegistration());
				ps.setInt(5, ce.getWorkers());
				ps.setString(6, ce.getPhone());
				ps.setString(7, ce.getPoliceStation());
				ps.setString(8, ce.getEmail());
				ps.setString(9, ce.getFloorArea());
				ps.setString(10, ce.getBuildingArea());
				ps.setString(11, ce.getStorageArea());
				ps.setString(12, ce.getFoundTime());
				ps.setString(13, ce.getAdress());
				ps.setString(14, ce.getLongitude());
				ps.setString(15, ce.getLatitude());
				ps.setString(16, ce.getInvolved());
				ps.setInt(17, ce.getAreaId());
				ps.setString(18, ce.getImage());
				ps.setString(19, ce.getMarks());
				ps.setString(20, ce.getTelephone());
				ps.setInt(21, ce.getCharacterId());
				ps.setString(22, ce.getDangerous());
				ps.setString(23, ce.getMaxdanger());
				ps.setString(24, ce.getFighting());
				ps.setInt(25, ce.getFirelane());
				ps.setInt(26, ce.getSafetyexit());
				ps.setInt(27, ce.getExtincteur());
				ps.setInt(28, ce.getElevator());
				ps.setInt(29, ce.getStaircase());
				ps.setString(30, ce.getFrontimage1());
				ps.setString(31, ce.getFrontimage2());
				ps.setString(32, ce.getFrontimage3());
				ps.setString(33, ce.getFrontimage4());
				ps.setString(34, ce.getPositions());
				ps.setString(35, ce.getCardid());
				rs = ps.executeUpdate();
				if(rs >0){
					result =  true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DBConnectionManager.close(ps);
				DBConnectionManager.close(conn);
			}
		}
		
		return result;
	}

	@Override
	public boolean ifExitCompany(String phone) {
		String strsql = "select phone from company where phone = ?";
		boolean result = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, strsql);
		ResultSet rs = null;
		try {
			ps.setString(1, phone);
			rs = ps.executeQuery();
			while(rs.next()){
				result = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}
	
	@Override
	public boolean ifExitCompanyBycardId(String cardId) {
		String strsql = "select cardid from company where phone = ?";
		boolean result = false;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, strsql);
		ResultSet rs = null;
		try {
			ps.setString(1, cardId);
			rs = ps.executeQuery();
			while(rs.next()){
				result = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}

	@Override
	public boolean modifyCompanyByC(CompanyEntity ce) {
		String sqlstr = "UPDATE company SET ";
		StringBuffer sb = new StringBuffer();
		sb.append(sqlstr);
		boolean bool = false;
		if(Utils.isNullStr(ce.getComanyName())) sb.append(" comanyName = ? ,");
		if(Utils.isNullStr(ce.getPerson())) sb.append(" person = ? ,");
		if(Utils.isNullStr(ce.getRegistration())) sb.append(" registration = ? ,");
		if(ce.getWorkers()!=0) sb.append(" workers = ? ,");
		if(Utils.isNullStr(ce.getEmail())) sb.append(" email = ? ,");
		if(Utils.isNullStr(ce.getFloorArea())) sb.append(" floorArea = ? ,");
		if(Utils.isNullStr(ce.getBuildingArea())) sb.append(" buildingArea = ? ,");
		if(Utils.isNullStr(ce.getStorageArea())) sb.append(" storageArea = ? ,");
		if(Utils.isNullStr(ce.getFoundTime())) sb.append(" foundTime = ? ,");
		if(Utils.isNullStr(ce.getAdress())) sb.append(" adress = ? ,");
		if(Utils.isNullStr(ce.getLongitude())) sb.append(" longitude = ? ,");
		if(Utils.isNullStr(ce.getLatitude())) sb.append(" latitude = ? ,");
		if(Utils.isNullStr(ce.getInvolved())) sb.append(" involved = ? ,");
		if(ce.getAreaId()!=0) sb.append(" areaId = ? ,");
		if(Utils.isNullStr(ce.getMarks())) sb.append(" marks = ? ,");
		if(Utils.isNullStr(ce.getTelephone())) sb.append(" telephone = ? ,");
		if(ce.getCharacterId()!=0) sb.append(" characterId = ? ,");
		if(Utils.isNullStr(ce.getDangerous())) sb.append(" dangerous = ? ,");
		if(Utils.isNullStr(ce.getMaxdanger())) sb.append(" maxdanger = ? ,");
		if(Utils.isNullStr(ce.getFighting())) sb.append(" fighting = ? ,");
		if(ce.getFirelane()!=0) sb.append(" firelane = ? ,");
		if(ce.getSafetyexit()!=0) sb.append(" safetyexit = ? ,");
		if(ce.getExtincteur()!=0) sb.append(" extincteur = ? ,");
		if(ce.getElevator()!=0) sb.append(" elevator = ? ,");
		if(ce.getStaircase()!=0) sb.append(" staircase = ? ,");
		if(Utils.isNullStr(ce.getPositions())) sb.append(" positions = ? ,");
		if(Utils.isNullStr(ce.getCardid())) sb.append(" cardid = ?  ");
		if(Utils.isNullStr(ce.getFrontimage1())) sb.append(" , frontimage1 = ? ");
		if(Utils.isNullStr(ce.getFrontimage2())) sb.append(" , frontimage2 = ? ");
		if(Utils.isNullStr(ce.getFrontimage3())) sb.append(" , frontimage3 = ? ");
		if(Utils.isNullStr(ce.getFrontimage4())) sb.append(" , frontimage4 = ? ");
		
		sqlstr = sb.toString();
//		if(sqlstr.substring(sqlstr.length()-1, 1) == "," ||",".equals(sqlstr.substring(sqlstr.length()-1, 1))) sqlstr = sqlstr.substring(1,sqlstr.length()-1);
		sqlstr = sqlstr+" WHERE phone = ?";
		int result = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sqlstr);
		try {
			ps.setString(1, ce.getComanyName());
			ps.setString(2, ce.getPerson());
			ps.setString(3, ce.getRegistration());
			ps.setInt(4, ce.getWorkers());
			ps.setString(5, ce.getEmail());
			ps.setString(6, ce.getFloorArea());
			ps.setString(7, ce.getBuildingArea());
			ps.setString(8, ce.getStorageArea());
			ps.setString(9, ce.getFoundTime());
			ps.setString(10, ce.getAdress());
			ps.setString(11, ce.getLongitude());
			ps.setString(12, ce.getLatitude());
			ps.setString(13, ce.getInvolved());
			ps.setInt(14, ce.getAreaId());
			ps.setString(15, ce.getMarks());
			ps.setString(16, ce.getTelephone());
			ps.setInt(17, ce.getCharacterId());
			ps.setString(18, ce.getDangerous());
			ps.setString(19, ce.getMaxdanger());
			ps.setString(20, ce.getFighting());
			ps.setInt(21, ce.getFirelane());
			ps.setInt(22, ce.getSafetyexit());
			ps.setInt(23, ce.getExtincteur());
			ps.setInt(24, ce.getElevator());
			ps.setInt(25, ce.getStaircase());
			ps.setString(26, ce.getPositions());
			ps.setString(27, ce.getCardid());
			
			if(Utils.isNullStr(ce.getFrontimage1())) {
				ps.setString(28, ce.getFrontimage1());
				if(Utils.isNullStr(ce.getFrontimage2())){
					ps.setString(29, ce.getFrontimage2());
					if(Utils.isNullStr(ce.getFrontimage3())){
						ps.setString(30, ce.getFrontimage3());
						if(Utils.isNullStr(ce.getFrontimage4())){
							ps.setString(31, ce.getFrontimage4());
							ps.setString(32, ce.getPhone());
						}else{
							ps.setString(31, ce.getPhone());
						}
					}else if(Utils.isNullStr(ce.getFrontimage4())){
						ps.setString(30, ce.getFrontimage4());
						ps.setString(31, ce.getPhone());
					}else{
						ps.setString(30, ce.getPhone());
					}
				}else if(Utils.isNullStr(ce.getFrontimage3())){
					ps.setString(29, ce.getFrontimage3());
					if(Utils.isNullStr(ce.getFrontimage4())){
						ps.setString(30, ce.getFrontimage4());
						ps.setString(31, ce.getPhone());
					}else{
						ps.setString(30, ce.getPhone());
					}
				}else if(Utils.isNullStr(ce.getFrontimage4())){
					ps.setString(29, ce.getFrontimage4());
					ps.setString(30, ce.getPhone());
				}else{
					ps.setString(29, ce.getPhone());
				}
			}else if(Utils.isNullStr(ce.getFrontimage2())) {
				ps.setString(28, ce.getFrontimage2());
				if(Utils.isNullStr(ce.getFrontimage3())){
					ps.setString(29, ce.getFrontimage3());
					if(Utils.isNullStr(ce.getFrontimage4())){
						ps.setString(30, ce.getFrontimage4());
						ps.setString(31, ce.getPhone());
					}else{
						ps.setString(30,ce.getPhone());
					}
				}else if(Utils.isNullStr(ce.getFrontimage4())){
					ps.setString(29, ce.getFrontimage4());
					ps.setString(30, ce.getPhone());
				}else{
					ps.setString(29, ce.getPhone());
				}
			}else if(Utils.isNullStr(ce.getFrontimage3())) {
				ps.setString(28, ce.getFrontimage3());
				if(Utils.isNullStr(ce.getFrontimage4())){
					ps.setString(29, ce.getFrontimage4());
					ps.setString(30, ce.getPhone());
				}else{
					ps.setString(29, ce.getPhone());
				}
			}else if(Utils.isNullStr(ce.getFrontimage4())) {
				ps.setString(28, ce.getFrontimage4());
				ps.setString(29, ce.getPhone());
			}else{
				ps.setString(28, ce.getPhone());
			}
			
			
			
			result = ps.executeUpdate();
			if(result>0){
				bool = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return bool;
	}

}
