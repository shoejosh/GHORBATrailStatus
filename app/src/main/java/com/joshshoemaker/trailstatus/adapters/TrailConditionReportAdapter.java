package com.joshshoemaker.trailstatus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joshshoemaker.trailstatus.R;
import com.joshshoemaker.trailstatus.models.TrailConditionReport;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Josh on 3/25/2016.
 */
public class TrailConditionReportAdapter extends BaseAdapter {

    private final Context context;
    private final List<TrailConditionReport> items;

    public TrailConditionReportAdapter(Context context, List<TrailConditionReport> items) {

        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        @Bind(R.id.condition) TextView condition;
        @Bind(R.id.report_date) TextView reportDate;
        @Bind(R.id.short_report) TextView shortReport;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_trail_condition_report, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        TrailConditionReport report = items.get(position);
        holder.condition.setText(report.getCondition());
        holder.shortReport.setText(report.getShortReport());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.US);
        String reportDateText = sdf.format(report.getUpdateDate());
        holder.reportDate.setText(reportDateText);

        return convertView;
    }
}
