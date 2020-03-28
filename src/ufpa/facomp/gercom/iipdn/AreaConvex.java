package ufpa.facomp.gercom.iipdn;


public class AreaConvex {

	public static double polygonArea(float [] X, float [] Y,int n){ 
		System.out.println("Length "+X[2]);
		double area = 0.0; 

		int h = n - 1; 
		for (int k = 0; k < n; k++) 
		{ 
			area += (X[h] + X[k]) * (Y[h] - Y[k]); 
			h = k; 
		} 
		return Math.abs(area / 2.0); 
	}

	public static void computeArea() {
		double computedArea;
		float greaterAngle;
		int greaterAngleIndex;
		int i=0;
		int n = ConvexHull.hull.size();
		float X[]=new float[n];
		float Y[]=new float[n];

		while(ConvexHull.hull.isEmpty()==false) {
			greaterAngle=1000;
			greaterAngleIndex=0;
			for(int j=0; j<ConvexHull.hull.size();j++) {
				if(ConvexHull.hull.get(j).angle<greaterAngle) {
					greaterAngle=ConvexHull.hull.get(j).angle;
					greaterAngleIndex=j;
				}
			}

			X[i]=ConvexHull.hull.get(greaterAngleIndex).x;
			Y[i]=ConvexHull.hull.get(greaterAngleIndex).y;
			ConvexHull.hull.remove(greaterAngleIndex);
			System.out.println("("+X[i]+","+Y[i]+")");
			i++;
		}		
		computedArea=AreaConvex.polygonArea(X, Y, n);
		System.out.println("A area calculada é: "+computedArea);
	}
}
