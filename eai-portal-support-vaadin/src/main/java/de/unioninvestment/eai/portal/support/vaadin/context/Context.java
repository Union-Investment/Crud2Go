package de.unioninvestment.eai.portal.support.vaadin.context;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Threadlocal Kontext für den Zugriff auf verschiedene Zentrale Funktionen der
 * UI.
 * 
 * @author carsten.mjartan
 */
public class Context {

	private static ContextProvider uiDelegateContextProvider = new UIContextProvider();
	
	private static ThreadLocal<ContextProvider> delegate = new ThreadLocal<ContextProvider>();
	

	private Context() {
		// utility class => hidden constructor
	}

	/**
	 * Diese Methode sollte nur durch {@link ContextUtil} oder
	 * {@link StagingUI} aufgerufen werden.
	 * 
	 * @param application
	 *            die im aktuellen Thread relevante {@link StagingUI}
	 */
	public static void setProvider(ContextProvider application) {
		delegate.set(application);
	}

	/**
	 * @return den Provider für Tests und andere Methoden
	 */
	static ContextProvider getProvider() {
		ContextProvider contextProvider = delegate.get();
		if (contextProvider == null) {
			return uiDelegateContextProvider;
		} else {
			return contextProvider;
		}
	}

	/**
	 * @return die aktuelle User-Locale
	 */
	public static Locale getLocale() {
		return getProvider().getLocale();
	}

	/**
	 * Übersetzung von UI-Texten.
	 * 
	 * @param key
	 *            der Übersetzungsschlüssel
	 * @param args
	 *            Parameter
	 * @return die Übersetzung gemäß der aktuellen Locale-Konfiguration
	 */
	public static String getMessage(String key, Object... args) {
		return getProvider().getMessage(key, args);
	}

	/**
	 * @param requiredType
	 *            der benötigte Typ eines Beans
	 * @return eine Instanz des Typs aus dem Spring ApplicationContext
	 * @throws NoSuchBeanDefinitionException
	 *             falls die Bean im Spring-Kontext nicht gefunden werden kann
	 * @throws BeansException
	 *             falls keine eindeutige Zuordnung getroffen werden kann
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return getProvider().getBean(requiredType);
	}

	
}
