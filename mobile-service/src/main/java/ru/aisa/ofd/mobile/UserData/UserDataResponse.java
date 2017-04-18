package ru.aisa.ofd.mobile.UserData;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDataResponse {
	private int errorCode;
	private String description; 
	private String login; 
    private String id;
    private String tgt;
    private String type;
  
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@JsonProperty("TGT")
	public String getTgt() {
		return tgt;
	}
	@JsonProperty("TGT")
	public void setTgt(String tgt) {
		this.tgt = tgt;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}    
}
