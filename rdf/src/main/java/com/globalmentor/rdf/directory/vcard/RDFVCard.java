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

package com.globalmentor.rdf.directory.vcard;

import java.net.URI;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.globalmentor.net.URIs;
import com.globalmentor.rdf.*;
import com.globalmentor.rdf.directory.RDFDirectory;
import com.globalmentor.text.directory.vcard.*;

import static com.globalmentor.text.directory.vcard.VCard.*;
import static com.globalmentor.rdf.spec.RDF.*;
import static com.globalmentor.java.Java.*;
import static com.globalmentor.java.Strings.NO_STRINGS;
import static com.globalmentor.net.URIs.*;
import static com.globalmentor.rdf.RDFResources.*;

import static java.util.Objects.*;

/**
 * An ontology to represent a vCard <code>text/directory</code> profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>, "vCard MIME
 * Directory Profile".
 * @author Garret Wilson
 */
public class RDFVCard extends RDFDirectory {

	/** The recommended prefix to the vCard namespace. */
	public static final String VCARD_NAMESPACE_PREFIX = "vcard";

	/** The URI to the vCard namespace. */
	public static final URI VCARD_NAMESPACE_URI = URI.create("http://www.w3.org/2006/vcard/ns#");

	//vCard type names
	/** Specifies the components of the name of the object the vCard represents. The local name of <code>vcard:N</code>. */
	public static final String N_TYPE_NAME = N_TYPE;

	//vCard property names

	/** Specifies the electronic mail address for communication with the object the vCard represents. The local name of <code>vcard:email</code>. */
	public static final String EMAIL_PROPERTY_NAME = getVariableName(EMAIL_TYPE);

	/** Specifies the formatted text corresponding to the name of the object the vCard represents. The local name of <code>vcard:fn</code>. */
	public static final String FN_PROPERTY_NAME = getVariableName(FN_TYPE);

	/** Specifies the components of the name of the object the vCard represents. The local name of <code>vcard:n</code>. */
	public static final String N_PROPERTY_NAME = getVariableName(N_TYPE);
	/** Specifies the family name component of the name of the object the vCard represents. The local name of <code>vcard:familyName</code>. */
	public static final String FAMILY_NAME_PROPERTY_NAME = "familyName";
	/** Specifies the given name component of the name of the object the vCard represents. The local name of <code>vcard:givenName</code>. */
	public static final String GIVEN_NAME_PROPERTY_NAME = "givenName";
	/** Specifies additional name component of the name of the object the vCard represents. The local name of <code>vcard:additionalName</code>. */
	public static final String ADDITIONAL_NAME_PROPERTY_NAME = "additionalName";
	/** Specifies the honorific prefix component of the name of the object the vCard represents. The local name of <code>vcard:honorificPrefix</code>. */
	public static final String HONORIFIC_PREFIX_PROPERTY_NAME = "honorificPrefix";
	/** Specifies the honorific suffix component of the name of the object the vCard represents. The local name of <code>vcard:honorificSuffixes</code>. */
	public static final String HONORIFIC_SUFFIX_PROPERTY_NAME = "honorificSuffix";

	/** The number of components of the vCard N type. */
	//TODO del if not needed	private static final int N_COMPONENT_COUNT=5;

	/**
	 * Sets the {@link #N_PROPERTY_NAME} name information for a resource.
	 * @param resource The resource the <code>vcard:n</code> property of which should be set.
	 * @param name The name information to set.
	 * @throws NullPointerException if the given resource and/or name is <code>null</code>.
	 */
	public static void setName(final RDFResource resource, final Name name) {
		requireNonNull(name, "Name cannot be null.");
		final RDFResource nResource = locateTypedResource(resource, null, VCARD_NAMESPACE_URI, N_TYPE_NAME); //create the vcard:N resource
		final Locale locale = name.getLocale(); //get the name locale, if any
		if(locale != null) { //if there is a locale
			setLanguage(nResource, locale); //set the vcard:N directory:language property
		}
		//create an array of the properties and values to store
		final List<Map.Entry<URI, String[]>> propertyValuesPairs = Arrays.asList(
				new SimpleImmutableEntry<URI, String[]>(createReferenceURI(VCARD_NAMESPACE_URI, FAMILY_NAME_PROPERTY_NAME), name.getFamilyNames()),
				new SimpleImmutableEntry<URI, String[]>(createReferenceURI(VCARD_NAMESPACE_URI, GIVEN_NAME_PROPERTY_NAME), name.getGivenNames()),
				new SimpleImmutableEntry<URI, String[]>(createReferenceURI(VCARD_NAMESPACE_URI, ADDITIONAL_NAME_PROPERTY_NAME), name.getAdditionalNames()),
				new SimpleImmutableEntry<URI, String[]>(createReferenceURI(VCARD_NAMESPACE_URI, HONORIFIC_PREFIX_PROPERTY_NAME), name.getHonorificPrefixes()),
				new SimpleImmutableEntry<URI, String[]>(createReferenceURI(VCARD_NAMESPACE_URI, HONORIFIC_SUFFIX_PROPERTY_NAME), name.getHonorificSuffixes()));
		for(final Map.Entry<URI, String[]> propertyValuesPair : propertyValuesPairs) { //for all the property value pairs
			final URI propertyURI = propertyValuesPair.getKey(); //get the property URI
			final String[] values = propertyValuesPair.getValue(); //get the string values
			final int valueCount = values.length; //see how many values there are
			if(values.length > 0) { //if there is at least one value
				if(valueCount == 1) { //if there is only one value
					nResource.setProperty(propertyURI, values[0]); //set the first value as the single value
				} else { //if there is more than one value
					final RDFListResource<RDFObject> valueListResource = new RDFListResource<>(nResource.getRDF()); //create a new list
					for(final String value : values) { //for each value
						final RDFResource valueResource = locateResource(valueListResource, null); //create a blank node
						setValue(valueResource, value); //set the value of the value resource
						valueListResource.add(valueResource); //add the value resource to the list resource
					}
					nResource.setProperty(propertyURI, valueListResource); //set the list of values as the property value
				}
			}
		}
		resource.setProperty(VCARD_NAMESPACE_URI, N_PROPERTY_NAME, nResource); //set the name property with the name resource we constructed
	}

	/** The property URIs for the name components. */
	private static final URI[] N_COMPONENT_PROPERTY_URIS = new URI[] {createReferenceURI(VCARD_NAMESPACE_URI, FAMILY_NAME_PROPERTY_NAME),
			createReferenceURI(VCARD_NAMESPACE_URI, GIVEN_NAME_PROPERTY_NAME), createReferenceURI(VCARD_NAMESPACE_URI, ADDITIONAL_NAME_PROPERTY_NAME),
			createReferenceURI(VCARD_NAMESPACE_URI, HONORIFIC_PREFIX_PROPERTY_NAME), createReferenceURI(VCARD_NAMESPACE_URI, HONORIFIC_SUFFIX_PROPERTY_NAME)};

	/**
	 * Retrieves the {@link #N_PROPERTY_NAME} name information from a resource.
	 * @param resource The resource the name of which should be retrieved.
	 * @return The name information, or <code>null</code> if there is no <code>vcard:n</code> property or the property value is not an {@link RDFResource}.
	 * @throws NullPointerException if the given resource and/or name is <code>null</code>.
	 */
	public static Name getName(final RDFResource resource) {
		final RDFResource nResource = asResource(resource.getPropertyValue(VCARD_NAMESPACE_URI, N_PROPERTY_NAME)); //get the name property value as a resource
		if(nResource != null) { //if there is a name resource
			final Locale language = getLanguage(resource); //get the language specification, if there is one
			final String[][] nameComponentValues = new String[N_COMPONENT_PROPERTY_URIS.length][]; //create arrays of string arrays
			for(int i = nameComponentValues.length - 1; i >= 0; --i) { //for each name component 
				final RDFObject nComponentObject = nResource.getPropertyValue(N_COMPONENT_PROPERTY_URIS[i]); //get this name component
				//if there is a name component, convert it to one or more LocaleText objects and then convert that to a string; otherwise, use an empty string array
				nameComponentValues[i] = nComponentObject != null ? toStringArray(getTexts(nComponentObject)) : NO_STRINGS;
			}
			return new Name(nameComponentValues[0], nameComponentValues[1], nameComponentValues[2], nameComponentValues[3], nameComponentValues[4], language); //return a new name from the values we read
		} else { //if there is no name resource
			return null; //indicate that there was no name specified
		}
	}

	/**
	 * Creates an array of the string representations of all non-<code>null</code> objects in the given array.
	 * @apiNote Moved from <code>com.globalmentor.java.Arrays</code>.
	 * @param array The array to convert to a string array.
	 * @return A string array, each element of which contains the string version of the corresponding object array element or <code>null</code> if the
	 *         corresponding object is <code>null</code>.
	 * @throws NullPointerException if the given array is <code>null</code>.
	 * @deprecated Replace all the low-level array processing with collections.
	 */
	@Deprecated
	private static String[] toStringArray(final Object[] array) {
		final int length = array.length; //get the length of the array
		final String[] strings = new String[length]; //create a string array
		for(int i = 0; i < length; ++i) { //for each index
			final Object object = array[i]; //get this object
			strings[i] = object != null ? object.toString() : null; //store the object or null in the new array
		}
		return strings; //return the new array of strings
	}

	/**
	 * Determines a formatted name for the given resource. The formatted name is determined in this order:
	 * <ol>
	 * <li>The lexical form of the literal value of the <code>vcard:fn</code> property, if available.</li>
	 * <li>A formatted string derived from the value of the <code>vcard:n</code> property, if available.</li>
	 * <li>The label of the resource as determined by {@link RDFResources#getDisplayLabel(RDFResource)}.
	 * </ol>
	 * @param resource The resource for which a formatted name should be determined.
	 * @return The best possible formatted name string for the resource.
	 * @throws NullPointerException if the given resource is <code>null</code>.
	 */
	public static String getFormattedName(final RDFResource resource) {
		final RDFObject fnObject = resource.getPropertyValue(VCARD_NAMESPACE_URI, FN_PROPERTY_NAME); //get the vcard:fn value
		if(fnObject instanceof RDFLiteral) { //if the object is a literal
			return ((RDFLiteral)fnObject).getLexicalForm(); //return the lexical form of the literal vcard:fn value
		}
		final Name name = getName(resource); //otherwise, see if the resource has a name specified
		if(name != null) { //if there is a name specified
			return name.toString(); //format the name and return it
		}
		return getDisplayLabel(resource); //if all else fails, just return a label for the resource
	}

	/**
	 * Sets the email of a resource using the vCard {@link #EMAIL_PROPERTY_NAME} property.
	 * @param resource The resource the email of which to set.
	 * @param emailURI The URI expressing the email to set.
	 * @return The resource value used to represent the email.
	 * @throws NullPointerException if the given resource and/or email URI is <code>null</code>.
	 * @throws IllegalArgumentException if the given email URI does not have a scheme of {@value URIs#MAILTO_SCHEME}.
	 */
	public static RDFResource setEmail(final RDFResource resource, final URI emailURI) {
		if(!requireNonNull(emailURI, "Email URI cannot be null.").getScheme().equals(MAILTO_SCHEME)) { //if the email doesn't have the mailto URI scheme
			throw new IllegalArgumentException("Email URI " + emailURI + " does not have the " + MAILTO_SCHEME + " scheme.");
		}
		return resource.addProperty(VCARD_NAMESPACE_URI, EMAIL_PROPERTY_NAME, locateResource(resource, emailURI)); //add a new resource with the email as the URI
	}
}
