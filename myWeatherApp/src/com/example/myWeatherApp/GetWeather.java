package com.example.myWeatherApp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;

/**
 * @author Miguel S This activity is used once the user selects an individual
 *         airport. It provides the city weather information for cities inside
 *         the US
 */
public class GetWeather extends AsyncTask<String, Void, String>
{
	// weather variables
	private WeatherNode activity_;
	String main = "";
	String currentCondition = "";
	String iconCondition = "";
	float temp = 0;
	int humidity = 0;
	int pressure = 0;
	int id = 0;
	float tempMin = 0;
	float tempMax = 0;

	public GetWeather( WeatherNode activityToCallBack )
	{

		activity_ = activityToCallBack;
	}

	private String getInternetData( String city ) throws Exception
	{

		BufferedReader in = null;
		String data = "";
		try
		{
			// setup http client
			HttpClient client = new DefaultHttpClient();
			// process data from
			URI website =
					new URI(
							"http://api.openweathermap.org/data/2.5/weather?q="
									+ city + ",US" );
			// request using get method
			HttpGet request = new HttpGet( website );
			HttpResponse response = client.execute( request );
			// string using buffered reader
			// streamreader bytes into characters
			in =
					new BufferedReader( new InputStreamReader( response
							.getEntity().getContent() ) );
			StringBuffer sb = new StringBuffer( "" );
			String l = "";
			String newline = System.getProperty( "line.separator" );
			while ( (l = in.readLine()) != null )
			{
				sb.append( l + newline );
			}
			in.close();
			data = sb.toString();
			//creates json object to parse
			return createJson( data );
		} finally
		{
			if ( in != null )
			{
				try
				{
					in.close();
					return createJson( data );
				} catch ( Exception e )
				{
					e.printStackTrace();
				}

			}
		}
	}

	private static JSONObject getObject( String tagName, JSONObject jObj )
			throws JSONException
	{
		JSONObject subObj = jObj.getJSONObject( tagName );
		return subObj;
	}

	/**
	 * @param data
	 * @return
	 * Get the weather information for the airport city
	 */
	private String createJson( String data )
	{

		try
		{
			//parse each JSONobject
			JSONObject jObj = new JSONObject( data );
			JSONObject coordObj = getObject( "coord", jObj );
			JSONObject sysObj = getObject( "sys", jObj );
			JSONArray jArr = jObj.getJSONArray( "weather" );
			JSONObject JSONWeather = jArr.getJSONObject( 0 );
			//get different information according to each JsonObject
			id = JSONWeather.getInt( "id" );
			main = JSONWeather.getString( "main" );
			currentCondition = JSONWeather.getString( "description" );
			iconCondition = JSONWeather.getString( "icon" );

			JSONObject mainObj = getObject( "main", jObj );
			temp = mainObj.getInt( "temp" );
			humidity = mainObj.getInt( "humidity" );
			pressure = mainObj.getInt( "pressure" );
			tempMin = mainObj.getInt( "temp_min" );
			tempMax = mainObj.getInt( "temp_max" );
			//converting to Celsius temperature (info given in Kelvin)
			temp = (float) (temp - 273.15);
			tempMin = (float) (tempMin - 273.15);
			tempMax = (float) (tempMax - 273.15);
			// info to update GUI
			return ("Temperature in Celsius: " + Float.toString( temp ) + "\n"
					+ "Humidity: " + Float.toString( humidity ) + "%\n"
					+ "Pressure in hPa: " + Float.toString( pressure ) + "\n"
					+ "Min. Temperature in Celsius: "
					+ Float.toString( tempMin ) + "\n"
					+ "Max. Temperature in Celsius: " + Float
						.toString( tempMax ));

		} catch ( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected String doInBackground( String... arg0 )
	{
		// TODO Auto-generated method stub
		// passing just city in the us to obtain information from API
		String rawLocation = arg0[0];
		try
		{
			return getInternetData( rawLocation );
		} catch ( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute( String webPage )
	{
		//updates the GUI
		activity_.updateText( webPage );

	}
}
