package nl.tue.student.thermostat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;


import com.quentindommerc.superlistview.SuperListview;
import com.quentindommerc.superlistview.SwipeDismissListViewTouchListener;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import java.util.ArrayList;

public class Day extends AppCompatActivity {
    SuperListview superList;
    String day;
    CustomScheduleListAdapter adapter;
    static Dialog d;
    static Dialog addDialog;
    FloatingActionButton fab;
    Thread loadFromServer;
    Thread uploadToServer;
    ArrayList<Switch> todaysSwitches,mondaySwitches,tuesdaySwitches,wednesdaySwitches,thursdaySwitches,fridaySwitches,saturdaySwitches,sundaySwitches;
    boolean daysAvailable = false;
    boolean nightsAvailable = false;
    public String choice = "day";
    public String time = "00:00";
    Intent starterIntent;
    ImageView leftImg;
    ImageView rightImg;
    boolean monday,tuesday,wednesday,thursday,friday,saturday,sunday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        starterIntent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            day = extras.getString("day");
        }
        toolbar.setTitle(day);
        System.out.println("Called oncreate for: " + day);

        setSupportActionBar(toolbar);

        ArrayList<String> lst = new ArrayList<String>();
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, lst);

        superList = (SuperListview) findViewById(R.id.superList);
        adapter = new CustomScheduleListAdapter(superList.getContext(),this);
        adapter.viewListVisible(true);

        superList.setAdapter(adapter);
        superList.setupSwipeToDismiss(new SwipeDismissListViewTouchListener.DismissCallbacks() {
            int selectedPosition;

            @Override
            public boolean canDismiss(int position) {
                selectedPosition = position;
                return true;
            }

            @Override
            public void onDismiss(final ListView listView, final int[] reverseSortedPositions) {
                d = new Dialog(superList.getContext());
                d.setTitle("Warning!");
                d.setContentView(R.layout.dialog2);

                TextView selectionText = (TextView) d.findViewById(R.id.textView8);
                selectionText.setText("Switch to " + adapter.switchto.get(selectedPosition) + " at " + adapter.time.get(selectedPosition) + "H on " + day + "?");

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
                        time = "00:00";
                        System.out.println(todaysSwitches.get(selectedPosition).getType());
                        uploadData(selectedPosition,"",false);
                        //uploadData(selectedPosition,todaysSwitches.get(selectedPosition).getType(),false);
                        d.dismiss();
                    }
                });

                d.show();
            }
        },false);

        //Retrieve items from the server and add them to the list.
        loadFromServer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Thread.sleep(500);
                    while(!superList.isAttachedToWindow()) {
                        Thread.sleep(10);
                        System.out.println("Waited 10 milliseconds so that the window can be attached to the superlist!");
                    }
                    final WeekProgram weekprogram = HeatingSystem.getWeekProgram();
                    todaysSwitches = weekprogram.data.get(day);

                    System.out.println("Done retrieving heatingsystem from the server!");
                    for (int i = 0; i < todaysSwitches.size(); i++) {
                        if(!todaysSwitches.get(i).getState()&&todaysSwitches.get(i).getType().equals("night")){
                            nightsAvailable = true;
                        }
                        if(!todaysSwitches.get(i).getState()&&todaysSwitches.get(i).getType().equals("day")){
                            daysAvailable = true;
                        }
                    }
                    System.out.println("nightsAvailable: " + nightsAvailable + ", daysAvailable: " + daysAvailable);

                    superList.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Adapter length: " + adapter.count);
                            adapter.removeAll();
                            System.out.println("Adapter length after removeall: " + adapter.count);
                            int counter = 0;
                            for(int j = 0; j < todaysSwitches.size(); j++){
                                if(todaysSwitches.get(j).getState()){
                                    counter++;
                                }
                            }
                            System.out.println("counter: " + counter);
                            for (int i = 0; i < todaysSwitches.size(); i++) {
                                if (todaysSwitches.get(i).getState()) {
                                    adapter.addItem(todaysSwitches.get(i).getTime(), todaysSwitches.get(i).getType());
                                    System.out.println("Added switch: (time: " + todaysSwitches.get(i).getTime() + ", type: " + todaysSwitches.get(i).getType() + ")");
                                    System.out.println(adapter.time.size());
                                }
                            }
                            ableDisableFab(adapter);
                            System.out.println("Done posting data to superList");

                        }
                    });
                    System.out.println("Done with load thread!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        loadFromServer.start();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
                addDialog = new Dialog(superList.getContext());
                addDialog.setTitle("Add Schedule");
                addDialog.setContentView(R.layout.dialog3);

                final NumberPicker np0 = (NumberPicker) addDialog.findViewById(R.id.numberPicker3);
                np0.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        return String.format("%02d", i);
                    }
                });
                np0.setMinValue(00);
                np0.setMaxValue(23);
                np0.setValue(6);
                np0.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                np0.setWrapSelectorWheel(false);
                final NumberPicker dp0 = (NumberPicker) addDialog.findViewById(R.id.numberPicker4);
                dp0.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        return String.format("%02d", i);
                    }
                });
                dp0.setMinValue(00);
                dp0.setMaxValue(59);
                dp0.setValue(30);
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

                leftImg = (ImageView) addDialog.findViewById(R.id.left_icon);
                rightImg = (ImageView) addDialog.findViewById(R.id.right_icon);
                final Button typeChoice = (Button) addDialog.findViewById(R.id.button7);
                if(daysAvailable && nightsAvailable){
                    choice = "day";
                    typeChoice.setVisibility(View.VISIBLE);
                    //typeChoice.setEnabled(true);
                }else if(daysAvailable){
                    choice = "day";
                    //typeChoice.setVisibility(View.INVISIBLE);
                    typeChoice.setText("Max of 5 night switches reached!");
                    typeChoice.setEnabled(false);
                }else if(nightsAvailable){
                    choice = "night";
                    //typeChoice.setVisibility(View.INVISIBLE);
                    typeChoice.setText("Max of 5 day switches reached!");
                    typeChoice.setEnabled(false);
                }
                if(choice.equals("day")){
                    leftImg.setBackgroundResource(R.drawable.night);
                    rightImg.setBackgroundResource(R.drawable.day);
                }else{
                    leftImg.setBackgroundResource(R.drawable.day);
                    rightImg.setBackgroundResource(R.drawable.night);
                }
                typeChoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(choice.equals("day")){
                            choice = "night";
                        }else{
                            choice = "day";
                        }
                        if(choice.equals("day")){
                            leftImg.setBackgroundResource(R.drawable.night);
                            rightImg.setBackgroundResource(R.drawable.day);
                        }else{
                            leftImg.setBackgroundResource(R.drawable.day);
                            rightImg.setBackgroundResource(R.drawable.night);
                        }
                    }
                });

                Button cancel = (Button) addDialog.findViewById(R.id.button5);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDialog.dismiss();
                    }
                });
                /**final CheckBox checkBoxMonday = (CheckBox)findViewById(R.id.mondaycheck);
                monday = checkBoxMonday.isChecked();
                final CheckBox checkBoxTuesday = (CheckBox)findViewById(R.id.tuesdaycheck);
                tuesday = checkBoxTuesday.isChecked();
                final CheckBox checkBoxWednesday = (CheckBox)findViewById(R.id.wednesdaycheck);
                wednesday = checkBoxWednesday.isChecked();
                final CheckBox checkBoxThursday = (CheckBox)findViewById(R.id.thursdaycheck);
                thursday = checkBoxThursday.isChecked();
                final CheckBox checkBoxFriday = (CheckBox)findViewById(R.id.fridaycheck);
                friday = checkBoxFriday.isChecked();
                final CheckBox checkBoxSaturday = (CheckBox)findViewById(R.id.saturdaycheck);
                saturday = checkBoxSaturday.isChecked();
                final CheckBox checkBoxSunday = (CheckBox)findViewById(R.id.sundaycheck);
                sunday = checkBoxSunday.isChecked();*/
                Button add = (Button) addDialog.findViewById(R.id.button6);
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
                        time = hours + ":" + minutes;
                        boolean duplicate = Day.checkDuplicate(time,todaysSwitches);
                        if(!duplicate) {
                            uploadData(-1, choice, true);
                        }else{
                            Snackbar.make(fab.getRootView(), "There is already a change at this time!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        addDialog.dismiss();
                    }
                });

                addDialog.show();
            }
        });
        ableDisableFab(adapter);
    }

    void ableDisableFab(Adapter a){
        if(a.getCount() > 9){
            fab.hide();
            Snackbar.make(fab.getRootView(), "There can only be 5 day-to-night and 5 night-to-day switches", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
            fab.show();
        }
    }

    void uploadData(final int position, final String type, final boolean bool){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    WeekProgram weekprogram = HeatingSystem.getWeekProgram();
                    ArrayList<Switch> todaysSwitches = weekprogram.data.get(day);
                    String daymonday="Monday";
                    String daytuesday="Tuesday";
                    String daywednesday="Wednesday";
                    String daythursday="Thursday";
                    String dayfriday="Friday";
                    String daysaturday="Saturday";
                    String daysunday="Saturday";
                    ArrayList<Switch> mondayswitched = weekprogram.data.get(daymonday);
                    ArrayList<Switch> tuesdayswitched = weekprogram.data.get(daytuesday);
                    ArrayList<Switch> wednesdayswitched = weekprogram.data.get(daywednesday);
                    ArrayList<Switch> thursdayswitched = weekprogram.data.get(daythursday);
                    ArrayList<Switch> fridayswitched = weekprogram.data.get(dayfriday);
                    ArrayList<Switch> saturdayswitched = weekprogram.data.get(daysaturday);
                    ArrayList<Switch> sundayswitched = weekprogram.data.get(daysunday);
                    int newPosition = 0;
                    if(position == -1) {
                        for (int i = 0; i < todaysSwitches.size(); i++) {
                            if (todaysSwitches.get(i).getType().equals(type) && !todaysSwitches.get(i).getState()){
                                newPosition = i;
                            }
                        }
                    }else{
                        int j = 0;
                        while(j < todaysSwitches.size() && !todaysSwitches.get(j).getState()){
                            j++;
                        }
                        newPosition = position + j;
                    }
                    String newType;
                    System.out.println("type: " + type);
                    if(type.equals("")){
                        System.out.println(todaysSwitches.get(newPosition).getType());
                        newType = todaysSwitches.get(newPosition).getType();
                    }else{
                        newType = type;
                    }
                    System.out.println("Day: " + day + ", position: " + newPosition + "time of position: " + todaysSwitches.get(newPosition).getTime() + ", type: " + newType + ", bool: " + bool + ", time:" + time);
                    weekprogram.data.get(day).set(newPosition,new Switch(newType,bool,time));
                    HeatingSystem.setWeekProgram(weekprogram);
                    if (monday && !day.equals("Monday")){
                        weekprogram.data.put(daymonday,mondayswitched).set(9, new Switch(newType,bool,time));
                        HeatingSystem.setWeekProgram(weekprogram);
                    }
                    if (tuesday && !day.equals("Tuesday")){
                        weekprogram.data.put(daytuesday,tuesdayswitched).set(9, new Switch(newType,bool,time));
                        HeatingSystem.setWeekProgram(weekprogram);
                    }
                    if (wednesday && !day.equals("Wednesday")){
                        weekprogram.data.put(daywednesday,wednesdayswitched).set(9, new Switch(newType,bool,time));
                        HeatingSystem.setWeekProgram(weekprogram);
                    }
                    if (thursday && !day.equals("Thursday")){
                        weekprogram.data.put(daythursday,thursdayswitched).set(9, new Switch(newType,bool,time));
                        HeatingSystem.setWeekProgram(weekprogram);
                    }
                    if (friday && !day.equals("Friday")){
                        weekprogram.data.put(dayfriday,fridayswitched).set(9, new Switch(newType,bool,time));
                        HeatingSystem.setWeekProgram(weekprogram);
                    }
                    if (saturday && !day.equals("Saturday")){
                        weekprogram.data.put(daysaturday,saturdayswitched).set(9, new Switch(newType,bool,time));
                        HeatingSystem.setWeekProgram(weekprogram);
                    }
                    if (sunday && !day.equals("Sunday")){
                        weekprogram.data.put(daysunday,sundayswitched).set(9, new Switch(newType,bool,time));
                        HeatingSystem.setWeekProgram(weekprogram);
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        recreate();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentIntent = NavUtils.getParentActivityIntent(this);
                if(parentIntent == null) {
                    finish();
                    return true;
                } else {
                    parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(parentIntent);
                    finish();
                    return true;
                }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void recreate(){
        try{
            Thread.sleep(500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("Restarted day activity!");
        finish();
        startActivity(starterIntent);
    }


    public static boolean checkDuplicate(String time,ArrayList<Switch> todaysSwitches){
        boolean duplicate = false;
        for(int i = 0; i < todaysSwitches.size(); i++){
            if(todaysSwitches.get(i).getTime().equals(time) && todaysSwitches.get(i).getState()){
                duplicate = true;
            }
        }
        return duplicate;
    }


    public void onCheckboxClicked(View view) {

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.mondaycheck:
                if (checked) {
                    monday=true;
                } else
                    monday = false;
                break;
            case R.id.tuesdaycheck:
                if (checked) {
                    tuesday = true;
                } else
                    tuesday = false;
                break;
            case R.id.wednesdaycheck:
                if (checked) {
                    wednesday = true;
                } else
                    wednesday = false;
                break;
            case R.id.thursdaycheck:
                if (checked) {
                    thursday = true;
                } else
                    thursday = false;
                break;
            case R.id.fridaycheck:
                if (checked) {
                    friday = true;
                } else
                    friday = false;
                break;
            case R.id.saturdaycheck:
                if (checked) {
                    saturday = true;
                } else
                    saturday = false;
                break;
            case R.id.sundaycheck:
                if (checked) {
                    sunday = true;
                } else
                    sunday = false;
                break;
        }

    }


}
