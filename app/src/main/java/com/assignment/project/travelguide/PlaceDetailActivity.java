package com.assignment.project.travelguide;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.assignment.project.travelguide.util.Util;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class PlaceDetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        imageView = (ImageView) findViewById(R.id.image);
        detail = (TextView) findViewById(R.id.detail);

        HashMap info = Util.getDestinationDetail(Util.getDestination().getPlaceId());

        InputStream ims = null;
        try {
            ims = getAssets().open(info.get("pic_path").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(ims, null);
        imageView.setImageDrawable(d);

        detail.setText(info.get("detail").toString());
        getSupportActionBar().setTitle(Util.getDestination().getPlaceName());

    }
}
