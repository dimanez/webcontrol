package net.webcontrol.app.siteparserfinal.ads_control_and_menu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.webcontrol.app.siteparserfinal.R;

public class SendMail extends AppCompatActivity implements View.OnClickListener {
    Button send;
    EditText question;
    public static final String MAILAPP = "knyazev.dmitriy.92@mail.ru";
    TextView textSendMail1, textSendMail2;
    String ANDROID = Build.VERSION.RELEASE;
    String MODEL = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
        textSendMail1 = (TextView) findViewById(R.id.textSendMail1);
        textSendMail2 = (TextView) findViewById(R.id.textSendMail2);
        send          = (Button) findViewById(R.id.sendMailBt);
        question      = (EditText) findViewById(R.id.question);

        send.setOnClickListener(this);

        String ss = "Mail: <a href=\"mailto:knyazev.dmitriy.92@mail.ru\">contact@web-control.xyz</a>";
        textSendMail1.setText(Html.fromHtml(ss));
        textSendMail1.setMovementMethod(LinkMovementMethod.getInstance());
        //кнопка назад
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        // Кому
        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[] { MAILAPP });
        // Зачем
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                "Web Control App");
        // О чём
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                question.getText().toString() + "\n" + MODEL + "\n" + ANDROID);
        emailIntent.setType("message/rfc822");
        // Поехали!
        SendMail.this.startActivity(Intent.createChooser(emailIntent,
                "Отправка письма..."));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_mail, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
