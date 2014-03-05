package de.unioninvestment.eai.portal.portlet.crud.domain.search;

import java.io.Reader;

import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

/**
 * Tokenizer that takes all terms as they are entered.
 * 
 * @author cmj
 * 
 */
public final class AsIsTokenizer extends CharTokenizer {

	public AsIsTokenizer(Version matchVersion, Reader input) {
		super(matchVersion, input);
	}

	@Override
	protected boolean isTokenChar(int c) {
		return true;
	}
}
