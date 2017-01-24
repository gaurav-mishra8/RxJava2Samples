package greenbotsite.rxjava2samples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by gaurav on 24/1/17.
 */

public class CompletableObserverExampleActivity extends AppCompatActivity {


    @BindView(R.id.btn_start)
    Button start;

    @BindView(R.id.tv_one)
    TextView textView;

    Timber.Tree debug = new Timber.DebugTree();

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CompletableObserverExampleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.plant(debug);

        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_start)
    public void start() {

        Completable.timer(2, TimeUnit.SECONDS).doOnComplete(() -> Timber.d("Action :  onComplete")).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete");
                        textView.append("onComplete");
                        textView.append(Utils.LINE_DELIMITER);
                    }

                    @Override
                    public void onError(Throwable e) {
                        textView.append("onError");
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Timber.uproot(debug);
    }


}
