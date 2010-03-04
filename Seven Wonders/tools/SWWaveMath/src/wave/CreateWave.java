package wave;

/*
 * This program writes a file with open GL statements for
 * carpet geometry 
 */
import java.util.List;

/**
 * The Class WondersWave.
 */
public class CreateWave {
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args){
		RectangleWave rect = new RectangleWave();
		for (int i=0;i<10;i++){
			VertexWave[][] recArray =  rect.getRectangles(10, 10,0.2f*i,.2f*i);
			UtilsWave.writeRectObj(recArray,10,10,i);
		}
	}
}