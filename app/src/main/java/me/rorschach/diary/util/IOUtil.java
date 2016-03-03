package me.rorschach.diary.util;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;

/**
 * Created by lei on 16-1-21.
 */
public class IOUtil {

    public static final String DIR_PATH = "/DiaryBackup";
    public static final String FILE_NAME = "/backup.xml";

    private static boolean ExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean backupDiaries() {
        if (ExternalStorageMounted()) {

            File filePath = new File(
                    Environment.getExternalStorageDirectory().getPath() + DIR_PATH);

            if (!filePath.exists()) {
                filePath.mkdir();
            }

            OutputStream os = null;

            try {

                os = new FileOutputStream(filePath + FILE_NAME);

                List<Diary> diaries = DbUtil.loadAllDiaries();

                XmlSerializer xs = Xml.newSerializer();

                xs.setOutput(os, "utf-8");

                xs.startDocument("utf-8", true);
                xs.startTag(null, "diaries");

                for (Diary diary : diaries) {
                    xs.startTag(null, "diary");

                    xs.startTag(null, "id");
                    xs.text(diary.getId() + "");
                    xs.endTag(null, "id");

                    xs.startTag(null, "title");
                    xs.text(diary.getTitle());
                    xs.endTag(null, "title");

                    xs.startTag(null, "body");
                    xs.text(diary.getBody());
                    xs.endTag(null, "body");

                    xs.startTag(null, "end");
                    xs.text(diary.getEnd());
                    xs.endTag(null, "end");

                    xs.startTag(null, "year");
                    xs.text(diary.getYear() + "");
                    xs.endTag(null, "year");

                    xs.startTag(null, "month");
                    xs.text(diary.getMonth() + "");
                    xs.endTag(null, "month");

                    xs.startTag(null, "day");
                    xs.text(diary.getDay() + "");
                    xs.endTag(null, "day");

                    xs.endTag(null, "diary");
                }

                xs.endTag(null, "diaries");
                xs.endDocument();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static List<Diary> importSamples(Context context) {
        XmlPullParser xpp = context.getResources().getXml(R.xml.example);
        return parseXml(xpp);
    }

    public static List<Diary> importBackup() {

        if (ExternalStorageMounted()) {
            XmlPullParser xpp = Xml.newPullParser();

            InputStream is = null;

            try {
                is = new FileInputStream(Environment.getExternalStorageDirectory().getPath()
                        + DIR_PATH + FILE_NAME);
                xpp.setInput(is, "utf-8");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return parseXml(xpp);
        } else {
            return null;
        }
    }

    @DebugLog
    public static List<Diary> parseXml(XmlPullParser xpp) {

        if (xpp == null) {
            throw new NullPointerException();
        } else {

            List<Diary> diaries = null;
            try {

                int eventType = xpp.getEventType();
                diaries = new ArrayList<>();
                Diary diary = null;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG:
                            if ("diary".equals(xpp.getName())) {
                                diary = new Diary();
                            } else if ("id".equals(xpp.getName())) {
                                diary.setId(Integer.parseInt(xpp.nextText()));
                            } else if ("title".equals(xpp.getName())) {
                                diary.setTitle(xpp.nextText());
                            } else if ("body".equals(xpp.getName())) {
                                diary.setBody(xpp.nextText());
                            } else if ("end".equals(xpp.getName())) {
                                diary.setEnd(xpp.nextText());
                            } else if ("year".equals(xpp.getName())) {
                                diary.setYear(Integer.parseInt(xpp.nextText()));
                            } else if ("month".equals(xpp.getName())) {
                                diary.setMonth(Integer.parseInt(xpp.nextText()));
                            } else if ("day".equals(xpp.getName())) {
                                diary.setDay(Integer.parseInt(xpp.nextText()));
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if ("diary".equals(xpp.getName())) {
                                diaries.add(diary);
                                diary = null;
                            }
                            break;
                    }
                    eventType = xpp.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return diaries;
        }
    }

}
