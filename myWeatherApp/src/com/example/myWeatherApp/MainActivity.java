package com.example.myWeatherApp;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.widget.SearchViewCompatIcs.MySearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * @author Miguel S //First activity in the app. Prompts the user to enter any
 *         string related to a city or airport in order to find specific
 *         information about an airport
 */
public class MainActivity extends Activity
{
	// objects in the GUI
	static String theNewData = " ";
	TextView codeText;
	TextView locationText;
	TextView nameText;
	Button retrieveWeatherButton;
	Button retrieveAirportsButton;
	EditText city;
	String updateCity;
	GetDataAsyncTask myActivity;
	ListView lv;
	// TAGS for listview
	private static final String airportList = "Airport";
	private static final String tagCode = "code";
	private static final String tagLocation = "location";
	private static final String tagName = "name";
	private static final String tagWeather = "Weather";
	// Array for all the airports
	ArrayList<HashMap<String, String>> receivedAirportArrayList =
			new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{

		// creating objects from the GUI
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		codeText = (TextView) findViewById( R.id.Code );
		lv = (ListView) findViewById( R.id.listView );
		locationText = (TextView) findViewById( R.id.Location );
		nameText = (TextView) findViewById( R.id.Name );
		retrieveAirportsButton = (Button) findViewById( R.id.retrieveAirport );
		city = (EditText) findViewById( R.id.city );
		myActivity = new GetDataAsyncTask( this );
		// once the user typed a city, app waits until button is pressed
		buttonPressed();

	}

	/**
	 * @param returnedMap
	 * Creates Listview with the information received
	 */
	public void dataReceivedFromNetwork(
			ArrayList<HashMap<String, String>> returnedMap )
	{
		
		receivedAirportArrayList = returnedMap;
		ListAdapter adapter =
				new SimpleAdapter( this, receivedAirportArrayList,
						R.layout.activity_airport_layout, new String[] {
								tagCode, tagLocation, tagName }, new int[] {
								R.id.Code, R.id.Location, R.id.Name } );
		lv.setAdapter( adapter );

		lv.setOnItemClickListener( new OnItemClickListener()
		{
			//triggers new activity once the airport is selected

			@Override
			public void onItemClick( AdapterView<?> parent, View view,
					int position, long id )
			{
				String weatherLocation =
						((TextView) view.findViewById( R.id.Location ))
								.getText().toString();
				
				weatherLocation = weatherLocation.substring( 10 );
				// passes only the location to the new activity
				Intent in =
						new Intent( getApplicationContext(), WeatherNode.class );
				in.putExtra( "Location", weatherLocation );
				//start new activity
				startActivity( in );

			}
		} );
		myActivity.cancel( true );
		myActivity = new GetDataAsyncTask( this );
		// reseting array

		buttonPressed();

	}

	/**
	 * Waits for the user to press the button and connect to the API
	 */
	public void buttonPressed()
	{
		retrieveAirportsButton.setOnClickListener( new View.OnClickListener()
		{

			public void onClick( View view )
			{
				if ( city.getText().toString() != "" )

				{
					// gets information from the Text Edit
					updateCity = city.getText().toString();
					// replaces spaces
					updateCity = updateCity.replaceAll( "\\s+", "+" );
					// clears previous airports stored in the array list
					receivedAirportArrayList.clear();
					//executes new thread passing the information 
					myActivity.execute( updateCity );

				}

			}
		} );

	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

}
