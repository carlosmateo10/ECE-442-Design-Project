package es.upm.dit.ece442designproject;

public class Hub {

    private String id, code;
    private int count;

    public Hub() {}

    public Hub(String id, String code, int count) {
        this.id = id;
        this.code = code;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
