package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.dao.OperationDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.Operation;
import com.cloudfire.entity.OperationQuery;
import com.cloudfire.entity.OperationType;

public class OperationDaoImpl implements OperationDao{

	@Override
	public int saveOperationRecord(int optype, String active, String passive,
			String time, int status) {
		String sql = " insert into operation_record(optype,active,passive,time,status) values(?,?,?,?,?) ";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ppst = DBConnectionManager.prepare(conn, sql);
		int result = 0;
		try {
			ppst.setInt(1, optype);
			ppst.setString(2, active);
			ppst.setString(3, passive);
			ppst.setString(4, time);
			ppst.setInt(5, status);
			result = ppst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ppst);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}

	@Override
	public List<OperationType> getAllType() {
		String sql = "select optid,optype from operationtype where optid != 0";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<OperationType> lstop = new ArrayList<OperationType>();
		try {
			rs = ps.executeQuery();
			while (rs.next()){
				OperationType op =new OperationType();
				op.setOptid(rs.getInt("optid"));
				op.setTypeName(rs.getString("optype"));
				lstop.add(op);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstop;
	}
	
	@Override
	public int  getOperationCount(OperationQuery query) {
		String sql = " select count(*) from operation_record where 1=1 ";
		if (StringUtils.isNotBlank(query.getOperator())){
			sql+= " and active = '"+query.getOperator()+"'";
		}
		if (StringUtils.isNotBlank(query.getObject())){
			sql+= " and passive = '"+query.getObject()+"'";
		}
		if (StringUtils.isNotBlank(query.getOpt())){
			sql+= " and optype = "+query.getOpt();
		}
		if (StringUtils.isNotBlank(query.getStartTime())){
			sql+= " and time > '"+query.getStartTime()+"'";
		}
		if (StringUtils.isNotBlank(query.getEndTime())){
			sql+= " and time < '"+query.getEndTime()+"'";
		}
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int total = 0;
		try {
			rs = ps.executeQuery();
			while(rs.next()) {
				total = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return total;
	}
	
	@Override
	public List<Operation> getOperations(OperationQuery query) {
		String sql = "select op.active,op.passive,op.time,op.status,opt.optid,opt.optype from operation_record op,operationType opt where op.optype = opt.optid  ";
		if (StringUtils.isNotBlank(query.getOperator())){
			sql+= " and active = '"+query.getOperator()+"'";
		}
		if (StringUtils.isNotBlank(query.getObject())){
			sql+= " and passive = '"+query.getObject()+"'";
		}
		if (StringUtils.isNotBlank(query.getOpt())){
			sql+= " and op.optype = "+query.getOpt();
		}
		if (StringUtils.isNotBlank(query.getStartTime())){
			sql+= " and time > '"+query.getStartTime()+"'";
		}
		if (StringUtils.isNotBlank(query.getEndTime())){
			sql+= " and time < '"+query.getEndTime()+"'";
		}
		sql += " order by time desc ";
//		sql += " limit "+query.getStartRow() + ","
//				+ query.getPageSize();
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		List<Operation> lstOp = new ArrayList<Operation>();
		try {
			rs = ps.executeQuery();
			while(rs.next()) {
				Operation op = new Operation();
				op.setOperator(rs.getString("active"));
				op.setObject(rs.getString("passive"));
				op.setOptype(rs.getInt("optid"));
				op.setTypeName(rs.getString("optype"));
				String result = "";
				switch(rs.getInt("status")){
				case 0:
					result= "待定";
					break;
				case 1:
					result= "成功";
					break;
				case 2:
					result= "失败";
					break;
				default:
					result = "失败";
					break;
				}
				op.setResult(result);
//				op.setState(rs.getInt("state"));
				op.setTime(rs.getString("time"));
//				op.setContent(rs.getString("content"));
				lstOp.add(op);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return lstOp;
	}

	@Override
	public int saveOperation(Operation operation) {
		String sql ="";
		switch(operation.getOptype()){
		case 2: //设置高阈值
		case 3: //设置低阈值
		case 4: //设置采集时间间隔
		case 5://设置上报时间间隔
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
			sql = "insert into operation(operator,object,opt,time,content) values(?,?,?,?,?)";
			break;
		default:
			sql = "insert into operation(operator,object,opt,time,content,state) values(?,?,?,?,?,1)";
		}
		int rs = 0;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		try {
			ps.setString(1, operation.getOperator());
			ps.setString(2, operation.getObject());
			ps.setInt(3, operation.getOptype());
			ps.setString(4, operation.getTime());
			ps.setString(5, operation.getContent());
			rs  = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}

	@Override
	public int getState(String waterMac, int optType) {
		String sql = "select state from operation where object = ? and opt = ? order by time desc  limit 1";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		ResultSet rs = null;
		int result = 0;
		try {
			ps.setString(1, waterMac);
			ps.setInt(2, optType);
			rs = ps.executeQuery();
			while(rs.next()) {
				result =rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			DBConnectionManager.close(rs);
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		
		return result;
	}

	@Override
	public int updateOperationState(String smokeMac, int state) {
		String sql = "update operation set state = ? where  object = ? and state = 0";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		int rs =  0;
		try {
			ps.setInt(1, state);
			ps.setString(2, smokeMac);
			
			rs = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
		return rs;
	}
}
