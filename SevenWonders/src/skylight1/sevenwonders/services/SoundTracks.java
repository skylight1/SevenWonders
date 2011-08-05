package skylight1.sevenwonders.services;

import skylight1.sevenwonders.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

public class SoundTracks
{
	private static final String TAG = "SoundTracks";

	public static final int SOUNDTRACK = 0;
//	public static final int WIND = 1;
	public static final int SPELL = 1;
	public static final int DEATH = 2;
	public static final int COIN = 3;
	public static final int BUMP = 4;

	private final int soundResources[] = {
			R.raw.soundtrack, //loops
//			R.raw.wind, //loops   - disabled for now
			R.raw.spell,
			R.raw.death,
			R.raw.coin_sound,
//			R.raw.bump, // using mediaplayer instead
			};

	private final int SOUNDPOOL_STREAMS = soundResources.length;
	private final int SOUNDPOOL_LOOPS = 1;

	private int soundIds[]  = new int[SOUNDPOOL_STREAMS];
	private int streamIds[] = new int[SOUNDPOOL_STREAMS];

	static private boolean enabled = true;

	private Context context;
	private SoundPool soundPool;
    private int streamVolume;
    private boolean running;
    private boolean paused;
    private boolean initCompleted = true;
    private MediaPlayer mp;

    private static SoundTracks soundTrack = new SoundTracks();

    public static SoundTracks getInstance() {
    	return soundTrack;
    }

	private SoundTracks() {
	}

    public void init(Context aContext)
    {
    	if(!enabled || context!=null || !initCompleted) {
    		return;
    	}
    	Log.i(TAG, "init sounds");

    	initCompleted=false;
    	running=true;
		context=aContext;

        soundPool = new SoundPool(SOUNDPOOL_STREAMS, AudioManager.STREAM_MUSIC, 100);

		//obtain current media volume
        AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);

        // load sounds in the background
    	new Thread(new Runnable() {
        	public void run() {
        		if(soundPool==null) {
        			Log.w(TAG,"sound init: soundPool is null!");
        			return;
        		}
    			Log.d(TAG,"sound init: loading all sounds");

				// "load" tracks (play actually loads into memory) and clear streamids
	    		for (int i = 0; i < SOUNDPOOL_STREAMS; i++) {
	    			soundIds[i]  =  soundPool.load(context, soundResources[i], i);
	    			streamIds[i] = 0;
	    		}

	    		// except do play the first soundtrack with volume (looped)
    			while(streamIds[SOUNDTRACK]==0 && running) {
    				try { Thread.sleep(200); } catch(InterruptedException e) { } //TODO: wait to load
					streamIds[SOUNDTRACK] =  soundPool.play(soundIds[SOUNDTRACK], 1.0f*streamVolume, 1.0f*streamVolume, 0, -1, 1f);
    			}

	    		//play (load / get streamids) for rest of streams - ready to resume
/*	            for (int i = SOUNDPOOL_LOOPS-1; i < SOUNDPOOL_STREAMS; i++) {
	    			while(streamIds[i]==0 && running) {
	    				try { Thread.sleep(200); } catch(InterruptedException e) { } //TODO optimize
	    				if(i>=SOUNDPOOL_LOOPS) { // don't loop non soundtrack sounds
	    					streamIds[i] =  soundPool.play(soundIds[i], 0f, 0f, 1, 0, 1f);  // no loop
	    				} else {
	    					streamIds[i] =  soundPool.play(soundIds[i], 0f, 0f, 1, -1, 1f); // loop
	    				}
	    			}
    				soundPool.pause(streamIds[i]);
    				Log.d(TAG, "paused stream "+i);
	    		}
*/
    			mp = MediaPlayer.create(context, R.raw.bump);
    			
	    		initCompleted=true;
            	Log.i(TAG, "first soundtrack loaded");
        	}
    	}).start();
    }
    private void release()
    {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
    		initCompleted=true;
        }
        if(mp!=null) {
			mp.stop();
			mp.release();
			mp = null;
        }
		System.gc();
    }
    public void stop() {
    	if(soundPool != null && initCompleted) {
        	running = false;
			for(int i = 0; i <= SOUNDPOOL_LOOPS; i++) { // stop looping sounds
				soundPool.setLoop(streamIds[i], 0);
			}
			
			//fade out last loop
//			fadeout();
			
			// stop all sounds
			for(int i = 0; i < soundIds.length; i++) {
				soundPool.stop(streamIds[i]);
			}
    	}
    	release();
    }

    // for now this is just for 1st soundtrack
    public void fadeoutSplashSoundTrack(final int track) {
    	if(soundPool==null || !initCompleted || paused) {
    		return;
    	}
		soundPool.setLoop(track, 0);
    	Thread fadeThread = new Thread( new Runnable() {
	    	public void run() {
				for (float f = 1.0f; f > 0.0f; f-=0.05f) {
					try {Thread.sleep(100);} catch(InterruptedException ioe) { }
					if(soundPool!=null) {
						soundPool.setVolume(streamIds[track], f*streamVolume, f*streamVolume);
					} else {
						Log.d(TAG,"soundPool is null while fading out "+track);
						return;
					}
				}
				if(soundPool!=null) {
					Log.d(TAG,"soundPool stopping and unloading 1st soundtrack");					
					soundPool.stop(streamIds[track]);
		    		soundPool.unload(streamIds[track]);
		    
	    			while(streamIds[DEATH]==0 && running) {
	    				try { Thread.sleep(200); } catch(InterruptedException e) { }
						streamIds[DEATH] =  soundPool.play(soundIds[DEATH], 0.0f*streamVolume, 0.0f*streamVolume, 0, -1, 1f);
	    			}
	    			soundPool.pause(streamIds[DEATH]);
					Log.i(TAG,"death sound is loaded and paused - ready to play");

				} else {
					Log.i(TAG,"soundPool is null while stopping "+track);
					return;
				}
			}
    	});
    	if(track==SOUNDTRACK) {
    		Log.d(TAG, "not starting wind sound...");
    		//soundPool.setVolume(streamIds[WIND], 1.0f*streamVolume, 1.0f*streamVolume);
    		//resume(WIND,-1);
    		//play(WIND,-1);
    	}
    	fadeThread.start();
    }
    public void fadeout() { //fades out all sounds
    	if(soundPool==null || !initCompleted || paused) {
    		return;
    	}
    	Thread fadeThread = new Thread( new Runnable() {
	    	public void run() {
				for (float f = 1.0f; f >= 0.0f; f-=0.1f) {
					for(int i = 0; i < SOUNDPOOL_LOOPS; i++) {
						try { Thread.sleep(100); } catch(InterruptedException e) { }
						if(soundPool!=null) {
							soundPool.setVolume(streamIds[i], f*streamVolume, f*streamVolume);
						} else {
							Log.i(TAG,"soundPool null while fading out");
							return;
						}
					}
				}
			}
    	});
    	fadeThread.start();
    }
    public void pause() {
    	if(soundPool!=null && initCompleted && !paused) {
    		paused=true;
			for (int i = 0; i < SOUNDPOOL_STREAMS; i++){
				soundPool.pause(streamIds[i]);
			}
    	}
    }
    public boolean resume() {
		//obtain current media volume again - in case it changed elsewhere, e.g. another app
    	if(context!=null) {
		    setStreamVolume(context);
    	}
    	if(soundPool!=null && initCompleted && paused) {
			for (int i = 0; i < SOUNDPOOL_STREAMS; i++) {
				soundPool.resume(streamIds[i]);
			}
    	}
		paused=false;
		return initCompleted;
    }
    public void resume(final int track) {
    	if(soundPool!=null && initCompleted && paused) {
    		soundPool.resume(streamIds[track]);
    	}
    }
    public void resume(final int track, final int repeats) {
    	if(soundPool!=null && initCompleted && !paused) {
    		soundPool.setLoop(streamIds[track], repeats);
    		soundPool.resume(streamIds[track]);
    	}
    }
    public void play(int track) {
    	play(track, 0);
    }
    public void play(int track, int repeats) {
    	if(track==BUMP) {
    		if(context!=null) {
	    		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	    		v.vibrate(8);
    		}
    	}
    	else if (soundPool!=null && initCompleted) {
			streamIds[track] = soundPool.play(soundIds[track], 1.0f*streamVolume, 1.0f*streamVolume, 1, repeats, 1f);
        }
    }
    public void stop(int track) {
    	if(soundPool != null) {
	    	soundPool.stop(streamIds[track]);
    	}
    }
    public static void setEnabled(boolean enable) {
    	enabled = enable;
    }

    public static void setVolume(Context context) {
    	SoundTracks tracks = SoundTracks.getInstance();
    	if(tracks!=null) {
    		tracks.setStreamVolume(context);
    	}
    }

	public void setStreamVolume(Context context) {
        AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
}
