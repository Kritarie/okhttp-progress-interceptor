package okhttp3.progress;

public interface ProgressListener {
    void onProgress(int progress);
    void progressIndeterminate();
}
