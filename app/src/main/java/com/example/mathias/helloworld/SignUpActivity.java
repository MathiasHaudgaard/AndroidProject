package com.example.mathias.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mathias.helloworld.HomeActivity.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mathias on 07-09-2015.
 */
public class SignUpActivity extends Activity {
    private Activity thisActivity = this;

    private Button confirmButton;
    private EditText inputName;
    private EditText inputPassword;
    private EditText inputConfirmPassword;
    private EditText inputEmail;
    private EditText inputPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signup);

        //Find input boxes
        inputName = (EditText) findViewById(R.id.NameBox);
        inputPassword = (EditText) findViewById(R.id.PasswordBox);
        inputConfirmPassword = (EditText) findViewById(R.id.ConfirmPasswordBox);
        inputEmail = (EditText) findViewById(R.id.EmailBox);
        inputPhoneNumber = (EditText) findViewById(R.id.PhoneNumberBox);
        //Find confirm button and add functionality
        confirmButton = (Button) findViewById(R.id.ConfirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString();
                String password = inputPassword.getText().toString();
                String confirmPassword = inputConfirmPassword.getText().toString();
                String email = inputEmail.getText().toString();
                String phoneNumber = inputPhoneNumber.getText().toString();

                //If all the input is valid, send the info to the server
                if (name.trim().length() > 0 &&
                        password.trim().length() > 0 &&
                        email.trim().length() > 0 &&
                        phoneNumber.trim().length() > 0 &&
                        password.equals(confirmPassword))
                {
                    registerUser(name, password, email, phoneNumber);
                } else {  //Show error message to user
                    Toast.makeText(getApplicationContext(),
                            "Enter your credentials first!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private boolean registerUser(final String name, final String password, final String email, final String phoneNumber) {
        //tag used for cancelling request
        String req_tag = "req_register";

        StringRequest req = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override  //If succesfull, should also log in user
                    public void onResponse(String response) {
                        Log.d("register", "Register response: " + response);

                        try {
                            //Create JSONObject, easier to work with
                            JSONObject JResponse = new JSONObject(response);
                            boolean error = JResponse.getBoolean("error");
                            if (!error) {
                                JSONObject JUser = JResponse.getJSONObject("user");
                                String name = JUser.getString("name");
                                String email = JUser.getString("email");
                                String phoneNumber = JUser.getString("phonenumber");
                                UserStatic.setName(name);
                                UserStatic.setEmail(email);
                                UserStatic.setPhoneNumber(phoneNumber);
                                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override //If not succesfull, show user error message
            public void onErrorResponse(VolleyError error) {
                Log.e("register", "Register error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        })  {
           @Override // Set all parameters for for server
            protected Map<String, String> getParams() {
               Map<String, String> params = new HashMap<String, String>();
               params.put("tag", "register");
               params.put("name", name);
               params.put("password", password);
               params.put("email", email);
               params.put("phonenumber", phoneNumber);
               return params;
           }
       };

        //add tag to request
        req.addMarker(req_tag);
        //Adding request to request queue
        NetworkSingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

