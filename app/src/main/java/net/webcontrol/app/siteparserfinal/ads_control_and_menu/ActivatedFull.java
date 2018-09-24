package net.webcontrol.app.siteparserfinal.ads_control_and_menu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import net.webcontrol.app.siteparserfinal.R;
import net.webcontrol.app.siteparserfinal.util.IabHelper;
import net.webcontrol.app.siteparserfinal.util.IabResult;
import net.webcontrol.app.siteparserfinal.util.Inventory;
import net.webcontrol.app.siteparserfinal.util.Purchase;

public class ActivatedFull extends AppCompatActivity implements View.OnClickListener {
    static final String ITEM_SCU = "full_app_content";

    IabHelper mHelper;

    Button buyFull;
    TextView billingText;
    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "fullactivated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activated_full);

        billingText = (TextView) findViewById(R.id.billingText);
        buyFull = (Button) findViewById(R.id.buyBt);
        buyFull.setOnClickListener(this);
        String base64EncodedPublicKey = "YOUR_KEY";

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    return;
                }
                // проверяем уже купленное
                mHelper. queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    // Слушатель для востановителя покупок.
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        private static final String TAG = "QueryInventoryFinishedListener";
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;
            }
            Log.d(TAG, "Query inventory was successful.");
/*
16.
* Проверяются покупки.
17.
* Обратите внимание, что надо проверить каждую покупку, чтобы убедиться, что всё норм!
18.
* см. verifyDeveloperPayload().
19.
*/
            Purchase purchase = inventory.getPurchase(ITEM_SCU);

            if (purchase != null && verifyDeveloperPayload(purchase)){
                //sharedPreferences
                billingText.setText("Full version activated");
                buyFull.setEnabled(false);
                sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putInt("fullactivated", 1242454);
                editor.apply();
            }

        }
    };

    @Override
    public void onClick(View v) {
        String payload ="";
        mHelper.launchPurchaseFlow(this, ITEM_SCU, 10001, mPurchaseFinishedListener,payload);
    }

    // слушатель завершения покупки
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                return;
            }
            if (purchase.getSku().equals(ITEM_SCU)) {
                Toast.makeText(getApplicationContext(), "Purchase for disabling ads done.", Toast.LENGTH_SHORT);
                // сохраняем в настройках, что отключили рекламу
                billingText.setText(R.string.buying_ok);
                sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putInt("fullactivated", 1242454);
                editor.apply();
            }
        }
    };
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
/*
* TODO: здесь необходимо свою верификацию реализовать
* Хорошо бы ещё с использованием собственного стороннего сервера.
*/
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode,resultCode,data))
            super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper!=null) mHelper.dispose();
        mHelper = null;
    }
}