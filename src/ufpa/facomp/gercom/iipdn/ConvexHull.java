package ufpa.facomp.gercom.iipdn;

import java.util.*;

/**
 * Classe responsável por organizar as coordenadas do motes de cada cluster
 * em forma de convex hull.
 */
public class ConvexHull{ 
	
	/** Coordenadas x e y. */
	double x, y;
	
	/** Ângulos. */
	double angle;
	
	/** Armazena todas as coordenadas que formarão um convex/cluster. */
	static Vector<ConvexHull> hull = new Vector<ConvexHull>(); 
	
	/** Coordenada x do centro do convex/cluster. */
	static double centerX;
	
	/** Coordenada y do centro do convex/cluster. */
	static double centerY;

	/**
	 * Construtor para pontos dentro do convex/cluster.
	 * 
	 * @param x coordenada x de um mote
	 * @param y coordenada y de um mote
	 */
	public ConvexHull(double x, double y){ 
		this.x=x; 
		this.y=y; 
	} 

	/**
	 * Constroi o convex/cluster a partir da lista de coordenadas.
	 * 
	 * @param points lista de coordenadas
	 * @param n tamanho da lista de coordenadas
	 */
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

	/**
	 * Armazena o ângulo.
	 * 
	 * @param angle ângulo
	 */
	public void setAngle(double angle) {
		this.angle=angle;
	}

	/**
	 * Retorna um ângulo.
	 * 
	 * @return angle ângulo
	 */
	public double getAngle() {
		return this.angle;
	}

	/**
	 * Retorna a orientação do convex/cluster.
	 * 
	 * @param p posição de um mote pertencente ao grupo
	 * @param q posição de um mote pertencente ao grupo
	 * @param r posição de um mote pertencente ao grupo
	 * @return 0 se são colinares
	 * @return 1 se no sentido horário
	 * @return 2 se no sentido anti-horário
	 */
	public static int orientation(ConvexHull p, ConvexHull q, ConvexHull r) { 
		double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y); 

		if (val == 0) return 0; 
		return (val > 0)? 1: 2; 
	}


	/**
	 * Printa o poligono 
	 */
	public static void showPolygon() {
		for (ConvexHull temp : ConvexHull.hull)
			System.out.println("(" + temp.x + ", " +  temp.y + ")"); 
	}

	
	/**
	 * Calcula o centro do poligono 
	 */
	public static void calculateCenterPolygon() {
		centerX=0;
		centerY=0;
		
		for (ConvexHull temp : ConvexHull.hull) {
			centerX = centerX + temp.x;
			centerY = centerY + temp.y;
		}
		centerX=centerX/ConvexHull.hull.size();
		centerY=centerY/ConvexHull.hull.size();
	 
	}

	
	/**
	 * Calcula os ângulos dos pontos do poligono 
	 */
	public static void calculateAnglePoints() {
		for (int i=0;i<ConvexHull.hull.size();i++) {
			ConvexHull.hull.get(i).setAngle(calculateAngle( ConvexHull.hull.get(i).x, ConvexHull.hull.get(i).y, centerX, centerY));
		}
	}

	
	/**
	 * Exibe os ângulos 
	 */
	public static void showAngles() {
		System.out.println("Angulos:");
		for (ConvexHull temp : ConvexHull.hull)
			System.out.println(temp.angle ); 
	}

	/**
	 * Calcula o ângulo entre uma coordenada e outra.
	 * 
	 * @param x coordenada x de um mote
	 * @param y coordenada y de um mote
	 * @param refX coordenada x do centro do convex/cluster
	 * @param refY coordenada y do centro do convex/cluster
	 * @return angle 
	 */
	public static double calculateAngle(double x, double y, double refX, double refY) {
		float angle = (float) Math.toDegrees(Math.atan2(y - refY, x - refX));

		if(angle < 0){
			angle += 360;
		}

		return angle;
	}
	
	/**
	 * Limpa a lista com as coordenadas 
	 */
	public static void clearHull() {
		hull.clear();
	}

} 

