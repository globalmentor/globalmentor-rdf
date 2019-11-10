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

import static java.util.function.Function.*;

import java.net.URI;
import java.util.*;
import java.util.stream.*;

import com.globalmentor.vocab.*;

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

		/** Vocabularies predefined for RDFa Core 1.1. */
		public static final VocabularyRegistry VOCABULARIES;

		/** Vocabulary terms predefined for RDFa Core 1.1, mapped to their names. */
		public static final Map<String, VocabularyTerm> VOCABULARY_TERMS;

		static {
			final URI wdrsNamespace = URI.create("http://www.w3.org/2007/05/powder-s#");
			final URI xhvNamespace = URI.create("http://www.w3.org/1999/xhtml/vocab#");

			VOCABULARIES = VocabularyRegistry.builder().registerPrefix("as", URI.create("https://www.w3.org/ns/activitystreams#"))
					.registerPrefix("csvw", URI.create("http://www.w3.org/ns/csvw#")).registerPrefix("cat", URI.create("http://www.w3.org/ns/dcat#"))
					.registerPrefix("cc", URI.create("http://creativecommons.org/ns#")).registerPrefix("cnt", URI.create("http://www.w3.org/2008/content#"))
					.registerPrefix("ctag", URI.create("http://commontag.org/ns#")).registerPrefix("dc", URI.create("http://purl.org/dc/terms/"))
					.registerPrefix("dc11", URI.create("http://purl.org/dc/elements/1.1/")).registerPrefix("dcat", URI.create("http://www.w3.org/ns/dcat#"))
					.registerPrefix("dcterms", URI.create("http://purl.org/dc/terms/")).registerPrefix("dqv", URI.create("http://www.w3.org/ns/dqv#"))
					.registerPrefix("duv", URI.create("https://www.w3.org/TR/vocab-duv#")).registerPrefix("earl", URI.create("http://www.w3.org/ns/earl#"))
					.registerPrefix("foaf", URI.create("http://xmlns.com/foaf/0.1/")).registerPrefix("gldp", URI.create("http://www.w3.org/ns/people#"))
					.registerPrefix("gr", URI.create("http://purl.org/goodrelations/v1#")).registerPrefix("grddl", URI.create("http://www.w3.org/2003/g/data-view#"))
					.registerPrefix("ht", URI.create("http://www.w3.org/2006/http#")).registerPrefix("ical", URI.create("http://www.w3.org/2002/12/cal/icaltzd#"))
					.registerPrefix("ldp", URI.create("http://www.w3.org/ns/ldp#")).registerPrefix("ma", URI.create("http://www.w3.org/ns/ma-ont#"))
					.registerPrefix("oa", URI.create("http://www.w3.org/ns/oa#")).registerPrefix("odrl", URI.create("http://www.w3.org/ns/odrl/2/"))
					.registerPrefix("og", URI.create("http://ogp.me/ns#")).registerPrefix("org", URI.create("http://www.w3.org/ns/org#"))
					.registerPrefix("owl", URI.create("http://www.w3.org/2002/07/owl#")).registerPrefix("prov", URI.create("http://www.w3.org/ns/prov#"))
					.registerPrefix("ptr", URI.create("http://www.w3.org/2009/pointers#")).registerPrefix("qb", URI.create("http://purl.org/linked-data/cube#"))
					.registerPrefix("rev", URI.create("http://purl.org/stuff/rev#")).registerPrefix("rdf", URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#"))
					.registerPrefix("rdfa", URI.create("http://www.w3.org/ns/rdfa#")).registerPrefix("rdfs", URI.create("http://www.w3.org/2000/01/rdf-schema#"))
					.registerPrefix("rif", URI.create("http://www.w3.org/2007/rif#")).registerPrefix("rr", URI.create("http://www.w3.org/ns/r2rml#"))
					.registerPrefix("schema", URI.create("http://schema.org/")).registerPrefix("sd", URI.create("http://www.w3.org/ns/sparql-service-description#"))
					.registerPrefix("sioc", URI.create("http://rdfs.org/sioc/ns#")).registerPrefix("skos", URI.create("http://www.w3.org/2004/02/skos/core#"))
					.registerPrefix("skosxl", URI.create("http://www.w3.org/2008/05/skos-xl#")).registerPrefix("ssn", URI.create("http://www.w3.org/ns/ssn/"))
					.registerPrefix("sosa", URI.create("http://www.w3.org/ns/sosa/")).registerPrefix("time", URI.create("http://www.w3.org/2006/time#"))
					.registerPrefix("v", URI.create("http://rdf.data-vocabulary.org/#")).registerPrefix("vcard", URI.create("http://www.w3.org/2006/vcard/ns#"))
					.registerPrefix("void", URI.create("http://rdfs.org/ns/void#")).registerPrefix("wdr", URI.create("http://www.w3.org/2007/05/powder#"))
					.registerPrefix("wdrs", wdrsNamespace).registerPrefix("xhv", xhvNamespace).registerPrefix("xml", URI.create("http://www.w3.org/XML/1998/namespace"))
					.registerPrefix("xsd", URI.create("http://www.w3.org/2001/XMLSchema#")).build();

			VOCABULARY_TERMS = Stream
					.of(VocabularyTerm.of(wdrsNamespace, "describedby"), VocabularyTerm.of(xhvNamespace, "license"), VocabularyTerm.of(xhvNamespace, "role"))
					.collect(Collectors.toMap(VocabularyTerm::getName, identity()));
		}

	}

}
