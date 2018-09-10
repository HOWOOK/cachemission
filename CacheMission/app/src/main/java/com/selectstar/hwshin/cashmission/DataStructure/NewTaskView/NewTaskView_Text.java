package com.selectstar.hwshin.cashmission.DataStructure.NewTaskView;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.selectstar.hwshin.cashmission.Activity.TaskActivity;
import com.selectstar.hwshin.cashmission.DataStructure.NewTaskView.NewTaskView;
import com.selectstar.hwshin.cashmission.R;

public class NewTaskView_Text extends NewTaskView {
    public NewTaskView_Text() {
        taskViewID = R.layout.taskview_text;
    }
    public void setContent(String content)
    {
        String[] array;
        String[] array2;
        String[] array3;
        String taskType = ((TaskActivity)parentActivity).getTaskType();
        TextView textView1 = parentActivity.findViewById(R.id.srcview);
        TextView textView2 = parentActivity.findViewById(R.id.srcview2);
        if(taskType.equals("DIALECT")){
            array = content.split("/");
            textView1.setText("["+array[0]+"]");
            textView1.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.textview_custom2));
            textView1.setTextSize(18);
            textView2.setText(array[1]+"\n\n※ 억양만 사투리여도 그대로 적으시고, 원하는 호칭이나 이름으로 자연스럽게 써주세요.");
            textView2.setTextColor(ContextCompat.getColor(parentActivity, R.color.fontColorActive));
            textView2.setTextSize(18);
        }else if(taskType.equals("RECORD")){
            array = content.split("/");
            array2 = array[1].split("\\(");
            array3 = array2[1].split("\\)");
            textView1.setText("["+array[0]+"]"+"\n"+array2[0]);
            textView1.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.textview_custom1));
            textView1.setTextSize(12);
            textView2.setText("(" + array3[0] + ")" + "\n" + array3[1]);
            textView2.setTextSize(18);
        }
    }
}
