package ru.aisa.ofd.mobile.UserData;

import java.io.Serializable;

public class UserDataRequest implements Serializable {
	private String login;
	private String password;
	
	public String getLogin() {
		return login;
	}
	public String getPassword() {
		return password;
	}	
}
