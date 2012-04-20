/**
 * ESUP-Portail esup-sifac-missions - Copyright (c) 2009 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-sifacmissions
 */
package org.esupportail.sifacmissions.services.sifac;

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.Assert;
import org.esupportail.sifacmissions.domain.beans.Mission;
import org.esupportail.sifacmissions.domain.beans.MissionDetails;
import org.esupportail.sifacmissions.ws.SifacPortailService;
import org.springframework.beans.factory.InitializingBean;

/**
 * Web service d'accès aux données de l'application SIFAC.
 */
public class SifacServiceImpl implements SifacService, InitializingBean {

	private final Logger logger = new LoggerImpl(getClass());

	private String mandant;
	private Integer firstYear;
	private SifacPortailService portailService;
	private CacheManager cacheManager;
	private String cacheName;
	private Cache cache;
	
	/**
	 * @param mandant Numéro de mandat de l'application SIFAC
	 */
	public void setMandant(String mandant) {
		this.mandant = mandant;
	}

	/**
	 * @param firstYear Première année de fonctionnement  de l'application SIFAC
	 */
	public void setFirstYear(Integer firstYear) {
		this.firstYear = firstYear;
	}

	/**
	 * @param service Client permettant de requêter le web service PORTAIL
	 */
	public void setPortailService(SifacPortailService service) {
		this.portailService = service;
	}

	/**
	 * @param cacheManager Gestionnaire de cache
	 */
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/**
	 * @param cacheName Nom interne du cache à utiliser
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(mandant, "property mandant of class " + getClass().getName() + " can not be null");
		Assert.notNull(firstYear, "property firstYear of class " + getClass().getName() + " can not be null");
		Assert.notNull(portailService, "property portailService of class " + getClass().getName() + " can not be null");
		Assert.notNull(cacheManager, "property cacheManager of class " + getClass().getName() + " can not be null");
		Assert.notNull(cacheName, "property cacheName of class " + getClass().getName() + " can not be null");

		if (!cacheManager.cacheExists(cacheName)) {
			cacheManager.addCache(cacheName);
		}

		cache = cacheManager.getCache(cacheName);
	}
	
	@Override
	public Integer getFirstYear() {
		return firstYear;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Mission> getFraisMissions(String matricule, String nom, String prenom, Integer year) throws SifacException {
		if (nom == null) {
			nom = "";
		}
		
		if (prenom == null) {
			prenom = "";
		}

		List<Mission> fms = null;
		String cacheKey = matricule + "|" + nom + "|" + prenom + "|" + year + "|" + mandant;

		if (cache.get(cacheKey) == null) {
			try {
				fms = portailService.getFraisMissions(matricule, nom, prenom, year.toString());
			}
			catch (Exception e) {
				throw new SifacException(e);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Frais de missions " + cacheKey + " not found in cache");
			}

			cache.put(new Element(cacheKey, fms));
			return fms;
		}
		else {
			fms = (List<Mission>) cache.get(cacheKey).getObjectValue();

			if (logger.isDebugEnabled()) {
				logger.debug("Frais de missions " + cacheKey + " found in cache");
			}
		}

		return fms;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MissionDetails> getMissionDetails(String matricule, String numeroMission) throws SifacException {
		List<MissionDetails> details = null;
		String cacheKey = matricule + "|" + numeroMission + "|" + mandant;

		if (cache.get(cacheKey) == null) {
			try {
				details = portailService.getMissionDetails(matricule, numeroMission);
			}
			catch (Exception e) {
				throw new SifacException(e);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Mission details " + cacheKey + " not found in cache");
			}

			cache.put(new Element(cacheKey, details));
			return details;
		}
		else {
			details = (List<MissionDetails>) cache.get(cacheKey).getObjectValue();

			if (logger.isDebugEnabled()) {
				logger.debug("Mission details " + cacheKey + " found in cache");
			}
		}

		return details;
	}

}
