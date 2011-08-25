package xjtu.androibot;

import org.jibble.pircbot.*;
import java.util.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import xjtu.androibot.IRCMessage;

public class AndBot extends PircBot
{	
	public AndBot()
	{
		this.setName("AndBot");		
	}
	
	public AndBot(String name)
	{
		this.setName(name);
	}
	
	public AndBot(String name, String login, String version)
	{
		this.setName(name);
		this.setLogin(login);		
		this.setVersion(version);
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		try
		{	
			IRCMessage ircMsg = new IRCMessage(channel,sender,login,hostname,message);
			if(message.startsWith("bot:"))
			{
				Message msg = new Message();
				msg.obj = ircMsg;
				MainActivity.mHandler.sendMessage(msg);	
				/*
				if(message.equalsIgnoreCase("time"))
				{
					String time = new Date().toString();
					sendMessage(channel, sender + ": The time is now " + time);
				}
				*/
			}
		}
		catch(Exception e)
		{
			sendMessage(channel, "Exception:" + e.getMessage());
		}
	}
}