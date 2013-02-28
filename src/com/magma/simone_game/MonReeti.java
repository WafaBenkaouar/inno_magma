package com.magma.simone_game;

import java.io.IOException;

import liburbi.UClient;

import android.graphics.Color;

public class MonReeti extends Robot{

	public String IP_REETI = "129.88.50.112";
	public int PORT_REETI = 54001;
	private UClient cli  ;
	
	
	public MonReeti(String IP, Integer PORT) throws IOException {
		super(IP, PORT);
		cli = new UClient(IP, PORT, UClient.URBI_BUFLEN) ;
	}
	
	
	public MonReeti(String nom) throws IOException {
		super("reeti");
		cli = new UClient(this.IP_REETI, this.PORT_REETI, UClient.URBI_BUFLEN) ;
	}

	public void connectRobot(){
		super.ip = IP_REETI;
		super.port = PORT_REETI;
		cli = new UClient(ip, port, UClient.URBI_BUFLEN) ;
	}
	
	public void playColor(String color) throws IOException{
		this.say(color);
		changeColor(color);
		//
		//+"player.playMus(\"/home/reeti/reetiDocuments/Music/SimonGame/red_long.ogg\");"
		//);
		
	}
	
	public void say(String message) throws IOException{
		cli.send("tts.say(\" "+message+"\");");
	}
	
	public void changeColor(String color)throws IOException{
		cli.send("servo.changeLedColor(\""+color+"\"); ");

	}


	@Override
	void disconnectRobot() throws IOException{
		cli.disconnect();
		
	}


	@Override
	void playBehaviour(String behaviourPath) throws IOException {
		cli.send("player.playSequence(\"/home/reeti/reetiDocuments/Sequences/Simon_Game/"+behaviourPath+"\");");
		
	}


	@Override
	void setIP(String IP) {
		ip = IP;		
	}


	@Override
	String getIP() {
		return ip;
	}


	@Override
	void setPort(Integer port) {
		super.port = port;
	}


	@Override
	Integer getPort() {
		return super.port;
	}


	@Override
	void playMusic(String musicPath) throws IOException {
		cli.send("player.playMus(\"/home/reeti/reetiDocuments/Music/SimonGame/"+musicPath+"\");");	
	}


	
}
