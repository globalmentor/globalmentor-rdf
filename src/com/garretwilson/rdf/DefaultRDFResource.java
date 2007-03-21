package com.garretwilson.rdf;

import java.lang.ref.*;
import java.net.URI;
import java.util.*;

import com.garretwilson.model.*;
import com.garretwilson.net.BoundPropertyResource;

import static com.garretwilson.rdf.RDFUtilities.*;
import com.garretwilson.rdf.rdfs.RDFSUtilities;

/**Represents the default implementation of an RDF resource.
<p>This class provides compare functionality that sorts according to the reference
	URI.</p>
<p>This resource keeps a weak reference to the data model that created it, if
	any.</p> 
@author Garret Wilson
*/
public class DefaultRDFResource extends BoundPropertyResource implements RDFResource, Cloneable
{

	/**The data model that created this resource, or <code>null</code> if the
		resource was created separate from a data model.
	*/
	private Reference<RDF> rdfReference;

		/**@return The RDF data model with which this resource is associated, or
			<code>null</code> if this resource is not associated with a data model.
		*/
		public RDF getRDF()
		{
			return rdfReference!=null ? rdfReference.get() : null;	//return the RDF, if any, indicated by the reference
		}

		/**Associates this resource with an RDF data model.
		@param rdf The RDF data model with which to associate this resource, or
			<code>null</code> if this resource should not be associated with any
			RDF data model.
		*/
		public void setRDF(final RDF rdf)
		{
			rdfReference=rdf!=null ? new WeakReference<RDF>(rdf) : null;	//save the RDF, if any, using a weak reference			
		}

	/**The list of properties, each of which is a <code>RDFPropertyValuePair</code>,
		with the name being the property predicate and the value being the property
		value.
	*/
	protected ArrayList<RDFPropertyValuePair> propertyList=new ArrayList<RDFPropertyValuePair>();  //G***should this really be protected, and not private? currently only used by RDFSequenceResource

	/**@return The number of properties this resource has.*/
	public int getPropertyCount() {return propertyList.size();}

	/**@return Iterable access to all properties, each of which is a {@link RDFPropertyValuePair}, with the name being the property predicate and the value being the property value.*/
	@SuppressWarnings("unchecked")	//we're cloning our own list, so we know its generic type
	public Iterable<RDFPropertyValuePair> getProperties()
	{
		return (Iterable<RDFPropertyValuePair>)propertyList.clone();	//clone and return the list of properties
	}
	
	/**@return An iterator that allows traversal of all properties, each of which
		is a <code>RDFPropertyValuePair</code>, with the name being the property predicate
		and the value being the property value.
	*/
	public ListIterator<RDFPropertyValuePair> getPropertyIterator()
	{
		return propertyList.listIterator(); //return an iterator to the properties
	}

	/**Searches and returns the first occurring property value that appears as
		an RDF statement object with a predicate of <code>propertyResource</code>.
	@param propertyResource The property resource.
	@return The value of the property, either a <code>RDFResource</code> or a
		<code>RDFLiteral</code>, or <code>null</code> if this resource has no such
		property.
	*/
	public RDFObject getPropertyValue(final RDFResource propertyResource)
	{
		for(final RDFPropertyValuePair propertyValuePair:propertyList)  //for each property
		{
		  if(propertyValuePair.getProperty().equals(propertyResource))  //if this resource is the same as the one requested
				return propertyValuePair.getPropertyValue(); //return the value of the property as an RDF object
		}
		return null;  //show that we couldn't find such a property
	}

	/**Searches and returns the first occurring property value that appears as
		an RDF statement object with a predicate of <code>propertyURI</code>.
	@param propertyURI The reference URI of the property resource.
	@return The value of the property, either a <code>RDFResource</code> or a
		<code>RDFLiteral</code>, or <code>null</code> if this resource has no such
		property.
	*/
	public RDFObject getPropertyValue(final URI propertyURI)
	{
		for(final RDFPropertyValuePair propertyValuePair:propertyList)  //for each property
		{
		  if(propertyURI.equals(propertyValuePair.getProperty().getReferenceURI()))  //if this resource is that identified by the property URI
				return propertyValuePair.getPropertyValue(); //return the value of the property as an RDF object
		}
		return null;  //show that we couldn't find such a property
	}

	/**Searches and returns the first occurring property value that appears as
		an RDF statement object with a predicate of a property URI formed by the
		given namespace URI and local name. This is a convenience function that
		creates a property URI from an XML qualified name automatically for searching.
	@param namespaceURI The XML namespace URI used in the serialization.
	@param localName The XML local name used in the serialization.
	@return The value of the property, either a <code>RDFResource</code> or a
		<code>RDFLiteral</code>, or <code>null</code> if this resource has no such
		property.
	*/
	public RDFObject getPropertyValue(final URI namespaceURI, final String localName)
	{
		return getPropertyValue(RDFUtilities.createReferenceURI(namespaceURI, localName)); //look for the property, combining the namespace URI and the local name for the reference URI
	}

	/**Searches and returns an iterable of all property values that appear as RDF
		statement objects with a predicate of <var>propertyURI</var>.
	@param propertyURI The reference URI of the property resources.
	@return A read-only iterable of values of properties.
	*/
	public Iterable<RDFObject> getPropertyValues(final URI propertyURI)
	{
		return getPropertyValues(propertyURI, RDFObject.class);	//get all properties, whether they are resources or literals
	}
	
	/**Searches and returns an iterable of all property values of the requested type that appear as RDF
		statement objects with a predicate of <var>propertyURI</var>.
	@param propertyURI The reference URI of the property resources.
	@param valueType The type of values to include
	@return A read-only iterable of values of properties.
	*/
	public <T> Iterable<T> getPropertyValues(final URI propertyURI, final Class<T> valueType)
	{
		final List<T> propertyValueList=new ArrayList<T>(); //create a list in which to store the property values
		for(final RDFPropertyValuePair propertyValuePair:propertyList)  //for each property
		{
		  if(propertyURI.equals(propertyValuePair.getProperty().getReferenceURI()))  //if this resource is that identified by the property URI
			{
				final RDFObject propertyValue=propertyValuePair.getPropertyValue();	//get the property value
				if(valueType.isInstance(propertyValue))	//if the property is of the correct type
				{
					propertyValueList.add((T)propertyValue); //add the value of the property to the value list
				}
			}
		}
		return Collections.unmodifiableList(propertyValueList);  //return an unmodifiable version of the list of properties we collected
	}
	
	/**Searches and returns an iterable of all property values that appear as
		RDF statement objects with a predicate of a property URI formed by the
		given namespace URI and local name. This is a convenience function that
		creates a property URI automatically for searching.
	@param namespaceURI The XML namespace URI that represents part of the reference URI.
	@param localName The XML local name that represents part of the reference URI.
	@return A read-only iterable of values of properties.
	*/
	public Iterable<RDFObject> getPropertyValues(final URI namespaceURI, final String localName)
	{
		return getPropertyValues(namespaceURI, localName, RDFObject.class);	//get all properties, whether they are resources or literals		
	}
	
	/**Searches and returns an iterable of all property values of the requested type that appear as
		RDF statement objects with a predicate of a property URI formed by the
		given namespace URI and local name. This is a convenience function that
		creates a property URI automatically for searching.
	@param namespaceURI The XML namespace URI that represents part of the reference URI.
	@param localName The XML local name that represents part of the reference URI.
	@param valueType The type of values to include
	@return A read-only iterable of values of properties.
	*/
	public <T> Iterable<T> getPropertyValues(final URI namespaceURI, final String localName, final Class<T> valueType)
	{
		return getPropertyValues(RDFUtilities.createReferenceURI(namespaceURI, localName), valueType); //look for the property, combining the namespace URI and the local name for the reference URI
	}

	/**Determines if the resource has the given property with the resource
		identified by the given URI.
	@param propertyURI The reference URI of the property resource.
	@param propertyValueURI The URI of the resource to which the property should
		be compared.
	@return <code>true</code> if the specified property is set to the specified
		value.
	*/
	public boolean hasPropertyResourceValue(final URI propertyURI, final URI propertyValueURI)
	{
		for(final RDFPropertyValuePair propertyValuePair:propertyList)  //for each property
		{
		  if(propertyURI.equals(propertyValuePair.getProperty().getReferenceURI()))  //if this resource is that identified by the property URI
			{
				if(propertyValuePair.getPropertyValue() instanceof RDFResource)	//if the value is a resource
				{
					//if the resource value has the correct reference URI
					if(propertyValueURI.equals(((RDFResource)propertyValuePair.getPropertyValue()).getReferenceURI())) 
						return true;
				}
			}
		}
		return false; //show that there was no matching property
	}

	/**Determines if the resource has the given property with the resource
		identified by the given URI.
	This is a convenience function that creates a property URI from an XML
		qualified name automatically for searching.
	@param namespaceURI The XML namespace URI that represents part of the reference URI.
	@param localName The XML local name that represents part of the reference URI.
	@param propertyValueURI The URI of the resource to which the property should
		be compared.
	@return <code>true</code> if the specified property is set to the specified
		value.
	*/
	public boolean hasPropertyResourceValue(final URI namespaceURI, final String localName, final URI propertyValueURI)
	{
		return hasPropertyResourceValue(RDFUtilities.createReferenceURI(namespaceURI, localName), propertyValueURI); //check the property, combining the namespace URI and the local name for the reference URI
	}
	
	/**Adds a property by creating a <code>RDFPropertyValuePair</code> from the given
		property and value. For each property, this resource serves as the subject
		of an RDF statement with the property as the predicate and the value as
		the object.
		<p>Note that the property is not simply a property URI &mdash; it is a
		resource that is identified by the property URI.</p>
		<p>If an equivalent property already exists, no action is taken.</p>
	@param property A property resource; the predicate of an RDF statement.
	@param value A property value; the object of an RDF statement.
	@return The added property value.
	*/
	public RDFObject addProperty(final RDFResource property, final RDFObject value)
	{
		final RDFPropertyValuePair propertyValuePair=new RDFPropertyValuePair(property, value); //create a name/value pair with the property and value
		if(!propertyList.contains(propertyValuePair)) //if there is not already this property with this value
		{
			propertyList.add(propertyValuePair);  //add the property and value to the list
			firePropertyChange(property.getReferenceURI().toString(), null, value);	//fire a property change event with the new property value
		}
		return value; //return the value we added
	}

	/**Adds a property by creating a <code>RDFPropertyValuePair</code> from the given
		property and value. This is a convenience method that allows specification
		of the property resource through a namespace URI and a local name.
		For each property, this resource serves as the subject
		of an RDF statement with the property as the predicate and the value as
		the object.
	<p>Note that the property is not simply a property URI &mdash; it is a
		resource that is identified by the property URI.</p>
	<p>If an equivalent property already exists, no action is taken.</p>
	@param propertyNamespaceURI The XML namespace URI used in the serialization
		of the property resource that is the predicate of an RDF statement.
	@param propertyLocalName The XML local name used in the serialization of the
		property resource that is the predicate of an RDF statement.
	@param value A property value; the object of an RDF statement.
	@return The added property value.
	*/
	public RDFObject addProperty(final URI propertyNamespaceURI, final String propertyLocalName, final RDFObject value)
	{
		return addProperty(RDFUtilities.locateResource(this, propertyNamespaceURI, propertyLocalName), value); //create a property from the namespace URI and local name, then add the actual property value
	}

	/**Adds a plain literal property from a string by creating a <code>RDFPropertyValuePair</code>
		from the given property and value. For each property, this resource serves
		as the subject of an RDF statement with the property as the predicate and
		the value, stored as a literal, as the object. No language is specified.
		<p>Note that the property is not simply a property URI &mdash; it is a
		resource that is identified by the property URI.</p>
		<p>If an equivalent property already exists, no action is taken.</p>
	@param property A property resource; the predicate of an RDF statement.
	@param literalValue A literal property value that will be stored in a
		<code>RDFLiteral</code>; the object of an RDF statement.
	@return The added property value.
	*/
	public RDFLiteral addProperty(final RDFResource property, final String literalValue)
	{
		return addProperty(property, literalValue, null);	//add the plain literal with no language specified
	}

	/**Adds a plain literal property from a string by creating a <code>RDFPropertyValuePair</code>
		from the given property and value. This is a convenience method that allows specification
		of the property resource through a namespace URI and a local name.
		For each property, this resource serves as the subject of an RDF statement
		with the property as the predicate and the value, stored as a literal, as
		the object. No language is specified
		<p>Note that the property is not simply a property URI &mdash; it is a
		resource that is identified by the property URI.</p>
		<p>If an equivalent property already exists, no action is taken.</p>
	@param propertyNamespaceURI The XML namespace URI used in the serialization
		of the property resource that is the predicate of an RDF statement.
	@param propertyLocalName The XML local name used in the serialization of the
		property resource that is the predicate of an RDF statement.
	@param literalValue A literal property value that will be stored in a
		<code>RDFLiteral</code>; the object of an RDF statement.
	@return The added property value.
	*/
	public RDFLiteral addProperty(final URI propertyNamespaceURI, final String propertyLocalName, final String literalValue)
	{
		return addProperty(RDFUtilities.locateResource(this, propertyNamespaceURI, propertyLocalName), literalValue); //create a new literal value and add the property
	}

	/**Adds a plain literal property from a string by creating a <code>RDFPropertyValuePair</code>
		from the given property and value. For each property, this resource serves
		as the subject of an RDF statement with the property as the predicate and
		the value, stored as a literal, as the object.
		<p>Note that the property is not simply a property URI &mdash; it is a
		resource that is identified by the property URI.</p>
		<p>If an equivalent property already exists, no action is taken.</p>
	@param property A property resource; the predicate of an RDF statement.
	@param literalValue A literal property value that will be stored in a
		<code>RDFLiteral</code>; the object of an RDF statement.
	@param language The language of the plain literal, or <code>null</code> if
		no language should be specified.
	@return The added property value.
	*/
	public RDFLiteral addProperty(final RDFResource property, final String literalValue, final Locale language)
	{
		return (RDFLiteral)addProperty(property, new RDFPlainLiteral(literalValue, language)); //create a new literal value and store the property
	}

	/**Adds a plain literal property from a string by creating a <code>RDFPropertyValuePair</code>
		from the given property and value. This is a convenience method that allows specification
		of the property resource through a namespace URI and a local name.
		For each property, this resource serves as the subject of an RDF statement
		with the property as the predicate and the value, stored as a literal, as
		the object.
		<p>Note that the property is not simply a property URI &mdash; it is a
		resource that is identified by the property URI.</p>
		<p>If an equivalent property already exists, no action is taken.</p>
	@param propertyNamespaceURI The XML namespace URI used in the serialization
		of the property resource that is the predicate of an RDF statement.
	@param propertyLocalName The XML local name used in the serialization of the
		property resource that is the predicate of an RDF statement.
	@param literalValue A literal property value that will be stored in a
		<code>RDFLiteral</code>; the object of an RDF statement.
	@param language The language of the plain literal, or <code>null</code> if
		no language should be specified.
	@return The added property value.
	*/
	public RDFLiteral addProperty(final URI propertyNamespaceURI, final String propertyLocalName, final String literalValue, final Locale language)
	{
		return addProperty(RDFUtilities.locateResource(this, propertyNamespaceURI, propertyLocalName), literalValue, language); //create a new literal value and add the property
	}

	/**Removes the property with the given property URI and property value.
	@param propertyURI The reference URI of the property resource of the property to be removed.
	@param propertyvalue The value of the property to be removed.
	@exception NullPointerException if the given property URI and/or property value is <code>null</code>.
	*/
	public void removeProperty(final URI propertyURI, final RDFObject propertyValue)
	{
		int propertiesRemovedCount=0;	//we haven't removed any properties, yet
		final Iterator<RDFPropertyValuePair> propertyIterator=getPropertyIterator();  //get an iterator to look at the properties
		while(propertyIterator.hasNext()) //while there are more properties
		{
			final RDFPropertyValuePair propertyValuePair=propertyIterator.next(); //get the next name/value pair
			if(propertyURI.equals(propertyValuePair.getProperty().getReferenceURI()))  //if this resource is that identified by the property URI
			{
				if(propertyValue.equals(propertyValuePair.getValue()))	//if the value matches
				{
					propertyIterator.remove();	//remove this property
					firePropertyChange(propertyURI.toString(), propertyValuePair.getValue(), null);	//fire a property change event with the old property value
					++propertiesRemovedCount;	//show that we removed another property
				}
			}
		}
//TODO fix in a shared method		return propertiesRemovedCount;	//return the number of properties we removed
	}
	
	/**Removes the property with the given property URI and property value.
	@param namespaceURI The XML namespace URI that represents part of the reference URI of the property to be removed.
	@param localName The XML local name that represents part of the reference URI of the property to be removed.
	@param propertyValue The value of the property to be removed.
	@exception NullPointerException if the given property URI and/or property value is <code>null</code>.
	*/
	public void removeProperty(final URI namespaceURI, final String localName, final RDFObject propertyValue)
	{
		removeProperty(RDFUtilities.createReferenceURI(namespaceURI, localName), propertyValue); //remove the property, combining the namespace URI and the local name for the reference URI
	}

	/**Removes all properties with the given URI.
	@param propertyURI The reference URI of the property resource of the
		properties to be removed.
	@return The number of properties removed.
	*/
	public int removeProperties(final URI propertyURI)
	{
		int propertiesRemovedCount=0;	//we haven't removed any properties, yet
		final Iterator<RDFPropertyValuePair> propertyIterator=getPropertyIterator();  //get an iterator to look at the properties
		while(propertyIterator.hasNext()) //while there are more properties
		{
			final RDFPropertyValuePair propertyValuePair=propertyIterator.next(); //get the next name/value pair
			if(propertyURI.equals(propertyValuePair.getProperty().getReferenceURI()))  //if this resource is that identified by the property URI
			{
				propertyIterator.remove();	//remove this property
				firePropertyChange(propertyURI.toString(), propertyValuePair.getValue(), null);	//fire a property change event with the old property value
				++propertiesRemovedCount;	//show that we removed another property
			}
		}
		return propertiesRemovedCount;	//return the number of properties we removed
	}
	
	/**Removes all properties with the given URI.
	@param namespaceURI The XML namespace URI that represents part of the
		reference URI of the properties to be removed.
	@param localName The XML local name that represents part of the reference URI
		of the properties to be removed.
	@return The number of properties removed.
	*/
	public int removeProperties(final URI namespaceURI, final String localName)
	{
		return removeProperties(RDFUtilities.createReferenceURI(namespaceURI, localName)); //remove the property, combining the namespace URI and the local name for the reference URI
	}

	/**Sets a property by removing all property values for the given property and
		creating a new <code>RDFPropertyValuePair</code> from the given
		property and value. If no value is given, all such properties are removed.
	@param property A property resource&mdash;the predicate of an RDF statement.
	@param value A property value&mdash;the object of an RDF statement&mdash;or
		<code>null</code> if all such properties should be removed with nothing
		to replace them.
	@return The added property value.
	@see #removeProperties(URI)
	@see #addProperty(RDFResource, RDFObject)
	*/
	public RDFObject setProperty(final RDFResource property, final RDFObject value)
	{
		removeProperties(property.getReferenceURI());	//remove all existing object values for this property
		if(value!=null)	//if there is a value to add
			return addProperty(property, value);	//add the given property and value
		else	//if no value was given
			return null;	//show that no value was added
	}

	/**Sets a property by first removing all such properties and then adding
		a new property. If no value is given, all such properties are removed.
	@param propertyNamespaceURI The XML namespace URI used in the serialization
		of the property resource that is the predicate of an RDF statement.
	@param propertyLocalName The XML local name used in the serialization of the
		property resource that is the predicate of an RDF statement.
	@param value A property value&mdash;the object of an RDF statement&mdash;or
		<code>null</code> if all such properties should be removed with nothing
		to replace them.
	@return The added property value.
	*/
	public RDFObject setProperty(final URI propertyNamespaceURI, final String propertyLocalName, final RDFObject value)
	{
		return setProperty(RDFUtilities.locateResource(this, propertyNamespaceURI, propertyLocalName), value); //create a property from the namespace URI and local name, then set the actual property value
	}

	/**Sets a property by first removing all such properties and then adding
		a new property. If no value is given, all such properties are removed.
	@param propertyURI The reference URI of the property resource.
	@param value A property value&mdash;the object of an RDF statement&mdash;or
		<code>null</code> if all such properties should be removed with nothing
		to replace them.
	@return The added property value.
	*/
	public RDFObject setProperty(final URI propertyURI, final RDFObject value)
	{
		return setProperty(RDFUtilities.locateResource(this, propertyURI), value); //create a property from the property URI, then set the actual property value
	}

	/**Sets a plain literal property from a string by removing all property values
		for the given property and creating a new <code>RDFPropertyValuePair</code>
		from the given property and value. No language is specified.
	If no value is given, all such properties are removed.
	@param property A property resource; the predicate of an RDF statement.
	@param literalValue A literal property value that will be stored in a
		<code>RDFLiteral</code>; the object of an RDF statement.
	@return The added property value.
	@see #removeProperties(URI)
	@see #addProperty(RDFResource, String)
	*/
	public RDFLiteral setProperty(final RDFResource property, final String literalValue)
	{
		return setProperty(property, literalValue, null);	//set the property without specifying a language
	}

	/**Sets a plain literal property from a string by first removing all such
		properties and then adding a new property. No language is specified.
	If no value is given, all such properties are removed.
	@param propertyNamespaceURI The XML namespace URI used in the serialization
		of the property resource that is the predicate of an RDF statement.
	@param propertyLocalName The XML local name used in the serialization of the
		property resource that is the predicate of an RDF statement.
	@param literalValue A literal property value that will be stored in a
		<code>RDFLiteral</code>; the object of an RDF statement.
	@return The added property value.
	*/
	public RDFLiteral setProperty(final URI propertyNamespaceURI, final String propertyLocalName, final String literalValue)
	{
		return setProperty(RDFUtilities.locateResource(this, propertyNamespaceURI, propertyLocalName), literalValue); //create a new literal value and set the property
	}

	/**Sets a plain literal property from a string by removing all property values
		for the given property and creating a new <code>RDFPropertyValuePair</code>
		from the given property and value.
	If no value is given, all such properties are removed.
	@param property A property resource; the predicate of an RDF statement.
	@param literalValue A literal property value that will be stored in a
		<code>RDFLiteral</code>; the object of an RDF statement.
	@param language The language of the plain literal, or <code>null</code> if
		no language should be specified.
	@return The added property value.
	@see #removeProperties(URI)
	@see #addProperty(RDFResource, String)
	*/
	public RDFLiteral setProperty(final RDFResource property, final String literalValue, final Locale language)
	{
		removeProperties(property.getReferenceURI());	//remove all existing object values for this property
		if(literalValue!=null)	//if there is a value to add
			return addProperty(property, literalValue, language);	//add the given property and value
		else	//if no value was given
			return null;	//show that no value was added
	}

	/**Sets a plain literal property from a string by first removing all such
		properties and then adding a new property.
	If no value is given, all such properties are removed.
	@param propertyNamespaceURI The XML namespace URI used in the serialization
		of the property resource that is the predicate of an RDF statement.
	@param propertyLocalName The XML local name used in the serialization of the
		property resource that is the predicate of an RDF statement.
	@param literalValue A literal property value that will be stored in a
		<code>RDFLiteral</code>; the object of an RDF statement.
	@param language The language of the plain literal, or <code>null</code> if
		no language should be specified.
	@return The added property value.
	*/
	public RDFLiteral setProperty(final URI propertyNamespaceURI, final String propertyLocalName, final String literalValue, final Locale language)
	{
		return setProperty(RDFUtilities.locateResource(this, propertyNamespaceURI, propertyLocalName), literalValue, language); //create a new literal value and set the property
	}

	/**Default constructor that creates a resource without a reference URI.
	@see RDF#createResource
	*/
	public DefaultRDFResource()
	{
		this((URI)null);	//create a resource without a reference URI
	}

	/**Constructs a resource with a reference URI.
	@param referenceURI The reference URI for the new resource.
	@see RDF#createResource
	*/
	public DefaultRDFResource(final URI referenceURI)
	{
		this(null, referenceURI);	//construct the class with no data model
	}

	/**Constructs a resource from a data model.
	@param rdf The data model with which this resource should be associated.
	*/
	public DefaultRDFResource(final RDF rdf)
	{
		this(rdf, null);	//construct the resource with no reference URI
	}

	/**Convenience constructor that constructs a resource using a namespace URI
		and local name which will be combined to form the reference URI.
	@param newNamespaceURI The XML namespace URI used in the serialization.
	@param newLocalName The XML local name used in the serialization.
	@see RDF#createResource
	*/
	public DefaultRDFResource(final URI newNamespaceURI, final String newLocalName)
	{
		this(null, newNamespaceURI, newLocalName);  //do the default construction, combining the namespace URI and the local name for the reference URI
	}

	/**Convenience constructor that constructs a resource using a namespace URI
		and local name which will be combined to form the reference URI.
	@param rdf The data model with which this resource should be associated.
	@param newNamespaceURI The XML namespace URI used in the serialization.
	@param newLocalName The XML local name used in the serialization.
	@see RDF#createResource
	*/
	public DefaultRDFResource(final RDF rdf, final URI newNamespaceURI, final String newLocalName)
	{
		this(rdf, RDFUtilities.createReferenceURI(newNamespaceURI, newLocalName));  //do the default construction, combining the namespace URI and the local name for the reference URI
	}

	/**Constructs a resource with a reference URI from a data model.
	@param rdf The data model with which this resource should be associated.
	@param referenceURI The reference URI for the new resource.
	@see RDF#createResource
	*/
	public DefaultRDFResource(final RDF rdf, final URI referenceURI)
	{
		super(referenceURI);  //construct the parent class with the reference URI
		setRDF(rdf);	//associate the resource with the given RDF data model, if any
	}

	/**Returns a hash code value for the resource. 
		If this resource has a reference URI, the default hash code is returned
		(i.e. the hash code of the refeference URI). If this resource has no
		reference URI, the number of properties is returned.
	@return The hash code of the reference URI, or the number of properties if
		this resource has no reference URI.
	*/
/*TODO important find out if we need this, and what will work best; this may have been added so that RDF resources would work well in hash maps, and this may have been required
for some reason, this method as listed is crucial for determining if two resources with values of boolean:true are equal; find out why this hack fixes that, and what should be done to make it work
*/ 
	public int hashCode()
	{
		return getReferenceURI()!=null ? super.hashCode() : getPropertyCount();	//return the normal hash code if we have a reference URI, or the number of properties if we have no reference URI 
	}

	/**If this resource has a reference URI, compares using the superclass
		functionality (comparing reference URIs). Otherwise, if the other object
		is also an RDF resource, compares properties.
	@param object The object with which to compare this RDF resource; should be
		another resource.
	@return <code>true<code> if this resource equals that specified in
		<code>object</code>.
	@see #getReferenceURI
	@see RDFUtilities#getValue(RDFResource)
	*/
	public boolean equals(final Object object)
	{
		if(getReferenceURI()==null && object instanceof RDFResource)	//if we don't have a reference URI, and the other object is an RDF resource
		{
			//TODO compare all properties; right now, we only compare rdf:value so that other code will work
			final RDFResource rdfResource=(RDFResource)object;	//cast the other object to an RDF resource
			final RDFObject value1=RDFUtilities.getValue(this);	//get our rdf:value
			if(value1!=null)	//if we have a value
			{
				return value1.equals(RDFUtilities.getValue(rdfResource));	//compare our rdf:value with the other rdf:value			
				
			}
		}
		return super.equals(object);	//if we have a reference URI or the other object isn't an RDF resource, do a default comparison (usually using reference URIs)
	}

	/**@return A copy of this resource with the same URI and identical properties.*/
	public Object clone()
	{
		try
		{ 
			DefaultRDFResource resource=(DefaultRDFResource)super.clone();	//create a cloned copy of this resource
			resource.propertyList=(ArrayList<RDFPropertyValuePair>)propertyList.clone();	//clone the property list
			return resource;	//return the cloned resource
		}
		catch(CloneNotSupportedException e)
		{ 
			throw new AssertionError("Cloning is unexpectedly not supported.");
		}
	}

	/**Compares this resource to another resource.
	<p>This version attempts to compare resource labels, if the object is an
		RDF resource; otherwise, the default ordering is used based upon the
		reference URI of the resource, if any.</p>
	@param resource The resource with which to compare this resource. This must be
		another <code>Resource</code> object.
	@return A negative integer, zero, or a positive integer as this resource
		reference URI is less than, equal to, or greater than the reference URI of
		the specified resource, respectively.
	@see RDFSUtilities#getLabel(RDFResource)
	*/	//TODO fix compare and Comparable; maybe remove, and use custom Comparable elsewhere (as in Collections.sort())
	public int compareTo(final RDFResource resource)	//G***is it correct to compare on different things? will this violate comparison rules? (e.g. two RDFResources with labels may compare differently than each of them compared against a normal Resource)
	{
//TODO del when works		if(resource instanceof RDFResource)	//if this resource is an RDF resource
		{
			final RDFLiteral label1=RDFSUtilities.getLabel(this);	//see if this resource has a label
			final RDFLiteral label2=RDFSUtilities.getLabel((RDFResource)resource);	//see if there is a label for the other resource
			if(label1!=null && label2!=null)	//if we have two labels to compare
			{
				return label1.compareTo(label2);	//compare labels
			}
		}
		return super.compareTo(resource);	//if all else fails, do a default comparison
	}

	/**Returns a string representation of the resource.
	<p>This implementation returns the reference URI, if any, along with the
		lexical form of the literal <code>rdf:value</code> property value or the <code>dc:title</code>, if any.
		If this resource has neither a reference URI or a literal
		<code>rdf:value</code> property value, the default representation will be
		used.</p> 
	@return A string representation of the resource.
	@see RDFUtilities#getValue(RDFResource)
	*/
	public String toString()
	{
		final StringBuilder stringBuilder=new StringBuilder();	//create a string buffer in which to construct a string representation
		final RDFLiteral value=getValue(this);	//get the value property, if any
/*TODO del if not wanted
		if(value==null)	//if there is no value, try to get the dc:title
		{
			value=asLiteral(getTitle(this));	//get the title and use it if it is a literal
		}
*/
		final RDFXMLGenerator rdfXMLGenerator=new RDFXMLGenerator();	//create a new RDF XML generator TODO use a common instance
		final URI referenceURI=getReferenceURI();	//get the reference URI
		if(referenceURI!=null || value==null)	//if we have a reference URI and/or no value
		{
			if(referenceURI!=null)	//if we have a reference URI
			{
				stringBuilder.append(rdfXMLGenerator.getLabel(getReferenceURI()));	//start with the default string
			}
			else	//if we have no reference URI
			{
				stringBuilder.append(super.toString());	//start with the default string				
			}
		}
		final RDFResource type=getType(this);	//get this resource's type
		if(type!=null)	//if there is a type
		{
			final URI typeURI=type.getReferenceURI();	//get the type URI
			if(typeURI!=null)	//if there is a type given
			{
				if(stringBuilder.length()>0)	//if we added something to the string builder already
				{
					stringBuilder.append(' ');	//separate the other information and the type
				}
				stringBuilder.append('(').append(rdfXMLGenerator.getLabel(typeURI)).append(')'); //append "(type)"
			}
		}
		if(value!=null)	//if we have a value
		{
			if(stringBuilder.length()>0)	//if we added something to the string builder already
			{
				stringBuilder.append(':').append(' ');	//separate the other information and the value
			}
			stringBuilder.append(value.getLexicalForm());	//append the lexical form of the resource
		}
		if(stringBuilder.length()>0)	//if we've gathered anything at all
		{
			return stringBuilder.toString();	//return the string we constructed
		}
		else	//if we have nothing to return
		{
			return super.toString();	//return the default representation
		}
	}

}