package idv.tony.ca103g4_app_mem;

public class DeskVO implements java.io.Serializable{
    public DeskVO(){

    }
    private int TotalDesk;
    private String  dek_no;
    private String  branch_no;
    private String  dek_id;
    private Integer dek_set;
    private Integer dek_status;
    private int dek_img;

    public DeskVO(String dek_no, int dek_img, Integer dek_status) {
        this.dek_no = dek_no;
        this.dek_img = dek_img;
        this.dek_status = dek_status;
    }

    public int getDek_img() {
        return dek_img;
    }

    public void setDek_img(int dek_img) {
        this.dek_img = dek_img;
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
     * @return the branch_no
     */
    public String getBranch_no() {
        return branch_no;
    }
    /**
     * @param branch_no the branch_no to set
     */
    public void setBranch_no(String branch_no) {
        this.branch_no = branch_no;
    }
    /**
     * @return the dek_id
     */
    public String getDek_id() {
        return dek_id;
    }
    /**
     * @param dek_id the dek_id to set
     */
    public void setDek_id(String dek_id) {
        this.dek_id = dek_id;
    }
    /**
     * @return the dek_set
     */
    public Integer getDek_set() {
        return dek_set;
    }
    /**
     * @param dek_set the dek_set to set
     */
    public void setDek_set(Integer dek_set) {
        this.dek_set = dek_set;
    }
    /**
     * @return the dek_status
     */
    public Integer getDek_status() {
        return dek_status;
    }
    /**
     * @param dek_status the dek_status to set
     */
    public void setDek_status(Integer dek_status) {
        this.dek_status = dek_status;
    }
}
