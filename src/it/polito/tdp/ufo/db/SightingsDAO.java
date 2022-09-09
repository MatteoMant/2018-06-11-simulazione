package it.polito.tdp.ufo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.ufo.model.AvvistamentiPerAnno;
import it.polito.tdp.ufo.model.Sighting;

public class SightingsDAO {
	
	public List<Sighting> getSightings() {
		String sql = "SELECT * FROM sighting WHERE country = 'us'" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Sighting> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(new Sighting(res.getInt("id"),
						res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), 
						res.getString("state"), 
						res.getString("country"),
						res.getString("shape"),
						res.getInt("duration"),
						res.getString("duration_hm"),
						res.getString("comments"),
						res.getDate("date_posted").toLocalDate(),
						res.getDouble("latitude"), 
						res.getDouble("longitude"))) ;
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<AvvistamentiPerAnno> getAllAvvistamentiPerAnno() {
		String sql = "SELECT YEAR(datetime) AS year, COUNT(*) AS num_avv "
				+ "FROM sighting "
				+ "WHERE country = 'us' "
				+ "GROUP BY YEAR(datetime) "
				+ "ORDER BY YEAR(datetime)" ;
			
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<AvvistamentiPerAnno> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(new AvvistamentiPerAnno(res.getInt("year"), res.getInt("num_avv"))) ;
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<String> getAllStatiByAnno(int anno) {
		String sql = "SELECT DISTINCT state "
				+ "FROM sighting "
				+ "WHERE country = 'us' AND YEAR(datetime) = ? "
				+ "ORDER BY state" ;
			
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<String> list = new ArrayList<>() ;
			
			st.setInt(1, anno);
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(res.getString("state")) ;
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public boolean verticiDaCollegare(String v1, String v2, int anno) {
		String sql = "SELECT stato1.state, stato2.state, COUNT(*) AS cnt "
				+ "FROM "
				+ "(SELECT * "
				+ "FROM sighting "
				+ "WHERE country = 'us' AND state = ? AND YEAR(datetime) = ?) AS stato1, "
				+ "(SELECT * "
				+ "FROM sighting "
				+ "WHERE country = 'us' AND state = ? AND YEAR(datetime) = ?) AS stato2 "
				+ "WHERE stato2.datetime > stato1.datetime" ;
			
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setString(1, v1);
			st.setInt(2, anno);
			
			st.setString(3, v2);
			st.setInt(4, anno);
			
			ResultSet res = st.executeQuery() ;
			
			res.first();
			
			int count = res.getInt("cnt") ;
	
			conn.close();
			return count > 0 ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false ;
		}
	}
	
}
