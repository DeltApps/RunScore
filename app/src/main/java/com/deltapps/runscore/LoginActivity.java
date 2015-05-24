package com.deltapps.runscore;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.initialize(this, "F5Q8Kr8RfqUcToRMfzqEkRtlGExNErs1vq3APjix",
                "eRadbh2qFcQft2xYwyo0EAp6b1jJKiiTBUMuBErd");

        MyParse myparse = new MyParse();
        if(myparse.isLogged()) {
            startActivity(new Intent(this, HistorialActivity.class));
            finish();
        }else
            setContentView(R.layout.activity_login);

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

    public void clickLogin (View view) {
        EditText userEditText = (EditText) findViewById(R.id.user);
        EditText passEditText = (EditText) findViewById(R.id.pass);
        final String user = userEditText.getText().toString();
        final String pass = passEditText.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                MyParse myparse = new MyParse();
                // Comprobamos si el login es correcto (true)
                if(myparse.logIn(user, pass)) {
                    Intent intent = new Intent(LoginActivity.this, HistorialActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

        Toast.makeText(getApplicationContext(),
                "Iniciando sesión...", Toast.LENGTH_SHORT).show();

    }

    public void clickSignUp (View view) {
        EditText userEditText = (EditText) findViewById(R.id.userS);
        EditText emailEditText = (EditText) findViewById(R.id.emailS);
        EditText passEditText = (EditText) findViewById(R.id.passS);
        EditText pass2EditText = (EditText) findViewById(R.id.passS2);
        final String user = userEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String pass = passEditText.getText().toString();
        final String pass2 = pass2EditText.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                MyParse myparse = new MyParse();
                // Comprobamos si el registro es correcto (true)
                if(myparse.signUp(user, email, pass, pass2)) {
                    Intent intent = new Intent(LoginActivity.this, HistorialActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "No se ha podido registrar", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

        Toast.makeText(getApplicationContext(),
                "Haciendo registro...", Toast.LENGTH_SHORT).show();

    }
}
