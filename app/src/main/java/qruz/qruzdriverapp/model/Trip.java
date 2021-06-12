package qruz.qruzdriverapp.model;

public class Trip {

    public String name , start_at , end_at , no_od_seats ;

    public Trip(String name, String start_at, String end_at, String no_od_seats) {
        this.name = name;
        this.start_at = start_at;
        this.end_at = end_at;
        this.no_od_seats = no_od_seats;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getEnd_at() {
        return end_at;
    }

    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }

    public String getNo_od_seats() {
        return no_od_seats;
    }

    public void setNo_od_seats(String no_od_seats) {
        this.no_od_seats = no_od_seats;
    }
}
