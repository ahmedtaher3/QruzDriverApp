package qruz.t.qruzdriverapp.model;


public class StationUser {


    private String id;

    private String name;

    private String email;

    private String phone;

    private String avatar;
    private String station_type;

    private String secondary_no ;

    private boolean isPickedUp;




    public StationUser(String id, String name, String email, String phone, String avatar, boolean isPickedUp, String secondary_no, String station_type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.isPickedUp = isPickedUp;
        this.secondary_no = secondary_no;
        this.station_type = station_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean getIsPickedUp() {
        return isPickedUp;
    }

    public void setIsPickedUp(boolean isPickedUp) {
        this.isPickedUp = isPickedUp;
    }

    public String getSecondary_no() {
        return secondary_no;
    }

    public void setSecondary_no(String secondary_no) {
        this.secondary_no = secondary_no;
    }

    public String getStation_type() {
        return station_type;
    }

    public void setStation_type(String station_type) {
        this.station_type = station_type;
    }



    @Override
    public String toString() {
        return "StationUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", avatar='" + avatar + '\'' +
                ", isPickedUp=" + isPickedUp +
                ", secondary_no='" + secondary_no + '\'' +
                ", station_type='" + station_type + '\'' +
                '}';
    }
}
