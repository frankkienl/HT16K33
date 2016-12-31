package nl.frankkie.androidthings.ht16k33;

import android.text.TextUtils;

import com.google.android.things.contrib.driver.ht16k33.Ht16k33;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Created by FrankkieNL on 12/31/2016.
 */

public class MyAlphanumericDisplay extends Ht16k33 {
    private static final short DOT = (short) (1 << 14);
    private ByteBuffer mBuffer = ByteBuffer.allocate(8);

    /**
     * Create a new driver for a HT16K33 based alphanumeric display connected on the given I2C bus.
     * @param bus
     * @throws IOException
     */
    public MyAlphanumericDisplay(String bus) throws IOException {
        super(bus);
    }

    /**
     * Clear the display memory.
     */
    public void clear() throws IOException {
        for (int i = 0; i < 4; i++) {
            writeColumn(i, (short) 0);
        }
    }

    /**
     * Display a character at the given index.
     * @param index index of the segment display
     * @param c character value
     * @param dot state of the dot LED
     */
    public void display(char c, int index, boolean dot) throws IOException {
        int val = Font.DATA[c];
        if (dot) {
            val |= DOT;
        }
        writeColumn(index, (short) val);
    }

    /**
     * Display a decimal number.
     * @param n number value
     */
    public void display(double n) throws IOException {
        // pad with leading space until we get 5 chars
        // since double always get formatted with a dot
        // and the dot doesn't consume any space on the display.
        display(String.format("%5s", n));
    }

    /**
     * Display an integer number.
     * @param n number value
     */
    public void display(int n) throws IOException {
        // pad with leading space until we get 4 chars
        display(String.format("%4s", n));
    }

    /**
     * Display a string.
     * @param s string value
     */
    public void display(String s) throws IOException {
        if (TextUtils.isEmpty(s)) {
            clear();
            return;
        }

        mBuffer.clear();
        mBuffer.mark();
        short n = (short) 0;
        char prevChar = (char) 0;
        char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            // truncate string to the size of the display
            if (mBuffer.position() == mBuffer.limit()) {
                //if next character (that would not fit on the display) would be a dot.
                if (charArray[i]=='.' && prevChar != '.'){
                    //add dot LED flag to last character
                    n |= DOT;
                    mBuffer.reset();
                    mBuffer.putShort(n);
                }
                break;
            }
            if (c == '.') {
                if (prevChar == '.') {
                    mBuffer.putShort(DOT);
                } else {
                    // add dot LED flag to the previous character.
                    n |= DOT;
                    mBuffer.reset();
                    mBuffer.putShort(n);
                }
            } else {
                // extract character data from font.
                n = (short) Font.DATA[c];
                mBuffer.mark();
                mBuffer.putShort(n);
            }
            prevChar = c;
        }

        // clear the rest of the display
        while (mBuffer.position() < mBuffer.capacity()) {
            mBuffer.put((byte) 0);
        }

        mBuffer.flip();
        // write display memory.
        for (int i = 0; i < mBuffer.limit() / 2; i++) {
            writeColumn(i, mBuffer.getShort());
        }
    }


}
