package com.magma.simone_game;


import java.io.IOException;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends Activity {

	
	 private Button redbutton ,bluebutton, greenbutton,  yellowbutton ;
	 static final private int NAO_ID = Menu.FIRST;
	 static final private int REETI_ID = Menu.FIRST + 1;
	 static final private int SETTINGS_ID = Menu.FIRST + 2;

	 public MainActivity(){}
	 
	 // private static final int LOCAL_AUDIO = 1;
	   // private static final int RESOURCES_AUDIO = 2;
	   // private static final int YELLOW_SOUND = 4;
	   // private static final int RED_SOUND = 1;
	   // private static final int BLUE_SOUND = 2;
	   // private static final int GREEN_SOUND = 3;
	    Robot myBot;
	  
	  

	   public int robot_name = 1;
	   
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		// gets sudo privilege
		try {
			Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onCreate(savedInstanceState);
		 // Inflate our UI from its XML layout description.
		setContentView(R.layout.activity_main);
		
		 // Hook up button presses to the appropriate event handler.
        ((Button) findViewById(R.id.redbutton)).setOnClickListener(mRedListener);
        ((Button) findViewById(R.id.bluebutton)).setOnClickListener(mBlueListener);
        ((Button) findViewById(R.id.greenbutton)).setOnClickListener(mGreenListener);
        ((Button) findViewById(R.id.yellowbutton)).setOnClickListener(mYellowListener);
		
			
	/*	
		redbutton = (Button) findViewById(R.id.redbutton);
		bluebutton = (Button) findViewById(R.id.bluebutton);
		greenbutton = (Button) findViewById(R.id.greenbutton);
		yellowbutton = (Button) findViewById(R.id.yellowbutton);
		
		
	
		redbutton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//playSample(R.raw.red_long);
				 try {
						myBot.play("red");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			}
		});
		
		bluebutton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 //playSample(R.raw.blue_long);
				 try {
				myBot.say(
						//"tts.say(\" bleu\"),"
						//"servo.changeLedColor(\"blue\"), "
								"player.playSequence(\"/home/reeti/reetiDocuments/Sequences/Emotions/disgusted\")  "
						//+"player.playMus(\"/home/reeti/reetiDocuments/Music/SimonGame/blue_long.ogg\");"
						);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		//Global.player.playSequence("/home/reeti/reetiDocuments/Sequences/Emotions/disgusted");
		
		greenbutton.setOnClickListener(  new View.OnClickListener() {
			
			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View v) {
				 //playSample(R.raw.green_long);
				
				 try {
						myBot.say(
								"tts.say(\" vert \");"
								+"servo.changeLedColor(\"green\"), "
							//	+"player.playMus(\"/home/reeti/reetiDocuments/Music/SimonGame/green_long.ogg\");"
								);
						
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			}
		});
		
		yellowbutton.setOnClickListener( new View.OnClickListener() {
			
			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View v) {
				//playSample(R.raw.yellow_long);
				
				 try {
						myBot.say(
								"tts.say(\" jaune\");"
							+	"servo.changeLedColor(\"yellow\"); "
								//+"player.playMus(\"/home/reeti/reetiDocuments/Music/SimonGame/yellow_long.ogg\");"
								);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			}
		});*/
		
		
	}

	 /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		menu.add(0, NAO_ID, 0, R.string.nao).setShortcut('0', 'n');
        menu.add(0, REETI_ID, 0, R.string.reeti).setShortcut('1', 'r');
        menu.add(0, SETTINGS_ID, 0, R.string.action_settings).setShortcut('1', 's');
		return true;
	}

	/**
     * Called right before your activity's option menu is displayed.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Before showing the menu,WE ENABLE THE OPTIONS
        menu.findItem(NAO_ID).setVisible(true);
        menu.findItem(REETI_ID).setVisible(true);
        menu.findItem(SETTINGS_ID).setVisible(true);
        return true;
    }

    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case NAO_ID:
            setNao();
            return true;
        case REETI_ID:
            setReeti();
            return true;
        case SETTINGS_ID:
            setRobot();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
    
   
    /**
     * A call-back for when the user presses the back button.
     */
    OnClickListener mRedListener = new OnClickListener() {
        public void onClick(View v) {
            try {
				setColor("red");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    };
    
    /**
     * A call-back for when the user presses the back button.
     */
    OnClickListener mYellowListener = new OnClickListener() {
        public void onClick(View v) {
            try {
				setColor("yellow");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    };
    
    /**
     * A call-back for when the user presses the back button.
     */
    OnClickListener mBlueListener = new OnClickListener() {
        public void onClick(View v) {
            try {
				setColor("blue");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    };
    
    /**
     * A call-back for when the user presses the back button.
     */
    OnClickListener mGreenListener = new OnClickListener() {
        public void onClick(View v) {
            try {
				setColor("green");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    };
    
    
    
    
	public void setColor(String color) throws IOException{	
		myBot.playColor(color);
	}
	
	public void setRobot(){
		/**
		 * TODO
		 * set robot type (reeti/nao with specifying ip adress and port
		 */
		

		
		
	} 
	
	 @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        try {
				myBot.disconnectRobot();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	public void setReeti(){
		try {
			myBot = new MonReeti("reeti") ;
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		try {
			myBot.say(" Bonjour");
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	public void setNao(){
		try {
				myBot = new MonNao("nao") ;
		} catch (IOException e1) {			
			e1.printStackTrace();
		}
		
	}
	
}
