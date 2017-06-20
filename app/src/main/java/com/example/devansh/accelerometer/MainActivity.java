package com.example.devansh.accelerometer;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float deltaX;
    private float deltaY;
    private float deltaZ;
    private long lastUpdate=0;

    private TextView currentX, currentY, currentZ;
    private LineChart chart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        LineData data = new LineData();
        chart.setData(data);
        chart.getDescription().setEnabled(false);

    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
        chart = (LineChart) findViewById(R.id.chart);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        deltaX = event.values[0];
        deltaY = event.values[1];
        deltaZ = event.values[2];
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - lastUpdate);

        // only allow one update every POLL_FREQUENCY.
        if(diffTime > 500) {
            lastUpdate = curTime;

            addEntry(deltaX, deltaY, deltaZ);
        }
        // display the current x,y,z accelerometer values
        displayCurrentValues();
    }

    private void addEntry(float deltaX, float deltaY, float deltaZ) {
        LineData data = chart.getData();
        if (data != null) {

            ILineDataSet xData = data.getDataSetByIndex(0);
            ILineDataSet yData = data.getDataSetByIndex(1);
            ILineDataSet zData = data.getDataSetByIndex(2);

            if (xData == null) {
                xData = createSetX();
                data.addDataSet(xData);
            }
            if (yData == null) {
                yData = createSetY();
                data.addDataSet(yData);
            }
            if (zData == null) {
                zData = createSetZ();
                data.addDataSet(zData);
            }

            data.addEntry(new Entry(xData.getEntryCount(),deltaX), 0);
            data.addEntry(new Entry(yData.getEntryCount(),deltaY), 1);
            data.addEntry(new Entry(zData.getEntryCount(),deltaZ), 2);

            chart.notifyDataSetChanged();

            // let the chart know it's data has changed


            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(50);

            // move to the latest entry
            chart.moveViewToX(xData.getEntryCount());
        }
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }
    private LineDataSet createSetX() {

        LineDataSet set = new LineDataSet(null, "X-Axis");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.GREEN);
        set.setLineWidth(1f);
        set.setFillColor(Color.GREEN);
        set.setValueTextColor(Color.GREEN);
        set.setDrawCircles(false);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    private LineDataSet createSetY() {

        LineDataSet set = new LineDataSet(null, "Y-Axis");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.BLUE);
        set.setLineWidth(1f);
        set.setFillColor(Color.BLUE);
        set.setValueTextColor(Color.BLUE);
        set.setDrawCircles(false);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    private LineDataSet createSetZ() {

        LineDataSet set = new LineDataSet(null, "Z-Axis");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.RED);
        set.setLineWidth(1f);
        set.setFillColor(Color.RED);
        set.setValueTextColor(Color.RED);
        set.setDrawCircles(false);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }



}