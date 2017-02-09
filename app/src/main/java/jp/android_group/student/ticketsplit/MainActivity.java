package jp.android_group.student.ticketsplit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

import static jp.android_group.student.ticketsplit.Utils.*;
import static jp.android_group.student.ticketsplit.getApiKey.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private RequestQueue mRequestQueue;
	String Key = getKey();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		AutoCompleteTextView Dep = (AutoCompleteTextView)findViewById(R.id.Dep);
		AutoCompleteTextView Arr = (AutoCompleteTextView)findViewById(R.id.Arr);
		Button Search = (Button)findViewById(R.id.button);
		Search.setOnClickListener(this);
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
	}
	public void onClick(View view) {
		hideIME(this, view);
		switch (view.getId()) {
			case R.id.button:
				search();
				break;
		}
	}
	public void search(){
		AutoCompleteTextView Dep = (AutoCompleteTextView)findViewById(R.id.Dep);
		AutoCompleteTextView Arr = (AutoCompleteTextView)findViewById(R.id.Arr);
		//final TextView result = (TextView)findViewById(R.id.result);
		String uri = "http://api.ekispert.jp/v1/json/search/course/plain?key=" + Key + "&from=" + Dep.getText() + "&to=" + Arr.getText() + "&shinkansen=false";
		mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response){
				try {
					JSONObject ResultSet = response.getJSONObject("ResultSet");
					JSONArray Course = ResultSet.getJSONArray("Course");
					//result.setText(Course.getJSONObject(0).getString("SerializeData"));
					price(Course.getJSONObject(0).getString("SerializeData"));
				}catch (JSONException e){
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("JsonSample",error.toString());
			}
		}));
	}
	public void price(String SerializeData){
		final TextView result = (TextView)findViewById(R.id.result);
		String uri = "http://api.ekispert.jp/v1/json/course/fare/divided?key=" + Key + "&serializeData=" + SerializeData;
		mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response){
				try {
					JSONObject ResultSet = response.getJSONObject("ResultSet");
					JSONObject Ticket = ResultSet.getJSONObject("Ticket");
					result.setText(Ticket.toString());//.getString("SerializeData"));
				}catch (JSONException e){
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("JsonSample",error.toString());
			}
		}));
	}
}