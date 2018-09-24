package net.webcontrol.app.siteparserfinal;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import net.webcontrol.app.siteparserfinal.ads_control_and_menu.AboutApp;
import net.webcontrol.app.siteparserfinal.ads_control_and_menu.ActivatedFull;
import net.webcontrol.app.siteparserfinal.ads_control_and_menu.SendMail;
import net.webcontrol.app.siteparserfinal.databases.ResultHelper;
import net.webcontrol.app.siteparserfinal.databases.ResultsActivity;
import net.webcontrol.app.siteparserfinal.dataloader.InfoActivity;

import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * by Knyazev D.A.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button start;
    LinearLayout llMain;
    RadioGroup rgSearcher;
    EditText siteUrl, stepET;
    TextView siteTitle;
    Button history;
    Button title;
    Button siteData;
    String titleS;
    Integer resumeValue = 1242454;
    int initialize;
    boolean timer = true;

    RadioGroup rgFL;
    RadioGroup rgSL;

    RadioButton radioGoogle;
    RadioButton radioBing;
    RadioButton radioAol;
    RadioButton radioAsk;
    RadioButton radioYandex;
    RadioButton radioBaidu;
    RadioButton radioYahoo;
    TextView resultView;
    Intent intentDB;
    public static final String APP_PREFERENCES = "mystartsettings";
    SharedPreferences sPref;

    private InterstitialAd mInterstitialAd;
    private AdView adView;

    List<String>external;
    VersionParse ps;
    private ProgressDialog pd;
    private AlertDialog.Builder limitDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Находим все элементы по модификаторам
        llMain     = (LinearLayout) findViewById(R.id.linLayout1);
        rgSearcher = (RadioGroup)   findViewById(R.id.rgSearcher);
        siteUrl    = (EditText)     findViewById(R.id.siteUrl);
        stepET     = (EditText)     findViewById(R.id.stepET);
        siteTitle  = (TextView)     findViewById(R.id.titleSite);
        start      = (Button)       findViewById(R.id.btStart);
        history    = (Button)       findViewById(R.id.historyM);
        title      = (Button)       findViewById(R.id.button_title);
        siteData   = (Button)       findViewById(R.id.siteData);

        rgFL = (RadioGroup) findViewById(R.id.rgSearcherFirstLine);
        rgSL = (RadioGroup) findViewById(R.id.rgSearcherSecondLine);

        radioGoogle = (RadioButton) findViewById(R.id.radioGoogle);
        radioBing   = (RadioButton) findViewById(R.id.radioBing);
        radioAol    = (RadioButton) findViewById(R.id.radioAol);
        radioAsk    = (RadioButton) findViewById(R.id.radioAsk); //сделан невидемым.
        radioYandex = (RadioButton) findViewById(R.id.radioYandex);
        radioBaidu  = (RadioButton) findViewById(R.id.radioBaidu);
        radioYahoo  = (RadioButton) findViewById(R.id.radioYahooJp);

        intentDB = new Intent(this, ResultsActivity.class);//Через кнопку открываем ResultActivity

        limitDialog = new AlertDialog.Builder(MainActivity.this);

        resultView = (TextView) findViewById(R.id.textResult1);

        try {
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            resultView.setText("ver. " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        history.setOnClickListener(this);
        title.setOnClickListener(this);
        start.setOnClickListener(this);
        radioGoogle.setOnClickListener(this);
        radioBing.setOnClickListener(this);
        radioAol.setOnClickListener(this);
        radioAsk.setOnClickListener(this);
        radioYandex.setOnClickListener(this);
        radioBaidu.setOnClickListener(this);
        radioYahoo.setOnClickListener(this);
        siteData.setOnClickListener(this);
        adView = (AdView) findViewById(R.id.adView);
        loadText(); //загружаем сохроняемые данные

        if (resumeValue != initialize){
            stepET.setEnabled(false);// Step Donate

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }else {
            stepET.setEnabled(true);
        }
        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();

        pd = ProgressDialog.show(MainActivity.this, "Waiting please", "request to server", true, false);
        ps = new VersionParse();
        ps.execute("your site with verify key");
    }

    /**
     * При возсрате в главное меню, если у нас не активирована полная версия приложения, запускается
     * таймер на кнопке Start (20 sec.).
     */
    @Override
    protected void onResume() {// Метод распространяется только на кнопку start
        super.onResume();
        if (resumeValue != 1242454) {
            new CountDownTimer(20000, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long millisUntilFinished) {
                    start.setEnabled(false);
                    start.setText("" + millisUntilFinished / 1000);
                    timer = false;
                }
                @Override
                public void onFinish() {
                    start.setText(R.string.start_bt);
                    start.setEnabled(true);
                    timer = true;
                }
            }.start();
            adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings){
            Intent appBilling = new Intent(this, ActivatedFull.class);
            startActivity(appBilling);
        }else if (id == R.id.action_item1) {
            Intent aboutApp = new Intent(this, AboutApp.class);
            startActivity(aboutApp);
        }else if (id == R.id.action_item2) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=YOUR_APP"));
            startActivity(intent);
        }else if (id == R.id.action_item3) {
            Intent sendMail = new Intent(this, SendMail.class);
            startActivity(sendMail);
        }
        return super.onOptionsItemSelected(item);
    }

    //Открываем следующее окно (BingParser)!
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.historyM:
                intentDB.putExtra("site", siteUrl.getText().toString());
                startActivity(intentDB);
                resumeValue = 1242454;
                break;
            case R.id.btStart:
                if (radioBing.isChecked()){
                    Intent intent = new Intent(this, BingParser.class);
                    intent.putExtra("site", siteUrl.getText().toString());
                    intent.putExtra("stepET", stepET.getText().toString());
                    intent.putExtra("wordsEncode", titleS);
                    startActivity(intent);
                    saveText();
                }else if (radioGoogle.isChecked()){
                    Intent intent2 = new Intent(this, GoogleParser.class);
                    intent2.putExtra("site", siteUrl.getText().toString());
                    intent2.putExtra("stepET", stepET.getText().toString());
                    intent2.putExtra("wordsEncode", titleS);
                    startActivity(intent2);
                    saveText();
                }else if (radioAol.isChecked()){
                    Intent intent3 = new Intent(this, AolParser.class);
                    intent3.putExtra("site", siteUrl.getText().toString());
                    intent3.putExtra("stepET", stepET.getText().toString());
                    intent3.putExtra("wordsEncode", titleS);
                    startActivity(intent3);
                    saveText();
                }else if (radioAsk.isChecked()){
                    Intent intent4 = new Intent(this, AskParser.class);
                    intent4.putExtra("site", siteUrl.getText().toString());
                    intent4.putExtra("stepET", stepET.getText().toString());
                    intent4.putExtra("wordsEncode", titleS);
                    startActivity(intent4);
                    saveText();
                }else if (radioYandex.isChecked()) {
                    Intent intent4 = new Intent(this, YandexParser.class);
                    intent4.putExtra("site", siteUrl.getText().toString());
                    intent4.putExtra("stepET", stepET.getText().toString());
                    intent4.putExtra("wordsEncode", titleS);
                    startActivity(intent4);
                    saveText();
                }else if (radioBaidu.isChecked()) {
                    Intent intent4 = new Intent(this, BaiduParser.class);
                    intent4.putExtra("site", siteUrl.getText().toString());
                    intent4.putExtra("stepET", stepET.getText().toString());
                    intent4.putExtra("wordsEncode", titleS);
                    startActivity(intent4);
                    saveText();
                }else if (radioYahoo.isChecked()) {
                    Intent intent4 = new Intent(this, YahooJpParser.class);
                    intent4.putExtra("site", siteUrl.getText().toString());
                    intent4.putExtra("stepET", stepET.getText().toString());
                    intent4.putExtra("wordsEncode", titleS);
                    startActivity(intent4);
                    saveText();
                }
                    resumeValue = initialize;// переменная вывода обратного отчета, с помощью метода onResume
                break;
            case R.id.button_title:
                    Intent intent2 = new Intent(this, TitleActivity.class);
                    startActivityForResult(intent2, 1);
                resumeValue = 1242454;
                break;
            case R.id.radioGoogle:
                resultView.setText(Html.fromHtml(getString(R.string.radio_buttonG)));
                resultView.setMovementMethod(LinkMovementMethod.getInstance());
                resumeValue = 1242454;
                rgSL.clearCheck();
                break;
            case R.id.radioBing:
                resultView.setText(Html.fromHtml(getString(R.string.radio_buttonB)));
                resultView.setMovementMethod(LinkMovementMethod.getInstance());
                resumeValue = 1242454;
                rgSL.clearCheck();
                break;
            case R.id.radioAol:
                resultView.setText(Html.fromHtml(getString(R.string.radio_buttonAol)));
                resultView.setMovementMethod(LinkMovementMethod.getInstance());
                resumeValue = 1242454;
                rgSL.clearCheck();
                break;
            case R.id.radioAsk:
                resultView.setText(Html.fromHtml(getString(R.string.radio_buttonAsk)));
                resultView.setMovementMethod(LinkMovementMethod.getInstance());
                resumeValue = 1242454;
                rgSL.clearCheck();
                break;
            case R.id.radioYandex:
                resultView.setText(Html.fromHtml(getString(R.string.radio_buttonY)));
                resultView.setMovementMethod(LinkMovementMethod.getInstance());
                resumeValue = 1242454;
                rgFL.clearCheck();
                break;
            case R.id.radioBaidu:
                resultView.setText(Html.fromHtml(getString(R.string.radio_buttonBaidu)));
                resultView.setMovementMethod(LinkMovementMethod.getInstance());
                resumeValue = 1242454;
                rgFL.clearCheck();
                break;
            case R.id.radioYahooJp:
                resultView.setText(Html.fromHtml(getString(R.string.radio_buttonYJP)));
                resultView.setMovementMethod(LinkMovementMethod.getInstance());
                resumeValue = 1242454;
                rgFL.clearCheck();
                break;
            case R.id.siteData:
                Intent intent5 = new Intent(this, InfoActivity.class);
                intent5.putExtra("yourSite", siteUrl.getText().toString());
                startActivity(intent5);
                resumeValue = 1242454;
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }

        titleS = data.getStringExtra("title");
        siteTitle.setText(titleS);

        if (siteTitle.getText().toString().equals("")){
            start.setEnabled(false);
        }
        if (siteUrl.getText().toString().equals("")){
            start.setEnabled(false);
        }
        if (siteTitle.getText().toString().length() > 4 && siteUrl.getText().toString().length() > 4 && timer){
            start.setEnabled(true);
        }
    }

    void saveText(){
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("siteUrlSS", siteUrl.getText().toString());
        editor.apply();
    }

    void loadText(){
        String saveText;

        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        saveText = sPref.getString("siteUrlSS", "");
        siteUrl.setText(saveText);
        sPref = getSharedPreferences("fullactivated", Context.MODE_PRIVATE);
        initialize = sPref.getInt("fullactivated", 363256268);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        if (resumeValue != initialize){
            showInterstitial();
        }else {
            finish();
        }
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                finish();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            finish();
        }
    }
    private void loadInterstitial() {
        // Disable the next level button and load the ad.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("StaticFieldLeak")
    private class VersionParse extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... arg) {
            external = new ArrayList<>();
            try {
                ResultHelper hh = new ResultHelper(new URL(arg[0]));
                List<TagNode> links = hh.getLinksByClass("page-header");
                for (TagNode divElement : links) {
                    external.add(divElement.getText().toString());
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return external;
        }

        protected void onPostExecute(List<String> external) {
            // если верификация не прошла, выводим диалогое окно
            String verify = external.toString().replaceAll(System.getProperty("line.separator"), "").replaceAll(" ", "");
            System.out.println(verify);
            String getKey = "[verification_459384213]"; //send on your verify page. (element div, attribute class, cssclassname page-header)
            boolean resultVerification = false;

            if (verify.equals(getKey)){
                pd.dismiss();
                resultVerification = true;
            }

            if(!resultVerification){
                limitDialog.setTitle("Stoping process").setMessage("Check the Internet connection, or you need update the app.").setCancelable(false).setNegativeButton("Back",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent answerNoResult = new Intent();
                                setResult(RESULT_OK, answerNoResult);
                                finish();
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = limitDialog.create();
                alert.show();
            }
        }
    }
}
