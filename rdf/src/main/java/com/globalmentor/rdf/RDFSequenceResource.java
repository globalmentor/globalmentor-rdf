/*
 * Copyright © 1996-2008 GlobalMentor, Inc. <https://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.rdf;

import java.net.URI;
import java.util.*;

import static com.globalmentor.rdf.spec.RDF.*;

/**
 * Represents an RDF sequence resource.
 * @author Garret Wilson
 */
public class RDFSequenceResource extends RDFContainerResource implements Comparator<RDFPropertyValuePair> {

	/** @return The namespace URI of the ontology defining the default type of this resource. */
	public URI getDefaultTypeNamespaceURI() {
		return NAMESPACE_URI;
	}

	/** @return The local name of the default type of this resource. */
	public String getDefaultTypeName() {
		return SEQ_CLASS_NAME;
	}

	/**
	 * Constructs an RDF sequence resource with a reference URI.
	 * @param newReferenceURI The reference URI for the new resource.
	 */
	public RDFSequenceResource(final URI newReferenceURI) {
		super(newReferenceURI); //construct the parent class
	}

	/**
	 * Data model and reference URI constructor.
	 * @param rdf The data model associated with the container.
	 * @param newReferenceURI The reference URI for the new resource.
	 */
	RDFSequenceResource(final RDFModel rdf, final URI newReferenceURI) {
		super(rdf, newReferenceURI); //construct the parent class
	}

	/**
	 * Adds a property by creating a {@link RDFPropertyValuePair} from the given property and value. If an equivalent property already exists, no action is taken.
	 * This version ensures that properties are sorted after being added to the resource.
	 * @param property A property resource; the predicate of an RDF statement.
	 * @param value A property value; the object of an RDF statement.
	 * @return The added property value.
	 */
	public <T extends RDFObject> T addProperty(final RDFResource property, final T value) {
		final T rdfObject = super.addProperty(property, value); //add this property normally
		Collections.sort(propertyList, this); //sort the items by the number contained in the names
		return rdfObject; //return the value we added
	}

	/**
	 * @return A read-only collection of the items (specified by <code>rdf:li_</code> properties), in the order specified by their <code>rdf:li_</code>
	 *         properties.
	 * @see #getItemList
	 */
	public Collection<RDFObject> getItemCollection() {
		return getItemList(); //return the ordered version
	}

	/**
	 * @return A read-only list of the items in the container, in the order specified by their <code>rdf:li_</code> properties.
	 */
	public List<RDFObject> getItemList() {
		final List<RDFPropertyValuePair> itemPropertyList = getItemProperties(); //get the <li> item properties
		itemPropertyList.sort(this); //sort the items by the number contained in the names
		return Collections.unmodifiableList(getItemList(itemPropertyList)); //return the values of the item properties as an unmodifiable list
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The name of the object is interpreted to be an RDF container property that contains as its reference URI the order within the container of the item.
	 * </p>
	 */
	@Override
	public int compare(final RDFPropertyValuePair object1, final RDFPropertyValuePair object2) { //TODO we might put this in RDFContainerResource, even though it isn't used by all containers---or maybe even make an internal class that does this comparation
		//create the URI string that will appear at the beginning of every rdf:li_XXX property
		//TODO testing; make this constant and static somewhere
		final String RDF_LI_REFERENCE_URI_PREFIX = NAMESPACE_URI + CONTAINER_MEMBER_PREFIX;
		//TODO testing; make this constant and static somewhere
		final int RDF_LI_REFERENCE_URI_PREFIX_LENGTH = RDF_LI_REFERENCE_URI_PREFIX.length();
		//get the reference URIs of the two properties
		final URI typeURI1 = object1.getProperty().getURI();
		final URI typeURI2 = object2.getProperty().getURI();
		//TODO fix for better checking		if(typeURI1.startsWith(CONTAINER_MEMBER_PREFIX) && typeURI2.startsWith(CONTAINER_MEMBER_PREFIX))  //if

		if(typeURI1.toString().startsWith(RDF_LI_REFERENCE_URI_PREFIX)) { //if the first property an rdf:li_XXX	//TODO fix better
			if(typeURI2.toString().startsWith(RDF_LI_REFERENCE_URI_PREFIX)) { //if the second property an rdf:li_XXX as well	//TODO fix better
				//TODO make sure the strings actually start with the correct prefix, and that the numbers are actually numbers
				final int count1 = Integer.parseInt(typeURI1.toString().substring(RDF_LI_REFERENCE_URI_PREFIX_LENGTH)); //get the first count by removing everyting before the count
				final int count2 = Integer.parseInt(typeURI2.toString().substring(RDF_LI_REFERENCE_URI_PREFIX_LENGTH)); //get the first count by removing everyting before the count
				return count1 - count2; //see which number is great
			} else { //if only the first is an rdf:li_XXX
				return -1; //put members at the beginning
			}
		} else { //if the first property is not an rdf:li_XXX
			if(typeURI2.toString().startsWith(RDF_LI_REFERENCE_URI_PREFIX)) { //if only the second property an rdf:li_XXX	//TODO fix better
				return 1; //put non-members at the end
			} else
				//if neither property is an rdf:li_XXX
				return typeURI1.compareTo(typeURI2); //compare the URIs normally
		}
		//TODO fix this to sort non-li properties as well
	}

}
