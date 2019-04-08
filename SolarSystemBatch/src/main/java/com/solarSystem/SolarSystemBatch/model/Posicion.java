package com.solarSystem.SolarSystemBatch.model;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "posicion")
public class Posicion {
	
	@EmbeddedId
	private PosicionId id; 
	

	private float x;
	private float y;
	

	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	
	public PosicionId getId() {
		return id;
	}
	public void setId(PosicionId id) {
		this.id = id;
	}
	
	public String toString() {
		return "Id posicion (planeta,dia)=("+id.getIdplaneta()+","+id.getDia_nro()+") X="+this.x+" Y="+this.y;
	}

}
