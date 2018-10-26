package com.selectstar.hwshin.cachemission.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.Activity.ExamActivity;
import com.selectstar.hwshin.cachemission.Activity.LoginActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListviewAdapter extends RecyclerView.Adapter<ListviewAdapter.ItemViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<JSONObject> mTaskList;
    private JSONObject userInfo;
    public void setUserInfo(JSONObject  userInfo) {
        this.userInfo = userInfo;
        userLoaded = true;
    }
    private int layout;
    private HashMap<String,Integer> iconIDMap;
    private Context mContext;
    private boolean userLoaded;
    private ListviewAdapter.ItemViewHolder mHolder;
    public ListviewAdapter(Context context, int layout, ArrayList<JSONObject> taskList,Context mContext){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mTaskList=taskList;
        this.layout=layout;
        this.userLoaded = false;
        this.iconIDMap = new HashMap<>();
        this.iconIDMap.put("OCR",R.drawable.main_icon_tl);
        this.iconIDMap.put("OCREXAM",R.drawable.main_icon_tl); //main_icon_te가 아직 없음
        //this.iconIDMap.put("PHOTO",R.drawable.photoicon); 옛날 아이콘
        this.iconIDMap.put("PHOTO",R.drawable.main_icon_ic);
        this.iconIDMap.put("PHOTOEXAM",R.drawable.main_icon_ie);
        //this.iconIDMap.put("VIDEO",R.drawable.tasktype_video); 옛날 아이콘
        this.iconIDMap.put("VIDEO",R.drawable.main_icon_vl);
        this.iconIDMap.put("VIDEOEXAM",R.drawable.main_icon_vl); //main_icon_ve이 아직 없음
        //this.iconIDMap.put("RECORD",R.drawable.tasktype_voice); 옛날 아이콘
        this.iconIDMap.put("RECORD",R.drawable.main_icon_sc);
        //this.iconIDMap.put("DIRECTRECORD",R.drawable.tasktype_voice); 옛날 아이콘
        this.iconIDMap.put("DIRECTRECORD",R.drawable.main_icon_sc);
        this.iconIDMap.put("DIRECTRECORDEXAM",R.drawable.main_icon_se);
        //this.iconIDMap.put("NUMBERING",R.drawable.tasktype_numbering); 옛날 아이콘
        this.iconIDMap.put("NUMBERING",R.drawable.main_icon_il);
        this.iconIDMap.put("NUMBERINGEXAM",R.drawable.main_icon_ie);
        this.iconIDMap.put("DICTATION",R.drawable.main_icon_tl);
        this.iconIDMap.put("DICTATIONEXAM",R.drawable.main_icon_tl); //main_icon_te가 아직 없음
        this.iconIDMap.put("DIALECT",R.drawable.tasktype_dialect); // 그냥 아이콘 특이하니까 냅둠 원래는 main_icon_vc 여야할듯
        this.iconIDMap.put("DIALECTEXAM",R.drawable.tasktype_dialect); //main_icon_ve이 아직 없음
        this.iconIDMap.put("BOXCROP",R.drawable.main_icon_il);
        this.iconIDMap.put("BOXCROPEXAM",R.drawable.main_icon_ie);
        this.mContext = mContext;
    }
    public ListviewAdapter(Context context, int layout, ArrayList<JSONObject> taskList,Context mContext,JSONObject user){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mTaskList=taskList;
        this.layout=layout;
        this.userInfo=user;
        this.userLoaded=true;
        this.iconIDMap = new HashMap<>();
        this.iconIDMap.put("OCR",R.drawable.main_icon_tl);
        this.iconIDMap.put("OCREXAM",R.drawable.main_icon_tl); //main_icon_te가 아직 없음
        //this.iconIDMap.put("PHOTO",R.drawable.photoicon); 옛날 아이콘
        this.iconIDMap.put("PHOTO",R.drawable.main_icon_ic);
        this.iconIDMap.put("PHOTOEXAM",R.drawable.main_icon_ie);
        //this.iconIDMap.put("VIDEO",R.drawable.tasktype_video); 옛날 아이콘
        this.iconIDMap.put("VIDEO",R.drawable.main_icon_vl);
        this.iconIDMap.put("VIDEOEXAM",R.drawable.main_icon_vl); //main_icon_ve이 아직 없음
        //this.iconIDMap.put("RECORD",R.drawable.tasktype_voice); 옛날 아이콘
        this.iconIDMap.put("RECORD",R.drawable.main_icon_sc);
        //this.iconIDMap.put("DIRECTRECORD",R.drawable.tasktype_voice); 옛날 아이콘
        this.iconIDMap.put("DIRECTRECORD",R.drawable.main_icon_sc);
        this.iconIDMap.put("DIRECTRECORDEXAM",R.drawable.main_icon_se);
        //this.iconIDMap.put("NUMBERING",R.drawable.tasktype_numbering); 옛날 아이콘
        this.iconIDMap.put("NUMBERING",R.drawable.main_icon_il);
        this.iconIDMap.put("NUMBERINGEXAM",R.drawable.main_icon_ie);
        this.iconIDMap.put("DICTATION",R.drawable.main_icon_tl);
        this.iconIDMap.put("DICTATIONEXAM",R.drawable.main_icon_tl); //main_icon_te가 아직 없음
        this.iconIDMap.put("DIALECT",R.drawable.tasktype_dialect); // 그냥 아이콘 특이하니까 냅둠 원래는 main_icon_vc 여야할듯
        this.iconIDMap.put("DIALECTEXAM",R.drawable.tasktype_dialect); //main_icon_ve이 아직 없음
        this.iconIDMap.put("BOXCROP",R.drawable.main_icon_il);
        this.iconIDMap.put("BOXCROPEXAM",R.drawable.main_icon_ie);
        this.mContext = mContext;
    }

    public ArrayList<JSONObject> getmTaskList() {
        return mTaskList;
    }
    public void updateQuest(int position, JSONArray questList){
        JSONObject targetItem = mTaskList.get(position);
        try {
            targetItem.put("questList",questList);

            mTaskList.set(position,targetItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }
    public void addItem(JSONObject item)
    {
        mTaskList.add(item);

        notifyItemInserted(mTaskList.size()-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public ListviewAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_lv, parent, false);
        this.mHolder=new ListviewAdapter.ItemViewHolder(view);
        return mHolder;
    }
    public ListviewAdapter.ItemViewHolder getHolder(){
        return mHolder;
    }
    public void updateItem( ListviewAdapter.ItemViewHolder holder, JSONArray questList)
    {
    Log.d("fullupdate",questList.toString());

        JSONObject[] questItem=new JSONObject[questList.length()];
        String[] questName=new String[questList.length()];
        int[] questReward=new int[questList.length()];

        try {
            if (questList.length() > 0) {
                for (int i = 0; i < questList.length(); i++) {
                    questItem[i] = (JSONObject) questList.get(i);

                    if ((boolean) questItem[i].get("isClear")) {
                        questName[i] = ((String) questItem[i].get("name")) + "(완료)";
                        questReward[i] = (Integer) questItem[i].get("reward");
                    } else {
                        questName[i] = ((String) questItem[i].get("name")) + "[" + String.valueOf(questItem[i].get("questDone")) + "/" + String.valueOf(questItem[i].get("questTotal")) + "]";
                        questReward[i] = (Integer) questItem[i].get("reward");
                    }

                    if((int)questItem[i].get("questTotal") == 10000){
                        Log.d("10000",String.valueOf(questItem[i].get("questTotal")));
                        questName[i]="";
                        questReward[i]=0;
                    }
                }

                Log.d("quest11",questName[0]);

                if((int)questItem[0].get("questTotal") != 10000) {
                    holder.quest1.setText(questName[0]);
                    if (questReward[0] != 0)
                        holder.quest1money.setText("+\uFFE6" + String.valueOf(questReward[0]));
                }

                if (questList.length() == 2) {
                    holder.quest2.setText(questName[1]);
                    if(questReward[1] != 0)
                        holder.quest2money.setText("+\uFFE6" + String.valueOf(questReward[1]));
                }else{
//                    if(holder.itemCL != null) {
//                        holder.itemCL.removeView(holder.quest2);
//                        holder.itemCL.removeView(holder.quest2money);
//                        ConstraintSet itemCLset = new ConstraintSet();
//                        itemCLset.clone(holder.itemCL);
//                        itemCLset.connect(holder.taskTv.getId(), ConstraintSet.BOTTOM, holder.quest1.getId(), ConstraintSet.TOP);
//                        itemCLset.connect(holder.quest1.getId(), ConstraintSet.TOP, holder.taskTv.getId(), ConstraintSet.BOTTOM);
//                        itemCLset.connect(holder.quest1.getId(), ConstraintSet.BOTTOM, holder.gold.getId(), ConstraintSet.TOP);
//                        itemCLset.connect(holder.gold.getId(), ConstraintSet.TOP, holder.quest1.getId(), ConstraintSet.BOTTOM);
//                        itemCLset.applyTo(holder.itemCL);
//                    }
                }

            }else{
//                if(holder.itemCL != null) {
//                    holder.itemCL.removeView(holder.quest1);
//                    holder.itemCL.removeView(holder.quest1money);
//                    holder.itemCL.removeView(holder.quest2);
//                    holder.itemCL.removeView(holder.quest2money);
//                    ConstraintSet itemCLset = new ConstraintSet();
//                    itemCLset.clone(holder.itemCL);
//                    itemCLset.connect(holder.taskTv.getId(), ConstraintSet.BOTTOM, holder.gold.getId(), ConstraintSet.TOP);
//                    itemCLset.connect(holder.gold.getId(), ConstraintSet.TOP, holder.taskTv.getId(), ConstraintSet.BOTTOM);
//                    itemCLset.applyTo(holder.itemCL);
//                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onBindViewHolder(final @NonNull ListviewAdapter.ItemViewHolder holder, int pos) {
        String taskType="";
        String taskName="";
        String taskGold="0";
        JSONArray questList=new JSONArray();
        JSONObject taskItem = mTaskList.get(pos);
        questReset(holder);
        try {
            taskType = taskItem.get("taskType").toString();
            taskName = taskItem.get("taskName").toString();
            taskGold = taskItem.get("gold").toString();
            Log.d("taskItem",taskItem.toString());

           questList=(JSONArray) taskItem.get("questList");


            updateItem(holder,questList);

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        Intent intent;
        if(taskType.contains("EXAM"))
            intent = new Intent(mContext, ExamActivity.class);
        else
            intent = new Intent(mContext, TaskActivity.class);
        intent.putExtra("taskType",taskType);
        intent.putExtra("taskTitle",taskName);
        intent.putExtra("upGold","\uFFE6"+taskGold);
        intent.putExtra("questList",questList.toString());

        try {
            intent.putExtra("taskView",taskItem.get("taskView").toString());
            if(taskType.contains("EXAM")) {
                intent.putExtra("examView", taskItem.get("controller").toString());
                intent.putExtra("examType", (int)taskItem.get("examType"));
            }
            else
                intent.putExtra("controller",taskItem.get("controller").toString());
            intent.putExtra("taskId",taskItem.get("id").toString());

        } catch(JSONException e)
        {
            e.printStackTrace();
        }
        System.out.println("--");
        System.out.println(taskType);
        try {
            System.out.println(taskItem.get("id"));
        }catch(JSONException e)
        {
            e.printStackTrace();
        }
        System.out.println("--");

        if(iconIDMap.containsKey(taskType))
            Glide.with(holder.itemView).load(iconIDMap.get(taskType)).into(holder.taskIcon);
        else
            Glide.with(holder.itemView).load(R.drawable.imagenotload).into(holder.taskIcon);

        holder.taskTv.setText(taskName);
        holder.gold.setText("\uFFE6"+taskGold);

        final Intent tent = intent;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userLoaded) {
                    try {
                        tent.putExtra("goldNow", userInfo.get("gold").toString());
                        tent.putExtra("goldPre", userInfo.get("maybe").toString());
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                    mContext.startActivity(tent);
                }
                else
                    Toast.makeText(mContext,"아직 로딩 중입니다!!",Toast.LENGTH_SHORT);
            }
        });

    }
    public void questReset(ListviewAdapter.ItemViewHolder holder){
        holder.quest1money.setText("");
        holder.quest1.setText("");
        holder.quest2.setText("");
        holder.quest2money.setText("");
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout itemCL;
        private ImageView taskIcon;
        private TextView gold;
        private TextView taskTv;
        private View itemView;
        private TextView quest1;
        private TextView quest2;
        private TextView quest1money;
        private TextView quest2money;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemCL = itemView.findViewById(R.id.itemCL);
            taskIcon = itemView.findViewById(R.id.taskType);
            gold = itemView.findViewById(R.id.gold);
            taskTv = itemView.findViewById(R.id.taskTitle);
            quest1=itemView.findViewById(R.id.quest1);
            quest1money=itemView.findViewById(R.id.quest1money);
            quest2=itemView.findViewById(R.id.quest2);
            quest2money=itemView.findViewById(R.id.quest2money);
        }
    }
}
