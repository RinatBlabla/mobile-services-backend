package ru.aisa.ofd.mobile.logout;

import com.fasterxml.jackson.annotation.JsonProperty;

import ru.aisa.cas.TicketGrantingTicket;

public class LogoutRequest {
	private String login;
	private String id;
	private TicketGrantingTicket tgt;
	private String type;
	
	public String getLogin() {
		return login;
	}
	public String getId() {
		return id;
	}
	@JsonProperty("TGT")
	public TicketGrantingTicket getTgt() {
		return tgt;
	}
	public String getType() {
		return type;
	}
}
