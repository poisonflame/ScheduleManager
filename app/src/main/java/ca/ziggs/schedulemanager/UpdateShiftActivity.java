package ca.ziggs.schedulemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Robby Sharma on 6/12/2017.
 */

public class UpdateShiftActivity extends AppCompatActivity {

    EditText editTitle,editName,editDate,editTime,editDuration,editBreaks;
    Button btnCancel,btnUpdate;
    String startTime,endTime;
    DBHandler db;//= new DBHandler(getApplicationContext());
    Calendar calendar = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    Calendar calendar3 = Calendar.getInstance();
    String rawDate;
    String rawStartTime,rawEndTime,rawStartTimeFormatted,rawEndTimeFormatted;
    String duration;
    String breaks;
    String rawDateAndStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_shift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBHandler(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        final Integer id = bundle.getInt("contentID");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //String id = getIntent().getStringExtra("contentID");
        final List<JobEntry> updateEntry = db.getShiftFromID(String.valueOf(id));

        //Initialize all TextBoxes and Buttons
        editTitle = (EditText)findViewById(R.id.editJobTitleUpdate);
        editName = (EditText)findViewById(R.id.editWorkplaceNameUpdate);
        editDate = (EditText)findViewById(R.id.editShiftDateUpdate);
        editTime = (EditText)findViewById(R.id.editShiftTimeUpdate);
        editDuration = (EditText)findViewById(R.id.editDurationUpdate);
        editBreaks = (EditText)findViewById(R.id.editBreaksUpdate);
        btnCancel = (Button)findViewById(R.id.btnCancelUpdate);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        editTitle.setKeyListener(null);
        editDuration.setKeyListener(null);
        editDate.setKeyListener(null);
        editBreaks.setKeyListener(null);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDial = new AlertDialog.Builder(view.getContext());
                alertDial.setMessage("Are you sure you want to cancel?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Yes.
                        finish();
                    }
                })
                        .setNegativeButton("No",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //No.
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alert = alertDial.create();
                alert.setTitle("Are you sure?");
                alert.show();

            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editBreaks.getText().toString().trim() == "" || editDuration.getText().toString().trim() == "" ||
                        editDate.getText().toString().trim() == "" || editTitle.getText().toString().trim() == "" ||
                        editName.getText().toString().trim() == "" || editTime.getText().toString().trim() == ""){
                    Toast.makeText(getApplicationContext(),"Please don't leave any fields empty.",Toast.LENGTH_LONG).show();
                }else{
                    // Add to DB

                    if(rawStartTime == null){
                        rawStartTime = updateEntry.get(0).getStartTime();
                    }

                    if(rawEndTime == null){
                        rawEndTime = updateEntry.get(0).getEndTime();
                    }

                    if(rawDate == null){
                        rawDate = updateEntry.get(0).getDate();
                    }

                    if(rawDateAndStartTime == null){
                        rawDateAndStartTime = updateEntry.get(0).getDateTime();
                    }

                    try {
                        final SimpleDateFormat sdf2 = new SimpleDateFormat("H:mm");
                        final Date dateObject2 = sdf2.parse(updateEntry.get(0).getStartTime());
                        rawStartTimeFormatted = new SimpleDateFormat("HH:mm").format(dateObject2);
                    }catch (ParseException e){
                        //nope.
                    }

                    //rawStartTimeFormatted

                    rawDateAndStartTime = rawDate + " " + rawStartTimeFormatted+":00";

                    AlertDialog.Builder alertDial = new AlertDialog.Builder(view.getContext());
                    alertDial.setMessage("Are you sure you want to save changes?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Yes.
                            boolean updateCheck  = db.updateEntry(id,editTitle.getText().toString(),editName.getText().toString(),editDuration.getText().toString(),rawStartTime,rawEndTime,editBreaks.getText().toString(),rawDate,editDate.getText().toString(),editTime.getText().toString(),rawDateAndStartTime);

                            //Toast.makeText(getApplicationContext(),rawDateAndStartTime,Toast.LENGTH_LONG).show();

                            if(updateCheck == true){
                                Toast.makeText(getApplicationContext(),"Shift Updated",Toast.LENGTH_SHORT).show();
                                finish();
                                Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                                mIntent.putExtra("loadFragment","scheduleFragment");
                                startActivity(mIntent);
                            }else{
                                Toast.makeText(getApplicationContext(),"Error Occurred. Make sure everything is filled out.",Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                            .setNegativeButton("No",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //No.
                                    dialogInterface.cancel();
                                }
                            });

                    AlertDialog alert = alertDial.create();
                    alert.setTitle("Confirm Changes?");
                    alert.show();




                    //db.addNewEntry(new JobEntry(editTitle.getText().toString(),editName.getText().toString(),editDuration.getText().toString(),rawStartTime,rawEndTime,editBreaks.getText().toString(),rawDate,editDate.getText().toString(),editTime.getText().toString(),rawDateAndStartTime));


                }
                //Toast.makeText(getApplicationContext(),"Added Crap",Toast.LENGTH_SHORT).show();
            }
        });

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = new DatePickerDialog(UpdateShiftActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(new Date().getTime());
                dialog.setMessage("Select Shift Date");
                dialog.show();
            }
        });

        editTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                TimePickerDialog dialog = new TimePickerDialog(UpdateShiftActivity.this,timeListener,calendar2.get(Calendar.HOUR_OF_DAY),calendar2.get(Calendar.MINUTE),false);
                dialog.setMessage("Select Shift Start Time");
                dialog.show();
            }
        });

        //Toast.makeText(getApplicationContext(),String.valueOf(id),Toast.LENGTH_LONG).show();
        //editTitle.setText(String.valueOf(db.getEn(String.valueOf(id))));

       for(JobEntry job:updateEntry){
           editTitle.setText(job.getTitle());
           editName.setText(job.getLocation());
           editDuration.setText(job.getDuration());
           editTime.setText(job.getFormattedTime());
           editBreaks.setText(job.getBreaks());
           editDate.setText(job.getFormattedDate());
           //Toast.makeText(getApplicationContext(),"d:"+job.getTitle(),Toast.LENGTH_LONG).show();
       }


//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean updateCheck  = db.updateEntry(0,editTitle.getText().toString(),editName.getText().toString(),editDuration.getText().toString(),startTime,endTime,editBreaks.getText().toString(),editDate.getText().toString(),editDate.getText().toString(),editTime.getText().toString(),editTime.getText().toString());
//
//                if(updateCheck == true){
//                    Toast.makeText(getApplicationContext(),"Shift Updated",Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(getApplicationContext(),"Error Occurred. Make sure everything is filled out.",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });


    }

    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int min) {
            String time=hour+":"+min;
            try{
                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                final Date dateObject = sdf.parse(time);
                editTime.setText(new SimpleDateFormat("hh:mm aa").format(dateObject));
                //calendar2.set(calendar2.get(Calendar.YEAR),calendar2.get(Calendar.MONTH),calendar2.get(Calendar.DAY_OF_WEEK),hour,min);
                calendar2.set(Calendar.HOUR_OF_DAY, hour);
                calendar2.set(Calendar.MINUTE, min);

                final SimpleDateFormat sdf2 = new SimpleDateFormat("H:mm");
                final Date dateObject2 = sdf2.parse(time);
                rawStartTimeFormatted= new SimpleDateFormat("HH:mm").format(dateObject2);

                rawStartTime = hour+":"+min;
                TimePickerDialog dialog = new TimePickerDialog(UpdateShiftActivity.this,timeListener2,calendar3.get(Calendar.HOUR_OF_DAY),calendar3.get(Calendar.MINUTE),false);
                dialog.setMessage("Select Shift End Time");
                dialog.show();
            }catch (final ParseException e){
                e.printStackTrace();
            }

            //shiftTime.setText(hour_formatted + ":"+ min + " " + suffix);
        }
    };

    TimePickerDialog.OnTimeSetListener timeListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int min) {
            String time=hour+":"+min;
            try{
                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                final Date dateObject = sdf.parse(time);
                editTime.append(" - " + new SimpleDateFormat("hh:mm aa").format(dateObject));
                //shiftTime.setText(new SimpleDateFormat("hh:mm aa").format(dateObject));
                //calendar2.set(calendar2.get(Calendar.YEAR),calendar2.get(Calendar.MONTH),calendar2.get(Calendar.DAY_OF_WEEK),hour,min);
                calendar3.set(Calendar.HOUR_OF_DAY, hour);
                calendar3.set(Calendar.MINUTE, min);

                final SimpleDateFormat sdf2 = new SimpleDateFormat("H:mm");
                final Date dateObject2 = sdf2.parse(time);
                rawEndTimeFormatted= new SimpleDateFormat("HH:mm").format(dateObject2);

                rawEndTime=hour+":"+min;

                //Calculate Time Difference
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                Date startOne = simpleDateFormat.parse(rawStartTime);
                Date endOne = simpleDateFormat.parse(rawEndTime);
                long differenceInTwo = endOne.getTime() - startOne.getTime();
                if(differenceInTwo<0){
                    Date dateMax = simpleDateFormat.parse("24:00");
                    Date dateMin = simpleDateFormat.parse("00:00");
                    differenceInTwo=(dateMax.getTime() -startOne.getTime() )+(endOne.getTime()-dateMin.getTime());
                }
                int days = (int) (differenceInTwo / (1000*60*60*24));
                int hours = (int) ((differenceInTwo - (1000*60*60*24*days)) / (1000*60*60));
                int mins = (int) (differenceInTwo - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
                editDuration.setText(hours+"h " + mins+"m");
                duration = hours+"h "+mins+"m";

                //Calculate breaks based on Walmarts formula.
                if(hours <= 4){
                    editBreaks.setText("1 Coffee");
                    breaks = "1 Coffee";
                }else if(hours == 5 && mins==0){
                    editBreaks.setText("1 Coffee");
                    breaks="1 Coffee";
                }else if(hours == 5 && mins > 0){
                    editBreaks.setText("1 Half, 1 Coffee");
                    breaks = "1 Half, 1 Coffee";
                }else if(hours >5 && hours <7){
                    editBreaks.setText("1 Half, 1 Coffee");
                    breaks = "1 Half, 1 Coffee";
                }else if(hours >= 7 ){
                    editBreaks.setText("1 Lunch, 2 Coffee");
                    breaks = "1 Lunch, 2 Coffee";
                }

            }catch (final ParseException e){
                e.printStackTrace();
            }
        }
    };

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {

            String day_formatted, month_formatted;
            if(String.valueOf(day).endsWith("11")) {
                day_formatted = String.valueOf(day) + "th";
            }else if(String.valueOf(day).endsWith("12")) {
                day_formatted = String.valueOf(day) + "th";
            }else if(String.valueOf(day).endsWith("13")){
                day_formatted = String.valueOf(day) + "th";
            }else if(String.valueOf(day).endsWith("1")){
                day_formatted = String.valueOf(day) + "st";
            }else if(String.valueOf(day).endsWith("2")){
                day_formatted = String.valueOf(day) + "nd";
            }else if(String.valueOf(day).endsWith("3")){
                day_formatted = String.valueOf(day) + "rd";
            }else{
                day_formatted = String.valueOf(day) + "th";
            }
            month += 1;
            if(month == 1){
                month_formatted = "January";
            }else if(month == 2){
                month_formatted = "February";
            }else if(month == 3){
                month_formatted = "March";
            }else if(month == 4){
                month_formatted = "April";
            }else if(month == 5){
                month_formatted = "May";
            }else if(month == 6){
                month_formatted = "June";
            }else if(month == 7){
                month_formatted = "July";
            }else if(month == 8){
                month_formatted = "August";
            }else if(month == 9){
                month_formatted = "September";
            }else if(month == 10){
                month_formatted = "October";
            }else if(month == 11){
                month_formatted = "November";
            }else if(month == 12){
                month_formatted = "December";
            }else{
                month_formatted = "";
            }

            //calendar.set(year,month, day);
            calendar.set(year,month-1,day);

            //GregorianCalendar GregorianCal = new GregorianCalendar(year,month,day -2);
            //int dayOfWeek = GregorianCal.get(Calendar.DAY_OF_WEEK);
            String dayName;

            SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
            try{
                Date date = inFormat.parse(day+"-"+month+"-"+year);
                SimpleDateFormat outFormat  = new SimpleDateFormat("EEEE");
                dayName = outFormat.format(date);
            }catch (ParseException e){
                dayName = "";
            }

//            if(dayOfWeek == Calendar.MONDAY){
//                dayName = "Monday";
//            }else if(dayOfWeek == Calendar.TUESDAY){
//                dayName = "Tuesday";
//            }else if(dayOfWeek == Calendar.WEDNESDAY){
//                dayName = "Wednesday";
//            }else if (dayOfWeek == Calendar.THURSDAY){
//                dayName = "Thursday";
//            }else if (dayOfWeek == Calendar.FRIDAY){
//                dayName = "Friday";
//            }else if(dayOfWeek == Calendar.SATURDAY){
//                dayName = "Saturday";
//            }else if(dayOfWeek == Calendar.SUNDAY){
//                dayName = "Sunday";
//            }else{
//                dayName = "";
//            }


            editDate.setText(dayName +", " + day_formatted + " " + month_formatted + " " + year);
            //rawDate = day+"/"+month+"/"+year;
            try{
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                final Date dateObject = sdf.parse(year+"-"+(month)+"-"+day);
                String datess = new SimpleDateFormat("yyyy-mm-dd").format(dateObject);
                rawDate = datess;
            }catch (ParseException e){
                // DO CRAP.
                rawDate = year+"-"+month+"-"+day;
            }

            //rawDate = year+"-"+month+"-"+day;
        }

    };

}
