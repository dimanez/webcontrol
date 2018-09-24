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
import net.webcontrol.app.siteparserfinal.helper.GoogleHelper;

import org.htmlcleaner.TagNode;

import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class GoogleParser extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Button errorBt, history;
    String wordsEncode, step;
    String searchPage, siteName, words, result;
    String parseCode;
    public static final Integer G_IDENTIFICATOR = R.drawable.gicon;
    public static final String GOOGLE_KEY = "GOOGLE";
    TextView resultG, errorView;
    List<String> output;
    int index, stepInt;
    int numOfPage = 1;
    int fc;              //Подсчет массива в процессе поиска.
    int pagination = 10; //Замена в URLe элемента, при нажатии Next
    int countingResult;  //Подсчет результата
    boolean value;       //Значение для сравнения в contains. Метод doInBackgrounds.
    boolean crash;       //Значение при ошибке парсинга гугла.
    boolean padConnector = true;//вариант как у bing
    ParseSite ps;
    ParseSite nps;
    //Диалоговые окна;
    private ProgressDialog pd;
    private AlertDialog.Builder limitDialog;

    Intent intentdb;
    ContentValues contentValues;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    PowerManager.WakeLock wakeLock;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);
        resultG   = (TextView) findViewById(R.id.resultG);
        errorView = (TextView) findViewById(R.id.errorView);
        errorBt   = (Button) findViewById(R.id.errorBt);
        history   = (Button) findViewById(R.id.historyG);

        limitDialog = new AlertDialog.Builder(GoogleParser.this);
        //Передаем данные между Activity
        Intent intent = getIntent();
        siteName    = intent.getStringExtra("site");
        words       = intent.getStringExtra("wordsEncode");
        step        = intent.getStringExtra("stepET");
        wordsEncode = URLEncoder.encode(words);
        intentdb    = new Intent(this,ResultsActivity.class);//Через кнопку открываем ResultActivity

        ListView lv = (ListView) findViewById(R.id.listViewDataG);

        errorBt.setOnClickListener(errListener);
        history.setOnClickListener(historyListener);
        //stop.setOnClickListener(stopListener);
        lv.setOnItemClickListener(this);//Слушатель нажатия на итем(Для открытия ссылки)

        //DB передаем данные в ResultActivity
        mDatabaseHelper = new DatabaseHelper(this, "wc.database", null, 7);
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
            intentdb.putExtra("site",siteName);
            finish();
            startActivity(intentdb);
        }
    };
    /**View.OnClickListener stopListener = new View.OnClickListener(){ // Возможно добавлю в будущем

        @Override
        public void onClick(View v) {
            nps.cancel(false);
            finish();
        }
    };**/

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
        pd = ProgressDialog.show(GoogleParser.this, "Working...", "request to server", true, false);
        //Запускаем парсинг
        ps = new ParseSite();
        ps.execute("https://www.google.ru/m/search?q=" + wordsEncode);
        searchPage = "https://www.google.ru/m/search?q=" + wordsEncode;
        System.out.println(wordsEncode);
        resultG.setVisibility(View.VISIBLE);
    }

    public void nextParse(){
        nps = new ParseSite();
        nps.execute("https://www.google.ru/m/search?q=" + wordsEncode + "&newwindow=1&start=" + pagination);
        searchPage = "https://www.google.ru/m/search?q=" + wordsEncode + "&newwindow=1&start=" + pagination;
        pagination = pagination + 10;
        String page;
        numOfPage++;
        page = String.valueOf(numOfPage);
        TextView textView2 = (TextView) findViewById(R.id.pageG);
        textView2.setText("Page: " + page);
    }

    @SuppressLint("StaticFieldLeak")
    private class ParseSite extends AsyncTask<String, Void, List<String>> {
        //Фоновая операция
        protected List<String> doInBackground(String... arg) {
            System.out.println("Loading doInBackground!");
            output = new ArrayList<String>();
            try
            {
                GoogleHelper hhG = new GoogleHelper(new URL(arg[0]));
                List<TagNode> links  = hhG.getLinksByClass("qzEoUe");
                List<TagNode> links2 = hhG.getLinksByClass("_yQe");
                List<TagNode> links3 = hhG.getLinksByClass("kv");
            if (padConnector) {
                for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext(); ) {
                    TagNode divElement = (TagNode) iterator.next();
                    output.add(divElement.getText().toString().replaceAll("�", ""));
                }
            }if (output.size() == 0){
                for (Iterator<TagNode> iterator = links2.iterator(); iterator.hasNext();)
                {
                    TagNode divElement = (TagNode) iterator.next();
                    output.add(divElement.getText().toString());
                }
                if (output.size() == 0){
                    for (Iterator<TagNode> iterator = links3.iterator(); iterator.hasNext();)
                    {
                        TagNode divElement = (TagNode) iterator.next();
                        output.add(divElement.getText().toString());
                    }
                }
                }
                for (String con : output){
                    CharSequence ch;
                    ch = siteName;
                    value = con.contains(ch);
                    {
                        if (value){
                            siteName=con;
                        }
                    }
                }
            } catch (Exception e) {
                crash = true;
            }
            return output;
        }

        @SuppressLint("SetTextI18n")
        protected void finalResult(){
            //поиск в массиве сайта.(ИЩЕМ ИНДЕКС)
            //поиск в массиве сайта.(ИЩЕМ ИНДЕКС)
            String www = "www." + siteName;
            String getResult;

            index = output.indexOf(siteName);
            if(index == -1){
                index = output.indexOf(www);
                System.out.println(www);
            }

            if (index != -1){
                int io = output.size() - index;
                countingResult = fc - io + 1;
                if (countingResult <= 10){
                    getResult = String.valueOf(countingResult);
                    resultG = (TextView) findViewById(R.id.resultG);
                    resultG.setEnabled(true);
                    resultG.setText("Result: " + getResult);
                    result = getResult;

                    //вставляем данные в таблицу
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");//("yyyy.MM.dd");
                    Date currentDate     = new Date();
                    contentValues.put(DatabaseHelper.SITE_NAME, siteName);
                    contentValues.put(DatabaseHelper.SITE_REQUEST, words);
                    contentValues.put(DatabaseHelper.SITE_RESULT, result);
                    contentValues.put(DatabaseHelper.DATE, sdf.format(currentDate));
                    contentValues.put(DatabaseHelper.URL, searchPage);
                    contentValues.put(DatabaseHelper.S_ID, G_IDENTIFICATOR);
                    contentValues.put(DatabaseHelper.I_KEY, GOOGLE_KEY);
                    // Вставляем данные в таблицу

                    mSqLiteDatabase.insert("site_result", null, contentValues);
                    history.setVisibility(View.VISIBLE);
                    ps.cancel(true);
                }
                if(countingResult >10) {
                    getResult = String.valueOf((countingResult + output.size() - 10));//ПРОВЕРИТЬ
                    resultG = (TextView) findViewById(R.id.resultG);
                    resultG.setEnabled(true);
                    resultG.setText("Result: " + getResult);
                    result = getResult;

                    //вставляем данный в таблицу
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");//("yyyy.MM.dd");
                    Date currentDate     = new Date();
                    contentValues.put(DatabaseHelper.SITE_NAME, siteName);
                    contentValues.put(DatabaseHelper.SITE_REQUEST, words);
                    contentValues.put(DatabaseHelper.SITE_RESULT, result);
                    contentValues.put(DatabaseHelper.DATE, sdf.format(currentDate));
                    contentValues.put(DatabaseHelper.URL, searchPage);
                    contentValues.put(DatabaseHelper.S_ID, G_IDENTIFICATOR);
                    contentValues.put(DatabaseHelper.I_KEY, GOOGLE_KEY);
                    // Вставляем данные в таблицу

                    mSqLiteDatabase.insert("site_result", null, contentValues);
                    history.setVisibility(View.VISIBLE);
                    nps.cancel(true);
                }
                mSqLiteDatabase.close();
            }
        }

        //Событие по окончанию парсинга
        protected void onPostExecute(List<String> output) {
            //Убираем диалог загрузки
            pd.dismiss();
            //Находим ListView
            ListView listView = (ListView) findViewById(R.id.listViewDataG);
            //Загружаем в него результат работы doInBackground
            listView.setAdapter(new ArrayAdapter<String>(GoogleParser.this, R.layout.mylist_item_g, output));

            //подсчет элементов
            int c = output.size();
            fc = fc + c;
            String count;
            count = String.valueOf(fc);
            TextView textView = (TextView) findViewById(R.id.countG);
            textView.setText(count);

            finalResult();
            //Условие завершение цикла операций при достижении определенного значения.
            if (fc >= stepInt) {
                //Создаем диалоговое окно
                limitDialog.setTitle("Stoping process").setMessage("Positions over "+stepInt).setCancelable(false).setNegativeButton("Back",
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
            else if (index <= -1 && !crash) {
                nextParse();
            } else if (crash) {
                System.out.println("Google crash, need download a Proxy");
                errorBt.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.VISIBLE);
                ps.cancel(false);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
        Toast.makeText(getApplicationContext(), ((TextView) itemClicked).getText(), Toast.LENGTH_SHORT).show();
        if (position==index){
            Intent opBrow = new Intent(Intent.ACTION_VIEW, Uri.parse(searchPage));
            startActivity(opBrow);
            finish(); // Закрываем активити
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_google, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                if (numOfPage <=1){
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
