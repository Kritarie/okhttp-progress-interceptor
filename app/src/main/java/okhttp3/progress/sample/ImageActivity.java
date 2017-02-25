package okhttp3.progress.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.progress.HttpProgressInterceptor;
import okhttp3.progress.ProgressListener;
import okhttp3.progress.RequestFilters;

public class ImageActivity extends AppCompatActivity implements ProgressListener {

    private static final String IMAGE_URL = "https://lh5.googleusercontent.com/-mvpIxrtlkAA/Tndmi1sf6zI/AAAAAAAAD1E/I2vwpRqn1z8/s500/FlyEarth500_Micael_Reynaud.gif";

    private HttpProgressInterceptor progressInterceptor;
    private Button button;
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        progressInterceptor = new HttpProgressInterceptor();
        progressInterceptor.addListener(this, RequestFilters.matchesUrl(IMAGE_URL));
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(progressInterceptor)
                .build();
        Glide.get(this).register(
                GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));

        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.image_view);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Glide.with(ImageActivity.this)
                        .load(IMAGE_URL)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageView);
            }
        });
    }

    @Override
    public void onProgress(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
                if (progress >= 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void progressIndeterminate() {
        progressBar.setIndeterminate(true);
    }

}
