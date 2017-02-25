package retrofit2.adapter.progress;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Request;
import okhttp3.progress.HttpProgressInterceptor;
import okhttp3.progress.ProgressListener;
import okhttp3.progress.RequestFilters;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressCall<T> {

    private final HttpProgressInterceptor progressInterceptor;
    private final Call<T> delegate;
    private final Executor executor;
    private ProgressListener listener;

    ProgressCall(HttpProgressInterceptor progressInterceptor, Executor executor, Call<T> delegate) {
        this.progressInterceptor = progressInterceptor;
        this.executor = executor;
        this.delegate = delegate;
    }

    public Response<T> execute(ProgressListener listener) throws IOException {
        this.listener = listener;
        return delegate.execute();
    }

    public void enqueue(final ProgressCallback<T> callback) {
        this.listener = callback;
        progressInterceptor.addListener(callback, RequestFilters.equals(delegate.request()));
        delegate.enqueue(new Callback<T>() {
            @Override
            public void onResponse(final Call<T> call, final Response<T> response) {
                progressInterceptor.removeListener(listener);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (delegate.isCanceled()) {
                            callback.onFailure(call, new IOException("Canceled"));
                        } else {
                            callback.onResponse(call, response);
                        }
                    }
                });
            }

            @Override
            public void onFailure(final Call<T> call, final Throwable t) {
                progressInterceptor.removeListener(listener);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(call, t);
                    }
                });
            }
        });
    }

    public boolean isExecuted() {
        return delegate.isExecuted();
    }

    public void cancel() {
        progressInterceptor.removeListener(listener);
        delegate.cancel();
    }

    public boolean isCanceled() {
        return delegate.isCanceled();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone") // deep copy.
    public ProgressCall<T> clone() {
        return new ProgressCall<>(progressInterceptor, executor, delegate.clone());
    }

    public Request request() {
        return delegate.request();
    }
}
