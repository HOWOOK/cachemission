package com.selectstar.hwshin.cachemission.DataStructure;

import com.selectstar.hwshin.cachemission.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ExplainHashMap {
    ArrayList<Integer> images_BOXCROP_pole =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_tree =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_transformer =new ArrayList<>();
    public HashMap<String,ArrayList>taskTypeLinkImages=new HashMap();
    public ExplainHashMap() {
        taskTypeLinkImages.put("BOXCROPpole", images_BOXCROP_pole);
        images_BOXCROP_pole.add(R.drawable.explain_task_pole_1);
        images_BOXCROP_pole.add(R.drawable.explain_task_pole_2);
        images_BOXCROP_pole.add(R.drawable.explain_task_pole_3);
        images_BOXCROP_pole.add(R.drawable.explain_task_pole_4);

        taskTypeLinkImages.put("BOXCROPtree", images_BOXCROP_tree);
        images_BOXCROP_tree.add(R.drawable.explain_task_tree_1);
        images_BOXCROP_tree.add(R.drawable.explain_task_tree_2);
        images_BOXCROP_tree.add(R.drawable.explain_task_tree_3);
        images_BOXCROP_tree.add(R.drawable.explain_task_tree_4);
        images_BOXCROP_tree.add(R.drawable.explain_task_tree_5);
        images_BOXCROP_tree.add(R.drawable.explain_task_tree_6);

        taskTypeLinkImages.put("BOXCROPtransformer", images_BOXCROP_transformer);
        images_BOXCROP_transformer.add(R.drawable.explain_task_transformer_1);
        images_BOXCROP_transformer.add(R.drawable.explain_task_transformer_2);
        images_BOXCROP_transformer.add(R.drawable.explain_task_transformer_3);
        images_BOXCROP_transformer.add(R.drawable.explain_task_transformer_4);
        images_BOXCROP_transformer.add(R.drawable.explain_task_transformer_5);
        images_BOXCROP_transformer.add(R.drawable.explain_task_transformer_6);
        images_BOXCROP_transformer.add(R.drawable.explain_task_transformer_7);

    }
}
