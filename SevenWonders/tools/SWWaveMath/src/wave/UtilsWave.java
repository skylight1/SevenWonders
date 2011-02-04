package wave;

import static wave.Constants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.List;

import javax.vecmath.Vector3d;

/**
 * The Class UtilsWave. Some writing utilities.
 */
public class UtilsWave{
	
	/** The Constant Rect. */
	static final String Rect = OUT_DIR + "/rectangles.txt";
	
	/** The Constant Triag. */
	static final String Triag = OUT_DIR + "/triangles.txt";
	
	/** The Constant WonTriag. */
	static final String WonTriag = OUT_DIR + "/wondersTriangles.txt";
	
	/** The Constant WonTriagObj. */
	static final String WonTriagObj = TMP_DIR + "/triag";
	
	/**
	 * f: this routine formats a number to 4 digits. 
	 * 
	 * @param val the val
	 * 
	 * @return the string
	 */
	static String f(double val){
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(6);
		nf.setMinimumFractionDigits(1);
		return nf.format(val);
	}
	
	/**
	 * Write rect.
	 * 
	 * @param myList the my list
	 */
	static void writeRect(List<RectangleWave> myList){
		PrintWriter pw=null;
		try {
			File myFile = new File(Rect);
			pw = new PrintWriter(myFile);		
			for (RectangleWave r: myList){
				pw.println(r.id);
				for (int i=0;i<4;i++){
					pw.println("x="+UtilsWave.f(r.vertex[i].getP().x)+", y="+UtilsWave.f(r.vertex[i].getP().y)+", z="+UtilsWave.f(r.vertex[i].getP().z)
							+", u="+ UtilsWave.f(r.vertex[i].getUv().x)+", v="+UtilsWave.f(r.vertex[i].getUv().y)
							+", nx="+ UtilsWave.f(r.vertex[i].getNormal().x)+", " 
							+"ny="+UtilsWave.f(r.vertex[i].getNormal().y)+", nz="+ 
							UtilsWave.f(r.vertex[i].getNormal().z));
				}
			}
		}catch(FileNotFoundException ex){
			System.out.println("File Output Problem");
			ex.printStackTrace();
		}finally{
			if (pw!=null){
				pw.flush();
				pw.close();
			}
		}
	}
	
	/**
	 * Write triag.
	 * 
	 * @param myList the my list
	 */
	static void writeTriag(List<TriangleWave> myList){
		PrintWriter pw=null;
		try {
			File myFile = new File(Triag);
			pw = new PrintWriter(myFile);
			for (TriangleWave r: myList){
				pw.println(r.id);
				for (int i=0;i<3;i++){
					pw.println("x="+UtilsWave.f(r.vertex[i].getP().x)+", y="+UtilsWave.f(r.vertex[i].getP().y)+", " 
							+"z="+UtilsWave.f(r.vertex[i].getP().z)
							+", u="+ UtilsWave.f(r.vertex[i].getUv().x)+", v="+UtilsWave.f(r.vertex[i].getUv().y)
							+", nx="+ UtilsWave.f(r.vertex[i].getNormal().x)+", ny="+f(r.vertex[i].getNormal().y)+", nz="+ f(r.vertex[i].getNormal().z));
				}
			}
		}catch(FileNotFoundException ex){
			System.out.println("File Output Problem");
			ex.printStackTrace();
		}finally{
			if (pw!=null){
				pw.flush();
				pw.close();
			}
		}
	}
	
	/**
	 * Write wonders triagle list in openGL statements.
	 * 
	 * @param myList the my list
	 */
	static void writeWondersTriag(List<TriangleWave> myList){	

		PrintWriter pw=null;
		try {
			File myFile = new File(WonTriag);
			pw = new PrintWriter(myFile);
			for (TriangleWave r: myList){
				pw.print("anOpenGLGeometryBuilder.add3DTriangle(");
				pw.print(UtilsWave.f(r.vertex[0].getP().x)+"f,"
						+UtilsWave.f(r.vertex[0].getP().y)+"f,"+UtilsWave.f(r.vertex[0].getP().z)+"f,");
				pw.print(UtilsWave.f(r.vertex[1].getP().x)+"f,"+UtilsWave.f(r.vertex[1].getP().y)+"f,"+UtilsWave.f(r.vertex[1].getP().z)+"f,");
				pw.print(UtilsWave.f(r.vertex[2].getP().x)+"f,"+UtilsWave.f(r.vertex[2].getP().y)+"f,"+UtilsWave.f(r.vertex[2].getP().z)+"f)");	
				pw.println();
				pw.print(".setTextureCoordinates(");
				pw.print(UtilsWave.f(r.vertex[0].getUv().x)+"f,"+UtilsWave.f(r.vertex[0].getUv().y)+"f,");
				pw.print(UtilsWave.f(r.vertex[1].getUv().x)+"f,"+UtilsWave.f(r.vertex[1].getUv().y)+"f,");
				pw.print(UtilsWave.f(r.vertex[2].getUv().x)+"f,"+UtilsWave.f(r.vertex[2].getUv().y)+"f)");
				pw.println();
				pw.print(".setNormal(");
				pw.print(UtilsWave.f(r.vertex[0].getNormal().x)+"f, "+UtilsWave.f(r.vertex[0].getNormal().y)+"f,"+ UtilsWave.f(r.vertex[0].getNormal().z)+"f,");
				pw.print(UtilsWave.f(r.vertex[1].getNormal().x)+"f, "+UtilsWave.f(r.vertex[1].getNormal().y)+"f,"+ UtilsWave.f(r.vertex[1].getNormal().z)+"f,");
				pw.print(UtilsWave.f(r.vertex[2].getNormal().x)+"f, "+UtilsWave.f(r.vertex[2].getNormal().y)+"f,"+ UtilsWave.f(r.vertex[2].getNormal().z)+"f);");
				pw.println();
			}
		}catch(FileNotFoundException ex){
			System.out.println("File Output Problem");
			ex.printStackTrace();
		}finally{
			if (pw!=null){
				pw.flush();
				pw.close();
			}
		}
	}
	/**
	 * Write wonders triagle list in openGL statements.
	 * 
	 * @param myList the my list
	 */
	static void writeRectObj(VertexWave[][] rectArray,int num1, int num2, int fileno){	

	
		PrintWriter pw=null;
		try {
			File myFile = new File(WonTriagObj+fileno+".obj");
			pw = new PrintWriter(myFile);
			
			for (int j=0;j<num2;j++){
				for (int i=0;i<num1;i++){
					VertexWave v=rectArray[i][j];
					pw.print("v ");
					pw.print(UtilsWave.f(v.getP().x)+" "
							+UtilsWave.f(v.getP().y)+" "+UtilsWave.f(v.getP().z));
					pw.println("");
				}
			}
			for (int j=0;j<num2;j++){
				for (int i=0;i<num1;i++){
					VertexWave v=rectArray[i][j];
					pw.print("vt ");
					pw.print(UtilsWave.f(v.getUv().x)+" "+UtilsWave.f(1f-v.getUv().y));
					pw.println("");
				}
			}
			for (int j=0;j<num2;j++){
				for (int i=0;i<num1;i++){
					VertexWave v=rectArray[i][j];
					pw.print("vn ");
					pw.print(UtilsWave.f(v.getNormal().x)+" "+UtilsWave.f(v.getNormal().y)+" "+ UtilsWave.f(v.getNormal().z));
					double sum= Math.sqrt(v.getNormal().x*v.getNormal().x+
							v.getNormal().y*v.getNormal().y +
							v.getNormal().z*v.getNormal().z);
					//pw.print(" sum=");pw.print(sum);
					
					pw.println("");
				}
			}
			for (int j=0;j<num2-1;j++){
				for (int i=0;i<num1-1;i++){
					int index=num1*j+i+1;
					pw.print("f ");
					pw.print(index+"/"+index+"/"+index +" "+
							(index+1)+"/"+(index+1)+"/"+(index+1)+" "+
							(index+num1)+"/"+(index+num1)+"/"+(index+num1));
					pw.println("");
					pw.print("f ");
					pw.print((index+1)+"/"+(index+1)+"/"+(index+1)+" "+
							(index+num1+1)+"/"+(index+num1+1)+"/"+(index+num1+1)+" "+
							(index+num1)+"/"+(index+num1)+"/"+(index+num1));
					pw.println("");
				}
			}
			
		}catch(FileNotFoundException ex){
			System.out.println("File Output Problem");
			ex.printStackTrace();
		}finally{
			if (pw!=null){
				pw.flush();
				pw.close();
			}
		}
	}
	static void writeRectObj2(VertexWave[][] rectArray,int num1, int num2){	

		
		PrintWriter pw=null;
		try {
			File myFile = new File(WonTriagObj);
			pw = new PrintWriter(myFile);
			
			for (int j=0;j<num2;j++){
				for (int i=0;i<num1;i++){
					VertexWave v=rectArray[i][j];
					pw.print("v ");
					pw.print(v.getP().x+" "
							+v.getP().y+" "+v.getP().z);
					pw.println(" ");
				}
			}
			for (int j=0;j<num2;j++){
				for (int i=0;i<num1;i++){
					VertexWave v=rectArray[i][j];
					pw.print("vt ");
					pw.print(v.getUv().x+" "+v.getUv().y);
					pw.println(" ");
				}
			}
			for (int j=0;j<num2;j++){
				for (int i=0;i<num1;i++){
					VertexWave v=rectArray[i][j];
					pw.print("vn ");
					pw.print(v.getNormal().x+" "+v.getNormal().y+" "+ v.getNormal().z);
					//double sum= Math.sqrt(v.getNormal().x*v.getNormal().x+
					//		v.getNormal().y*v.getNormal().y +
					//		v.getNormal().z*v.getNormal().z);
					//pw.print(" sum=");pw.print(sum);
					
					pw.println(" ");
				}
			}
			for (int j=0;j<num2-1;j++){
				for (int i=0;i<num1-1;i++){
					int index=num1*j+i+1;
					pw.print("f ");
					pw.print(index+"/"+index+"/"+index +" "+
							(index+1)+"/"+(index+1)+"/"+(index+1)+" "+
							(index+num1)+"/"+(index+num1)+"/"+(index+num1));
					pw.println(" ");
					pw.print("f ");
					pw.print((index+1)+"/"+(index+1)+"/"+(index+1)+" "+
							(index+num1+1)+"/"+(index+num1+1)+"/"+(index+num1+1)+" "+
							(index+num1)+"/"+(index+num1)+"/"+(index+num1));
					pw.println(" ");
				}
			}
			
		}catch(FileNotFoundException ex){
			System.out.println("File Output Problem");
			ex.printStackTrace();
		}finally{
			if (pw!=null){
				pw.flush();
				pw.close();
			}
		}
	}
	/**
	 * Write VertexWave array.
	 * 
	 * @param vArray the v array
	 * @param num1 the num1
	 * @param num2 the num2
	 */
	static void writeArray(VertexWave[][] vArray, int num1,int num2){
		PrintWriter pw=null;
		try {
			File myFile = new File(TMP_DIR + "/array2d.txt");
			pw = new PrintWriter(myFile);

			for (int i=0;i<num1;i++){
				for (int j=0; j< num2;j++){
					VertexWave myV=vArray[i][j];
					pw.println("i="+i+",j="+j+",x="+UtilsWave.f(myV.getP().x)+", " +
							"y="+UtilsWave.f(myV.getP().y)+", z="+UtilsWave.f(myV.getP().z)
							+", u="+ UtilsWave.f(myV.getUv().x)+", v="+f(myV.getUv().y));
				}
			}

		}catch(FileNotFoundException ex){
			System.out.println("File Output Problem");
			ex.printStackTrace();
		}finally{
			if (pw!=null){
				pw.flush();
				pw.close();
			}
		}

	}
	
	/**
	 * Write cross product array.
	 * 
	 * @param vArray the v array
	 * @param num1 the num1
	 * @param num2 the num2
	 * @param fname the fname
	 */
	static void writeCrossArray(Vector3d[][] vArray, int num1,int num2, String fname){
		PrintWriter pw=null;
		try {
			File myFile = new File(fname);
			pw = new PrintWriter(myFile);
			for (int i=0;i<num1;i++){
				for (int j=0; j< num2;j++){
					Vector3d myV=vArray[i][j];
					pw.println("i="+i+",j="+j+", x="+f(myV.x)+", y="+f(myV.y)+", z="+f(myV.z));
				}
			}
		}catch(FileNotFoundException ex){
			System.out.println("File Output Problem");
			ex.printStackTrace();
		}finally{
			if (pw!=null){
				pw.flush();
				pw.close();
			}
		}	
	}

}
