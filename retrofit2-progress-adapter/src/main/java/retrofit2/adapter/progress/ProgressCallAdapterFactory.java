package retrofit2.adapter.progress;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import okhttp3.progress.HttpProgressInterceptor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class ProgressCallAdapterFactory extends CallAdapter.Factory {

    private final HttpProgressInterceptor progressInterceptor;
    private final Executor executor;

    private ProgressCallAdapterFactory(HttpProgressInterceptor progressInterceptor) {
        this.progressInterceptor = progressInterceptor;
        if (platformIsAndroid()) {
            this.executor = new MainThreadExecutor();
        } else {
            this.executor = new Executor() {
                @Override
                public void execute(Runnable command) {
                    command.run();
                }
            };
        }
    }

    private boolean platformIsAndroid() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return true;
            }
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    public static ProgressCallAdapterFactory create(HttpProgressInterceptor progressInterceptor) {
        return new ProgressCallAdapterFactory(progressInterceptor);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != ProgressCall.class) {
            return null;
        }
        final Type responseType = Utils.getCallResponseType(returnType);
        return new CallAdapter<Object, ProgressCall<?>>() {
            @Override public Type responseType() {
                return responseType;
            }

            @Override public ProgressCall<Object> adapt(Call<Object> call) {
                return new ProgressCall<>(progressInterceptor, executor, call);
            }
        };
    }

    private static class MainThreadExecutor implements Executor {

        private static final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }
}
