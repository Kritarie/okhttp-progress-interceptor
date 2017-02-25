package okhttp3.progress;

import okhttp3.Request;

public interface RequestFilter {
    boolean listensFor(Request request);
}
