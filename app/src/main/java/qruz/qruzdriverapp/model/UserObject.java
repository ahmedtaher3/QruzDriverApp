package qruz.qruzdriverapp.model;

public class UserObject {

    String id ; ;
boolean is_picked_up ;

    public UserObject(String id, boolean is_picked_up) {
        this.id = id;
        this.is_picked_up = is_picked_up;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIs_picked_up() {
        return is_picked_up;
    }

    public void setIs_picked_up(boolean is_picked_up) {
        this.is_picked_up = is_picked_up;
    }

    @Override
    public String toString() {
        return "UserObject{" +
                "id='" + id + '\'' +
                ", is_picked_up='" + is_picked_up + '\'' +
                '}';
    }
}
