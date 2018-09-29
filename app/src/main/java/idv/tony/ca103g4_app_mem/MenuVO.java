package idv.tony.ca103g4_app_mem;

public class MenuVO implements java.io.Serializable{

    private String Menu_No;
    private String Menu_Id;
    private String Menu_Type;
    private Integer Menu_Price;
    private String Menu_Intro;
    private Integer Menu_Status;


    public MenuVO() {
        super();
    }

    public MenuVO(String Menu_No, String Menu_Id, String Menu_Type, Integer Menu_Price, String Menu_Intro, Integer Menu_Status) {
        super();
        this.Menu_No = Menu_No;
        this.Menu_Id = Menu_Id;
        this.Menu_Type = Menu_Type;
        this.Menu_Price = Menu_Price;
        this.Menu_Intro = Menu_Intro;
        this.Menu_Status = Menu_Status;
    }

    public String getMenu_No() {
        return Menu_No;
    }

    public void setMenu_No(String menu_No) {
        Menu_No = menu_No;
    }

    public String getMenu_Id() {
        return Menu_Id;
    }

    public void setMenu_Id(String menu_Id) {
        Menu_Id = menu_Id;
    }

    public String getMenu_Type() {
        return Menu_Type;
    }

    public void setMenu_Type(String menu_Type) {
        Menu_Type = menu_Type;
    }

    public Integer getMenu_Price() {
        return Menu_Price;
    }

    public void setMenu_Price(Integer menu_Price) {
        Menu_Price = menu_Price;
    }

    public String getMenu_Intro() {
        return Menu_Intro;
    }

    public void setMenu_Intro(String menu_Intro) {
        Menu_Intro = menu_Intro;
    }

    public Integer getMenu_Status() {
        return Menu_Status;
    }

    public void setMenu_Status(Integer menu_Status) {
        Menu_Status = menu_Status;
    }




}


