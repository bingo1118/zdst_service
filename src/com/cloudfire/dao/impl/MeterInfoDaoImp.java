package com.cloudfire.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.cloudfire.dao.MeterInfoDao;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.ProofGasEntity;
import com.cloudfire.until.GetTime;

public class MeterInfoDaoImp implements MeterInfoDao {

	@Override
	public boolean updateMeterInfo(String mac,String quantity, String time,String voltage,String electricity,String power) {
		// TODO Auto-generated method stub
		String sql="insert into elecMeterDeviceInfo (device,quantity,time,voltage,electricity,power) values(?,?,?,?,?,?)";
		Connection conn=DBConnectionManager.getConnection();
		PreparedStatement ps=DBConnectionManager.prepare(conn, sql);
		boolean result=false;
		try{
			ps.setString(1, mac);;
			ps.setString(2, quantity);
			ps.setString(3, time);
			ps.setString(4, voltage);
			ps.setString(5, electricity);
			ps.setString(6, power);
			if(ps.executeUpdate()>0)
				result=true;
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			DBConnectionManager.close(ps);;
			DBConnectionManager.close(conn);
		}
		return result;
	}

	@Override
	public void insertProofGas(ProofGasEntity gas) {
		String sql = "INSERT into g_proofgas(proofGasMac,proofGasType,proofGasMmol,proofGasTemp,proofGasModel,proofGasUnit,proofGasState,proofGasTime)VALUES(?,?,?,?,?,?,?,?)";
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, sql);
		String time = GetTime.ConvertTimeByLong();
		try {
			ps.setString(1, gas.getProofGasMac());
			ps.setString(2, gas.getProofGasType());
			ps.setString(3, gas.getProofGasMmol());
			ps.setString(4, gas.getProofGasTemp());
			ps.setString(5, gas.getProofGasModel());
			ps.setString(6, gas.getProofGasUnit());
			ps.setString(7, gas.getProofGasState());
			ps.setString(8, time);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			DBConnectionManager.close(conn);
		}
	}

}
