package net.mechanixlab.das.uber;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.mechanixlab.das.uber.Common.Common;
import net.mechanixlab.das.uber.Model.User;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button btnSingIn,btnRegister;
    RelativeLayout rootlayout;


    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fornt/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);

        //int firebase

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference(Common.user_driver_tbl);


        //int view

        btnRegister = findViewById(R.id.register);
        btnSingIn = findViewById(R.id.btnSingin);
        rootlayout = findViewById(R.id.rootLayout);


        //event

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });


        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }


        });
    }

    private void showLoginDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("SING IN");
        dialog.setMessage("Please use mail for regiartion");
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View login_layout = inflater.inflate(R.layout.layout_login,null);
        final MaterialEditText edtEmail = login_layout.findViewById(R.id.etdEmail);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.etdPassword);

        edtEmail.setText("sndasnsu@gmail.com");
        edtPassword.setText("1234567");


        dialog.setView(login_layout);

        dialog.setPositiveButton("SING IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                  btnSingIn.setEnabled(false);
                //cheek text validation

                if (TextUtils.isEmpty(edtEmail.getText().toString())){

                    Snackbar.make(rootlayout," Please enter the Sing in",Snackbar.LENGTH_SHORT).show();
                    return;

                }


                if (TextUtils.isEmpty(edtPassword.getText().toString())){

                    Snackbar.make(rootlayout," Please enter your password",Snackbar.LENGTH_SHORT).show();
                    return;

                }

                if (edtPassword.getText().toString().length() <6 ){

                    Snackbar.make(rootlayout," Password is too short",Snackbar.LENGTH_SHORT).show();
                    return;

                }


                final SpotsDialog watingDalog = new SpotsDialog(MainActivity.this);
                watingDalog.show();

                //loging

                auth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())

                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        watingDalog.dismiss();
                        Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);

                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        watingDalog.dismiss();
                        btnSingIn.setEnabled(true);
                        Snackbar.make(rootlayout,"Failed"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });


        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        dialog.show();


    }

            private void showRegisterDialog() {

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("REGISTER");
                dialog.setMessage("Please use mail for regiartion");
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View registation = inflater.inflate(R.layout.registation,null);
                final MaterialEditText edtEmail = registation.findViewById(R.id.etdEmail);
                final MaterialEditText edtName = registation.findViewById(R.id.etdName);
                final MaterialEditText edtPhone = registation.findViewById(R.id.etdPhone);
                final MaterialEditText edtPassword = registation.findViewById(R.id.etdPassword);




                dialog.setView(registation);

                dialog.setPositiveButton("REGISTIER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        //cheek text validation

                        if (TextUtils.isEmpty(edtEmail.getText().toString())){

                            Snackbar.make(rootlayout," Please enter the mail address",Snackbar.LENGTH_SHORT).show();
                            return;

                        }

                        if (TextUtils.isEmpty(edtName.getText().toString())){

                            Snackbar.make(rootlayout," Please enter  Your name",Snackbar.LENGTH_SHORT).show();
                            return;

                        }

                        if (TextUtils.isEmpty(edtPhone.getText().toString())){

                            Snackbar.make(rootlayout," Please enter the phone number",Snackbar.LENGTH_SHORT).show();
                            return;

                        }

                        if (TextUtils.isEmpty(edtPassword.getText().toString())){

                            Snackbar.make(rootlayout," Please enter your password",Snackbar.LENGTH_SHORT).show();
                            return;

                        }

                        if (edtPassword.getText().toString().length() <6 ){

                            Snackbar.make(rootlayout," Password is too short",Snackbar.LENGTH_SHORT).show();
                            return;

                        }


                        //register new user


                        auth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        //save user to database

                                        User user = new User();
                                        user.setEmail(edtEmail.getText().toString());
                                        user.setPassword(edtPassword.getText().toString());
                                        user.setName(edtName.getText().toString());
                                        user.setPhone(edtPhone.getText().toString());

                                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Snackbar.make(rootlayout," Registation succesfully!!1",Snackbar.LENGTH_SHORT).show();
                                                    }


                                                })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootlayout," Sorry"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                                            }
                                        });




                                    }
                                })      .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootlayout," Sorry"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                            }
                        }) ;




                    }
                });

                dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                dialog.show();


            }


    }

