package com.solarSystem.SolarSystemBatch.model;



import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Planeta")
@Table(name = "planeta")
public class Planeta {
	
	@Id
	private Integer idplaneta;
	private String nombre;
  	private float velocidad_angular;
  	private float distancia_sol;
  	private String sentido;
	
	public Integer getIdplaneta() {
		return idplaneta;
	}
	public void setIdplaneta(Integer idplaneta) {
		this.idplaneta = idplaneta;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public float getVelocidad_angular() {
		return velocidad_angular;
	}
	public void setVelocidad_angular(float velocidad_angular) {
		this.velocidad_angular = velocidad_angular;
	}
	public float getDistancia_sol() {
		return distancia_sol;
	}
	public void setDistancia_sol(float distancia_sol) {
		this.distancia_sol = distancia_sol;
	}
	public String getSentido() {
		return sentido;
	}
	public void setSentido(String sentido) {
		this.sentido = sentido;
	}

	public String toString()
	{	String sen = "";
		if (this.sentido.equals("+") ){
			sen = "Horario";
		}else {
			sen = "AntiHorario";
		} 
		return "Planeta "+this.nombre+" ubicado a "+this.distancia_sol+" km del sol, se mueve a "+this.velocidad_angular+" grados por dia en sentido "+sen;
	}
	
	private float getGradosRecorridos(int dia) {
		float gradosRecorridos = 0;
		//Esto tiene que mantenerse en modulo 360!!
		if (this.sentido.equals("+") ){
			//Si los grados son mayores a 360, dio mas de una vuelta hay que normalizarlo.
			if((this.velocidad_angular * dia) > 360)
			{
				//Con el % busco el angulo neto de giro
				gradosRecorridos = 360 - ((this.velocidad_angular * dia) % 360);
			}else {
				gradosRecorridos = 360 - (this.velocidad_angular * dia);
			}
			
		}else {
			//El caso antihorario es mas simple, dado que mueve hacia donde crecen los angulos
			if((this.velocidad_angular * dia) > 360) {
				gradosRecorridos = (this.velocidad_angular * dia) % 360;
			}else {
				gradosRecorridos = this.velocidad_angular * dia;
			}
		}
		return gradosRecorridos;
	}
	
	public float getCoordenadaX(int dia) {
		float gradosRecorridos = getGradosRecorridos(dia);
		//Se agrega esta validacion para evitar errores de precision
		if(gradosRecorridos == 90 || gradosRecorridos == 270){
			return 0;
		}
		
		double b = Math.toRadians(gradosRecorridos);
		return (float) (this.distancia_sol * Math.cos(b));
	}
	
	public float getCoordenadaY(int dia) {
		float gradosRecorridos = getGradosRecorridos(dia);
		
		if(gradosRecorridos == 0 || gradosRecorridos == 180 || gradosRecorridos == 360){
			return 0;
		}
		
		double b = Math.toRadians(gradosRecorridos);
		return (float) (this.distancia_sol * Math.sin(b));
	}

}
