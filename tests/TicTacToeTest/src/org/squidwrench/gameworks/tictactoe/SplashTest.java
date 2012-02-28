package org.squidwrench.gameworks.tictactoe;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class org.squidwrench.gameworks.tictactoe.SplashTest \
 * org.squidwrench.gameworks.tictactoe.tests/android.test.InstrumentationTestRunner
 */
public class SplashTest extends ActivityInstrumentationTestCase2<Splash> {

    public SplashTest() {
        super("org.squidwrench.gameworks.tictactoe", Splash.class);
    }

}
