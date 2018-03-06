package com.example.denish.bloodbank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.denish.bloodbank.MainActivity.RC_TYPE;

public class SelectGroup extends AppCompatActivity {

    private static final String TAG = "SelectGroup";
    EditText mSelectGroup,mMobileno;
    Button mNext;
    String type;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);

        mSelectGroup = findViewById(R.id.et_select_group);
        mMobileno = findViewById(R.id.et_mobileno);
        mNext = findViewById(R.id.btn_next);


        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Inside SelectGroup Listener");
                if((type = mSelectGroup.getText().toString()).length() > 0 &&
                        type.equals("O+") || type.equals("O-") || type.equals("A-") || type.equals("A+")
                        || type.equals("B+") || type.equals("B-") || type.equals("AB-") || type.equals("AB+")){

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("group", type); // Storing string
                    editor.putString("mobile",mMobileno.getText().toString());
                    editor.apply();
                    Log.d(TAG, "onClick: preference added (group,mobile) :" + type + "," + mMobileno.getText().toString());

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                    intent.putExtra("group","O+ve");
//                    intent.putExtra("group",type);
//                    intent.putExtra("mobile","9988998877");
//                    intent.putExtra("mobile",mMobileno.getText().toString());
//                    setResult(RC_TYPE,intent);
                    startActivity(intent);
                    //finish();
                }else{
                    Toast.makeText(SelectGroup.this, "Write Proper Blood Group Please", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }



}
