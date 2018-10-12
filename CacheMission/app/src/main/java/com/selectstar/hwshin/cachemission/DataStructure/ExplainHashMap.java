package com.selectstar.hwshin.cachemission.DataStructure;

import com.selectstar.hwshin.cachemission.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ExplainHashMap {
    ArrayList<Integer> images_BOXCROP_pole =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_tree =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_transformer =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_preProcess =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_partA =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_partB =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_partC =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_partD =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_partE =new ArrayList<>();
    ArrayList<Integer> images_BOXCROP_partG =new ArrayList<>();


    public HashMap<String,ArrayList>taskTypeLinkImages=new HashMap();
    public ExplainHashMap() {
        taskTypeLinkImages.put("BOXCROPpole", images_BOXCROP_pole);
        images_BOXCROP_pole.add(R.drawable.small_pole_1);
        images_BOXCROP_pole.add(R.drawable.small_pole_2);
        images_BOXCROP_pole.add(R.drawable.small_pole_3);
        images_BOXCROP_pole.add(R.drawable.small_pole_4);
        images_BOXCROP_pole.add(R.drawable.small_pole_5);
        images_BOXCROP_pole.add(R.drawable.small_pole_6);


        taskTypeLinkImages.put("BOXCROPtree", images_BOXCROP_tree);
        images_BOXCROP_tree.add(R.drawable.small_tree_1);
        images_BOXCROP_tree.add(R.drawable.small_tree_2);
        images_BOXCROP_tree.add(R.drawable.small_tree_3);
        images_BOXCROP_tree.add(R.drawable.small_tree_4);
        images_BOXCROP_tree.add(R.drawable.small_tree_5);
        images_BOXCROP_tree.add(R.drawable.small_tree_6);


        taskTypeLinkImages.put("BOXCROPtransformer", images_BOXCROP_transformer);
        images_BOXCROP_transformer.add(R.drawable.small_trans_1);
        images_BOXCROP_transformer.add(R.drawable.small_trans_2);
        images_BOXCROP_transformer.add(R.drawable.small_trans_3);
        images_BOXCROP_transformer.add(R.drawable.small_trans_4);
        images_BOXCROP_transformer.add(R.drawable.small_trans_5);
        images_BOXCROP_transformer.add(R.drawable.small_trans_6);
        images_BOXCROP_transformer.add(R.drawable.small_trans_7);


        taskTypeLinkImages.put("BOXCROPpreProcess", images_BOXCROP_preProcess);
        images_BOXCROP_preProcess.add(R.drawable.small_pre_1);
        images_BOXCROP_preProcess.add(R.drawable.small_pre_2);
        images_BOXCROP_preProcess.add(R.drawable.small_pre_3);
        images_BOXCROP_preProcess.add(R.drawable.small_pre_4);
        images_BOXCROP_preProcess.add(R.drawable.small_pre_5);
        images_BOXCROP_preProcess.add(R.drawable.small_pre_6);


        taskTypeLinkImages.put("BOXCROPpartA", images_BOXCROP_partA);
        images_BOXCROP_partA.add(R.drawable.small_a_1);
        images_BOXCROP_partA.add(R.drawable.small_a_2);
        images_BOXCROP_partA.add(R.drawable.small_a_3);
        images_BOXCROP_partA.add(R.drawable.small_a_4);
        images_BOXCROP_partA.add(R.drawable.small_a_5);
        images_BOXCROP_partA.add(R.drawable.small_a_6);
        images_BOXCROP_partA.add(R.drawable.small_a_7);

        taskTypeLinkImages.put("BOXCROPpartB", images_BOXCROP_partB);
        images_BOXCROP_partB.add(R.drawable.small_b_1);
        images_BOXCROP_partB.add(R.drawable.small_b_2);
        images_BOXCROP_partB.add(R.drawable.small_b_3);
        images_BOXCROP_partB.add(R.drawable.small_b_4);
        images_BOXCROP_partB.add(R.drawable.small_b_5);
        images_BOXCROP_partB.add(R.drawable.small_b_6);
        images_BOXCROP_partB.add(R.drawable.small_b_7);


        taskTypeLinkImages.put("BOXCROPpartC", images_BOXCROP_partC);
        images_BOXCROP_partC.add(R.drawable.small_c_1);
        images_BOXCROP_partC.add(R.drawable.small_c_2);
        images_BOXCROP_partC.add(R.drawable.small_c_3);
        images_BOXCROP_partC.add(R.drawable.small_c_4);
        images_BOXCROP_partC.add(R.drawable.small_c_5);
        images_BOXCROP_partC.add(R.drawable.small_c_6);
        images_BOXCROP_partC.add(R.drawable.small_c_7);


        taskTypeLinkImages.put("BOXCROPpartD", images_BOXCROP_partB);
        images_BOXCROP_partD.add(R.drawable.small_d_1);
        images_BOXCROP_partD.add(R.drawable.small_d_2);
        images_BOXCROP_partD.add(R.drawable.small_d_3);
        images_BOXCROP_partD.add(R.drawable.small_d_4);
        images_BOXCROP_partD.add(R.drawable.small_d_5);
        images_BOXCROP_partD.add(R.drawable.small_d_6);
        images_BOXCROP_partD.add(R.drawable.small_d_7);


        taskTypeLinkImages.put("BOXCROPpartE", images_BOXCROP_partE);
        images_BOXCROP_partE.add(R.drawable.small_e_1);
        images_BOXCROP_partE.add(R.drawable.small_e_2);
        images_BOXCROP_partE.add(R.drawable.small_e_3);
        images_BOXCROP_partE.add(R.drawable.small_e_4);
        images_BOXCROP_partE.add(R.drawable.small_e_5);
        images_BOXCROP_partE.add(R.drawable.small_e_6);
        images_BOXCROP_partE.add(R.drawable.small_e_7);


        taskTypeLinkImages.put("BOXCROPpartG", images_BOXCROP_partG);
        images_BOXCROP_partG.add(R.drawable.small_g_1);
        images_BOXCROP_partG.add(R.drawable.small_g_2);
        images_BOXCROP_partG.add(R.drawable.small_g_3);
        images_BOXCROP_partG.add(R.drawable.small_g_4);
        images_BOXCROP_partG.add(R.drawable.small_g_5);
        images_BOXCROP_partG.add(R.drawable.small_g_6);
        images_BOXCROP_partG.add(R.drawable.small_g_7);

    }
}
