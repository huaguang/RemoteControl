package com.hahaha.remotecontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by Guang on 2016/5/27.
 */
public class RemoteControlFragment extends Fragment{
    private TextView mSelectedText;
    private TextView mWorkingText;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_remote_control,container,false);
        mSelectedText=(TextView)v.findViewById(R.id.selected_textView);
        mWorkingText=(TextView)v.findViewById(R.id.working_textView);
        View.OnClickListener numberButtonListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view=(TextView)v;
                String work=mWorkingText.getText().toString();
                String text=view.getText().toString();
                if(work.equals("0")){
                    mWorkingText.setText(text);
                }else{
                    mWorkingText.setText(work+text);
                }

            }
        };
       /* Button zeroBut=(Button)v.findViewById(R.id.zero_but);
        zeroBut.setOnClickListener(numberButtonListener);
        Button oneBut=(Button)v.findViewById(R.id.one_but);
        oneBut.setOnClickListener(numberButtonListener);
        Button enterBut=(Button)v.findViewById(R.id.enter_but);
        enterBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String work=mWorkingText.getText().toString();
                if(work.trim().equals("")){
                    mWorkingText.setText("0");
                }else{
                    mSelectedText.setText(work);
                }
            }
        });*/
        int number=1;
        TableLayout table=(TableLayout)v.findViewById(R.id.tableLayout);
        for(int i=2;i<table.getChildCount()-1;i++){ //跳过两个文本视图
            TableRow tr=(TableRow) table.getChildAt(i);
            for(int j=0;j<tr.getChildCount();j++){
                Button but=(Button)tr.getChildAt(j);
                but.setOnClickListener(numberButtonListener);
                but.setText(String.valueOf(number++));
            }
        }
        TableRow tr=(TableRow)table.getChildAt(table.getChildCount()-1);
        Button deleteBut=(Button)tr.getChildAt(0);
        deleteBut.setText("Delete");
        deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorkingText.setText("0");
            }
        });
        Button zeroBut=(Button)tr.getChildAt(1);
        zeroBut.setText("0");
        zeroBut.setOnClickListener(numberButtonListener);
        Button enterBut=(Button)tr.getChildAt(2);
        enterBut.setText("Enter");
        enterBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String work=mWorkingText.getText().toString();
                if(work.trim().equals("")){
                    mWorkingText.setText("0");
                }else{
                    mSelectedText.setText(work);
                }
            }
        });

        return v;
    }
}
