package idv.tony.ca103g4_app_mem;

import java.io.Serializable;

public class BranchVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -13511614598800593L;
	private String branch_No;
	private String branch_Name;
	private String branch_City;
	private String branch_Dist;
	private String branch_Addr;
	private String branch_Pos;
	private String branch_Lan;
	private String branch_Lat;
	private String branch_Time;
	private Double branch_Del;
	private String branch_Tel;
	private Integer branch_Tdesk;
	
	
	
	public String getBranch_No() {
		return branch_No;
	}
	public void setBranch_No(String branch_No) {
		this.branch_No = branch_No;
	}
	public String getBranch_Name() {
		return branch_Name;
	}
	public void setBranch_Name(String branch_Name) {
		this.branch_Name = branch_Name;
	}
	public String getBranch_City() {
		return branch_City;
	}
	public void setBranch_City(String branch_City) {
		this.branch_City = branch_City;
	}
	public String getBranch_Dist() {
		return branch_Dist;
	}
	public void setBranch_Dist(String branch_Dist) {
		this.branch_Dist = branch_Dist;
	}
	public String getBranch_Addr() {
		return branch_Addr;
	}
	public void setBranch_Addr(String branch_Addr) {
		this.branch_Addr = branch_Addr;
	}
	public String getBranch_Pos() {
		return branch_Pos;
	}
	public void setBranch_Pos(String branch_Pos) {
		this.branch_Pos = branch_Pos;
	}
	public String getBranch_Lan() {
		return branch_Lan;
	}
	public void setBranch_Lan(String branch_Lan) {
		this.branch_Lan = branch_Lan;
	}
	public String getBranch_Lat() {
		return branch_Lat;
	}
	public void setBranch_Lat(String branch_Lat) {
		this.branch_Lat = branch_Lat;
	}
	public String getBranch_Time() {
		return branch_Time;
	}
	public void setBranch_Time(String branch_Time) {
		this.branch_Time = branch_Time;
	}
	public Double getBranch_Del() {
		return branch_Del;
	}
	public void setBranch_Del(Double branch_Del) {
		this.branch_Del = branch_Del;
	}
	public String getBranch_Tel() {
		return branch_Tel;
	}
	public void setBranch_Tel(String branch_Tel) {
		this.branch_Tel = branch_Tel;
	}
	public Integer getBranch_Tdesk() {
		return branch_Tdesk;
	}
	public void setBranch_Tdesk(Integer branch_Tdesk) {
		this.branch_Tdesk = branch_Tdesk;
	}
}