package com.odeval.scoopit.ui.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Integer, Drawable> {

    private int imageViewID;
    private Context context;
    private View row;

    public Object fetch(String address) throws MalformedURLException, IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

    private Drawable ImageOperations(Context ctx, String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(Drawable d) {
        ImageView icon = (ImageView) row.findViewById(imageViewID);
        if (icon != null && d != null) {
            icon.setImageDrawable(d);
        }
    }

    public void setImageId(int imageViewID) {
        this.imageViewID = imageViewID;
    }

    public void setRow(View row) {
        this.row = row;
    }

    public void setContext(Context ctx) {
        context = ctx;
    }

    @Override
    protected Drawable doInBackground(String... url) {
        Drawable d = ImageOperations(context, url[0]);
        return d;
    }
}