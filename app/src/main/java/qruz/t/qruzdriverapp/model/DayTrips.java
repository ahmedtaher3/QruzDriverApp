package qruz.t.qruzdriverapp.model;

public class DayTrips {

    private String fullName;
    private String name;
    private boolean selected;
    private boolean today;

    public DayTrips(String fullName, String name, boolean selected, boolean today) {
        this.fullName = fullName;
        this.name = name;
        this.selected = selected;
        this.today = today;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isToday() {
        return today;
    }

    public void setToday(boolean today) {
        this.today = today;
    }
}
