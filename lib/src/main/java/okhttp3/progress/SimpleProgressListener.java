package okhttp3.progress;

public abstract class SimpleProgressListener implements ProgressListener {

    @Override
    public void onProgress(int progress) {
        // no-op
    }

    @Override
    public void progressIndeterminate() {
        // no-op
    }
}
