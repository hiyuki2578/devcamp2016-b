package jp.android_group.student.ticketsplit;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, android.app.DatePickerDialog.OnDateSetListener {

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
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					// フォーカスが外れた場合キーボードを非表示にする
					InputMethodManager inputMethodMgr = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
					inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
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
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					// フォーカスが外れた場合キーボードを非表示にする
					InputMethodManager inputMethodMgr = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
					inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
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

			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					// フォーカスが外れた場合キーボードを非表示にする
					InputMethodManager inputMethodMgr = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
					inputMethodMgr.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
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
		AutoCompleteTextView Dep = (AutoCompleteTextView)findViewById(R.id.Dep);
		AutoCompleteTextView Via = (AutoCompleteTextView)findViewById(R.id.Via);
		AutoCompleteTextView Arr = (AutoCompleteTextView)findViewById(R.id.Arr);
		Button Day = (Button)findViewById(R.id.Day);
		String Day_s = regex(Day.getText().toString(), "/");
		String uri = "https://api.ekispert.jp/v1/json/search/course/plain?key=" + Key + "&from=" + Dep.getText() + "&via=" + Via.getText() + "&to=" + Arr.getText() + "&plane=false&bus=false&date=" + Day_s;
		if(Via.length() == 0){
			uri = "https://api.ekispert.jp/v1/json/search/course/plain?key=" + Key + "&from=" + Dep.getText() + "&to=" + Arr.getText() + "&shinkansen=false&plane=false&date=" + Day_s;
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
							trans_t += Point.getJSONObject(i).getJSONObject("Station").getString("Name") + "駅\n" + Line.getJSONObject(i).getString("Name") + "\n";// + Point.getJSONObject(i + 1).getJSONObject("Station").getString("Name") + "駅\n";
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
						JSONObject Result = SearchResult.getJSONObject("ResultSet");
						JSONArray Course = Result.getJSONArray("Course");
						JSONObject SearchType = Course.getJSONObject(num);
						JSONArray Price = SearchType.getJSONArray("Price");
						JSONObject Route = SearchType.getJSONObject("Route");
						JSONArray Line = Route.getJSONArray("Line");
						JSONArray Point = Route.getJSONArray("Point");
						int j = -1;
						for(int i = 0;i < Point.length() - 1;i++){
							text += Point.getJSONObject(i).getJSONObject("Station").getString("Name") + "駅\n" + Line.getJSONObject(i).getString("Name") + "\n";// + Point.getJSONObject(i + 1).getJSONObject("Station").getString("Name") + "駅\n";
							if(Integer.parseInt(Price.getJSONObject(i - j).getString("fromLineIndex")) == i + 1) {
								fare += Point.getJSONObject(i).getJSONObject("Station").getString("Name") + "駅\n" + Price.getJSONObject(i - j).getString("Oneway") + "円\n";// + Point.getJSONObject(Point.length() - 1).getJSONObject("Station").getString("Name") + "駅\n";
							}else{
								j += 1;
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
							JSONObject Result = SearchResult.getJSONObject("ResultSet");
							JSONArray Course = Result.getJSONArray("Course");
							JSONObject SearchType = Course.getJSONObject(num);
							JSONArray Price = SearchType.getJSONArray("Price");
							JSONObject Route = SearchType.getJSONObject("Route");
							JSONObject Line = Route.getJSONObject("Line");
							JSONArray Point = Route.getJSONArray("Point");
							text += Point.getJSONObject(0).getJSONObject("Station").getString("Name")+"駅\n"+Line.getString("Name")+"\n"+Price.getJSONObject(0).getString("Oneway")+"円\n"+Point.getJSONObject(1).getJSONObject("Station").getString("Name")+"駅\n";
							trans_t += Point.getJSONObject(0).getJSONObject("Station").getString("Name")+"駅\n"+Line.getString("Name")+"\n"+Point.getJSONObject(1).getJSONObject("Station").getString("Name")+"駅\n";
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
				Log.d("Price_json",error.toString());
			}
		}));
	}
}