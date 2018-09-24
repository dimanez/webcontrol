package net.webcontrol.app.siteparserfinal.first_settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import net.webcontrol.app.siteparserfinal.MainActivity;
import net.webcontrol.app.siteparserfinal.R;

public class SettingService extends AppCompatActivity implements View.OnClickListener{
    Switch switchSS;
    EditText siteUrl;
    EditText siteKw;
    RadioGroup rgSS;
    RadioButton rb1, rb2, rb3, rb4;
    Button next;
    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "mysettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_service);

        siteUrl = (EditText) findViewById(R.id.siteUrlSS);
        siteKw  = (EditText) findViewById(R.id.siteKwSS);
        rgSS    = (RadioGroup) findViewById(R.id.radioGroupSS);
        next    = (Button) findViewById(R.id.btSS);

        rb1 = (RadioButton) findViewById(R.id.radioButton4h);
        rb2 = (RadioButton) findViewById(R.id.radioButton8h);
        rb3 = (RadioButton) findViewById(R.id.radioButton12h);
        rb4 = (RadioButton) findViewById(R.id.radioButton24h);

        next.setOnClickListener(this);
        rb1.setOnClickListener(this);
        rb2.setOnClickListener(this);
        rb3.setOnClickListener(this);
        rb4.setOnClickListener(this);

        loadText();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btSS:
                saveText();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("yourSite", siteUrl.getText().toString());

                startActivity(intent);
                break;
        }

    }

    void saveText(){
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Editor editor = sPref.edit();
        editor.putString("siteUrlSS",siteUrl.getText().toString());

        editor.commit();
    }

    void loadText(){
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String saveText = sPref.getString("siteUrlSS","");
        siteUrl.setText(saveText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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
