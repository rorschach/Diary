package me.rorschach.diary.utils;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.diary.bean.Diary;

/**
 * Created by lei on 16-1-21.
 */
public class XmlUtils {

    @DebugLog
    public static void serializerXml(Context context) throws Exception {
        XmlSerializer xs = Xml.newSerializer();
        xs.setOutput(context.openFileOutput("diaries.xml", Context.MODE_PRIVATE), "utf-8");

        xs.startDocument("utf-8", true);
        xs.startTag(null, "diaries");

        for(int i = 1; i <= 5; i++){
            xs.startTag(null, "diary");

            xs.startTag(null, "id");
            xs.text(1+"");
            xs.endTag(null, "id");

            xs.startTag(null, "title");
            xs.text("title" + i);
            xs.endTag(null, "title");

            xs.startTag(null, "body");
            xs.text("body" + i);
            xs.endTag(null, "body");

            xs.startTag(null, "date");
            xs.text("date" + i);
            xs.endTag(null, "date");

            xs.startTag(null, "end");
            xs.text("end" + i);
            xs.endTag(null, "end");

            xs.startTag(null, "year");
            xs.text("2016");
            xs.endTag(null, "year");

            xs.startTag(null, "month");
            xs.text("1");
            xs.endTag(null, "month");

            xs.startTag(null, "day");
            xs.text("21");
            xs.endTag(null, "day");

            xs.endTag(null, "diary");
        }

        xs.endTag(null, "diaries");
        xs.endDocument();
    }

    @DebugLog
    public static List<Diary> parserXml (Context context) throws Exception {

        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(context.openFileInput("diaries.xml"), "utf-8");

        int eventType = xpp.getEventType();
        List<Diary> diaries = new ArrayList<>();
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

        return diaries;
    }

}
