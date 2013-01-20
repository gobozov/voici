package com.speechpro.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 20.01.13
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class ResponseResult implements Parcelable {


    private String key;
    private Status status;

    public ResponseResult() {
    }

    public ResponseResult(Status status, String key) {
        this.status = status;
        this.key = key;
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

    @Override
    public String toString() {
        return "ResponseResult{" +
                "key='" + key + '\'' +
                ", status=" + status +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.d("speechpro", "writeToParcel");
        parcel.writeString(key);
        parcel.writeSerializable(status);

    }

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
