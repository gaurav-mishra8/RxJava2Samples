package greenbotsite.rxjava2samples;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static greenbotsite.rxjava2samples.Utils.LINE_DELIMITER;

/**
 * Created by gaurav on 18/1/17.
 */

public class AmbExampleActivity extends AppCompatActivity {

    @BindView(R.id.btn_start)
    Button start;

    @BindView(R.id.tv_one)
    TextView textView;

    CompositeDisposable compositeDisposable;

    Timber.Tree debug = new Timber.DebugTree();

    private ProgressDialog progressDialog;

    private Thread threadOne;
    private Thread threadTwo;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AmbExampleActivity.class);
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

        killThreads();

        showProgress();

        compositeDisposable.add(Observable.ambArray(createObservable1(), createObservable2())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()));
    }

    private void showProgress() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);

        if (!progressDialog.isShowing()) {
            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
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
                hideProgress();
                textView.append("onError " + e);
                textView.append(LINE_DELIMITER);
                Timber.e("Observer : onError ");
            }

            @Override
            public void onComplete() {
                hideProgress();
                textView.append("onComplete");
                textView.append(LINE_DELIMITER);
                Timber.d("Observer : onComplete ");
            }
        };

        Timber.d("Creating Observer(Subscriber) ..." + disposableObserver.toString());

        return disposableObserver;
    }

    private Observable<String> createObservable1() {

        return Observable.defer(() -> Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> subscriber) throws Exception {

                Timber.d("Inside Observable.create 1 on thread..." + Thread.currentThread().getName());

                threadOne = new Thread(() -> {
                    try {
                        Timber.d("starting off new thread one..." + Thread.currentThread().getName());
                        Thread.sleep(getRandomTime());
                        if (!subscriber.isDisposed())
                            subscriber.onNext("hello from one");
                        subscriber.onComplete();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                });

                threadOne.start();
            }
        }));

    }

    private long getRandomTime() {
        return new Random().nextInt(200) + 1000;
    }

    private Observable<String> createObservable2() {

        return Observable.defer(() -> Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> subscriber) throws Exception {

                Timber.d("Inside Observable.create 2 on thread..." + Thread.currentThread().getName());

                threadTwo = new Thread(() -> {
                    try {
                        Timber.d("starting off new thread two..." + Thread.currentThread().getName());
                        Thread.sleep(getRandomTime());
                        if (!subscriber.isDisposed())
                            subscriber.onNext("hello from two");
                        subscriber.onComplete();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                });

                threadTwo.start();
            }
        }));

    }

    private void killThreads() {
        if (threadOne != null)
            threadOne.interrupt();

        if (threadTwo != null)
            threadTwo.interrupt();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("Clearing Observables...count=" + compositeDisposable.size());
        compositeDisposable.clear();
        killThreads();
        Timber.uproot(debug);
    }


}
