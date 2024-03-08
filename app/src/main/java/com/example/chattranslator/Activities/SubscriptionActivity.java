package com.example.chattranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.example.chattranslator.R;
import com.example.chattranslator.databinding.ActivitySubscriptionBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SubscriptionActivity extends AppCompatActivity {

    ActivitySubscriptionBinding activitySubscriptionBinding;
    private static final String PREF_NAME = "MyPrefs";
    private static final String ONE_MONTH = "monthly_subscribed";
    private static final String ONE_YEAR = "yearly_subscribed";
    private static final String ONE_TIME = "one_time_purchased";
    private static final String LAST_SHOWN_KEY = "lastShown";
    private SharedPreferences sharedPreferences;
    private BillingClient billingClient;
    private String plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySubscriptionBinding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(activitySubscriptionBinding.getRoot());
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        plan = null;

        billingClient = BillingClient.newBuilder(SubscriptionActivity.this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        activitySubscriptionBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick();
            }
        });
        activitySubscriptionBinding.firstBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPlan("$10", BillingClient.SkuType.SUBS, view);
            }
        });

        activitySubscriptionBinding.secondBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPlan("$30", BillingClient.SkuType.SUBS, view);
            }
        });

        activitySubscriptionBinding.thirdBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPlan("$70", BillingClient.SkuType.INAPP, view);
            }
        });

        selectPlan("$30", BillingClient.SkuType.SUBS, activitySubscriptionBinding.secondBtnView);
        activitySubscriptionBinding.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plan != null) {
                    querySkuDetails();
                } else {
                    Log.e("TAG", "onClick: Plan is null");
                }
            }
        });
    }

    private void selectPlan(String price, String skuType, View v) {
        Drawable normalBackground = getResources().getDrawable(R.drawable.btn_normal);
        activitySubscriptionBinding.firstBtnView.setBackground(normalBackground);
        activitySubscriptionBinding.secondBtnView.setBackground(normalBackground);
        activitySubscriptionBinding.thirdBtnView.setBackground(normalBackground);

        // Reset text color of all text views inside the buttons
        activitySubscriptionBinding.tvOneMonth.setTextColor(Color.BLACK);
        activitySubscriptionBinding.tvRateOneMonth.setTextColor(Color.BLACK);
        activitySubscriptionBinding.tvOneYear.setTextColor(Color.BLACK);
        activitySubscriptionBinding.tvRateOneYear.setTextColor(Color.BLACK);
        activitySubscriptionBinding.tvOneTime.setTextColor(Color.BLACK);
        activitySubscriptionBinding.tvRateOneTime.setTextColor(Color.BLACK);

        // Set background color and text color for the selected button
        Drawable greenBackground = getResources().getDrawable(R.drawable.btn_blue_background);
        v.setBackground(greenBackground);
        if (v instanceof TextView) {
            ((TextView) v).setTextColor(Color.WHITE);
        } else {
            Log.e("Error", "View is not a TextView");
        }

        plan = price;
    }


    private PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        }
    };

    private void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            }
        };
        billingClient.consumeAsync(consumeParams, listener);

        if (!purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            Toast.makeText(this, "Purchased", Toast.LENGTH_SHORT).show();

            String sku = purchase.getSkus().get(0);
            switch (sku) {
                case "one_month_subscription":
                    grantAccessToApp(ONE_MONTH);
                    break;
                case "one_year_subscription":
                    grantAccessToApp(ONE_YEAR);
                    break;
                case "one_time_purchase":
                    grantAccessToApp(ONE_TIME);
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(this, "Already Purchased", Toast.LENGTH_SHORT).show();
        }
    }

    private void querySkuDetails() {
        List<String> skuList = new ArrayList<>();
        skuList.add("one_month_subscription");
        skuList.add("one_year_subscription");
        skuList.add("one_time_purchase");

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

        billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (skuDetailsList != null && !skuDetailsList.isEmpty()) {
                        SkuDetails selectedSkuDetails = null;
                        switch (plan) {
                            case "$10":
                                selectedSkuDetails = getSkuDetails("one_month_subscription", skuDetailsList);
                                break;
                            case "$30":
                                selectedSkuDetails = getSkuDetails("one_year_subscription", skuDetailsList);
                                break;
                            case "$70":
                                selectedSkuDetails = getSkuDetails("one_time_purchase", skuDetailsList);
                                break;
                        }
                        if (selectedSkuDetails != null) {
                            buySubscription(selectedSkuDetails);
                        } else {
                            Toast.makeText(SubscriptionActivity.this, "Invalid plan selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.d("TAG", "onSkuDetailsResponse: empty list");
                }
            }
        });
    }

    private SkuDetails getSkuDetails(String sku, List<SkuDetails> skuDetailsList) {
        for (SkuDetails skuDetails : skuDetailsList) {
            if (sku.equals(skuDetails.getSku())) {
                return skuDetails;
            }
        }
        return null;
    }

    private void buySubscription(SkuDetails skuDetails) {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                billingClient.startConnection(this);
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d("TAG", "onBillingSetupFinished: BillingClient ready");
                    BillingFlowParams.Builder builder = BillingFlowParams.newBuilder();
                    builder.setSkuDetails(skuDetails);
                    BillingFlowParams flowParams = builder.build();
                    int responseCode = billingClient.launchBillingFlow(SubscriptionActivity.this, flowParams).getResponseCode();
                    Log.d("TAG", "onBillingSetupFinished: Launch billing flow response code: " + responseCode);
                } else {
                    Log.e("TAG", "onBillingSetupFinished: Error code " + billingResult.getResponseCode());
                }
            }

        });
    }

    private void onCancelClick() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_SHOWN_KEY, Calendar.getInstance().getTimeInMillis() + (3 * 24 * 60 * 60 * 1000));
        editor.apply();

        moveToNextActivity();
    }

    private void moveToNextActivity() {
        if (!isCustomKeyboardEnabled()&&!isMicPermissionGranted()) {
            startActivity(new Intent(SubscriptionActivity.this, MicAccessActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(SubscriptionActivity.this, MainFeaturesActivity.class));
            finish();
        }
    }

    private void grantAccessToApp(String subscriptionType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(subscriptionType, true);
        editor.apply();

        if (!isCustomKeyboardEnabled()&&!isMicPermissionGranted()) {
            startActivity(new Intent(SubscriptionActivity.this, MicAccessActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(SubscriptionActivity.this, MainFeaturesActivity.class));
            finish();
        }
    }

    private boolean isMicPermissionGranted()
    {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean isCustomKeyboardEnabled() {
        InputMethodManager imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            List<InputMethodInfo> inputMethodInfos = imeManager.getEnabledInputMethodList();
            String packageName = getPackageName();
            for (InputMethodInfo info : inputMethodInfos) {
                if (info.getPackageName().equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {
        Toast.makeText(SubscriptionActivity.this, "Acknowledged", Toast.LENGTH_SHORT).show();
    };
}