package qruz.t.qruzdriverapp.model;

public class DriverTrips {
    private String date;
    private String dayName;
    private Boolean flag;
    private String id;
    private Boolean isReturn;
    private String name;
    private Partner partner;
    private String startsAt;
    private String userCount;

    public DriverTrips(String str, String str2, String str3, String str4, String str5, Boolean bool, Partner partner2, String str6, Boolean bool2) {
        this.id = str;
        this.name = str2;
        this.dayName = str3;
        this.date = str4;
        this.startsAt = str5;
        this.flag = bool;
        this.partner = partner2;
        this.userCount = str6;
        this.isReturn = bool2;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getDayName() {
        return this.dayName;
    }

    public void setDayName(String str) {
        this.dayName = str;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String str) {
        this.date = str;
    }

    public String getStartsAt() {
        return this.startsAt;
    }

    public void setStartsAt(String str) {
        this.startsAt = str;
    }

    public Boolean getFlag() {
        return this.flag;
    }

    public void setFlag(Boolean bool) {
        this.flag = bool;
    }

    public Partner getPartner() {
        return this.partner;
    }

    public void setPartner(Partner partner2) {
        this.partner = partner2;
    }

    public String getUserCount() {
        return this.userCount;
    }

    public void setUserCount(String str) {
        this.userCount = str;
    }

    public Boolean getIsReturn() {
        return this.isReturn;
    }

    public void setIsReturn(Boolean bool) {
        this.isReturn = bool;
    }
}
