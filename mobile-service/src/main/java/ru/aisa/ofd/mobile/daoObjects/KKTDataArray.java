package ru.aisa.ofd.mobile.daoObjects;

import com.fasterxml.jackson.annotation.JsonProperty;


public class KKTDataArray {
	
	private String register_number_kkt;
	private String factory_number_kkt;
	private String factory_number_fn;
	private int kktState;
	private String kktname;
	
	@JsonProperty("KKTState")
	public int getKktState() {
		return kktState;
	}
	@JsonProperty("KKTState")
	public void setKktState(int kktState) {
		this.kktState = kktState;
	}
	@JsonProperty("KKTRegId")
	public String getRegister_number_kkt() {
		return register_number_kkt;
	}
	@JsonProperty("KKTRegId")
	public void setRegister_number_kkt(String register_number_kkt) {
		this.register_number_kkt = register_number_kkt;
	}
	@JsonProperty("KKTFactId")
	public String getFactory_number_kkt() {
		return factory_number_kkt;
	}
	@JsonProperty("KKTFactId")
	public void setFactory_number_kkt(String factory_number_kkt) {
		this.factory_number_kkt = factory_number_kkt;
	}
	@JsonProperty("KKTFactFNId")
	public String getFactory_number_fn() {
		return factory_number_fn;
	}
	@JsonProperty("KKTFactFNId")
	public void setFactory_number_fn(String factory_number_fn) {
		this.factory_number_fn = factory_number_fn;
	}

	public String getKktname() {
		return kktname;
	}
	public void setKktname(String kktname) {
		this.kktname = kktname;
	}
}
