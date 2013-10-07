package de.unioninvestment.eai.portal.support.vaadin.context;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 * Delegate-Interface für {@link Context}.
 * 
 * @author carsten.mjartan
 */
public interface ContextProvider {

	/**
	 * @return die aktuelle User-Locale
	 */
	Locale getLocale();
	
	/**
	 * Übersetzung von UI-Texten.
	 * 
	 * @param key
	 *            der Übersetzungsschlüssel
	 * @param args
	 *            Parameter
	 * @return die Übersetzung gemäß der aktuellen Locale-Konfiguration
	 */
	String getMessage(String key, Object... args);

	/**
	 * @param requiredType
	 *            der benötigte Typ eines Beans
	 * @return eine Instanz des Typs aus dem Spring ApplicationContext
	 * @throws NoSuchBeanDefinitionException
	 *             falls die Bean im Spring-Kontext nicht gefunden werden kann
	 * @throws BeansException
	 *             falls keine eindeutige Zuordnung getroffen werden kann
	 */
	<T> T getBean(Class<T> requiredType);

	/**
	 * @return {@link IllegalStateException}, falls der Context nicht ermittelt werden kann
	 */
	ApplicationContext getSpringContext();

}