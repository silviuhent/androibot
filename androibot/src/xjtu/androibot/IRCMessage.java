package xjtu.androibot;

public class IRCMessage
{
	String channel;
	String sender;
	String login;
	String hostname;
	String message;
	
	public IRCMessage(String channel, String sender, String login, String hostname, String message)
	{
		this.channel = channel;
		this.sender = sender;
		this.login = login;
		this.hostname = hostname;
		this.message = message;
	}
}