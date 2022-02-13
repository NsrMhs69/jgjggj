package com.queen.musicPaint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.adivery.sdk.Adivery;
import com.adivery.sdk.AdiveryAdListener;
import com.adivery.sdk.AdiveryNativeAdView;
import com.queen.musicPaint.Generic.PaintObject;
import com.queen.musicPaint.model.InputApplicationVersionDTO;
import com.queen.musicPaint.model.OutputGetApplicationVersion;
import com.queen.musicPaint.model.OutputServiceModel;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Paint_Activity extends Activity implements OnTouchListener, SensorEventListener {

    private int topBg;
    private int index;
    private int exercise;
    private ImageView iv;
    private String title;
    private int cnt = 0;
    private RelativeLayout rl;
    private RelativeLayout starRl;
    private MyCountDownTimer countDownTimer;
    private long startTime = 2 * 1000;
    private long interval = 1 * 1000;
    private int sound;
    private Paint_Activity main_context;
    private ImageView prevbtn;
    private ImageView nextbtn;
    private PaintView paintView;
    private long lastUpdate;
    private SensorManager sensorManager;
    private Point size;
    int width;
    public static final int REQUEST_STORAGE_PERMISSIONS = 101;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(Paint_Activity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "شما دسترسی برای ذخیره را ندارید", Toast.LENGTH_SHORT).show();
                } else {
                    new SaveImage(main_context, rl);
                }
                break;
        }
    }
    public static boolean checkAndRequestPermissions(Activity context) {
        int storagePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_STORAGE_PERMISSIONS);
            return false;
        }
        return true;
    }
    InputApplicationVersionDTO inputApplicationVersionDTO;
    public void loaddataForVersion(){
        inputApplicationVersionDTO = new InputApplicationVersionDTO();
        inputApplicationVersionDTO.setApplicationName(getPackageName());
        inputApplicationVersionDTO.setMarketName(getString(R.string.market));
    }
    private void getApplicationVersion(InputApplicationVersionDTO input) {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<OutputServiceModel<OutputGetApplicationVersion>> call = apiInterface.getApplicationVersionCode(input);
        call.enqueue(new Callback<OutputServiceModel<OutputGetApplicationVersion>>() {
            @Override
            public void onResponse(Call<OutputServiceModel<OutputGetApplicationVersion>> call, Response<OutputServiceModel<OutputGetApplicationVersion>> response) {

                if (response.isSuccessful()) {
                    int statusCode = response.body().getStatus().getCode();
                    String message = response.body().getStatus().getMessage();
                    Log.i("elsaTeam", statusCode + "");
                    if (statusCode == 1) {
                        String newVersion = response.body().getBody().getVersionNumber();
                        String versionText = response.body().getBody().getVersionText();
                        boolean forceUpdate = response.body().getBody().isForceUpate();
                        Log.i("elsaTeam", newVersion + "/" + versionText);
                        String appVersionName = BuildConfig.VERSION_NAME;
                        if (Float.parseFloat(newVersion) > Float.parseFloat(appVersionName)) {
                            Log.e("elsaTeam", "Display Dialog Update");
                            updateDialog(versionText, forceUpdate);
                        }
                    } else {

                        Log.i("elsaTeam", message + "");

                    }
                } else {

                    Log.i("elsaTeam", "Error:-(");

                }
            }

            @Override
            public void onFailure(Call<OutputServiceModel<OutputGetApplicationVersion>> call, Throwable t) {
                if (t instanceof IOException) {

                    Log.i("elsaTeam", "Error :-( MayBe  internet Problem");
                }
            }
        });


    }
    public void updateDialog(String s, Boolean forceUpdate) {
        final Dialog dialog = new Dialog(Paint_Activity.this);
        dialog.setContentView(R.layout.dialog_update);
        Window window = dialog.getWindow();
        window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        MaterialButton updateBtn = (MaterialButton) dialog.findViewById(R.id.updateBtn);
        TextView versionText = (TextView) dialog.findViewById(R.id.versionText);
        versionText.setText(s);
        updateBtn.setOnClickListener(v1 -> {


            if (forceUpdate) {

                if (getString(R.string.market).equals("CafeBazar")) {
                    Uri uri = Uri.parse(getResources().getString(R.string.cafe_link) + getPackageName());
                    Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(goMarket);
                } else {
                    Uri uri = Uri.parse(getResources().getString(R.string.myket_link) + getPackageName());
                    Intent goMarket = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(goMarket);
                }
                dialog.dismiss();
                finish();
            } else {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
        loaddataForVersion();
        getApplicationVersion(inputApplicationVersionDTO);
        Adivery.prepareInterstitialAd(Paint_Activity.this, getString(R.string.interstitial));
        // banner native adivery + toye layasham hast
        AdiveryNativeAdView adView = findViewById(R.id.native_ad_view);
        adView.setListener(new AdiveryAdListener() {
            @Override
            public void onAdLoaded() {
                // تبلیغ شما بارگذاری شد
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String reason) {
                adView.setVisibility(View.INVISIBLE);
            }

        });
        adView.loadAd();


        Display display = getWindowManager().getDefaultDisplay();
        size = Generic.overrideGetSize(display);

        rl = (RelativeLayout) findViewById(R.id.paint_rl);

        paintView = new PaintView(this);
        paintView.setOnTouchListener(this);
        rl.addView(paintView);


        ImageView colorbtn = getImageButton(R.id.colorbtn);
        ImageView penbtn = getImageButton(R.id.penbtn);
        ImageView deletebtn = getImageButton(R.id.deletebtn);
        ImageView savebtn = getImageButton(R.id.savebtn);
        ImageView undobtn = getImageButton(R.id.undobtn);
        ImageView redobtn = getImageButton(R.id.redobtn);


        paintView.penColor = getIntent().getExtras().getInt("pencolor");
        paintView.brushSize = getIntent().getExtras().getInt("brushSize");
        topBg = getIntent().getExtras().getInt("bg");
        exercise = getIntent().getExtras().getInt("exercise");
        index = getIntent().getExtras().getInt("index");
        title = getIntent().getExtras().getString("title");
        sound = getIntent().getExtras().getInt("sound");


        countDownTimer = new MyCountDownTimer(startTime, interval);

        starRl = new RelativeLayout(this);
        rl.addView(starRl);

        //

        if (topBg != 0) {


            iv = new ImageView(this);
            iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));


            iv.setBackgroundResource(topBg);

            ((LinearLayout) penbtn.getParent()).removeView(penbtn);
            ((LinearLayout) colorbtn.getParent()).removeView(colorbtn);
            ((LinearLayout) undobtn.getParent()).removeView(undobtn);
            ((LinearLayout) redobtn.getParent()).removeView(redobtn);

            rl.addView(iv);

//			CheckNavigation(index);
        } else {

//            ((LinearLayout) nextbtn.getParent()).removeView(subbtn);
//            ((LinearLayout) nextbtn.getParent()).removeView(nextbtn);
//            ((LinearLayout) prevbtn.getParent()).removeView(prevbtn);
        }


        main_context = this;
        Generic.playSound(main_context, sound);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();


    }

//	public ArrayList<Generic.PaintObject> getExercise(int id) {
//		switch (id) {
//		case 1:
//			return Generic.Animals;
//		case 2:
//			return Generic.Vehicle;
//		case 3:
//			return Generic.Vegitable;
//		}
//		return null;
//	}

    private ImageView getImageButton(final int id) {

        ImageView ib = (ImageView) findViewById(id);

//        Drawable dr = ib.getCompoundDrawables()[0];
//        dr.setBounds(0, 0, 60, 60);
//        ib.setCompoundDrawables(dr, null, null, null);
//
//        Generic.setSize(size, ib, 0.1390, 0.0842);


        ib.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch (id) {
                    case R.id.penbtn:
                        Dialog pendialog = new Dialog(Paint_Activity.this);
                        pendialog.setCanceledOnTouchOutside(true);
                        pendialog.setContentView(R.layout.penlayout);
                        pendialog.setTitle("اندازه قلم");

                        final SeekBar sk = (SeekBar) pendialog
                                .findViewById(R.id.seekBar1);
                        sk.setProgress(paintView.brushSize);
                        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar,
                                                          int progress, boolean fromUser) {
                                paintView.brushSize = progress;
                            }
                        });

                        pendialog.show();

                        break;

                    case R.id.colorbtn:

                        final Dialog dialog = new Dialog(Paint_Activity.this);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.setContentView(R.layout.colorpickerlayout);
                        dialog.setTitle("انتخاب رنگ");

                        final ColorPickerView cpv = (ColorPickerView) dialog
                                .findViewById(R.id.color_picker_view);
                        cpv.setColor(paintView.penColor);

                        final View b1 = (View) dialog.findViewById(R.id.view1);
                        b1.setBackgroundColor(paintView.penColor);

                        cpv.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {

                            @Override
                            public void onColorChanged(int color) {
                                paintView.penColor = cpv.getColor();
                                b1.setBackgroundColor(paintView.penColor);

                            }
                        });

                        Button dialogButton = (Button) dialog
                                .findViewById(R.id.dialogButtonOK);
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                paintView.penColor = cpv.getColor();

                                if (Adivery.isLoaded(getString(R.string.interstitial))) {
                                    Adivery.showAd(getString(R.string.interstitial));
                                }
                                dialog.dismiss();
                            }
                        });

                        View b2 = (View) dialog.findViewById(R.id.view2);
                        b2.setBackgroundColor(cpv.getColor());

                        dialog.show();

                        break;
                    case R.id.deletebtn:
                        paintView.ClearAll();

                        if (Adivery.isLoaded(getString(R.string.interstitial))) {
                            Adivery.showAd(getString(R.string.interstitial));
                        }
                        break;

                    case R.id.undobtn:
                        paintView.Undo();

                        if (Adivery.isLoaded(getString(R.string.interstitial))) {
                            Adivery.showAd(getString(R.string.interstitial));
                        }
                        break;
                    case R.id.redobtn:
                        paintView.Redo();

                        if (Adivery.isLoaded(getString(R.string.interstitial))) {
                            Adivery.showAd(getString(R.string.interstitial));
                        }
                        break;
                    case R.id.savebtn:

                        if (checkAndRequestPermissions(Paint_Activity.this)) {
                          new SaveImage(main_context, rl);
                        }


                        if (Adivery.isLoaded(getString(R.string.interstitial))){
                            Adivery.showAd(getString(R.string.interstitial));
                        }
                        break;
                }


            }

        });
        return ib;
    }

    private void redrawPaint(PaintObject po) {
        paintView.penColor = po.penColor;
        paintView.brushSize = po.brushSize;
        iv.setBackgroundResource(po.bg);
        Generic.playSound(main_context, po.sound);
        paintView.ClearAll();
    }

//	private void CheckNavigation(int i) {
//		ArrayList<PaintObject> pos = getExercise(exercise);
//		if (pos.size()-1 == i) {
//			nextbtn.setAlpha(0.5f);
//
//		} else {
//			nextbtn.setAlpha(1f);
//		}
//
//		if (i == 0) {
//
//			prevbtn.setAlpha(0.5f);
//		} else {
//
//			prevbtn.setAlpha(1f);
//		}
//		// TODO Auto-generated method stub
//
//	}

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            countDownTimer.cancel();
            ((PaintView) v).startPath(x, y);


        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            ImageView rl1 = (ImageView) getStars();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(x, y, 0, 0);
            rl1.setLayoutParams(params);
            starRl.addView(rl1);

            ((PaintView) v).addPath(x, y);


        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            countDownTimer.start();
            ((PaintView) v).endPath(x, y);
        }
        return true;
    }

    private View getStars() {
        final ImageView iv = new ImageView(this);
        cnt = (cnt + 1) % 3;
        switch (cnt) {
            case 0:
                iv.setBackgroundResource(R.drawable.star_blue);
                break;
            case 1:
                iv.setBackgroundResource(R.drawable.star_pink);
                break;
            case 2:
                iv.setBackgroundResource(R.drawable.star_yellow);
                break;
        }

        Animation an = AnimationUtils.loadAnimation(this, R.anim.disappear);
        an.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub

                iv.setVisibility(View.INVISIBLE);
            }
        });
        iv.startAnimation(an);

        return iv;

    }

    public void saveImage(Bitmap b, int count) throws Exception {

        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;

        String s = "File";
        String alphaAndDigits = s.replaceAll("[^a-zA-Z0-9]+", "_");

        String fileName = alphaAndDigits + "_Paint_" + count;
        File file = new File(path, fileName + ".jpg");

        fOut = new FileOutputStream(file);
        b.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        fOut.flush();
        fOut.close();

        MediaStore.Images.Media.insertImage(getContentResolver(), b,
                file.getName(), file.getName());
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //getAccelerometer(event);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        Generic.playMedia();
    }


    @Override
    protected void onPause() {

        super.onPause();
        sensorManager.unregisterListener(this);

        Generic.pauseMedia();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Generic.pauseMedia();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 500) {
                return;
            }
            lastUpdate = actualTime;
            Toast.makeText(this, "", Toast.LENGTH_SHORT)
                    .show();

            paintView.ClearAll();
        }
    }


    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            if (starRl.getChildCount() != 0)
                starRl.removeAllViews();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d("Cader",
                    "Timer - " + String.valueOf(millisUntilFinished / 1000));
        }

    }


}
