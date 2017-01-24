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
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by gaurav on 24/1/17.
 */

public class SingleObserverExampleActivity extends AppCompatActivity {

    @BindView(R.id.btn_start)
    Button start;

    @BindView(R.id.tv_one)
    TextView textView;

    Timber.Tree debug = new Timber.DebugTree();

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SingleObserverExampleActivity.class);
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

        Single single = new Single() {
            @Override
            protected void subscribeActual(SingleObserver observer) {
                observer.onSuccess("hello");
            }
        };

        single.subscribeWith(getObserver());

    }


    @NonNull
    private SingleObserver<String> getObserver() {

        SingleObserver<String> singleObserver = new SingleObserver<String>() {

            @Override
            public void onSubscribe(Disposable d) {
                Timber.d("onSubscribe");
            }

            @Override
            public void onSuccess(String s) {
                Timber.d("onSuccess");
                textView.append(s);
                textView.append(Utils.LINE_DELIMITER);
                textView.append("onSuccess");
            }

            @Override
            public void onError(Throwable e) {
                Timber.d("onError " + e);
            }
        };

        Timber.d("Creating Observer(Subscriber) ..." + singleObserver.toString());

        return singleObserver;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Timber.uproot(debug);
    }


}
