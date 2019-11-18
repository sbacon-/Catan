package com.chancho.catan.control;

import java.util.ArrayList;

public class Chat{
	public ArrayList<String> chatlog = new ArrayList<String>();
	public ArrayList<Integer> chatlogpid = new ArrayList<Integer>();
	public String compose = "Compose...";
	public void send() {
		if(compose!="Compose...") {
			chatlog.add(compose);
			chatlogpid.add(-1);
			compose = "Compose...";
		}
	}
	public void send(String msg) {
		if(compose!="Compose...") {
			chatlog.add(msg);
			chatlogpid.add(-1);
			compose = "Compose...";
		}
	}
	public void send(int pid) {
		if(compose!="Compose...") {
			chatlog.add(compose);
			chatlogpid.add(pid);
			compose = "Compose...";
		}
	}
}
