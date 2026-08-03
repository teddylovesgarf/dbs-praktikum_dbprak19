package db_praktikum.entities_collection;

public class CDTitle {

    private Integer titleNumber;
    private String titleName;
    private MusikCD cd;   // referenz zur zugehörigen CD

    public CDTitle() {
    }

    public Integer getTitleNumber() {
        return titleNumber;
    }

    public void setTitleNumber(Integer titleNumber) {
        this.titleNumber = titleNumber;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public MusikCD getCd() {
        return cd;
    }

    public void setCd(MusikCD cd) {
        this.cd = cd;
    }
}