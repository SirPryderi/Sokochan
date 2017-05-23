package tests;

import org.junit.Test;
import sokochan.MapLoader;
import sokochan.SokochanEngine;

import java.io.File;
import java.nio.file.NoSuchFileException;

/**
 * Tests for {@link MapLoader}
 * Created by Vittorio on 04/12/2016.
 */
@SuppressWarnings("JavaDoc")
public class MalformedFileTest {

    @Test(expected = NoSuchFileException.class)
    public void absentMap() throws Exception {
        new SokochanEngine(new File("tests/testlevels/nonexistent.skb"));
    }

    @Test(expected = MapLoader.MapLoaderException.class)
    public void malformedLevel1() throws Exception {
        SokochanEngine engine = new SokochanEngine(new File("tests/testlevels/test4.skb"));
        System.out.println(engine.getMapName());
    }

    @Test(expected = MapLoader.MapLoaderException.class)
    public void malformedLevel2() throws Exception {
        SokochanEngine engine = new SokochanEngine(new File("tests/testlevels/test5.skb"));
    }

    @Test(expected = MapLoader.MapLoaderException.class)
    public void malformedLevel3() throws Exception {
        SokochanEngine engine = new SokochanEngine(new File("tests/testlevels/test6.skb"));
    }

    @Test(expected = MapLoader.MapLoaderException.class)
    public void malformedLevel4() throws Exception {
        SokochanEngine engine = new SokochanEngine(new File("tests/testlevels/test7.skb"));
    }

}