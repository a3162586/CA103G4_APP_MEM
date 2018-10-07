package idv.tony.ca103g4_app_mem;

import java.sql.Timestamp;


public class ResVO implements java.io.Serializable{
	public ResVO() {
		
	}
	private String     res_no;    	
	private String     mem_no;    	  
	private String     dek_no; 	
	private Timestamp  res_submit;	
	private Timestamp  res_timebg;
	private Timestamp  res_timefn;    	
	private Integer    res_people;    	  
	private Integer    res_status;
	/**
	 * @return the res_no
	 */
	public String getRes_no() {
		return res_no;
	}
	/**
	 * @param res_no the res_no to set
	 */
	public void setRes_no(String res_no) {
		this.res_no = res_no;
	}
	/**
	 * @return the mem_no
	 */
	public String getMem_no() {
		return mem_no;
	}
	/**
	 * @param mem_no the mem_no to set
	 */
	public void setMem_no(String mem_no) {
		this.mem_no = mem_no;
	}
	/**
	 * @return the dek_no
	 */
	public String getDek_no() {
		return dek_no;
	}
	/**
	 * @param dek_no the dek_no to set
	 */
	public void setDek_no(String dek_no) {
		this.dek_no = dek_no;
	}
	/**
	 * @return the res_submit
	 */
	public Timestamp getRes_submit() {
		return res_submit;
	}
	/**
	 * @param res_submit the res_submit to set
	 */
	public void setRes_submit(Timestamp res_submit) {
		this.res_submit = res_submit;
	}
	/**
	 * @return the res_timebg
	 */
	public Timestamp getRes_timebg() {
		return res_timebg;
	}
	/**
	 * @param res_timebg the res_timebg to set
	 */
	public void setRes_timebg(Timestamp res_timebg) {
		this.res_timebg = res_timebg;
	}
	/**
	 * @return the res_timefn
	 */
	public Timestamp getRes_timefn() {
		return res_timefn;
	}
	/**
	 * @param res_timefn the res_timefn to set
	 */
	public void setRes_timefn(Timestamp res_timefn) {
		this.res_timefn = res_timefn;
	}
	/**
	 * @return the res_people
	 */
	public Integer getRes_people() {
		return res_people;
	}
	/**
	 * @param res_people the res_people to set
	 */
	public void setRes_people(Integer res_people) {
		this.res_people = res_people;
	}
	/**
	 * @return the res_status
	 */
	public Integer getRes_status() {
		return res_status;
	}
	/**
	 * @param res_status the res_status to set
	 */
	public void setRes_status(Integer res_status) {
		this.res_status = res_status;
	}
	
	
	
}
