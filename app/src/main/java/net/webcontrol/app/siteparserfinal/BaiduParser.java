package net.webcontrol.app.siteparserfinal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.webcontrol.app.siteparserfinal.databases.DatabaseHelper;
import net.webcontrol.app.siteparserfinal.databases.ResultsActivity;
import net.webcontrol.app.siteparserfinal.helper.BaiduHelper;
import net.webcontrol.app.siteparserfinal.helper.BaiduPadHelper;

import org.htmlcleaner.TagNode;

import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class BaiduParser extends AppCompatActivity implements AdapterView.OnItemClickListener{
    Button parseBd, history, errorBt;
    String siteName, searchPage, words, result;
    String wordsEncode, step;
    public static final Integer BD_IDENTIFICATOR = R.drawable.bdicon;
    public static final String BAIDU_KEY = "BAIDU";
    TextView errorView;
    List<String> output;
    ListView listView;
    TextView resultBd;
    int index, stepInt;
    int numOfPage = 1;
    int fc;              //Подсчет массива в процессе поиска.
    int pagination = 10; //Замена в URLe элемента, при нажатии Next
    int countingResult;  //Подсчет результата
    boolean value;       //Значение для сравнения в contains. Метод doInBackgrounds.
    boolean padConnector = true;
    //Диалоговы окна
    private ProgressDialog pd;
    private AlertDialog.Builder limitDialog;

    ParseSite ps, nps;

    Intent intentdb;
    ContentValues contentValues;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu);
        resultBd  = (TextView) findViewById(R.id.resultBd);
        errorView = (TextView) findViewById(R.id.errorView);
        parseBd   = (Button) findViewById(R.id.parseBd);
        history   = (Button) findViewById(R.id.historyBd);
        errorBt   = (Button) findViewById(R.id.errorBt);

        limitDialog = new AlertDialog.Builder(BaiduParser.this);
        //Передаем данные между Activity
        Intent intent = getIntent();
        siteName    = intent.getStringExtra("site");
        words       = intent.getStringExtra("wordsEncode");
        step        = intent.getStringExtra("stepET");
        wordsEncode = URLEncoder.encode(words);
        intentdb    = new Intent(this, ResultsActivity.class);

        ListView lv = (ListView) findViewById(R.id.listViewDataBd);
        history.setOnClickListener(historyListener);
        lv.setOnItemClickListener(this);
        errorBt.setOnClickListener(errListener);

        //DB передаем данные в ResultActivity
        mDatabaseHelper = new DatabaseHelper(this, "wc.database", null,7);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        contentValues = new ContentValues();

        stepInt = Integer.parseInt(step);
        if (stepInt > 250){
            stepInt = 250;
        }else if (stepInt < 11){
            stepInt = 11;
        }

        //back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //отключаем блокировки экрана при работе активности
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "INFO");
        wakeLock.acquire();
    }

    View.OnClickListener historyListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            intentdb.putExtra("site", siteName);
            finish();
            startActivity(intentdb);
        }
    };

    private View.OnClickListener errListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        startParse();
    }

    public void startParse(){
        //Показываем диалог ожидания
        pd = ProgressDialog.show(BaiduParser.this, "Working...", "request to server", true, false);
        //Запускаем парсинг
        searchPage = "http://m.baidu.com/s?wd="+ wordsEncode;
        ps = new ParseSite();
        ps.execute(searchPage);
        System.out.println(wordsEncode);
        parseBd.setVisibility(View.INVISIBLE);
        resultBd.setVisibility(View.VISIBLE);

    }

    public void nextParse() {
        searchPage = "http://m.baidu.com/s?wd=" + wordsEncode + "&pn="+ pagination;
        nps = new ParseSite();
        nps.execute(searchPage);
        pagination = pagination + 10;
        String page;
        numOfPage++;
        page=String.valueOf(numOfPage);
        TextView textView2 = (TextView) findViewById(R.id.pageBd);
        textView2.setText("Page: " + page);
        //код осановки парсинга при проблеме с поисковиком или блокировкой
        if (output.size() == 0 && numOfPage == 5){
            nps.cancel(true);
            errorBt.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
        }
        if (output.size() == 0 && numOfPage == 20){
            nps.cancel(true);
            errorBt.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
        }
        if (output.size() == 0 && numOfPage == 290){
            nps.cancel(true);
            errorBt.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ParseSite extends AsyncTask<String, Void, List<String>> {
        //Фоновая операция
        protected List<String> doInBackground(String... arg) {
            System.out.println("Loading doInBackground!");
            output = new ArrayList<String>();
            try
            {
                BaiduHelper hhG      = new BaiduHelper(new URL(arg[0]));
                List<TagNode> links  = hhG.getLinksByClass("site");//("c-showurl c-line-clamp1");   Сделать также как и в гугле, по мониторам определение
                BaiduPadHelper hhB   = new BaiduPadHelper(new URL(arg[0]));
                List<TagNode> links2 = hhB.getLinksByClass("c-showurl c-line-clamp1");//("c-showurl c-line-clamp1");
                if (padConnector) {
                    System.out.println("Первая попытка");
                    for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext(); ) {
                        TagNode divElement = (TagNode) iterator.next();
                        output.add(divElement.getText().toString());
                    }
                }
                if (output.size() == 0){ //Реализация для Pad'ов
                    padConnector = false;
                    System.out.println("Вторая попытка!");
                    for (Iterator<TagNode> iterator = links2.iterator(); iterator.hasNext();)
                    {
                        TagNode divElement = (TagNode) iterator.next();
                        output.add(divElement.getText().toString());
                    }
                }
                for (String con : output){
                    CharSequence ch;
                    ch = siteName;
                    value = con.contains(ch);
                    {
                        if (value){
                            siteName = con;
                        }
                    }
                }
            } catch (Exception e)
            {
                System.err.println("crash!");
                e.printStackTrace();
            }
            return output;
        }

        @SuppressLint("SetTextI18n")
        protected void finalResult(){
            //поиск в массиве сайта.(ИЩЕМ ИНДЕКС)
            String www = "www." + siteName;
            String getResult;

            index = output.indexOf(siteName);

            if(index == -1) {
                index = output.indexOf(www);
                System.out.println(www);
            }
            if (index != -1){
                int io = output.size() - index;
                countingResult = fc - io + 1;
                if (countingResult < 10){
                    getResult = String.valueOf(countingResult);
                    resultBd = (TextView) findViewById(R.id.resultBd);
                    resultBd.setEnabled(true);
                    resultBd.setText("Result: " + getResult);
                    result = getResult;

                    //вставляем данный в таблицу
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");//("yyyy.MM.dd");
                    Date currentDate     = new Date();
                    contentValues.put(DatabaseHelper.SITE_NAME, siteName);
                    contentValues.put(DatabaseHelper.SITE_REQUEST, words);
                    contentValues.put(DatabaseHelper.SITE_RESULT, result);
                    contentValues.put(DatabaseHelper.DATE, sdf.format(currentDate));
                    contentValues.put(DatabaseHelper.URL, searchPage);
                    contentValues.put(DatabaseHelper.S_ID,BD_IDENTIFICATOR);
                    contentValues.put(DatabaseHelper.I_KEY,BAIDU_KEY);
                    // Вставляем данные в таблицу

                    mSqLiteDatabase.insert("site_result", null, contentValues);
                    history.setVisibility(View.VISIBLE);
                }
                if (countingResult > 10){
                    getResult = String.valueOf((countingResult + output.size() - 10));
                    resultBd = (TextView) findViewById(R.id.resultBd);
                    resultBd.setEnabled(true);
                    resultBd.setText("Result: " + getResult);
                    result = getResult;

                    //вставляем данный в таблицу
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");//("yyyy.MM.dd");
                    Date currentDate     = new Date();
                    contentValues.put(DatabaseHelper.SITE_NAME, siteName);
                    contentValues.put(DatabaseHelper.SITE_REQUEST, words);
                    contentValues.put(DatabaseHelper.SITE_RESULT, result);
                    contentValues.put(DatabaseHelper.DATE, sdf.format(currentDate));
                    contentValues.put(DatabaseHelper.URL, searchPage);
                    contentValues.put(DatabaseHelper.S_ID,BD_IDENTIFICATOR);
                    contentValues.put(DatabaseHelper.I_KEY,BAIDU_KEY);
                    // Вставляем данные в таблицу

                    mSqLiteDatabase.insert("site_result", null, contentValues);
                    history.setVisibility(View.VISIBLE);
                }
                mSqLiteDatabase.close();
            }
        }

        //Событие по окончанию парсинга
        protected void onPostExecute(List<String> output) {
            //Убираем диалог загрузки
            pd.dismiss();
            //Находим ListView
            listView = (ListView) findViewById(R.id.listViewDataBd);
            //Загружаем в него результат работы doInBackground
            listView.setAdapter(new ArrayAdapter<String>(BaiduParser.this, android.R.layout.simple_list_item_1, output));
            //подсчет элементов
            int c = output.size();
            fc = fc + c;
            String count;
            count = String.valueOf(fc);
            TextView textView = (TextView) findViewById(R.id.countBd);
            textView.setText(count);
            finalResult();
            if (fc >= stepInt){
                //Создаем диалоговое окно
                limitDialog.setTitle("Stoping process").setMessage("Positions over " + stepInt).setCancelable(false).setNegativeButton("Back",
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
                index = 150;
            }
            //Цикл операций поиска сайта
            else if (index <= -1){
                nextParse();
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
        Toast.makeText(getApplicationContext(), ((TextView) itemClicked).getText(), Toast.LENGTH_SHORT).show();
        if (position == index){
            Intent opBrow = new Intent(Intent.ACTION_VIEW, Uri.parse(searchPage));
            startActivity(opBrow);
            finish(); // Закрываем активити
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_baidu_parser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                if (numOfPage <= 1){
                    ps.cancel(true);
                }else {
                    nps.cancel(true);
                }
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }
}
