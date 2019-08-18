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

import java.lang.reflect.*;
import java.net.URI;

import static com.globalmentor.java.Java.*;

import static java.util.Objects.*;

/**
 * An RDF resource factory that can create Java classes within a certain package based upon the type local name. The class of the resource to be created should
 * have a reference URI constructor accepting <code>null</code>. The class of the resource to be created must have a reference URI constructor or a default
 * constructor. This class is thread safe.
 * @author Garret Wilson
 */
public class DefaultRDFResourceFactory implements RDFResourceFactory {

	/** The name of the package, with no '.' suffix, from which Java classes for this namespace will be created. */
	private final String packageName;

	/** @return The name of the package, with no '.' suffix, from which Java classes for this namespace will be created. */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Package constructor.
	 * @param namespacePackage The package for which Java classes for this namespace will be created.
	 * @throws NullPointerException if the given package is <code>null</code>.
	 */
	public DefaultRDFResourceFactory(final Package namespacePackage) {
		this(namespacePackage.getName()); //construct the class with the package name
	}

	/**
	 * Package name constructor.
	 * @param packageName The name of the package, with no '.' suffix, from which Java classes for this namespace will be created.
	 * @throws NullPointerException if the given package name is <code>null</code>.
	 */
	public DefaultRDFResourceFactory(final String packageName) {
		this.packageName = requireNonNull(packageName, "Package name cannot be null.");
	}

	/**
	 * Creates a resource with the provided reference URI based upon the type reference URI composed of the given XML serialization type namespace and local name.
	 * A type property derived from the specified type namespace URI and local name will be added to the resource. This implementation creates a class name by
	 * combining the package name with the type local name, separated by a '.'. If the resulting class has a URI constructor, it will be used; otherwise, its
	 * default constructor will be used. This implementation ignores the namespace URI.
	 * @param referenceURI The reference URI of the resource to create, or <code>null</code> if the resource created should be represented by a blank node.
	 * @param typeNamespaceURI The XML namespace used in the serialization of the type URI, or <code>null</code> if the type is not known.
	 * @param typeLocalName The XML local name used in the serialization of the type URI, or <code>null</code> if the type is not known.
	 * @return The resource created with this reference URI, with the given type added if a type was given, or <code>null</code> if no suitable resource can be
	 *         created.
	 */
	public RDFResource createResource(final URI referenceURI, final URI typeNamespaceURI, final String typeLocalName) {
		final String className = getPackageName() + PACKAGE_SEPARATOR + typeLocalName; //construct a class name from the package
		try {
			final Class<?> resourceClass = Class.forName(className); //get the class for the resource
			if(RDFResource.class.isAssignableFrom(resourceClass)) { //if this is a resource class
				Object resource = null; //we'll try to create the resource; if not, we'll return null
				try {
					try {
						final Constructor<?> constructor = resourceClass.getConstructor(URI.class); //try to get a reference URI constructor
						resource = constructor.newInstance(referenceURI); //create the resource from the reference URI constructor
					} catch(final NoSuchMethodException noSuchMethodException) { //if there is no reference URI constructor
						resource = resourceClass.newInstance(); //create the resource using its default constructor
					}
				} catch(final IllegalArgumentException illegalArgumentException) { //ignore errors and return null
				} catch(final InstantiationException instantiationException) { //ignore errors and return null
				} catch(final IllegalAccessException illegalAccessException) { //ignore errors and return null
				} catch(final InvocationTargetException invocationTargetException) { //ignore errors and return null
				}
				return RDFResource.class.cast(resource); //cast and return the resource we created, if any
			} else { //if this class is not for an RDF resource
				return null; //we can't return an RDF resource
			}
		} catch(final ClassNotFoundException classNotFoundException) {
			return null; //if there is no such class, we can't create a resource
		}

	}

}