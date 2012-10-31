package org.esupportail.sifacmissions.services.auth;

import org.esupportail.commons.services.authentication.AuthUtils;
import org.esupportail.commons.services.authentication.AuthenticationService;
import org.esupportail.commons.services.authentication.info.AuthInfo;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.Assert;
import org.esupportail.commons.utils.ContextUtils;
import org.esupportail.sifacmissions.domain.beans.User;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Yves Deschamps (Universite de Lille 1)
 */
public class AuthenticatorImpl implements Authenticator, InitializingBean {

	private static final String AUTH_INFO_ATTRIBUTE = AuthenticatorImpl.class.getName() + ".authInfo";
	private static final String USER_ATTRIBUTE = AuthenticatorImpl.class.getName() + ".user";

	private final Logger logger = new LoggerImpl(getClass());

	private AuthenticationService authenticationService;

	/**
	 * @param authenticationService Service d'authentification
	 */
	public void setAuthenticationService(final AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
		
		if (logger.isDebugEnabled()) {
			logger.debug("authenticationService: " + authenticationService.getClass().getName());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(authenticationService, "property authenticationService of class " + getClass().getName() + " can not be null");
	}

	@Override
	public User getUser() throws Exception {
		try {
			AuthInfo authInfo = (AuthInfo) ContextUtils.getSessionAttribute(AUTH_INFO_ATTRIBUTE);
			if (authInfo != null) {
				User user = (User) ContextUtils.getSessionAttribute(USER_ATTRIBUTE);
				if (logger.isDebugEnabled()) {
					logger.debug("found auth info in session: " + user);
				}

				return user;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("no auth info found in session");
			}

			authInfo = authenticationService.getAuthInfo();
			if (authInfo == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("authInfo is null");
				}

				return null;
			}

			if (AuthUtils.SHIBBOLETH.equals(authInfo.getType())) {
				if (logger.isDebugEnabled()) {
					logger.debug("Shibboleth authentication");
				}

				User user = new User();
				user.setLogin(authInfo.getId());

				storeToSession(authInfo, user);
				return user;
			}

			if (AuthUtils.CAS.equals(authInfo.getType())) {
				if (logger.isDebugEnabled()) {
					logger.debug("CAS authentication");
				}

				User user = new User();
				user.setLogin(authInfo.getId());

				storeToSession(authInfo, user);
				return user;
			}
		}
		catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

		return null;
	}

	private void storeToSession(final AuthInfo authInfo, final User user) {
		if (logger.isDebugEnabled()) {
			logger.debug("storing to session: " + authInfo);
		}

		ContextUtils.setSessionAttribute(AUTH_INFO_ATTRIBUTE, authInfo);
		ContextUtils.setSessionAttribute(USER_ATTRIBUTE, user);
	}

}