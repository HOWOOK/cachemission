package com.selectstar.hwshin.cachemission.DataStructure;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.R;
import static android.support.constraint.Constraints.TAG;

public class AsyncTaskCancelTimerTask extends CountDownTimer {
    private AsyncTask asyncTask;
    private WaitHttpRequest waitHttpRequest;
    private boolean interrupt;
    private Activity mActivity;

    public AsyncTaskCancelTimerTask(AsyncTask asyncTask, long startTime, long interval) {
        super(startTime, interval);
        this.asyncTask = asyncTask;
    }

    public AsyncTaskCancelTimerTask(AsyncTask asyncTask, long startTime, long interval, boolean interrupt, Activity activity) {
        super(startTime, interval);
        this.asyncTask = asyncTask;
        this.interrupt = interrupt;
        this.mActivity=activity;

    }

    @Override
    public void onTick(long millisUntilFinished) {
        Log.d(TAG, "onTick..");

        if(asyncTask == null) {
            this.cancel();
            return;
        }

        if(asyncTask.isCancelled())
            this.cancel();

        if(asyncTask.getStatus() == AsyncTask.Status.FINISHED)
            this.cancel();
    }

    @Override
    public void onFinish() {
        Log.d(TAG, "onTick..");

        if(asyncTask == null || asyncTask.isCancelled() )
            return;

        try {
            if(asyncTask.getStatus() == AsyncTask.Status.FINISHED)
                return;

            if(asyncTask.getStatus() == AsyncTask.Status.PENDING ||
                    asyncTask.getStatus() == AsyncTask.Status.RUNNING ) {
                Toast.makeText(mActivity,"네트워크연결이 불안정합니다. 연결상태를 확인하고 앱을 다시 실행해 주세요.",Toast.LENGTH_LONG).show();

                waitHttpRequest = (WaitHttpRequest)asyncTask;
                if(waitHttpRequest.httpDialog!=null)
                    waitHttpRequest.httpDialog.dismiss();
                waitHttpRequest.httpDialogSomethingOptimizationFailed.dismiss();

                asyncTask.cancel(interrupt);

            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}