package nl.tue.student.thermostat;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import org.thermostatapp.util.HeatingSystem;

import java.util.Timer;
import java.util.TimerTask;

//This interface would help in swiping views
public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {


    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    NavigationView navigationView;

    static boolean useSchedule;

    TimerTask secondaryThreadTask;

    Thread secondaryThread;
    public static final Time time = new Time();
    public static double currentTemp;
    public static double currentDayTemp;
    public static double currentNightTemp;
    public static double targetTemp;
    boolean monday,tuesday,wednesday,thursday,friday,saturday,sunday;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Thermostat"));
        tabLayout.addTab(tabLayout.newTab().setText("Schedule"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                //, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        //toggle.syncState();

        //navigationView = (NavigationView) findViewById(R.id.nvView);

        secondaryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String getTime;
                    String getDay;
                    String getTemp;
                    String getDayTemp;
                    String getNightTemp;
                    String getTargetTemp;


                    //weekProgram = HeatingSystem.getWeekProgram();


                    getTargetTemp = HeatingSystem.get("targetTemperature");
                    targetTemp = Double.parseDouble(getTargetTemp);

                    getTemp = HeatingSystem.get("currentTemperature");
                    currentTemp = Double.parseDouble(getTemp);

                    getDayTemp = HeatingSystem.get("dayTemperature");
                    currentDayTemp = Double.parseDouble(getDayTemp);

                    getNightTemp = HeatingSystem.get("nightTemperature");
                    currentNightTemp = Double.parseDouble(getNightTemp);

                    getDay = HeatingSystem.get("day");
                    getTime = HeatingSystem.get("time");
                    time.setTime(getDay, getTime);

                    String getParam;
                    getParam = HeatingSystem.get("weekProgramState");
                    if (getParam.equals("on")) {
                        Homepage.upcomingChangesVisible = true;
                        Homepage.useSwitch.setChecked(true);
                        Homepage.useSchedule = true;

                    } else if (getParam.equals("off")){
                        Homepage.upcomingChangesVisible = false;
                        Homepage.useSwitch.setChecked(false);
                    }
                    secondaryThread.wait();
                } catch (Exception e) {
                    //System.err.println(e);
                }
            }
        });
        secondaryThreadTask = new TimerTask() {
            @Override
            public void run() {
                secondaryThread.run();
            }
        };
        Timer timer2 = new Timer();
        timer2.schedule(secondaryThreadTask, 0, 2000);



    }



    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (viewPager.getCurrentItem() == 1) { //move back to homepage
            viewPager.setCurrentItem(0);
        }else {
            super.onBackPressed();
        }
    }

    public static boolean isUsingSchedule() {
        return useSchedule;
    }


}
