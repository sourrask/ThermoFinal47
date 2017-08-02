package nl.tue.student.thermostat;

/**
 * Created by s154563 on 11-6-2016.
 */
public class Time {
    int minutes = 0;
    int hours = 0;
    int day = 0; //day 0 is monday

    public void setTime(int days, int hours, int minutes) {
        days = this.day;
        hours = this.hours;
        minutes = this.hours;
    }

    public void setTime(String day, String time) {
        switch (day) {
            case "Monday":
                this.day = 0;
                break;
            case "Tuesday":
                this.day = 1;
                break;
            case "Wednesday":
                this.day = 2;
                break;
            case "Thursday":
                this.day = 3;
                break;
            case "Friday":
                this.day = 4;
                break;
            case "Saturday":
                this.day = 5;
                break;
            case "Sunday":
                this.day = 6;
                break;
        }
        String[] timeSplit = time.split(":");
        hours = Integer.parseInt(timeSplit[0]);
        minutes = Integer.parseInt(timeSplit[1]);
    }

    public void increaseTime() {
        minutes++;
        if (minutes == 60) {
            minutes = 0;
            hours++;
        }
        if (hours == 24) {
            hours = 0;
            day++;
        }
        if (day == 7) {
            day = 0;
        }
    }

    public String getMinutesString() {
        String minutesS = Integer.toString(minutes);
        if (minutesS.length() == 1) {
            minutesS = "0" + minutesS;
        }
        return minutesS;
    }

    public String getHoursString() {
        String hoursS = Integer.toString(hours);
        if (hoursS.length() == 1) {
            hoursS = "0" + hoursS;
        }
        return hoursS;
    }

    public String getTomorrowString() {
        this.day++;
        if (this.day == 7)
            this.day = 0;
        String day = getDayString();
        this.day--;
        if (this.day == -1)
            this.day = 6;
        return day;
    }

    public String getDayString() {
        switch (day) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
            case 6:
                return "Sunday";
            default:
                return "";
        }
    }

    public int getMinutes() {
        return minutes;
    }

    public int getHours() {
        return hours;
    }

    public int getDay() {
        return day;
    }

    public boolean hasNotYetComeToPass(String time) { //I feel poetic
        String[] timeSplit = time.split(":");
        int hh = Integer.parseInt(timeSplit[0]);
        int mm = Integer.parseInt(timeSplit[1]);

        if (hh > hours) {
            return true;
        } else if (hh == hours && mm >= minutes) {
            return true;
        } else {
            return false;
        }

    }
}
