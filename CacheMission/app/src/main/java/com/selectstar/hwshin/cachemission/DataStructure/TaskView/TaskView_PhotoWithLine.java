package com.selectstar.hwshin.cachemission.DataStructure.TaskView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.selectstar.hwshin.cachemission.Activity.PatherActivity;
import com.selectstar.hwshin.cachemission.Photoview.OnMatrixChangedListener;
import com.selectstar.hwshin.cachemission.Photoview.PhotoView;
import com.selectstar.hwshin.cachemission.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskView_PhotoWithLine extends TaskView {

    public class LineView extends View{
        private Paint paint = new Paint();
        private float[] Lines = new float[4];
        DisplayMetrics displayMetrics = new DisplayMetrics();

        public Paint getPaint() {
            return paint;
        }

        public float[] getLines() {
            return Lines;
        }

        public LineView(Context context){//one Answer
            super(context);
            parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            getDeviceDpi = displayMetrics.densityDpi;
            dpScale = (float) getDeviceDpi / 160f;
            paint.setAntiAlias(true);
            paint.setStrokeWidth(3f * dpScale);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(this.getResources().getColor(R.color.colorPoint2));
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            TaskView_PhotoWithLine.LineView.this.setLayoutParams(params);
        }

        public void onDraw(Canvas canvas){
            canvas.drawLines(Lines, paint);
        }
    }

    private PhotoView photoView;
    public float[][] answerCoordination;
    public float[][] testAnswerCoordination;
    public float[][] changedCoordination;
    public float[][] answerCoordinationSubmit;
    public float[][] changedCoordinationSubmit;
    public ImageView expandView;
    boolean isExamFlag;
    public boolean ImageReady;
    private TaskView_PhotoWithLine.LineView answerLineView;
    private TaskView_PhotoWithLine.LineView answerLineViewSubmit;
    private float [] answerLines, answerLinesSubmit;
    private Bitmap expandBitmap;
    private String[] arrayExpand, arrayURI, arrayAnswer, arrayTemp;
    private String[] tempAnswer, testAnswer;
    private ConstraintLayout photoWithLineCL;
    private int getDeviceDpi;
    private float dpScale;
    private HashMap<String, Bitmap> bitmaps;
    private float iouValue;
    private ArrayList<String>LineTestAnswerArray;

    public TaskView_PhotoWithLine(){
        taskViewID = R.layout.taskview_photowithline;
        bitmaps = new HashMap<>();
    }

    @Override
    public void setParentActivity(PatherActivity parentActivity) {
        super.setParentActivity(parentActivity);
    }

    public PhotoView getPhotoView() {
        return this.photoView;
    }

    //사진 url을 서버에서 받아와서 미리 로딩해놓는 함수이다.
    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;

        Bitmap retBitmap = null;


        try{
            imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true); //url로 input받는 flag 허용
            connection.connect(); //연결
            is = connection.getInputStream(); // get inputstream
            retBitmap = BitmapFactory.decodeStream(is);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(connection!=null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }

    @Override
    public void setPreviewContents(ArrayList<JSONObject> list)
    {
        try {
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = list.get(i);
                final String content = jsonObject.get("content").toString();
                String[] preArray0 = content.split(">");
                final String[] preArray1 = preArray0[1].split("\\*");
                Log.d("arrayset",preArray0[1]);
                new Thread() {
                    public void run() {

                        Bitmap bitmap = getBitmap(parentActivity.getString(R.string.mainurl) + "/media/" + preArray1[0]);
                        bitmaps.put(preArray1[0], bitmap);


                    }
                }.start();



            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void setContent(String content) {
        expandView = parentActivity.findViewById(R.id.expandView);
        final float expandSize = 2f;
        parentActivity.findViewById(R.id.expandViewCL).setVisibility(View.INVISIBLE);
        photoWithLineCL = parentActivity.findViewById(R.id.photoWithLineCL);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDeviceDpi = displayMetrics.densityDpi;
        dpScale = (float) getDeviceDpi / 160f;

        //처음에 expandview가 연결안되어있어서 연결해줘야함
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) parentActivity.findViewById(R.id.photoWithLineCL));
        constraintSet.clear(parentActivity.findViewById(R.id.expandViewCL).getId(), ConstraintSet.TOP);
        constraintSet.clear(parentActivity.findViewById(R.id.expandViewCL).getId(), ConstraintSet.BOTTOM);
        constraintSet.clear(parentActivity.findViewById(R.id.expandViewCL).getId(), ConstraintSet.LEFT);
        constraintSet.clear(parentActivity.findViewById(R.id.expandViewCL).getId(), ConstraintSet.RIGHT);
        constraintSet.connect(parentActivity.findViewById(R.id.expandViewCL).getId(), ConstraintSet.TOP, parentActivity.findViewById(R.id.photoWithLineCL).getId(), ConstraintSet.TOP, (int)(44 * dpScale));
        constraintSet.connect(parentActivity.findViewById(R.id.expandViewCL).getId(), ConstraintSet.LEFT, parentActivity.findViewById(R.id.photoWithLineCL).getId(), ConstraintSet.LEFT, (int)(26 * dpScale));
        constraintSet.applyTo((ConstraintLayout) parentActivity.findViewById(R.id.photoWithLineCL));

        //플래그 처리
        if(parentActivity.getTaskType().contains("EXAM"))
            isExamFlag = true;

        //box 좌표 구해서 저장
        // *content의 양식 =>    확대좌표>URI*<답<답<답...
        //                      f,f,f,f>String*<f,f,f,f<f,f,f,f<f,f,f,f....
        arrayExpand = content.split(">");
        arrayURI = arrayExpand[1].split("\\*");
        if(arrayURI.length == 2) {//좌표를 찾은적이 있는 놈이라면..
            arrayAnswer = arrayURI[1].split("<");
            if (!(arrayAnswer.length == 0)) {
                answerCoordination = new float[arrayAnswer.length - 1][4];
                changedCoordination = new float[answerCoordination.length][4];
                coordinationParsing(arrayAnswer);
            }
        }
        if(testFlag) {
            System.out.println("미친놈"+String.valueOf(LineTestAnswer.length()));
            LineTestAnswerArray= jsonToArrayList(LineTestAnswer);
            testAnswer = new String[LineTestAnswer.length()];
            for (int i = 0; i < LineTestAnswer.length(); i++) {
                try {
                    testAnswer[i] = LineTestAnswer.get(i).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!(testAnswer.length == 0)) {
                System.out.println("미친년"+String.valueOf(testAnswer[0])+"머머"+String.valueOf(testAnswer.length));
                testAnswerCoordination = new float[testAnswer.length ][4];
//                changedCoordination = new float[answerCoordination.length][4];
//                answerType = new int[answerCoordination.length];
//                answerCount = answerCoordination.length;
                coordinationParsingForTest(testAnswer);
            }
        }

        answerLineView = new TaskView_PhotoWithLine.LineView(parentActivity);
        answerLineView.setId(View.generateViewId());
        photoWithLineCL.addView(answerLineView);

        answerLineViewSubmit = new TaskView_PhotoWithLine.LineView(parentActivity);
        answerLineViewSubmit.setId(View.generateViewId());
        answerLineViewSubmit.paint.setColor(parentActivity.getResources().getColor(R.color.colorPoint1));
        photoWithLineCL.addView(answerLineViewSubmit);

        photoView = parentActivity.findViewById(R.id.srcview);
        photoView.setMaximumScale(15);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if(bitmaps.containsKey(arrayURI[0])) {
            Glide.with(parentActivity)
                    .asBitmap()
                    .load(bitmaps.get(arrayURI[0]))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            resource = BitmapResourceFitScreenSize(resource);
                            expandBitmap = resource;
                            photoView.setImageBitmap(resource);
                            ImageReady = true;
                            drawAnswer(answerCoordination, answerCoordinationSubmit);
                            parentActivity.findViewById(R.id.expandViewCL).bringToFront();
                        }
                    });
        }else{
            Glide.with(parentActivity)
                    .asBitmap()
                    .load(Uri.parse(parentActivity.getString(R.string.mainurl) + "/media/" + arrayURI[0]))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            resource = BitmapResourceFitScreenSize(resource);
                            expandBitmap = resource;
                            photoView.setImageBitmap(resource);
                            ImageReady = true;
                            drawAnswer(answerCoordination, answerCoordinationSubmit);
                            parentActivity.findViewById(R.id.expandViewCL).bringToFront();
                        }
                    });
        }

        photoView.setOnMatrixChangeListener(new OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                if(answerCoordination != null ){
                    drawAnswer(answerCoordination, answerCoordinationSubmit);
                }
            }
        });

    }

    private Bitmap BitmapResourceFitScreenSize(Bitmap resource) {
        int pvWidth, pvHeight;
        float rscRatio, pvRatio;

        pvWidth = photoView.getWidth();
        pvHeight = photoView.getHeight();
        rscRatio = (float) resource.getWidth() / (float) resource.getHeight();
        pvRatio = (float) pvWidth / (float) pvHeight;

        if(rscRatio > pvRatio)//처음에 가로가 꽉 차서 나오는 경우
            resource = Bitmap.createScaledBitmap(resource, pvWidth, (int)(pvWidth / rscRatio), true );
        else//처음에 세로가 꽉 차서 나오는 경우
            resource = Bitmap.createScaledBitmap(resource, (int)(pvHeight * rscRatio), pvHeight, true );

        return resource;
    }

    public void expandBitmapSetting(float X, float Y, float expandSize){
        Bitmap bitmap = bitmapExpand(X, Y, expandSize);
        expandView.setImageBitmap(bitmap);
    }

    private Bitmap bitmapExpand(float X, float Y, float expandSize) {
        Bitmap returnBitmap;
        int padding_val = 85; //85? : expand view의 크기가 170이다. (expand View size / 2)
        X = X - photoView.getDisplayRect().left;
        Y = Y - photoView.getDisplayRect().top;

        //Bitmap을 확대
        returnBitmap = Bitmap.createScaledBitmap(expandBitmap
                ,(int)(expandBitmap.getWidth() * expandSize)
                ,(int)(expandBitmap.getHeight() * expandSize)
                ,true);

        //상하좌우에 검은색 패딩 삽입
        Bitmap baseBitmap = Bitmap.createBitmap(returnBitmap.getWidth() + 2 * padding_val,
                returnBitmap.getHeight() + 2 * padding_val, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(baseBitmap);
        can.drawARGB(255,0,0,0); //This represents black color
        can.drawBitmap(returnBitmap, padding_val, padding_val, null);
        returnBitmap = baseBitmap;

        //현재 터치위치에 맞추어 100 * 100 expandView에 띄울 뷰의 bitmap 설정
        float setX, setY;
        setX = X * expandSize;
        setY = Y * expandSize;
        if (setX < 0f)
            setX = 0f;
        else if(setX > (float) returnBitmap.getWidth() - (float) padding_val * 2f)
            setX = returnBitmap.getWidth() - (float) padding_val * 2f;

        if(setY < 0f)
            setY = 0f;
        else if(setY > (float) returnBitmap.getHeight() - (float) padding_val * 2f)
            setY = returnBitmap.getHeight() - (float) padding_val * 2f;

        returnBitmap = returnBitmap.createBitmap(
                returnBitmap
                , (int)(setX) //X 시작위치
                , (int)(setY) //Y 시작위치
                , padding_val * 2 // 너비
                , padding_val * 2); // 높이

        return returnBitmap;
    }


    //String 형태로 서버에서 넘어온 좌표를 float[][] 형식으로 바꾸어 answerCoordination에 넣어줌
    private void coordinationParsing(String[] array) {
        for(int i =1; i < array.length; i++){
            arrayTemp = array[i].split(",");
            answerCoordination[i-1][0] = (float) Float.parseFloat(arrayTemp[0]);
            answerCoordination[i-1][1] = (float) Float.parseFloat(arrayTemp[1]);
            answerCoordination[i-1][2] = (float) Float.parseFloat(arrayTemp[2]);
            answerCoordination[i-1][3] = (float) Float.parseFloat(arrayTemp[3]);
        }
    }

    private void coordinationParsingForTest(String[] array) {
        System.out.println("들왔다");
        for(int i =0; i < array.length; i++){

            arrayTemp = array[i].split(",");
            String[] arrayTempParsed=new String[4];
            if(Float.parseFloat(arrayTemp[0])>Float.parseFloat(arrayTemp[2])) {
                arrayTempParsed[0]=arrayTemp[2];
                arrayTempParsed[1]=arrayTemp[3];
                arrayTempParsed[2]=arrayTemp[0];
                arrayTempParsed[3]=arrayTemp[1];
            }else{
                arrayTempParsed[0]=arrayTemp[0];
                arrayTempParsed[1]=arrayTemp[1];
                arrayTempParsed[2]=arrayTemp[2];
                arrayTempParsed[3]=arrayTemp[3];
            }
            System.out.println("parsing"+Float.parseFloat(arrayTemp[0]));
            testAnswerCoordination[i][0] = (float) Float.parseFloat(arrayTempParsed[0]);
            testAnswerCoordination[i][1] = (float) Float.parseFloat(arrayTempParsed[1]);
            testAnswerCoordination[i][2] = (float) Float.parseFloat(arrayTempParsed[2]);
            testAnswerCoordination[i][3] = (float) Float.parseFloat(arrayTempParsed[3]);
            //answerType[i-1] = 0;
        }
    }

    public boolean similarityTest(float leftPercentSend, float topPercentSend, float rightPercentSend, float bottomPercentSend) {
        return true;
    }

    public void addAnswer(float leftPercentSend, float topPercentSend, float rightPercentSend, float bottomPercentSend) {
        Log.d("들어온 답", ((Float) leftPercentSend).toString() + ", " + ((Float) topPercentSend).toString() + ", " + ((Float) rightPercentSend).toString() + ", " + ((Float) bottomPercentSend).toString());

        float[][] postAnswerCoordinationSubmit = answerCoordinationSubmit;
        float[][] newAnswerCoordinationSubmit;
        if (answerCoordinationSubmit != null) {
            newAnswerCoordinationSubmit = new float[answerCoordinationSubmit.length + 1][4];
            System.arraycopy(postAnswerCoordinationSubmit, 0, newAnswerCoordinationSubmit, 0 , postAnswerCoordinationSubmit.length);
            changedCoordinationSubmit = new float[answerCoordinationSubmit.length + 1][4];
            newAnswerCoordinationSubmit[answerCoordinationSubmit.length][0] = leftPercentSend;
            newAnswerCoordinationSubmit[answerCoordinationSubmit.length][1] = topPercentSend;
            newAnswerCoordinationSubmit[answerCoordinationSubmit.length][2] = rightPercentSend;
            newAnswerCoordinationSubmit[answerCoordinationSubmit.length][3] = bottomPercentSend;
        }else{
            newAnswerCoordinationSubmit = new float[1][4];
            changedCoordinationSubmit = new float[1][4];
            newAnswerCoordinationSubmit[0][0] = leftPercentSend;
            newAnswerCoordinationSubmit[0][1] = topPercentSend;
            newAnswerCoordinationSubmit[0][2] = rightPercentSend;
            newAnswerCoordinationSubmit[0][3] = bottomPercentSend;
        }
        answerCoordinationSubmit = newAnswerCoordinationSubmit;

    }

    public void removeAnswer() {
        answerCoordination = null;
        changedCoordination = null;
        answerCoordinationSubmit = null;
        changedCoordinationSubmit = null;
        photoWithLineCL.removeView(answerLineView);
        answerLineView = null;
        photoWithLineCL.removeView(answerLineViewSubmit);
        answerLineViewSubmit = null;
        answerLines = null;
        answerLinesSubmit = null;
    }

    public void drawAnswer(float[][] answerCoordination, float[][] answerCoordinationSubmit) {
        if(answerCoordination != null)
            coordinationChange(answerCoordination);
        if(answerCoordinationSubmit != null)
            coordinationChange2(answerCoordinationSubmit);

        if(answerCoordination != null) {
            answerLines = new float[4 * changedCoordination.length];
            for (int i = 0; i < changedCoordination.length; i++){
                for (int j = 0; j < changedCoordination[i].length; j++){
                    answerLines[4 * i + j]  = changedCoordination[i][j];
                }
            }
        }else {
            answerLines = new float[4];
        }

        if(answerLines != null) {
            answerLineView.Lines = answerLines;
            answerLineView.invalidate();
        }

        if(answerCoordinationSubmit != null) {
            answerLinesSubmit = new float[4 * changedCoordinationSubmit.length];
            for (int i = 0; i < changedCoordinationSubmit.length; i++){
                for (int j = 0; j < changedCoordinationSubmit[i].length; j++){
                    answerLinesSubmit[4 * i + j]  = changedCoordinationSubmit[i][j];
                }
            }
        }
        else {
            answerLinesSubmit = new float[4];
        }

        if(answerLinesSubmit != null) {
            answerLineViewSubmit.Lines = answerLinesSubmit;
            answerLineViewSubmit.invalidate();
        }
    }


    //퍼센트 좌표를 현재 photoview의 스케일에 맞게 절대 좌표로 변경해준다.
    private void coordinationChange(float[][] answerCoordination) {
        float photoWidth, photoHeight;
        photoWidth = photoView.getDisplayRect().right - photoView.getDisplayRect().left;
        photoHeight = photoView.getDisplayRect().bottom - photoView.getDisplayRect().top;
        for (int i = 0; i< answerCoordination.length; i++){
            changedCoordination[i][0] = photoView.getDisplayRect().left + answerCoordination[i][0] * photoWidth; //left
            changedCoordination[i][1] = photoView.getDisplayRect().top + answerCoordination[i][1] * photoHeight; //top
            changedCoordination[i][2] = photoView.getDisplayRect().left + answerCoordination[i][2] * photoWidth; //right
            changedCoordination[i][3] = photoView.getDisplayRect().top + answerCoordination[i][3] * photoHeight; //bottom
        }
    }

    //퍼센트 좌표를 현재 photoview의 스케일에 맞게 절대 좌표로 변경해준다.
    private void coordinationChange2(float[][] answerCoordination) {
        float photoWidth, photoHeight;
        photoWidth = photoView.getDisplayRect().right - photoView.getDisplayRect().left;
        photoHeight = photoView.getDisplayRect().bottom - photoView.getDisplayRect().top;
        for (int i = 0; i<answerCoordination.length; i++){
            changedCoordinationSubmit[i][0] = photoView.getDisplayRect().left + answerCoordination[i][0] * photoWidth; //left
            changedCoordinationSubmit[i][1] = photoView.getDisplayRect().top + answerCoordination[i][1] * photoHeight; //top
            changedCoordinationSubmit[i][2] = photoView.getDisplayRect().left + answerCoordination[i][2] * photoWidth; //right
            changedCoordinationSubmit[i][3] = photoView.getDisplayRect().top + answerCoordination[i][3] * photoHeight; //bottom
        }
    }

    public void updateTestSet(int boxIndex){
        System.out.println("미친놈"+String.valueOf(LineTestAnswerArray.size()));
        LineTestAnswerArray= jsonToArrayList(LineTestAnswer);
        LineTestAnswerArray.remove(boxIndex);
        testAnswer = new String[LineTestAnswerArray.size()];
        for (int i = 0; i < LineTestAnswerArray.size(); i++) {

            testAnswer[i] = LineTestAnswerArray.get(i).toString();

        }
        if (!(testAnswer.length == 0)) {
            System.out.println("미친년"+String.valueOf(testAnswer[0])+"머머"+String.valueOf(testAnswer.length));
            testAnswerCoordination = new float[testAnswer.length ][4];
//                changedCoordination = new float[answerCoordination.length][4];
//                answerType = new int[answerCoordination.length];
//                answerCount = answerCoordination.length;
            coordinationParsingForTest(testAnswer);
        }
    }
    public ArrayList<String> jsonToArrayList(JSONArray jsonArray) {
        ArrayList<String> listdata = new ArrayList<String>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    listdata.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return listdata;
    }
    public String compareCoordination(float left, float top, float right, float bottom, int boxIndex){
        String returnVal="";
        float firstPointDifference,secondPointDifference;
        firstPointDifference= (float) Math.sqrt(Math.pow((testAnswerCoordination[boxIndex][0]-left),2)+Math.pow((testAnswerCoordination[boxIndex][1]-top),2));
        secondPointDifference=(float) Math.sqrt(Math.pow((testAnswerCoordination[boxIndex][2]-right),2)+Math.pow((testAnswerCoordination[boxIndex][3]-bottom),2));
        if(firstPointDifference<0.05&&secondPointDifference<0.05)
            returnVal="pass";
        else if(!(firstPointDifference<0.05)&&secondPointDifference<0.05)
            returnVal="leftWrong";
        else if((firstPointDifference<0.05)&&!(secondPointDifference<0.05))
            returnVal="rightWrong";
        else
            returnVal="allWrong";



        return returnVal;

    }
    public int findCandidate(float left, float top, float right, float bottom){

        float minDistance=10000;
        int minDistanceIndex=0;
        float firstPointDifference,secondPointDifference;
        if(testAnswerCoordination != null) {
            for (int i = 0; i < LineTestAnswerArray.size(); i++) {
                firstPointDifference= (float) Math.sqrt(Math.pow((testAnswerCoordination[i][0]-left),2)+Math.pow((testAnswerCoordination[i][1]-top),2));
                secondPointDifference=(float) Math.sqrt(Math.pow((testAnswerCoordination[i][2]-right),2)+Math.pow((testAnswerCoordination[i][3]-bottom),2));
                System.out.println("미차미차"+String.valueOf(firstPointDifference+secondPointDifference));
                if (firstPointDifference+secondPointDifference<minDistance) {
                    minDistance = firstPointDifference+secondPointDifference;
                    minDistanceIndex=i;
                }
            }
        }
        return minDistanceIndex;
    }

}

