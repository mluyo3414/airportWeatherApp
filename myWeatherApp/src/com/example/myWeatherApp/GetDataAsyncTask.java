package com.example.myWeatherApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.text.Html;
import android.text.StaticLayout;
import android.util.Log;
import android.view.View;

/**
 * @author Miguel S
 * This asynchronous class connects the app with the JSON API passing the location
 * that the user input in the edit text.
 */
public class GetDataAsyncTask extends AsyncTask<String, Void, String>
{

	//ArrayList for each airport
	ArrayList<HashMap<String, String>> myAirports =
			new ArrayList<HashMap<String, String>>();
	//Json Array for airports
	JSONArray airportJsonArray = null;
	//new async class to parse the data received
	GetDataAsyncTask myNewAsynTask;
	//string that contains the data
	static String theNewData = " ";
	//main activity to call back
	private MainActivity activity_;
	//each airport
	static JSONObject jObj = null;
	private static final String tagCode = "code";
	private static final String tagLocation = "location";
	private static final String tagName = "name";

	
	/**
	 * @param activityToCallBack
	 * constructor
	 */
	public GetDataAsyncTask( MainActivity activityToCallBack )
	{
		//activity to update the GUI
		activity_ = activityToCallBack;
	}

	/**
	 * @param city
	 * @return
	 * @throws Exception
	 * Connects to the website that returns airport information in Json format
	 */
	/**
	 * @param city
	 * @return
	 * @throws Exception
	 */
	public String getInternetData( String city ) throws Exception
	{
		//read all the info from the website
		BufferedReader in = null;
		String data = "";
		try
		{
			// setup http client
			HttpClient client = new DefaultHttpClient();
			// process data from
			URI website =
					new URI( "http://airportcode.riobard.com/search?q=" + city
							+ "&fmt=JSON" );
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

			return data;
		} finally
		{
			if ( in != null )
			{
				try
				{
					in.close();
					return data;
				} catch ( Exception e )
				{
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	protected String doInBackground( String... params )
	{
		try
		{
			// passing string to form json
			return getInternetData( params[0] );
		} catch ( Exception e )
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute( String fromInternetdata )
	{
        //calls new async task to parse the data in an async activity
		ParserAsync myNewParserAsync;
		myNewParserAsync = new ParserAsync();
		myNewParserAsync.execute( fromInternetdata );

	}

	/**
	 * @author Miguel S
	 * This class is nested inside the other asynctask to parse the data
	 */
	class ParserAsync extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>>
	{
		protected ArrayList<HashMap<String, String>> parseData( String rawData )
		{
			//using Json data
			ArrayList<HashMap<String, String>> AirportArrayList =
					new ArrayList<HashMap<String, String>>();
			theNewData = "{\"Airports\": " + rawData + "}";
			
			try
			{
				jObj = new JSONObject( theNewData );
			} catch ( JSONException e1 )
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try
			{
				// Getting Array of airports since there are multiple airports in the 
				//json array
				airportJsonArray = jObj.getJSONArray( "Airports" );

				// looping through All airports
				for( int i = 0; i < airportJsonArray.length(); i++ )
				{
					JSONObject c = airportJsonArray.getJSONObject( i );

					// Storing each json item in variable
					String code = c.getString( tagCode );
					String name = c.getString( tagName );
					String location = c.getString( tagLocation );
					//storing individual airports( one per hashmap)
					HashMap<String, String> map = new HashMap<String, String>();
					map.put( tagCode, "CODE: "+ code );
					map.put( tagLocation, "LOCATION: " +location );
					map.put( tagName, "NAME: "+name );
					//add each airport to the list
					AirportArrayList.add( map );

				}

			} catch ( JSONException e )
			{
				e.printStackTrace();
			}
			//passes back arrayList to doInBackground
			return AirportArrayList;
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params )
		{
			// TODO Auto-generated method stub
			//calling parsing method from doInbackground
			myAirports = parseData( params[0] );
	
			return  myAirports;
		}
		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> myList)
		{
			// TODO Auto-generated method stub
			 //return to update the activity with the new information
			activity_.dataReceivedFromNetwork( myList );

		}

	}

}
