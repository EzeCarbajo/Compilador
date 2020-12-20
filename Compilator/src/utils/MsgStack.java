package utils;

import java.util.Vector;

public class MsgStack {
	private Vector<String> msgs;
	
	public MsgStack() {	msgs = new Vector<String>(); }
	
	public String getMsg(int index) { return (index <= msgs.size())? msgs.elementAt(index): null;	}

	public void addMsg(String msg) { msgs.add(msg); }
	
	public int getSize() { return msgs.size(); }
	
	public MsgStack addAll(MsgStack others) {
		msgs.addAll(others.msgs);
		return this;
	}
	
	public String toString() {
		String text = "";
		for (int i = 0; i < msgs.size(); i++) {
			text += msgs.get(i) + "\n";
		}
		return text;
	}
}
