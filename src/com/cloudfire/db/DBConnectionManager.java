package com.cloudfire.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBConnectionManager {
//  private static Log                   
    private static ComboPooledDataSource cpds = null;
    public static void init() {
        // 建立数据库连接池
        String DRIVER_NAME = SystemConfig.getConfigInfomation("DRIVER"); // 驱动器
        String DATABASE_URL = SystemConfig.getConfigInfomation("URL"); // 数据库连接url
        String DATABASE_USER = SystemConfig.getConfigInfomation("USERNAME"); // 数据库用户名
        String DATABASE_PASSWORD = SystemConfig.getConfigInfomation("PASSWORD"); // 数据库密�码
        int Min_PoolSize = 50;
        int Max_PoolSize = 200;
        int Acquire_Increment = 5;
        int Initial_PoolSize = 10;
        int Idle_Test_Period = 3000;// 每隔3000s检测空闲连接是否超过最大空闲时间
        int Max_IdleTime=1800; //连接池内连接的最大空闲时间
        String Validate = SystemConfig.getConfigInfomation("Validate");// 每次连接验证连接是否可用
        if (Validate.equals("")) {            Validate = "false";        }
        // 最小连接数
        try {
            Min_PoolSize = Integer.parseInt(SystemConfig.getConfigInfomation("Min_PoolSize"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 增量条数
        try {
            Acquire_Increment = Integer.parseInt(SystemConfig.getConfigInfomation("Acquire_Increment"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 最大连接数
        try {
            Max_PoolSize = Integer.parseInt(SystemConfig.getConfigInfomation("Max_PoolSize"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 初始化连接数
        try {
            Initial_PoolSize = Integer.parseInt(SystemConfig.getConfigInfomation("Initial_PoolSize"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 每隔3000s测试连接是否可以正常使用
        try {
            Idle_Test_Period = Integer.parseInt(SystemConfig.getConfigInfomation("Idle_Test_Period"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try {
        	Max_IdleTime = Integer.parseInt(SystemConfig.getConfigInfomation("Max_IdleTime"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            cpds = new ComboPooledDataSource();
            cpds.setDriverClass(DRIVER_NAME); // 驱动器
            cpds.setJdbcUrl(DATABASE_URL); // 数据库url
            cpds.setUser(DATABASE_USER); // 用户名
            cpds.setPassword(DATABASE_PASSWORD); // 密码
            cpds.setInitialPoolSize(Initial_PoolSize); // 初始化连接池大小
            cpds.setMinPoolSize(Min_PoolSize); // 最少连接数
            cpds.setMaxPoolSize(Max_PoolSize); // 最大连接数
            cpds.setMaxIdleTime(Max_IdleTime); //@add 080322
            cpds.setAcquireIncrement(Acquire_Increment); // 连接数的增量
            cpds.setIdleConnectionTestPeriod(Idle_Test_Period); // 测连接有效的时间间隔
            cpds.setTestConnectionOnCheckout(Boolean.getBoolean(Validate)); // 每次连接验证连接是否可用
            
            
//            cpds.setTestConnectionOnCheckin(true);		//<!--如果设为true那么在取得连接的同时将校验连接的有效性。Default: false --> 
//            cpds.setIdleConnectionTestPeriod(20);		//<!--每20秒检查所有连接池中的空闲连接。Default: 0 -->    
//            cpds.setPreferredTestQuery("SELECT 1");
            
//            cpds.setAcquireRetryAttempts(3);	//<!--定义在从数据库获取新连接失败后重复尝试的次数。Default: 30 --> 
//			  cpds.setBreakAfterAcquireFailure(true);	//如果设为true，那么在尝试获取连接失败后该数据源将申明已断开并永久关闭。
//            cpds.setCheckoutTimeout(8000);		//<!--当连接池用完时客户端调用getConnection()后等待获取新连接的时间，超时后将抛出SQLException,如设为0则无限期等待。单位毫秒。Default: 0 -->
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int getIConnectionsCount(int type){
		try {
			if (cpds != null) {
				switch(type){
				case 1:
					return cpds.getNumConnections();
				case 2:
					return cpds.getNumBusyConnections();
				case 3:
					return cpds.getNumIdleConnections();
				case 4:
					return cpds.getNumUnclosedOrphanedConnections();
				}
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}

    	return -1;
    }
    
    public static Connection getConnection() {// 获取数据库连接
        Connection connection = null;
        try {
            if (cpds == null) {
                init();
            }
            connection = cpds.getConnection(); // getconnection
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }

    public static void release() {
        try {
            if (cpds != null) {
                cpds.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static PreparedStatement prepare(Connection conn,  String sql) {
		PreparedStatement pstmt = null; 
		try {
			if(conn != null) {
				pstmt = conn.prepareStatement(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pstmt;
	}
	
	public static PreparedStatement prepare(Connection conn,  String sql, int autoGenereatedKeys) {
		PreparedStatement pstmt = null; 
		try {
			if(conn != null) {
				pstmt = conn.prepareStatement(sql, autoGenereatedKeys);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pstmt;
	}
	
	public static Statement getStatement(Connection conn) {
		Statement stmt = null; 
		try {
			if(conn != null) {
				stmt = conn.createStatement();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmt;
	}
	
	
	public static ResultSet getResultSet(Statement stmt, String sql) {
		ResultSet rs = null;
		try {
			if(stmt != null) {
				rs = stmt.executeQuery(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static void executeUpdate(Statement stmt, String sql) {
		try {
			if(stmt != null) {
				stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(Connection conn) {
		try {
			if(conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(Statement stmt) {
		try {
			if(stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(PreparedStatement ps) {
		try {
			if(ps != null) {
				ps.close();
				ps = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(ResultSet rs) {
		try {
			if(rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
