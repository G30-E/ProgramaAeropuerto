package com.example.aeropuerto.ui;
import android.app.Activity; import android.os.Bundle; import android.widget.Button;
import com.example.aeropuerto.R; import com.example.aeropuerto.data.DBHelper; import com.example.aeropuerto.views.BarChartView;
import java.util.ArrayList; import java.util.List; import java.util.Map;

public class StatsActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_stats);
        ((Button)findViewById(R.id.btnBackHome)).setOnClickListener(v->finish());
    }
    @Override protected void onResume(){ super.onResume();
        DBHelper db=new DBHelper(this);
        List<Map<String,Object>> rows=db.reportByNationality();
        List<BarChartView.Entry> list=new ArrayList<>();
        for(Map<String,Object> m: rows){
            list.add(new BarChartView.Entry((String)m.get("nationality"),
                ((Number)m.get("avg_sec")).floatValue(),
                ((Number)m.get("incomplete_ratio")).floatValue()));
        }
        ((BarChartView)findViewById(R.id.barChart)).setData(list);
    }
}