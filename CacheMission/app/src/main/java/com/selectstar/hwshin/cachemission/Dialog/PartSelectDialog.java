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
import android.widget.TextView;

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
    private TextView reallyNoMoreTask;
    private int examType;
    private String taskDifficulty;
    private int partNumber;
    private PartAdapter mAdapter;
    private int asyncNum = 0;
    private boolean taskIsExist = false;

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
        reallyNoMoreTask = findViewById(R.id.reallyNoMoreTask);
        RecyclerView partRecycler = findViewById(R.id.partRecycler);

        ArrayList<Integer> idList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<Integer> maskList = new ArrayList<>();
        ArrayList<Integer> maskImageList = new ArrayList<>();
        ArrayList<String> maskTextList = new ArrayList<>();
        ArrayList<Integer> levelCountList = new ArrayList<>();
        ArrayList<Integer> levelMaxList = new ArrayList<>();
        ArrayList<Boolean> testingList = new ArrayList<>();
        ArrayList<String> stateList=new ArrayList<>();
        mAdapter = new PartAdapter((PatherActivity)context, idList, nameList, maskList, maskImageList, maskTextList, levelCountList, levelMaxList, testingList, stateList, this);
        partRecycler.setLayoutManager(new GridLayoutManager(context,2));
        partRecycler.setAdapter(mAdapter);

        System.out.println("테타 : "+taskType);
        System.out.println("테디 : "+taskDifficulty);

        if((taskType.equals("BOXCROP") || taskType.equals("BOXCROPEXAM")) && taskDifficulty.equals("EASY")) {
            partNumber = 100;
            asyncNum = 10;
            checkPossible(partNumber + 1, idList, R.drawable.part_g, nameList, "부품 G", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList, stateList);
            checkPossible(partNumber + 2, idList, R.drawable.part_pre, nameList,"전봇대 부품들", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList, stateList);
            checkPossible(partNumber + 3, idList, R.drawable.part_pole, nameList,"전봇대", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList, stateList);
            checkPossible(partNumber + 4, idList, R.drawable.part_tree, nameList,"나무", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList, stateList);
            checkPossible(partNumber + 5, idList, R.drawable.part_transformer, nameList,"변압기", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList, stateList);
            checkPossible(partNumber + 6, idList, R.drawable.part_a, nameList,"부품 A", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList, stateList);
            checkPossible(partNumber + 7, idList, R.drawable.part_b, nameList,"부품 B", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList, stateList);
            checkPossible(partNumber + 8, idList, R.drawable.part_c, nameList,"부품 C", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList, stateList);
            checkPossible(partNumber + 9, idList, R.drawable.part_d, nameList,"부품 D", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList, stateList);
            checkPossible(partNumber + 10, idList, R.drawable.part_e, nameList,"부품 E", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList, stateList);
        }
        if((taskType.equals("BOXCROP") || taskType.equals("BOXCROPEXAM")) && taskDifficulty.equals("NORMAL")) {
            partNumber = 200;
            asyncNum = 5;
            checkPossible(partNumber + 6, idList, R.drawable.part_a, nameList,"부품 A", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList,stateList);
            checkPossible(partNumber + 7, idList, R.drawable.part_b, nameList,"부품 B", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList,stateList);
            checkPossible(partNumber + 8, idList, R.drawable.part_c, nameList,"부품 C", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList,stateList);
            checkPossible(partNumber + 9, idList, R.drawable.part_d, nameList,"부품 D", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList,stateList);
            checkPossible(partNumber + 10, idList, R.drawable.part_e, nameList,"부품 E", maskList, maskImageList, maskTextList, levelCountList, levelMaxList,testingList,stateList);
        }
    }

    private void checkPossible(int partNum, final ArrayList<Integer> idList, final Integer idListItem,
                               final ArrayList<String> nameList, final String nameListItem,
                               final ArrayList<Integer> maskList, final ArrayList<Integer> maskImageList,
                               final ArrayList<String> maskTextList, final ArrayList<Integer> levelCountList,
                               final ArrayList<Integer> levelMaxList,final ArrayList<Boolean> testingList,
                               final ArrayList<String> stateList) {

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
                    asyncNum--;
                    try {
                        JSONObject resultTemp = new JSONObject(result);
                        System.out.println("테스크 어베일 결과 : "+result);

                        //idList, nameList 넣기
                        idList.add(idListItem);
                        nameList.add(nameListItem);

                        //levelCountList, levelMaxList 넣기
                        if(taskType.contains("EXAM")) {
                            levelCountList.add((Integer) 123);//일단은 임의의 숫자. (수정요망) EXAM에서는 레벨이 없기 때문에 얘를 띄울 필요가 없다.
                            levelMaxList.add((Integer) 456);
                        }else{
                            levelCountList.add((Integer) resultTemp.get("level_count"));
                            levelMaxList.add((Integer) resultTemp.get("level_max"));
                        }

                        //idList, testingList, stateList, maskList, maskImageList, maskTextList 넣기;
                        if(resultTemp.get("success").toString().equals("true")){
                            taskIsExist = true;
                            testingList.add(false);
                            stateList.add("proceed");
                            maskList.add(R.drawable.color_transparency);
                            maskImageList.add(R.drawable.color_transparency);
                            maskTextList.add("");
                        }else{
                            Log.d("실패!", partNumTemp + ", " +resultTemp.toString());
                            if(resultTemp.get("message").toString().equals("black")){
                                taskIsExist = true;
                                testingList.add(false);
                                stateList.add("이 부품에 대해 블랙 처리되었습니다.");
                                maskList.add(R.drawable.color_blacktransparency);
                                maskImageList.add(R.drawable.partselectdialog_black);
                                maskTextList.add("작업 자격 박탈");
                            }else if(resultTemp.get("message").toString().equals("nomore")){//안띄우는걸로 타결
                                if(asyncNum == 0){//모든 부품 로딩이 끝났다면 '가능한 부품 로딩중..'삭제
                                    reallyNoMoreTask.setText("남은 작업이 없습니다.");
                                }
                                idList.remove(idList.size() - 1);
                                nameList.remove(nameList.size() - 1);
                                levelCountList.remove(levelCountList.size() - 1);
                                levelMaxList.remove(levelMaxList.size() - 1);
                            }else if(resultTemp.get("message").toString().equals("needtest")){
                                taskIsExist = true;
                                testingList.add(true);
                                stateList.add("먼저 이 부품에 대한 테스트를 통과하셔야 합니다.");
                                maskList.add(R.drawable.color_blueblackgradient);
                                maskImageList.add(R.drawable.color_transparency);
                                maskTextList.add("테스트 받기");
                            }else if(resultTemp.get("message").toString().equals("exceed")){
                                taskIsExist = true;
                                testingList.add(false);
                                stateList.add("회원님의 랭크에 해당하는 작업 횟수를 모두 소진하였습니다.");
                                maskList.add(R.drawable.color_blacktransparency);
                                maskImageList.add(R.drawable.partselectdialog_exceed);
                                maskTextList.add("미션 검사 중");
                            }else if(resultTemp.get("message").toString().equals("needcert")){
                                taskIsExist = true;
                                testingList.add(false);
                                stateList.add("먼저 검수 자격을 얻어야 진행할 수 있습니다.");
                                maskList.add(R.drawable.color_blacktransparency);
                                maskImageList.add(R.drawable.partselectdialog_needcert);
                                maskTextList.add("작업 조건 필요");
                            }else{
                                taskIsExist = true;
                                testingList.add(false);
                                stateList.add("잘못된 접근");
                                maskList.add(R.drawable.color_transparency);
                                maskImageList.add(R.drawable.color_transparency);
                                maskTextList.add("");
                            }
                        }
                        if(asyncNum == 0 && taskIsExist){//모든 부품 로딩이 끝났다면 '가능한 부품 로딩중..'삭제
                            reallyNoMoreTask.setText("");
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


