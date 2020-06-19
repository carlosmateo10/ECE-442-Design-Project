package es.upm.dit.ece442designproject;

import java.util.ArrayList;

public class Group {

    private String id;
    private int count;
    private ArrayList<String> ssMACs;

    public Group() {}

    public Group(String id, int count, ArrayList<String> ssMACs) {
        this.id = id;
        this.count = count;
        this.ssMACs = ssMACs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<String> getSsMACs() {
        return ssMACs;
    }

    public void setSsMACs(ArrayList<String> ssMACs) {
        this.ssMACs = ssMACs;
    }
}
