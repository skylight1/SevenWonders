package wave;

import static wave.Constants.*;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;


// TODO: Auto-generated Javadoc
/**
 * The Class RectangleWave.
 */
public class RectangleWave{
	
	/** The id. */
	int id;
	
	/** The vertex. */
	VertexWave[] vertex;
	
	/** The Constant X1. */
	static final float X1=-0.5f;
	
	/** The Constant X2. */
	static final float X2=0.5f;
	
	/** The Constant Z1. */
	static final float Z1=-1.25f;
	
	/** The Constant Z2. */
	static final float Z2=1.25f;
	
	/** The Constant S1. */
	static final float S1=0.0f;
	
	/** The Constant S2. */
	static final float S2= 320f/1024f;
	
	/** The Constant T1. */
	static final float T1=0.0f;
	
	/** The Constant T2. */
	static final float T2=480f/1024f;
	
	/** The Constant Vecu. */
	static final String Vecu = TMP_DIR + "/uArray.txt";
	
	/** The Constant Vecv. */
	static final String Vecv= TMP_DIR + "/vArray.txt";
	
	/** The Constant VecCross. */
	static final String VecCross= TMP_DIR + "/crossArray.txt";
	
	/**
	 * Instantiates a new rectangle.
	 */
	public RectangleWave(){};
	
	/**
	 * Instantiates a new rectangle.
	 * 
	 * @param id the id
	 * @param vertex the vertex
	 */
	public RectangleWave(int id, VertexWave[] vertex) {
		super();
		this.id = id;
		this.vertex = vertex;
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
	 * Gets the vertex.
	 * 
	 * @return the vertex
	 */
	public VertexWave[] getVertex() {
		return vertex;
	}

	/**
	 * Sets the vertex.
	 * 
	 * @param vertex the new vertex
	 */
	public void setVertex(VertexWave[] vertex) {
		this.vertex = vertex;
	}

	/**
	 * Subdiv rectangle.
	 * 
	 * @param num1 the num1
	 * @param num2 the num2
	 * 
	 * @return the list< rectangle>
	 */
	List<RectangleWave> subdivRectangle(int num1, int num2){
		VertexWave vArray[][] = new VertexWave[num1][num2];
		List<RectangleWave> recList = new ArrayList<RectangleWave>();
		int vertexCount=0;
		float x, y, z,u,v;
		for (int i=0;i<num1;i++){
			for (int j=0; j< num2;j++){
				x=X1+(i*(X2-X1)/(num1-1));
				z=Z1+j*(Z2-Z1)/(num2-1);
				y=-0.25f+(float)(-0.02*java.lang.Math.cos(z*java.lang.Math.PI*3.0+1.0*java.lang.Math.PI))
				//	y=-0.25f+(float)(-0.02*java.lang.Math.cos(z*java.lang.Math.PI*2.0))+
				+  (float)(-0.02*java.lang.Math.cos(x*java.lang.Math.PI*3.0+java.lang.Math.PI));
				//	y=(float)(-0.15*java.lang.Math.cos(z*java.lang.Math.PI));	
				u = S2*i/(num1-1);
				v = T2*j/(num2-1);
				Point3d vtx= new Point3d(x,y,z);
				Vector2d uv = new Vector2d(u,v);
				VertexWave myVertex = new VertexWave(vertexCount++, vtx, uv);
				vArray[i][j]=myVertex;
			}
		}
		UtilsWave.writeArray(vArray,  num1, num2);

		int rectCount=0;

		Vector3d vecu[][] = new Vector3d[num1][num2];
		Vector3d vecv[][] = new Vector3d[num1][num2];
		
		/* vector in the u direction */
		for (int i=0;i<num1-1;i++){
			for (int j=0; j< num2;j++){
				Point3d pt1=vArray[i+1][j].getP();
				Point3d pt2=vArray[i][j].getP();
				vecu[i][j]=new Vector3d(pt1.x-pt2.x,
						pt1.y-pt2.y,pt1.z-pt2.z);
			}
		}
		/* vector in the v direction */
		for (int i=0;i<num1;i++){
			for (int j=0; j< num2-1;j++){
				Point3d pt1=vArray[i][j+1].getP();
				Point3d pt2=vArray[i][j].getP();
				vecv[i][j]=new Vector3d(pt1.x-pt2.x,
						pt1.y-pt2.y,pt1.z-pt2.z);
			}
		}	
		//Right end u direction
		for (int i=0;i<num2;i++){ 
			vecu[num1-1][i]=new Vector3d(vecu[num1-2][i].x,
					vecu[num1-2][i].y,vecu[num1-2][i].z);

		}
		//top end  v direction
		for (int j=0;j<num1;j++){ 
			vecv[j][num2-1]=new Vector3d(vecv[j][num2-2].x,
					vecv[j][num2-2].y,vecv[j][num2-2].z);

		}

//		Utils.writeCrossArray(vecu,num1,num2,Vecu);
//		Utils.writeCrossArray(vecv,num1,num2,Vecv);
		Vector3d vecCross[][] = new Vector3d[num1][num2];
		vecCross[0][0]=new Vector3d();
		vecCross[0][0].cross(vecu[0][0],vecv[0][0]);
		vecCross[num1-1][0]=new Vector3d();
		vecCross[num1-1][0].cross(vecu[num1-1][0], vecv[num1-1][0]);
		vecCross[0][num2-1]=new Vector3d();
		vecCross[0][num2-1].cross(vecu[0][num2-1], vecv[0][num2-1]);
		vecCross[num1-1][num2-1]=new Vector3d();
		vecCross[num1-1][num2-1].cross(vecu[num1-1][num2-1], vecv[num1-1][num2-1]);

		for (int i=1;i<num1-1;i++){

			Vector3d vec1=new Vector3d();
			Vector3d vec2=new Vector3d();
			vec1.cross(vecu[i-1][0], vecv[i][0]);
			vec2.cross(vecu[i][0], vecv[i][0]);
			vec1.add(vec2);
			vecCross[i][0]=vec1;

			vec1=new Vector3d();
			vec2=new Vector3d();

			vec1.cross(vecu[i-1][num2-1], vecv[i][num2-1]);
			vec2.cross(vecu[i][num2-1], vecv[i][num2-1]);
			vec1.add(vec2);
			vecCross[i][num2-1]=vec1;
		}
		for (int j=1;j<num2-1;j++){
			Vector3d vec1=new Vector3d();
			Vector3d vec2=new Vector3d();
			vec1.cross(vecu[0][j-1], vecv[0][j-1]);
			vec2.cross(vecu[0][j], vecv[0][j-1]);
			vec1.add(vec2);
			vecCross[0][j]=vec1;

			vec1=new Vector3d();
			vec2=new Vector3d();	
			vec1.cross(vecu[num1-1][j-1], vecv[num1-1][j-1]);
			vec2.cross(vecu[num1-1][j], vecv[num1-1][j]);
			vec1.add(vec2);
			vecCross[num1-1][j]=vec1; 	
		}
		for (int i=1;i<num1-1;i++){
			for (int j=1; j< num2-1;j++){
				Vector3d vec1=new Vector3d();
				Vector3d vec2=new Vector3d();
				Vector3d vec3=new Vector3d();
				Vector3d vec4=new Vector3d();

				vec1.cross(vecu[i-1][j], vecv[i-1][j]);
				vec2.cross(vecu[i][j-1], vecv[i][j-1]);
				vec3.cross(vecu[i-1][j-1], vecv[i-1][j-1]);
				vec4.cross(vecu[i][j], vecv[i][j]);

				vec1.add(vec2);
				vec1.add(vec3);
				vec1.add(vec4);
				vecCross[i][j]=vec1;	
			}
		} 
//		Utils.writeCrossArray(vecCross,num1,num2,VecCross);

		for (int i=0;i<num1;i++){
			for (int j=0; j< num2;j++){
				vArray[i][j].setNormal(vecCross[i][j]);
			}
		}
		for (int i=0;i<num1-1;i++){
			for (int j=0; j< num2-1;j++){
				VertexWave[] vArray2 = new VertexWave[4];
				vArray2[0]=vArray[i][j];
				vArray2[1]=vArray[i+1][j];
				vArray2[2]=vArray[i+1][j+1];
				vArray2[3]=vArray[i][j+1];
				RectangleWave myRect = new RectangleWave(rectCount++, vArray2);
				recList.add(myRect);	
			}
		}  
		return recList; 
	}
	
	
	/**
	 * Subdiv rectangle.
	 * 
	 * @param num1 the num1
	 * @param num2 the num2
	 * 
	 * @return the list< rectangle>
	 */
	VertexWave[][] getRectangles(int num1, int num2, float p1, float p2){
		VertexWave vArray[][] = new VertexWave[num1][num2];
		List<RectangleWave> recList = new ArrayList<RectangleWave>();
		int vertexCount=0;
		float x, y, z,u,v;
		for (int i=0;i<num1;i++){
			for (int j=0; j< num2;j++){
				x=X1+(i*(X2-X1)/(num1-1));
				z=Z1+j*(Z2-Z1)/(num2-1);
				y=-0.25f+(float)(-0.02*java.lang.Math.cos(z*java.lang.Math.PI*3.0+p1*java.lang.Math.PI))
				//	y=-0.25f+(float)(-0.02*java.lang.Math.cos(z*java.lang.Math.PI*2.0))+
				+  (float)(-0.02*java.lang.Math.cos(x*java.lang.Math.PI*3.0+p2*java.lang.Math.PI));
				//	y=(float)(-0.15*java.lang.Math.cos(z*java.lang.Math.PI));	
				u = S2*i/(num1-1);
				v = T2*j/(num2-1);
				Point3d vtx= new Point3d(x,y,z);
				Vector2d uv = new Vector2d(u,v);
				VertexWave myVertex = new VertexWave(vertexCount++, vtx, uv);
				vArray[i][j]=myVertex;
			}
		}
		UtilsWave.writeArray(vArray,  num1, num2);

		int rectCount=0;

		Vector3d vecu[][] = new Vector3d[num1][num2];
		Vector3d vecv[][] = new Vector3d[num1][num2];
		
		/* vector in the u direction */
		for (int i=0;i<num1-1;i++){
			for (int j=0; j< num2;j++){
				Point3d pt1=vArray[i+1][j].getP();
				Point3d pt2=vArray[i][j].getP();
				vecu[i][j]=new Vector3d(pt1.x-pt2.x,
						pt1.y-pt2.y,pt1.z-pt2.z);
			}
		}
		/* vector in the v direction */
		for (int i=0;i<num1;i++){
			for (int j=0; j< num2-1;j++){
				Point3d pt1=vArray[i][j+1].getP();
				Point3d pt2=vArray[i][j].getP();
				vecv[i][j]=new Vector3d(pt1.x-pt2.x,
						pt1.y-pt2.y,pt1.z-pt2.z);
			}
		}	
		//Right end u direction
		for (int i=0;i<num2;i++){ 
			vecu[num1-1][i]=new Vector3d(vecu[num1-2][i].x,
					vecu[num1-2][i].y,vecu[num1-2][i].z);

		}
		//top end  v direction
		for (int j=0;j<num1;j++){ 
			vecv[j][num2-1]=new Vector3d(vecv[j][num2-2].x,
					vecv[j][num2-2].y,vecv[j][num2-2].z);

		}

//		Utils.writeCrossArray(vecu,num1,num2,Vecu);
//		Utils.writeCrossArray(vecv,num1,num2,Vecv);
		Vector3d vecCross[][] = new Vector3d[num1][num2];
		vecCross[0][0]=new Vector3d();
		vecCross[0][0].cross(vecu[0][0],vecv[0][0]);
		Vector3d myVec1 = normalizeVec(vecCross[0][0]);
		vecCross[0][0]=myVec1;
		vecCross[num1-1][0]=new Vector3d();
		vecCross[num1-1][0].cross(vecu[num1-1][0], vecv[num1-1][0]);
		Vector3d myVec2 = normalizeVec(vecCross[num1-1][0]);
		vecCross[num1-1][0]=myVec2;
		vecCross[0][num2-1]=new Vector3d();
		vecCross[0][num2-1].cross(vecu[0][num2-1], vecv[0][num2-1]);
		Vector3d myVec3 = normalizeVec(vecCross[0][num2-1]);
		vecCross[0][num2-1]=myVec3;
		vecCross[num1-1][num2-1]=new Vector3d();
		vecCross[num1-1][num2-1].cross(vecu[num1-1][num2-1], vecv[num1-1][num2-1]);
		Vector3d myVec4 = normalizeVec(vecCross[num1-1][num2-1]);
		vecCross[num1-1][num2-1]=myVec4;

		for (int i=1;i<num1-1;i++){

			Vector3d vec1=new Vector3d();
			Vector3d vec2=new Vector3d();
			vec1.cross(vecu[i-1][0], vecv[i][0]);
			vec2.cross(vecu[i][0], vecv[i][0]);
			vec1.add(vec2);
			Vector3d myVec5 = normalizeVec(vec1);
			vecCross[i][0]=myVec5;
			vec1=new Vector3d();
			vec2=new Vector3d();
			vec1.cross(vecu[i-1][num2-1], vecv[i][num2-1]);
			vec2.cross(vecu[i][num2-1], vecv[i][num2-1]);
			vec1.add(vec2);
			Vector3d myVec6 = normalizeVec(vec1);
			vecCross[i][num2-1]=myVec6;
		}
		for (int j=1;j<num2-1;j++){
			Vector3d vec1=new Vector3d();
			Vector3d vec2=new Vector3d();
			vec1.cross(vecu[0][j-1], vecv[0][j-1]);
			vec2.cross(vecu[0][j], vecv[0][j-1]);
			vec1.add(vec2);
			Vector3d myVec7 = normalizeVec(vec1);
			vecCross[0][j]=myVec7;
			vec1=new Vector3d();
			vec2=new Vector3d();	
			vec1.cross(vecu[num1-1][j-1], vecv[num1-1][j-1]);
			vec2.cross(vecu[num1-1][j], vecv[num1-1][j]);
			vec1.add(vec2);
			Vector3d myVec8 = normalizeVec(vec1);
			vecCross[num1-1][j]=myVec8; 	
		}
		for (int i=1;i<num1-1;i++){
			for (int j=1; j< num2-1;j++){
				Vector3d vec1=new Vector3d();
				Vector3d vec2=new Vector3d();
				Vector3d vec3=new Vector3d();
				Vector3d vec4=new Vector3d();

				vec1.cross(vecu[i-1][j], vecv[i-1][j]);
				vec2.cross(vecu[i][j-1], vecv[i][j-1]);
				vec3.cross(vecu[i-1][j-1], vecv[i-1][j-1]);
				vec4.cross(vecu[i][j], vecv[i][j]);

				vec1.add(vec2);
				vec1.add(vec3);
				vec1.add(vec4);
				Vector3d myVec = normalizeVec(vec1);
				vecCross[i][j]=myVec;	
			}
		} 
//		Utils.writeCrossArray(vecCross,num1,num2,VecCross);

		for (int i=0;i<num1;i++){
			for (int j=0; j< num2;j++){
				vArray[i][j].setNormal(vecCross[i][j]);
			}
		}
		for (int i=0;i<num1-1;i++){
			for (int j=0; j< num2-1;j++){
				VertexWave[] vArray2 = new VertexWave[4];
				vArray2[0]=vArray[i][j];
				vArray2[1]=vArray[i+1][j];
				vArray2[2]=vArray[i+1][j+1];
				vArray2[3]=vArray[i][j+1];
				RectangleWave myRect = new RectangleWave(rectCount++, vArray2);
				recList.add(myRect);	
			}
		}  
		return vArray; 
	}
	
	/**
	 * Normalize vec.
	 * 
	 * @param myVec the my vec
	 * 
	 * @return the vector3d
	 */
	Vector3d normalizeVec(Vector3d myVec){
		Vector3d newVec;
		double rmeanSquare=Math.sqrt(myVec.x*myVec.x + myVec.y*myVec.y + myVec.z*myVec.z);
	    double x=myVec.x/rmeanSquare;
	    double y=myVec.y/rmeanSquare;
	    double z=myVec.z/rmeanSquare;
		newVec=new Vector3d(x,y,z);
		return newVec;
	}
	/**
	 * Gets the l triag from l rect.
	 * 
	 * @param rList the r list
	 * 
	 * @return the l triag from l rect
	 */
	List<TriangleWave> getLTriagFromLRect(List<RectangleWave> rList){
		List<TriangleWave> triagList=new ArrayList<TriangleWave>();
		for (RectangleWave r: rList){
			List<TriangleWave> tList= Rect2TriangleWaves( r);
			triagList.addAll(tList);
		}
		return triagList;
	}
	
	/**
	 * Rect2 TriangleWaves.
	 * 
	 * @param r the r
	 * 
	 * @return the list< TriangleWave>
	 */
	List<TriangleWave> Rect2TriangleWaves(RectangleWave r){
		List<TriangleWave> tList= new ArrayList<TriangleWave>();
		VertexWave tArray1[] = new VertexWave[3];
		tArray1[0]=r.vertex[0];
		tArray1[1]=r.vertex[1];
		tArray1[2]=r.vertex[3];
		VertexWave tArray2[] = new VertexWave[3];
		tArray2[0]=r.vertex[1];
		tArray2[1]=r.vertex[2];
		tArray2[2]=r.vertex[3];
		int id=0;
		TriangleWave triag1= new TriangleWave(id++, tArray1);
		TriangleWave triag2= new TriangleWave(id++, tArray2);
		tList.add(triag1);
		tList.add(triag2);
		return tList;
	}
}