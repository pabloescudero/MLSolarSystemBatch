package com.solarSystem.SolarSystemBatch.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.solarSystem.SolarSystemBatch.model.Posicion;

@Transactional
public class PosicionDAO {
	
	private EntityManager em;
	
	public PosicionDAO(EntityManager em) {
		this.em = em;
	}
	
	public void deletePosiciones(){
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM Posicion").executeUpdate();
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void savePosiciones(List<Posicion> posicionList) {
		try {
		em.getTransaction().begin();
		for(Posicion pos: posicionList) {
			em.persist(pos);
		}
		em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
