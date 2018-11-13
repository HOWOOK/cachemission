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
    private  PartAdapter mAdapter;

    private ImageView cancelbtn;
    private ImageView partPole;
    private ImageView partTree;
    private ImageView partTransformer;
    private ConstraintLayout dialogCL;

    public PartSelectDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public PartSelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public PartSelectDialog(@NonNull Context context, int themeResId, String taskID ) {
        super(context, themeResId);
        this.context = context;
        this.taskID = taskID;
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
        mAdapter = new PartAdapter((PatherActivity)context, idList, nameList,this);
        partRecycler.setLayoutManager(new GridLayoutManager(context,2));
        partRecycler.setAdapter(mAdapter);

        idList.add(R.drawable.part_g);
        nameList.add("부품 G");
        idList.add(R.drawable.part_pre);
        nameList.add("전봇대 부품들");
        idList.add(R.drawable.part_pole);
        nameList.add("전봇대");
        idList.add(R.drawable.part_tree);
        nameList.add("나무");
        idList.add(R.drawable.part_transformer);
        nameList.add("변압기");
        idList.add(R.drawable.part_a);
        nameList.add("부품 A");
        idList.add(R.drawable.part_b);
        nameList.add("부품 B");
        idList.add(R.drawable.part_c);
        nameList.add("부품 C");
        idList.add(R.drawable.part_d);
        nameList.add("부품 D");
        idList.add(R.drawable.part_e);
        nameList.add("부품 E");

//        checkPossibleAdd(1, idList, nameList);
//        checkPossibleAdd(2, idList, nameList);
//        checkPossibleAdd(3, idList, nameList);
//        checkPossibleAdd(4, idList, nameList);
//        checkPossibleAdd(5, idList, nameList);
//        checkPossibleAdd(6, idList, nameList);
//        checkPossibleAdd(7, idList, nameList);
//        checkPossibleAdd(8, idList, nameList);
//        checkPossibleAdd(9, idList, nameList);
//        checkPossibleAdd(10, idList, nameList);

    }

//    private void checkPossibleAdd(final int partNum, final ArrayList<Integer> idList, final ArrayList<String> nameList) {
//
//        JSONObject param = new JSONObject();
//
//        try {
//            param.put("taskID", taskID);
//            param.put("version", "9.9.9"); //버전은 의미 없어서 임의값
//            param.put("option", partNum);
//
//            SharedPreferences token = context.getSharedPreferences("token", 0);//mode 0 means MODE_PRIVATE
//            final String loginToken = token.getString("loginToken","");
//
//            HurryHttpRequest asyncTask = new HurryHttpRequest(context){
//                @Override
//                protected void onPostExecute(Object o) {
//                    super.onPostExecute(o);
//                    try {
//                        JSONObject resultTemp = new JSONObject(result);
//                        if(resultTemp.get("success").toString().equals("true")){
//                            Log.d("성공!", "짜짠");
//
//                            if(partNum == 1){
//                                idList.add(R.drawable.part_g);
//                                nameList.add("부품 G");
//                            }
//                            if(partNum == 2){
//                                idList.add(R.drawable.part_pre);
//                                nameList.add("전봇대 부품들");
//                            }
//                            if(partNum == 3){
//                                idList.add(R.drawable.part_pole);
//                                nameList.add("전봇대");
//                            }
//                            if(partNum == 4){
//                                idList.add(R.drawable.part_tree);
//                                nameList.add("나무");
//                            }
//                            if(partNum == 5){
//                                idList.add(R.drawable.part_transformer);
//                                nameList.add("변압기");
//                            }
//                            if(partNum == 6){
//                                idList.add(R.drawable.part_a);
//                                nameList.add("부품 A");
//                            }
//                            if(partNum == 7){
//                                idList.add(R.drawable.part_b);
//                                nameList.add("부품 B");
//                            }
//                            if(partNum == 8){
//                                idList.add(R.drawable.part_c);
//                                nameList.add("부품 C");
//                            }
//                            if(partNum == 9){
//                                idList.add(R.drawable.part_d);
//                                nameList.add("부품 D");
//                            }
//                            if(partNum == 10){
//                                idList.add(R.drawable.part_e);
//                                nameList.add("부품 E");
//                            }
//
//                        }else{
//                            Log.d("실패!", partNum + ", " +resultTemp.toString());
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    mAdapter.notifyDataSetChanged();
//                }
//
//            };
//            asyncTask.execute(context.getString(R.string.mainurl) + "/testing/taskValid", param, loginToken);
//
//
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

}


