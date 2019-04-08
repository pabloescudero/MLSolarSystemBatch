package com.solarSystem.SolarSystemBatch.model;


import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "clima")
public class Clima {
	@Id
	private Integer iddia;
	private String pronostico;
	private float area_triangulo;
	private byte sol_triangulado;
	private byte alineacion_sol;
	private byte pico;	
	private Date fecha;

	public Integer getIdia() {
		return iddia;
	}
	public Clima setIdia(Integer idia) {
		this.iddia = idia;
		return this;
	}
	public String getPronostico() {
		return pronostico;
	}
	public Clima setPronostico(String pronostico) {
		this.pronostico = pronostico;
		return this;
	}
	public float getArea_triangulo() {
		return area_triangulo;
	}
	public Clima setArea_triangulo(float area_triangulo) {
		this.area_triangulo = area_triangulo;
		return this;
	}
	public byte getSol_triangulado() {
		return sol_triangulado;
	}
	public Clima setSol_triangulado(byte sol_triangulado) {
		this.sol_triangulado = sol_triangulado;
		return this;
	}
	public byte getAlineacion_sol() {
		return alineacion_sol;
	}
	public Clima setAlineacion_sol(byte alineacion_sol) {
		this.alineacion_sol = alineacion_sol;
		return this;
	}
	public byte getPico() {
		return pico;
	}
	public Clima setPico(byte pico) {
		this.pico = pico;
		return this;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public void process(Posicion pos1,Posicion pos2,Posicion pos3) {
		calcularArea(pos1,pos2,pos3);
		checkAlineacion(pos1,pos2,pos3);
		checkTriangulacionSol(pos1,pos2,pos3);
		predecir();
	}
	
	private void predecir() {
		this.pronostico = "Despejado";
		//Llegado a este punto tengo todos los datos como para hacer una prediccion puntual
		if(this.alineacion_sol == 1) {
			this.pronostico = "Sequia";
		}else if(this.sol_triangulado == 1) {
			this.pronostico = "Lluvia";
		}else if(this.area_triangulo == 0 && this.alineacion_sol == 0) {
			this.pronostico = "Presion Optima";
		}
	}
	
	private void calcularArea(Posicion pos1,Posicion pos2,Posicion pos3) {
		/*
		 * Para encontrar el area formado por 2 puntos en R2, con 
		 * simplemente hacer un determinante de los 3 puntos se tiene el area.
		 * A modo de no ensuciar el codigo, uso el determinante simplificado
		 * |X1,Y1|
		 * |X2,Y2|
		 * |X3,Y3|
		 */
		float X1 = pos1.getX();
		float Y1 = pos1.getY();
		float X2 = pos2.getX();
		float Y2 = pos2.getY();
		float X3 = pos3.getX();
		float Y3 = pos3.getY();
		
		this.area_triangulo = ((X1*Y2+X2*Y3+X3*Y1)-(X1*Y3+X3*Y2+X2*Y1))/2;
		
		if(this.area_triangulo<0) {
			this.area_triangulo*=-1;//si da negativo le cambio el signo
		}
	}
	
	private void checkAlineacion(Posicion pos1,Posicion pos2,Posicion pos3) {
		//Si el area del triangulo es 0, quiere decir que los 3 puntos estan alineados
		if(this.area_triangulo == 0) {
			/*
			 * Para comprobar si el punto (0,0) es parte de la recta que forman los 3 
			 * puntos, se evalua la ecuacion de la recta en el punto (0,0)  
			 */
			//Con 2 puntos ya es suficiente
			float X1 = pos1.getX();
			float Y1 = pos1.getY();
			float X2 = pos2.getX();
			float Y2 = pos2.getY();
			float test = 0;
			
			//Si la recta esta sobre los ejes me ahorro las cuentas
			if (X1 == 0 && X2 == 0 || Y1 == 0 && Y2 == 0) {
				this.alineacion_sol = 1;
			}else { 
	    		test = (-1*Y2)*X1+Y1*X1;
				//Por las dudas sea 0
				if ((X2-X1)!=0) {
					test = test /(X2-X1);
				}
				test = test +Y1;
				
				if(test==0) {
					//El punto (0,0) pertence a la recta, por lo tanto esta alineado con el sol
					this.alineacion_sol = 1;
				}else {
					//El punto (0,0) no pertence a la recta, por lo tanto esta no alineado con el sol
					this.alineacion_sol = 0;
				}
			}
		}else {
			//No esta alineado porque los puntos forman un triangulo
			this.alineacion_sol = 0;
		}
	
	}
	
	private void checkTriangulacionSol(Posicion pos1,Posicion pos2,Posicion pos3) {
		//Si no hay triangulo me ahorro los calculos
		if(this.area_triangulo==0) {
			this.sol_triangulado = 0;
		}else {
			/*Para saber si el sol (0,0) pertence o no al triangulo encerrado por los puntos, 
			 *el origen de coordenadas debe pertencer a las 3 inecuaciones formadas por las 3 
			 *rectas de los puntos
			*/
			boolean ok = false;
			//Recta 1-2
			if(checkInecuacionOrigen(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY(), pos3.getX(), pos3.getY())){
				if(checkInecuacionOrigen(pos1.getX(), pos1.getY(), pos3.getX(), pos3.getY(), pos2.getX(), pos2.getY())){
					if(checkInecuacionOrigen(pos2.getX(), pos2.getY(), pos3.getX(), pos3.getY(), pos1.getX(), pos1.getY())){
						ok = true;
					}
				}
			}
			
			if(ok) {
				this.sol_triangulado = 1;
			}else {
				this.sol_triangulado = 0;
			}
		}
	}
	
	private boolean checkInecuacionOrigen(float X1, float Y1, float X2, float Y2, float X3, float Y3) {
		//Determino el sentido de la inecuacion
		boolean menor = false;
		float test = (Y2-Y1);
		test = test / (X2-X1);
		test = test * (X3-X1);
		test = test + Y1;
		if (Y3 <= test) {
			//el sentido es <=
			menor = true;
		}
		//Evaluo el (0,0)
		test = 0;
		test = (Y2-Y1);
		test = test / (X2-X1);
		test = test * ((-1)*X1);
		test = test + Y1;
		//Si el sentido quedo menor, 0 tiene que ser menor que test
		if(menor) {
			return 0<=test;
		}else {
			return 0>=test;
		}
	}
	
	public static void definirPicosLluvia(List<Clima> climaList) {
		float max=0;
		Clima maxLLuvia = null;

		for(Clima c: climaList) {
			if(c.getPronostico().equals("Lluvia")) {
				
				if(max<c.getArea_triangulo()) {
					max = c.getArea_triangulo();
					maxLLuvia = c;
				}
			
			}else if (max!=0){ //Si se termina la temporada de lluvias seteo el maximo
				maxLLuvia.setPico((byte)1);
				max = 0;
				maxLLuvia = null;
			}
		}
		
	}
}
