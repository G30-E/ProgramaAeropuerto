package com.example.aeropuerto.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {
    public static class Entry { public String label; public float value; public float extra;
        public Entry(String l,float v,float e){label=l;value=v;extra=e;} }
    private List<Entry> data=new ArrayList<>();
    private final Paint text=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bar=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axis=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF r=new RectF();
    public BarChartView(Context c){ super(c); init(); } public BarChartView(Context c, AttributeSet a){ super(c,a); init(); }
    public BarChartView(Context c, AttributeSet a, int s){ super(c,a,s); init(); }
    private void init(){ text.setTextSize(32f); axis.setStrokeWidth(2f); }
    public void setData(List<Entry> entries){ data= entries!=null?entries:new ArrayList<Entry>(); if(data.size()>10) data=data.subList(0,10); invalidate(); }
    @Override protected void onDraw(Canvas c){
        super.onDraw(c); float w=getWidth(), h=getHeight(), pad=32f, left=pad*2, right=w-pad, top=pad, bottom=h-pad*2;
        c.drawLine(left,bottom,right,bottom,axis);
        if(data==null||data.isEmpty()){ text.setTextAlign(Paint.Align.CENTER); c.drawText("Sin datos", w/2f, h/2f, text); return; }
        float max=0; for(Entry e: data) max=Math.max(max, e.value); if(max<=0) max=1;
        int n=data.size(); float gap=20f; float barH=(bottom-top-(n-1)*gap)/n; if(barH<20) barH=20;
        float y=top; text.setTextAlign(Paint.Align.LEFT);
        for(Entry e: data){
            float bw=(e.value/max)*(right-left); r.set(left,y,left+bw,y+barH); c.drawRect(r,bar);
            String label=e.label+"  "+String.format("%.1f s",e.value)+"  ("+Math.round(e.extra*100)+"% incompletos)";
            c.drawText(label,left,y-6,text); y+=barH+gap;
        }
    }
}