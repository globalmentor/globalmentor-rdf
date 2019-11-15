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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.*;

import com.globalmentor.vocab.VocabularyRegistry;

/**
 * Tests of {@link RDFa}.
 * @author Garret Wilson
 */
public class RDFaTest {

	private static final URI DC_NAMESPACE = URI.create("http://purl.org/dc/terms/");
	private static final URI OG_NAMESPACE = URI.create("http://ogp.me/ns#");

	/** @see RDFa#toPrefixAttributeValue(VocabularyRegistry) */
	@Test
	public void testToPrefixAttributeValueEmpty() {
		assertThat(RDFa.toPrefixAttributeValue(VocabularyRegistry.EMPTY), is(""));
	}

	/** @see RDFa#toPrefixAttributeValue(VocabularyRegistry) */
	@Test
	public void testToPrefixAttributeValueOneRegistration() {
		final VocabularyRegistry registry = VocabularyRegistry.of(Map.entry("dc", DC_NAMESPACE));
		assertThat(RDFa.toPrefixAttributeValue(registry), is("dc: http://purl.org/dc/terms/"));
	}

	/** @see RDFa#toPrefixAttributeValue(VocabularyRegistry) */
	@Test
	public void testToPrefixAttributeValueTwoRegistrations() {
		final VocabularyRegistry registry = VocabularyRegistry.of(Map.entry("dc", DC_NAMESPACE), Map.entry("og", OG_NAMESPACE));
		assertThat(RDFa.toPrefixAttributeValue(registry),
				oneOf("dc: http://purl.org/dc/terms/ og: http://ogp.me/ns#", "og: http://ogp.me/ns# dc: http://purl.org/dc/terms/"));
	}

}
