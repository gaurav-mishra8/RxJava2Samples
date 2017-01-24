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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.AsyncSubject;
import timber.log.Timber;

import static greenbotsite.rxjava2samples.Utils.LINE_DELIMITER;

/**
 * Created by gaurav on 24/1/17.
 */

public class AsyncSubjectExampleActivity extends AppCompatActivity {

    @BindView(R.id.btn_start)
    Button start;

    @BindView(R.id.tv_one)
    TextView textView;

    CompositeDisposable compositeDisposable;
    Timber.Tree debug = new Timber.DebugTree();

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AsyncSubjectExampleActivity.class);
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

        AsyncSubject<String> asyncSubject = AsyncSubject.create();

        asyncSubject.onNext("item 1");
        asyncSubject.onNext("item 2");

        DisposableObserver firstObserver = asyncSubject.subscribeWith(getFirstObserver());
        compositeDisposable.add(firstObserver);  // will receive the last value only i.e. item 4 and terminal onComplete

        asyncSubject.onNext("item 3");
        asyncSubject.onNext("item 4");

        DisposableObserver secondObsever = asyncSubject.subscribeWith(getDelayedObserver());
        compositeDisposable.add(secondObsever); // will receive the last value only i.e. item 4 and terminal onComplete

        asyncSubject.onComplete();
        //asyncSubject.onError(new RuntimeException("Crash!!")); // in case of error only onError gets called

    }

    private void unsubscribe() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
    }


    @NonNull
    private DisposableObserver<String> getFirstObserver() {

        DisposableObserver<String> disposableObserver = new DisposableObserver<String>() {

            // override onstart to check when observer is attached
            @Override
            protected void onStart() {
                super.onStart();
                Timber.d("Observer : onStart ");
            }

            @Override
            public void onNext(String s) {
                textView.append("FirstSubscriber : onNext " + s);
                textView.append(LINE_DELIMITER);
                Timber.d("FirstSubscriber :onNext ");
            }

            @Override
            public void onError(Throwable e) {
                textView.append("FirstSubscriber : onError " + e.getMessage());
                textView.append(LINE_DELIMITER);
                Timber.e("FirstSubscriber :onError ");
            }

            @Override
            public void onComplete() {
                textView.append("FirstSubscriber : onComplete");
                textView.append(LINE_DELIMITER);
                Timber.d("FirstSubscriber :onComplete ");
            }
        };

        Timber.d("Creating Observer(Subscriber) ..." + disposableObserver.toString());

        return disposableObserver;
    }


    @NonNull
    private DisposableObserver<String> getDelayedObserver() {

        DisposableObserver<String> disposableObserver = new DisposableObserver<String>() {

            // override onstart to check when observer is attached
            @Override
            protected void onStart() {
                super.onStart();
                Timber.d("DelayedSubscriber : onStart ");
            }

            @Override
            public void onNext(String s) {
                textView.append("DelayedSubscriber : onNext " + s);
                textView.append(LINE_DELIMITER);
                Timber.d("DelayedSubscriber : onNext ");
            }

            @Override
            public void onError(Throwable e) {
                textView.append("DelayedSubscriber : onError " + e.getMessage());
                textView.append(LINE_DELIMITER);
                Timber.e("DelayedSubscriber : onError ");
            }

            @Override
            public void onComplete() {
                textView.append("DelayedSubscriber : onComplete");
                textView.append(LINE_DELIMITER);
                Timber.d("DelayedSubscriber : onComplete ");
            }
        };

        Timber.d("Creating Observer(Subscriber) ..." + disposableObserver.toString());

        return disposableObserver;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Timber.d("Clearing Observables...count=" + compositeDisposable.size());
        unsubscribe();

        Timber.uproot(debug);
    }
}
