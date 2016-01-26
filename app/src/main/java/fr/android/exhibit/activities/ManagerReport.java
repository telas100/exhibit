package fr.android.exhibit.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.activeandroid.query.Delete;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.android.exhibit.entities.LiteBeacon;
import fr.android.exhibit.entities.LiteDevice;
import fr.android.exhibit.entities.LiteRecord;

public class ManagerReport extends AppCompatActivity {
    private BarChart chart;
    private final static LinkedHashMap<String,Integer> STATIC_AGE_ENTRIES = new LinkedHashMap<String,Integer>();
    static {
        STATIC_AGE_ENTRIES.put("< 10 ans", 2);
        STATIC_AGE_ENTRIES.put("10 - 15 ans", 12);
        STATIC_AGE_ENTRIES.put("15 - 20 ans", 30);
        STATIC_AGE_ENTRIES.put("20 - 25 ans", 28);
        STATIC_AGE_ENTRIES.put("25 - 30 ans", 12);
        STATIC_AGE_ENTRIES.put("30 - 50 ans", 8);
        STATIC_AGE_ENTRIES.put("> 50 ans", 1);

        Integer[] years = new Integer[]{96,96,96,98,84,84,84,34,100,
                96,96,96,98,84,84,84,34,100,70,55};

        for (int i=10;i<30;i++) {
            char gender = (i % 2 == 0) ? 'M' : 'F';
            LiteDevice ld = new LiteDevice(gender, new Date(years[i-10], 11, 1), "debug",
                    "Jean-Michel", "debug@tipsy.fr", "Tipsy", "DE:BU:G0:00:00:"+String.valueOf(i));
            LiteBeacon lb = new LiteBeacon("debug", "DE:BU:G0:00:00:"+String.valueOf(i), "debug_uuid", i, 0);
            ld.save();
            lb.save();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_report);

        chart = (BarChart)findViewById(R.id.barchart);
        fillBarChart(chart);
//        HashMap<Integer,Integer> map = LiteDevice.getCountByAge();
    }

    private void fillBarChart(BarChart chart) {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        int index = 0;
        for(Map.Entry<String, Integer> e : STATIC_AGE_ENTRIES.entrySet()) {
            xVals.add(e.getKey());
            yVals.add(new BarEntry(e.getValue(), index));
            Log.e("GRAPH", e.getKey()+" ll "+index);
            index++;
        }
        BarDataSet set = new BarDataSet(yVals, "");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        BarData data = new BarData(xVals, set);
        chart.getLegend().setEnabled(false);
        chart.setData(data);
        chart.setDescription("");
        chart.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Delete()
                .from(LiteBeacon.class)
                .where("name == ?","debug")
                .execute();
        new Delete()
                .from(LiteDevice.class)
                .where("name == ?","debug")
                .execute();
    }
}
