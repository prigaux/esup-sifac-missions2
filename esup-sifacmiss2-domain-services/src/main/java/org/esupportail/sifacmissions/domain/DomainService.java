/**
 * ESUP-Portail Blank Application - Copyright (c) 2010 ESUP-Portail consortium.
 */
package org.esupportail.sifacmissions.domain;

import java.util.List;

import org.esupportail.sifacmissions.domain.beans.Mission;
import org.esupportail.sifacmissions.domain.beans.MissionDetails;
import org.esupportail.sifacmissions.domain.beans.User;
import org.esupportail.sifacmissions.services.sifac.SifacException;

/**
 * Service permettant de manipuler les modèles métiers de l'application.
 * 
 * @author Yves Deschamps (Universite de Lille 1)
 * @author Florent Cailhol (Anyware Services)
 */
public interface DomainService {

	/**
	 * Récupère un utilisateur à partir de son identifiant.
	 * 
	 * @param uid Identifiant
	 * @return Utilisateur
	 */
	User getUser(String uid);

	/**
	 * Récupère l'année de mise en ligne de l'application SIFAC.
	 * 
	 * @return Année
	 */
	Integer getFirstYear();

	/**
	 * Récupère le nom d'un utilisateur à partir de son identifiant.
	 * 
	 * @param uid Identifiant
	 * @return Nom de l'utilisateur (sans accent)
	 */
	String getNom(String uid);

	/**
	 * Récupère le prénom d'un utilisateur à partir de son identifiant.
	 * 
	 * @param uid Identifiant
	 * @return Prénom de l'utilisateur (sans accent)
	 */
	String getPrenom(String uid);

	/**
	 * Récupère la liste des missions pour un utilisateur et une année donnés.
	 * Le nom et prénom sont ceux de l'utilisateur et doivent être sans accent.
	 * 
	 * @param matricule Numéro de matricule
	 * @param nom Nom
	 * @param prenom Prénom
	 * @param year Année
	 * @return Liste des frais de mission
	 * @throws SifacException
	 */
	List<Mission> getFraisMissions(String matricule, String nom, String prenom, Integer year) throws SifacException;

	/**
	 * Récupère la liste des frais pour l'utilisateur et la mission donnés.
	 * 
	 * @param matricule Numéro de matricule
	 * @param mission Numéro de mission
	 * @return Liste de détails
	 * @throws SifacException
	 */
	List<MissionDetails> getMissionDetails(String matricule, String mission) throws SifacException;

	/**
	 * Détermine si un utilisateur possède un homonyne.
	 * 
	 * @param user Utilisateur
	 * @return true si un utilisateur avec le nom nom est trouvé
	 */
	Boolean isHomonyme(User user);

	/**
	 * Retourne le service de récupération des matricules.
	 * 
	 * @return Service de récupération des matricules
	 */
	MatriculeService getMatriculeService();

}
