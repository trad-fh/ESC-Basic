package com.example.escbasicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageButton addContact;
    private ImageButton contact;
    private TextView phoneNum;
    private TextView[] dials = new TextView[10];
    private TextView star;
    private TextView sharp;
    private ImageButton message;
    private ImageButton call;
    private ImageButton backSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        setUpUI();

        if (phoneNum.getText().length() == 0) {
            message.setVisibility(View.GONE);
            backSpace.setVisibility(View.GONE);
        }
    }

    private void checkPermissions() {
        int resultCall = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int resultSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (resultCall == PackageManager.PERMISSION_DENIED || resultSms == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS}, 1001);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 허용됨", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "권한 허용이 필요합니다. 설정에서 허용을 해주세요.", Toast.LENGTH_SHORT).show();
                Log.d("PermissionDenied", "권한이 거부되어 앱을 종료합니다.");
                finish();
            }
        }
    }

    private void setUpUI() {
        // 설정
        addContact = findViewById(R.id.main_ibtn_add);
        contact = findViewById(R.id.main_ibtn_contact);
        phoneNum = findViewById(R.id.main_tv_phone);

        for (int i = 0; i < dials.length; i++) {
            dials[i] = findViewById(getResourceID("main_tv_"+ i, "id", this));
        }

        star = findViewById(R.id.main_tv_star);
        sharp = findViewById(R.id.main_tv_sharp);
        message = findViewById(R.id.main_ibtn_message);
        call = findViewById(R.id.main_ibtn_call);
        backSpace = findViewById(R.id.main_ibtn_backspace);

        //AddContact
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivity(addIntent);
            }
        });

        //Contact
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contact = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(contact);
            }
        });

        //Dials (import star,sharp)
        setOnClickDial(star, "*");
        setOnClickDial(sharp, "#");

        for (int i = 0; i < 10; i++) {
            setOnClickDial(dials[i], String.valueOf(i));
        }

        //Message
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageIntent = new Intent(MainActivity.this, MessageActivity.class);
                messageIntent.putExtra("phone_num", phoneNum.getText().toString());
                startActivity(messageIntent);
            }
        });

        //Call
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent =  new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum.getText()));
                startActivity(callIntent);
            }
        });

        //BackSpace
        backSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNum.getText().length() > 0) {
                    phoneNum.setText(changeToDial(phoneNum.getText().subSequence(0, phoneNum.getText().length() - 1).toString()));

                    if (phoneNum.getText().length() == 0) {
                        message.setVisibility(View.GONE);
                        backSpace.setVisibility(View.GONE);
                    }
                }
            }
        });

        backSpace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                phoneNum.setText("");

                message.setVisibility(View.GONE);
                backSpace.setVisibility(View.GONE);

                return true;
            }
        });
    }

    private void setOnClickDial(View view, final String input) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message.setVisibility(View.VISIBLE);
                backSpace.setVisibility(View.VISIBLE);
                phoneNum.setText(changeToDial(phoneNum.getText() + input));
            }
        });
    }

    private int getResourceID (final String resName, final String resType, final Context ctx) {
        final int ResourceID =
                ctx.getResources().getIdentifier(resName, resType, ctx.getApplicationInfo().packageName);
        if (ResourceID == 0){
            throw new IllegalArgumentException("No resource string found with name");
        } else {
            return ResourceID;
        }
    }

    private  String changeToDial (String phoneNum) {
        phoneNum = phoneNum.replaceAll("-", "");

        if (phoneNum.contains("*") || phoneNum.contains("#") || phoneNum.contains("+")) return phoneNum;

        if (phoneNum.length() > 11) return  phoneNum;
        else if (phoneNum.length() >7) return phoneNum.substring(0, 3) + "-" + phoneNum.substring(3, 7) + "-" + phoneNum.substring(7, phoneNum.length());
        else if (phoneNum.length() > 3) return phoneNum.substring(0,3) + "-" + phoneNum.substring(3,phoneNum.length());
        else return phoneNum;
    }
}