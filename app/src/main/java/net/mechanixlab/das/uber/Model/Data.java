package net.mechanixlab.das.uber.Model;

/**
 * Created by User on 12/15/2017.
 */

public class Data {

    public String tittle;
    public String detail;


    public Data() {

    }

    public Data(String tittle, String detail) {
        this.tittle = tittle;
        this.detail = detail;
    }


    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
