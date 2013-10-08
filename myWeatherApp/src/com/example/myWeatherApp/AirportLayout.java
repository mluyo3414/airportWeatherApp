package com.example.myWeatherApp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * @author Miguel S
 * This class contains each element in the listview that contains the airport information
 */
public class AirportLayout extends Activity
{
	//default methods
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_airport_layout );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.airport_layout, menu );
		return true;
	}

}
