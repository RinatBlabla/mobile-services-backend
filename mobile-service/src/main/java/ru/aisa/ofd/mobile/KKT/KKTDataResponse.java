package ru.aisa.ofd.mobile.KKT;

import org.json.JSONArray;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KKTDataResponse {
	private String errorCode;
	private String description;
	private String tradePCount;
	private String kktcount;
	private String kktonlinecount;
	private String kktofflinecount;
	private String kktcritofflinecount;
	private String kktexofflinecount;
	private JSONArray kktdataarray;
	
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String gettradePCount() {
		return tradePCount;
	}
	public void settradePCount(String tradePCount) {
		this.tradePCount = tradePCount;
	}
	
	@JsonProperty("KKTCount")
	public String getKktcount() {
		return kktcount;
	}
	
	@JsonProperty("KKTCount")
	public void setKktcount(String kktcount) {
		this.kktcount = kktcount;
	}
	@JsonProperty("KKTOnlineCount")
	public String getKktonlinecount() {
		return kktonlinecount;
	}
	@JsonProperty("KKTOnlineCount")
	public void setKktonlinecount(String kktonlinecount) {
		this.kktonlinecount = kktonlinecount;
	}
	@JsonProperty("KKTOfflineCount")
	public String getKktofflinecount() {
		return kktofflinecount;
	}
	@JsonProperty("KKTOfflineCount")
	public void setKktofflinecount(String kktofflinecount) {
		this.kktofflinecount = kktofflinecount;
	}
	@JsonProperty("KKTCritOfflineCount")
	public String getKktcritofflinecount() {
		return kktcritofflinecount;
	}
	@JsonProperty("KKTCritOfflineCount")
	public void setKktcritofflinecount(String kktcritofflinecount) {
		this.kktcritofflinecount = kktcritofflinecount;
	}
	@JsonProperty("KKTExOfflineCount")
	public String getKktexofflinecount() {
		return kktexofflinecount;
	}
	@JsonProperty("KKTExOfflineCount")
	public void setKktexofflinecount(String kktexofflinecount) {
		this.kktexofflinecount = kktexofflinecount;
	}
	@JsonProperty("KKTDataArray")
	public JSONArray getKktdataarray() {
		return kktdataarray;
	}
	@JsonProperty("KKTDataArray")
	public void setKktdataarray(JSONArray kktdataarray) {
		this.kktdataarray = kktdataarray;
	}
}
