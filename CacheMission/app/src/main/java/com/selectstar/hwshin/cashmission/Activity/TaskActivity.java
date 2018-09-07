package com.selectstar.hwshin.cashmission.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.selectstar.hwshin.cashmission.DataStructure.Controller.Controller;
import com.selectstar.hwshin.cashmission.DataStructure.HttpRequest;
import com.selectstar.hwshin.cashmission.DataStructure.TaskView.TaskView;
import com.selectstar.hwshin.cashmission.DataStructure.UIHashmap;
import com.selectstar.hwshin.cashmission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskActivity extends AppCompatActivity {

    Intent intent;
    int controllerID, taskViewID;
    String mId;
    TaskView mTaskView;
    Controller mController;
    int[][] mParameter;
    String tempsrcURI="foobar";
    String tasktitle;
    String buttons;
    private UIHashmap uiHashmap;
    String tasktype;
    Dialog explainDialog;

    //사투리특별전용옵션
    static String region_dialect;

    @Override
    protected void onResume() {
        super.onResume();

        //사투리선택해야하는 테스크면 선택하게 만들어야함
        String region;
        SharedPreferences tasktoken = getSharedPreferences("tasktoken", MODE_PRIVATE);
        SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
        region = explain.getString("region",null);
        if((tasktype.equals("DIALECT") || tasktype.equals("RECORD"))&& tasktoken.getInt(tasktype + "tasktoken",0) == 100 && region==null){
            Intent intent_region = new Intent(TaskActivity.this, RegionActivity.class);
            intent_region.putExtra("wanttochange","false");
            startActivity(intent_region);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //지역 재 선택을 위한 인터페이스
        if(tasktype.equals("DIALECT") || tasktype.equals("RECORD")) {
            String region;
            TextView regionText = findViewById(R.id.regionText);
            SharedPreferences explain = getSharedPreferences("region", MODE_PRIVATE);
            region = explain.getString("region", null);

            if (region != null)
                regionText.setText("[선택지역] " + region);

            regionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_region = new Intent(TaskActivity.this, RegionActivity.class);
                    intent_region.putExtra("wanttochange","true");
                    startActivity(intent_region);
                }
            });
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        //캡쳐방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);


        SharedPreferences token = getSharedPreferences("token",MODE_PRIVATE);
        String stringtoken;
        stringtoken = token.getString("logintoken",null);
        if(stringtoken==null){
            stringtoken="";
        }
        /*
        *intent로 부터 받아와야할 것 :   1. 어떤 controller를 사용하는지
        * 2. 어떤 taskview를 사용하는지  3. 두개의 constraint 관계는 어떤지
        */
        explainDialog = new Dialog(this);
        intent = getIntent();
        uiHashmap = new UIHashmap();
        mId=(String)intent.getStringExtra("taskid");
        mTaskView = (TaskView) uiHashmap.taskViewHashMap.get(intent.getStringExtra("taskview"));
        mController = (Controller) uiHashmap.controllerHashMap.get(intent.getStringExtra("controller"));
        mParameter =  (int[][]) uiHashmap.taskHashMap.get(intent.getStringExtra("tasktype"));
        tasktitle = intent.getStringExtra("tasktitle");
        buttons= intent.getStringExtra("buttons");

        TextView mtasktitle = findViewById(R.id.tasktitletext);
        mtasktitle.setText(tasktitle);
        taskViewID = mTaskView.taskViewID;
        controllerID = mController.controllerID;

        tasktype = intent.getStringExtra("tasktype");

        // TaskView Inflating
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent1 = (ViewGroup) findViewById(R.id.taskview);
        inflater.inflate(taskViewID, parent1);
        ViewGroup parent2 = (ViewGroup) findViewById(R.id.controller);
        inflater.inflate(controllerID, parent2);

        //TaskView와 Controller의 constraint 설정
        //ConstraintSet.TOP -> 3 // ConstraintSet.BOTTOM -> 4
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout =  (ConstraintLayout) findViewById(R.id.taskConstLayout);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.taskview, ConstraintSet.TOP, mParameter[1][0], mParameter[1][1]);
        constraintSet.connect(R.id.taskview, ConstraintSet.BOTTOM, mParameter[2][0], mParameter[2][1] );
        constraintSet.connect(R.id.controller, ConstraintSet.TOP, mParameter[3][0], mParameter[3][1] );
        constraintSet.connect(R.id.controller, ConstraintSet.BOTTOM, mParameter[4][0], mParameter[4][1]);
        constraintSet.applyTo(constraintLayout);

        //TaskView의 weight설정 (default == 10)
        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) parent1.getLayoutParams();
        params1.verticalWeight = mParameter[0][0];
        parent1.setLayoutParams(params1);
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) parent2.getLayoutParams();
        params2.verticalWeight = mParameter[5][0];
        parent2.setLayoutParams(params2);

        mTaskView.setParent(this,intent);
        mController.setParent(this,intent);
        mController.usingactivity=this;
        mController.mtaskview=mTaskView;


        //TaskView에 source설정 (보통은 srcTaskView1에만 들어가며 Text의 경우 2개를 받는다)
        View srcTaskView1 = (View) findViewById(R.id.srcview);
        View srcTaskView2 = null;
        TextView goldnow=findViewById(R.id.goldnow);
        TextView goldpre=findViewById(R.id.goldpre);
        if(intent.getStringExtra("taskview").equals("text")) {
            srcTaskView2 = (View) findViewById(R.id.srcview2);
        }
        mTaskView.setContent(mId, tempsrcURI, this, tasktype, 0, srcTaskView1, srcTaskView2, goldnow, goldpre);

        //Controller에 source설정
        View view = findViewById(R.id.controller);
        mController.setLayout(mId,view,getApplicationContext(),tasktype,intent,buttons);

        //해당 task가 처음이라면 설명서 띄워주는 것
        SharedPreferences tasktoken = getSharedPreferences("tasktoken", MODE_PRIVATE);
        if(tasktoken.getInt(tasktype + "tasktoken",0) == 100){
            //do nothing
        }else{
            Intent intent_taskExplain = new Intent(TaskActivity.this, TaskExplainActivity.class);
            intent_taskExplain.putExtra("tasktype", tasktype);
            startActivity(intent_taskExplain);
        }

        //물음표버튼누르면 설명서 띄워주는것
        ImageView howbtn = findViewById(R.id.howbtn);
        howbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_taskExplain = new Intent(TaskActivity.this, TaskExplainActivity.class);
                intent_taskExplain.putExtra("tasktype", tasktype);
                startActivity(intent_taskExplain);
            }
        });

        //돈 플러스되는거 에니메이션
        if(intent.getStringExtra("maybe_up")!=null  && intent.getStringExtra("gold_up")!=null){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.goldtranslate);
            if(Integer.parseInt(intent.getStringExtra("gold_up")) == 0){//지급예정금액만 애니메이팅
                TextView maybe_up = findViewById(R.id.goldpre_anim);
                maybe_up.setText("+ \uFFE6"+intent.getStringExtra("maybe_up"));
                maybe_up.startAnimation(animation);
            }else{//현재금액만 애니메이팅
                TextView gold_up = findViewById(R.id.goldnow_anim);
                gold_up.setText("+ \uFFE6"+intent.getStringExtra("gold_up"));
                gold_up.startAnimation(animation);
            }
        }

        if(intent.getStringExtra("bonus_up")!=null){
            if(!intent.getStringExtra("bonus_up").equals("0"))
                Toast.makeText(getApplicationContext(),"일일 미션을 완료했습니다! \n(\uFFE6"+intent.getStringExtra("bonus_up")+" 추가 획득)",Toast.LENGTH_SHORT).show();
        }



    }

    public String getBase64String(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    super.onActivityResult(requestCode, resultCode, data);
//ImageView iv=findViewById(R.id.pagerimage);

//    Bitmap btm=(Bitmap)data.getExtras().get("data");
    //iv.setImageBitmap(btm);
//Uri uri=data.getData();
//String uristring=uri.toString();



  //  SharedPreferences bitmap = getSharedPreferences("bitmap", MODE_PRIVATE);
  //  SharedPreferences.Editor editor = bitmap.edit();
    //logintoken이라는 key값으로 token을 저장한다.
   // editor.putString("bitmap", getBase64String(btm));
  //  editor.commit();


    View srcTaskView1 = (View) findViewById(R.id.srcview);
    View srcTaskView2 = null;
    mTaskView.setContent(mId, "donthttp", this, tasktype, 0, srcTaskView1);
}
}
