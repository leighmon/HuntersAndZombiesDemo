package com.example.huntersandzombiesdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class DuelUserActivity extends Activity {
	private String duelUsername;
	private TextView userLabel;
	private Button duelBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_duel_user);
		Intent data = getIntent();
		if (data!=null){
			duelUsername = data.getStringExtra(Duel.DUEL_USERNAME);
			userLabel = (TextView) findViewById(R.id.duelUserName);
			userLabel.setText(duelUsername);
		}
		
		duelBtn = (Button) findViewById(R.id.duelButton);
		duelBtn.setOnClickListener(new OnClickListener(){
			@Override
		      public void onClick(View v) {
//				Intent intent = new Intent(Duel.this, DuelUserActivity.class);
//				intent.putExtra(DUEL_USERNAME, users.get(v.getId()));
//				startActivity(intent); 
//				Thread.sleep(1000);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						DuelUserActivity.this);
				alertDialogBuilder.setTitle("Congratulations!");
				alertDialogBuilder.setMessage("You have won a duel against "+duelUsername+"!\nYou have won 20 coins.");
				Dashboard.money+=20;
				alertDialogBuilder.setNeutralButton("Close",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						launchIntent();
					}
				} ).show(); 
		      }
		});
	}
	
	private void launchIntent() {
        Intent it = new Intent(DuelUserActivity.this,Dashboard.class);
        startActivity(it); 
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.duel_user, menu);
		return true;
	}

}