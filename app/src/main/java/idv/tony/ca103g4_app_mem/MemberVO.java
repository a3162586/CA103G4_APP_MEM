package idv.tony.ca103g4_app_mem;

import java.sql.Date;

public class MemberVO {
    private String mem_No;
    private String mem_Id;
    private String mem_Pw;
    private String mem_Name;
    private String mem_Gender;
    private Date mem_Bir;
    private String mem_Mail;
    private String mem_Phone;
    private String mem_Receiver;
    private String mem_Repno;
    private String mem_Recounty;
    private String mem_Retown;
    private String mem_Readdr;
    private String mem_Cardnum;
    private String mem_Carddue;
    private Integer mem_Bonus;
    private Date  mem_Credate;
    private String mem_Status;
    private boolean isMem;
//    private byte[] mem_Photo;




    public MemberVO(String mem_No, String mem_Id, String mem_Pw, String mem_Name, String mem_Gender, Date mem_Bir,
                    String mem_Mail, String mem_Phone, String mem_Receiver, String mem_Repno, String mem_Recounty,
                    String mem_Retown, String mem_Readdr, String mem_Cardnum, String mem_Carddue, Integer mem_Bonus,
                    Date mem_Credate, String mem_Status) {
        super();
        this.mem_No = mem_No;
        this.mem_Id = mem_Id;
        this.mem_Pw = mem_Pw;
        this.mem_Name = mem_Name;
        this.mem_Gender = mem_Gender;
        this.mem_Bir = mem_Bir;
        this.mem_Mail = mem_Mail;
        this.mem_Phone = mem_Phone;
        this.mem_Receiver = mem_Receiver;
        this.mem_Repno = mem_Repno;
        this.mem_Recounty = mem_Recounty;
        this.mem_Retown = mem_Retown;
        this.mem_Readdr = mem_Readdr;
        this.mem_Cardnum = mem_Cardnum;
        this.mem_Carddue = mem_Carddue;
        this.mem_Bonus = mem_Bonus;
        this.mem_Credate = mem_Credate;
        this.mem_Status = mem_Status;
//        this.mem_Photo = mem_Photo;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MemberVO) {
            MemberVO membervo = (MemberVO)obj;
            return this.mem_No.equals(membervo.mem_No);
        }
        return super.equals(obj);
    }


    public MemberVO() {
        super();
    }

    public boolean isMem() {
        return isMem;
    }

    public void setMem(boolean mem) {
        isMem = mem;
    }

    public String getMem_No() {
        return mem_No;
    }
    public void setMem_No(String mem_No) {
        this.mem_No = mem_No;
    }
    public String getMem_Id() {
        return mem_Id;
    }
    public void setMem_Id(String mem_Id) {
        this.mem_Id = mem_Id;
    }
    public String getMem_Pw() {
        return mem_Pw;
    }
    public void setMem_Pw(String mem_Pw) {
        this.mem_Pw = mem_Pw;
    }
    public String getMem_Name() {
        return mem_Name;
    }
    public void setMem_Name(String mem_Name) {
        this.mem_Name = mem_Name;
    }
    public String getMem_Gender() {
        return mem_Gender;
    }
    public void setMem_Gender(String mem_Gender) {
        this.mem_Gender = mem_Gender;
    }
    public Date getMem_Bir() {
        return mem_Bir;
    }
    public void setMem_Bir(Date mem_Bir) {
        this.mem_Bir = mem_Bir;
    }
    public String getMem_Mail() {
        return mem_Mail;
    }
    public void setMem_Mail(String mem_Mail) {
        this.mem_Mail = mem_Mail;
    }
    public String getMem_Phone() {
        return mem_Phone;
    }
    public void setMem_Phone(String mem_Phone) {
        this.mem_Phone = mem_Phone;
    }
    public String getMem_Receiver() {
        return mem_Receiver;
    }
    public void setMem_Receiver(String mem_Receiver) {
        this.mem_Receiver = mem_Receiver;
    }
    public String getMem_Repno() {
        return mem_Repno;
    }
    public void setMem_Repno(String mem_Repno) {
        this.mem_Repno = mem_Repno;
    }
    public String getMem_Recounty() {
        return mem_Recounty;
    }
    public void setMem_Recounty(String mem_Recounty) {
        this.mem_Recounty = mem_Recounty;
    }
    public String getMem_Retown() {
        return mem_Retown;
    }
    public void setMem_Retown(String mem_Retown) {
        this.mem_Retown = mem_Retown;
    }
    public String getMem_Readdr() {
        return mem_Readdr;
    }
    public void setMem_Readdr(String mem_Readdr) {
        this.mem_Readdr = mem_Readdr;
    }
    public String getMem_Cardnum() {
        return mem_Cardnum;
    }
    public void setMem_Cardnum(String mem_Cardnum) {
        this.mem_Cardnum = mem_Cardnum;
    }
    public String getMem_Carddue() {
        return mem_Carddue;
    }
    public void setMem_Carddue(String mem_Carddue) {
        this.mem_Carddue = mem_Carddue;
    }
    public Integer getMem_Bonus() {
        return mem_Bonus;
    }
    public void setMem_Bonus(Integer mem_Bonus) {
        this.mem_Bonus = mem_Bonus;
    }
    public Date getMem_Credate() {
        return mem_Credate;
    }
    public void setMem_Credate(Date mem_Credate) {
        this.mem_Credate = mem_Credate;
    }
    public String getMem_Status() {
        return mem_Status;
    }
    public void setMem_Status(String mem_Status) {
        this.mem_Status = mem_Status;
    }
//    public byte[] getMem_Photo() {
//        return mem_Photo;
//    }
//    public void setMem_Photo(byte[] mem_Photo) {
//        this.mem_Photo = mem_Photo;
//    }


}

