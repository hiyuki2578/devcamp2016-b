package jp.android_group.student.ticketsplit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static jp.android_group.student.ticketsplit.Utils.*;
import static jp.android_group.student.ticketsplit.getApiKey.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, android.app.DatePickerDialog.OnDateSetListener {

	private RequestQueue mRequestQueue;
	private View mFocusView;
	String Key = getKey();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFocusView = findViewById(R.id.focusView);
		mFocusView.requestFocus();

		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		AutoCompleteTextView Dep = (AutoCompleteTextView)findViewById(R.id.Dep);
		AutoCompleteTextView Via = (AutoCompleteTextView)findViewById(R.id.Via);
		AutoCompleteTextView Arr = (AutoCompleteTextView)findViewById(R.id.Arr);
		Button Search = (Button)findViewById(R.id.button);
		Button Day = (Button)findViewById(R.id.Day);
		Day.setText(getDate("yyyy/MM/dd"));
		Search.setOnClickListener(this);
		Day.setOnClickListener(this);
		Dep.setOnFocusChangeListener(this);
		Via.setOnFocusChangeListener(this);
		Arr.setOnFocusChangeListener(this);
		Dep.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				AutoComp(MainActivity.this, s.toString() ,(AutoCompleteTextView)findViewById(R.id.Dep));
			}

			@Override
			public void afterTextChanged(Editable s) {
				AutoComp(MainActivity.this, s.toString() ,(AutoCompleteTextView)findViewById(R.id.Dep));
			}
		});
		Via.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				AutoComp(MainActivity.this, s.toString() ,(AutoCompleteTextView)findViewById(R.id.Via));
			}

			@Override
			public void afterTextChanged(Editable s) {
				AutoComp(MainActivity.this, s.toString() ,(AutoCompleteTextView)findViewById(R.id.Via));
			}
		});
		Arr.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				AutoComp(MainActivity.this, s.toString() ,(AutoCompleteTextView)findViewById(R.id.Arr));
			}

			@Override
			public void afterTextChanged(Editable s) {
				AutoComp(MainActivity.this, s.toString() ,(AutoCompleteTextView)findViewById(R.id.Arr));
			}
		});

		// キーボードのエンターを取得
		Arr.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (event == null || event.getAction() == KeyEvent.ACTION_UP) {
					search();
					mFocusView.requestFocus();
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				}
				return true;
			}
		});

		Via.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (event == null || event.getAction() == KeyEvent.ACTION_UP) {
					mFocusView.requestFocus();
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				}
				return true;
			}
		});

		Dep.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (event == null || event.getAction() == KeyEvent.ACTION_UP) {
					mFocusView.requestFocus();
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				}
				return true;
			}
		});

	}

	public void onFocusChange(View view, boolean hasFocus){
		switch(view.getId()) {
			case R.id.Dep:
			case R.id.Via:
			case R.id.Arr:
				NoFocus(this, view, hasFocus, (AutoCompleteTextView) findViewById(view.getId()));
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.action_settings:
				Intent intent = new Intent(this, Preference.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View view) {
		hideIME(this, view);
		switch (view.getId()) {
			case R.id.button:
				search();
				break;
			case R.id.Day:
				showDatePickerDialog(view);
				break;
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year , int month, int day){	//日時指定でOKを押したときに呼ばれる
		Button Day = (Button)findViewById(R.id.Day);
		Day.setText(String.valueOf(year) + "/" + String.format(Locale.JAPAN,"%1$02d",month + 1) + "/" + String.format(Locale.JAPAN,"%1$02d",day));	// 2016/10/12 形式で返す "%1$02d"で2桁で返す
	}

	public void showDatePickerDialog(View view){
		DatePickerDialog datePickerDialog = new DatePickerDialog();
		datePickerDialog.show(getFragmentManager(), "DatePicker");	//理解して
	}

	public void search(){
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
		AutoCompleteTextView Dep = (AutoCompleteTextView)findViewById(R.id.Dep);
		AutoCompleteTextView Via = (AutoCompleteTextView)findViewById(R.id.Via);
		AutoCompleteTextView Arr = (AutoCompleteTextView)findViewById(R.id.Arr);
		final TextView textView = (TextView)findViewById(R.id.result);
		Button Day = (Button)findViewById(R.id.Day);
		String Day_s = regex(Day.getText().toString(), "/", "");
		String uri = "https://api.ekispert.jp/v1/json/search/course/plain?key=" + Key + "&from=" + Dep.getText() + "&via=" + Via.getText() + "&to=" + Arr.getText() + getOption(spf) + "&date=" + Day_s;
		if(Via.length() == 0){
			uri = "https://api.ekispert.jp/v1/json/search/course/plain?key=" + Key + "&from=" + Dep.getText() + "&to=" + Arr.getText() + getOption(spf) + "&date=" + Day_s;
		}
		mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response){
				try {
					JSONObject ResultSet = response.getJSONObject("ResultSet");
					JSONArray Course = ResultSet.getJSONArray("Course");
					price(Course.getJSONObject(0).getString("SerializeData"), response, 0);
				}catch (JSONException e){
					e.printStackTrace();
					try {
						JSONObject ResultSet = response.getJSONObject("ResultSet");
						price(ResultSet.getString("SerializeData"), response, 0);
					}catch(JSONException er){
						er.printStackTrace();
						textView.setText("データが存在しません。");
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("Search_json",error.toString());
			}
		}));
	}

	public void price(String SerializeData, final JSONObject SearchResult, final int num){
		final TextView result = (TextView)findViewById(R.id.result);
		final TextView trans = (TextView)findViewById(R.id.trans);
		String uri = "https://api.ekispert.jp/v1/json/course/fare/divided?key=" + Key + "&serializeData=" + SerializeData;
		mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response){
				try {
					String text = "";
					String trans_t = "経路\n";
					String line;
					JSONObject ResultSet = response.getJSONObject("ResultSet");
					JSONObject Ticket = ResultSet.getJSONObject("Ticket");
					JSONArray Part = Ticket.getJSONArray("Part");
					for(int i = 0;i < Part.length();i++){
						JSONObject Point_i = Part.getJSONObject(i);
						JSONArray Point = Point_i.getJSONArray("Point");
						text += Point.getJSONObject(0).getJSONObject("Station").getString("Name") + "駅\n" + Point_i.getJSONObject("Price").getString("Oneway") + "円\n";//+Point.getJSONObject(1).getJSONObject("Station").getString("Name")+"駅\n";
					}
					JSONObject Result = SearchResult.getJSONObject("ResultSet");
					JSONArray Course = Result.getJSONArray("Course");
					JSONObject SearchType = Course.getJSONObject(num);
					JSONObject Route = SearchType.getJSONObject("Route");
					JSONArray Line = Route.getJSONArray("Line");
					JSONArray Point = Route.getJSONArray("Point");
					for(int i = 0;i < Point.length() - 1;i++){
						line = getLineName(Line.getJSONObject(i).getString("Name"));
						trans_t += Point.getJSONObject(i).getJSONObject("Station").getString("Name") + "駅\n" + line + "\n";// + Point.getJSONObject(i + 1).getJSONObject("Station").getString("Name") + "駅\n";
					}
					trans_t += Point.getJSONObject(Point.length() - 1).getJSONObject("Station").getString("Name") + "駅\n";
					text += Point.getJSONObject(Point.length() - 1).getJSONObject("Station").getString("Name") + "駅\n";
					result.setText(text);
					trans.setText(trans_t);
				}catch (JSONException e){
					e.printStackTrace();
					try {
						String text = "";
						String fare = "";
						String line;
						JSONObject Result = SearchResult.getJSONObject("ResultSet");
						JSONArray Course = Result.getJSONArray("Course");
						JSONObject SearchType = Course.getJSONObject(num);
						JSONArray Price = SearchType.getJSONArray("Price");
						JSONObject Route = SearchType.getJSONObject("Route");
						JSONArray Line = Route.getJSONArray("Line");
						JSONArray Point = Route.getJSONArray("Point");
						for(int i = 0;i < Point.length() - 1;i++) {
							line = getLineName(Line.getJSONObject(i).getString("Name"));
							text += Point.getJSONObject(i).getJSONObject("Station").getString("Name") + "駅\n" + line + "\n";
						}
						for (int j = 1; j < Price.length() - 1; j++) {
							if(Price.getJSONObject(j).getString("kind").equals("Fare")) {
								int station = Integer.parseInt(Price.getJSONObject(j).getString("fromLineIndex"));
								fare += Point.getJSONObject(station - 1).getJSONObject("Station").getString("Name") + "駅\n" + Price.getJSONObject(j).getString("Oneway") + "円\n";
							}
						}
						text += Point.getJSONObject(Point.length() - 1).getJSONObject("Station").getString("Name") + "駅";
						fare += Point.getJSONObject(Point.length() - 1).getJSONObject("Station").getString("Name") + "駅";
						result.setText(fare);
						trans.setText(text);
					}catch (JSONException er) {
						er.printStackTrace();
						try{
							String text = "";
							String trans_t = "";
							String line;
							JSONObject Result = SearchResult.getJSONObject("ResultSet");
							JSONArray Course = Result.getJSONArray("Course");
							JSONObject SearchType = Course.getJSONObject(num);
							JSONArray Price = SearchType.getJSONArray("Price");
							JSONObject Route = SearchType.getJSONObject("Route");
							JSONObject Line = Route.getJSONObject("Line");
							JSONArray Point = Route.getJSONArray("Point");
							line = getLineName(Line.getString("Name"));
							text += Point.getJSONObject(0).getJSONObject("Station").getString("Name")+"駅\n"+line+"\n"+Price.getJSONObject(0).getString("Oneway")+"円\n"+Point.getJSONObject(1).getJSONObject("Station").getString("Name")+"駅\n";
							trans_t += Point.getJSONObject(0).getJSONObject("Station").getString("Name")+"駅\n"+line+"\n"+Point.getJSONObject(1).getJSONObject("Station").getString("Name")+"駅\n";
							result.setText(text);
							trans.setText(trans_t);
						}catch (JSONException err) {
							err.printStackTrace();
						}
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				try {
					String text = "";
					String fare = "";
					String line;
					JSONObject Result = SearchResult.getJSONObject("ResultSet");
					JSONArray Course = Result.getJSONArray("Course");
					JSONObject SearchType = Course.getJSONObject(num);
					JSONArray Price = SearchType.getJSONArray("Price");
					JSONObject Route = SearchType.getJSONObject("Route");
					JSONArray Line = Route.getJSONArray("Line");
					JSONArray Point = Route.getJSONArray("Point");
					for(int i = 0;i < Point.length() - 1;i++) {
						line = getLineName(Line.getJSONObject(i).getString("Name"));
						text += Point.getJSONObject(i).getJSONObject("Station").getString("Name") + "駅\n" + line + "\n";
					}
					for (int j = 1; j < Price.length() - 1; j++) {
						if(Price.getJSONObject(j).getString("kind").equals("Fare")) {
							int station = Integer.parseInt(Price.getJSONObject(j).getString("fromLineIndex"));
							fare += Point.getJSONObject(station - 1).getJSONObject("Station").getString("Name") + "駅\n" + Price.getJSONObject(j).getString("Oneway") + "円\n";
						}
					}
					text += Point.getJSONObject(Point.length() - 1).getJSONObject("Station").getString("Name") + "駅";
					fare += Point.getJSONObject(Point.length() - 1).getJSONObject("Station").getString("Name") + "駅";
					result.setText(fare);
					trans.setText(text);
				}catch (JSONException er) {
					er.printStackTrace();
					try{
						String text = "";
						String trans_t = "";
						String line;
						JSONObject Result = SearchResult.getJSONObject("ResultSet");
						JSONArray Course = Result.getJSONArray("Course");
						JSONObject SearchType = Course.getJSONObject(num);
						JSONArray Price = SearchType.getJSONArray("Price");
						JSONObject Route = SearchType.getJSONObject("Route");
						JSONObject Line = Route.getJSONObject("Line");
						JSONArray Point = Route.getJSONArray("Point");
						line = getLineName(Line.getString("Name"));
						text += Point.getJSONObject(0).getJSONObject("Station").getString("Name")+"駅\n"+line+"\n"+Price.getJSONObject(0).getString("Oneway")+"円\n"+Point.getJSONObject(1).getJSONObject("Station").getString("Name")+"駅\n";
						trans_t += Point.getJSONObject(0).getJSONObject("Station").getString("Name")+"駅\n"+line+"\n"+Point.getJSONObject(1).getJSONObject("Station").getString("Name")+"駅\n";
						result.setText(text);
						trans.setText(trans_t);
					}catch (JSONException err) {
						err.printStackTrace();
					}
				}
			}
		}));
	}
}