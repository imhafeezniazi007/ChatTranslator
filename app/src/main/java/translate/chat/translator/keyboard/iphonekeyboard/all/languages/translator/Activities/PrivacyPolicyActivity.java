package translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chattranslator.R;
import com.example.chattranslator.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicyActivity extends AppCompatActivity {
    ActivityPrivacyPolicyBinding activityPrivacyPolicyBinding;

    private static final String KEY_FIRST_TIME = "isFirstTime";
    private static final String PREF_NAME = "MyPrefs";
    private SharedPreferences sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPrivacyPolicyBinding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(activityPrivacyPolicyBinding.getRoot());

        sharedPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        TextView htmlTextView = activityPrivacyPolicyBinding.spannableTextSecond;
        TextView htmlTextViewSecond = activityPrivacyPolicyBinding.spannableText;
        String htmlContent = getString(R.string.html_content_second);
        String htmlContentSecond = getString(R.string.html_content);

        Spanned spannedString = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spannedString = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY);
        }
        Spanned spannedStringSecond = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spannedStringSecond = Html.fromHtml(htmlContentSecond, Html.FROM_HTML_MODE_LEGACY);
        }

        SpannableString spannableString = new SpannableString(spannedString);
        SpannableString spannableStringSecond = new SpannableString(spannedStringSecond);

        int startIndex = htmlContent.indexOf("Privacy Policy");
        int endIndex = startIndex + "Privacy Policy".length();

        int secondSIndex = 16;
        int secondEIndex = 22;

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#5492FF"));
        spannableStringSecond.setSpan(colorSpan, secondSIndex, secondEIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_website))));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#5492FF"));
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        htmlTextView.setText(spannableString);
        htmlTextView.setMovementMethod(LinkMovementMethod.getInstance());
        htmlTextViewSecond.setText(spannableStringSecond);
        htmlTextViewSecond.setMovementMethod(LinkMovementMethod.getInstance());

        activityPrivacyPolicyBinding.appCompatButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityPrivacyPolicyBinding.checkBox.isChecked()) {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean(KEY_FIRST_TIME, false);
                    editor.apply();
                    startActivity(new Intent(PrivacyPolicyActivity.this, SubscriptionActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(PrivacyPolicyActivity.this, "Please read our privacy policy.", Toast.LENGTH_SHORT).show();
                    activityPrivacyPolicyBinding.checkBox.setError("CheckMe");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}