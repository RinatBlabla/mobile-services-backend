package ru.company.ofd.mobile.casr;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import ru.company.casr.casrRestApiConfiguration;
import ru.company.casr.casrRestClient;
import ru.company.casr.casrException;
import ru.company.casr.ServiceTicket;
import ru.company.casr.TicketGrantingTicket;
import ru.company.casr.TicketValidationResult;
import ru.company.casr.TicketValidationResult.casrError;

///интеграция со сторонним сервисом

public class casrIntegrator {
	private static final String casr_TEST_URL_STRING = System.getProperty("casrUrl");
	private static final String casr_TEST_SERVICE_URL_STRING = System.getProperty("casrServiceUrl");
	private String login;
	private String type;
	private String id;
	private String tgtParam;
	private int resultCode;
	private casrError casrErrorDesc;

	public casrIntegrator(String casr_USERNAME,String casr_PASSWORD) throws KeyManagementException,
	IllegalArgumentException, NoSuchAlgorithmException, IOException, casrException,
	URISyntaxException {
		final URL casrUrl = new URL(casr_TEST_URL_STRING);

		// Конфигурирую REST клиент, в нем же задают политики доверия сертификатом и хостам через публичные сеттеры.

		final casrRestApiConfiguration conf = new casrRestApiConfiguration(casrUrl);
        conf.setTimeout(1000);		
		final casrRestClient client = conf.initialize();
		final TicketGrantingTicket tgt = client.requestTicketGrantingTicket(casr_USERNAME, casr_PASSWORD);
		final ServiceTicket st = client.requestServiceTicket(tgt, casr_TEST_SERVICE_URL_STRING);
		final TicketValidationResult tvr = client.validateServiceTicket(st);
		conf.setTimeout(1000);
		
		tgtParam = tgt.getTgt();

		// Аттрибуты пользователя (логин, тип: юзер\агент и id) приходят в составе TicketValidationResult.
		login = tvr.getAttributes().get("login").toString();
		type = tvr.getAttributes().get("type").toString();
		id = tvr.getAttributes().get("id").toString();
		resultCode = tvr.getValidationResultCode();
		casrErrorDesc = tvr.getcasrError();
	}
	
	public casrIntegrator(String casr_USERNAME,TicketGrantingTicket receivedTGT,String logout) 
			throws KeyManagementException, IllegalArgumentException, NoSuchAlgorithmException, 
			IOException, casrException, URISyntaxException{
		final URL casrUrl = new URL(casr_TEST_URL_STRING);
		final casrRestApiConfiguration conf = new casrRestApiConfiguration(casrUrl);
        conf.setTimeout(1000);		
		final casrRestClient client = conf.initialize();
		final int status = client.getTicketGrantingTicketStatus(receivedTGT);
		conf.setTimeout(1000);
		resultCode = status;
		
		if(logout.equals("yes")){
			client.deleteTicketServiceTicket(receivedTGT);
		}
	}
	
	public casrError getcasrErrorDesc() {
		return casrErrorDesc;
	}

	public int getResultCode() {
		return resultCode;
	}

	public String getTgtParam() {
		return tgtParam;
	}

	public String getLogin(){
		return login;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}
}
