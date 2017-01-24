package greenbotsite.rxjava2samples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static greenbotsite.rxjava2samples.Utils.LINE_DELIMITER;

/**
 * Created by gaurav on 17/1/17.
 */

public class ZipExampleActivity extends AppCompatActivity {

    @BindView(R.id.btn_start)
    Button start;

    @BindView(R.id.tv_one)
    TextView textView;

    CompositeDisposable compositeDisposable;

    Timber.Tree debug = new Timber.DebugTree();

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ZipExampleActivity.class);
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
        compositeDisposable.add(Observable.zip(createObservable1(), createObservable2(), (s, integer) -> {
            String concat = s.concat(String.valueOf(integer));
            return concat;
        }).subscribeWith(getObserver()));
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

    private Observable<String> createObservable1() {
        Timber.d("Creating Observable(Emitter) ...");
        return Observable.just("one", "two", "three");
    }

    private Observable<Integer> createObservable2() {
        Timber.d("Creating Observable(Emitter) ...");
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

                if (!e.isDisposed()) {
                    e.onNext(2);
                    e.onNext(3);
                    e.onComplete();
                } else
                    e.onError(new IllegalStateException());

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
