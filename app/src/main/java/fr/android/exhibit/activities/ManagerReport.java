package fr.android.exhibit.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import fr.android.exhibit.entities.LiteBeacon;
import fr.android.exhibit.entities.LiteDevice;
import fr.android.exhibit.entities.LiteRecord;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ManagerReport extends AppCompatActivity {
    private static final String LIGHTNING_PUBLIC_SESSION_URL = "http://public.lightning-viz.org/sessions/bc8c34f9-31cc-480a-95b9-69b361d9dacf/visualizations";
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

        for (int i=1;i<100;i++) {
            char gender = (i % 2 == 0) ? 'M' : 'F';
            LiteDevice ld = new LiteDevice(gender, new Date(random(30,100), 11, 1), "debug",
                    "Jean-Michel", "debug@tipsy.fr", "Tipsy", "DE:BU:G0:00:00:"+String.valueOf(i));
            ld.save();
            LiteBeacon lb = new LiteBeacon("debug", "DE:BU:G0:00:00:"+String.valueOf(i), "debug_uuid", i, 0);
            lb.save();
            LiteRecord lr = null;
            int records = random(2,4);
            for(int j=1;j < records;j++) {
                lr = new LiteRecord(lb, -1, -1, random(3, 4), -1);
                lr.save();
                lr = new LiteRecord(lb, -1, -1, random(2, 4), -1);
                lr.save();
                lr = new LiteRecord(lb, -1, -1, random(2, 4), -1);
                lr.save();
                lr = new LiteRecord(lb, -1, -1, random(1, 4), -1);
                lr.save();
            }
        }
    }

    protected static int random(int min, int max) {
        return (int)(Math.random() * (max-min)) + min;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_report);

        chart = (BarChart)findViewById(R.id.barchart);
        fillBarChart(chart);
        // doit ressembler à ça = {"type":"line","data":{"series":[-3,2,3,4,5],"index":[2,3,4,5,6],"xaxis":"l'axe X"}}
        final OkHttpClient client = new OkHttpClient();
        final okhttp3.Response[] response = {null};
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, getProximityMatrixJSON());
                    Request request = new Request.Builder()
                            .url(LIGHTNING_PUBLIC_SESSION_URL)
                            .post(body)
                            .build();
                    response[0] = client.newCall(request).execute();
                    Log.e("RESPONSEBODY", response[0].body().string());

                    body = RequestBody.create(JSON, getAgeHistogramJSON());
                    request = new Request.Builder()
                            .url(LIGHTNING_PUBLIC_SESSION_URL)
                            .post(body)
                            .build();
                    response[0] = client.newCall(request).execute();
                    Log.e("RESPONSEBODY", response[0].body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }


    private String getProximityMatrixJSON() throws JSONException {
        TreeMap<Integer,Integer> map = LiteRecord.getProximityMatrix();
        // {"data":{"xaxis":"Année de naissance","yaxis":"Nombre","matrix":[[0,1],[3,-4]],"colormap":"Purples"},"options":{"numbers":true,"labels":true},"type":"matrix"}

        JSONArray matrix = new JSONArray();
        JSONArray line = new JSONArray();
        for(int i = 0 ; i < map.size()*2; i++)
            line.put(map.get(3));
        matrix.put(line);
        line = new JSONArray();
        line.put(map.get(3));
        for(int i = 0 ; i < (map.size()-1)*2; i++)
            line.put(map.get(2));
        line.put(map.get(3));
        matrix.put(line);
        line = new JSONArray();
        line.put(map.get(3));
        line.put(map.get(2));
        for(int i = 0 ; i < (map.size()-2)*2; i++)
            line.put(map.get(1));
        line.put(map.get(2));
        line.put(map.get(3));
        matrix.put(line);
        line = new JSONArray();
        line.put(map.get(3));
        line.put(map.get(2));
        for(int i = 0 ; i < (map.size()-2)*2; i++)
            line.put(map.get(1));
        line.put(map.get(2));
        line.put(map.get(3));
        matrix.put(line);
        line = new JSONArray();
        line.put(map.get(3));
        for(int i = 0 ; i < (map.size()-1)*2; i++)
            line.put(map.get(2));
        line.put(map.get(3));
        matrix.put(line);
        line = new JSONArray();
        for(int i = 0 ; i < map.size()*2; i++)
            line.put(map.get(3));
        matrix.put(line);

        JSONObject data = new JSONObject();
        data.put("matrix", matrix);

        JSONObject options = new JSONObject();
        options.put("numbers",true);

        JSONObject output = new JSONObject();
        output.put("type","matrix");
        output.put("options",options);
        output.put("data",data);

        return output.toString();
    }

    private String getAgeHistogramJSON() throws JSONException {
        TreeMap<Integer,Integer> map = LiteDevice.getCountByAge();
        SortedSet<Integer> xvalues = (SortedSet<Integer>) map.keySet();
        //{"type":"line","data":{"series":[-3,2,3,4,5],"index":[2,3,4,5,6],"xaxis":"l'axe X"}}
        String prefix = "";
        JSONArray values = new JSONArray();
        for(Integer key : xvalues) {
            for(int i = 0; i < map.get(key); i++)
                values.put(key);
        }
        JSONObject data = new JSONObject();
        data.put("values", values);

        JSONObject output = new JSONObject();
        output.put("type","histogram");
        output.put("data",data);

        return output.toString();
    }

    private String getAgeLineJSON() throws JSONException {
        TreeMap<Integer,Integer> map = LiteDevice.getCountByAge();
        SortedSet<Integer> xvalues = (SortedSet<Integer>) map.keySet();
        //{"type":"line","data":{"series":[-3,2,3,4,5],"index":[2,3,4,5,6],"xaxis":"l'axe X"}}
        String prefix = "";
        JSONArray indexes = new JSONArray();
        JSONArray values = new JSONArray();
        for(Integer key : xvalues) {
            indexes.put(key);
            values.put(map.get(key));
        }
        JSONObject data = new JSONObject();
        data.put("series", values);
        data.put("index", indexes);
        data.put("xaxis","Année de naissance");
        data.put("yaxis","Nombre");

        JSONObject output = new JSONObject();
        output.put("type","line");
        output.put("data",data);

        return output.toString();
    }

    private void fillBarChart(BarChart chart) {
        TreeMap<Integer,Integer> map = LiteDevice.getCountByAge();
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();

        int index = 0;
        for(Map.Entry<Integer,Integer> entry : map.entrySet()) {
            xVals.add(entry.getKey().toString());
            yVals.add(new BarEntry(entry.getValue(), index));
            Log.e("GRAPH", entry.getKey() + " ll " + index);
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
//        new Delete()
//                .from(LiteBeacon.class)
//                .where("name == ?","debug")
//                .execute();
//        new Delete()
//                .from(LiteDevice.class)
//                .where("name == ?","debug")
//                .execute();
//        new Delete()
//                .from(LiteRecord.class)
//                .where("rssi == ?","-1")
//                .execute();
    }
}
