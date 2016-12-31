package nl.frankkie.androidthings.ht16k33;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.ht16k33.Ht16k33;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import nl.frankkie.androidthings.ht16k33.textscroller.TextScroller;

/**
 * Created by FrankkieNL on 12/31/2016.
 */

public class MainActivity extends Activity {

    public static final String TAG = "HT16K33 Test";
    private Ht16k33 mDisplay;
    TextScroller mTextScroller;
    private Handler mHandler = new Handler();
    long textScrollerDelay = 250; //ms
    Runnable runTextScrollerStep = new Runnable() {
        @Override
        public void run() {
            mTextScroller.step();
            try {
                if (mDisplay instanceof MyAlphanumericDisplay) {
                    ((MyAlphanumericDisplay) mDisplay).display(mTextScroller.getText());
                }
                if (mDisplay instanceof AlphanumericDisplay) {
                    ((AlphanumericDisplay) mDisplay).display(mTextScroller.getText());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(runTextScrollerStep, textScrollerDelay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startOriginalDriver();
        //startFixedDriver();

        startTextScroller();
    }

    public void startOriginalDriver() {
        try {
            mDisplay = new AlphanumericDisplay(BoardDefaults.getI2cBus());
            mDisplay.setEnabled(true);
            ((AlphanumericDisplay) mDisplay).clear();
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
            mDisplay = null;
        }
    }

    public void startFixedDriver() {
        try {
            mDisplay = new MyAlphanumericDisplay(BoardDefaults.getI2cBus());
            mDisplay.setEnabled(true);
            ((MyAlphanumericDisplay) mDisplay).clear();
            Log.d(TAG, "Initialized I2C Display");
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
            mDisplay = null;
        }
    }

    public void startTextScroller() {
        mTextScroller = new TextScroller("A DOT BETWEEN EVERY NUMBER 1.2.3.4.5.6.7.8.9.0 ** IP " + getIPAddress() + " **");
        mHandler.post(runTextScrollerStep);
    }

    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        return sAddr;
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }
}
