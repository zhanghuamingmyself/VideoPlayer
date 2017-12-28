package com.zhanghuaming.videoplayer;

import com.google.gson.Gson;
import com.zhanghuaming.videoplayer.bean.VideoURLBean;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testJson(){
        Gson gson = new Gson();
        VideoURLBean b1 = new VideoURLBean();
        b1.owner="zhm";
        b1.name="1.mp4";
        b1.date = null;
        VideoURLBean b2 = new VideoURLBean();
        b2.owner="zhm";
        b2.name="2.mp4";
        b2.date = null;
        VideoURLBean b3 = new VideoURLBean();
        b3.owner="zhm";
        b3.name="3.mp4";
        b3.date = null;
        VideoURLBean b4 = new VideoURLBean();
        b4.owner="zhm";
        b4.name="ghh.mp4";
        b4.date = null;
        List<VideoURLBean> list = new ArrayList<>();
        list.add(b1);
        list.add(b2);
        list.add(b3);
        list.add(b4);
        String s=gson.toJson(list);
        System.out.print(s);

    }
}