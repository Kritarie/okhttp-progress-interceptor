package retrofit2.adapter.progress;

import okhttp3.progress.ProgressListener;
import retrofit2.Callback;

public abstract class SimpleProgressCallback<T> implements Callback<T>, ProgressListener {

    @Override
    public void progressIndeterminate() {
        // no-op
    }
}
