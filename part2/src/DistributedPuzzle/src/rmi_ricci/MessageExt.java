package rmi_ricci;

public class MessageExt extends Message {

	private int a;
	
	public MessageExt(String s) {
		super(s);
	}
	
	public MessageExt() {
		super("default");
	}
	
	public void set(int a) {
		this.a = a;
	}
}
