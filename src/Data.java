public class Data {

    private int id;
    private int number;
    private String time;
    private boolean send;

    public Data() {
    }

    public Data(int id, int number, String time, boolean send) {
        this.id = getId();
        this.number = number;
        this.time = time;
        this.send = send;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
