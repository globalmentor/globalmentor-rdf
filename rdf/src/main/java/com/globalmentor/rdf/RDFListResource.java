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

import com.globalmentor.java.Arrays;

import static com.globalmentor.rdf.RDFResources.*;
import static com.globalmentor.rdf.spec.RDF.*;

/**
 * Represents an RDF list resource.
 * <p>
 * Manipulating an RDF list resource using its convenience methods may add anonymous resources that are are not known to the underlying RDF data model that
 * produced this list resource. Similarly, multiple resources may be created to represent the nil resource, the special immutable resource defined by RDF.
 * </p>
 * @param <E> The type of elements contained in the list.
 * @author Garret Wilson
 */
public class RDFListResource<E extends RDFObject> extends TypedRDFResource implements List<E> {

	/** @return The namespace URI of the ontology defining the default type of this resource. */
	public URI getDefaultTypeNamespaceURI() {
		return NAMESPACE_URI;
	}

	/** @return The local name of the default type of this resource. */
	public String getDefaultTypeName() {
		return LIST_CLASS_NAME;
	}

	/** Default constructor for an empty list with the <code>rdf:nil</code> reference URI. */
	public RDFListResource() {
		this((RDFModel)null); //construct a list resource with no RDF data model
	}

	/**
	 * Constructs an RDF list resource with a reference URI.
	 * @param newReferenceURI The reference URI for the new resource.
	 */
	public RDFListResource(final URI newReferenceURI) {
		this((RDFModel)null, newReferenceURI); //construct the class with no data model 
	}

	/**
	 * Data model, reference URI, and optional namespace URI and local name constructor.
	 * @param rdf The RDF data model to use as a factory for creating properties.
	 * @param newReferenceURI The reference URI for the new resource.
	 */
	protected RDFListResource(final RDFModel rdf, final URI newReferenceURI) {
		this(rdf, newReferenceURI, null, null); //construct the class without a first or next resource
	}

	/**
	 * Constructs an anonymous RDF list resource with a single element. The rest of the list is set to the RDF nil resource.
	 * @param first The first and only element of the list.
	 */
	public RDFListResource(final E first) {
		this((URI)null, first); //create a list with no other elements
	}

	/**
	 * Data model constructor.
	 * @param rdf The RDF data model to use as a factory for creating properties.
	 */
	public RDFListResource(final RDFModel rdf) {
		this(rdf, NIL_RESOURCE_URI); //construct the list with a nil reference URI
	}

	/**
	 * Constructs an anonymous RDF list resource with a single element. The rest of the list is set to the RDF nil resource.
	 * @param rdf The RDF data model to use as a factory for creating properties.
	 * @param first The first and only element of the list.
	 */
	public RDFListResource(final RDFModel rdf, final E first) {
		this(rdf, (URI)null, first); //create a list with no other elements
	}

	/**
	 * Constructs an RDF list resource with a single element. The rest of the list is set to the RDF nil resource.
	 * @param newReferenceURI The reference URI for the new resource.
	 * @param first The first and only element of the list.
	 */
	public RDFListResource(final URI newReferenceURI, final E first) {
		this(newReferenceURI, first, new RDFListResource<E>(NIL_RESOURCE_URI)); //create a list with no other elements
	}

	/**
	 * Constructs an RDF list resource with a single element. The rest of the list is set to the RDF nil resource.
	 * @param rdf The RDF data model to use as a factory for creating properties.
	 * @param newReferenceURI The reference URI for the new resource.
	 * @param first The first and only element of the list.
	 */
	public RDFListResource(final RDFModel rdf, final URI newReferenceURI, final E first) {
		this(rdf, newReferenceURI, first, new RDFListResource<E>(rdf, NIL_RESOURCE_URI)); //create a list with no other elements
	}

	/**
	 * Constructs an anonymous RDF list resource with a current element and the rest of the list.
	 * @param rdf The RDF data model to use as a factory for creating properties.
	 * @param first The first element of the list.
	 * @param rest The list resource representing the rest of the list, or <code>null</code> if no rest should be specified.
	 */
	public RDFListResource(final RDFModel rdf, final E first, final RDFResource rest) {
		this(rdf, (URI)null, first, rest); //construct a list with an anonymous reference URI
	}

	/**
	 * Constructs an RDF list resource with a current element and the rest of the list.
	 * @param newReferenceURI The reference URI for the new resource.
	 * @param first The first element of the list.
	 * @param rest The list resource representing the rest of the list, or <code>null</code> if no rest should be specified.
	 */
	public RDFListResource(final URI newReferenceURI, final E first, final RDFResource rest) {
		this(null, newReferenceURI, first, rest); //construct the list with no RDF data model
	}

	/**
	 * Constructs an RDF list resource with a current element and the rest of the list.
	 * @param rdf The RDF data model to use as a factory for creating properties.
	 * @param newReferenceURI The reference URI for the new resource.
	 * @param first The first element of the list, or <code>null</code> if there should be no first element.
	 * @param rest The list resource representing the rest of the list, or <code>null</code> if no rest should be specified.
	 */
	protected RDFListResource(final RDFModel rdf, final URI newReferenceURI, final E first, final RDFResource rest) {
		super(rdf, newReferenceURI); //construct the list with the reference URI and namespace URI and local name information
		if(first != null) //if there is a first element
			setFirst(first); //set the first element
		if(rest != null) //if a rest is specified
			setRest(rest); //set the rest of the list
	}

	/**
	 * Constructs an anonymous RDF list resource with the contents of a collection.
	 * @param <T> The type of the {@link RDFObject}.
	 * @param rdf The RDF data model to use as a factory for creating properties.
	 * @param collection The collection with which to populate the list.
	 * @return The anonymous list resource.
	 */
	public static <T extends RDFObject> RDFListResource<T> create(final RDFModel rdf, final Collection<? extends T> collection) {
		return create(rdf, (URI)null, collection); //create and return a list with an anonymous reference URI
	}

	/**
	 * Constructs an RDF list resource with the contents of a collection.
	 * @param <T> The type of the {@link RDFObject}.
	 * @param rdf The RDF data model to use as a factory for creating properties.
	 * @param newReferenceURI The reference URI for the new resource.
	 * @param collection The collection with which to populate the list, each element of which is an {@link RDFObject}.
	 * @return The RDF list resource containing the collection.
	 */
	public static <T extends RDFObject> RDFListResource<T> create(final RDFModel rdf, final URI newReferenceURI, final Collection<? extends T> collection) {
		//TODO now that we've removed the requirement of passing an RDF data model, maybe this can be optimized---or maybe removed altogether
		final RDFListResource<T> listResource; //we'll create a list resource for the collection 
		if(!collection.isEmpty()) { //if there are elements in the collection
			final Iterator<? extends T> resourceIterator = collection.iterator(); //get an iterator to the elements of the collection
			listResource = new RDFListResource<T>(rdf, resourceIterator.next()); //get the first resource in the collection
			RDFListResource<T> list = listResource; //we'll keep adding to the list as we go along
			while(resourceIterator.hasNext()) { //while there are more elements in the collection

				final RDFListResource<T> nextList = new RDFListResource<T>(rdf, resourceIterator.next()); //get the next resource in the collection
				setRest(list, nextList); //add the next list to this one
				list = nextList; //we'll use the next list the next time around 
			}
		} else { //if the collection is empty
			listResource = new RDFListResource<T>(rdf, NIL_RESOURCE_URI); //use the nil list
		}
		return listResource; //return whichever list resource we created or used
	}

	/**
	 * Retrieves the first element of the list. If this resource has more than one property of <code>rdf:first</code>, it is undefined which of those property
	 * values will be returned.
	 * @param resource The list resource for which the first element will be returned.
	 * @return The first element of the list, or <code>null</code> if the resource has no first element specified.
	 */
	public static RDFObject getFirst(final RDFResource resource) {
		return resource.getPropertyValue(NAMESPACE_URI, FIRST_PROPERTY_NAME); //return the first element property
	}

	/**
	 * Retrieves the first element of this list. If this resource has more than one property of <code>rdf:first</code>, it is undefined which of those property
	 * values will be returned.
	 * @return The first element of this list, or <code>null</code> if there is no first element specified.
	 */
	@SuppressWarnings("unchecked")
	public E getFirst() {
		return (E)getFirst(this); //return the first element property
	}

	/**
	 * Retrieves the rest of the list, which is usually another list. If the resource has more than one property of <code>rdf:rest</code>, it is undefined which
	 * of those property values will be returned.
	 * @param resource The list resource for which the rest property will be returned.
	 * @return The resource representing the rest of the list, or <code>null</code> if the resource has no rest of the list specified.
	 */
	public static RDFResource getRest(final RDFResource resource) {
		return asResource(resource.getPropertyValue(NAMESPACE_URI, REST_PROPERTY_NAME)); //return the rest property
	}

	/**
	 * Retrieves the rest of this list, which is usually another list. If this resource has more than one property of <code>rdf:rest</code>, it is undefined which
	 * of those property values will be returned.
	 * @return The resource representing the rest of the list, or <code>null</code> if this resource has no rest of the list specified.
	 */
	public RDFResource getRest() {
		return getRest(this); //return the rest property
	}

	/**
	 * Replaces all <code>rdf:first</code> properties of the resource with a new property with the given value.
	 * @param <T> The type of the {@link RDFObject}.
	 * @param resource The resource for which the first property should be set.
	 * @param value The first element of the list, or <code>null</code> if the element should be removed.
	 */
	public static <T extends RDFObject> void setFirst(final RDFResource resource, final T value) {
		if(value != null) { //if there is a value
			resource.setProperty(NAMESPACE_URI, FIRST_PROPERTY_NAME, value); //set the first value
		} else { //if there is no value
			resource.removeProperties(NAMESPACE_URI, FIRST_PROPERTY_NAME); //remove the first value		
		}
	}

	/**
	 * Replaces all <code>rdf:first</code> properties of this list with a new property with the given value.
	 * @param value The first element of the list, or <code>null</code> if the element should be removed.
	 */
	public void setFirst(final E value) {
		setFirst(this, value); //set the first value
	}

	/**
	 * Replaces all <code>rdf:rest</code> properties of the resource with a new property with the given value.
	 * @param resource The resource for which the rest property should be set.
	 * @param value The resource, which should be a list, representing the rest of the elements of the list, or <code>null</code> if the rest should be removed.
	 */
	public static void setRest(final RDFResource resource, final RDFResource value) {
		if(value != null) { //if there is a value
			resource.setProperty(NAMESPACE_URI, REST_PROPERTY_NAME, value); //set the first value
		} else { //if there is no value
			resource.removeProperties(NAMESPACE_URI, REST_PROPERTY_NAME); //remove the rest of the list		
		}
	}

	/**
	 * Replaces all <code>rdf:rest</code> properties of this list with a new property with the given value.
	 * @param value The resource, which should be a list, representing the rest of the elements of the list, or <code>null</code> if the rest should be removed.
	 */
	public void setRest(final RDFResource value) {
		setRest(this, value); //set the first value
	}

	/** @return A new instance of an RDF list resource representing the nil resource. */
	protected RDFListResource<E> createNil() {
		return new RDFListResource<E>(getRDF(), NIL_RESOURCE_URI); //create a nil list resource
	}

	@Override
	public int size() { //TODO cache the size of the list if we can at all, because walking the list is very expensive; perhaps only cache the size if there are no non-anonymous list nodes within the list (even though if a data model was constructed from a nodeID serialization, other lists could reference an anonymous sublist)
		int size = 0; //start out knowing about no elements
		RDFResource list = this; //start with this list resource
		while(list != null && !RDFResources.isNil(list)) { //while we have a list and it's not the nil resource
			++size; //show that we found another element
			list = getRest(list); //look at the rest of the list
		}
		return size; //return whatever size we found
	}

	@Override
	public boolean isEmpty() {
		return RDFResources.isNil(this); //we're empty if we're the nil list		
	}

	/**
	 * {@inheritDoc}
	 * @throws ClassCastException Thrown if the specified element is not an {@link RDFObject}.
	 */
	@Override
	public boolean contains(final Object object) {
		return indexOf(object) >= 0; //if the object has a valid index, it's contained in the list
	}

	@Override
	public Iterator<E> iterator() {
		return new RDFListIterator(); //create a new iterator over all the list elements
	}

	/**
	 * {@inheritDoc}
	 * @implSpec This method is currently unsupported.
	 */
	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException(); //TODO implement listIterator(), and have iterator() call this
	}

	/**
	 * {@inheritDoc}
	 * @implSpec This method is currently unsupported.
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException(); //TODO implement listIterator(), and have iterator() call this
	}

	@Override
	public Object[] toArray() {
		return toArray(new RDFObject[size()]); //create a new array of RDF objects, store the elements in it, and return the array TODO is this correct? check
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] array) {
		final int size = size(); //get our size
		array = Arrays.getArray(array, size); //make sure our array is large enough
		int i = 0; //this will index into the array we're filling
		final Iterator<E> iterator = iterator(); //get an iterator to the elements
		while(iterator.hasNext()) { //while there are more elements
			try {
				array[i++] = (T)iterator.next(); //put the next object into the array and advance our index
			} catch(ClassCastException classCastException) { //if there is a problem casting to T
				throw (ArrayStoreException)new ArrayStoreException(classCastException.getMessage()).initCause(classCastException);
			}

		}
		if(array.length > size) //if we didn't fill the array
			array[size] = null; //set the next element to null
		return array; //return the array we filled
	}

	/**
	 * Returns the first contained resource with the specified reference URI.
	 * @param uri The reference URI of the resource to retrieve.
	 * @return The first contained resource with the given reference URI, or <code>null</code> if the list does not contain a resource with the given reference
	 *         URI.
	 */
	public RDFResource getResourceByReferenceURI(final URI uri) {
		int currentIndex = 0; //start out on the first index
		RDFResource list = this; //start with this list resource
		while(list != null && !RDFResources.isNil(list)) { //while we have a list and it's not the nil resource
			final RDFObject object = getFirst(list); //get this resource
			if(object instanceof RDFResource) {
				final RDFResource resource = (RDFResource)object; //get the resource
				if(uri.equals(resource.getURI())) { //if this resource has the correct reference URI
					return resource; //return the resource
				}
			}
			list = getRest(list); //look at the rest of the list
			++currentIndex; //go to the next index
		}
		return null; //report that we couldn't find a resource containing the requested resource
	}

	@Override
	@SuppressWarnings("unchecked")
	public E get(int index) {
		int currentIndex = 0; //start out on the first index
		RDFResource list = this; //start with this list resource
		while(list != null && !RDFResources.isNil(list)) { //while we have a list and it's not the nil resource
			if(currentIndex == index) { ///if this is the correct index
				return (E)getFirst(list); //return the element at this index
			}
			list = getRest(list); //look at the rest of the list
			++currentIndex; //go to the next index
		}
		throw new IndexOutOfBoundsException("The index " + index + " must be >=0 and <" + currentIndex); //show that we don't have an element for this index
	}

	@Override
	@SuppressWarnings("unchecked")
	public E set(final int index, final E object) {
		int currentIndex = 0; //start out on the first index
		RDFResource list = this; //start with this list resource, which guarantees that the list will not be null the first time around
		while(list != null && !RDFResources.isNil(list)) { //while we have a list and it's not the nil resource
			if(currentIndex == index) { ///if this is the correct index
				final E oldFirst = (E)getFirst(list); //get the current first of the list
				setFirst(list, object); //set this element as the value of this node
				return oldFirst; //return the element that was previously at this index  
			}
			list = getRest(list); //look at the rest of the list
			++currentIndex; //go to the next index
		}
		throw new IndexOutOfBoundsException("The index " + index + " must be >=0 and <" + currentIndex); //show that we don't have an element for this index		
	}

	/**
	 * {@inheritDoc}
	 * @implSpec This implementation calls {@link #add(int, RDFObject)} using {@link #size()}.
	 */
	@Override
	public boolean add(final E object) {
		add(size(), object); //add the object to the end of the list
		return true; //show that we modified the list
	}

	/**
	 * {@inheritDoc}
	 * @see #create(RDFModel, URI, Collection)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void add(final int index, final E object) {
		int currentIndex = 0; //start out on the first index
		RDFResource list = this; //start with this list resource, which guarantees that the list will not be null the first time around
		do { //do post-checks, because we'll always have a list the first time around, as we're using this list as the first element
			if(currentIndex == index) { ///if this is the correct index
				final URI oldReferenceURI = list.getURI(); //get the old reference URI of this list element (which might even be the nil URI)
				final E oldFirst = (E)getFirst(list); //get the current first of the list
				final RDFResource oldRest = getRest(list); //get the current rest of the list
				list.setReferenceURI(null); //effectively change the current node into an anonymous node
				setFirst(list, object); //set this element as the value of this node
				setRest(list, new RDFListResource<E>(oldReferenceURI, oldFirst, oldRest)); //create a new element that mimics the old one, and add it as the rest of the list
				return; //we've inserted the element, so there's nothing more to do here 
			}
			if(!RDFResources.isNil(list)) { //if we're not at the last element
				list = getRest(list); //look at the rest of the list
				++currentIndex; //go to the next index
			} else { //if we reached the last element
				list = null; //don't bother even looking for another list---it would be incorred for the nil list to have a rest property, anyway
			}
		} while(list != null); //keep looking while we have a list and it's not the nil resource
		throw new IndexOutOfBoundsException("The index " + index + " must be >=0 and <" + currentIndex); //show that we don't have an element for this index
	}

	@Override
	public boolean remove(final Object object) {
		//TODO del		final RDFObject rdfObject=(RDFObject)object;	//cast the object to an RDF object to make sure it's the correct type
		RDFResource list = this; //start with this list resource
		while(list != null && !RDFResources.isNil(list)) { //while we have a list and it's not the nil resource
			final RDFObject first = getFirst(list); //get the first object
			final RDFResource rest = getRest(list); //get the rest of the list
			if(Objects.equals(object, first)) { //if this node contains the correct object
				setFirst(list, getFirst(rest)); //transfer the element of the next node to this node
				setRest(list, getRest(rest)); //transfer the rest of the next node to this node					
				if(RDFResources.isNil(rest)) { //if the rest of the list is the nil list, the empty list
					list.setReferenceURI(rest.getURI()); //effectively change the current node into the empty list
				}
				return true; //we've removed the current value by taking over the value of the next node and removing the next node from our list
			}
			list = rest; //look at the rest of the list
		}
		return false; //show that we didn't modify the list
	}

	@Override
	@SuppressWarnings("unchecked")
	public E remove(final int index) {
		int currentIndex = 0; //start out on the first index
		RDFResource list = this; //start with this list resource
		while(list != null && !RDFResources.isNil(list)) { //while we have a list and it's not the nil resource
			final E first = (E)getFirst(list); //get the first object
			final RDFResource rest = getRest(list); //get the rest of the list
			if(currentIndex == index) { ///if this is the correct index
				setFirst(list, getFirst(rest)); //transfer the element of the next node to this node
				setRest(list, getRest(rest)); //transfer the rest of the next node to this node					
				if(RDFResources.isNil(rest)) { //if the rest of the list is the nil list, the empty list
					list.setReferenceURI(rest.getURI()); //effectively change the current node into the empty list
				}
				return first; //we've removed the current value; return the old first value
			}
			list = rest; //look at the rest of the list
			++currentIndex; //go to the next index
		}
		throw new IndexOutOfBoundsException("The index " + index + " must be >=0 and <" + currentIndex); //show that we don't have an element for this index		
	}

	@Override
	public boolean containsAll(final Collection<?> collection) {
		final Iterator<?> iterator = collection.iterator(); //get an iterator to the collection
		while(iterator.hasNext()) { //while there are more elements in the collection
			if(!contains(iterator.next())) { //if we don't contain the next element
				return false; //show that there at least one element we don't contain
			}
		}
		return true; //we checked all elements, and we have them all
	}

	@Override
	public boolean addAll(final Collection<? extends E> collection) {
		boolean modified = false; //we haven't modified the list, yet
		for(E resource : collection) { //look at each resource in the collection
			if(add(resource)) { //add the next element to our list; if the operation modified the list
				modified = true; //show that we modified the list
			}
		}
		return modified; //show whether we modified this list
	}

	/**
	 * {@inheritDoc}
	 * @implSpec This method is currently unsupported.
	 */
	@Override
	public boolean addAll(final int index, final Collection<? extends E> collection) { //TODO implement addAll(int, Collection)
		throw new UnsupportedOperationException("RDFListResource does not yet support addAll(int, Collection)");
	}

	@Override
	public boolean removeAll(final Collection<?> collection) {
		boolean modified = false; //we haven't modified the list, yet
		for(Object object : collection) { //look at each object in the collection
			if(remove(object)) { //remove the next element from our list; if the operation modified the list
				modified = true; //show that we modified the list
			}
		}
		return modified; //show whether we modified this list		
	}

	/**
	 * {@inheritDoc}
	 * @implSpec This method is currently unsupported.
	 */
	@Override
	public boolean retainAll(final Collection<?> collection) { //TODO add retainAll() support
		throw new UnsupportedOperationException("RDFListResource does not yet support retainAll()");
	}

	@Override
	public int indexOf(final Object object) {
		int index = 0; //start out on the first index
		//TODO del		final RDFObject rdfObject=(RDFObject)object;	//cast the object to an RDF object to make sure it's the correct type
		RDFResource list = this; //start with this list resource
		while(list != null && !RDFResources.isNil(list)) { //while we have a list and it's not the nil resource
			final RDFObject first = getFirst(list); //get the first object
			if(Objects.equals(object, first)) { //if the objects are equal (taking into account nulls)
				return index; //show the index at which we found the object 
			}
			list = getRest(list); //look at the rest of the list
			++index; //go to the next index
		}
		return -1; //show that we couldn't find the specified object
	}

	@Override
	public int lastIndexOf(final Object object) {
		int lastIndexOf = -1; //start off not knowing the last index of the object
		int index = 0; //start out on the first index
		//TODO del		final RDFObject rdfObject=(RDFObject)object;	//cast the object to an RDF object to make sure it's the correct type
		RDFResource list = this; //start with this list resource
		while(list != null && !RDFResources.isNil(list)) { //while we have a list and it's not the nil resource
			final RDFObject first = getFirst(list); //get the first object
			if(Objects.equals(object, first)) { //if the objects are equal (taking into account nulls)
				lastIndexOf = index; //keep track of the last index at which we found the object 
			}
			list = getRest(list); //look at the rest of the list
			++index; //go to the next index
		}
		return lastIndexOf; //return the last index at which we found the object, or -1 if we couldn't find it a tall 		
	}

	@Override
	public void clear() {
		setFirst(null); //remove our first element
		setRest(null); //remove the rest of the list
		setReferenceURI(NIL_RESOURCE_URI); //set our reference URI to the nil reference URI
	}

	/**
	 * {@inheritDoc}
	 * @implSpec This method is currently unsupported.
	 */
	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		throw new UnsupportedOperationException(); //TODO implement subList()
	}

	/**
	 * An iterator that allows iteration over the the elements in the list resource.
	 * @implSpec This implementation does not support {@link #remove()}.
	 */
	protected class RDFListIterator implements Iterator<E> {

		/** The next list resource in the list. */
		protected RDFResource nextList;

		/** Default constructor. */
		public RDFListIterator() {
			nextList = RDFListResource.this; //the first list element will be represented by our current list resource
		}

		@Override
		public boolean hasNext() {
			//we have a next list element if there is a next list node and it's not the nil resource;
			//make sure it has a next element as well, in case the list is corrupt, as that is what the next() method will use
			return nextList != null && !RDFResources.isNil(nextList) && getFirst(nextList) != null;
		}

		@Override
		@SuppressWarnings("unchecked")
		public E next() {
			if(nextList != null && !RDFResources.isNil(nextList)) { //if we have a next list that isn't the nil resource
				final E nextResource = (E)getFirst(nextList); //get the element represented by the list
				if(nextResource != null) { //if there is a next resource
					nextList = getRest(nextList); //show which list (if any) holds the rest of the list elements
					return nextResource; //return the resource element we found
				}
			}
			throw new NoSuchElementException(); //show that there are no more elements
		}

		/**
		 * {@inheritDoc}
		 * @implSpec This method is currently unsupported.
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException(); //show that we don't yet support removing elements			
		}

	}

}
