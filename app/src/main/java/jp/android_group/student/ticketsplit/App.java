package jp.android_group.student.ticketsplit;

import android.app.Application;

import com.deploygate.sdk.DeployGate;

/**
 * Created by hiyuki on 2017/02/12.
 */

public class App extends Application {
	@Override
	public void onCreate(){
		super.onCreate();
		DeployGate.install(this);
	}
}
