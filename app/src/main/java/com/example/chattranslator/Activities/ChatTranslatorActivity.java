package com.example.chattranslator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import com.example.chattranslator.Adapters.CountrySpinnerAdapter;
import com.example.chattranslator.Fragments.ChatTranslatorDefault;
import com.example.chattranslator.Models.Language;
import com.example.chattranslator.R;
import com.example.chattranslator.Services.LanguageApiService;
import com.example.chattranslator.databinding.ActivityChatTranslatorBinding;

import java.util.List;

public class ChatTranslatorActivity extends AppCompatActivity {

    ActivityChatTranslatorBinding activityChatTranslatorBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatTranslatorBinding = ActivityChatTranslatorBinding.inflate(getLayoutInflater());
        setContentView(activityChatTranslatorBinding.getRoot());
        activityChatTranslatorBinding.toolbar.setTitle(R.string.toolbar_cta);
        activityChatTranslatorBinding.toolbar.setNavigationIcon(R.drawable.back);
        activityChatTranslatorBinding.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(activityChatTranslatorBinding.toolbar);

        activityChatTranslatorBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String text = getIntent().getStringExtra("text");
        replaceFragment(new ChatTranslatorDefault(), text);
    }

    private void replaceFragment(Fragment fragment, String text) {
        Bundle bundle = new Bundle();
        bundle.putString("text", text);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}