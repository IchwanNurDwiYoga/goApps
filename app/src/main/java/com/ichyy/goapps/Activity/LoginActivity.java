package com.ichyy.goapps.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ichyy.goapps.R;
import com.ichyy.goapps.model.User;
import com.ichyy.goapps.response.ResponseLogin;
import com.ichyy.goapps.rest.ApiClient;
import com.ichyy.goapps.rest.ApiInterface;
import com.ichyy.goapps.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.btnLogin)
    Button login;

    @BindView(R.id.etUsername)
    EditText etusername;

    @BindView(R.id.etPassword)
    EditText etpassword;

    @BindView(R.id.tvregister)
    TextView register_user;

    ApiInterface apiservice;
    SessionManager sessionManager;

    private static final String TAG = "LoginActivity";

    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        setContentView(R.layout.login_relative);
        ButterKnife.bind(this);

        apiservice = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(this);



        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
        }
        });
    }

    private void loginUser() {
        String username = etusername.getText().toString();
        String password = etpassword.getText().toString();

        apiservice.login(username,password).enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if(response.isSuccessful())
                {
                    User userLoggedIn = response.body().getUser();
                    sessionManager.createLoginSession(userLoggedIn);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                }
                else if (!response.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "User Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Log.e(TAG, "Onfailure: "+t.getLocalizedMessage());
                Toast.makeText(LoginActivity.this,"Gagal Terhubung ke Server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
