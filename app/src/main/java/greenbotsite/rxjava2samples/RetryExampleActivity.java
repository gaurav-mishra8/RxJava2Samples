package greenbotsite.rxjava2samples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static greenbotsite.rxjava2samples.Utils.LINE_DELIMITER;

/**
 * Created by gaurav on 18/1/17.
 */
public class RetryExampleActivity extends AppCompatActivity {

    @BindView(R.id.btn_start)
    Button start;

    @BindView(R.id.tv_one)
    TextView textView;

    CompositeDisposable compositeDisposable;
    Timber.Tree debug = new Timber.DebugTree();
    private int clickcnt;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RetryExampleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.plant(debug);

        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);

        compositeDisposable = new CompositeDisposable();

    }

    @OnClick(R.id.btn_start)
    public void start() {

        clickcnt++;

        compositeDisposable.add(createObservable()
                .retryWhen(throwableObservable -> throwableObservable
                        .zipWith(Observable.range(1, 3), (throwable, integer) -> integer)
                        .flatMap(new Function<Integer, ObservableSource<Long>>() {
                            @Override
                            public ObservableSource<Long> apply(Integer integer) throws Exception {
                                long x = (long) Math.pow(2, integer);
                                Timber.d("waiting before retrying for " + x + " seconds");
                                return Observable.timer(x, TimeUnit.SECONDS);
                            }
                        }))
                .subscribeWith(getObserver()));
    }

    @NonNull
    private DisposableObserver<String> getObserver() {

        DisposableObserver<String> disposableObserver = new DisposableObserver<String>() {

            // override onstart to check when observer is attached
            @Override
            protected void onStart() {
                super.onStart();
                Timber.d("Observer : onStart ");
            }

            @Override
            public void onNext(String s) {
                textView.append("onNext " + s.toString());
                textView.append(LINE_DELIMITER);
                Timber.d("Observer : onNext ");
            }

            @Override
            public void onError(Throwable e) {
                textView.append("onError " + e);
                textView.append(LINE_DELIMITER);
                Timber.e("Observer : onError ");
            }

            @Override
            public void onComplete() {
                textView.append("onComplete");
                textView.append(LINE_DELIMITER);
                Timber.d("Observer : onComplete ");
            }
        };

        Timber.d("Creating Observer(Subscriber) ..." + disposableObserver.toString());

        return disposableObserver;
    }

    private Observable<String> createObservable() {

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {

                if (!e.isDisposed()) {
                    Timber.d("inside observable emitting items..clickcnt is " + clickcnt);
                    if (clickcnt < 3)
                        e.onError(new RuntimeException("it failed"));
                    else {
                        e.onNext("hello " + String.valueOf(clickcnt));
                        e.onComplete();
                    }
                } else {
                    e.onError(new IllegalStateException());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Timber.d("Clearing Observables...count=" + compositeDisposable.size());
        compositeDisposable.clear();
        Timber.uproot(debug);
    }


}
