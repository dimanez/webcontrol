package net.webcontrol.app.siteparserfinal.dataloader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.webcontrol.app.siteparserfinal.R;
import net.webcontrol.app.siteparserfinal.ads_control_and_menu.AboutApp;
import net.webcontrol.app.siteparserfinal.ads_control_and_menu.SendMail;

import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    TextView siteNameInfo, externalLinks, indexedG, rankG, indexedY, rankY, bingLink;
    ParseSite lps;
    ParseSite2 fps;
    List<String> external;
    List<String> iG;
    List<String> rG;
    List<String> iY;
    List<String> rY;
    String yourSite;
    Button refresh;
    SharedPreferences sPref;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView indexG = (TextView) findViewById(R.id.indexG);
        TextView indexY = (TextView) findViewById(R.id.indexY);

        siteNameInfo  = (TextView) findViewById(R.id.siteNameInfo);
        externalLinks = (TextView) findViewById(R.id.externalLinks);
        indexedG      = (TextView) findViewById(R.id.indexedG);
        rankG         = (TextView) findViewById(R.id.rankG);
        indexedY      = (TextView) findViewById(R.id.indexedY);
        rankY         = (TextView) findViewById(R.id.rankY);
        refresh       = (Button) findViewById(R.id.refresh);
        bingLink      = (TextView) findViewById(R.id.indexedB);

        Intent intent = getIntent();
        yourSite = intent.getStringExtra("yourSite");

        refresh.setOnClickListener(this);

        String bing = "Bing: <a href=\"https://www.bing.com/search?q=site:" + yourSite + "&first=1\">Indexed links</a>";
        bingLink.setText(Html.fromHtml(bing));
        bingLink.setMovementMethod(LinkMovementMethod.getInstance());

        String google =("<font color=\"blue\">G</font><font color=\"red\">o</font><font color=\"#FFBF00\">o</font><font color=\"blue\">g</font><font color=\"green\">l</font><font color=\"red\">e</font>");
        indexG.setText(Html.fromHtml(google));

        String yandex = ("<font color=\"#FF0000\">Y</font>andex");
        indexY.setText(Html.fromHtml(yandex));

        loadText();
        //кнопка назад
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        //Показываем диалог ожидания
        progressDialog = ProgressDialog.show(InfoActivity.this, "Working...", "request to server", true, false);
        lps = new ParseSite();
        lps.execute("http://e.megaindex.ru/analysis/" + yourSite);
        fps = new ParseSite2();
        fps.execute("https://www.linkpad.ru/?search=" + yourSite + "#/default.aspx?r=3&i=" + yourSite);
        saveText();
    }

    private class ParseSite extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... arg) {
            iG = new ArrayList<String>();
            rG = new ArrayList<String>();
            iY = new ArrayList<String>();
            rY = new ArrayList<String>();
            try {
                InfoHelper hh  = new InfoHelper(new URL(arg[0]));
                InfoHelper hh2 = new InfoHelper(new URL(arg[0]));
                InfoHelper hh3 = new InfoHelper(new URL(arg[0]));
                InfoHelper hh4 = new InfoHelper(new URL(arg[0]));
                List<TagNode> links  = hh.getLinksByClass("set_g_page");//("ap selected aj");
                List<TagNode> links2 = hh2.getLinksByClass("set_pr");
                List<TagNode> links3 = hh3.getLinksByClass("set_y_page");
                List<TagNode> links4 = hh4.getLinksByClass("set_tic");
                for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext(); ) {
                    TagNode divElement = (TagNode) iterator.next();
                    iG.add(divElement.getText().toString());
                }
                for (Iterator<TagNode> iterator2 = links2.iterator();iterator2.hasNext();){
                    TagNode divElement2 = (TagNode) iterator2.next();
                    rG.add(divElement2.getText().toString());
                }
                for (Iterator<TagNode> iterator3 = links3.iterator();iterator3.hasNext();){
                    TagNode divElement3 = (TagNode) iterator3.next();
                    iY.add(divElement3.getText().toString());
                }
                for (Iterator<TagNode> iterator4 = links4.iterator();iterator4.hasNext();){
                    TagNode divElement4 = (TagNode) iterator4.next();
                    rY.add(divElement4.getText().toString());
                }

            }catch (Exception ex){
                ex.printStackTrace();
                //externalLinks.setText("Server Error");
            }
            return external;
        }
        //Событие по окончанию парсинга
        @SuppressLint("SetTextI18n")
        protected void onPostExecute(List<String> external) {
            //Убираем диалог загрузки
            progressDialog.dismiss();
            indexedG.setText("Indexed" + iG.toString().replaceAll(" ", "").replaceAll("\\p{Punct}", ""));//(System.getProperty("line.separator"), ""));
            //externalLinks.setText("External Links: "+external.toString());
            rankG.setText("Google PR" + rG.toString().replaceAll(" ", "").replaceAll("\\p{Punct}", ""));
            indexedY.setText("Indexed" + iY.toString().replaceAll(" ", "").replaceAll("\\p{Punct}", ""));
            rankY.setText("Yandex tIC" + rY.toString().replaceAll(" ", "").replaceAll("\\p{Punct}", ""));
            siteNameInfo.setText("http://" + yourSite);
            lps.cancel(true);
        }


    }
    private class ParseSite2 extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... arg) {
            external = new ArrayList<String>();
            try {
                LinkHelper lh = new LinkHelper(new URL(arg[0]));
                List<TagNode> links = lh.getLinksByClass("ap selected aj");
                for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext(); ) {
                    TagNode divElement = (TagNode) iterator.next();
                    external.add(divElement.getText().toString());
                }

            }catch (Exception ex){
                ex.printStackTrace();
                //externalLinks.setText("Server Error");
            }
            return external;
        }
        //Событие по окончанию парсинга
        protected void onPostExecute(List<String> external) {
            //Убираем диалог загрузки
            //progressDialog.dismiss();
            externalLinks.setText("External links: "+external.toString().replaceAll(" ", ""));
            //externalLinks.setText("External Links: "+external.toString());
            fps.cancel(true);
        }
    }

    void saveText(){
        sPref = getPreferences(MODE_PRIVATE);
        Editor editor = sPref.edit();
        editor.putString("iG", indexedG.getText().toString().replaceAll("\\p{Punct}", ""));
        editor.putString("rG",rankG.getText().toString().replaceAll("\\p{Punct}", ""));
        editor.putString("iY",indexedY.getText().toString().replaceAll("\\p{Punct}", ""));
        editor.putString("rY",rankY.getText().toString().replaceAll("\\p{Punct}",""));
        editor.putString("external",externalLinks.getText().toString());
        editor.putString("yourUrl",siteNameInfo.getText().toString());
        editor.commit();
    }

    void loadText(){
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString("iG","");//.replaceAll("\\p{Punct}","");
        String saveText2 = sPref.getString("rG","");
        String saveText3 = sPref.getString("iY","");
        String saveText4 = sPref.getString("rY","");
        String saveText5 = sPref.getString("external","");
        String saveText6 = sPref.getString("yourUrl","");
        indexedG.setText(savedText);
        rankG.setText(saveText2);
        indexedY.setText(saveText3);
        rankY.setText(saveText4);
        externalLinks.setText(saveText5);
        siteNameInfo.setText(saveText6);
    }

    @Override
    protected void onDestroy() {
        saveText();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_item1:
                Intent aboutApp = new Intent(this, AboutApp.class);
                startActivity(aboutApp);
                break;
            case R.id.action_item2:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=YOUR_APP"));
                startActivity(intent);
                break;
            case R.id.action_item3:
                Intent sendMail = new Intent(this, SendMail.class);
                startActivity(sendMail);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
