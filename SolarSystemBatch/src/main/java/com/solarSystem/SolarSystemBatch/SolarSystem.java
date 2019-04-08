package com.solarSystem.SolarSystemBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import com.solarSystem.SolarSystemBatch.dao.ClimaDAO;
import com.solarSystem.SolarSystemBatch.dao.PersistenceManager;
import com.solarSystem.SolarSystemBatch.dao.PlanetaDAO;
import com.solarSystem.SolarSystemBatch.dao.PosicionDAO;
import com.solarSystem.SolarSystemBatch.model.Clima;
import com.solarSystem.SolarSystemBatch.model.Planeta;
import com.solarSystem.SolarSystemBatch.model.Posicion;
import com.solarSystem.SolarSystemBatch.model.PosicionId;


public class SolarSystem {
	private EntityManager em;
	private PlanetaDAO planetaDao;
	private PosicionDAO posicionDao;
	private ClimaDAO climaDao;
	private List<Planeta> planetas;
	
	private Map<PosicionId,Posicion> posicionMap; 
	private List<Date> diasList;
	private Integer dias;
	private Integer anios;
	private Boolean started;
	
	//El constructor solo determina la inicializacion
	public SolarSystem() {
		this.started = false;
	}
	
	public void run(int anios) {
		this.anios = anios;
		//Inicializo el sistema con los años a predecir
    	initialize(anios); 
    	
    	//Calculo el avance de los planetas respecto a los dias
    	updatePosiciones();
    	
    	//Calculo el clima para los dias generados 
    	calcularClima();
    	
    	//Finalizo la transaccion
    	end();
	}
	
	//Inicializa la conexion a la base, carga los planetas y la cantidad de dias que se van a tener que predecir
	private void initialize(int anios) {
		try {
			em = PersistenceManager.INSTANCE.getEntityManager();
			this.planetaDao = new PlanetaDAO(em);
			this.climaDao = new ClimaDAO(em);
			this.posicionDao = new PosicionDAO(em);
			
			planetas = planetaDao.getPlanetas();
			
			if(planetas.size()!=3) {
				throw new RuntimeException("La cantidad de planetas deben ser si o si  3, se detectaron "+planetas.size());
			}
			
			//Comienzo de 0
			posicionDao.deletePosiciones();
			climaDao.deleteClimas();
 
			calcularDias(anios);
			this.started = true;
			
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
    private void calcularDias( int anios) {
    	this.diasList = new ArrayList<Date>();
    	
    	Calendar calNow = Calendar.getInstance();
    	Calendar calThen = Calendar.getInstance();
    	Date now = new Date();
    	
    	calNow.setTime(now);
    	calThen.setTime(now);
    	calThen.add(Calendar.YEAR, anios);
    	Date then = calThen.getTime();
    	long diff = then.getTime() - now.getTime();
    	this.dias = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    	
    	//Cargo las fechas para hacerlo mas legible
    	for (int i=0;i<this.dias;i++) {
    		calNow.add(Calendar.DATE, 1);
    		Date dia = calNow.getTime();
    		diasList.add(dia);
    	}
    	
    }
    
    private void updatePosiciones() {
    	List<Posicion> posicionList = new ArrayList<Posicion>();
    	this.posicionMap = new HashMap<PosicionId,Posicion>();
    	
    	//Se calcula las posiciones para cada planeta
        for(Planeta p:planetas) {
        	//Dia por dia se calcula la posicion
        	for(int dia=1; dia<=this.dias;dia++) {
        		Posicion pos = new Posicion();
        		PosicionId id = new PosicionId(dia,p.getIdplaneta());
        		//Genero el ID
        		pos.setId(id);
        		//Calculo la posicion para el dia en X
        		pos.setX(p.getCoordenadaX(dia));
        		//Calculo la posicion para el dia en Y        		
        		pos.setY(p.getCoordenadaY(dia));
        		//Agrego la posicion a la lista de posiciones
        		posicionList.add(pos);
        		
        		//Genero un mapa para poder acceder directamente a las posiciones en los dias indicados
        		this.posicionMap.put(id, pos);
        	}       	
        }
        
        //Guardo las posiciones calculadas
        posicionDao.savePosiciones(posicionList);
    }
    
    private void calcularClima() {
    	List<Clima> climaList = new ArrayList<Clima>();
    	List<Posicion> posList = new ArrayList<Posicion>();
    	
    	int iddia=1;
    	
    	//Se insertaron ordenados se iteran ordenados
    	for(Date fecha:diasList) {
    		Clima clima = new Clima();
    		clima.setFecha(fecha);
    		clima.setIdia(iddia);
    		
    		posList.clear();
    		for(Planeta p:planetas) {
    			//Genero la key de busqueda
    			PosicionId id = new PosicionId(iddia,p.getIdplaneta());
    			//Agrego la posicion guardada en el map
    			posList.add(posicionMap.get(id));
    		}
    		
    		//Esto es asqueroso, pero me asegure que antes de llegar aca solo existan 3 planetas
    		clima.process(posList.get(0),posList.get(1),posList.get(2));
    		
    		climaList.add(clima);
    		iddia++;
    	}
    	
    	Clima.definirPicosLluvia(climaList);
    	
    	//Guardo los pronosticos
    	climaDao.saveClimas(climaList);
    	
    	imprimirInforme(climaList);
    	
    }
    
    private void imprimirInforme(List<Clima> climaList) {
    	SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    	List<String> sequia = new ArrayList<String>();
    	List<String> lluvia = new ArrayList<String>();
    	List<String> optimas = new ArrayList<String>();
    	
    	for(Clima c: climaList) {
    		if(c.getPronostico().equals("Sequia")) {
    			sequia.add("Periodo de Sequia :"+format.format(c.getFecha()));
    		}else if(c.getPico() == 1) {
    			lluvia.add("Periodo de lluvia con pico:"+format.format(c.getFecha()));
    		}else if(c.getPronostico().equals("Presion Optima")) {
    			optimas.add("Periodo de condiciones optimas");
    		}
    	}
    	
    	System.out.println("-----------------------------------------");
    	System.out.println("Se realizo la prediccion de "+dias+" dias correspondientes a "+this.anios+" años");
    	System.out.println("Se tendran "+sequia.size()+" periodos de sequia");
    	System.out.println("Se tendran "+lluvia.size()+" periodos de lluvia");
    	for(String print:lluvia) {
    		System.out.println(print);
    	}
    	System.out.println("Se tendran "+optimas.size()+" condiciones optimas");
    	System.out.println("-----------------------------------------");
    	
    }
    
    private void end() {
    	em.close();
    }
    
	public Integer getDias() {
		return dias;
	}

	public void setDias(Integer dias) {
		this.dias = dias;
	}

	public Boolean getStarted() {
		return started;
	}

	public void setStarted(Boolean started) {
		this.started = started;
	}
}

