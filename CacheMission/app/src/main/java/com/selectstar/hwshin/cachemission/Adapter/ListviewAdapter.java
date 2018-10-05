package com.selectstar.hwshin.cachemission.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.selectstar.hwshin.cachemission.Activity.ExamActivity;
import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.DataStructure.TaskListItem;
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
    private int layout;
    private HashMap<String,Integer> iconIDMap;
    private Context mContext;
    public ListviewAdapter(Context context, int layout, ArrayList<JSONObject> taskList,Context mContext,JSONObject user){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mTaskList=taskList;
        this.layout=layout;
        this.userInfo=user;
        this.iconIDMap = new HashMap<>();
        this.iconIDMap.put("OCR",R.drawable.tasktype_ocr);
        this.iconIDMap.put("PHOTO",R.drawable.photoicon);
        this.iconIDMap.put("VIDEO",R.drawable.tasktype_video);
        this.iconIDMap.put("RECORD",R.drawable.tasktype_voice);
        this.iconIDMap.put("DIRECTRECORD",R.drawable.tasktype_voice);
        this.iconIDMap.put("NUMBERING",R.drawable.tasktype_numbering);
        this.iconIDMap.put("DICTATION",R.drawable.tasktype_dictation);
        this.iconIDMap.put("DIALECT",R.drawable.tasktype_dialect);
        this.mContext = mContext;
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
        return new ListviewAdapter.ItemViewHolder(view);
    }
    public void updateItem( ListviewAdapter.ItemViewHolder holder, JSONArray questList)
    {

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

                }
                holder.quest1.setText(questName[0]);
                holder.quest1money.setText("\uFFE6" + String.valueOf(questReward[0]));
                if (questList.length() == 2) {
                    holder.quest2.setText(questName[1]);
                    holder.quest2money.setText("\uFFE6" + String.valueOf(questReward[1]));
                }


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
            intent.putExtra("goldNow", userInfo.get("gold").toString());
            intent.putExtra("goldPre", userInfo.get("maybe").toString());
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
        if(taskType.contains("EXAM")) {
            taskType = taskType.substring(0, taskType.length() - 4);
            Glide.with(holder.itemView).load(R.drawable.tasktypeadd).into(holder.checkIcon);
        }
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
                mContext.startActivity(tent);
            }
        });
        //TextView dailyMission=convertView.findViewById(R.id.dailyMission);
        //dailyMission.setText(taskItem.getDailyMission());
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView taskIcon;
        private ImageView checkIcon;
        private TextView gold;
        private TextView taskTv;
        private View itemView;
        private  TextView quest1;
        private TextView quest2;
        private TextView quest1money;
        private  TextView quest2money;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            taskIcon = itemView.findViewById(R.id.taskType);
            checkIcon = itemView.findViewById(R.id.taskTypeCheck);
            gold = itemView.findViewById(R.id.gold);
            taskTv = itemView.findViewById(R.id.taskTitle);
            quest1=itemView.findViewById(R.id.quest1);
            quest1money=itemView.findViewById(R.id.quest1money);
            quest2=itemView.findViewById(R.id.quest2);
            quest2money=itemView.findViewById(R.id.quest2money);
        }
    }
}
