package rmi_ricci;

import java.io.Serializable;

public class Message implements Serializable  {
	
	private String content;
	
	public Message(String s){
		content = s;
	}
	
	public String getContent(){
		return content;
	}
}
