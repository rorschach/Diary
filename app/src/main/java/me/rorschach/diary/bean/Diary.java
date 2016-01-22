package me.rorschach.diary.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by lei on 16-1-18.
 */
@Table(database = DiaryDb.class)
public class Diary extends BaseModel implements Parcelable {

    @Column
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private String title;

    @Column
    private String body;

    @Column
    private String end;

    @Column
    private int year;

    @Column
    private int month;

    @Column
    private int day;

    public Diary() {
    }

    public Diary(String title, String body, String end, int year, int month, int day) {
        this.title = title;
        this.body = body;
        this.end = end;
        this.year = year;
        this.month = month;
        this.day = day;
    }

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
