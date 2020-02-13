package com.zzh.refresh.loadingview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.zzh.refresh.R;

public abstract class SimpleImageLoadingView extends BaseLoadingView {
    public SimpleImageLoadingView(Context context) {
        super(context);
        init();
    }

    public SimpleImageLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleImageLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private ImageView iv_image;

    protected void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.lib_ptr_view_simple_image_loading, this, true);
        iv_image = findViewById(R.id.iv_image);
    }

    public ImageView getImageView() {
        return iv_image;
    }
}
