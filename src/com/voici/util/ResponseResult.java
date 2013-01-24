package com.voici.util;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 20.01.13
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class ResponseResult implements Serializable {


    private String key;
    private Status status;
    private String error;
    private String score;

    public ResponseResult() {
    }

    public ResponseResult(Status status, String key) {
        this.status = status;
        this.key = key;
    }


    public ResponseResult(Status status, String key, String score) {
        this.status = status;
        this.key = key;
        this.score = score;
    }

    public ResponseResult(String error, Status status) {
        this.status = status;
        this.error = error;
    }

    public ResponseResult(String error) {
        this.error = error;
    }


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "key='" + key + '\'' +
                ", status=" + status +
                ", error='" + error + '\'' +
                '}';
    }


    //
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        Log.d("voici", "writeToParcel");
//        parcel.writeString(key);
//        parcel.writeSerializable(status);
//
//    }

    public enum Status implements Serializable{

        OK("OK"), ERROR("ERROR"), INCONSISTENT_COMPOSITE("INCONSISTENT_COMPOSITE");

        private String name;

        private Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }
}
