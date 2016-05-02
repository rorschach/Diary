package me.rorschach.diary.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 实体类，每一个对象都是一篇便签
 */
@Table(database = DiaryDb.class)        //数据库信息由DiaryDb这个类定义
public class Diary extends BaseModel implements Parcelable {

    @Column                             //列
    @PrimaryKey(autoincrement = true)   //设置为主键并且自增长
    private long id;                    //便签的id

    @Column
    private String title;               //标题

    @Column
    private String body;                //正文

    @Column
    private String end;                 //结尾

    @Column
    private int year;

    @Column
    private int month;

    @Column
    private int day;

    //提供一个空的构造方法
    public Diary() {
    }

    //带参数的构造方法，因为id自增长，因此不需要设置id
    public Diary(String title, String body, String end, int year, int month, int day) {
        this.title = title;
        this.body = body;
        this.end = end;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    //getter setter方法，用于设置和获取便签的信息

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "Diary[" + "id - " + id
                + ", title - " + title + ", body - " + body + ", end - " + end
                + ", year - " + year + ", month - " + month + ", day - " + day + "]\n";
    }

    //后面的不需要了解

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeString(this.end);
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
    }

    protected Diary(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.body = in.readString();
        this.end = in.readString();
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
    }

    public static final Parcelable.Creator<Diary> CREATOR = new Parcelable.Creator<Diary>() {
        public Diary createFromParcel(Parcel source) {
            return new Diary(source);
        }

        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };
}
