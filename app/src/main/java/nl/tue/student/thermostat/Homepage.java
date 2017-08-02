package nl.tue.student.thermostat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.thermostatapp.util.CorruptWeekProgramException;
import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Homepage extends Fragment {
    TextView targetTemp;
    TextView currentTime;
    TextView currentTemp;
    TextView currentDay;
    ImageButton imageButtonDown;
    ImageButton imageButtonUp;
    SeekArc seekArc;
    boolean seekArcIsBeingTouched = false;
    double arcTemp;
    TimerTask task;
    long clockDelay = 200;

     static boolean useSchedule = false;

     static android.widget.ToggleButton useSwitch;

    TimerTask uiUpdateTask;

    static boolean upcomingChangesVisible;
    ImageView noSchedule;

    ImageView iv_icon0;
    TextView txt_name0;
    ImageView iv_icon1;
    TextView txt_name1;
    ImageView iv_icon2;
    TextView txt_name2;
    ImageView iv_icon00;
    TextView txt_name00;
    ImageView iv_icon11;
    TextView txt_name11;
    ImageView iv_icon22;
    TextView txt_name22;

    ArrayList<ImageView> imageViews = new ArrayList<>();
    ArrayList<TextView> textViews = new ArrayList<>();
    ArrayList<ImageView> imageViews1 = new ArrayList<>();
    ArrayList<TextView> textViews1 = new ArrayList<>();


    Time time = MainActivity.time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage, container, false);
        targetTemp = (TextView)view.findViewById(R.id.targetTemp);
        currentTime = (TextView)view.findViewById(R.id.currentTime);
        currentTemp = (TextView) view.findViewById(R.id.currentTemp);
        currentDay = (TextView) view.findViewById(R.id.currentDay);
        imageButtonDown = (ImageButton) view.findViewById(R.id.imageButtonDown);
        imageButtonUp = (ImageButton) view.findViewById(R.id.imageButtonUp);


        iv_icon0 = (ImageView) view.findViewById(R.id.iv_icon0);
        txt_name0 = (TextView) view.findViewById(R.id.txt_name0);
        iv_icon1 = (ImageView) view.findViewById(R.id.iv_icon1);
        txt_name1 = (TextView) view.findViewById(R.id.txt_name1);
        iv_icon2 = (ImageView) view.findViewById(R.id.iv_icon2);
        txt_name2 = (TextView) view.findViewById(R.id.txt_name2);

        iv_icon00 = (ImageView) view.findViewById(R.id.iv_icon00);
        txt_name00 = (TextView) view.findViewById(R.id.txt_name00);
        iv_icon11 = (ImageView) view.findViewById(R.id.iv_icon11);
        txt_name11 = (TextView) view.findViewById(R.id.txt_name11);
        iv_icon22 = (ImageView) view.findViewById(R.id.iv_icon22);
        txt_name22 = (TextView) view.findViewById(R.id.txt_name22);

        imageViews.add(iv_icon0);
        textViews.add(txt_name0);
        imageViews.add(iv_icon1);
        textViews.add(txt_name1);
        imageViews.add(iv_icon2);
        textViews.add(txt_name2);
        imageViews1.add(iv_icon00);
        textViews1.add(txt_name00);
        imageViews1.add(iv_icon11);
        textViews1.add(txt_name11);
        imageViews1.add(iv_icon22);
        textViews1.add(txt_name22);


        noSchedule = (ImageView) view.findViewById(R.id.noSchedule);
        useSwitch = (android.widget.ToggleButton) view.findViewById(R.id.ToggleButton);


        useSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (useSchedule) {
                    useSchedule = false;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HeatingSystem.put("weekProgramState", "off");
                                useSwitch.setChecked(false);
                                setUpcomingChangesListVisible(false);
                            } catch (Exception e) {
                                System.err.println("Error from getdata "+e);
                            }
                        }
                    }).start();
                } else {
                    useSchedule = true;


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HeatingSystem.put("weekProgramState", "on");
                                useSwitch.setChecked(true);
                                setUpcomingChangesListVisible(true);
                            } catch (Exception e) {
                                System.err.println("Error from getdata "+e);
                            }
                        }
                    }).start();
                }
            }
        });


        task = new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            WeekProgram weekProgram = HeatingSystem.getWeekProgram();

                            ArrayList<Switch> todaysSwitches = weekProgram.data.get(time.getDayString());
                            ArrayList<Switch> tomorrowsSwitches = weekProgram.data.get(time.getTomorrowString());


                            final ArrayList<Integer> icons = new ArrayList<Integer>();
                            final ArrayList<Integer> icons2 = new ArrayList<Integer>();
                            final ArrayList<String> texts = new ArrayList<String>();
                            final ArrayList<String> texts2 = new ArrayList<String>();


                            //putting all the correct texts and icons in the arraylists of today
                            if (!todaysSwitches.isEmpty()) {
                                for (int i = 0; i < todaysSwitches.size(); i++) {
                                    Switch aSwitch = todaysSwitches.get(i);
                                    if (aSwitch.getState() && time.hasNotYetComeToPass(aSwitch.getTime())) {
                                        final int icon;
                                        final int icon2;
                                        final String getTime;
                                        final String temp;

                                        getTime = aSwitch.getTime();
                                        if (aSwitch.getType().equals("day")) {
                                            icon2 = R.drawable.day;
                                            icon = R.drawable.night;
                                            temp = Double.toString(MainActivity.currentDayTemp);
                                        } else {
                                            icon2 = R.drawable.night;
                                            icon = R.drawable.day;
                                            temp = Double.toString(MainActivity.currentNightTemp);
                                        }
                                        icons.add(icon);
                                        //texts.add(getTime);
                                        texts.add("-- " + getTime + " -->");
                                        icons2.add(icon2);
                                        texts2.add(temp + "°C  |  today");
                                    }
                                }
                            }

                            //putting all the correct texts and icons in the arraylists of tomorrow
                            if (!tomorrowsSwitches.isEmpty()) {
                                for (int i = 0; i < tomorrowsSwitches.size(); i++) {
                                    Switch aSwitch = tomorrowsSwitches.get(i);
                                    if (aSwitch.getState()) {
                                        final int icon;
                                        final int icon2;
                                        final String getTime;
                                        final String temp;

                                        getTime = aSwitch.getTime();
                                        if (aSwitch.getType().equals("day")) {
                                            icon2 = R.drawable.day;
                                            icon = R.drawable.night;
                                            temp = Double.toString(MainActivity.currentDayTemp);
                                        } else {
                                            icon2 = R.drawable.night;
                                            icon = R.drawable.day;
                                            temp = Double.toString(MainActivity.currentNightTemp);
                                        }
                                        icons.add(icon);
                                        //texts.add(getTime);
                                        texts.add("-- " + getTime + " -->");
                                        icons2.add(icon2);
                                        texts2.add(temp + "°C  |  tomorrow");
                                    }
                                }
                            }

                            //getting the icons and texts from the arraylists to the correct imageviews and textviews
                            txt_name0.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (upcomingChangesVisible) {
                                        noSchedule.setVisibility(View.INVISIBLE);
                                        if (icons.size() > 2) {
                                            for (int i = 0; i < 3; i++) {
                                                imageViews.get(i).setImageResource(icons.get(i));
                                                imageViews1.get(i).setImageResource(icons2.get(i));
                                                textViews.get(i).setText(texts.get(i));
                                                textViews1.get(i).setText(texts2.get(i));
                                            }

                                        } else if (icons.size() == 2) {
                                            for (int i = 0; i < 2; i++) {
                                                imageViews.get(i).setImageResource(icons.get(i));
                                                imageViews1.get(i).setImageResource(icons2.get(i));
                                                textViews.get(i).setText(texts.get(i));
                                                textViews1.get(i).setText(texts2.get(i));
                                            }
                                            imageViews.get(2).setImageResource(0);
                                            imageViews1.get(2).setImageResource(0);
                                            textViews.get(2).setText("");
                                            textViews1.get(2).setText("");

                                        } else if (icons.size() == 1) {
                                            imageViews.get(0).setImageResource(icons.get(0));
                                            imageViews1.get(0).setImageResource(icons2.get(0));
                                            textViews.get(0).setText(texts.get(0));
                                            textViews1.get(0).setText(texts2.get(0));
                                            for (int i = 1; i < 3; i++) {
                                                imageViews.get(i).setImageResource(0);
                                                imageViews1.get(i).setImageResource(0);
                                                textViews.get(i).setText(" ");
                                                textViews1.get(i).setText(" ");
                                            }

                                        } else {
                                            for (int i = 0; i < 3; i++) {
                                                imageViews.get(i).setImageResource(0);
                                                imageViews1.get(i).setImageResource(0);
                                                textViews.get(i).setText("");
                                                textViews1.get(i).setText("");
                                            }
                                            textViews.get(1).setText("No upcoming changes");
                                        }
                                    } else {
                                        for (int i=0; i<3; i++) {
                                            imageViews.get(i).setImageResource(0);
                                            imageViews1.get(i).setImageResource(0);
                                            textViews.get(i).setText("");
                                            textViews1.get(i).setText("");
                                        }
                                        noSchedule.setVisibility(View.VISIBLE);
                                    }
                                }
                            });




                        } catch (ConnectException e) {
                            System.err.println("Error from getdata " + e);
                        } catch (CorruptWeekProgramException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {

                        }



                    }
                }).run();

                currentTime.post(new Runnable() {
                    @Override
                    public void run() {
                        currentTime.setText(time.getHoursString() + ":" + time.getMinutesString());
                        time.increaseTime();
                        currentTemp.setText(Double.toString(MainActivity.currentTemp) + " \u00B0" + "C");
                        currentDay.setText(time.getDayString());

                        if (!seekArcIsBeingTouched) {
                            seekArc.setProgress((int) ((10 *MainActivity.targetTemp) - 50));
                        }

                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 0, clockDelay);



        //importing the arc
        seekArc = (SeekArc)view.findViewById(R.id.seekArc);

        seekArc.setMax(250);
        seekArc.setStartAngle(0);
        seekArc.setSweepAngle(280);
        seekArc.setTouchInSide(true);
        seekArc.setArcWidth(10);
        seekArc.setArcRotation(220);
        seekArc.setProgressWidth(50);
        seekArc.setRoundedEdges(true);

        String cold = "#674F22";
        String hot = "#674F22";
        String midStr = "0";

        StringBuilder result = new StringBuilder("#");
        for (int i=0;i<3;i++) {
            String h1 = cold.substring(i*2+1, 3+(i*2));
            String h2 = hot.substring(i*2+1, 3+(i*2));

            double l1 = Long.parseLong(h1, 16);
            double l2 = Long.parseLong(h2, 16);

            double progress = (double) seekArc.getProgress();

            progress = progress/255;

            long mid = (long) (((1 - progress)*l1) + (progress * l2)); //truncating not rounding

            midStr = Long.toString(mid, 16);
            if (midStr.length() == 1) {
                result.append("0");
            }
            result.append(midStr.toUpperCase());

        }
        seekArc.setProgressColor(Color.parseColor(result.toString()));
        seekArc.setArcColor(Color.parseColor(result.toString()));

        seekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int j, boolean b) {

                // hard to set the slider to the extremes, this takes care of that
                arcTemp = (double)seekArc.getProgress()/10+5;
                if(arcTemp>30) arcTemp = 30;
                if(arcTemp<5) arcTemp = 5;

                targetTemp.setText(Double.toString(arcTemp) + " \u00B0" + "C"); //tweaky temporary solution



                String cold = "#3a87bf";
                String hot = "#3a87bf";
                String midStr = "0";

                StringBuilder result = new StringBuilder("#");
                for (int i=0;i<3;i++) {
                    String h1 = cold.substring(i*2+1, 3+(i*2));
                    String h2 = hot.substring(i*2+1, 3+(i*2));

                    double l1 = Long.parseLong(h1, 16);
                    double l2 = Long.parseLong(h2, 16);

                    double progress = (double) seekArc.getProgress();

                    progress = progress/255;

                    long mid = (long) (((1 - progress)*l1) + (progress * l2)); //truncating not rounding

                    midStr = Long.toString(mid, 16);
                    if (midStr.length() == 1) {
                        result.append("0");
                    }
                    result.append(midStr.toUpperCase());

                }
                seekArc.setProgressColor(Color.parseColor(result.toString()));
                seekArc.setArcColor(Color.parseColor(result.toString()));
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
                seekArcIsBeingTouched = true;
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("targetTemperature", Double.toString(arcTemp));

                        } catch (Exception e) {
                            System.err.println("Error from getdata "+e);
                        }
                    }
                }).start();
                seekArcIsBeingTouched = false;
                MainActivity.targetTemp = ((double) seekArc.getProgress())/10 + 5;
            }
        });

        imageButtonDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                seekArc.setProgress(seekArc.getProgress()-1);
                arcTemp = (double)seekArc.getProgress()/10+5;
                if(arcTemp>30) arcTemp = 30;
                if(arcTemp<5) arcTemp = 5;
                arcTemp = (double) Math.round(arcTemp * 10) / 10;
                System.out.println(arcTemp);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("targetTemperature", Double.toString(arcTemp));

                        } catch (Exception e) {
                            System.err.println("Error from getdata "+e);
                        }
                    }
                }).start();
               MainActivity.targetTemp = MainActivity.targetTemp - 0.1;
            }
        });

        imageButtonUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                seekArc.setProgress(seekArc.getProgress()+1);
                arcTemp = (double)seekArc.getProgress()/10+5;
                if(arcTemp>30) arcTemp = 30;
                if(arcTemp<5) arcTemp = 5;
                arcTemp = (double) Math.round(arcTemp * 10) / 10;
                System.out.println(arcTemp);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("targetTemperature", Double.toString(arcTemp));

                        } catch (Exception e) {
                            System.err.println("Error from getdata "+e);
                        }
                    }
                }).start();
                MainActivity.targetTemp = MainActivity.targetTemp + 0.1;
            }
        });

        uiUpdateTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (useSchedule) {
                            if (useSwitch.isChecked()) {
                                setUpcomingChangesListVisible(true);

                            }
                        } else if (!useSchedule){
                            if (!useSwitch.isChecked()) {
                                setUpcomingChangesListVisible(false);
                            }
                        }
                    }
                });
            }

            private void runOnUiThread(Runnable runnable) {
            }


        };
        Timer timer2 = new Timer();
        timer2.schedule(uiUpdateTask, 0, 10);



        //updating the current temperature
        targetTemp.setText(Double.toString(((double) seekArc.getProgress())/10 + 5) + " \u00B0" + "C");





        return view;


    }





    public static void setUpcomingChangesListVisible(boolean b) {
        if (!b) {
            upcomingChangesVisible = false;
        } else if (b) {
            upcomingChangesVisible = true;
        }
    }




}

