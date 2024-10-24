package com.example.ftopapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StaticActivity extends AppCompatActivity {

    private BarChart barChart;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);


        barChart = findViewById(R.id.statistic_chart);
        pieChart = findViewById(R.id.category_chart);


        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 2000));
        barEntries.add(new BarEntry(1, 2500));
        barEntries.add(new BarEntry(2, 1500));
        barEntries.add(new BarEntry(3, 3000));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Expenses by Week");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();


        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(156f, "Coffee"));
        pieEntries.add(new PieEntry(87f, "Subscriptions"));
        pieEntries.add(new PieEntry(69f, "Transportation"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categories");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
