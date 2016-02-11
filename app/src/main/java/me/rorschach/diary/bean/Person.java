package me.rorschach.diary.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by lei on 16-2-1.
 */
@Table(database = PersonDb.class)
public class Person implements Parcelable {

    @Column
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private String name;

    @Column
    private String tel;

    @Column
    private String college;

    @Column
    private boolean stared;

    public Person() {

    }

    public Person(String name, String tel, String college, boolean stared) {
        this.name = name;
        this.tel = tel;
        this.college = college;
        this.stared = stared;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public boolean isStared() {
        return stared;
    }

    public void setStared(boolean stared) {
        this.stared = stared;
    }

    @Override
    public String toString() {
        return "id " + id
                + ", name " + name
                + ", tel " + tel
                + ", college " + college
                + ", stared " + stared;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.tel);
        dest.writeString(this.college);
        dest.writeByte(stared ? (byte) 1 : (byte) 0);
    }

    protected Person(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.tel = in.readString();
        this.college = in.readString();
        this.stared = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public void call() {

    }
}
