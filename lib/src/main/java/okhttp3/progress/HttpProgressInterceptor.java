package okhttp3.progress;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpProgressInterceptor implements Interceptor {

    private final CopyOnWriteArrayList<ProgressListener> listeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<RequestFilter> filters = new CopyOnWriteArrayList<>();

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        final Response response = chain.proceed(request);
        if (listeners.isEmpty()) {
            return response;
        }
        return response.newBuilder()
                .body(new ProgressResponseBody(response.body(), new ProgressListener() {
                    @Override
                    public void onProgress(int progress) {
                        for (int i = 0; i < listeners.size(); i++) {
                            RequestFilter filter = filters.get(i);
                            if (filter.listensFor(request)) {
                                ProgressListener listener = listeners.get(i);
                                listener.onProgress(progress);
                            }
                        }
                    }

                    @Override
                    public void progressIndeterminate() {
                        for (int i = 0; i < listeners.size(); i++) {
                            RequestFilter filter = filters.get(i);
                            if (filter.listensFor(request)) {
                                ProgressListener listener = listeners.get(i);
                                listener.progressIndeterminate();
                            }
                        }
                    }
                }))
                .build();
    }

    public void addListener(ProgressListener listener) {
        addListener(listener, RequestFilters.any());
    }

    public void addListener(ProgressListener listener, RequestFilter filter) {
        listeners.add(listener);
        filters.add(filter);
    }

    public void removeListener(ProgressListener listener) {
        int index = listeners.indexOf(listener);
        listeners.remove(index);
        filters.remove(index);
    }
}
