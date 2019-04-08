package com.solarSystem.SolarSystemBatch.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.solarSystem.SolarSystemBatch.model.Clima;

@Transactional
public class ClimaDAO {
	private EntityManager em;
	
	public ClimaDAO(EntityManager em) {
		this.em = em;
	}

	public void deleteClimas(){
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM Clima").executeUpdate();
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveClimas(List<Clima> climaList) {
		try {
		em.getTransaction().begin();
		for(Clima clima: climaList) {
			em.persist(clima);
		}
		em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
