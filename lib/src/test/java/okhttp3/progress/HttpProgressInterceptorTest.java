package okhttp3.progress;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.Okio;
import okio.Sink;
import okio.Source;

import static org.mockito.Mockito.*;

public class HttpProgressInterceptorTest {

    @Rule
    public MockWebServer mockWebServer;

    private OkHttpClient httpClient;
    private HttpProgressInterceptor httpProgressInterceptor;

    @Before
    public void setup() {
        mockWebServer = new MockWebServer();
        httpProgressInterceptor = new HttpProgressInterceptor();
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(httpProgressInterceptor)
                .build();
    }

    @Test
    public void removed_listener_stops_receiving_progress() throws IOException {
        mockWebServer.enqueue(mockImage());
        ProgressListener listener = mock(ProgressListener.class);

        httpProgressInterceptor.addListener(listener);
        httpProgressInterceptor.removeListener(listener);

        consumeResponse(mockWebServer.url("v1/test/"));

        verifyZeroInteractions(listener);
    }

    @Test
    public void multiple_listeners_receive_progress() throws IOException {
        mockWebServer.enqueue(mockImage());
        ProgressListener listener1 = mock(ProgressListener.class);
        ProgressListener listener2 = mock(ProgressListener.class);

        httpProgressInterceptor.addListener(listener1);
        httpProgressInterceptor.addListener(listener2);
        consumeResponse(mockWebServer.url("/v1/test/"));

        verify(listener1, atLeastOnce()).onProgress(anyInt());
        verify(listener2, atLeastOnce()).onProgress(anyInt());
    }

    @Test
    public void listener_does_not_receive_progress_when_filtering() throws IOException {
        mockWebServer.enqueue(mockImage());
        ProgressListener listener = mock(ProgressListener.class);
        RequestFilter filter = mock(RequestFilter.class);
        when(filter.listensFor(any(Request.class))).thenReturn(false);

        httpProgressInterceptor.addListener(listener, filter);
        consumeResponse(mockWebServer.url("/v1/test"));

        verify(listener, never()).onProgress(anyInt());
    }

    private MockResponse mockImage() throws IOException {
        Buffer buffer = bufferTestImage();
        return new MockResponse()
                .setHeader("Content-Type", "image/png")
                .setBody(buffer);
    }

    private Buffer bufferTestImage() throws IOException {
        File file = new File("/Users/seanamos/Documents/Android/okhttp-progress-interceptor/lib/build/classes/test/image.png");
        InputStream is = new FileInputStream(file);
        Source source = Okio.source(is);
        Buffer buffer = new Buffer();
        buffer.writeAll(source);
        return buffer;
    }

    private void consumeResponse(HttpUrl url) throws IOException {
        Response response = httpClient.newCall(new Request.Builder().url(url).build()).execute();
        OutputStream stream = new ByteArrayOutputStream();
        Sink sink = Okio.sink(stream);
        response.body().source().readAll(sink);
        response.close();
        sink.close();
    }

}
