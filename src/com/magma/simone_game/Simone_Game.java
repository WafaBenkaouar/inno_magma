package com.magma.simone_game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Handler;
import android.os.Message;
import android.view.Menu;



public class Simone_Game {

/* Test modes -- enable them by setting true. */
	private static final boolean DISABLE_TIMEOUT = false;
	private static final boolean TEST_RAZZ = false;
	private static final boolean SHORT_GAME = false;
	
	
	 
	 public Robot myBot;
	 public int robot_name = 1;
	
	/* Ideally we pause between lighting or beeping for 50ms or 20ms, but the system tick 
	 * is observed to be 100ms.  To conform to people's intuition of how long things should be
	 * we shorten the isLit time of buttons by the difference between the delay we want between
	 * lit lights, and what we can actually get from the system tick. */
	private static final int TICK_DURATION = 100;
	private static final int BETWEEN_DURATION = 50;
	private static final int TICK_COMPENSATION =
			BETWEEN_DURATION < TICK_DURATION ? TICK_DURATION - BETWEEN_DURATION : 0;
	private static final int RAZZ_DURATION = 
			(TICK_COMPENSATION < 100 ) ? 100 - TICK_COMPENSATION : 20;
	private static final int RAZZ_COMPENSATION =
			RAZZ_DURATION < TICK_DURATION ? TICK_DURATION - BETWEEN_DURATION : 0;
	
	/* Classes of messages to handle through our Handler. */
	
	private static final int UI = 0;
	private static final int TIMEOUT = 1;
	
	/* Keys for save and restore of game state */
	public static final String KEY_THE_GAME = "theGame";
	public static final String KEY_GAME_LEVEL = "gameLevel";
	public static final String KEY_LONGEST_SEQUENCE = "longestSequence";

	/* Game States for controlling action of update. */
	private static final int IDLE = 0;
	private static final int LISTENING = 1;
	private static final int PLAYING = 2;
	private static final int REPLAYING = 3;
	private static final int LONG_PLAYING = 4;
	private static final int WINNING = 5;
	private static final int RAZZING = 6;
	private static final int WON = 7;
	private static final int LOSING = 8;
	private static final int LOST = 9;
	private static final int PAUSED = 10;
	
	/* Names for the reaction we make */
	private static final int GREEN = 0;
	private static final int RED = 1;
	private static final int YELLOW = 2;
	private static final int BLUE = 3;
	private static final int VICTORY_SOUND = 4;
	private static final int LOSE_SOUND = 5;
	private static final int SPECIAL_RAZZ = 6;
	

	/* Game sequences */
	private boolean[] activeColors = new boolean [4];
	private int[] longestSequence = new int[32];
	private int[] currentSequence = new int[32];
	private int[] razzSequence = {RED, YELLOW, BLUE, GREEN, GREEN, GREEN, GREEN, RED, YELLOW, LOSE_SOUND };
	private static final Random RNG = new Random();
	private boolean isLit;
	private boolean heardButtonPress;  // Avoid a race of: down -> listen -> up.
	private long pauseDuration;
	
	// Variables
	private int longestLength;
	private int sequenceLength;
	private int sequenceIndex;
	private int totalLength;
	private int playerPosition;
	private long beepDuration;
	private long mLastUpdate;
	private int gameMode;
	private int winToneIndex;
	private int razToneIndex;
	private int theGame;
	
	public interface Listener {
		void buttonStateChanged(int index);

		void multipleButtonStateChanged();
	}

	public static final int TOTAL_BUTTONS = 4;
	private boolean[] buttonPressMap = new boolean[TOTAL_BUTTONS];
	private List<Listener> listeners = new ArrayList<Listener>();
	
	public Simone_Game(){
		for (int i = 0; i < TOTAL_BUTTONS; ++i) {
			buttonPressMap[i] = false;
		}
		
		//initialisation
		longestLength = 0;	// Superfluous? Should be initialized by preferences stuff in Activity now.
		sequenceLength = 0;	// Superfluous? Should be initialized by preferences stuff in Activity now.
		mLastUpdate = System.currentTimeMillis();
		gameMode = IDLE;
		//isLit = false;
		//heardButtonPress = false;
		//pauseDuration = 0;
		winToneIndex = 0;
		razToneIndex = 0;
	}
	
	/*
	 * scaleBeepDuration
	 * 
	 * Set how long to play each tone.
	 * According to Simon Inns reverse engineering of the Simon, the beep durations are:
	 * 
	 * .42 seconds for sequence lengths 1 to 5,
	 * .32 seconds for 6 to 13 and
	 * .22 seconds for 14 to 31, all with .05 seconds between tones.
	 * 
	 * The problem is that the finest granularity of time for event handling,
	 * the system TICK_DURATION seems to be .1 seconds.
	 * (IE subtract 50 ms, so that the between tone of .05 s, that ends up being .1 s.
	 * doesn't screw us up.
	 */
	void scaleBeepDuration (int index) {
		if (index < 6 ) beepDuration = 420;		 // 1 to 5 is .42s 
		else if (index < 14) beepDuration = 320; // 6 to 13 is .32s
		else beepDuration = 220;				// 14 to 31 is .22s
		beepDuration -= TICK_COMPENSATION;
		if (beepDuration < 0) beepDuration = 0;  // I don't know what negative values would do.
	}
	
private UpdateHandler mUpdateHandler = new UpdateHandler();
	
	class UpdateHandler extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UI: 
				Simone_Game.this.update();
				break;
			case TIMEOUT: 
				Simone_Game.this.gameTimeoutLose();
				break;
			}
		}
		
		public void sleep(long delayMillis) {
			this.removeMessages(UI);
			sendMessageDelayed(obtainMessage(UI), delayMillis);
		}
	}
	
	public int getLevel () {
		if (totalLength <= 8) return 1;
		else if (totalLength <= 14) return 2;
		else if (totalLength <= 20) return 3;
		else return 4;
	}
	
	public void setLevel(int level) {
		int savedTotalLength = totalLength;
		
		switch (level) {
		case 1:
			totalLength = 8;
			break;
		case 2:
			totalLength = 14;
			break;
		case 3:
			totalLength = 20;
			break;
		case 4:
			totalLength = 30;
			break;
		default:
			totalLength = 4; 	// Should never get here.
			break;
		}
		if (totalLength != savedTotalLength) {	// If we changed the game level reset the game.
			gameClearTimeout ();
			if (pauseDuration > 0)  pauseDuration = 0;  // Go directly to idle, and don't pause.
			if (isLit) playNext();	  // If there's a button lit, turn it off.
			gameMode = IDLE;
			sequenceIndex = 0;
			// We could set sequenceLength to 0 and inhibit last, but I think not.
		}
	}
	
	public void setGame(int level) {
		theGame = level;
	}
	
	public int getGame() {
		return theGame;
	}
	
	public void setLongest(String sequence) {
		longestLength = sequence.length(); 	// Just fine if string is empty.
		longestSequence = parseSequenceAsString(sequence);
	}
	
	public void setCurrent(String sequence) {
		sequenceLength = sequence.length(); 	// Just fine if string is empty.
		currentSequence = parseSequenceAsString(sequence);
		
	}
	
	public String getLongest() {
		return parseSequenceToString(longestSequence, longestLength);
	}
	
	private int[] parseSequenceAsString(String longest) {
		int[] retval = new int[32];
		for (int i = 0; i < longest.length(); i++) {
			retval[i] = Integer.parseInt(Character.toString(longest.charAt(i)));
		}
		return retval;
	}
	
	private String parseSequenceToString(int[] array, int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(array[i]);
		}
		return sb.toString();
	}
	
	public void gameSetTimeout() {
		mUpdateHandler.removeMessages(TIMEOUT);						// Clear any old timeouts.
		if (!DISABLE_TIMEOUT)
			mUpdateHandler.sendEmptyMessageDelayed(TIMEOUT, 3000);   // Set a new 3 second timeout.
	}
	
	public void gameClearTimeout() {
		mUpdateHandler.removeMessages(TIMEOUT);						// Clear any old timeouts.		
	}
	
	public void update() {
		long now = System.currentTimeMillis();
		long delay = beepDuration;	// Events are normally the length of a beep.
		if (isLit) delay = BETWEEN_DURATION;   // Usually delay 50ms after turning off a lit light.
								 // Except that our tick seems to be 100ms, so this doesn't really work
								 // as we would hope.  So we compensate.
		if (gameMode == WINNING) {  // Special delays when playing winning tone sequence.
			if (winToneIndex == 0) delay = 20; // First beep duration is .02 s.
			else delay = BETWEEN_DURATION + 20;		// Subsequent beeps are .07 s.
													//If BETWEEN_DURATION changes, change this.
			if (isLit) delay = 20;	// and delay .02 s. between tones.
			
		}
		if (gameMode == RAZZING) {  // Special delays when playing winning tone sequence.
			if (razToneIndex == 0) delay = BETWEEN_DURATION; // First beep duration is .05 s.
			else delay = RAZZ_DURATION;		// Subsequent beeps are .1 s.
			delay -= RAZZ_COMPENSATION;
			if (delay < 0) delay = 0;  // I don't know what negative values would do.
													
			if (isLit) delay = BETWEEN_DURATION;	// and delay .05 s. between tones.			
		}
		/* We rely on the update routine to do no state changing on a pause, to set pauseDuration to 0
		 * and return here to continue doing what we were otherwise going to do. */
		if (pauseDuration > 0) delay = pauseDuration;

		if (gameMode != LISTENING) {
			if (now - mLastUpdate > delay) {
				playNext();
				mLastUpdate = now;
			}
			mUpdateHandler.sleep(delay);
			}
	}
	public void playNext() {
		if (pauseDuration > 0) { 									// OK, we've delayed.
			pauseDuration = 0; 
			return;
		}
		switch (gameMode) {
		case REPLAYING:
		case PLAYING:  //  Play the current sequence.
			if (sequenceIndex < sequenceLength) {	// Keep playing
				if (isLit) {
					showButtonRelease(currentSequence[sequenceIndex]); // Stop previous tone.
					isLit = false;
					sequenceIndex++;								// Point at next
					if (sequenceIndex == sequenceLength) { // Played last tone.
						if (gameMode == PLAYING) {		// If we're playing begin listening for input.
							gameSetTimeout();			
							sequenceIndex = 0;			// Now use sequenceIndex as match cursor.
							gameMode = LISTENING;					/* gameMode = SET_LISTEN;	// switch to Listen when button release feedback is done. */
						} else gameMode = IDLE;							// or go to Idle state after replay.
					}
				} else {
					showButtonPress(currentSequence[sequenceIndex]);	// Flash and beep current.
					isLit = true;
				}
			} // Fall through and do nothing if we're past the end of the sequence.
			break;
		case LONG_PLAYING:  //  Play the current sequence.
			if (isLit) {
				showButtonRelease(longestSequence[sequenceIndex]); // Stop previous tone.
				isLit = false;
				sequenceIndex++;								// Point at next
				return;
			}
			if (sequenceIndex < longestLength) {
				showButtonPress(longestSequence[sequenceIndex]);	// Flash and beep current.
				isLit = true;
			} else {											// Played all
				scaleBeepDuration(sequenceLength);			// Restore to normal value.
				gameMode = IDLE;
			}
			break;
		case WINNING:
			if (isLit) {
				showButtonRelease(currentSequence[sequenceLength - 1]);
				isLit = false;
				if (winToneIndex == 6) gameMode = WON;
			} else {
				showButtonPress(currentSequence[sequenceLength - 1]);
				isLit = true;
				winToneIndex++;
			}
			break;
		case RAZZING:
			if (isLit) {
				showButtonRelease(razzSequence[razToneIndex]);
				isLit = false;
				if (razToneIndex == 9) gameLose();  // Kludge: Light nothing and play lose tone.
				razToneIndex++;
			} else {
				showButtonPress(razzSequence[razToneIndex]);
				isLit = true;
			}
			break;

		case LOSING:
			gameMode = LOST;
			break;
		}
	}
	
	public void playCurrent() {
		gameMode = PLAYING;
		sequenceIndex = 0;
		update();
	}
	
	public void playLast() {
		switch (gameMode) {
		case IDLE:
		case WON:
		case LOST:
			
			/* In case user was fast on the draw: Reset all buttons. */
			for (int index = 0; index < 4; index++) {  
				showButtonRelease(index);
			}
			
			gameMode = REPLAYING;
			sequenceIndex = 0;
			update();
			break;
		default:
				return;
		}
	}
	
	public void playLongest () {
		switch (gameMode) {
		case IDLE:
		case WON:
		case LOST:
			
			/* In case user was fast on the draw: Reset all buttons. */
			for (int index = 0; index < 4; index++) {  
				showButtonRelease(index);
			}
			
			gameMode = LONG_PLAYING;
			sequenceIndex = 0;
			scaleBeepDuration(longestLength);
			update();
			break;
		default:
				return;
		}
	}
	
	public int getRandomColor () {
		int retval = RNG.nextInt(4);
		if (theGame == 3)  {			// Filter out inactive colors
			while (activeColors[retval] == false)  retval = RNG.nextInt(4);  // Keep trying till we get an active.
		}
		return retval;
	}
	
	public void gameStart() {
		for (int i = 0; i < 4; i++)  {
			activeColors[i] = true;			// Mark all colors active.
		}
		
		/* In case user was fast on the draw: Reset all buttons. */
		for (int index = 0; index < 4; index++) {  
			showButtonRelease(index);
		}
		
		if (SHORT_GAME) totalLength = 3;  // Temporary Test: crowbar win to 3 steps.
		winToneIndex = 0;
		razToneIndex = 0;
		sequenceLength = 1;
		scaleBeepDuration (1);
		playerPosition = 1;
		currentSequence[0] = getRandomColor();
		playCurrent();
	}

	public void maintainLongest () {
		if (sequenceLength > longestLength) {
			for (int i = 0; i < sequenceLength; i++) {
				longestSequence[i] = currentSequence[i];
			}
			longestLength = sequenceLength;
		}		
	}
	
	public void gameWin() {
		mLastUpdate = System.currentTimeMillis();
		pauseDuration = 800;		// We play the winning tone .8 s. after win.
		gameMode = WINNING;
		if (TEST_RAZZ) gameMode = RAZZING;		// Make razz tone the win tone on test.
		update();
	}

	public void razzWin() {
		mLastUpdate = System.currentTimeMillis();
		pauseDuration = 800;		// We play the winning tone .8 s. after win.
		gameMode = RAZZING;
		update();
	}
	

	public void gameTimeoutLose () {
		if (theGame == 3)  {
			activeColors[currentSequence[sequenceIndex]] = false;
		}
		gameLose();
	}
	public void gameLose() {
		//doStream(soundIds[LOSE_SOUND]);
		
		if (theGame == 3) {   // In game 3 we eliminate a color and start again.
			int activeColorCount = 0;
			for (int i=0; i < 4; i++) {
				if (activeColors[i]) activeColorCount++;
			}
			if (activeColorCount == 1) gameWin();
			else {
				sequenceLength = 1;
				scaleBeepDuration (1);
				currentSequence[0] = getRandomColor();
				gameCycle();
			}
		} else {
			gameMode = LOSING;
			update();
		}
	}
	
	public void gameCycle() {
		mLastUpdate = System.currentTimeMillis();
		pauseDuration = 800;		// Wait .8s after last key pressed to play next for game 1 and 3.
		playerPosition = 1;
		update();
		playCurrent();
	}

	/*
	 * pressButton is called by the Touch Handler in response to user cction
	 * We deal with the work in response to the user action and then we show
	 * that we have pressed the button.
	 */
	
	public void pressButton (int buttonIndex)  {
		if (gameMode != LISTENING) return;		// Only examine values when game is in play.
		// Guard against entering LISTENING state between a press and a release.
		heardButtonPress = true;
		// Logic for game 2:  We take user input as next color and fall through to normal case.
		if (playerPosition > sequenceLength) {
			currentSequence[sequenceIndex] = buttonIndex;
			sequenceLength++;
			playerPosition++;		// Point past new end of list and trigger restart of matching.
		}
		
		// Regular logic.
		if (currentSequence[sequenceIndex] == buttonIndex) {	// showButton only if match.
			maintainLongest();
			showButtonPress(buttonIndex);
		}
		else {
			gameClearTimeout();					// showButton Press would have done this for us.
			/**
			 * TODO
			 * doStream(soundIds[LOSE_SOUND]);
			 */
			try {
				myBot.playReaction(LOSE_SOUND);
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			if (theGame == 3) {		// Eliminate color that was pressed in game 3.
				activeColors[buttonIndex] = false;
			}
			gameLose();
		}
	}

	/*
	 * releaseButton is called by the Touch Handler in response to user cction
	 * We deal with the work in response to the user action and then we show
	 * that we have released the button.
	 */
	
	public void showButtonPress(int index) {
		gameClearTimeout();		// The real game has a cheat:  Timeout is suspended while pressing button.
		if (index >= 0 && index < TOTAL_BUTTONS) {
			if (buttonPressMap[index] == false) {
				buttonPressMap[index] = true;
			
				switch (gameMode) {
				case WON:
					try {
						myBot.playReaction(VICTORY_SOUND);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					/**
					 * TODO
					 * doStream(soundIds[VICTORY_SOUND]);
					 */
					break;
				case WINNING:
					/**
					 * TODO
					*doStream(soundIds[RED]);  // Play the red sound for win.
					**/
					try {
						myBot.playReaction(RED);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				case LOSING: 
					/**
					 * TODO
					 * doStream(soundIds[LOSE_SOUND]);
					 */
					try {
						myBot.playReaction(LOSE_SOUND);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					return;
				case LISTENING: 
					if (currentSequence[sequenceIndex] == index) // When we miss we barf immediately
						
						/**
						 * TODO
						 * doStream(soundIds[index]);
						 */
					try {
						myBot.playReaction(index);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					else 
						try {
							myBot.playReaction(LOSE_SOUND);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						/**
						 * TODO
						 * doStream(soundIds[LOSE_SOUND]);
						 */
					break;
				case RAZZING:
					if (razToneIndex < 9) 
						/**
						 * TODO
						 * doStream(soundIds[index]);
						 */
						try {
							myBot.playReaction(index);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					break;
				default: 
					/**
					 * TODO
					 * doStream(soundIds[index]);
					 */
					try {
						myBot.playReaction(index);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
				for (Listener listener : listeners) {
					listener.buttonStateChanged(index); 
				}  
			}
		}
	}

	public void releaseButton (int buttonIndex ){
		if (gameMode != LISTENING) return;
		// Guard against acting on a button press that happened before we were LISTENING.
		if (heardButtonPress == false) return;

		heardButtonPress = false;			// Reset our heardButtonPress state.
		mLastUpdate = System.currentTimeMillis();
		gameSetTimeout();
		
		if (sequenceIndex < sequenceLength) {
			if (currentSequence[sequenceIndex] == buttonIndex)  { // Matched. Continue.
				showButtonRelease(buttonIndex);			// showButton only if match.
				sequenceIndex++;
				if (sequenceIndex == sequenceLength) { 
					if (sequenceLength < totalLength) {  // Add one more.
						if (theGame == 2) {  // In game 2, user adds next item in sequence
							if (playerPosition > sequenceLength) {
								playerPosition = 1;
								sequenceIndex = 0;		// We added one. Now restart matching sequence.
							} else {
								playerPosition++;		// Set the stage for adding to sequence on next button press.
							}
						} else {
							sequenceLength++;
							playerPosition = 1;
							scaleBeepDuration (sequenceLength);
							currentSequence[sequenceIndex] = getRandomColor();
							gameCycle();
						}
					} else {  // Total win!
						if (theGame == 3 && sequenceLength == 31) razzWin ();
						else gameWin();
					}					
				} else {
					playerPosition++;
				}
			} else {
				if (theGame == 3) {		// Eliminate color that was pressed in game 3.
					activeColors[buttonIndex] = false;
				}
				gameLose ();
			}
		}
		
	}
	
	public void showButtonRelease(int index) {
		if (index >= 0 && index < TOTAL_BUTTONS) {
			if (buttonPressMap[index] == true) {
				buttonPressMap[index] = false;
					/**
					 * TODO : stop playing sound
					 * if (speakerStream != 0) {
					
						soundPool.stop(speakerStream);
						speakerStream = 0;
						
				} */
				
				for (Listener listener : listeners) {
					listener.buttonStateChanged(index);
				} 
			}
		}
	}


	public void doStream (int soundId) {
		if (soundId != 0) {  // Don't do anything different if our soundID is invalid.
			/**
			 * TODO: stop sound and start the new one
			 * if (speakerStream !=0) {  // Stop what we were doing.
				soundPool.stop(speakerStream);
			}
			speakerStream = soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);*/
		} 
	}
	
	public boolean isButtonPressed(int index) {
		if (index < 0 || index > TOTAL_BUTTONS) {
			return false;
		} else {
			return buttonPressMap[index];
		}
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void releaseAllButtons() {
		for (int i = 0; i < buttonPressMap.length; ++i) {
			buttonPressMap[i] = false;
		}
		for (Listener listener : listeners) {
			listener.multipleButtonStateChanged();
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
	
	public void dispose() {
		
	}
}
