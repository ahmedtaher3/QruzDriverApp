package qruz.t.qruzdriverapp.model;

public class DayTrips {

    private String fullName;
    private String name;
    private boolean selected;
    private boolean today;
    private boolean visible;

    public DayTrips(String fullName, String name, boolean selected, boolean today, boolean visible) {
        this.fullName = fullName;
        this.name = name;
        this.selected = selected;
        this.today = today;
        this.visible = visible;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
