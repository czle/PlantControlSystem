package com.example.a20190418test;

public class DiaryDTO {

    static String ip = "192.168.0.10";

    String NO ;
    String DATESTAMP;
    String CONTENT;
    String SELECTDATE;

    public void setNO(String NO) {
        this.NO = NO;
    }

    public void setCONTENT(String CONTENT) {
        this.CONTENT = CONTENT;
    }

    public void setDATESTAMP(String DATESTAMP) {
        this.DATESTAMP = DATESTAMP;
    }

    public void setSELECTDATE(String SELECTDATE) {
        this.SELECTDATE = SELECTDATE;
    }

    public String getNO() {
        return NO;
    }

    public String getCONTENT() {
        return CONTENT;
    }

    public String getDATESTAMP() {
        return DATESTAMP;
    }



}
