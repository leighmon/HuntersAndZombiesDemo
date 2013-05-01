package com.example.huntersandzombiesdemo;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

class UpdateLocationTimerTask extends TimerTask {
	private Handler handler;
	
	private GPSTracker gpsTracker;
	private GoogleMap googleMap;
	private int iteration;

	private Marker myLocationMarker;
	
	public UpdateLocationTimerTask(Context context, GPSTracker gpsTracker, GoogleMap googleMap) {
		this.gpsTracker = gpsTracker;
		this.googleMap = googleMap;
		this.handler = new Handler();
		this.myLocationMarker = null;
		this.iteration = 0;

		if (this.gpsTracker.canGetLocation()) {
			Location myLocation = this.gpsTracker.getLocation();
			myLocationMarker = googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).title("Me!"));
		}
	}
	
	@Override
	public void run() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				handler.post(new Runnable(){
					@Override
					public void run() {
						if (gpsTracker.canGetLocation()) {
							myLocationMarker.remove();
							Location myLocation = gpsTracker.getLocation();
							iteration++;
							myLocationMarker = googleMap.addMarker(new MarkerOptions()
								.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).title("Me!" + iteration));
						}
					}});
			}}).start();
	}
}

public class Dashboard extends FragmentActivity {
	private static final int UPDATE_LOCATION_PERIOD = 5000;
	private Button inventoryButton;
	private Button scoreButton;
	private Button duelButton;
	private Button resetButton;
    private GoogleMap googleMap;
    public final static String USER_NAME = "com.example.huntersandzombies.USERNAME";
    public final static String USER_MONEY = "com.example.huntersandzombies.MONEY";
    public final static String INVENTORY = "com.example.huntersandzombies.INVENTORY";
    public final static int INVENTORY_REQUEST = 1;
    public static String username;
    public static int money;   
    public static ArrayList<String> inventory = new ArrayList<String>();
    public static GPSTracker gpsTracker;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		Intent intent = getIntent();
		Parse.initialize(this, "IVMpQf3ccNsiWfdfufivTkjlMHOYC5dgAO8APfjB", "aeC7EJihUm9MQw5lZqw38OnIWvhAY93MJ2JLDm3M"); 
		ParseAnalytics.trackAppOpened(getIntent());
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
		  // get the new user
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
			finish();
		} else {		
        setUpMapIfNeeded();
		gpsTracker = new GPSTracker(this);
		Timer timer = new Timer();
		TimerTask updateLocation = new UpdateLocationTimerTask(Dashboard.this, gpsTracker, googleMap);
		timer.scheduleAtFixedRate(updateLocation, 0, UPDATE_LOCATION_PERIOD);
		
		inventory = new ArrayList<String>();
		scoreButton = (Button) findViewById(R.id.scoreButton);
		duelButton = (Button) findViewById(R.id.duelButton);
		inventoryButton = (Button) findViewById(R.id.inventoryButton);
		resetButton = (Button) findViewById(R.id.resetBtn);
		scoreButton.setOnClickListener(scoreHandler);
		inventoryButton.setOnClickListener(inventoryHandler);
		duelButton.setOnClickListener(duelHandler);
		resetButton.setOnClickListener(resetHandler);
		
		if(!intent.hasExtra(USER_MONEY)){
			money = 100; //default value
			showInstructions();
		}
		else{
			money = intent.getIntExtra(USER_NAME, 100);
		}
	}
		

	}
	
	private void showInstructions(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				Dashboard.this);
		alertDialogBuilder.setTitle("Welcome to Hunters and Zombies!");
		alertDialogBuilder.setMessage("Click on the duel button to show the zombies in your area, and click on the shop button to show your inventory of weapons!");
		alertDialogBuilder.setNeutralButton("Close", null).show();
	}
	
	View.OnClickListener resetHandler = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			money = 100;
			inventory = new ArrayList<String>();
			Context context = getApplicationContext();
			CharSequence text = "Application has been reset";
			int duration = Toast.LENGTH_SHORT;
//			Toast.makeText(context, text, duration).show();
			showInstructions();
			

		}
	};
	/*
	 * on click listener for the scorebutton
	 */
	View.OnClickListener scoreHandler = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle bundle = new Bundle();
			bundle.putString(USER_NAME, username);
			bundle.putInt(USER_MONEY, money);
			//bundle.putStringArray(INVENTORY, inventory);
			Intent intent = new Intent(Dashboard.this, Score.class);
			intent.putExtras(bundle);
//			startActivityForResult(intent, 1); //add request code for each
			startActivity(intent);
		}
	};
	
	/**
	 * On click listener for the inventory button
	 */
	View.OnClickListener inventoryHandler = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(Dashboard.this, Inventory.class);
			Bundle bundle = new Bundle();
			bundle.putString(USER_NAME, username);
			bundle.putInt(USER_MONEY, money);
			bundle.putStringArrayList(INVENTORY, inventory);
			intent.putExtras(bundle);
			startActivityForResult(intent, INVENTORY_REQUEST);
		}
	};
	
	/**
	 * On click listener for the duel handler button
	 */
	View.OnClickListener duelHandler = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Bundle bundle = new Bundle();
			bundle.putString(USER_NAME, username);
			bundle.putInt(USER_MONEY, money);
			bundle.putStringArrayList(INVENTORY, inventory);
//			bundle.putStringArray(INVENTORY, inventory);
			Intent intent = new Intent(Dashboard.this, Duel.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};
	
	/**
	 * Basically need to store the data and do nothing more, so it can be passed to other screens
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
		switch(requestCode){
		case INVENTORY_REQUEST: 
			if(extras!=null){
				System.out.println("working");
			money = extras.getInt(USER_MONEY);
			inventory = extras.getStringArrayList(INVENTORY);
			}
			break;
		}
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
		if(!intent.hasExtra(USER_MONEY)){
			money = 100; //default value
		}
		else{
			
			money = bundle.getInt(USER_MONEY);
		}
        
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}
	
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #googleMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
    	
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
//    	money = 100;
    	googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//    	if (googleMap.isMyLocationEnabled()) {
//    		Location x = googleMap.getMyLocation();
//    		googleMap.addMarker(
//    				new MarkerOptions()
//    				.position(new LatLng(x.getLatitude(), x.getLongitude()))
//    				.title("Here you are!"));
//            googleMap.addMarker(new MarkerOptions()
//      	.position(new LatLng(42.36036686, -71.08679982))
//	     	.title("Wohoo!"));
//  	}
    	googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.35848655, -71.09211361), 21.0f));
    }
}
