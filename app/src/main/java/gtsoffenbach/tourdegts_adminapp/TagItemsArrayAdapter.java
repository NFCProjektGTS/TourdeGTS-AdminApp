package gtsoffenbach.tourdegts_adminapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marlon on 09.11.2014.
 */
public class TagItemsArrayAdapter extends ArrayAdapter<TagItem> {

    int layoutResourceID;

    public TagItemsArrayAdapter(Context context, int layoutResourceID, ArrayList<TagItem> tagItems){
        super(context, layoutResourceID,tagItems);
        this.layoutResourceID = layoutResourceID;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            LayoutInflater inflater = ((Activity) this.getContext()).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceID, parent, false);

        }
        TagItem tagItem = this.getItem(position);

        //((TextView) convertView).setText(tagItem.getName());
        TextView txtID = (TextView) convertView.findViewById(R.id.textViewID);
        TextView txtName = (TextView) convertView.findViewById(R.id.textViewName);

        txtID.setText(String.valueOf(tagItem.getId()));
        txtName.setText(tagItem.getName());

        return convertView;
    }

}
