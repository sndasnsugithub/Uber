package net.mechanixlab.das.uber.Model;

/**
 * Created by User on 12/15/2017.
 */

public class Sender {


        public Data data ;
        public String to ;

    public Sender()
    {

    }

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
