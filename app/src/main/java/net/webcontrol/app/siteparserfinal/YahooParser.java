package net.webcontrol.app.siteparserfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.webcontrol.app.siteparserfinal.helper.YahooHelper;

import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class YahooParser extends AppCompatActivity implements AdapterView.OnItemClickListener{

    Button parseYH;
    String siteName;
    String words,step;
    String searchPage;
    //TextView result;
    List<String> output;
    ListView listView;
    TextView resultYH;
    int index,stepInt;
    int p;
    int fc; // Подсчет массива в процессе поиска.
    int z = 1;//Замена в URLe элемента, при нажатии Next
    int st;//Подсчет результата

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yahoo);
        resultYH = (TextView) findViewById(R.id.resultYH);
        //Находим кнопку
        parseYH = (Button)findViewById(R.id.parseYH);
        //Регистрируем onClick слушателя
        parseYH.setOnClickListener(myListener);
        Button buttonNext = (Button) findViewById(R.id.nextYH);
        buttonNext.setOnClickListener(nextListener);
        //Передаем данные между Activity
        Intent intent = getIntent();
        siteName  = intent.getStringExtra("site");
        String ww = intent.getStringExtra("wordsEncode");
        step      = intent.getStringExtra("stepET");
        words     = ww.replaceAll(" ", "+");
        ListView lv = (ListView) findViewById(R.id.listViewDataYH);
        lv.setOnItemClickListener(this);

    }
    //Диалог ожидания
    private ProgressDialog pd;
    //Слушатель OnClickListener для нашей кнопки
    private View.OnClickListener myListener = new View.OnClickListener(){

        public void onClick(View v){
            //Показываем диалог ожидания
            pd = ProgressDialog.show(YahooParser.this, "Working...", "request to server", true, false);
            //Запускаем парсинг
            searchPage = "https://search.yahoo.com/search;_ylt=--?numOfPage=" + words;
            ParseSite ps = new ParseSite();
            ps.execute(searchPage);
            System.out.println(words);
            parseYH.setVisibility(View.INVISIBLE);
            resultYH.setVisibility(View.VISIBLE);


        }
    };

    //Слушатель второй кнопки
    private View.OnClickListener nextListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            searchPage = "https://search.yahoo.com/search;_ylt=--?numOfPage=" + words + "&b=" + (z + 10);
            ParseSite nps = new ParseSite();
            nps.execute(searchPage);
            z=z + 10;
            String page;
            p++;
            page=String.valueOf(p);
            TextView textView2 = (TextView) findViewById(R.id.pageYH);
            textView2.setText("Page: " + page);
        }
    };

    private class ParseSite extends AsyncTask<String, Void, List<String>> {
        //Фоновая операция
        protected List<String> doInBackground(String... arg) {
            System.out.println("Loading doInBackground!!!!");
            output = new ArrayList<String>();
            try
            {
                YahooHelper hhYH = new YahooHelper(new URL(arg[0]));
                List<TagNode> links = hhYH.getLinksByClass(" fz-15px fw-m fc-12th wr-bw lh-15");

                for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext();)
                {
                    TagNode divElement = (TagNode) iterator.next();
                    output.add(divElement.getText().toString());
                }
                System.out.println(index);
                System.out.println(output.size());
            } catch (Exception e)
            {
                System.err.println("crash!!!!!!");
                e.printStackTrace();
            }
            return output;
        }
        protected void finalResult(){
            //поиск в массиве сайта.(ИЩЕМ ИНДЕКС)
            index=output.indexOf(siteName);
            System.out.println("INDEX = "+index);

            if (index != -1){
                int io = 10 - index;
                st = fc - io + 1;
                if (st < 10){
                    String  d = String.valueOf(st);
                    resultYH  = (TextView) findViewById(R.id.resultYH);
                    resultYH.setEnabled(true);
                    resultYH.setText("Result: " + d);}
                else { String d = String.valueOf((st + 10));
                    resultYH = (TextView) findViewById(R.id.resultYH);
                    resultYH.setEnabled(true);
                    resultYH.setText("Result: " + d);}
            }
        }

        //Событие по окончанию парсинга
        protected void onPostExecute(List<String> output) {
            //Убираем диалог загрузки
            pd.dismiss();
            //Находим ListView
            listView = (ListView) findViewById(R.id.listViewDataYH);
            //Загружаем в него результат работы doInBackground
            listView.setAdapter(new ArrayAdapter<String>(YahooParser.this, android.R.layout.simple_list_item_1, output));
            //подсчет элементов
            int c = output.size();
            fc = fc + c;
            String count;
            count = String.valueOf(fc);
            TextView textView = (TextView) findViewById(R.id.countYH);
            textView.setText(count);
            finalResult();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
        Toast.makeText(getApplicationContext(),((TextView)itemClicked).getText(),Toast.LENGTH_SHORT).show();

        System.out.println("Possition: " + position + " ID: " + id);
        if (position == index){
            System.out.println("WORKING!!!!");
            Intent opBrow = new Intent(Intent.ACTION_VIEW, Uri.parse(searchPage));
            startActivity(opBrow);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yahoo_parser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
