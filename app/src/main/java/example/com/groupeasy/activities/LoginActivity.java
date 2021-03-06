package example.com.groupeasy.activities;

/**
 * This Class Allows existing users to login using their credentials
 * New users can click on new user to go to create account page
 * NEW: Developer shortcut, clicking on GroupEasy icon will log the user in
 * **/

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import example.com.groupeasy.R;
import example.com.groupeasy.utility.prefManager;

/**
 *  Entry point of the app .
 *  Login using email & password
 * */

public class LoginActivity extends AppCompatActivity {

    //initilize varaibles
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context context;
    private Button btnLoginEmail, register;
    private TextInputLayout userEmail, userPassword;
    private ImageView BtnLogin;

    private ProgressDialog mRegProgress;
//    private RelativeLayout myRelativeLayout;
    private TextInputEditText EmailText;
    private TextInputEditText PassText;

    /** Firebase db init*/
    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
    final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
    private prefManager PrefManager;
    private DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("members");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Tried to use Preference Manager to stop user from coming to this screen everytime he opens up
        //This featuer is Still buggy, as the user still ends up on this screen if he removes app from the navigation drawer
      /*  PrefManager = new prefManager(this);
        if (!PrefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }*/

        /*Link to XML layout*/
        setContentView(R.layout.activity_login);

        // initialize the context
        this.context = LoginActivity.this;
        /* Calling methods for
        1. Initialising elements by IDs
        2. Initialising elements with Listeners*/
        initElementsWithIds();
        initElementsWithListeners();
        mAuth = FirebaseAuth.getInstance();
        mRegProgress = new ProgressDialog(this);
        checkUser();

    }

    private void checkUser() {


        try {
            String CurrentUser = current_user.getUid();
            Log.w(CurrentUser, "currentUSer");

            try {
                String test = getIntent().getExtras().getString("keyName");
            } catch (Exception e) {
                BtnLogin.performClick();
            }


        } catch (Exception e) {

            Log.w("no user", "currentUSer");
            String email = "peter@peter.com";
            String password = "password";

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                    } else {
                        mRegProgress.hide();
                        String error;
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            error = "Password is weak";
                        } catch (FirebaseAuthInvalidUserException e) {
                            error = "Invalid Email!";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            error = "Please check if you do not have an account, or the entered details are right";
                        } catch (Exception e) {
                            error = "Unknown error!";
                            e.printStackTrace();
                        }
                        Toast.makeText(LoginActivity.this, R.string.check_details_or_resigistered, Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    private void launchHomeScreen() {
//        PrefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        finish();
    }

    //this snippet to disallow back key
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /*Handling all onclick events(listeners)*/
    private void initElementsWithListeners() {

        /***developer shortcut**/
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(current_user != null){
                Intent intent = new Intent(context,DashboardActivity.class);
                startActivity(intent);
                finish();
                }

                else
                    Toast.makeText(LoginActivity.this, "Make sure you are logged in", Toast.LENGTH_SHORT).show();
            }
        });

        btnLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, createAccount.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = userEmail.getEditText().getText().toString();
                String password = userPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mRegProgress.setTitle("Checking your credentials");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.setMessage("Please wait");
                    mRegProgress.show();

                    loginUser(email,password);
                }
                else{
                    Toast.makeText(LoginActivity.this, R.string.email_pass_req,Toast.LENGTH_LONG).show();
                    //code for if empty
                }
            }
        });

        //Hide keyboard onTouchedOutside
        EmailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //Hide keyboard onTouchedOutside
        PassText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    // Failed code
    // code for checking if device is connected to internet
//    public static boolean hasActiveInternetConnection(Context context) {
//        if (isNetwokAvailable(context)) {
//            try {
//                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
//                urlc.setRequestProperty("User-Agent", "Test");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1500);
//                urlc.connect();
//                return (urlc.getResponseCode() == 200);
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error checking internet connection", e);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Log.d(LOG_TAG, "No network available!");
//        }
//        return false;
//    }
//    private static boolean isNetwokAvailable(Context context) {
//     return false;
//    }

    /*Initialize all ui elements by linking to xml ids*/
    private void initElementsWithIds()
    {
        BtnLogin = (ImageView) findViewById(R.id.login_logo);
        btnLoginEmail = (Button) findViewById(R.id.btn_login_email);
        register = (Button) findViewById(R.id.Register);
        userEmail = (TextInputLayout) findViewById(R.id.user_email);
        userPassword = (TextInputLayout) findViewById(R.id.user_password);
//      myRelativeLayout = (RelativeLayout) findViewById(R.id.my_login_relative_layout);
        EmailText = (TextInputEditText) findViewById(R.id.user_email_text);
        PassText = (TextInputEditText) findViewById(R.id.user_pass_text);

    }

    //If user details are authentic, he is navigated into this method and his email& pass are passed as arguments
    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    mRegProgress.dismiss();
                    String device_token = FirebaseInstanceId.getInstance().getToken();
                    String current_user = mAuth.getCurrentUser().getUid();

                    mUserDB.child(current_user).child("device_token").setValue(device_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent intent = new Intent(context,DashboardActivity.class);
                            intent.putExtra("userName","value");
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                else{
                    mRegProgress.hide();
                    String error;
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e){
                        error = "Password is weak";
                    } catch (FirebaseAuthInvalidUserException e) {
                        error = "Invalid Email!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error = "Please check if you do not have an account, or the entered details are right";
                    } catch (Exception e) {
                        error = "Unknown error!";
                        e.printStackTrace();
                    }
                   Toast.makeText(LoginActivity.this, R.string.check_details_or_resigistered, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
