package okhttp3.progress.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;
import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;
import okhttp3.progress.HttpProgressInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.progress.ProgressCall;
import retrofit2.adapter.progress.ProgressCallAdapterFactory;
import retrofit2.adapter.progress.ProgressCallback;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class RetrofitActivity extends AppCompatActivity {

    private Service service;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        HttpProgressInterceptor progressInterceptor = new HttpProgressInterceptor();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(progressInterceptor)
                .build();
        service = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://swapi.co")
                .addCallAdapterFactory(ProgressCallAdapterFactory.create(progressInterceptor))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Service.class);

        Button button = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                service.fetchPeople().enqueue(new ProgressCallback<PagingResponse<Person>>() {
                    @Override
                    public void onProgress(final int progress) {
                        Log.d("Progress", "Progress: " + progress);
                        progressBar.setProgress(progress);
                        if (progress >= 100) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void progressIndeterminate() {
                        Log.d("Progress", "Progress indeterminate.");
                        progressBar.setIndeterminate(true);
                    }

                    @Override
                    public void onResponse(Call<PagingResponse<Person>> call, Response<PagingResponse<Person>> response) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<PagingResponse<Person>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private interface Service {
        @GET("api/people")
        ProgressCall<PagingResponse<Person>> fetchPeople();
    }

    private class PagingResponse<T> {
        public int count;
        public String next;
        public String previous;
        public List<T> results;
    }

    private class Person {
        public String name;
        public String birth_year;
        public String eye_color;
        public String gender;
        public String hair_color;
        public String height;
        public String mass;
        public String skin_color;
        public String home_world;
        public List<String> films;
        public List<String> species;
        public List<String> starships;
        public List<String> vehicles;
        public String created;
        public String edited;
        public String url;
    }

}
