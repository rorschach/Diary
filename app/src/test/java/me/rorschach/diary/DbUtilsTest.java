package me.rorschach.diary;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by lei on 16-1-20.
 */
public class DbUtilsTest {

    List<Diary> mDiaries;

    @Before
    public void setUp() throws Exception {
        mDiaries = DbUtils.addDiaries();
    }

    @Test
    public void testAddDiaries() throws Exception {
        mDiaries = DbUtils.addDiaries();
        assertEquals(10, mDiaries.size());
    }

    @Test
    public void testInsertDiary() throws Exception {

    }

    @Test
    public void testDeleteDiary() throws Exception {

    }

    @Test
    public void testUpdateDiary() throws Exception {

    }

    @Test
    public void testQueryDiaryById() throws Exception {

    }

    @Test
    public void testQueryDiaryByTitle() throws Exception {

    }

    @Test
    public void testQueryDiaryByDate() throws Exception {

    }

    @Test
    public void testLoadAllTitles() throws Exception {
        List<String> titles = DbUtils.loadAllTitles();
        for(int i = 0; i < 10; i++){
            assertEquals("title - " + i, titles.get(i));
        }
    }
}