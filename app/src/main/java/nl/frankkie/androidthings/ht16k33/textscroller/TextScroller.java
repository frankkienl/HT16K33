package nl.frankkie.androidthings.ht16k33.textscroller;

import android.util.Log;

/**
 * Created by FrankkieNL on 12/29/2016.
 */

public class TextScroller {

    private String text = "";
    private int step;
    private int dispLength;
    private String disp = "";

    public TextScroller(String text) {
        dispLength = 4;
        step = -1;
        disp = "";
        if (!text.endsWith(" ")) {
            text += " ";
        }
        this.text = prepareText(text);
    }

    /**
     * Prepare the String for use in the TextScroller.
     * if there are 2 '.'-s after eachother, place space in between.
     *
     * @param s
     * @return
     */
    public static String prepareText(String s) {
        int numChars = 0;
        String buffer = "";
        for (char c : s.toCharArray()) {
            if (c != '.') {
                numChars++;
            } else {
                if (buffer.endsWith(".")) {
                    buffer += " ";
                    numChars++;
                }
            }
            buffer += c;
        }
        return buffer;
    }

    public static int getBufferLength(String s) {
        int numChars = 0;
        for (char c : s.toCharArray()) {
            if (c != '.') {
                numChars++;
            }
        }
        return numChars;
    }

    public void step() {
        step++;
        if (step >= text.length()) {
            step = 0;
        }
        int begin = step;
        String temp = text.substring(begin, text.length());

        while (getBufferLength(temp) < dispLength) {
            temp += text;
        }
        //truncate
        String temp2 = "";
        char[] charArray = temp.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            temp2 += c;
            if (getBufferLength(temp2) >= dispLength) {
                if ((i + 1 < charArray.length) && charArray[i + 1] == '.') {
                    temp2 += ".";
                }
                break;
            }
        }
        disp = temp2;

        if (disp.startsWith(".")) {
            step();
        }
    }

    public void show() {
        Log.d("TextScroller", disp);
    }

    public String getText() {
        return disp;
    }
}
