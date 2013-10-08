package de.unioninvestment.eai.portal.support.vaadin.security;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class RichTextAreaSanitizer {

	public static final String NAMESPACE = "http://www.w3.org/1999/xhtml";

	private static PolicyFactory policy;

	static {
		policy = new HtmlPolicyBuilder() //
				.allowCommonBlockElements() //
				.allowCommonInlineFormattingElements() //
				.allowStyling() //
				
				.allowElements("hr") //
				
				.allowStandardUrlProtocols() //
				.requireRelNofollowOnLinks() //
				
				.allowElements("a") //
				.allowAttributes("href").onElements("a") //

				.allowElements("img") //
				.allowAttributes("src").onElements("img") //
				
				.allowAttributes("align").onElements("div") //

				.allowElements("font") //
				.allowAttributes("color", "face", "size").onElements("font") //
				
				.toFactory();
	}

	public static String sanitize(String html) {
		return policy.sanitize(html);
	}

}
