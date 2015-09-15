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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends Activity {


    private Button confirmButton;
    private EditText inputPassword;
    private EditText inputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Find input boxes
        inputPassword = (EditText) findViewById(R.id.PasswordBox);
        inputEmail = (EditText) findViewById(R.id.EmailBox);

        //Find confirm button and add functionality
        confirmButton = (Button) findViewById(R.id.ConfirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = inputPassword.getText().toString();
                String email = inputEmail.getText().toString();

                //If all the input is valid, send the info to the server
                if (password.trim().length() > 0 &&
                        email.trim().length() > 0)
                {
                    registerUser(email, password);
                } else {  //Show error message to user
                    Toast.makeText(getApplicationContext(),
                            "Enter your credentials first!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private boolean registerUser(final String email, final String password) {
        //tag used for cancelling request
        String req_tag = "req_login";

        StringRequest req = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override  //If succesfull, should move to next screen
                    public void onResponse(String response) {
                        Log.d("login", "Login response: " + response);

                        try {
                            //Create JSONObject, easier to work with
                            JSONObject JResponse = new JSONObject(response);
                            boolean error = JResponse.getBoolean("error");
                            if (!error) {
                                String name = JResponse.getString("name");
                                String email = JResponse.getString("email");
                                UserStatic.setName(name);
                                UserStatic.setEmail(email);
                                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override //If not succesfull, show user error message
            public void onErrorResponse(VolleyError error) {
                Log.e("login", "Login error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        })  {
            @Override // Set all parameters for for server
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("password", password);
                params.put("email", email);
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
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
