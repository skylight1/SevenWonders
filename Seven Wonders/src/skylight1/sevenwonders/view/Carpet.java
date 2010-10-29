package skylight1.sevenwonders.view;
import java.util.Iterator;
import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

import skylight1.opengl.OpenGLGeometry;
import skylight1.opengl.OpenGLGeometryBuilder;
import skylight1.opengl.GeometryBuilder.NormalizableTriangle3D;
import skylight1.opengl.GeometryBuilder.TexturableRectangle2D;
import skylight1.opengl.GeometryBuilder.TexturableTriangle3D;
import skylight1.sevenwonders.R;
import android.os.SystemClock;

/**
 * The magic carpet!
 * This also records a history of turn events and returns a roll angle based on the recent turns.
 * @author Johannes
 *
 */
public class Carpet {
	
	private static final int HISTORY_SIZE = 6;

	/**
	 * A helper class used to store a float value together with a related time tick count.
	 * @author Johannes
	 *
	 */
	private class TimestampedFloat {
		long time;
		float value;
		
		public TimestampedFloat(long time, float value){
			this.time = time;
			this.value = value;
		}

		public long createdHowManyMillisAgo(final long aGivenTime) {
			return aGivenTime - time;
		}

		public void setValues(long time, float value) {
			this.time = time;
			this.value = value;
		}
	}
	
	private OpenGLGeometry[] carpetGeometry;
	
	private static final int PERIOD_FOR_CARPET_ANIMATION_CYCLE = 800;
	private static final long TIMESPAN_IN_MILLIS_TO_CONSIDER_FOR_CARPET_ROLLING = 3000;
	private LinkedList<TimestampedFloat> turnEventsHistory;
	private float lastAngle;
	private float priorTolastAngle;

	private SevenWondersGLRenderer renderer;	
	
	private static final int[] CARPET_OBJ_IDS = new int[] { R.raw.carpet_wave_0, R.raw.carpet_wave_1,
		R.raw.carpet_wave_2, R.raw.carpet_wave_3, R.raw.carpet_wave_4, R.raw.carpet_wave_5, R.raw.carpet_wave_6,
		R.raw.carpet_wave_7, R.raw.carpet_wave_8, R.raw.carpet_wave_9 };
	
	public Carpet(SevenWondersGLRenderer renderer) {
		priorTolastAngle = 0f;
		lastAngle = 0f;
		turnEventsHistory = new LinkedList<TimestampedFloat>();
		this.renderer = renderer; 
	}	
	
	public void createGeometry(
			final OpenGLGeometryBuilder<TexturableTriangle3D<NormalizableTriangle3D<Object>>, TexturableRectangle2D<Object>> anOpenGLGeometryBuilder) {

		carpetGeometry = new OpenGLGeometry[CARPET_OBJ_IDS.length];
		for (int i = 0; i < CARPET_OBJ_IDS.length; i++) {
			anOpenGLGeometryBuilder.startGeometry();
			renderer.loadRequiredObj(CARPET_OBJ_IDS[i], anOpenGLGeometryBuilder);
			carpetGeometry[i] = anOpenGLGeometryBuilder.endGeometry();
		}
	}
	
	/**
	 * Add one turn and a timestamp of when it happened to the short list of recent turns. 
	 * @param anAngleOfTurn how much did we turn.
	 */
	public void recordTurnAngle(float anAngleOfTurn) {
		synchronized (this) {			
			if (turnEventsHistory.size() > HISTORY_SIZE) {
				final TimestampedFloat reuseMe = turnEventsHistory.poll();
				reuseMe.setValues(SystemClock.uptimeMillis(), anAngleOfTurn);
			} else {
				turnEventsHistory.add(new TimestampedFloat(SystemClock.uptimeMillis(), anAngleOfTurn));
			}
		}		
	}

	/**
	 * Returns the roll angle depending on how much time has passed since the last couple turns.
	 * If there were a lot of recent turns, the roll angle will be large, it the carpet has not turned for a while, this will
	 * be 0.
	 * @return angle by which to roll the carpet. 
	 */
	public float getRollAngle() {
			float angle = 0;			
			synchronized (this) {
				final long timeOfMostRecentEvent = turnEventsHistory.getLast().time;
				final Iterator<TimestampedFloat> iterator = turnEventsHistory.iterator();
				while (iterator.hasNext()) {
					TimestampedFloat v = iterator.next();
					long millisSinceCreation = v.createdHowManyMillisAgo(timeOfMostRecentEvent);					
					if (millisSinceCreation > 0 && millisSinceCreation <  TIMESPAN_IN_MILLIS_TO_CONSIDER_FOR_CARPET_ROLLING ) {
						float fractionOfWindow = (float) millisSinceCreation / TIMESPAN_IN_MILLIS_TO_CONSIDER_FOR_CARPET_ROLLING;
						angle += v.value * (1f-fractionOfWindow); 
					}
				}
			}
			angle = -1.0f*angle;
			priorTolastAngle = lastAngle; 
			angle = (lastAngle+angle+priorTolastAngle)/3;
			lastAngle = angle;
			return angle;	
	}


	public void draw(GL10 aGl) {
		final int carpetIndex = (int) ((SystemClock.uptimeMillis() % PERIOD_FOR_CARPET_ANIMATION_CYCLE) / (PERIOD_FOR_CARPET_ANIMATION_CYCLE / carpetGeometry.length));
		
		float rollAngle = getRollAngle();		
		if (Math.abs(rollAngle) > 0.001) {
			aGl.glPushMatrix();		
			aGl.glRotatef(rollAngle, 0f, 0f, 1f);
			carpetGeometry[carpetIndex].draw(aGl);		
			aGl.glPopMatrix();
		} else
		{
			carpetGeometry[carpetIndex].draw(aGl);
		}
	}
}
