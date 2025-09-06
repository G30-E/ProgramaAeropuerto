package com.example.aeropuerto.ui;
import android.app.Activity; import android.content.Intent; import android.os.Bundle; import android.widget.Button;
import com.example.aeropuerto.R;
public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main);
        ((Button)findViewById(R.id.btnNew)).setOnClickListener(v-> startActivity(new Intent(this, NewCaptureActivity.class)));
        ((Button)findViewById(R.id.btnRecords)).setOnClickListener(v-> startActivity(new Intent(this, RecordsActivity.class)));
        ((Button)findViewById(R.id.btnStats)).setOnClickListener(v-> startActivity(new Intent(this, StatsActivity.class)));
    }
}