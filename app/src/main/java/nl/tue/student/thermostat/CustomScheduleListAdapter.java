package nl.tue.student.thermostat;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.NumberPicker;

import java.util.ArrayList;


public class CustomScheduleListAdapter extends BaseAdapter {
    ArrayList<String> time = new ArrayList<String>();
    ArrayList<String> switchto = new ArrayList<String>();
    boolean viewListVisible;
    Day day;
    private Context context;
    Dialog d;
    int count = 1;
    Adapter a;
    Dialog editDialog;


    CustomScheduleListAdapter(Context context, Day day){
        this.context=context;
        this.day = day;
        a = this;
    }

    public void addItem(String begin, String to) {
        if (!time.isEmpty()) {
            if(time.get(0).equals("")) {
                time.clear();
                switchto.clear();
                time.add(begin);
                switchto.add(to);
            } else {
                time.add(begin);
                switchto.add(to);
            }
        } else {
            time.add(begin);
            switchto.add(to);
        }
        count = time.size();
        notifyDataSetChanged();
    }

    public void removeFirst() {
        if (!time.isEmpty()){
            if (!time.get(0).equals("")) {
                time.remove(0);
                switchto.remove(0);
                count = time.size();
            }
        } else {
            time.add("");
            switchto.add(null);
            count = 0;
        }
        notifyDataSetChanged();
    }

    public void removeAll() {
        time.clear();
        switchto.clear();
        time.add("");
        switchto.add("");
        count = 0;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (viewListVisible) {
            return count;
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void viewListVisible(boolean b) {
        viewListVisible = b;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (time.isEmpty()) {
            removeAll();
        }

        View row=null;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(R.layout.schedule_list_item,parent,false);

            final ImageView dayIcon= (ImageView) row.findViewById(R.id.day_icon);
            TextView textview= (TextView) row.findViewById(R.id.txt_begin);
            TextView textview1 = (TextView) row.findViewById(R.id.txt_end);
            ImageView nightIcon = (ImageView) row.findViewById(R.id.night_icon);
            final ImageView editIcon = (ImageView) row.findViewById(R.id.edit_icon);
            ImageView deleteIcon = (ImageView) row.findViewById(R.id.delete_icon);


            ImageView abegin = (ImageView) row.findViewById(R.id.abegin_icon);
            ImageView aend = (ImageView) row.findViewById(R.id.aend_icon);

            if(switchto.get(position).equals("day")){
                dayIcon.setBackgroundResource(R.drawable.night);
                nightIcon.setBackgroundResource(R.drawable.day);
            }else if(switchto.get(position).equals("night")){
                dayIcon.setBackgroundResource(R.drawable.day);
                nightIcon.setBackgroundResource(R.drawable.night);
            }
            System.out.println("Added " + time.get(position) + " to position: " + position);
            textview.setText(time.get(position));
            editIcon.setBackgroundResource(R.drawable.editpng);
            deleteIcon.setBackgroundResource(R.drawable.deletepng);


            abegin.setBackgroundResource(R.drawable.abegin);
            aend.setBackgroundResource(R.drawable.aend);


            editIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editDialog = new Dialog(day.superList.getContext());
                    editDialog.setTitle("Edit schedule");
                    editDialog.setContentView(R.layout.dialog3);

                    day.choice = day.adapter.switchto.get(position);

                    final android.widget.NumberPicker np0 = (android.widget.NumberPicker) editDialog.findViewById(R.id.numberPicker3);
                    np0.setFormatter(new android.widget.NumberPicker.Formatter() {
                        @Override
                        public String format(int i) {
                            return String.format("%02d", i);
                        }
                    });
                    np0.setMinValue(00);
                    np0.setMaxValue(23);
                    np0.setValue(Integer.parseInt(day.adapter.time.get(position).substring(0,2)));
                    np0.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    np0.setWrapSelectorWheel(false);
                    final android.widget.NumberPicker dp0 = (android.widget.NumberPicker) editDialog.findViewById(R.id.numberPicker4);
                    dp0.setFormatter(new android.widget.NumberPicker.Formatter() {
                        @Override
                        public String format(int i) {
                            return String.format("%02d", i);
                        }
                    });
                    dp0.setMinValue(00);
                    dp0.setMaxValue(59);
                    dp0.setValue(Integer.parseInt(day.adapter.time.get(position).substring(3,5)));
                    dp0.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                    dp0.setWrapSelectorWheel(true);
                    dp0.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            System.out.println("old: " + oldVal + ", new: " + newVal);
                            if(oldVal == 59 && newVal == 0 && np0.getValue() < 23){
                                np0.setValue(np0.getValue()+1);
                            }
                            if(oldVal == 0 && newVal == 59 && np0.getValue() > 0){
                                np0.setValue(np0.getValue()-1);
                            }
                        }
                    });

                    day.leftImg = (ImageView) editDialog.findViewById(R.id.left_icon);
                    day.rightImg = (ImageView) editDialog.findViewById(R.id.right_icon);
                    final Button setButton = (Button) editDialog.findViewById(R.id.button6);
                    setButton.setText("update");
                    final Button typeChoice = (Button) editDialog.findViewById(R.id.button7);
                    typeChoice.setVisibility(View.INVISIBLE);
                    /*if((day.daysAvailable && day.nightsAvailable) || (day.daysAvailable && day.choice.equals("night")) || (day.nightsAvailable && day.choice.equals("day"))){
                        typeChoice.setVisibility(View.INVISIBLE);
                        //typeChoice.setEnabled(false);
                    }else if(day.daysAvailable){
                        day.choice = "day";
                        typeChoice.setVisibility(View.INVISIBLE);
                        //typeChoice.setEnabled(false);
                    }else if(day.nightsAvailable){
                        day.choice = "night";
                        typeChoice.setVisibility(View.INVISIBLE);
                        //typeChoice.setEnabled(false);
                    } else {
                        typeChoice.setVisibility(View.INVISIBLE);
                    }*/
                    if(day.choice.equals("day")){
                        day.leftImg.setBackgroundResource(R.drawable.night);
                        day.rightImg.setBackgroundResource(R.drawable.day);
                    }else{
                        day.leftImg.setBackgroundResource(R.drawable.day);
                        day.rightImg.setBackgroundResource(R.drawable.night);
                    }
                    typeChoice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(day.choice.equals("day")){
                                day.choice = "night";
                            }else{
                                day.choice = "day";
                            }
                            if(day.choice.equals("day")){
                                day.leftImg.setBackgroundResource(R.drawable.night);
                                day.rightImg.setBackgroundResource(R.drawable.day);
                            }else{
                                day.leftImg.setBackgroundResource(R.drawable.day);
                                day.rightImg.setBackgroundResource(R.drawable.night);
                            }
                        }
                    });

                    Button cancel = (Button) editDialog.findViewById(R.id.button5);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.dismiss();
                        }
                    });

                    Button add = (Button) editDialog.findViewById(R.id.button6);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String hours = ""+np0.getValue();
                            if(hours.length() == 1){
                                hours = "0" + hours;
                            }

                            String minutes = ""+dp0.getValue();
                            if(minutes.length() == 1){
                                minutes = "0" + minutes;
                            }
                            day.time = hours + ":" + minutes;

                            System.out.println("choice: " + day.choice);
                            boolean duplicate = Day.checkDuplicate(day.time,day.todaysSwitches);
                            if(!duplicate) {
                                day.uploadData(position, day.choice, true);
                            }else{
                                Snackbar.make(day.fab.getRootView(), "You can not edit this change to this time because there is already a change at that time of the day!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            editDialog.dismiss();
                        }
                    });

                    editDialog.show();
                }
            });

            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d = new Dialog(context);
                    d.setTitle("Warning!");
                    d.setContentView(R.layout.dialog2);

                    TextView selectionText = (TextView) d.findViewById(R.id.textView8);
                    selectionText.setText("Switch to " + day.adapter.switchto.get(position) + " at " + day.adapter.time.get(position) + "H on " + day.day + "?");

                    Button cancel = (Button) d.findViewById(R.id.button3);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                        }
                    });

                    Button ok = (Button) d.findViewById(R.id.button4);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            day.time = "00:00";
                            day.uploadData(position,"",false);
                            d.dismiss();
                        }
                    });

                    d.show();
                }
            });
        }else{
            row=convertView;
        }
        return row;
    }


}