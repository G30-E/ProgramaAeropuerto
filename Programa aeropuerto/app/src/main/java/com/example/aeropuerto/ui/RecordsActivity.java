package com.example.aeropuerto.ui;
import android.app.Activity; import android.app.AlertDialog; import android.os.Bundle;
import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup;
import android.widget.BaseAdapter; import android.widget.Button; import android.widget.ListView; import android.widget.TextView; import android.widget.Toast;
import com.example.aeropuerto.R; import com.example.aeropuerto.data.Capture; import com.example.aeropuerto.data.DBHelper;
import java.io.File; import java.io.FileOutputStream; import java.io.OutputStreamWriter; import java.nio.charset.StandardCharsets;
import java.util.ArrayList; import java.util.List; import java.util.Locale;

public class RecordsActivity extends Activity {
    private DBHelper db; private List<Capture> data=new ArrayList<>(); private RecordsAdapter adapter;
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_records);
        db=new DBHelper(this); adapter=new RecordsAdapter();
        ((ListView)findViewById(R.id.listRecords)).setAdapter(adapter);
        findViewById(R.id.btnExport).setOnClickListener(v->exportCsv());
        findViewById(R.id.btnBackHome).setOnClickListener(v->finish());
    }
    @Override protected void onResume(){ super.onResume(); data=db.getAll(); adapter.notifyDataSetChanged(); }
    private void exportCsv(){
        try{
            File dir=getExternalFilesDir(null); if(dir==null) dir=getFilesDir();
            File out=new File(dir,"capturas.csv");
            try(OutputStreamWriter w=new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8)){
                w.write("id,person_name,nationality,seconds,fingerprints,iris,nat_rec,shift\n");
                for(Capture c: data){
                    w.write(String.format(Locale.US,"%d,%s,%s,%.1f,%d,%d,%d,%s\n",
                        c.id, safe(c.personName), safe(c.nationality), c.seconds, c.fingerprintsTaken?1:0, c.irisTaken?1:0, c.nationalityRecorded?1:0, safe(c.shift)));
                }
            }
            Toast.makeText(this,"CSV en: "+out.getAbsolutePath(),Toast.LENGTH_LONG).show();
        }catch(Exception e){ Toast.makeText(this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show(); }
    }
    private String safe(String s){ return s==null? "" : s.replace(","," "); }
    private class RecordsAdapter extends BaseAdapter{
        @Override public int getCount(){ return data.size(); }
        @Override public Object getItem(int p){ return data.get(p); }
        @Override public long getItemId(int p){ return data.get(p).id; }
        @Override public View getView(int p, View cv, ViewGroup parent){
            ViewHolder h;
            if(cv==null){ cv= LayoutInflater.from(RecordsActivity.this).inflate(R.layout.item_record,parent,false); h=new ViewHolder(cv); cv.setTag(h);} else { h=(ViewHolder)cv.getTag(); }
            Capture c=data.get(p);
            h.txtTitle.setText(c.personName+" — "+c.nationality+" — "+String.format(Locale.US,"%.1f s",c.seconds));
            h.txtSubtitle.setText("Huellas: "+(c.fingerprintsTaken?"sí":"no")+" | Iris: "+(c.irisTaken?"sí":"no")+" | Datos OK: "+(c.nationalityRecorded?"sí":"no"));
            h.btnDelete.setOnClickListener(v->confirmDelete(c));
            return cv;
        }
        class ViewHolder{ TextView txtTitle, txtSubtitle; Button btnDelete; ViewHolder(View v){ txtTitle=v.findViewById(R.id.txtTitle); txtSubtitle=v.findViewById(R.id.txtSubtitle); btnDelete=v.findViewById(R.id.btnDelete);} }
    }
    private void confirmDelete(Capture c){
        new AlertDialog.Builder(this).setTitle("Eliminar").setMessage("¿Eliminar el registro de "+c.personName+" ("+c.nationality+")?")
            .setPositiveButton("Sí",(d,w)->{ db.delete(c.id); onResume(); }).setNegativeButton("No",null).show();
    }
}