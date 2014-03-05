package de.unioninvestment.eai.portal.portlet.crud.domain.search;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;

/**
 * Analyser that handles terms 'as is'.
 * 
 * @author cmj
 */
public final class AsIsAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		return new TokenStreamComponents(new AsIsTokenizer(Version.LUCENE_46,
				reader));
	}
}
