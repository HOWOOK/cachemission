package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.selectstar.hwshin.cachemission.Activity.TaskActivity;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class TaskView_Text extends TaskView {
    public TaskView_Text() {
        taskViewID = R.layout.taskview_text;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list) {

    }

    public void setContent(String content)
    {
        String[] array;
        String[] array2;
        String[] array3;
        String taskType = parentActivity.getTaskType();
        TextView textView1 = parentActivity.findViewById(R.id.srcview);
        TextView textView2 = parentActivity.findViewById(R.id.srcview2);
        System.out.println(taskType);
        if(taskType.equals("DIALECT")){
            array = content.split("/");
            textView1.setText("["+array[0]+"]");
            textView1.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.textview_custom2));
            textView1.setTextSize(18);
            textView2.setText(array[1]+"\n\n※ 억양만 사투리여도 그대로 적으시고, 원하는 호칭이나 이름으로 자연스럽게 써주세요.");
            textView2.setTextColor(ContextCompat.getColor(parentActivity, R.color.fontColorActive));
            textView2.setTextSize(18);
        }else if(taskType.equals("RECORD") || taskType.equals("RECORDEXAM")){


            if(!(content.indexOf("(")>0)){
                array = content.split("/");
                textView1.setText("["+array[0]+"]");
                textView1.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.textview_custom2));
                textView1.setTextSize(18);
                textView2.setText(array[1]+"\n\n※ 상황을 그대로 사투리로 옮겨주세요.");
                textView2.setTextColor(ContextCompat.getColor(parentActivity, R.color.fontColorActive));
                textView2.setTextSize(18);
            }
           else{
            array = content.split("/");

            array2 = array[1].split("\\(");
            array3 = array2[1].split("\\)");
            textView1.setText("["+array[0]+"]"+"\n"+array2[0]);
            //textView1.setText("??");
            textView1.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.textview_custom1));
            textView1.setTextSize(12);
            textView2.setText("(" + array3[0] + ")" + "\n" + array3[1]);
            //textView2.setText("??");
            textView2.setTextSize(18);
            }
        }else if(taskType.equals("DIALECTRECORD")) {
            array = content.split("/");
            textView1.setText("["+array[0]+"]");
            textView1.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.textview_custom2));
            textView1.setTextSize(18);
            textView2.setText(array[1]+"\n\n※ 원하는 호칭이나 이름으로 자유롭게 부르시고, 억양에 너무 부담 갖지 않으셔도 됩니다.");
            textView2.setTextColor(ContextCompat.getColor(parentActivity, R.color.fontColorActive));
            textView2.setTextSize(18);

        }
    }
}
