package net.webcontrol.app.siteparserfinal;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.webcontrol.app.siteparserfinal.ads_control_and_menu.AboutApp;
import net.webcontrol.app.siteparserfinal.ads_control_and_menu.SendMail;

public class TitleActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTitle;
    Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        editTitle = (EditText) findViewById(R.id.editTitle);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(this);

        //кнопка назад
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra("title", editTitle.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title, menu);
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
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=www.web_control.xyz.webcontrol"));
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
