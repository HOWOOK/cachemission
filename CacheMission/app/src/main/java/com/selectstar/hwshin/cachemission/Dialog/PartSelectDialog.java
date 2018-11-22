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
    private int examType;
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

    public PartSelectDialog(@NonNull Context context, int themeResId, String taskID, String taskType, int examType, String taskDifficulty) {
        super(context, themeResId);
        this.context = context;
        this.taskID = taskID;
        this.taskType = taskType;
        this.examType = examType;
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
        ArrayList<Boolean> testingList = new ArrayList<>();
        mAdapter = new PartAdapter((PatherActivity)context, idList, nameList, levelCountList, levelMaxList, this,testingList);
        partRecycler.setLayoutManager(new GridLayoutManager(context,2));
        partRecycler.setAdapter(mAdapter);

        System.out.println("테타 : "+taskType);
        System.out.println("테디 : "+taskDifficulty);

        if((taskType.equals("BOXCROP") || taskType.equals("BOXCROPEXAM")) && taskDifficulty.equals("EASY")) {
            partNumber = 100;
            checkPossible(partNumber + 1, idList, R.drawable.part_g, nameList,"부품 G", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 2, idList, R.drawable.part_pre, nameList,"전봇대 부품들", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 3, idList, R.drawable.part_pole, nameList,"전봇대", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 4, idList, R.drawable.part_tree, nameList,"나무", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 5, idList, R.drawable.part_transformer, nameList,"변압기", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 6, idList, R.drawable.part_a, nameList,"부품 A", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 7, idList, R.drawable.part_b, nameList,"부품 B", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 8, idList, R.drawable.part_c, nameList,"부품 C", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 9, idList, R.drawable.part_d, nameList,"부품 D", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 10, idList, R.drawable.part_e, nameList,"부품 E", levelCountList, levelMaxList,testingList);
        }
        if((taskType.equals("BOXCROP") || taskType.equals("BOXCROPEXAM")) && taskDifficulty.equals("NORMAL")) {
            partNumber = 200;
            checkPossible(partNumber + 6, idList, R.drawable.part_a, nameList,"부품 A", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 7, idList, R.drawable.part_b, nameList,"부품 B", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 8, idList, R.drawable.part_c, nameList,"부품 C", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 9, idList, R.drawable.part_d, nameList,"부품 D", levelCountList, levelMaxList,testingList);
            checkPossible(partNumber + 10, idList, R.drawable.part_e, nameList,"부품 E", levelCountList, levelMaxList,testingList);
        }
    }

    private void checkPossible(int partNum, final ArrayList<Integer> idList, final Integer idListItem, final ArrayList<String> nameList, final String nameListItem, final ArrayList<Integer> levelCountList, final ArrayList<Integer> levelMaxList,final ArrayList<Boolean> testingList) {
        JSONObject param = new JSONObject();
        try {
            param.put("taskID", taskID);
            if(taskType.contains("EXAM"))
                param.put("examType", examType);
            param.put("version", "9.9.9"); //버전은 의미 없어서 임의값
            param.put("option", partNum);

            SharedPreferences token = context.getSharedPreferences("token", 0);//mode 0 means MODE_PRIVATE
            final String loginToken = token.getString("loginToken","");

            final int partNumTemp = partNum;
            HurryHttpRequest asyncTask = new HurryHttpRequest(context){
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    try {
                        JSONObject resultTemp = new JSONObject(result);
                        System.out.println("테스크 어베일 결과 : "+result);

                        nameList.add(nameListItem);

                        if(taskType.contains("EXAM")) {
                            levelCountList.add((Integer) 123);//일단은 임의의 숫자. (수정요망) EXAM에서는 레벨이 없기 때문에 얘를 띄울 필요가 없다.
                            levelMaxList.add((Integer) 456);
                        }else{
                            levelCountList.add((Integer) resultTemp.get("level_count"));
                            levelMaxList.add((Integer) resultTemp.get("level_max"));
                        }

                        if(resultTemp.get("success").toString().equals("true")){
                            idList.add(idListItem);
                            testingList.add(false);
                        }else{
                            Log.d("실패!", partNumTemp + ", " +resultTemp.toString());
                            if(resultTemp.get("message").toString().equals("black")){
                                idList.add(R.drawable.examining_false);
                                testingList.add(false);
                            }else if(resultTemp.get("message").toString().equals("nomore")){
                                idList.add(R.drawable.examining_falsepush);
                                testingList.add(true);
                            }else if(resultTemp.get("message").toString().equals("needtest")){
                                idList.add(R.drawable.btn_x);
                                testingList.add(true);
                            }else if(resultTemp.get("message").toString().equals("exceed")){
                                idList.add(R.drawable.examining_truepush);
                                testingList.add(false);
                            }else {
                                idList.add(R.drawable.examining_true);
                                testingList.add(false);
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


