package ru.aisa.ofd.mobile.Controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import ru.aisa.casr.casrRestClient;
import ru.aisa.casr.casrException;
import ru.aisa.casr.TicketGrantingTicket;
import ru.aisa.casr.TicketValidationResult;
import ru.aisa.ofd.mobile.casr.casrIntegrator;
import ru.aisa.ofd.mobile.Interface.MethodsDAO;
import ru.aisa.ofd.mobile.KKT.KKTDataRequest;
import ru.aisa.ofd.mobile.KKT.KKTDataResponse;
import ru.aisa.ofd.mobile.UserData.*;
import ru.aisa.ofd.mobile.logout.LogoutRequest;

@Controller
public class HomeController {	
	private static final int SUCCESSCODE = 0;
	private static final String casrException = "casrException";
	private static final int UNSUCCESSCODE = -1;
	private MethodsDAO methodsDAO;
	
	private String casrUsername ="";
	private String casrPassword ="";
	private TicketGrantingTicket casrTGT;
	
	Locale dLocale = new Locale.Builder().setLanguage("ru").setScript("Cyrl").build();
	ResourceBundle labels = ResourceBundle.getBundle("Translations", dLocale);
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	public void setMethodsDAO(MethodsDAO ms)
	{
		this.methodsDAO = ms;
	}
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String testConnection() throws SQLException{
		casrUsername = "zibawanam@binka.me";	
		return "home";
	}
	//-Следующий блок добавлен лишь для требуемого отображения в логах
	//-Отображение лишь нужных данных оставлено, но закомментировано
	public String convertToJSON(Object o){
		Gson gson = new Gson();
		String json = gson.toJson(o);
		return json;
	}
	//---
	
//Регистрация пользователя в системе (POST):--------------------------------------------------------------------------
	  @RequestMapping(value = "/", method = RequestMethod.POST,  produces = "application/json")
	  @ResponseBody
	  public UserDataResponse postLoginAndPass(@RequestBody UserDataRequest  reqBody) { 
		  UserDataResponse udr = new UserDataResponse();
		  
		  try{
			 casrUsername = reqBody.getLogin();
			 casrPassword = reqBody.getPassword();
			 //-Следующий блок возможно не понадобится в финальной версии
			 logger.debug(" Запрос на регистрацию: " + convertToJSON(udr));
			 //---
			 //logger.debug(" Запрос на регистрацию. Логин = " + casrUsername);

			 //Валидация данных на стороннем сервере
		 
			 casrIntegrator ci = new casrIntegrator(casrUsername, casrPassword);
			 if(ci.getResultCode() == TicketValidationResult.casr_SUCCESS){
				 udr.setErrorCode(SUCCESSCODE);
				 udr.setDescription(labels.getString("" + SUCCESSCODE));
				 udr.setId(ci.getId());
				 udr.setLogin(ci.getLogin());
				 udr.setTgt(ci.getTgtParam());
				 udr.setType(ci.getType());
				/* logger.debug(" " + udr.getLogin() + " зарегистрирован!"+ "\n" + 
				 "Ответ на запрос на регистрацию  Login = " +
				 udr.getLogin()  + "; ID =  " + udr.getId() + "; TGT =  " + udr.getTgt() + 
				 "; Type = " + udr.getType());*/
				 logger.debug("Ответ на запрос на регистрацию: " + convertToJSON(udr));
				 
				 return udr;
			 }
			 else{
				 udr.setErrorCode(ci.getResultCode());
				 udr.setDescription(labels.getString("" + ci.getResultCode()));
				 /*logger.warn(" " + casrUsername  + " не зарегистрирован. Код ошибки: " 
				 + ci.getResultCode()  + " Описание ошибки: " + ci.getcasrErrorDesc());*/
				 logger.warn(" Запрос на регистрацию: " + convertToJSON(udr));
				 //---
	             return udr;
			 }
		 }catch(casrException cwfe){
			 udr.setErrorCode(cwfe.getErrorCode());
			 udr.setDescription(labels.getString(casrException));
			 /*logger.warn(" " + casrUsername  + " не зарегистрирован. Код ошибки: "
			 + cwfe.getErrorCode()+ " Описание ошибки: " + cwfe.getMessage());*/
			 logger.warn("Ответ на запрос на регистрацию: " + convertToJSON(udr));
			 return udr;
			 
		 }catch(Exception e){
			 udr.setErrorCode(UNSUCCESSCODE);
			 udr.setDescription(labels.getString("" + UNSUCCESSCODE));
			 /*logger.warn(" " + casrUsername  + " не зарегистрирован. Код ошибки: "
			 + UNSUCCESSCODE + " Описание ошибки: " + e.getMessage());*/
			 logger.warn("Ответ на запрос на регистрацию: " + convertToJSON(udr));
			 return udr;
		 }
	  }
	  
//Получение данных о ККТ (POST):---------------------------------------------------------------
	  @RequestMapping(value = "/kkt", method = RequestMethod.POST,  produces = "application/json")
	  @ResponseBody
	  public KKTDataResponse postKKT(@RequestBody KKTDataRequest  reqBody){
		  KKTDataResponse k = new KKTDataResponse();
		  
		  casrUsername = reqBody.getLogin();
		  casrTGT = reqBody.getTgt();
		  
		  /*logger.debug(" Запрос на получение данных о ККТ. Логин = " + casrUsername + "; ID =  "
		  + casrId + "; TGT =  " + casrTGT + "; Тип = " + casrType);*/
		  logger.debug(" Запрос на получение данных о ККТ: " + convertToJSON(reqBody));
		  
		  try{
			  //сначала - проверка валидности  tgt в casrе
			  casrIntegrator ci = new casrIntegrator(casrUsername, casrTGT,"no");
			  
			  if(ci.getResultCode() == casrRestClient.TGT_STATUS_OK){
				  k.setErrorCode("" +ci.getResultCode());
				  k.setDescription(labels.getString("" + SUCCESSCODE));
				  /*logger.debug(" Валидация TGT для пользователя: " + casrUsername + " с TGT =  " + casrTGT +
						  "прошла успешно!");*/
				  logger.debug(" Результат проверки валидности TGT от casr: " + convertToJSON(k));
				  
				  //далее селекты в БД
				  Map<String,Object> map = new HashMap<String,Object>();
				  map = methodsDAO.kktData(casrUsername);
				  JSONArray jsonList = new JSONArray();
				  int kktCountSQL =  methodsDAO.kktCountSQL(casrUsername);
				  int tradePCount = methodsDAO.tradePCountSQL(casrUsername);
				 
				  k.setKktcount("" + kktCountSQL);
				  k.setKktdataarray(jsonList.put(map.get("kktdataarray")));
				  k.settradePCount(""+ tradePCount);
				  k.setKktonlinecount("" +map.get("kktOnlineCount"));
				  k.setKktofflinecount("" +map.get("kktOfflineCount"));
				  k.setKktcritofflinecount("" +map.get("kktCritOfflineCount"));
				  k.setKktexofflinecount("" +map.get("kktExOfflineCount"));
				  
				  jsonList= null;
				  map = null;
				  return k;
			  }
			  else{
				  k.setErrorCode("" +ci.getResultCode());
				  k.setDescription(labels.getString("" + ci.getResultCode()));	
				  /*logger.warn(" Валидация TGT для пользователя: " + casrUsername +" с TGT =  " + casrTGT +
					" завершилась с ошибкой. Код ошибки: "+ ci.getResultCode() + " Описание ошибки: "
					+ ci.getcasrErrorDesc());*/
				  logger.warn(" Результат проверки валидности TGT от casr: " + convertToJSON(k));

				  return k;
			  }
		  }catch(casrException cwfe){
			  k.setErrorCode("" + cwfe.getErrorCode());
			  k.setDescription(labels.getString(casrException));
			  /*logger.warn(" Валидация TGT для пользователя: " + casrUsername +" с TGT =  " + casrTGT
					+ " завершилась с ошибкой. Код ошибки: " + cwfe.getErrorCode() + " Описание ошибки: "
					+ cwfe.getMessage());*/
			  logger.warn(" Результат проверки валидности TGT от casr: " + convertToJSON(k));

			  return k;
		  
		  }catch(Exception e){
			  k.setErrorCode("" +UNSUCCESSCODE);
			  k.setDescription(labels.getString("" + UNSUCCESSCODE));
			  /*logger.warn(" Валидация TGT для пользователя: " + casrUsername +" с TGT =  " + casrTGT
					  + " завершилась с ошибкой. Код ошибки: " + UNSUCCESSCODE + " Описание ошибки: "
					  + e.getMessage());*/
			  logger.warn(" Результат проверки валидности TGT от casr: " + convertToJSON(k));
			  logger.warn("Ошибка: "+ e.getMessage() );
			  
			  return k;
		  }
	  }
	  
//Логаут (POST):---------------------------------------------------------------------------------
	  @RequestMapping(value = "/logout", method = RequestMethod.POST,  produces = "application/json")
	  @ResponseBody
	  public ResponseEntity<String> logoutResp(@RequestBody LogoutRequest  reqBody) {  
		  casrUsername = reqBody.getLogin();
		//  casrId = reqBody.getId();
		  casrTGT = reqBody.getTgt();
		  //casrType = reqBody.getType();
		  /*logger.debug(" Запрос на логаут. Логин = " + casrUsername + "; ID =  " + casrId +"; TGT =  "
		  + casrTGT + "; Тип = " + casrType);*/
		  logger.debug(" Запрос на логаут: " + convertToJSON(reqBody));
		  
		  try{
			  //сначала - проверка валидности  tgt
			  casrIntegrator ci = new casrIntegrator(casrUsername, casrTGT,"yes");
			  if(ci.getResultCode() == casrRestClient.TGT_STATUS_OK){
				  logger.debug(" Логаут для пользователя: " + casrUsername + "прошел успешно!");
				  return new ResponseEntity<String>(HttpStatus.OK);
			  }
			  else{
				  logger.warn(" Логаут для пользователя: " + casrUsername + "с TGT =  " + casrTGT + 
						   " завершился с ошибкой. Код ошибки: " + ci.getResultCode() 
						   + " Описание ошибки: " + ci.getcasrErrorDesc());				  
				  return new ResponseEntity<String>(HttpStatus.OK);
			  }
		  }catch(casrException cwfe){
			  logger.warn(" Логаут для пользователя: " + casrUsername + "с TGT =  " + casrTGT + 
						   " завершился с ошибкой. Код ошибки: " + cwfe.getErrorCode() 
					  + "Описание ошибки: " + cwfe.getMessage());
			  return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		  
		  }catch(Exception e){		
			  logger.warn(" Логаут для пользователя: " + casrUsername + "с TGT =  " + casrTGT + 
						   " завершился с ошибкой. Код ошибки: " + UNSUCCESSCODE  
					  + "Описание ошибки: " + e.getMessage());
			  return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		  }
	  }
}
