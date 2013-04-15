package com.example.thebreakroom;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private TextView mUserName;
	private TextView mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Parse.initialize(this, "nSec4SOo4RDJhbMJhvX1JdcdAgg5VF0mQdkkdHKG", "1IMivB5ci1yc4QBSHeGjrYNltKmeU29VWQPjzzfx");
		Button login = (Button) findViewById(R.id.login);
		mUserName = (TextView)findViewById(R.id.login_username);
		mPassword = (TextView)findViewById(R.id.login_password);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				login();
			}
		});
	}

	public void login() {
		ParseUser.logInInBackground(mUserName.getText().toString(), mPassword.getText().toString(),
				new LogInCallback() {
					@Override
					public void done(ParseUser user, ParseException e) {
						if (user != null) {
							startGroupActivity();
						} else {
							makeToast("Login failed. Please try again later.");
						}
					}
				});
	}

	public void startGroupActivity() {
		Intent urlIntent = new Intent(this, GroupListActivity.class);
		// urlIntent.putExtra(WebActivity.URL, authUrl);
		this.startActivity(urlIntent);
	}
	
	public void makeToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
