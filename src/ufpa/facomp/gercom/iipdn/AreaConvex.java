package ufpa.facomp.gercom.iipdn;

/**
 *  Classe responsável pelo cálculo da área dentro do convex/cluster.
 */
public class AreaConvex {

	/** 
	 * Calcula a área do convex/cluster.
	 * 
	 * @param X coordenadas x dos membros do cluster
	 * @param Y coordenadas y dos membros do cluster
	 * @param n número de membros do cluster
	 * @return área do convex/cluster
	 */
	public static double polygonArea(double [] X, double [] Y,int n){ 
		double area = 0.0; 

		int h = n - 1; 
		for (int k = 0; k < n; k++) 
		{ 
			area += (X[h] + X[k]) * (Y[h] - Y[k]); 
			h = k; 
		} 
		return Math.abs(area / 2.0); 
	}

	/**
	 * Calcula a área do convex/cluster.
	 * 
	 * @return área do convex/cluster
	 */
	public static double computeArea() {
		double computedArea;
		double greaterAngle;
		int greaterAngleIndex;
		int i=0;
		int n = ConvexHull.hull.size();
		double X[]=new double[n];
		double Y[]=new double[n];

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
			i++;
		}		
		computedArea=AreaConvex.polygonArea(X, Y, n);
		
		return computedArea;
	}
}
