package net.webcontrol.app.siteparserfinal.databases;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import net.webcontrol.app.siteparserfinal.R;
import net.webcontrol.app.siteparserfinal.ads_control_and_menu.AboutApp;
import net.webcontrol.app.siteparserfinal.ads_control_and_menu.SendMail;

import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResultsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ListView listRes;
    TextView itemName;
    Spinner searchSpinner;
    ImageView imageView;
    List<String>output;
    List<String>siteID;//Test потом удалить если не работает
    List<Integer>imageID;
    List<String>external;// Пропуем реализовать external links
    String siteName, spinnerCount, spinnerRows;
    Integer siddb;
    Button delete;
    boolean spinnerValue;

    ParseSite lps;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        siteName      = intent.getStringExtra("site");
        listRes       = (ListView) findViewById(R.id.listViewRes);
        itemName      = (TextView)findViewById(R.id.itemname);
        imageView     = (ImageView) findViewById(R.id.iconSearch);
        delete        = (Button)findViewById(R.id.deleteDB);
        searchSpinner = (Spinner) findViewById(R.id.searchSpinner);
        //Test DB
        mDatabaseHelper = new DatabaseHelper(this, "wc.database", null, 7);
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        delete.setOnClickListener(this);

        //кнопка назад
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //обработчик spinner
        searchSpinner.setOnItemSelectedListener(this);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        searchSpinner.setSelection(position);
        String setSearch = (String) searchSpinner.getSelectedItem();
        if (setSearch.equals("All")){
            spinnerValue = false;
            runnerDB();
        }if (setSearch.equals("Google")){
            spinnerRows  = "i_key = ?";
            spinnerCount = "GOOGLE";
            spinnerValue = true;
            runnerDB();
        }if (setSearch.equals("Bing")){
            spinnerRows  = "i_key = ?";
            spinnerCount = "BING";
            spinnerValue = true;
            runnerDB();
        }if (setSearch.equals("Aol")){
            spinnerRows  = "i_key = ?";
            spinnerCount = "AOL";
            spinnerValue = true;
            runnerDB();
        }if (setSearch.equals("Ask")){
            spinnerRows  = "i_key = ?";
            spinnerCount = "ASK";
            spinnerValue = true;
            runnerDB();
        }if (setSearch.equals("Yandex")){
            spinnerRows  = "i_key = ?";
            spinnerCount = "YANDEX";
            spinnerValue = true;
            runnerDB();
        }if (setSearch.equals("Baidu")){
            spinnerRows  = "i_key = ?";
            spinnerCount = "BAIDU";
            spinnerValue = true;
            runnerDB();
        }if (setSearch.equals("YahooJp")){
            spinnerRows  = "i_key = ?";
            spinnerCount = "YAHOO_JP";
            spinnerValue = true;
            runnerDB();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onClick(View v) {
        mDatabaseHelper.dropDataBase(mSqLiteDatabase);// очищаем список
        mSqLiteDatabase.close();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        runnerDB();
        //pd = ProgressDialog.show(ResultsActivity.this, "Working...", "request to server", true, false);

//        lps = new ParseSite();
//        int count = (int) (Math.random()*5);
//        System.out.println(count);
//        if (count == 0){
//            lps.execute("http://goo.gl/2cL5As");
//        }if (count == 1){
//            lps.execute("http://goo.gl/Hoagah");
//        }if (count == 2){
//            lps.execute("http://goo.gl/dO2mo9");
//        }if (count == 3){
//            lps.execute("http://goo.gl/tihAqU");
//        }if (count == 4){
//            lps.execute("http://goo.gl/WeCyKK");
//        }
    }


    protected void runnerDB(){
        if (!spinnerValue){ //Условия выполнения сортировки через курсор
            cursor = mSqLiteDatabase.query("site_result", new String[] {DatabaseHelper.SITE_NAME,
                            DatabaseHelper.SITE_REQUEST, DatabaseHelper.SITE_RESULT, DatabaseHelper.DATE, DatabaseHelper.URL, DatabaseHelper.S_ID, DatabaseHelper.I_KEY},
                    null, null, //тест сортировки в этих двух атрибутах
                    null, null, "date DESC");
        }else {
            cursor = mSqLiteDatabase.query("site_result", new String[] {DatabaseHelper.SITE_NAME,
                            DatabaseHelper.SITE_REQUEST, DatabaseHelper.SITE_RESULT, DatabaseHelper.DATE, DatabaseHelper.URL, DatabaseHelper.S_ID, DatabaseHelper.I_KEY},
                    spinnerRows, new String[]{spinnerCount}, //тест сортировки в этих двух атрибутах
                    null, null, "date DESC");
        }

        output  = new ArrayList<String>();
        siteID  = new ArrayList<String>();
        imageID = new ArrayList<Integer>();

        while (cursor.moveToNext()) {
            String sitedb    = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SITE_NAME));
            String requestdb = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SITE_REQUEST));
            String resultdb  = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SITE_RESULT));
            String datedb    = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE));
            String urldb     = cursor.getString(cursor.getColumnIndex(DatabaseHelper.URL));
            siddb = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.S_ID));

            Integer res = Integer.valueOf(resultdb);
            String color = "#FFF";
            if (res <= 20){
                color = "#00FF40";
            }if (res > 20 && res <= 50){
                color = "#D7DF01";
            }if (res > 50){
                color = "#FE2E2E";
            }
            imageID.add(siddb);
            output.add("<a href=\"" + urldb + "\">" + sitedb.replaceAll("�", "") +"</a>"+ " " + "| " + requestdb + " | " + "<font color=" + color + ">" + resultdb + "</font>" + " | " + datedb);
        }
        cursor.getCount();
        loadList();
        cursor.close();
    }

    protected void loadList() {
        CustomListAdapter adapter = new CustomListAdapter(this, output, imageID, siteID);
        listRes.setAdapter(adapter);
    }

    private class ParseSite extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... arg) {
            external = new ArrayList<String>();
            try {
                ResultHelper hh = new ResultHelper(new URL(arg[0]));
                List<TagNode> links = hh.getLinksByClass("catItemHeader");//("ap selected aj");
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
            //pd.dismiss();
            String yandex = external.toString().replaceAll(System.getProperty("line.separator"), "");
            //externalLinks.setText(yandex.replaceAll(" ", ""));
            System.out.println(yandex.replaceAll(" ", ""));
            //externalLinks.setText("External Links: "+external.toString());
            lps.cancel(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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
