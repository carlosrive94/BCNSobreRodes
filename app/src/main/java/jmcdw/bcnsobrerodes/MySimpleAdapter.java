package jmcdw.bcnsobrerodes;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by ywy on 2015/11/21.
 */
public class MySimpleAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Map<String, Object>> list;
    private int layoutID;
    private String flag[];
    private int ItemIDs[];
    public MySimpleAdapter(Context context, List<Map<String, Object>> list,
                           int layoutID, String flag[], int ItemIDs[]) {

        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.layoutID = layoutID;
        this.flag = flag;
        this.ItemIDs = ItemIDs;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return 0;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(layoutID, null);
        for (int i = 0; i < flag.length; i++) {
            if (convertView.findViewById(ItemIDs[i]) instanceof TextView) {

                TextView tv = (TextView) convertView.findViewById(ItemIDs[i]);

                tv.setText((String) list.get(position).get(flag[i]));
            }
        }

        ((Button)convertView.findViewById(R.id.Approvebtn)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(Verify.ma)
                                .setTitle("DIY SimpleAdapter")
                                .setMessage("Approve Success！")
                                .show();
                    }
                });
        ((Button)convertView.findViewById(R.id.Denybtn)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(Verify.ma)
                                .setTitle("DIY SimpleAdapter")
                                .setMessage("Deny Success！")
                                .show();
                    }
                });
        return convertView;
    }
}