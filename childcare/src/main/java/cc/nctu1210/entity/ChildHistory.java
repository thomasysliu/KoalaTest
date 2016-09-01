package cc.nctu1210.entity;

/**
 * Created by User on 2016/9/1.
 */
public class ChildHistory {
    private String times;
    private String place;


    public ChildHistory(String times, String place) {
        this.times = times;
        this.place = place;
    }

    public void setTimes(String times)
    {
        this.times = times;
    }

    public void setPlace(String place)
    {
        this.place = place;
    }

    public String getTimes() {
        return times;
    }

    public String getPlace() {
        return place;
    }
}