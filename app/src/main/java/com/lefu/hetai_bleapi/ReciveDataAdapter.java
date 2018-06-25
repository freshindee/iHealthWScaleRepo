package com.lefu.hetai_bleapi;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */
public class ReciveDataAdapter<T> extends BaseAdapter {
    private final LayoutInflater mInflator;
    protected final List<T> mDataSet = new ArrayList<T>();
    private final Activity mActivity;

    public ReciveDataAdapter(Activity activity){
        mInflator = activity.getLayoutInflater();
        mActivity = activity;
    }

    public int getCount() {
        return mDataSet.size();
    }

    public T getItem(int position) {
        return mDataSet.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void addToFirst(T t) {
        mDataSet.add(0, t);
    }

    public void addToFirst(List<T> t) {
        mDataSet.addAll(t);
        notifyDataSetChanged();
    }

    public void removeItem(T t) {
        if (t != null) {
            mDataSet.remove(t);
            notifyDataSetChanged();
        }
    }

    public void clearAll() {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    public List<T> getDataSource() {
        return mDataSet;
    }

    public void updateListViewData(List<T> lists) {
        mDataSet.clear();
        mDataSet.addAll(lists);
        //Log.d("", "updating listview");
        notifyDataSetChanged();
    }

    public void addData(T t) {
        if (t != null) {
            mDataSet.add(t);
            notifyDataSetChanged();
        }
    }

    public void addData(List<T> lists) {
        mDataSet.addAll(lists);
        notifyDataSetChanged();
    }

    public void addDatasOnly(List<T> newDatas) {
        mDataSet.addAll(newDatas);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.list_item_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);


            final String deviceName =(String)mDataSet.get(position);
            if (deviceName != null && deviceName.length() > 0){
                viewHolder.deviceName.setText(deviceName);
            }
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        return view;
    }

    static class ViewHolder {
        TextView deviceName;
//        TextView deviceAddress;
//        TextView deviceRssi;
//        TextView ibeaconUUID;
//        TextView ibeaconMajor;
//        TextView ibeaconMinor;
//        TextView ibeaconTxPower;
//        TextView ibeaconDistance;
//        TextView ibeaconDistanceDescriptor;
//        TextView deviceLastUpdated;
//        View ibeaconSection;
//        ImageView deviceIcon;
    }

}
