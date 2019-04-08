package com.solarSystem.SolarSystemBatch.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PosicionId implements Serializable {
 
	private static final long serialVersionUID = 1L;

	@Column(name = "dia_nro")
    private Integer dia_nro;
 
    @Column(name = "idplaneta")
    private Integer idplaneta;
    
    public PosicionId(Integer dia_nro, Integer idplaneta) {
        this.dia_nro = dia_nro;
        this.idplaneta = idplaneta;
    }
 
    public PosicionId() {
    }
 
    public Integer getDia_nro() {
		return dia_nro;
	}

	public void setDia_nro(Integer dia_nro) {
		this.dia_nro = dia_nro;
	}

	public Integer getIdplaneta() {
		return idplaneta;
	}

	public void setIdplaneta(Integer idplaneta) {
		this.idplaneta = idplaneta;
	}    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PosicionId)) return false;
        PosicionId that = (PosicionId) o;
        return Objects.equals(getDia_nro(), that.getDia_nro()) &&
                Objects.equals(getIdplaneta(), that.getIdplaneta());
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(getDia_nro(), getIdplaneta());
    }
}