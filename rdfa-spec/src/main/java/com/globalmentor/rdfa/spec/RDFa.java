/*
 * Copyright © 2019 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.rdfa.spec;

import static java.util.Collections.*;

import java.net.URI;
import java.util.*;

/**
 * Definition of the Resource Description Framework through Attributes (RDFa).
 * @author Garret Wilson
 * @see <a href="https://www.w3.org/TR/rdfa-core/">RDFa Core 1.1</a>
 */
public class RDFa {

	/**
	 * Definitions predefined for RDFa Core 1.1, so that "… RDFa users can use these … without having the obligation of defining [them] …".
	 * @apiNote These same definitions are available in <a href="https://www.w3.org/2013/json-ld-context/rdfa11">JSON format for JSON-LD</a>.
	 * @see <a href="https://www.w3.org/2011/rdfa-context/rdfa-1.1">RDFa Core Initial Context</a>
	 */
	public static final class InitialContext {

		/** Vocabulary prefixes predefined for RDFa Core 1.1. */
		public static final Map<String, URI> VOCABULARY_PREFIXES;

		/** Vocabulary terms predefined for RDFa Core 1.1. */
		public static final Map<String, URI> VOCABULARY_TERMS;

		static {
			final Map<String, URI> vocabularyPrefixes = new HashMap<>();
			vocabularyPrefixes.put("as", URI.create("https://www.w3.org/ns/activitystreams#"));
			vocabularyPrefixes.put("csvw", URI.create("http://www.w3.org/ns/csvw#"));
			vocabularyPrefixes.put("cat", URI.create("http://www.w3.org/ns/dcat#"));
			vocabularyPrefixes.put("cc", URI.create("http://creativecommons.org/ns#"));
			vocabularyPrefixes.put("cnt", URI.create("http://www.w3.org/2008/content#"));
			vocabularyPrefixes.put("ctag", URI.create("http://commontag.org/ns#"));
			vocabularyPrefixes.put("dc", URI.create("http://purl.org/dc/terms/"));
			vocabularyPrefixes.put("dc11", URI.create("http://purl.org/dc/elements/1.1/"));
			vocabularyPrefixes.put("dcat", URI.create("http://www.w3.org/ns/dcat#"));
			vocabularyPrefixes.put("dcterms", URI.create("http://purl.org/dc/terms/"));
			vocabularyPrefixes.put("dqv", URI.create("http://www.w3.org/ns/dqv#"));
			vocabularyPrefixes.put("duv", URI.create("https://www.w3.org/TR/vocab-duv#"));
			vocabularyPrefixes.put("earl", URI.create("http://www.w3.org/ns/earl#"));
			vocabularyPrefixes.put("foaf", URI.create("http://xmlns.com/foaf/0.1/"));
			vocabularyPrefixes.put("gldp", URI.create("http://www.w3.org/ns/people#"));
			vocabularyPrefixes.put("gr", URI.create("http://purl.org/goodrelations/v1#"));
			vocabularyPrefixes.put("grddl", URI.create("http://www.w3.org/2003/g/data-view#"));
			vocabularyPrefixes.put("ht", URI.create("http://www.w3.org/2006/http#"));
			vocabularyPrefixes.put("ical", URI.create("http://www.w3.org/2002/12/cal/icaltzd#"));
			vocabularyPrefixes.put("ldp", URI.create("http://www.w3.org/ns/ldp#"));
			vocabularyPrefixes.put("ma", URI.create("http://www.w3.org/ns/ma-ont#"));
			vocabularyPrefixes.put("oa", URI.create("http://www.w3.org/ns/oa#"));
			vocabularyPrefixes.put("odrl", URI.create("http://www.w3.org/ns/odrl/2/"));
			vocabularyPrefixes.put("og", URI.create("http://ogp.me/ns#"));
			vocabularyPrefixes.put("org", URI.create("http://www.w3.org/ns/org#"));
			vocabularyPrefixes.put("owl", URI.create("http://www.w3.org/2002/07/owl#"));
			vocabularyPrefixes.put("prov", URI.create("http://www.w3.org/ns/prov#"));
			vocabularyPrefixes.put("ptr", URI.create("http://www.w3.org/2009/pointers#"));
			vocabularyPrefixes.put("qb", URI.create("http://purl.org/linked-data/cube#"));
			vocabularyPrefixes.put("rev", URI.create("http://purl.org/stuff/rev#"));
			vocabularyPrefixes.put("rdf", URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
			vocabularyPrefixes.put("rdfa", URI.create("http://www.w3.org/ns/rdfa#"));
			vocabularyPrefixes.put("rdfs", URI.create("http://www.w3.org/2000/01/rdf-schema#"));
			vocabularyPrefixes.put("rif", URI.create("http://www.w3.org/2007/rif#"));
			vocabularyPrefixes.put("rr", URI.create("http://www.w3.org/ns/r2rml#"));
			vocabularyPrefixes.put("schema", URI.create("http://schema.org/"));
			vocabularyPrefixes.put("sd", URI.create("http://www.w3.org/ns/sparql-service-description#"));
			vocabularyPrefixes.put("sioc", URI.create("http://rdfs.org/sioc/ns#"));
			vocabularyPrefixes.put("skos", URI.create("http://www.w3.org/2004/02/skos/core#"));
			vocabularyPrefixes.put("skosxl", URI.create("http://www.w3.org/2008/05/skos-xl#"));
			vocabularyPrefixes.put("ssn", URI.create("http://www.w3.org/ns/ssn/"));
			vocabularyPrefixes.put("sosa", URI.create("http://www.w3.org/ns/sosa/"));
			vocabularyPrefixes.put("time", URI.create("http://www.w3.org/2006/time#"));
			vocabularyPrefixes.put("v", URI.create("http://rdf.data-vocabulary.org/#"));
			vocabularyPrefixes.put("vcard", URI.create("http://www.w3.org/2006/vcard/ns#"));
			vocabularyPrefixes.put("void", URI.create("http://rdfs.org/ns/void#"));
			vocabularyPrefixes.put("wdr", URI.create("http://www.w3.org/2007/05/powder#"));
			vocabularyPrefixes.put("wdrs", URI.create("http://www.w3.org/2007/05/powder-s#"));
			vocabularyPrefixes.put("xhv", URI.create("http://www.w3.org/1999/xhtml/vocab#"));
			vocabularyPrefixes.put("xml", URI.create("http://www.w3.org/XML/1998/namespace"));
			vocabularyPrefixes.put("xsd", URI.create("http://www.w3.org/2001/XMLSchema#"));
			VOCABULARY_PREFIXES = unmodifiableMap(vocabularyPrefixes);
			final Map<String, URI> vocabularyTerms = new HashMap<>();
			vocabularyTerms.put("describedby", URI.create("http://www.w3.org/2007/05/powder-s#describedby"));
			vocabularyTerms.put("license", URI.create("http://www.w3.org/1999/xhtml/vocab#license"));
			vocabularyTerms.put("role", URI.create("http://www.w3.org/1999/xhtml/vocab#role"));
			VOCABULARY_TERMS = unmodifiableMap(vocabularyTerms);
		}

	}

}
