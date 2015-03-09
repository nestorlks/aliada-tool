// ALIADA - Automatic publication under Linked Data paradigm
//          of library and museum data
//
// Component: aliada-rdfizer
// Responsible: ALIADA Consortiums
package eu.aliada.rdfizer;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import eu.aliada.rdfizer.datasource.Cache;
import eu.aliada.rdfizer.datasource.rdbms.JobInstance;
import eu.aliada.rdfizer.pipeline.format.xml.ImmutableNodeList;
import eu.aliada.rdfizer.pipeline.format.xml.XPath;
import eu.aliada.rdfizer.pipeline.nlp.NERService;
import eu.aliada.shared.ID;
import eu.aliada.shared.Strings;
import eu.aliada.shared.rdfstore.RDFStoreDAO;

/**
 * A generic tool used in templates to invoke some useful functions.
 * 
 * @author Andrea Gazzarini
 * @since 1.0
 */
@Component
public class Function {
	
	@Autowired
	private Cache cache;
	
	private RDFStoreDAO rdfStore = new RDFStoreDAO();
	
	@Autowired
	private XPath xpath;
	
	@Autowired
	private NERService ner;
	
	/**
	 * Returns a new generated UID.
	 * 
	 * @return a new generated UID.
	 */
	public long getId() {
		return ID.get();
	}
	
	public List<String> asList(final String [] array) {
		return array != null ? Arrays.asList(array) : null;
	}
	
	/**
	 * Normalizes and lowercases a given string.
	 * Diacritics are normalized and space are replaced with underscores.
	 * 
	 * @param value the string to be normalized.
	 * @return the normalized string.
	 */
	public String normalize(final String value) {
		return Strings.toURILocalName(value).toLowerCase();
	}
	
	/**
	 * Normalizes a given string.
	 * Diacritics are normalized and space are replaced with underscores.
	 * 
	 * @param value the string to be normalized.
	 * @return the normalized string.
	 */
	public String normalizeWithoutLowercase(final String value) {
		return Strings.toURILocalName(value);
	}	
	
	public String uuid(final String value) {
		return UUID.nameUUIDFromBytes(value.getBytes()).toString();
	}
	
	/**
	 * Normalizes a given string as {@link Function#normalize} but also removing all spaces and punctuation.
	 * 
	 * @param value the string to be normalized.
	 * @return the normalized string.
	 */
	public String normalizeStrong(final String value) {
		   return value == null ? null
			        : Normalizer.normalize(value, Form.NFD)
			            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
			            .replaceAll("[^A-Za-z0-9]", "");
	}		
	
	public String lidoClass(final String value) {
		try {
			final String midx = "/id/resource/";
			
			int indexOfIdResource = value.indexOf(midx);
			if (indexOfIdResource != -1) {
				int indexOfSlash = value.indexOf("/", (indexOfIdResource + midx.length() + 1));
				if (indexOfSlash != -1) {
					return value.substring((indexOfIdResource + midx.length()), indexOfSlash);
				}
			}
			return null;
		} catch (Exception exception) {
			return null;
		}
	}
	
	/**
	 * Returns the ALIADA event type class corresponding to the given CIDOC-CRM class.
	 * 
	 * @param from the CIDOC-CRM event type class.
	 * @return the ALIADA class that corresponds to the given input class.
	 */
	public String toAliadaEventTypeClass(final String from) {
		return cache.getAliadaEventTypeClassFrom(from);
	}
	
	/**
	 * Returns true if the given string is not null and not empty.
	 * 
	 * @param value the string to check.
	 * @return true if the given string is not null and not empty.
	 */
	public boolean isNotNullAnrdfStoredNotEmpty(final String value) {
		return Strings.isNotNullAndNotEmpty(value);
	}
	
	public String getOntologyTypeURI(final Integer id, final String term) {
		if (true) return "IMAGE";
		try {
			JobInstance instance = cache.getJobInstance(id);
			if (instance != null) {
				final String [] uris = rdfStore.getOntologyTypeURI(instance.getSparqlEndpointUrl(), instance.getSparqlUsername(), instance.getSparqlPassword(), term);
				if (uris != null && uris.length > 0) {
					return uris[0];
				}
			}
			return null;
		} catch(Exception exception){
			exception.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Extracts a set of named entities from the input data.
	 * The context object is supposed to be a MARC record, so the tag/code input values are trasformed in a MARCXML XPATH.  
	 * 
	 * @param tag the target tag.
	 * @param code the subfield code.
	 * @param record the MARC record.
	 * @return a map of named entities from the input data.
	 * @throws XPathExpressionException in case of XPATH failure.
	 */
	public Map<String, String> marcner(final String tag, final String code, final Object record) throws XPathExpressionException {
		final ImmutableNodeList list = xpath.dfs(tag, code, record);
		final StringBuilder builder = new StringBuilder();
		for (final Node node : list) {
			builder.append(node.getTextContent()).append("\n");
		}
		return ner.detectEntities(builder.toString());
	}
}
