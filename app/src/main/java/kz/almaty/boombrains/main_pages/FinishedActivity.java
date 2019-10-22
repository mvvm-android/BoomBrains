package kz.almaty.boombrains.main_pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.almaty.boombrains.R;
import kz.almaty.boombrains.game_pages.start_page.AreYouReadyActivity;
import kz.almaty.boombrains.helpers.SharedPrefManager;

@SuppressLint("SetTextI18n")
public class FinishedActivity extends AppCompatActivity {

    @BindView(R.id.successImg) ImageView successImg;
    @BindView(R.id.successMessTxt) TextView successMessage;
    @BindView(R.id.errorsTxt) TextView errors;
    @BindView(R.id.yourScoreTxt) TextView scoreTxt;
    @BindView(R.id.recordTxt) TextView recordTxt;
    @BindView(R.id.zanovoBtn) TextView zanovoBtn;
    @BindView(R.id.quitBtn) TextView quitBtn;
    @BindView(R.id.parentConst) ConstraintLayout parent;
    @BindView(R.id.view3) View view1;
    @BindView(R.id.view4) View view2;

    int position, score, error;
    private InterstitialAd mInterstitialAd;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished);
        ButterKnife.bind(this);

        loadGoogleAd();

        position = getIntent().getIntExtra("position", 0);
        name = getIntent().getStringExtra("name");

        setBtnColor(position);
        setBackgrounds(position);

        zanovoBtn.setOnClickListener(v -> startActivities(position));

        quitBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
            overridePendingTransition(0,0);
        });
    }

    private void loadGoogleAd() {
        MobileAds.initialize(this, initializationStatus -> Log.d("INITIALIZATION::", "COMPLETED"));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7342268862236285/2044423261");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                showGoogleAdd();
                Log.d("LOADING::", "LOADED");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d("LOADING::", "FAILED");
            }
            @Override
            public void onAdOpened() { }
            @Override
            public void onAdClicked() { }
            @Override
            public void onAdLeftApplication() { }
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    private void showGoogleAdd() {
        if (SharedPrefManager.getAddCount(getApplication()) >= 3) {
            new Handler().postDelayed(() -> {
                mInterstitialAd.show();
                SharedPrefManager.setAddCount(getApplication(), 0);
            }, 30);
        }
    }

    private void startActivities(int position) {
        switch (position) {
            case 0: case 1: case 2: case 3: case 4: case 5: case 6: {
                finishAndStart(new Intent(this, AreYouReadyActivity.class), position);
                break;
            }
        }
    }

    private void finishAndStart(Intent intent, int position) {
        intent.putExtra("position", position);
        if (name != null) {
            intent.putExtra("name", name);
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void setBackgrounds(int position) {
        switch (position) {
            case 0: {
                setShulteBackgrounds();
                break;
            }
            case 1: {
                setZapomniChisloBackgrounds();
                break;
            }
            case 2: {
                setFindNumBackgrounds();
                break;
            }
            case 3: {
                setNumZnakiBackgrounds();
                break;
            }
            case 4: {
                setEquationBackgrounds();
                break;
            }
            case 5: {
                setShulteLetterBackgrounds();
                break;
            }
            case 6: {
                setRemWordsBackgrounds();
                break;
            }
        }
    }

    private void setEquationBackgrounds() {
        parent.setBackgroundResource(R.drawable.equation_blur);
        score = getIntent().getIntExtra("equationScore", 0);
        error = getIntent().getIntExtra("equationErrors", 0);
        String message = getIntent().getStringExtra("equationRecord");
        if (message != null) {
            successMessage.setText(message);
            setMeasures(successImg, R.drawable.zapomni_new_record);
            setAudio(R.raw.new_record);
        } else {
            successMessage.setText(getString(R.string.YesNoTimeOutAnswer));
            setMeasures(successImg, R.drawable.zapomni_timed_out);
            setAudio(R.raw.game_over);
        }
        scoreTxt.setText(getString(R.string.YourScore) + " " + score);
        errors.setText(getString(R.string.Mistakes) + " " + error);
        setRecords(SharedPrefManager.getEquationRecord(getApplication()));
    }

    private void setFindNumBackgrounds() {
        parent.setBackgroundResource(R.drawable.find_blur);
        score = getIntent().getIntExtra("findScore", 0);
        error = getIntent().getIntExtra("findErrors", 0);
        String message = getIntent().getStringExtra("findRecord");
        if (message != null) {
            successMessage.setText(message);
            setMeasures(successImg, R.drawable.zapomni_new_record);
            setAudio(R.raw.new_record);
        } else {
            successMessage.setText(getString(R.string.YesNoTimeOutAnswer));
            setMeasures(successImg, R.drawable.zapomni_timed_out);
            setAudio(R.raw.game_over);

        }
        scoreTxt.setText(getString(R.string.YourScore) + " " + score);
        errors.setText(getString(R.string.Mistakes) + " " + error);
        setRecords(SharedPrefManager.getFindRecord(getApplication()));
    }

    private void setAudio(int raw) {
        if (SharedPrefManager.isSoundEnabled(getApplication())) {
            MediaPlayer player = MediaPlayer.create(this, raw);
            player.setOnCompletionListener(mp -> {
                mp.reset();
                mp.release();
            });
            player.start();
        }
    }

    private void setZapomniChisloBackgrounds() {
        parent.setBackgroundResource(R.drawable.zapomni_chislo_blur);
        score = getIntent().getIntExtra("chisloScore", 0);
        error = getIntent().getIntExtra("chisloErrors", 0);
        String message = getIntent().getStringExtra("chisloRecord");
        if (message != null) {
            successMessage.setText(message);
            setMeasures(successImg, R.drawable.zapomni_new_record);
            setAudio(R.raw.new_record);
        } else {
            successMessage.setText(getString(R.string.YesNoTimeOutAnswer));
            setMeasures(successImg, R.drawable.zapomni_timed_out);
            setAudio(R.raw.game_over);

        }
        scoreTxt.setText(getString(R.string.YourScore) + " " + score);
        errors.setText(getString(R.string.Mistakes) + " " + error);
        setRecords(SharedPrefManager.getChisloRecord(getApplication()));
    }

    private void setShulteBackgrounds() {
        score = getIntent().getIntExtra("shulteScore", 0);
        error = getIntent().getIntExtra("shulteErrors", 0);
        String message = getIntent().getStringExtra("shulteRecord");
        if (message != null) {
            successMessage.setText(message);
            setMeasures(successImg, R.drawable.zapomni_new_record);
            setAudio(R.raw.new_record);
        } else {
            successMessage.setText(getString(R.string.YesNoTimeOutAnswer));
            setMeasures(successImg, R.drawable.zapomni_timed_out);
            setAudio(R.raw.game_over);
        }
        scoreTxt.setText(getString(R.string.YourScore) + " " + score);
        errors.setText(getString(R.string.Mistakes) + " " + error);
        setRecords(SharedPrefManager.getShulteRecord(getApplication()));
        parent.setBackgroundResource(R.drawable.shulte_blur);
    }

    private void setNumZnakiBackgrounds() {
        score = getIntent().getIntExtra("znakiScore", 0);
        error = getIntent().getIntExtra("znakiErrors", 0);
        String message = getIntent().getStringExtra("znakiRecord");
        if (message != null) {
            successMessage.setText(message);
            setMeasures(successImg, R.drawable.zapomni_new_record);
            setAudio(R.raw.new_record);
        } else {
            successMessage.setText(getString(R.string.YesNoTimeOutAnswer));
            setMeasures(successImg, R.drawable.zapomni_timed_out);
            setAudio(R.raw.game_over);
        }
        scoreTxt.setText(getString(R.string.YourScore) + " " + score);
        errors.setText(getString(R.string.Mistakes) + " " + error);
        setRecords(SharedPrefManager.getNumZnakiRecord(getApplication()));
        parent.setBackgroundResource(R.drawable.number_znaki_blur);
    }

    private void setShulteLetterBackgrounds() {
        score = getIntent().getIntExtra("letterScore", 0);
        error = getIntent().getIntExtra("letterErrors", 0);
        String message = getIntent().getStringExtra("letterRecord");
        if (message != null) {
            successMessage.setText(message);
            setMeasures(successImg, R.drawable.zapomni_new_record);
            setAudio(R.raw.new_record);
        } else {
            successMessage.setText(getString(R.string.YesNoTimeOutAnswer));
            setMeasures(successImg, R.drawable.zapomni_timed_out);
            setAudio(R.raw.game_over);
        }
        scoreTxt.setText(getString(R.string.YourScore) + " " + score);
        errors.setText(getString(R.string.Mistakes) + " " + error);
        setRecords(SharedPrefManager.getShulteLetterRecord(getApplication()));
        parent.setBackgroundResource(R.drawable.shulte_letter_blur);
    }

    private void setRemWordsBackgrounds() {
        score = getIntent().getIntExtra("slovoScore", 0);
        error = getIntent().getIntExtra("slovoErrors", 0);
        String message = getIntent().getStringExtra("slovoRecord");
        if (message != null) {
            successMessage.setText(message);
            setMeasures(successImg, R.drawable.zapomni_new_record);
            setAudio(R.raw.new_record);
        } else {
            successMessage.setText(getString(R.string.YesNoTimeOutAnswer));
            setMeasures(successImg, R.drawable.zapomni_timed_out);
            setAudio(R.raw.game_over);
        }
        scoreTxt.setText(getString(R.string.YourScore) + " " + score);
        errors.setText(getString(R.string.Mistakes) + " " + error);
        setRecords(SharedPrefManager.getSlovoRecord(getApplication()));
        parent.setBackgroundResource(R.drawable.rem_words_blur);
    }

    private void setBtnColor(int position) {
        switch (position) {
            case 0:
                setColors(R.color.vnimanieColor);
                break;
            case 1:
                setColors(R.color.pamiatColor);
                break;
            case 2:
                setColors(R.color.findColor);
                break;
            case 3:
                setColors(R.color.numZnakiColor);
                break;
            case 4:
                setColors(R.color.equationColor);
                break;
            case 5:
                setColors(R.color.shulteLetterColor);
                break;
            case 6:
                setColors(R.color.remWordsColor);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setRecords(String record) {
        if (record != null) {
            recordTxt.setText(getString(R.string.Record) + " " + record);
        } else {
            recordTxt.setText(getString(R.string.Record) + " 0");
        }
    }

    private void setColors(int color) {
        zanovoBtn.setTextColor(getResources().getColor(color));
        quitBtn.setTextColor(getResources().getColor(color));
    }

    private void setMeasures(ImageView image, int resource) {
        image.setImageResource(resource);
        Drawable drawable = getResources().getDrawable(resource);
        int width = drawable.getIntrinsicWidth() / 2;
        int height = drawable.getIntrinsicHeight() / 2;
        image.getLayoutParams().width = width;
        image.getLayoutParams().height = height;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        startActivity(new Intent(getApplication(), MainActivity.class));
        overridePendingTransition(0,0);
    }
}
