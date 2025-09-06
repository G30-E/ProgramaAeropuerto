package com.example.aeropuerto.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aeropuerto.R;
import com.example.aeropuerto.data.Capture;
import com.example.aeropuerto.data.DBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NewCaptureActivity extends Activity {
    private Chronometer chrono; private long startMs=0L, endMs=0L;
    private Spinner spnNationality, spnShift; private CheckBox chkHuella, chkIris, chkNacReg;
    private EditText edtPersonName, edtOperator, edtEquipment, edtNotes;
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_new_capture);
        chrono=findViewById(R.id.chronometer); spnNationality=findViewById(R.id.spnNationality); spnShift=findViewById(R.id.spnShift);
        chkHuella=findViewById(R.id.chkHuella); chkIris=findViewById(R.id.chkIris); chkNacReg=findViewById(R.id.chkNacReg);
        edtPersonName=findViewById(R.id.edtPersonName);
        edtOperator=findViewById(R.id.edtOperator); edtEquipment=findViewById(R.id.edtEquipment); edtNotes=findViewById(R.id.edtNotes);

        List<String> countries=new ArrayList<>(); Set<String> added=new HashSet<>();
        for(String code: Locale.getISOCountries()){
            Locale loc=new Locale("", code);
            String name=loc.getDisplayCountry(new Locale("es"));
            if(TextUtils.isEmpty(name)) continue;
            String up=name.toUpperCase(Locale.ROOT);
            if(up.contains("GUATEMALA") || up.contains("ESTADOS UNIDOS") || up.contains("UNITED STATES")) continue;
            if(!added.contains(name)){ countries.add(name); added.add(name); }
        }
        Collections.sort(countries, String::compareToIgnoreCase);
        spnNationality.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countries));

        ArrayAdapter<CharSequence> shiftAdapter=ArrayAdapter.createFromResource(this, R.array.turnos, android.R.layout.simple_spinner_dropdown_item);
        spnShift.setAdapter(shiftAdapter);

        Button btnStart=findViewById(R.id.btnStart), btnStop=findViewById(R.id.btnStop), btnSave=findViewById(R.id.btnSave), btnCancel=findViewById(R.id.btnCancel);
        btnStart.setOnClickListener(v->{ startMs=System.currentTimeMillis(); chrono.setBase(SystemClock.elapsedRealtime()); chrono.start(); endMs=0L; });
        btnStop.setOnClickListener(v->{ if(startMs==0){ Toast.makeText(this,"Primero presiona Iniciar",Toast.LENGTH_SHORT).show(); return; } endMs=System.currentTimeMillis(); chrono.stop(); });
        btnSave.setOnClickListener(v->save()); btnCancel.setOnClickListener(v->finish());
    }
    private void save(){
        if(startMs==0 || endMs==0 || endMs<=startMs){ Toast.makeText(this,"Tiempo inválido: inicia y finaliza el cronómetro",Toast.LENGTH_SHORT).show(); return; }
        String personName=edtPersonName.getText().toString().trim();
        if(TextUtils.isEmpty(personName)){ Toast.makeText(this,"Ingresa el nombre de la persona",Toast.LENGTH_SHORT).show(); return; }
        String nationality=(String) spnNationality.getSelectedItem();
        if(TextUtils.isEmpty(nationality)){ Toast.makeText(this,"Selecciona una nacionalidad",Toast.LENGTH_SHORT).show(); return; }
        Capture c=new Capture();
        c.personName=personName;
        c.nationality=nationality; c.fingerprintsTaken=chkHuella.isChecked(); c.irisTaken=chkIris.isChecked(); c.nationalityRecorded=chkNacReg.isChecked();
        c.startMs=startMs; c.endMs=endMs; c.seconds=(endMs-startMs)/1000.0;
        c.operator=edtOperator.getText().toString().trim(); c.shift=(String) spnShift.getSelectedItem(); c.equipment=edtEquipment.getText().toString().trim(); c.notes=edtNotes.getText().toString().trim();
        c.outlier=c.seconds<10 || c.seconds>900;
        long id=new DBHelper(this).insert(c);
        if(id>0){ Toast.makeText(this,"Guardado ("+String.format(java.util.Locale.US,"%.1f s",c.seconds)+")",Toast.LENGTH_SHORT).show(); finish(); }
        else Toast.makeText(this,"No se pudo guardar",Toast.LENGTH_SHORT).show();
    }
}