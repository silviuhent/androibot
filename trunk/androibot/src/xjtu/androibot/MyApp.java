package xjtu.androibot;

import android.app.Application;
import android.os.Bundle;

public class MyApp extends MainActivity
{
	private static MyApp instance;
	
	public static MyApp getInstance()
	{
		return instance;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		instance = this;
	}
}