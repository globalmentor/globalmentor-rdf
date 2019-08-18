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

package com.globalmentor.rdf;

import java.net.URI;

import static com.globalmentor.java.Classes.*;
import static com.globalmentor.rdf.RDFResources.*;
import static com.globalmentor.rdf.spec.RDF.*;

/**
 * An RDF resource that by default adds an <code>rdf:type</code> property upon creation. By default the type name will be set to the local name of the class.
 * @author Garret Wilson
 */
public abstract class ClassTypedRDFResource extends DefaultRDFResource {

	/**
	 * Constructs a resource with a reference URI.
	 * @param referenceURI The reference URI for the new resource.
	 * @param typeNamespaceURI The namespace of the type URI to be added after construction of the object.
	 * @throws NullPointerException if the given type namespace URI is <code>null</code>.
	 */
	public ClassTypedRDFResource(final URI referenceURI, final URI typeNamespaceURI) {
		this(null, referenceURI, typeNamespaceURI); //construct the class with no data model
	}

	/**
	 * Convenience constructor that constructs a resource using a namespace URI and local name which will be combined to form the reference URI.
	 * @param namespaceURI The XML namespace URI that represents part of the reference URI.
	 * @param localName The XML local name that represents part of the reference URI.
	 * @param typeNamespaceURI The namespace of the type URI to be added after construction of the object.
	 * @throws NullPointerException if the given type namespace URI is <code>null</code>.
	 */
	public ClassTypedRDFResource(final URI namespaceURI, final String localName, final URI typeNamespaceURI) {
		this((RDFModel)null, namespaceURI, localName, typeNamespaceURI); //do the default construction, combining the namespace URI and the local name for the reference URI
	}

	/**
	 * Convenience constructor that constructs a resource using a namespace URI and local name which will be combined to form the reference URI.
	 * @param rdf The data model with which this resource should be associated.
	 * @param namespaceURI The XML namespace URI used in the serialization.
	 * @param localName The XML local name used in the serialization.
	 * @param typeNamespaceURI The namespace of the type URI to be added after construction of the object.
	 * @throws NullPointerException if the given type namespace URI is <code>null</code>.
	 */
	public ClassTypedRDFResource(final RDFModel rdf, final URI namespaceURI, final String localName, final URI typeNamespaceURI) {
		this(rdf, createReferenceURI(namespaceURI, localName), typeNamespaceURI); //do the default construction, combining the namespace URI and the local name for the reference URI
	}

	/**
	 * Constructs a resource with a reference URI from a data model.
	 * @param rdf The data model with which this resource should be associated.
	 * @param referenceURI The reference URI for the new resource.
	 * @param typeNamespaceURI The namespace of the type URI to be added after construction of the object.
	 * @throws NullPointerException if the given type namespace URI is <code>null</code>.
	 */
	public ClassTypedRDFResource(final RDFModel rdf, final URI referenceURI, final URI typeNamespaceURI) {
		super(rdf, referenceURI); //construct the parent class
		addType(this, typeNamespaceURI, getLocalName(getClass())); //add the default type based upon the given type namespace URI and the class name
	}

}