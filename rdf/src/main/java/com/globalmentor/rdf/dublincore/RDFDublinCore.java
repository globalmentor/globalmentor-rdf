/*
 * Copyright © 1996-2008 GlobalMentor, Inc. <http://www.globalmentor.com/>
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

package com.globalmentor.rdf.dublincore;

import java.text.*;
import java.util.*;

import com.globalmentor.model.Locales;
import com.globalmentor.rdf.*;

import static com.globalmentor.rdf.RDFResources.*;
import static com.globalmentor.vocab.dcmi.DCMES.*;

import com.globalmentor.text.*;

/**
 * Utilities for working with Dublin Core stored in RDF.
 * @author Garret Wilson
 */
public class RDFDublinCore {

	/**
	 * Adds a <code>dc:creator</code> property with the given value to the resource.
	 * @param resource The resource to which the property should be added.
	 * @param value The property value to add.
	 * @return The added literal property value.
	 */
	public static RDFLiteral addCreator(final RDFResource resource, final String value) {
		return resource.addProperty(TERM_CREATOR.getNamespace(), TERM_CREATOR.getName(), value);
	}

	/**
	 * Adds a <code>dc:contributor</code> property with the given value to the resource.
	 * @param resource The resource to which the property should be added.
	 * @param value The property value to add.
	 * @return The added literal property value.
	 */
	public static RDFLiteral addContributor(final RDFResource resource, final String value) {
		return resource.addProperty(TERM_CONTRIBUTOR.getNamespace(), TERM_CONTRIBUTOR.getName(), value);
	}

	/**
	 * Adds a <code>dc:description</code> property with the given value to the resource.
	 * @param resource The resource to which the property should be added.
	 * @param value The property value to add.
	 * @return The added literal property value.
	 */
	public static RDFLiteral addDescription(final RDFResource resource, final String value) {
		return resource.addProperty(TERM_DESCRIPTION.getNamespace(), TERM_DESCRIPTION.getName(), value);
	}

	/**
	 * Adds a <code>dc:identifier</code> property with the given value to the resource.
	 * @param resource The resource to which the property should be added.
	 * @param value The property value to add.
	 * @return The added literal property value.
	 */
	public static RDFLiteral addIdentifier(final RDFResource resource, final String value) {
		return resource.addProperty(TERM_IDENTIFIER.getNamespace(), TERM_IDENTIFIER.getName(), value);
	}

	/**
	 * Adds a <code>dc:language</code> property with the given value to the resource.
	 * @param resource The resource to which the property should be added.
	 * @param locale The property value to add.
	 * @return The added literal property value.
	 */
	public static RDFLiteral addLanguage(final RDFResource resource, final Locale locale) {
		//add the literal language tag for this locale
		return resource.addProperty(TERM_LANGUAGE.getNamespace(), TERM_LANGUAGE.getName(), Locales.getLanguageTag(locale));
	}

	/**
	 * Adds a <code>dc:publisher</code> property with the given value to the resource.
	 * @param resource The resource to which the property should be added.
	 * @param value The property value to add.
	 * @return The added literal property value.
	 */
	public static RDFLiteral addPublisher(final RDFResource resource, final String value) {
		return resource.addProperty(TERM_PUBLISHER.getNamespace(), TERM_PUBLISHER.getName(), value);
	}

	/**
	 * Adds a <code>dc:rights</code> property with the given value to the resource.
	 * @param resource The resource to which the property should be added.
	 * @param value The property value to add.
	 * @return The added literal property value.
	 */
	public static RDFLiteral addRights(final RDFResource resource, final String value) {
		return resource.addProperty(TERM_RIGHTS.getNamespace(), TERM_RIGHTS.getName(), value);
	}

	/**
	 * Adds a <code>dc:source</code> property with the given value to the resource.
	 * @param resource The resource to which the property should be added.
	 * @param value The property value to add.
	 * @return The added literal property value.
	 */
	public static RDFLiteral addSource(final RDFResource resource, final String value) {
		return resource.addProperty(TERM_SOURCE.getNamespace(), TERM_SOURCE.getName(), value);
	}

	/**
	 * Adds a <code>dc:title</code> property with the given value to the resource.
	 * @param resource The resource to which the property should be added.
	 * @param value The property value to add.
	 * @return The added literal property value.
	 */
	public static RDFLiteral addTitle(final RDFResource resource, final String value) {
		return resource.addProperty(TERM_TITLE.getNamespace(), TERM_TITLE.getName(), value);
	}

	/**
	 * Returns the value of the first <code>dc:creator</code> property.
	 * @param resource The resource the property of which should be located.
	 * @return The value of the first <code>dc:creator</code> property, or <code>null</code> if no such property exists.
	 */
	public static RDFObject getCreator(final RDFResource resource) {
		return resource.getPropertyValue(TERM_CREATOR.getNamespace(), TERM_CREATOR.getName());
	}

	/**
	 * Returns the value of the first <code>dc:date</code> property parsed as a date, using the default W3C full date style.
	 * @param resource The resource the property of which should be located.
	 * @return The value of the first <code>dc:date</code> property, or <code>null</code> if no such property exists or it does not contain a date.
	 * @see W3CDateFormat.Style#DATE_TIME
	 */
	public static Date getDate(final RDFResource resource) {
		return RDFResources.getDate(resource, TERM_DATE.getNamespace(), TERM_DATE.getName());
	}

	/**
	 * Returns the value of the first <code>dc:date</code> property parsed as a date.
	 * @param resource The resource the property of which should be located.
	 * @param style The style of the date formatting.
	 * @return The value of the first <code>dc:date</code> property, or <code>null</code> if no such property exists or it does not contain a date.
	 */
	public static Date getDate(final RDFResource resource, final W3CDateFormat.Style style) {
		return RDFResources.getDate(resource, TERM_DATE.getNamespace(), TERM_DATE.getName(), style);
	}

	/**
	 * Returns the value of the first <code>dc:date</code> property parsed as a date.
	 * @param resource The resource the property of which should be located.
	 * @param dateFormat The object to parse the date.
	 * @return The value of the first <code>dc:date</code> property, or <code>null</code> if no such property exists or it does not contain a date.
	 */
	public static Date getDate(final RDFResource resource, final DateFormat dateFormat) {
		return RDFResources.getDate(resource, TERM_DATE.getNamespace(), TERM_DATE.getName(), dateFormat);
	}

	/**
	 * Returns the value of the first <code>dc:description</code> property.
	 * @param resource The resource the property of which should be located.
	 * @return The value of the first <code>dc:description</code> property, or <code>null</code> if no such property exists.
	 */
	public static RDFObject getDescription(final RDFResource resource) {
		return resource.getPropertyValue(TERM_DESCRIPTION.getNamespace(), TERM_DESCRIPTION.getName());
	}

	/**
	 * Returns the value of the first <code>dc:language</code> property.
	 * @param resource The resource the property of which should be located.
	 * @return The value of the first <code>dc:language</code> property, or <code>null</code> if no such property exists or the property value does not contain a
	 *         literal language tag.
	 */
	public static Locale getLanguage(final RDFResource resource) {
		final RDFObject languageObject = resource.getPropertyValue(TERM_LANGUAGE.getNamespace(), TERM_LANGUAGE.getName());
		if(languageObject instanceof RDFLiteral) { //if this is a literal value
			return Locales.createLocale(((RDFLiteral)languageObject).getLexicalForm()); //create a locale from the literal's lexical form
		} else { //if there is no literal language tag
			return null; //show that we can't create a language locale from a non-literal
		}
	}

	/**
	 * Returns the value of the first <code>dc:title</code> property as a literal.
	 * @param resource The resource the property of which should be located.
	 * @return The value of the first <code>dc:title</code> property, or <code>null</code> if no such property exists as a literal.
	 */
	public static RDFLiteral getTitle(final RDFResource resource) {
		return asLiteral(resource.getPropertyValue(TERM_TITLE.getNamespace(), TERM_TITLE.getName()));
	}

	/**
	 * Set the <code>dc:title</code> property of the resource.
	 * @param resource The resource of which the property should be set.
	 * @param value The property value to set.
	 * @return The set literal property value.
	 */
	public static RDFLiteral setTitle(final RDFResource resource, final String value) {
		return resource.setProperty(TERM_TITLE.getNamespace(), TERM_TITLE.getName(), value);
	}

	/**
	 * Returns the value of the first <code>dc:rights</code> property.
	 * @param resource The resource the property of which should be located.
	 * @return The value of the first <code>dc:rights</code> property, or <code>null</code> if no such property exists.
	 */
	public static RDFObject getRights(final RDFResource resource) {
		return resource.getPropertyValue(TERM_RIGHTS.getNamespace(), TERM_RIGHTS.getName());
	}

	/**
	 * Sets the <code>dc:date</code> property with the given value to the resource, using the default W3C full date style.
	 * @param resource The resource to which the property should be set.
	 * @param date The property value to set.
	 * @return The added literal property value.
	 * @see W3CDateFormat.Style#DATE_TIME
	 */
	public static RDFLiteral setDate(final RDFResource resource, final Date date) {
		return RDFResources.setDate(resource, TERM_DATE.getNamespace(), TERM_DATE.getName(), date);
	}

	/**
	 * Sets the <code>dc:date</code> property with the given value to the resource, using the given style.
	 * @param resource The resource to which the property should be set.
	 * @param date The property value to set.
	 * @param style The style of the date formatting.
	 * @return The added literal property value.
	 */
	public static RDFLiteral setDate(final RDFResource resource, final Date date, final W3CDateFormat.Style style) {
		return RDFResources.setDate(resource, TERM_DATE.getNamespace(), TERM_DATE.getName(), date, style);
	}

	/**
	 * Sets the <code>dc:date</code> property with the given value to the resource, using the given formatter.
	 * @param resource The resource to which the property should be set.
	 * @param date The property value to set.
	 * @param dateFormat The object to format the date.
	 * @return The added literal property value.
	 */
	public static RDFLiteral setDate(final RDFResource resource, final Date date, final DateFormat dateFormat) {
		return RDFResources.setDate(resource, TERM_DATE.getNamespace(), TERM_DATE.getName(), date, dateFormat);
	}

	/**
	 * Sets the <code>dc:language</code> property with the given value to the resource.
	 * @param resource The resource to which the property should be set.
	 * @param locale The property value to set.
	 * @return The added literal property value.
	 */
	public static RDFLiteral setLanguage(final RDFResource resource, final Locale locale) {
		//set the literal language tag for this locale
		return resource.setProperty(TERM_LANGUAGE.getNamespace(), TERM_LANGUAGE.getName(), Locales.getLanguageTag(locale));
	}

}
