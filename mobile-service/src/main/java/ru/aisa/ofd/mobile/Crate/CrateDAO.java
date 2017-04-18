package ru.aisa.ofd.mobile.Crate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import ru.company.casr.KKTTableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import ru.aisa.ofd.mobile.Interface.MethodsDAO;
import ru.aisa.ofd.mobile.daoObjects.KKT;
import ru.aisa.ofd.mobile.daoObjects.KKTDataArray;
import ru.aisa.ofd.mobile.daoObjects.Receipt;
import ru.aisa.ofd.mobile.daoObjects.TradePoint;

@Repository
public class CrateDAO implements MethodsDAO{
	 private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	 private static String companyId = "";
	 private static final int DEFAULTSTATUSCODE = -1;
	 
	 //--Навания таблиц---------------------------------------
	 private static final String USERTABLENAME = System.getProperty("USERTABLENAME");
	 private static final String KKTSTABLENAME = System.getProperty("KKTSTABLENAME");
	 private static final String TRADEPTABLENAME = System.getProperty("TRADEPTABLENAME");
	 private static final String RECEIPTSTABLENAME = System.getProperty("RECEIPTSTABLENAME");
	//---------------------------------------------------------------------
	 
	 List<KKTDataArray> collforJSON = new LinkedList();
	 List<String> testList = new LinkedList();
	 Set<String> setkktRegistrationId = new HashSet<String>();
	 HashMap h = new HashMap();
	 Map<String,Object> map = new HashMap<String,Object>();
	 List<Object> kktAndStatistics = new LinkedList();
	 
	 @Autowired
	 public void setDataSource(DataSource dataSource){
		 this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	 }
	private void companyIdGet(String login) throws SQLException{
			
			String sql = "select companyId from " + USERTABLENAME + " where uLogin =:u_login";
			SqlParameterSource namedParameters = new MapSqlParameterSource("u_login", login);
			
			companyId = this.namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
		}
	
	public int tradePCountSQL(String login) throws SQLException{
		//String sql = "select count(tp.id) from user u,tradeP tp where u.companyId = tp.companyId and u.uLogin =:u_login";
		//Делю запросы на два, тк в crate аггрегационные функции не поддерживаются для джоинтов
		//кроме того разрешенная выборка не более 100
		
		if(companyId.equals(""))
		{
			companyIdGet(login);
		}
		String sql = "select count(id) from "+TRADEPTABLENAME+" where companyId  =:companyId";  
	    SqlParameterSource namedParameters = new MapSqlParameterSource("companyId", companyId);

	    return this.namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
	}
	
	public int kktCountSQL(String login) throws SQLException{  
		//Делю запросы на два, тк в crate аггрегационные функции не поддерживаются для джоинтов
		//кроме того разрешенная выборка не более 100
		
		if(companyId.equals(""))
		{
			companyIdGet(login);
		}

		String sql = "select count(id) from "+KKTSTABLENAME+" where activated = 'true' and companyId  =:companyId";   
	    SqlParameterSource namedParameters = new MapSqlParameterSource("companyId", companyId);

	    return this.namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);	
	}

	//-Здесь будет собираться вся инфа для KKTDataArray-и-вся-статистика-по-ККТ-----------------------------------------------------
	
	public Map<String,Object> kktData(String login) throws SQLException{
		
		//Делю запросы на два, тк в crate аггрегационные функции не поддерживаются для джоинтов
		String sql = "select k.factory_number_fn as factory_number_fn,  k.factory_number_kkt as factory_number_kkt,"
				+ "k.register_number_kkt as register_number_kkt from "+USERTABLENAME+" u,"+KKTSTABLENAME+" k where "
				+ "u.companyId = k.companyId and k.activated = 'true' and u.uLogin  =:u_login";
	    SqlParameterSource namedParameters = new MapSqlParameterSource("u_login", login);

	    //получил общие значения для ккт: 
	    List<KKT> lk = this.namedParameterJdbcTemplate.query(sql, namedParameters, new KKTRowMapper());
	    
	  //фильтрую kktRegistrationId на повторяющиеся    
	  	for(int i=0; i<lk.size();i++){
	  		setkktRegistrationId.add(lk.get(i).getRegister_number_kkt());
	    } 	
	  	
	  	List<Object> list = KKTStateMap(setkktRegistrationId);
	  	//setkktRegistrationId = null;                                //очищаем сет
	 	h = (HashMap) list.get(0);
	 	int size = h.size();
 
	    for(int i=0;i<lk.size();i++){
	    	KKTDataArray kda = new KKTDataArray();
	    	kda.setKktname("");
	    	kda.setFactory_number_fn(lk.get(i).getFactory_number_fn());
	    	kda.setFactory_number_kkt(lk.get(i).getFactory_number_kkt());
	    	kda.setRegister_number_kkt(lk.get(i).getRegister_number_kkt());
	    	
	    	if(size != 0){
	    		kda.setKktState(Integer.parseInt(lk.get(i).getRegister_number_kkt()));
	    	}
	    	else
	    	{
	    		kda.setKktState(DEFAULTSTATUSCODE);
	    	}
	    	collforJSON.add(kda);
	    	kda =null;
	    }
	    
	    map.put("kktdataarray", collforJSON);
	    map.put("kktOnlineCount", (int)list.get(1));
	    map.put("kktOfflineCount", (int)list.get(2));
	    map.put("kktCritOfflineCount", (int)list.get(3));
	    map.put("kktExOfflineCount", (int)list.get(4));
	    
	    return map;
	}
	
	private List<Object> KKTStateMap(Set<String> setkktRegistrationId){
			
		String sql = "select max(r.requestmessage['dateTime']) as maxDate,"+
				" r.requestmessage['kktRegistrationId']  as kktRegistrationId from "+RECEIPTSTABLENAME+" r where "+
				"r.requestmessage['kktRegistrationId'] in (:kktRegistrationId) group by r.requestmessage['kktRegistrationId']";
			
		SqlParameterSource namedParameters = new MapSqlParameterSource("kktRegistrationId", setkktRegistrationId);
		List<Receipt> lGetMaxDate = this.namedParameterJdbcTemplate
				.query(sql, namedParameters,new ReceiptRowMapper());
		
		int status = -1; //Значение по умолчанию
		int kktOnlineCount =0;
		int kktOfflineCount =0;
		int kktCritOfflineCount =0;
		int kktExOfflineCount =0;

		for(int i = 0;i<lGetMaxDate.size();i++){
			
			switch(KKTTableItem.getStatus(lGetMaxDate.get(i).getMaxDate()).toString()){
				case "Онлайн": status = 0;kktOnlineCount++;
                break;
				case "Офлайн": status = 1;kktOfflineCount++;
                break;
				case "Критическое время офлайн": status = 2;kktCritOfflineCount++;
                break;
				case "Превышено время офлайн": status = 3;kktExOfflineCount++;
                break;
				case "Неизвестно": status = -1;
                break;
                default: status = -1;
                break;
			}
			h.put(lGetMaxDate.get(i).getkktRegistrationId(), status);
		}
		kktAndStatistics.add(h);
		kktAndStatistics.add(kktOnlineCount);
		kktAndStatistics.add(kktOfflineCount);
		kktAndStatistics.add(kktCritOfflineCount);
		kktAndStatistics.add(kktExOfflineCount);
		
		return kktAndStatistics;
	}	
	
	public List<TradePoint> sqlTradePointdata(String login) throws SQLException{ 
		
		String sql = "select tp.name_tradeP as name_tradeP from "+USERTABLENAME+" u,"+TRADEPTABLENAME
				+ " tp where u.companyId = tp.companyId and u.uLogin  =:u_login";
		SqlParameterSource namedParameters = new MapSqlParameterSource("u_login", login);
		
		List<TradePoint> ltp = this.namedParameterJdbcTemplate.query(sql, namedParameters, new TradePointRowMapper());
	    System.out.println("ltp.size() = " + ltp.size());
	    return ltp;
	}
	
	private static final class KKTRowMapper implements RowMapper<KKT>{
		@Override
		public KKT mapRow(ResultSet rs, int RowNum) throws SQLException{
			KKT kkt = new KKT();

			kkt.setFactory_number_fn(rs.getString("factory_number_fn"));
			kkt.setFactory_number_kkt(rs.getString("factory_number_kkt"));
			//следующие значения хранятся в БД в формате String!!!
			kkt.setRegister_number_kkt(rs.getString("register_number_kkt"));
			return kkt;
		}
	}
	
	private static final class TradePointRowMapper implements RowMapper<TradePoint>{
		@Override
		public TradePoint mapRow(ResultSet rs, int RowNum) throws SQLException{
			TradePoint tp = new TradePoint();
			tp.setTradePointName(rs.getString("name_tradeP"));
			return tp;
		}
	}
	
	private static final class ReceiptRowMapper implements RowMapper<Receipt>{
		@Override
		public Receipt mapRow(ResultSet rs, int RowNum) throws SQLException{
			Receipt r = new Receipt();
			r.setMaxDate(rs.getLong("maxdate"));
			r.setkktRegistrationId(rs.getString("kktRegistrationId"));
			return r;
		}
	}
}
