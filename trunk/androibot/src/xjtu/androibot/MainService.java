package xjtu.androibot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class MainService extends Service
{
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		Toast.makeText(this, "服务已启动", Toast.LENGTH_LONG).show();
	}
}