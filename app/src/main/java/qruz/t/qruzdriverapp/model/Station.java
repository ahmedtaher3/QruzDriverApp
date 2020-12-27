package qruz.t.qruzdriverapp.model;

import java.util.List;

public class Station {

    private String id;

    private String name;

    private String latitude;

    private String longitude;

    private String date;

    private int usersNumber;

    public Station(String id, String name, String latitude, String longitude, String date, int usersNumber) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.usersNumber = usersNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUsersNumber() {
        return usersNumber;
    }

    public void setUsersNumber(int usersNumber) {
        this.usersNumber = usersNumber;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getUsers() {
        return usersNumber;
    }

    public void setUsers(int usersNumber) {
        this.usersNumber = usersNumber;
    }

}
