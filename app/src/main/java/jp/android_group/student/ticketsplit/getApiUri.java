package jp.android_group.student.ticketsplit;

/**
 * Created by hiyuki on 2017/02/17.
 */

public class getApiUri {
	static String searchCoursePlain(String Dep, String Via, String Arr, String Option, String Date){
		if(Via.length() != 0){
			return "https://api.ekispert.jp/v1/json/search/course/plain?key=" + getApiKey.getKey() + "&from=" + Dep + "&via=" + Via + "&to=" + Arr + Option + "&date=" + Date;
		}else{
			return "https://api.ekispert.jp/v1/json/search/course/plain?key=" + getApiKey.getKey() + "&from=" + Dep + "&to=" + Arr + Option + "&date=" + Date;
		}
	}

	static String courseFareDivided(String SerializeData){
		return "https://api.ekispert.jp/v1/json/course/fare/divided?key=" + getApiKey.getKey() + "&serializeData=" + SerializeData;
	}
}
