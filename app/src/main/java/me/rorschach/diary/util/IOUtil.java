package me.rorschach.diary.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;

/**
 * 文件操作工具类，封装了文件的输入输出操作
 */
public class IOUtil {

    //备份文件夹的名称
    public static final String DIR_PATH = "Diary";

    //备份文件的名称
    public static final String FILE_NAME = "backup.xml";

    /**
     * 判断是否存在外置存储
     *
     * @return
     */
    private static boolean ExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 备份所有的便签到备份文件中
     *
     * @return 备份是否成功
     */
    @DebugLog
    public static boolean backupDiaries() {
        //若存在外置存储，则开始备份，否则直接返回
        if (ExternalStorageMounted()) {

            //获得备份文件夹的路径
            File filePath = new File(
                    Environment.getExternalStorageDirectory().getPath()
                            + File.separator + DIR_PATH);

            //若不存在备份文件夹，则创建该文件夹
            if (!filePath.exists()) {
                if (!filePath.mkdir()) {
                    return false;
                }
            }

            //输出流，用于写数据到文件中
            OutputStream os = null;

            try {

                File file = new File(Environment.getExternalStorageDirectory().getPath()
                        + File.separator + DIR_PATH + File.separator + FILE_NAME);

                if (file.exists()) {
                    FileWriter fw = new FileWriter(file);
                    fw.write("");
                    fw.close();
                }else {
                    if (!file.createNewFile()) {
                        return false;
                    }
                }


                //获得备份文件对应的输出流
                os = new FileOutputStream(file);

                //从数据库中加载所有的便签数据
                List<Diary> diaries = DbUtil.loadAllDiaries();

                //获得XML文件生成器
                XmlSerializer xs = Xml.newSerializer();

                //开始写入数据，将每一条便签的数据写入文件
                xs.setOutput(os, "utf-8");

                xs.startDocument("utf-8", true);
                xs.startTag(null, "diaries");

                for (Diary diary : diaries) {
                    xs.startTag(null, "diary");

                    xs.startTag(null, "id");
                    xs.text(diary.getId() + "");
                    xs.endTag(null, "id");

                    xs.startTag(null, "title\n");
                    xs.text(diary.getTitle());
                    xs.endTag(null, "title\n");

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

            } catch (IOException e) {   //若抛出这个异常，打印到控制台
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {   //若输出流没有关闭，则关闭输出流
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.d("TAG", "backupDiaries: " + "ok");

            return true;
        } else {
            return false;
        }
    }

    /**
     * 导入示例用的三篇便签
     *
     * @param context 上下文
     * @return 从文件解析得到的便签集合
     */
    public static List<Diary> importSamples(Context context) {

        //获得资源中的xml文件对应的XML解析器，用于解析xml文件
        XmlPullParser xpp = context.getResources().getXml(R.xml.example);
        return parseXml(xpp);
    }

    /**
     * 还原备份文件
     *
     * @return 还原得到的便签集合
     */
    @DebugLog
    public static List<Diary> importBackup() {

        //若存在外置存储，则开始备份，否则直接返回
        if (ExternalStorageMounted()) {

            //获得xml解析器的实例
            XmlPullParser xpp = Xml.newPullParser();

            //输入流，用于从文件中获得数据
            InputStream is = null;

            try {

                //获得备份文件对应的输入流
                is = new FileInputStream(Environment.getExternalStorageDirectory().getPath()
                        + File.separator + DIR_PATH + File.separator + FILE_NAME);
                xpp.setInput(is, "utf-8");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {   //若输入流没有关闭，则关闭输出流
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return parseXml(xpp);       //返回解析的结果
        } else {
            return null;
        }
    }

    /**
     * 解析Xml文件，得到便签列表
     *
     * @param xpp Xml解析器，用于解析Xml文件
     * @return 便签列表
     */
    @DebugLog
    public static List<Diary> parseXml(XmlPullParser xpp) {

        //若传近来的xml解析器为空，则抛出异常
        if (xpp == null) {
            throw new NullPointerException();
        } else {

            List<Diary> diaries = null;
            try {

                //pull解析的流程
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
