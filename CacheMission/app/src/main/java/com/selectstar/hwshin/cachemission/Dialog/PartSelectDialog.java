package com.selectstar.hwshin.cachemission.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.Adapter.PartAdapter;
import com.selectstar.hwshin.cachemission.DataStructure.HurryHttpRequest;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PartSelectDialog extends Dialog{

    private Context context;
    private String taskID;
    private String taskType;
    private String taskDifficulty;
    private int partNumber;
    private  PartAdapter mAdapter;

    private ImageView cancelbtn;
    private ConstraintLayout dialogCL;

    public PartSelectDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public PartSelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public PartSelectDialog(@NonNull Context context, int themeResId, String taskID, String taskType, String taskDifficulty) {
        super(context, themeResId);
        this.context = context;
        this.taskID = taskID;
        this.taskType = taskType;
        this.taskDifficulty = taskDifficulty;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_partselect);
        getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.colorPrimary));
        dialogCL = findViewById(R.id.dialogCL);
        cancelbtn = findViewById(R.id.cancelbtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PartSelectDialog.this.dismiss();
            }
        });
        RecyclerView partRecycler = findViewById(R.id.partRecycler);

        ArrayList<Integer> idList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<Integer> levelCountList = new ArrayList<>();
        ArrayList<Integer> levelMaxList = new ArrayList<>();
        mAdapter = new PartAdapter((PatherActivity)context, idList, nameList, levelCountList, levelMaxList, this);
        partRecycler.setLayoutManager(new GridLayoutManager(context,2));
        partRecycler.setAdapter(mAdapter);

        if(taskType.equals("BOXCROP") && taskDifficulty.equals("EASY")) {
            partNumber = 100;
            checkPossible(partNumber + 1, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 2, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 3, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 4, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 5, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 6, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 7, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 8, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 9, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 10, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 11, idList, nameList, levelCountList, levelMaxList);
        }
        if(taskType.equals("BOXCROP") && taskDifficulty.equals("NORMAL")) {
            partNumber = 200;
            checkPossible(partNumber + 6, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 7, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 8, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 9, idList, nameList, levelCountList, levelMaxList);
            checkPossible(partNumber + 10, idList, nameList, levelCountList, levelMaxList);
        }
    }

    private void checkPossible(final int partNum, final ArrayList<Integer> idList, final ArrayList<String> nameList, final  ArrayList<Integer> levelCountList, final ArrayList<Integer> levelMaxList) {
        JSONObject param = new JSONObject();
        try {
            param.put("taskID", taskID);
            param.put("version", "9.9.9"); //버전은 의미 없어서 임의값
            param.put("option", partNum);

            SharedPreferences token = context.getSharedPreferences("token", 0);//mode 0 means MODE_PRIVATE
            final String loginToken = token.getString("loginToken","");

            HurryHttpRequest asyncTask = new HurryHttpRequest(context){
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    try {
                        JSONObject resultTemp = new JSONObject(result);
                        System.out.println("테스크 어베일 결과 : "+result);
                        if(resultTemp.get("success").toString().equals("true")){
                            levelCountList.add((Integer) resultTemp.get("level_count"));
                            levelMaxList.add((Integer) resultTemp.get("level_max"));
                            if(partNum == 1){
                                idList.add(R.drawable.part_g);
                                nameList.add("부품 G");
                            }else if(partNum == 2){
                                idList.add(R.drawable.part_pre);
                                nameList.add("전봇대 부품들");
                            }else if(partNum == 3){
                                idList.add(R.drawable.part_pole);
                                nameList.add("전봇대");
                            }else if(partNum == 4){
                                idList.add(R.drawable.part_tree);
                                nameList.add("나무");
                            }else if(partNum == 5){
                                idList.add(R.drawable.part_transformer);
                                nameList.add("변압기");
                            }else if(partNum == 6){
                                idList.add(R.drawable.part_a);
                                nameList.add("부품 A");
                            }else if(partNum == 7){
                                idList.add(R.drawable.part_b);
                                nameList.add("부품 B");
                            }else if(partNum == 8){
                                idList.add(R.drawable.part_c);
                                nameList.add("부품 C");
                            }else if(partNum == 9){
                                idList.add(R.drawable.part_d);
                                nameList.add("부품 D");
                            }else if(partNum == 10){
                                idList.add(R.drawable.part_e);
                                nameList.add("부품 E");
                            }
                        }else{
                            Log.d("실패!", partNum + ", " +resultTemp.toString());
                            if(resultTemp.get("message").toString().equals("needtest")){
                                //TODO : jump to testActivity
                            }else if(resultTemp.get("message").toString().equals("nomore")){
                                Toast.makeText(context, "남은 테스크가 없습니다.", Toast.LENGTH_SHORT).show();
                            }else if(resultTemp.get("message").toString().equals("black")){
                                Toast.makeText(context, "해당 테스크를 더 이상 진행 할 수 없는 유저입니다.", Toast.LENGTH_SHORT).show();
                            }else if(resultTemp.get("message").toString().equals("exceed")){
                                Toast.makeText(context, "현재 레벨에서 한번에 할 수 있는 테스크를 모두 완료하였습니다.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "false", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mAdapter.notifyDataSetChanged();
                }

            };
            asyncTask.execute(context.getString(R.string.mainurl) + "/testing/taskValid", param, loginToken);





        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}


