package com.selectstar.hwshin.cachemission.DataStructure;

import com.selectstar.hwshin.cachemission.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ExplainHashMap {
    ArrayList<Integer> images_BOXCROP=new ArrayList<>();
    public HashMap<String,ArrayList>taskTypeLinkImages=new HashMap();
    public ExplainHashMap() {
        taskTypeLinkImages.put("BOXCROP",images_BOXCROP);
        images_BOXCROP.add(R.drawable.explain_task_dialect1);
        images_BOXCROP.add(R.drawable.explain_task_dialect2);
        images_BOXCROP.add(R.drawable.explain_task_dialect2);
        images_BOXCROP.add(R.drawable.explain_task_dialect3);
    }
}
