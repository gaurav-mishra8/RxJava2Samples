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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static greenbotsite.rxjava2samples.Utils.LINE_DELIMITER;

/**
 * Created by gaurav on 17/1/17.
 */

public class ThreadingExampleActivity extends AppCompatActivity {

    @BindView(R.id.btn_start)
    Button start;

    @BindView(R.id.tv_one)
    TextView textView;

    CompositeDisposable compositeDisposable;

    Timber.Tree debug = new Timber.DebugTree();

    private String savedString;

    private ProgressDialog progressBar;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ThreadingExampleActivity.class);
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

        showProgress();

        compositeDisposable.add(createObservable1()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .doOnNext(s -> saveToDb(s))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()));
    }

    private void showProgress() {
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Please wait");
        progressBar.show();
    }

    private void hideProgress() {
        if (progressBar != null && progressBar.isShowing())
            progressBar.dismiss();
    }

    private void saveToDb(String s) throws InterruptedException {
        Timber.d("writing to db......" + s + "..on thread " + Thread.currentThread().getName());
        Thread.sleep(3000);
        savedString = s;
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
        return Observable.defer(() -> {
            Timber.d("Creating Observable(Emitter) ...on thread " + Thread.currentThread().getName());
            Thread.sleep(2000);
            return Observable.just("hello");
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
