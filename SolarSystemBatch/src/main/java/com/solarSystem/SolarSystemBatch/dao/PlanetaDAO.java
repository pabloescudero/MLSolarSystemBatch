package com.solarSystem.SolarSystemBatch.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.solarSystem.SolarSystemBatch.model.Planeta;

public class PlanetaDAO {
	
	private EntityManager em;
	
	public PlanetaDAO(EntityManager em) {
		this.em = em;
	}
	
	@SuppressWarnings("unchecked")
	public List<Planeta> getPlanetas(){
		Query query = em.createQuery("from Planeta", Planeta.class);
		List<Planeta> planetas = query.getResultList();
		return planetas;
	}
}
