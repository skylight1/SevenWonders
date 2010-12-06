package skylight1.sevenwonders.services;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import skylight1.sevenwonders.R;

public class SoundTracks
{
	private static final String TAG = "SoundTracks";

	public static final int SOUNDTRACK = 0;
	public static final int WIND = 1;
	public static final int SPELL = 2;
	public static final int BUMP = 3;

	private final int soundResources[] = {
			R.raw.soundtrack, //loops
			R.raw.wind, //loops
			R.raw.spell,
			R.raw.bump
			};

	private final int SOUNDPOOL_STREAMS = soundResources.length;
	private final int SOUNDPOOL_LOOPS = 2;

	private int soundIds[]  = new int[SOUNDPOOL_STREAMS];
	private int streamIds[] = new int[SOUNDPOOL_STREAMS];

	static private boolean enabled = true;

	private Context context;
	private SoundPool soundPool;
    private int streamVolume;
    private boolean running;
    private boolean paused;
    private boolean initCompleted = true;

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

				// "load" tracks (play actually loads into memory) then play at no volume and pause streams
	    		for (int i = 0; i < SOUNDPOOL_STREAMS; i++) {
	    			soundIds[i]  =  soundPool.load(context, soundResources[i], 1);
	    			streamIds[i] = 0;
	    		}

	    		// except do play the first soundtrack with volume (looped)
    			while(streamIds[SOUNDTRACK]==0 && running) {
    				try { Thread.sleep(200); } catch(InterruptedException e) { } //TODO optimize this wait
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
	    		initCompleted=true;
            	Log.i(TAG, "all sounds loaded");
        	}
    	}).start();
    }
    private void release()
    {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            System.gc();
    		initCompleted=true;
        }
    }
    public void stop() {
    	if(soundPool != null && initCompleted) {
        	running = false;
        	context = null;
			for(int i = 0; i <= SOUNDPOOL_LOOPS; i++) { // stop looping sounds
				soundPool.setLoop(streamIds[i], 0);
			}
			
			//fade out last loop
			fadeout();
			
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
					Log.d(TAG,"soundPool stopping and unloading soundtrack");					
					soundPool.stop(streamIds[track]);
		    		soundPool.unload(streamIds[track]);
				} else {
					Log.i(TAG,"soundPool is null while stopping "+track);
					return;
				}
			}
    	});
    	if(track==SOUNDTRACK) {
    		Log.d(TAG, "starting wind");
    		//soundPool.setVolume(streamIds[WIND], 1.0f*streamVolume, 1.0f*streamVolume);
    		//resume(WIND,-1);
    		play(WIND,-1);
    	}
    	fadeThread.start();
    }
    public void fadeout() {
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
        if (soundPool!=null && initCompleted) {
			streamIds[track] =  soundPool.play(soundIds[track], 1.0f*streamVolume, 1.0f*streamVolume, 1, repeats, 1f);
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
    //sets volume to all sounds (need to call from vol button handler)
    public void setVolume(int streamVolume) {
    	if(soundPool!=null && initCompleted) {
			for (int i = 0; i < SOUNDPOOL_STREAMS; i++) {
				soundPool.setVolume(streamIds[i],1.0f*streamVolume,1.0f*streamVolume);
			}
    	}
    }
}
