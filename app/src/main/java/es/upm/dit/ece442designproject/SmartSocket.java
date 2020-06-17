package es.upm.dit.ece442designproject;

public class SmartSocket {

    private String MAC, Id, PIN;
    private int mode, threshold, power;
    private boolean up_down, ext_trig, power_safety, state, manual_block;

    public SmartSocket() {}

    public SmartSocket(String MAC, String id, int mode, int threshold, int power, boolean up_down, boolean ext_trig, boolean power_safety, boolean state, boolean manual_block, String PIN) {
        this.MAC = MAC;
        this.Id = id;
        this.mode = mode;
        this.threshold = threshold;
        this.power = power;
        this.up_down = up_down;
        this.ext_trig = ext_trig;
        this.power_safety = power_safety;
        this.state = state;
        this.manual_block = manual_block;
        this.PIN = PIN;
    }

    public String getPIN() { return PIN; }

    public void setPIN(String PIN) { this.PIN = PIN; }

    public boolean isManual_block() { return manual_block; }

    public void setManual_block(boolean manual_block) { this.manual_block = manual_block; }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public boolean isPower_safety() {
        return power_safety;
    }

    public void setPower_safety(boolean power_safety) {
        this.power_safety = power_safety;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isUp_down() {
        return up_down;
    }

    public void setUp_down(boolean up_down) {
        this.up_down = up_down;
    }

    public boolean isExt_trig() {
        return ext_trig;
    }

    public void setExt_trig(boolean ext_trig) {
        this.ext_trig = ext_trig;
    }
}
