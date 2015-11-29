package jmcdw.bcnsobrerodes.Utils;

/**
 * Created by ywy on 2015/11/26.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import jmcdw.bcnsobrerodes.BanUser;
import jmcdw.bcnsobrerodes.Delete;
import jmcdw.bcnsobrerodes.R;


public class MySimpleAdapter3 extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Map<String, Object>> list;
    private int layoutID;
    private String flag[];
    private int ItemIDs[];
    private Context context;
    private Intent intent;

    public MySimpleAdapter3(Context context, List<Map<String, Object>> list,
                            int layoutID, String flag[], int ItemIDs[]) {

        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.layoutID = layoutID;
        this.flag = flag;
        this.ItemIDs = ItemIDs;
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(layoutID, null);
        for (int i = 0; i < flag.length; i++) {
            if (convertView.findViewById(ItemIDs[i]) instanceof TextView) {

                TextView tv = (TextView) convertView.findViewById(ItemIDs[i]);

                tv.setText((String) list.get(position).get(flag[i]));
            }
        }

        ((Button)convertView.findViewById(R.id.Banbtn)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = (String) list.get(position).get(flag[0]);
                        String query = "update users " +
                                "set esta_baneado = 1 " +
                                "where username =\""+username+"\"";

                        Persistence persistence = new Persistence(context);
                        persistence.execute(query, "modification");

                        Intent intent = new Intent(context, BanUser.class);
                        ((Activity) context).finish();
                        context.startActivity(intent);

                    }
                });

        ((Button)convertView.findViewById(R.id.Desbanbtn)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = (String) list.get(position).get(flag[0]);
                        String query = "update users " +
                                "set esta_baneado = 0 " +
                                "where username =\""+username+"\"";

                        Persistence persistence = new Persistence(context);
                        persistence.execute(query, "modification");

                        Intent intent = new Intent(context, BanUser.class);
                        ((Activity) context).finish();
                        context.startActivity(intent);

                    }
                });
        return convertView;
    }

}
