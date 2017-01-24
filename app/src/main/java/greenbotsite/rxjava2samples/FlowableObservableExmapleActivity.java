package greenbotsite.rxjava2samples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

/**
 * Created by gaurav on 24/1/17.
 */

public class FlowableObservableExmapleActivity extends AppCompatActivity {

    @BindView(R.id.btn_start)
    Button start;

    @BindView(R.id.tv_one)
    TextView textView;

    Timber.Tree debug = new Timber.DebugTree();

    DisposableSubscriber<Integer> subscriber;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FlowableObservableExmapleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.plant(debug);

        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);


    }

    public Flowable<Integer> getObservable() {

        return Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                final View.OnTouchListener listener = (v, event) -> {
                    emitter.onNext((int) event.getX());
                    return false;
                };

                findViewById(R.id.ll_container).setOnTouchListener(listener);

                emitter.setCancellable(() -> {
                    Timber.d(("setting touch listener as null"));
                    findViewById(R.id.ll_container).setOnTouchListener(null);
                });
            }
        }, BackpressureStrategy.DROP);


    }

    @OnClick(R.id.btn_start)
    public void start() {


        subscriber = getSubscriber();

        getObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);

    }

    @NonNull
    private DisposableSubscriber<Integer> getSubscriber() {
        return new DisposableSubscriber<Integer>() {
            @Override
            public void onNext(Integer integer) {
                Timber.d("onNext ");
                textView.setText("Touching at x " + String.valueOf(integer));
            }

            @Override
            public void onError(Throwable t) {
                textView.setText("onError");
                textView.append(Utils.LINE_DELIMITER);
            }

            @Override
            public void onComplete() {
                textView.setText("onComplete");
                textView.append(Utils.LINE_DELIMITER);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscriber != null && !subscriber.isDisposed())
            subscriber.dispose();

        Timber.uproot(debug);
    }


}
