package translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.OnAdsCallback;
import com.example.chattranslator.R;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.rewarded.RewardedAd;

public class AdManager {
    private Activity mContext;
    private RewardedAd mRewardedAd;
    private TemplateView mNativeAdView;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private String mPlacement;

    public AdManager(Activity context) {
        mContext = context;
    }
    public AdManager(Activity context, String placement) {
        mContext = context;
        mPlacement = placement;
    }

    public AdManager(Activity context, TemplateView adView) {
        mContext = context;
        mNativeAdView = adView;
    }

    public AdManager(Activity context, AdView adView, String placement) {
        mContext = context;
        mAdView = adView;
        mPlacement = placement;
    }

    public void showAd(AdType adType, OnAdsCallback callback) {
        switch (adType) {
            case BANNER:
                showBannerAd(mPlacement);
                break;
            case INTERSTITIAL:
                showInterstitialAd(callback);
                break;
            case NATIVE:
                showNativeAd();
                break;
            default:
                break;
        }
    }
    public void showAd(AdType adType) {
        switch (adType) {
            case BANNER:
                showBannerAd(mPlacement);
                break;
            case INTERSTITIAL:
                showInterstitialAd(null);
                break;
            case NATIVE:
                showNativeAd();
                break;
            default:
                break;
        }
    }

    private void showNativeAd() {

        AdLoader adLoader = new AdLoader.Builder(mContext, mContext.getString(R.string.id_native_ad))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().build();
                        mNativeAdView.setStyles(styles);
                        mNativeAdView.setNativeAd(nativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void showBannerAd(String placement) {

        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        Bundle extras = new Bundle();
        extras.putString("collapsible", placement);

        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();

        mAdView.loadAd(adRequest);
    }


    boolean isAdDisableTemp = false;

    private void showInterstitialAd(OnAdsCallback callback) {
        if(callback == null)
            return;
        if (isAdDisableTemp) {
            callback.onDismiss();
            return;
        }
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(mContext, mContext.getString(R.string.id_interstitial_ad), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd loadedInterstitialAd) {
                        if (loadedInterstitialAd != null) {
                            loadedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    callback.onDismiss();
                                }
                            });
                            loadedInterstitialAd.show(mContext);
                        } else {
                            Log.d("TAG", "The interstitial ad object is null");
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d("TAG", loadAdError.toString());
                        callback.onError(loadAdError.getMessage());
                    }

                });


    }

    public enum AdType {
        BANNER,
        INTERSTITIAL,
        NATIVE
    }
}