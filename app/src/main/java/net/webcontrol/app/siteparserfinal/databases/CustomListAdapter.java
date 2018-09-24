package net.webcontrol.app.siteparserfinal.databases;

import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.webcontrol.app.siteparserfinal.R;

import java.util.List;

/**
 * Created by Дима on 02.04.2016.
 */
public class CustomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final List<String> output;
    private final List<String> siteID;
    private final List<Integer> imageID;

    public CustomListAdapter(Activity context, List<String> output, List<Integer>imageID, List<String>siteID) {
        super(context, R.layout.custom_list,output);
        this.context = context;
        this.output  = output;
        this.imageID = imageID;
        this.siteID  = siteID;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_list, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.itemname);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.iconSearch);

        //TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(Html.fromHtml(output.get(position)));
        txtTitle.setMovementMethod(LinkMovementMethod.getInstance());
        imageView.setImageResource(imageID.get(position));

        //extratxt.setText("Description "+itemname[position]);
        return rowView;
    }
}
