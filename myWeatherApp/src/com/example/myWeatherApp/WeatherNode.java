package com.example.myWeatherApp;

import java.util.Arrays;
import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

/**
 * @author Miguel S
 * This class retrieves the information from the airport chosen
 * by the user
 */
public class WeatherNode extends Activity
{
  Location myLoc;
  TextView weather;
  GetWeather getWeatherAsync;
  String airportCode;
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		// creating variables and objects
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_weather_node );
		weather = (TextView) findViewById(R.id.input);
		//retrieves information from the intent in main
		Intent in= getIntent();
	    Bundle b = in.getExtras();
	    getWeatherAsync =new GetWeather( this );
	    
	    if(b!=null)
	    {
	    	 // getting information
	        airportCode =b.get("Location").toString();
	         // string manipulation to obtain the city and make sure it is not two words
	        // otherwise replace the space with +
	        List<String> cityList = Arrays.asList(airportCode.split(","));
			String city = cityList.get( 0 );
			city = city.replaceAll( "\\s+", "+" );
			String country = cityList.get( cityList.size()-1).trim();
	       // app only supports US due to API (takes only country codes)
			if ( country.equals( "United States" ))
			{
				
				getWeatherAsync.execute( city );
			}
			
			else updateText( "LOCATION NOT SUPPORTED" );
	        
	    }
	    
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.weather_node, menu );
		return true;
	}
	
	/**
	 * @param location
	 * updates GUI with the new weather information
	 */
	public void updateText(String location)
	{
		
		weather.setText( location );
	}
	
	

}
