package greenbotsite.rxjava2samples;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_map)
    Button map;
    @BindView(R.id.btn_flatmap)
    Button flatmap;
    @BindView(R.id.btn_zip)
    Button zip;
    @BindView(R.id.btn_threading)
    Button threading;
    @BindView(R.id.btn_amb)
    Button amb;
    @BindView(R.id.btn_repeat)
    Button repeat;
    @BindView(R.id.btn_skip)
    Button skip;
    @BindView(R.id.btn_buffer)
    Button buffer;
    @BindView(R.id.btn_debounce)
    Button debounce;
    @BindView(R.id.btn_filter)
    Button filter;
    @BindView(R.id.btn_retry)
    Button retry;
    @BindView(R.id.btn_scan)
    Button scan;
    @BindView(R.id.btn_take)
    Button take;
    @BindView(R.id.btn_sub_replay)
    Button subReplay;
    @BindView(R.id.btn_sub_behaviour)
    Button subBehaviour;
    @BindView(R.id.btn_sub_publish)
    Button subPublish;
    @BindView(R.id.btn_sub_async)
    Button subAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        map.setOnClickListener(this);
        flatmap.setOnClickListener(this);
        zip.setOnClickListener(this);
        threading.setOnClickListener(this);
        amb.setOnClickListener(this);
        repeat.setOnClickListener(this);
        skip.setOnClickListener(this);
        buffer.setOnClickListener(this);
        debounce.setOnClickListener(this);
        filter.setOnClickListener(this);
        retry.setOnClickListener(this);
        scan.setOnClickListener(this);
        take.setOnClickListener(this);
        subAsync.setOnClickListener(this);
        subBehaviour.setOnClickListener(this);
        subPublish.setOnClickListener(this);
        subReplay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_map:
                MapExampleActivity.startActivity(this);
                break;
            case R.id.btn_flatmap:
                FlatMapExampleActivity.startActivity(this);
                break;
            case R.id.btn_zip:
                ZipExampleActivity.startActivity(this);
                break;
            case R.id.btn_threading:
                ThreadingExampleActivity.startActivity(this);
                break;
            case R.id.btn_amb:
                AmbExampleActivity.startActivity(this);
                break;
            case R.id.btn_repeat:
                RepeatExampleActivity.startActivity(this);
                break;
            case R.id.btn_skip:
                SkipExampleActivity.startActivity(this);
                break;
            case R.id.btn_debounce:
                DebounceExampleActivity.startActivity(this);
                break;
            case R.id.btn_filter:
                FilterExampleActivity.startActivity(this);
                break;
            case R.id.btn_retry:
                RetryExampleActivity.startActivity(this);
                break;
            case R.id.btn_buffer:
                BufferExampleActivity.startActivity(this);
                break;
            case R.id.btn_take:
                TakeExampleActivity.startActivity(this);
                break;
            case R.id.btn_scan:
                ScanExampleActivity.startActivity(this);
                break;
            case R.id.btn_sub_async:
                AsyncSubjectExampleActivity.startActivity(this);
                break;
            case R.id.btn_sub_behaviour:
                BehaviourSubjectExampleActivity.startActivity(this);
                break;
            case R.id.btn_sub_publish:
                PublishSubjectExampleActivity.startActivity(this);
                break;
            case R.id.btn_sub_replay:
                ReplaySubjectExampleActivity.startActivity(this);
                break;
        }

    }
}
