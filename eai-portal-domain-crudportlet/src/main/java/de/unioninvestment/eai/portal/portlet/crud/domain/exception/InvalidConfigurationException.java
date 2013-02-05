package de.unioninvestment.eai.portal.portlet.crud.domain.exception;

public class InvalidConfigurationException extends BusinessException {

	public InvalidConfigurationException(String code, Object... args) {
		super(code, args);
	}

	public InvalidConfigurationException(String code) {
		super(code);
	}

}
