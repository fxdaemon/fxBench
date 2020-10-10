package org.fxbench.trader.local;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fxbench.entity.BaseEntity;
import org.fxbench.trader.LoginException;

public class DBAccess
{
	private final static Log logger = LogFactory.getLog(DBAccess.class);
	private Connection conn;
	
	public DBAccess() {
	}
	
	public static DBAccess newInstance() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		return new DBAccess();
	}
	
	public void connect(String dbHost, String dbName, String dbUserName, String dbPassword) throws LoginException {
		try {	 
			StringBuffer sb = new StringBuffer();
			sb.append("jdbc:mysql://");
			sb.append(dbHost).append("/");
			sb.append(dbName).append("?");
			conn = DriverManager.getConnection(sb.toString(), dbUserName, dbPassword);
//			dbAccess.conn.setAutoCommit(true);
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
		    throw new LoginException(ex.getCause(), ex.getMessage());
		}
	}
	
	public void close() {
        try {
            if (!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
        	logger.error(e.toString());
        }
    }
		
	public List<BaseEntity> select(BaseEntity baseEntity) {
		Statement stmt = null;
		ResultSet rs = null;
		List<BaseEntity> result = new ArrayList<BaseEntity>();
		try {
		    stmt = conn.createStatement();
		    rs = stmt.executeQuery(baseEntity.getSelSql());
		    while (rs.next()) {
                result.add(baseEntity.newEntity(rs));
            }
		} catch (SQLException ex){
		    // handle any errors
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
		} finally {
		    // it is a good idea to release resources in a finally{} block
		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException sqlEx) { } // ignore
		        rs = null;
		    }
		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore
		        stmt = null;
		    }
		}
		return result;
	}
}
