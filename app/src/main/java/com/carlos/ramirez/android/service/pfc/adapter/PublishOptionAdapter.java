package com.carlos.ramirez.android.service.pfc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.model.PublishOptions;

import java.util.List;

/**
 * Created by carlos on 22/9/15.
 */
public class PublishOptionAdapter extends BaseAdapter {
    private final Context context;
    private final int layoutResourceId;
    private List<PublishOptions> optionsList;

    public PublishOptionAdapter(Context context, List<PublishOptions> optionsList) {
        this.layoutResourceId = R.layout.row_options;
        this.context = context;
        this.optionsList = optionsList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return optionsList.get(position);
    }

    @Override
    public int getCount() {
        return optionsList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProviderUserHolder holder;

        if (row == null) {
            LayoutInflater inflater;
            try {
                inflater = ((Activity) context).getLayoutInflater();
            } catch (ClassCastException e) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ProviderUserHolder();
            holder.publishOptionTitleTextView = (TextView) row.findViewById(R.id.titleOptionTextView);
            holder.publishOptionDescriptionTextView = (TextView) row.findViewById(R.id.descriptionOptionTextView);

            row.setTag(holder);
        } else {
            holder = (ProviderUserHolder) row.getTag();
        }
        holder.publishOptionTitleTextView.setText(optionsList.get(position).getTitle());
        holder.publishOptionDescriptionTextView.setText(optionsList.get(position).getDescription());
        return row;
    }

    static class ProviderUserHolder {
        TextView publishOptionTitleTextView;
        TextView publishOptionDescriptionTextView;
    }
}
