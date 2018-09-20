
    package com.selectstar.hwshin.cachemission.Adapter;

    import android.content.Context;
    import android.content.Intent;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.TextView;

    import com.selectstar.hwshin.cachemission.Activity.NoticePopUpActivity;
    import com.selectstar.hwshin.cachemission.DataStructure.NoticeItem;
    import com.selectstar.hwshin.cachemission.R;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;

    public class NoticeviewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<NoticeItem> mNoticeList;
    private int layout;

    public NoticeviewAdapter(Context context, int layout, ArrayList<NoticeItem> noticeList){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mNoticeList=noticeList;
        this.layout=layout;
    }


    @Override
    public int getCount() {
        return mNoticeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNoticeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NoticeItem noticeItem = mNoticeList.get(position);
        String date1 = noticeItem.getDate();
        System.out.println("서버에서 받은 날짜 : "+date1);
        String date;
        date = timeCalculate(date1);



        convertView = inflater.inflate(layout, parent, false);

        TextView titleView = (TextView) convertView.findViewById(R.id.title2);
        titleView.setText(noticeItem.getTitle());
        TextView dateView = (TextView) convertView.findViewById(R.id.date);
        dateView.setText(date);
        final Context context = convertView.getContext();

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NoticePopUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("title",noticeItem.getTitle());
                intent.putExtra("date",noticeItem.getDate());
                intent.putExtra("content",noticeItem.getContent());
                context.startActivity(intent);
            }
        });
        return convertView;
    }


        //나중에 더 정교하게할 필요가 있음 (1년 52주인데 48주로 계산된다던지...)
        private String timeCalculate(String data1) {
            long currentTime = System.currentTimeMillis();
            long calDate = 0;
            Integer num;
            String returnVal="";

            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            try {
                Date postDate = format.parse(data1);
                calDate = currentTime - postDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(calDate < 24l*60l*60l*1000l){
                returnVal = "오늘";
            }else if((calDate >= 24l*60l*60l*1000l) && (calDate < 24l*60l*60l*1000l*7l)){
                num = (int) (long) (calDate / (24l*60l*60l*1000l));
                returnVal = String.valueOf(num)+"일전";
            }else if((calDate >= 24l*60l*60l*1000l*7l) && (calDate < 24l*60l*60l*1000l*7l*4l)){
                num = (int) (long) (calDate / (24l*60l*60l*1000l*7l));
                returnVal = String.valueOf(num)+"주전";
            }else if((calDate >= 24l*60l*60l*1000l*7l*4l) && (calDate < 24l*60l*60l*1000l*7l*4l*12l)){
                num = (int) (long) (calDate / (24l*60l*60l*1000l*7l*4l));
                returnVal = String.valueOf(num)+"달전";
            }else if(calDate >= 24l*60l*60l*1000l*7l*4l*12l){
                num = (int) (long) (calDate / (24l*60l*60l*1000l*7l*4l*12l));
                returnVal = String.valueOf(num)+"년전";
            }

            return returnVal;
        }
    }