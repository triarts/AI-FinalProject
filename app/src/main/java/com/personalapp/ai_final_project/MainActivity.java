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
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
    Button btnStartStop; boolean startStop = true; clickListener mclickListener = new clickListener();
    seekBarListener mSeekbarListener = new seekBarListener();
    int BpmMax = 120, BpmMin = 60, BpmStep = 1;
    int InterMax = 10, InterMin = 1, InterStep = 1;
    int currentBpm = 60;
    //List ArrList;
    List rowList; int currentRowIndex=0;
    final PostToServer mPostToServer = new PostToServer();
    String[] changedhb;
    List<String[]> listHBCSV;

    boolean flagRandom = true;

    //## graph
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private int rowcounter=0;
    private String[] datagraph;
    private int counter;
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

        btnStartStop = (Button) findViewById(R.id.btnStartStop);
        btnStartStop.setOnClickListener(mclickListener);


        seekBarBpm=(SeekBar)findViewById(R.id.seekBarBpm);
        seekBarBpm.setMax( (BpmMax - BpmMin) / BpmStep );
        seekBarBpm.setOnSeekBarChangeListener(mSeekbarListener);

        seekBarInterval=(SeekBar)findViewById(R.id.seekBarInterval);
        seekBarInterval.setMax( (InterMax - InterMin) / InterStep );
        seekBarInterval.setOnSeekBarChangeListener(mSeekbarListener);


        InputStream inputStream = getResources().openRawResource(glob.csv_filtered);
        csvFile = new CSVFile(inputStream);
        listHBCSV = csvFile.read();
        if(flagRandom)
        {
            Collections.shuffle(listHBCSV);
        }

        //###GRAPH
        datagraph =  (String[]) listHBCSV.get(rowcounter);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setDrawBorder(true);
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(1000);
        viewport.setMinY(-0.1);
        viewport.setMaxY(1.5);
        viewport.setScrollable(true);
        //###GRAPH


        //tvLog.append(PostToServer.StringArrayCombine(list.get(0)));


//        ArrList.add(PostToServer.StringArrayCombine(list.get(0)));
//        ArrList.add(PostToServer.StringArrayCombine(list.get(1)));
//        changedhb = list.get(1);

        //PostToServer.postRequest(glob.defaultURL,MainActivity.this,PostToServer.setJson("hb",ArrList));
        setupArrListSample();
        mPostToServer.setTextViewLog(tvLog);
        mPostToServer.setShared(MainActivity.this);
        mPostToServer.setAllVariable(glob.defaultURL,PostToServer.setJson(glob.keyJson,setupArrListSample()));



        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable(){
            int count = 5;
            public void run(){
                //do something
                if(startStop)
                {
                    Log.d("interval","handler");
                    mPostToServer.execute();
                    mPostToServer.bridge(setupArrListSample(),mPostToServer,glob.keyJson);
                }
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

    //untuk tentuin brapa banyaks ampel yang di kirim per interval
    int fixedInterval = 5;//second
    public int samplePerInterval()
    {
        float totalSample = ((float)currentBpm/60)*(float)fixedInterval;
        Log.d(TAG,String.valueOf(Math.ceil(totalSample)));
        return (int)Math.ceil(totalSample); // real
        //return 3; // testing
    }


    int bottomIndex,upperIndex,gap,datazise;
    public List<String> setupArrListSample()
    {
        if(currentRowIndex+samplePerInterval() >= datazise) {
            //reset kalau sudah mentok
            if(flagRandom)
            {
                Collections.shuffle(listHBCSV);
            }
            currentRowIndex = 0;
        }

        bottomIndex =  currentRowIndex;
        currentRowIndex+=samplePerInterval();
        upperIndex =  currentRowIndex;

        gap = upperIndex - bottomIndex;
        datazise = listHBCSV.size();

        Log.d(TAG,""+currentRowIndex+"  "+datazise);
        List<String> ArrList = new ArrayList<String>();
        ArrList.clear();

        for(int i=bottomIndex;i<upperIndex;i++)
        {
            ArrList.add(PostToServer.StringArrayCombine(listHBCSV.get(i)));
        }

        return ArrList;

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
        public void onStartTrackingTouch(SeekBar seekBar) { }


        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            tvSamplePerInterval.setText(""+samplePerInterval());
        }
    }

    //## BUTTONLISTENER
    public class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (R.id.btnStartStop == v.getId()) {
                if(startStop)
                {
                    startStop = false;
                    tvLog.setText("Paused\n"+tvLog.getText());
                    btnStartStop.setText("Start");
                }
                else
                {
                    startStop = true;
                    tvLog.setText("Continue\n"+tvLog.getText());
                    btnStartStop.setText("Pause");
                }
            }
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


    //###GRAPH
    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <listHBCSV.size(); i++) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0;i<187;i++)
                            {
                                if(counter < datagraph.length-1)
                                {
                                    addEntry();
                                    counter+=1;
                                }
                                else{
                                    rowcounter++;
                                    counter=0;

                                }
                            }

                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    // add random data to graph
    Double dataInput;
    private void addEntry() {
            dataInput = Double.parseDouble(datagraph[counter]);
            // here, we choose to display max 10 points on the viewport and we scroll to end
            series.appendData(new DataPoint(lastX++, dataInput), true, 935);
    }
    //###GRAPH
}
