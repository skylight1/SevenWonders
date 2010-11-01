package wave;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;


// TODO: Auto-generated Javadoc
/**
 * The Class VertexWave.
 */
public class VertexWave{
	
	/** The id. */
	int id;
	
	/** The p. */
	private Point3d p;
	
	/** The uv a 2d vector represting the UV 
	 * co-ordinates used for Texture
	 */
	private Vector2d uv;	
	
	/** The normal to the surface at the vertex */
	private Vector3d normal;
	
	/**
	 * Instantiates a new vertex wave.
	 * 
	 * @param id the id
	 * @param p the p
	 * @param uv the uv
	 */
	public VertexWave(int id, Point3d p, Vector2d uv) {
		super();
		this.id = id;
		this.p = p;
		this.uv = uv;
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the p.
	 * 
	 * @return the p
	 */
	public Point3d getP() {
		return p;
	}
	
	/**
	 * Sets the p.
	 * 
	 * @param p the new p
	 */
	public void setP(Point3d p) {
		this.p = p;
	}
	
	/**
	 * Gets the uv.
	 * 
	 * @return the uv
	 */
	public Vector2d getUv() {
		return uv;
	}
	
	/**
	 * Sets the uv.
	 * 
	 * @param uv the new uv
	 */
	public void setUv(Vector2d uv) {
		this.uv = uv;
	}
	
	/**
	 * Gets the normal.
	 * 
	 * @return the normal
	 */
	public Vector3d getNormal() {
		return normal;
	}
	
	/**
	 * Sets the normal.
	 * 
	 * @param normal the new normal
	 */
	public void setNormal(Vector3d normal) {
		this.normal = normal;
	}			
} 

