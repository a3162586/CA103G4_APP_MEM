package idv.tony.ca103g4_app_mem;

public class CouponVO implements java.io.Serializable{
	private String  coup_Sn;
	private String coucat_No;
	private String  coup_Status;
	private CoucatVO coucatVO;
	
	public CouponVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	public CoucatVO getCoucatVO() {
		return coucatVO;
	}

	public void setCoucatVO(CoucatVO coucatVO) {
		this.coucatVO = coucatVO;
	}

	public String getCoup_Sn() {
		return coup_Sn;
	}
	public void setCoup_Sn(String coup_Sn) {
		this.coup_Sn = coup_Sn;
	}
	public String getCoucat_No() {
		return coucat_No;
	}
	public void setCoucat_No(String coucat_No) {
		this.coucat_No = coucat_No;
	}
	public String getCoup_Status() {
		return coup_Status;
	}
	public void setCoup_Status(String coup_Status) {
		this.coup_Status = coup_Status;
	}
	

}
