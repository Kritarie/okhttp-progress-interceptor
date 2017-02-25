package okhttp3.progress;

import okhttp3.HttpUrl;
import okhttp3.Request;

public final class RequestFilters {

    public static RequestFilter any() {
        return new RequestFilter() {
            @Override
            public boolean listensFor(Request request) {
                return true;
            }
        };
    }

    public static RequestFilter equals(final Request request) {
        return new RequestFilter() {
            @Override
            public boolean listensFor(Request innerRequest) {
                return innerRequest.equals(request);
            }
        };
    }

    public static RequestFilter referenceEquals(final Request request) {
        return new RequestFilter() {
            @Override
            public boolean listensFor(Request innerRequest) {
                return innerRequest == request;
            }
        };
    }

    public static RequestFilter matchesUrl(final Request request) {
        return matchesUrl(request.url());
    }

    public static RequestFilter matchesUrl(HttpUrl url) {
        return matchesUrl(url.toString());
    }

    public static RequestFilter matchesUrl(final String url) {
        return new RequestFilter() {
            @Override
            public boolean listensFor(Request request) {
                return request.url().toString().equals(url);
            }
        };
    }

    private RequestFilters() {
        throw new AssertionError("No instances.");
    }
}
