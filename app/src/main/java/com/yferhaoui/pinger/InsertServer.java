package com.yferhaoui.pinger;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class InsertServer extends AppCompatActivity {

    private EditText name;
    private EditText ip;
    private EditText ipMac;
    private EditText externalPort;
    private EditText internalPort;
    private RadioGroup isPublic;

    private EditText setExpiryDate;
    private DatePickerDialog expiryDate;
    private Integer expiryYear;
    private Integer expiryMonth;
    private Integer expiryDay;

    private Button cancel;
    private Button enter;

    private LinearLayout form;

    private String action;

    private DBHelper dataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_server);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.toolbar));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final Intent intent = getIntent();
        this.dataBase = new DBHelper(this);

        ((TextView)this.findViewById(R.id.header_text)).setText(intent.getStringExtra("action") + " Server");

        form = findViewById(R.id.form);

        this.action = intent.getStringExtra("action");

        name = findViewById(R.id.name_input);
        name.setText(intent.getStringExtra("name"));

        ip = findViewById(R.id.ip_input);
        ip.setText(intent.getStringExtra("ip"));
        if (this.action.equals("Update")) {
            ip.setFocusable(false);
            ip.setClickable(false);
            //ip.setBackground(R.id.background_button_block);
        }

        ipMac = findViewById(R.id.ip_mac_input);
        ipMac.setText(intent.getStringExtra("ipMac"));

        externalPort =  findViewById(R.id.external_port_input);
        if (intent.getIntExtra("externalPort", 0) != 0) {
            externalPort.setText(String.valueOf(intent.getIntExtra("externalPort", 0)));
        }

        internalPort = findViewById(R.id.internal_port_input);
        if (intent.getIntExtra("internalPort", 0) != 0) {
            internalPort.setText(String.valueOf(intent.getIntExtra("internalPort", 0)));
        }

        isPublic = findViewById(R.id.radio_public_input);
        boolean select = intent.getBooleanExtra("isPublic", true);
        if (!select) {
            isPublic.check(R.id.no);
        }

        final Long theDate = intent.getLongExtra("expiryDate", 0);
        setExpiryDate = (EditText) findViewById(R.id.date_edit);
        setExpiryDate.setInputType(InputType.TYPE_NULL);
        setExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cldr = Calendar.getInstance();
                if (theDate != 0) {
                    cldr.setTimeInMillis(theDate);
                }
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                expiryDate = new DatePickerDialog(InsertServer.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                InsertServer.this.expiryYear = year;
                                InsertServer.this.expiryMonth = monthOfYear;
                                InsertServer.this.expiryDay = dayOfMonth;
                                setExpiryDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                expiryDate.show();
            }
        });


        if (theDate != null && theDate != 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(theDate);
            this.expiryYear = cal.get(Calendar.YEAR);
            this.expiryMonth = cal.get(Calendar.MONTH);
            this.expiryDay = cal.get(Calendar.DAY_OF_MONTH);
            setExpiryDate.setText( this.expiryDay + "/" + (this.expiryMonth + 1) + "/" + this.expiryYear);
        }

        cancel = findViewById(R.id.cancel);
        enter = findViewById(R.id.enter);
        enter.setText(intent.getStringExtra("action"));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertServer.this.finish();
            }
        });



        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InsertServer.this.finish();
                String nameS = InsertServer.this.name.getText().toString();
                String ipS = InsertServer.this.ip.getText().toString();
                if( nameS != null && ipS != null && !nameS.equals("") && !ipS.equals("")) {


                    if (InsertServer.this.action.equals("Add")) {
                        boolean containsName = InsertServer.this.dataBase.contains("name", nameS);
                        boolean containsIp = InsertServer.this.dataBase.contains("ip", ipS);

                        if (!containsName && !containsIp && InsertServer.this.expiryYear != null && InsertServer.this.expiryMonth != null && InsertServer.this.expiryDay != null) {
                            Long dateExipryS = new GregorianCalendar(InsertServer.this.expiryYear,
                                                                InsertServer.this.expiryMonth,
                                                                InsertServer.this.expiryDay).getTime().getTime();

                            String ipMacS = InsertServer.this.ipMac.getText().toString();
                            String externalPortS = InsertServer.this.externalPort.getText().toString();
                            String internalPortS = InsertServer.this.internalPort.getText().toString();
                            boolean isPublicS = InsertServer.this.isPublic.getCheckedRadioButtonId() ==  R.id.yes;

                            InsertServer.this.dataBase.insertServer(ipS, nameS, ipMacS, externalPortS, internalPortS, isPublicS, dateExipryS);

                            Intent refresh = new Intent(InsertServer.this, MainActivity.class);
                            startActivity(refresh);
                            InsertServer.this.finish();
                        } else if (containsName) {
                            Toast.makeText(InsertServer.this, "Name already used !", Toast.LENGTH_LONG).show();
                        } else if (containsIp) {
                            Toast.makeText(InsertServer.this, "Ip already used !", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(InsertServer.this, "Please enter a date !", Toast.LENGTH_LONG).show();
                        }
                    } else {

                        boolean containsName = InsertServer.this.dataBase.contains("name", nameS);

                        if (!containsName || (containsName && nameS.equals(intent.getStringExtra("name")))) {
                            Long dateExipryS = new GregorianCalendar(InsertServer.this.expiryYear,
                                    InsertServer.this.expiryMonth,
                                    InsertServer.this.expiryDay).getTime().getTime();

                            String ipMacS = InsertServer.this.ipMac.getText().toString();
                            String externalPortS = InsertServer.this.externalPort.getText().toString();
                            String internalPortS = InsertServer.this.internalPort.getText().toString();
                            boolean isPublicS = InsertServer.this.isPublic.getCheckedRadioButtonId() ==  R.id.yes;

                            InsertServer.this.dataBase.updateServer(ipS, nameS, ipMacS, externalPortS, internalPortS, isPublicS, dateExipryS);

                            Intent refresh = new Intent(InsertServer.this, MainActivity.class);
                            startActivity(refresh);
                            InsertServer.this.finish();
                        } else {
                            Toast.makeText(InsertServer.this, "Name already used !", Toast.LENGTH_LONG).show();
                        }

                    }



                } else if (nameS == null || nameS.equals("")) {
                    Toast.makeText(InsertServer.this, "Name cannot be empty !", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(InsertServer.this, "Ip cannot be empty !", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
