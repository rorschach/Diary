package me.rorschach.diary;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testAddDiary() throws Exception {

        for(int i = 0; i < 10; i++){
            Diary diary = new Diary();
        }
    }
}