package ru.malyshev.cranescale;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.text.DecimalFormat;

import com.androidplot.Plot;
import com.androidplot.xy.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//Created by Developer on 04.02.18.

public class ChartActivity extends AppCompatActivity {

    private static final int SERIES_ALPHA = 255;
    private static final int NUM_GRIDLINES = 5;
    private XYPlot plot;
    private PanZoom panZoom;

    TextView min_text;
    TextView mid_text;
    TextView max_text;
    TextView count_text;

    String posDate;
    ResultOne resultOne;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView txt = (TextView) toolbar.findViewById(R.id.toolbar_title);
            txt.setText("График");
        }

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        posDate = bundle.getString("posDate");

        plot = (XYPlot) findViewById(R.id.plot);

        min_text = (TextView) findViewById(R.id.min_text);
        mid_text = (TextView) findViewById(R.id.mid_text);
        max_text = (TextView) findViewById(R.id.max_text);
        count_text = (TextView) findViewById(R.id.count_text);

        min_text.setText("");
        mid_text.setText("");
        max_text.setText("");
        count_text.setText("");

        // set a fixed origin and a "by-value" step mode so that grid lines will
        // move dynamically with the data when the users pans or zooms:
        plot.setUserDomainOrigin(0);
        plot.setUserRangeOrigin(0);

        // predefine the stepping of both axis
        // increment will be chosen from list to best fit NUM_GRIDLINES grid lines

        double[] inc_domain = new double[]{10,50,100,500};
        double[] inc_range = new double[]{1,5,10,20,50};

        plot.setDomainStepModel(new StepModelFit(plot.getBounds().getxRegion(),inc_domain,NUM_GRIDLINES));
        plot.setRangeStepModel( new StepModelFit(plot.getBounds().getyRegion(),inc_range,NUM_GRIDLINES));

        plot.getGraph().setLinesPerRangeLabel(4);
        plot.getGraph().setLinesPerDomainLabel(4);
        plot.getGraph().getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("#####"));
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("#####.#"));

        plot.setRangeLabel("Вес");
        plot.setDomainLabel("Измерения");

        plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);

        panZoom = PanZoom.attach(plot, PanZoom.Pan.BOTH, PanZoom.Zoom.STRETCH_BOTH, PanZoom.ZoomLimit.MIN_TICKS);
        plot.getOuterLimits().set(0, 1800, 0, 500);
        //initSpinners();

        // enable autoselect of sampling level based on visible boundaries:
        plot.getRegistry().setEstimator(new ZoomEstimator());

        checkAndReadOrCreateBasket();
        reset();

        addSeries();
    }

    private void checkAndReadOrCreateBasket(){
        String jsonText = PreferClass.readSharedSetting(ChartActivity.this, PreferClass.CS_RESULTS, "non");
        if(!jsonText.equals("non")){
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            ResultsClass resultsClass = gson.fromJson(jsonText, ResultsClass.class);

            for (int i = 0; i < resultsClass.getList().size(); i++) {
                if(resultsClass.getList().get(i).getDate().equals(posDate)){
                    resultOne = resultsClass.getList().get(i);
                }
//                class_objects.add(resultsClass.getList().get(i));
            }

            plot.setTitle(resultOne.getDate());

            min_text.setText("Минимум: " + resultOne.getMin() + " кг.");
            mid_text.setText("Среднее: " + resultOne.getMid() + " кг.");
            max_text.setText("Максимум: " + resultOne.getMax() + " кг.");
            count_text.setText("Измерений: " + resultOne.getCounter());

        }else{
//            plot.setDomainBoundaries(0, 100, BoundaryMode.FIXED);
//            plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
//            plot.redraw();
            SupportClass.ToastMessage(ChartActivity.this, "Результата нет");
            ChartActivity.this.finish();
            return;
        }
    }

    private void addSeries(){
        final FixedSizeEditableXYSeries series = new FixedSizeEditableXYSeries("График 1", resultOne.getList().size());

        for(int i = 0; i < resultOne.getList().size(); i++) {
            series.setX(i, i);
            series.setY(resultOne.getList().get(i), i);
        }

        // wrap our series in a SampledXYSeries with a threshold of 1000.
        LineAndPointFormatter formatter = new LineAndPointFormatter(Color.rgb(50, 0, 0), null, Color.argb(SERIES_ALPHA, 100, 0, 0), null);
        int hold = resultOne.getCounter()/10;
        final SampledXYSeries sampledSeries = new SampledXYSeries(series, OrderedXYSeries.XOrder.ASCENDING, 2, hold);
        plot.addSeries(sampledSeries, formatter);

        plot.redraw();
    }

    private void reset() {

        int len = resultOne.getList().size() * 2;
        plot.setDomainBoundaries(0, len, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        plot.redraw();
    }
}