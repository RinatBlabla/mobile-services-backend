package ru.aisa.ofd.mobile.Interface;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import ru.aisa.ofd.mobile.daoObjects.TradePoint;

public interface MethodsDAO {
	
	public int tradePointsCountSQL(String login) throws SQLException;
	public int kktCountSQL(String login) throws SQLException;
	public Map<String,Object> kktData(String login) throws SQLException;
	public List<TradePoint> sqlTradePointdata(String login) throws SQLException;
}
