package com.personalapp.ai_final_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String TAG = "maindeb";
    Toolbar myToolbar;
    Intent intent;
    TextView tvLog,tvHBnum,tvSamplePerInterval;
    Global glob = new Global();
    CSVFile csvFile;
    SeekBar seekBarBpm,seekBarInterval;
    seekBarListener mSeekbarListener = new seekBarListener();
    int BpmMax = 120, BpmMin = 60, BpmStep = 1;
    int InterMax = 10, InterMin = 1, InterStep = 1;
    int currentBpm = 60;
    List ArrList;
    List rowList; int currentRowIndex=0;
    final PostToServer mPostToServer = new PostToServer();
    String[] changedhb;
    List<String[]> listHBCSV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        tvLog = (TextView) findViewById(R.id.tvLog);
        tvHBnum = (TextView) findViewById(R.id.tvHBasNumber);
        tvSamplePerInterval = (TextView)findViewById(R.id.tvSamplePerInterval);
        currentBpm = 60;


        seekBarBpm=(SeekBar)findViewById(R.id.seekBarBpm);
        seekBarBpm.setMax( (BpmMax - BpmMin) / BpmStep );
        seekBarBpm.setOnSeekBarChangeListener(mSeekbarListener);

        seekBarInterval=(SeekBar)findViewById(R.id.seekBarInterval);
        seekBarInterval.setMax( (InterMax - InterMin) / InterStep );
        seekBarInterval.setOnSeekBarChangeListener(mSeekbarListener);


        InputStream inputStream = getResources().openRawResource(glob.csv_500);
        csvFile = new CSVFile(inputStream);
        listHBCSV = csvFile.read();
        Collections.shuffle(listHBCSV);
        //tvLog.append(PostToServer.StringArrayCombine(list.get(0)));

        ArrList = new ArrayList<String>();
//        ArrList.add(PostToServer.StringArrayCombine(list.get(0)));
//        ArrList.add(PostToServer.StringArrayCombine(list.get(1)));
//        changedhb = list.get(1);

        //PostToServer.postRequest(glob.defaultURL,MainActivity.this,PostToServer.setJson("hb",ArrList));
        setupArrListSample();
        mPostToServer.setTextViewLog(tvLog);
        mPostToServer.setAllVariable(glob.defaultURL,MainActivity.this,PostToServer.setJson("hb",ArrList));



        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable(){
            int count = 5;
            public void run(){
                //do something
                Log.d("interval","handler");
                mPostToServer.execute();
                handler.postDelayed(this, delay);
            }
        }, delay);

//        Intent i= new Intent(MainActivity.this, volleySender.class);
//        MainActivity.this.startService(i);


// ### SETTING UI
        seekBarBpm.setProgress(0);
        tvSamplePerInterval.setText(""+samplePerInterval());
        tvHBnum.setText(""+currentBpm);
    }

    int fixedInterval = 5;//second
    public int samplePerInterval()
    {
        float totalSample = ((float)currentBpm/60)*(float)fixedInterval;
        Log.d(TAG,String.valueOf(Math.ceil(totalSample)));
        return (int)Math.ceil(totalSample);
    }

//    public void setHBRandomRow()
//    {
//        rowList = new ArrayList<Integer>();
//        for(int i=0;i<listHBCSV.size();i++)
//        {
//            rowList.add(i);
//        }
//        Collections.shuffle(rowList);
//    }

    int bottomIndex,upperIndex,gap,datazise;
    public void setupArrListSample()
    {
        bottomIndex =  currentRowIndex;
        currentRowIndex+=samplePerInterval();
        upperIndex =  currentRowIndex;

        gap = upperIndex - bottomIndex;
        datazise = listHBCSV.size();

        ArrList.clear();
        if(currentRowIndex < datazise)
        {
            for(int i=0;i<gap;i++)
            {
                ArrList.add(PostToServer.StringArrayCombine(listHBCSV.get(i)));
            }
        }
        else
        {
            //reset kalau sudah mentok
            Collections.shuffle(listHBCSV);
            currentRowIndex = 0;
        }

    }



    public class seekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(seekBar.getId() == R.id.seekBarBpm)
            {
                double value = BpmMin + (progress * BpmStep);
                currentBpm = (int)value;
                tvHBnum.setText(String.valueOf(currentBpm));
                //Log.d(TAG,String.valueOf(value));
                //Toast.makeText(MainActivity.this, "gesera", Toast.LENGTH_SHORT).show();
            }
            else if (seekBar.getId() == R.id.seekBarInterval) {
                //Toast.makeText(MainActivity.this, "geserbbbb", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {


        }


        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            setupArrListSample();
            mPostToServer.setPostParam(PostToServer.setJson("hb",ArrList));

//            ArrList.add(PostToServer.StringArrayCombine(changedhb));

            tvSamplePerInterval.setText(""+samplePerInterval());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            //Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
}
