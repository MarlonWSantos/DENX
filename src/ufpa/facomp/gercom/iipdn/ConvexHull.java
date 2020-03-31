package ufpa.facomp.gercom.iipdn;

import java.util.*;	

public class ConvexHull{ 
	double x, y;
	double angle;
	static Vector<ConvexHull> hull = new Vector<ConvexHull>(); 
	static double centerX=0, centerY=0;

	public ConvexHull(double x, double y){ 
		this.x=x; 
		this.y=y; 
	} 

	public static void convexHull(ConvexHull points[], int n){ 
		if (n < 3) return; 

		int l = 0; 
		for (int i = 1; i < n; i++) 
			if (points[i].x < points[l].x) 
				l = i; 

		int p = l, q; 
		do{
			
			hull.add(points[p]); 

			q = (p + 1) % n; 

			for (int i = 0; i < n; i++){ 
				if (orientation(points[p], points[i], points[q]) 
						== 2) 
					q = i; 
			} 
			p = q; 

		} while (p != l); 		
	} 

	public void setAngle(double angle) {
		this.angle=angle;
	}

	public double getAngle() {
		return this.angle;
	}

	public static int orientation(ConvexHull p, ConvexHull q, ConvexHull r) { 
		double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y); 

		if (val == 0) return 0; 
		return (val > 0)? 1: 2; 
	}

	//Printa o poligono
	public static void showPolygon() {
		System.out.println("Convex:");
		for (ConvexHull temp : ConvexHull.hull)
			System.out.println("(" + temp.x + ", " +  temp.y + ")"); 
	}

	//Calcula o centro do poligono
	public static void calculateCenterPolygon() {
		for (ConvexHull temp : ConvexHull.hull) {
			centerX = centerX + temp.x;
			centerY = centerY + temp.y;
		}
		centerX=centerX/ConvexHull.hull.size();
		centerY=centerY/ConvexHull.hull.size();

		System.out.println("O centro do poligono é ("+centerX+","+centerY+")"); 
	}

	//Calcula os angulos dos pontos do poligono
	public static void calculateAnglePoints() {
		for (int i=0;i<ConvexHull.hull.size();i++) {
			ConvexHull.hull.get(i).setAngle(calculateAngle( ConvexHull.hull.get(i).x, ConvexHull.hull.get(i).y, centerX, centerY));
		}
	}

	//Printa os angulos
	public static void showAngles() {
		System.out.println("Angulos:");
		for (ConvexHull temp : ConvexHull.hull)
			System.out.println(temp.angle ); 
	}

	public static double calculateAngle(double x, double y, double refX, double refY) {
		float angle = (float) Math.toDegrees(Math.atan2(y - refY, x - refX));

		if(angle < 0){
			angle += 360;
		}

		return angle;
	}
	
	public static void clearHull() {
		hull.clear();
	}

} 

