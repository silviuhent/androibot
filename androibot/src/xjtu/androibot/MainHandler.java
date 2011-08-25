package xjtu.androibot;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class MainHandler extends Handler
{
	@Override
	public void handleMessage(Message msg)
	{
		try
		{
			switch(msg.what)
			{
			case 1:
			{
				Toast.makeText(MainActivity.getInstance(), "hello", Toast.LENGTH_LONG).show();
				
			}
			default:
				break;
			}
		}
		catch(Exception e)
		{
		    
		}
	}
}