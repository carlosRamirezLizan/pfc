package com.carlos.ramirez.android.service.pfc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.carlos.ramirez.android.service.pfc.R;
import com.carlos.ramirez.android.service.pfc.model.SubscribeOptions;

import java.util.List;

/**
 * Created by carlos on 22/9/15.
 */
public class SubscribeOptionAdapter extends BaseAdapter {
    private final Context context;
    private final int layoutResourceId;
    private List<SubscribeOptions> optionsList;

    public SubscribeOptionAdapter(Context context, List<SubscribeOptions> optionsList) {
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
            holder.optionTitleTextView = (TextView) row.findViewById(R.id.titleOptionTextView);
            holder.optionDescriptionTextView = (TextView) row.findViewById(R.id.descriptionOptionTextView);

            row.setTag(holder);
        } else {
            holder = (ProviderUserHolder) row.getTag();
        }
        holder.optionTitleTextView.setText(optionsList.get(position).getTitle());
        holder.optionDescriptionTextView.setText(optionsList.get(position).getDescription());
        return row;
    }

    static class ProviderUserHolder {
        TextView optionTitleTextView;
        TextView optionDescriptionTextView;
    }
}
