package com.magma.simone_game;

import java.io.IOException;

public class MonNao extends Robot{


	private static final String IP_NAO = "192.168.0.101";
	private static final int PORT_NAO = 54000;
	
	
	public MonNao(String IP, Integer PORT) throws IOException {
		super(IP_NAO, PORT_NAO);
		
	}
	
	public MonNao(String nom) throws IOException{
		super("nao");
	
	}

	public void connectRobot(){
		super.ip = IP_NAO;
		super.port = PORT_NAO;
		
	}

	@Override
	void say(String message) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	void playColor(String color) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	void changeColor(String color) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	void disconnectRobot() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void playBehaviour(String behaviourPath) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	void setIP(String IP) {
		// TODO Auto-generated method stub
		
	}

	@Override
	String getIP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void setPort(Integer port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	Integer getPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void playMusic(String musicPath) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
}
