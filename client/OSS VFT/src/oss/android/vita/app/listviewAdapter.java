package oss.android.vita.app;

import java.util.ArrayList;
import java.util.HashMap;

import mxc.app.engine.Collection;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
/**
 *
 * @author Paresh N. Mayani
 */
public class listviewAdapter extends BaseAdapter
{
    public Collection list;
    Activity activity;
    public float totalGuitsSh = 0;
 
    public listviewAdapter(Activity activity, Collection list) {
        super();
        this.activity = activity;
        this.list = list;
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }
 
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        //return list.get(position);
        return 0;
    }
 
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
 
    private class ViewHolder {
           TextView txtFirst;
           TextView txtSecond;
           TextView txtThird;
           TextView txtFourth;
           TextView txtSixth;
           
      }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
 
        // TODO Auto-generated method stub
                ViewHolder holder;
                LayoutInflater inflater =  activity.getLayoutInflater();
 
                if (convertView == null)
                {
                    convertView = inflater.inflate(R.layout.listview_row, null);
                    holder = new ViewHolder();
                    holder.txtFirst = (TextView) convertView.findViewById(R.id.FirstText);
                    holder.txtSecond = (TextView) convertView.findViewById(R.id.SecondText);
                    holder.txtThird = (TextView) convertView.findViewById(R.id.ThirdText);
                    holder.txtFourth = (TextView) convertView.findViewById(R.id.FourthText);
                    holder.txtSixth = (TextView) convertView.findViewById(R.id.SixthText);
                    convertView.setTag(holder);
                }
                else
                {
                    holder = (ViewHolder) convertView.getTag();
                }
                
                //text += list.elementAt(i).getString("brand")+"   "+plan_collection.elementAt(i).getString("tuluv")+"   "+plan_collection.elementAt(i).getString("name")+"     "+plan_collection.elementAt(i).getFloat("guitsSh")+"		"+plan_collection.elementAt(i).getFloat("guitsPack")+"\n";
 
                
                holder.txtFirst.setText(list.elementAt(position).getString("code"));
                holder.txtSecond.setText(list.elementAt(position).getString("name"));
                holder.txtThird.setText(list.elementAt(position).getFloat("tuluv")+"");
                holder.txtFourth.setText(list.elementAt(position).getFloat("guitsSh")+"");
                holder.txtSixth.setText(list.elementAt(position).getFloat("per")+"");
 
            return convertView;
    }
 
}