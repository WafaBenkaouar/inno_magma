package com.magma.simone_game;


import java.io.IOException;

import com.poetnerd.simonclone.SimonClone;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;


public class MainActivity extends Activity {

	
	 private Button redbutton ,bluebutton, greenbutton,  yellowbutton ;
	 static final private int NAO_ID = Menu.FIRST;
	 static final private int REETI_ID = Menu.FIRST + 1;
	 static final private int SETTINGS_ID = Menu.FIRST + 2;
	 private Simone_Game model;
	 private Robot myBot;
	 public int robot_name = 1;
	 
	 
	 private static final int LEVEL_DIALOG = 1;
		private static final int GAME_DIALOG = 2;
		private static final int ABOUT_DIALOG = 3;
		private static final int HELP_DIALOG = 4;
		
		private Menu mMenu;
		private AlertDialog levelDialog;
		private AlertDialog gameDialog;
		private AlertDialog aboutDialog;
		private AlertDialog helpDialog;
		private TextView levelDisplay;
		private TextView gameDisplay;
	 
	 public MainActivity(){}   
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		 model = new Simone_Game();
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
		

        
	}
	
	@Override
    protected void onPause () {
    	super.onPause();
    	SharedPreferences settings = getPreferences (0); // Private mode by default.
    	SharedPreferences.Editor editor = settings.edit();
    	
    	editor.putInt(Simone_Game.KEY_GAME_LEVEL, model.getLevel());	// Game Level
    	editor.putInt(Simone_Game.KEY_THE_GAME, model.getGame());	// The Game
    	editor.putString(Simone_Game.KEY_LONGEST_SEQUENCE, model.getLongest());	// Longest match

    	editor.commit();
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
	
   
    @Override
    protected Dialog onCreateDialog(int id) {
    	AlertDialog.Builder builder;
    	switch (id) {
    	case LEVEL_DIALOG:
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.set_level);
            builder.setSingleChoiceItems(R.array.level_choices, model.getLevel() - 1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	model.setLevel(whichButton + 1);
                	levelDisplay.setText(String.valueOf(whichButton + 1));
                }
            });
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	levelDialog.dismiss();
                }
            });
            levelDialog = builder.create();
            return levelDialog;
    	case GAME_DIALOG:
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.set_game);
            builder.setSingleChoiceItems(R.array.game_choices, model.getGame() - 1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	model.setGame(whichButton + 1);
                	gameDisplay.setText(String.valueOf(whichButton + 1));
                }
            });
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	gameDialog.dismiss();
                }
            });
            gameDialog = builder.create();
            return gameDialog;
    	case ABOUT_DIALOG:
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.about);
    		builder.setMessage(R.string.long_about);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	aboutDialog.dismiss();
                }
            });
            aboutDialog = builder.create();
            return aboutDialog;
    	case HELP_DIALOG:
    		builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.help);
    		builder.setMessage(R.string.long_help);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	helpDialog.dismiss();
                }
            });
            helpDialog = builder.create();
            return helpDialog;
    	default: return null;
    	}
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
