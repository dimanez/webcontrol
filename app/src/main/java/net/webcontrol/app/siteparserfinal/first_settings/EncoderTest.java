package net.webcontrol.app.siteparserfinal.first_settings;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by Knyazev D.A. on 19.05.2016.
 */
public class EncoderTest {
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void main(String[] args) throws UnsupportedEncodingException {
        String rg = URLEncoder.encode("what to visit", java.nio.charset.StandardCharsets.UTF_8.toString());
        System.out.println(rg);
        String en = URLEncoder.encode("what to visit", Charset.forName("ISO_8859_1").toString());
        System.out.println(en);
        String ruLang = URLEncoder.encode("Привет мир");
        System.out.println(ruLang);
        String chLang = URLEncoder.encode("你就知道");
        System.out.println(chLang);
    }
}
