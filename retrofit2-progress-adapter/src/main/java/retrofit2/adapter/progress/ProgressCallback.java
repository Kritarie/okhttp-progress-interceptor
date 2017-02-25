package retrofit2.adapter.progress;

import okhttp3.progress.ProgressListener;
import retrofit2.Callback;

public interface ProgressCallback<T> extends Callback<T>, ProgressListener {
}
