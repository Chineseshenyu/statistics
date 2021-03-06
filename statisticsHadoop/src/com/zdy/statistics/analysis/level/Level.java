package com.zdy.statistics.analysis.level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.bson.types.BasicBSONList;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.zdy.statistics.analysis.common.MongoDBConnector;
import com.zdy.statistics.analysis.common.MysqlConnect;
import com.zdy.statistics.main.MainRun;
import com.zdy.statistics.util.DateTimeUtil;

public class Level {

	private DB db;
	private DBCollection collection;
	
	public Level() {
		db = MongoDBConnector.getDB();
		collection = db.getCollection("server");
	}
	
	/**
	 * mongodb脚本
	 * db.runCommand({"group":{
		"ns":"server",
		"key":{"message.level":true},
		"initial":{"count":0},
		"$reduce":function(doc,prev){
					prev.count++;
				},
		"condition":{"message.type":"level"}
		}})
	 * @return
	 */
	public String analysis(){
		
		BasicDBObject cmd = new BasicDBObject();
		
		BasicDBObject group = new BasicDBObject();
		group.put("ns", "server");
		group.put("key", new BasicDBObject("message.level","true"));
		group.put("initial", new BasicDBObject("count",0));
		group.put("$reduce", "function(doc,prev){"+
							 "	prev.count++; "+
							 "}");
		group.put("condition", new BasicDBObject("message.type","level"));
		
		cmd.put("group", group);
		CommandResult commandResult = db.command(cmd);
		BasicBSONList retval = (BasicBSONList) commandResult.get("retval");
		
		Map<Integer,String> resMap = new TreeMap<Integer, String>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				
				return o2.compareTo(o1);
			}
			
		});
		if(retval != null){
			for (Object object : retval) {
				
				BasicDBObject dbObject = (BasicDBObject) object;
				Integer level = ((int)(double)dbObject.get("message.level"));
				Integer count = (int)(double)(dbObject.get("count"));
				
				resMap.put(level, count+"");
			}
//			resMap.put("日期", DateTimeUtil.dateCalculate(new Date(), -1));
		}
		
		return JSONObject.fromObject(resMap).toString();
	}
	
	public void insertResult(){
		Connection connection = null;
		
		String sql = " insert into level_info (result,date) values (?,?)";
		PreparedStatement pstmt = null;
		
		try {
			connection = MysqlConnect.getConnection();
			connection.setAutoCommit(false);
			pstmt = connection.prepareStatement(sql);
			System.out.println(analysis());
			pstmt.setString(1, analysis());
			pstmt.setString(2, DateTimeUtil.dateCalculate(new Date(), -1));
			
			pstmt.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			if(pstmt != null){try { pstmt.close(); } catch (SQLException e) { }}
			if(connection != null){try { connection.close(); } catch (SQLException e) { }}
		}
	}
	public static void main(String[] args) {
		new Level().insertResult();
	}
}
