package com.garretwilson.rdf;

import java.net.*;
import java.util.Locale;
import com.garretwilson.net.*;
import com.garretwilson.text.xml.XMLBase;
import com.garretwilson.text.xml.XMLUtilities;
import com.garretwilson.text.xml.XMLConstants;
import com.garretwilson.util.Debug;
import com.garretwilson.util.LocaleUtilities;
import org.w3c.dom.*;

/**Class that is able to construct an RDF data model from an XML-based
	RDF serialization. Each instance of an RDF processor maintains an internal
	RDF data model throughout its lifetime that is continually updated with
	every new RDF processing that occurs.
	<p>The RDF processor maintains RDF data in two separate formats: the RDF
	data model <code>RDF</code>, as well as a list of statements used to create
	the data model. The RDF data model may be replaced and its members modified,
	but these actions will not update the list of RDF statements. The RDF
	statements are only generated by the RDF processor itself as it parses
	RDF serializations, and are available to give information on the parser
	actions.</p>
	TODO If a property from a non-RDF namespace has (for example) a "resource" attribute (i.e. a property with no namespace), property resources are not correctly created and can cause endless loops when trying to analyze the namespace
@author Garret Wilson
*/
public class RDFXMLProcessor extends AbstractRDFProcessor implements RDFConstants, RDFXMLConstants
{

	//constants for parseAttributeProperties()
	/**An attribute in the context of a resource description (<code>&lt;rdf:Description&gt;</code>).*/
	protected static final int DESCRIPTION_CONTEXT=1;
	/**An attribute in the context of a resource reference (<code>reference="&hellip;"</code>).*/
	protected static final int REFERENCE_CONTEXT=2;
	/**An attribute in the context of a property reference with only attributes and no children.*/
	protected static final int EMPTY_PROPERTY_CONTEXT=3;
	/**An attribute in the context of a reference short form (<code>parse=Type"Resource"</code>).*/
	protected static final int PROPERTY_AND_NODE_CONTEXT=4;

	/**Default constructor.*/
	public RDFXMLProcessor()
	{
		super();  //construct parent base class
	}

	/**Constructor that specifies an existing data model to continue filling.
	@param newRDF The RDF data model to use.
	*/
	public RDFXMLProcessor(final RDF newRDF)
	{
		super(newRDF);  //construct the parent class
	}

	/**Processes RDF serialized in an XML document. Processes data contained in
		every <code>&lt;rdf:RDF&gt;</code> data island.
	@param document The XML document that might contain RDF data.
	@return The RDF data model resulting from this processing and any previous
		processing.
	@exception URISyntaxException Thrown if a URI is syntactically incorrect.
	*/
	public RDF process(final Document document) throws URISyntaxException
	{
		return process(document, null);  //process this document with no base URI specified
	}

	/**Processes RDF serialized in an XML document. Processes data contained in
		every <code>&lt;rdf:RDF&gt;</code> data island.
	@param document The XML document that might contain RDF data.
	@param baseURI The base URI, or <code>null</code> if the base URI is not
		known.
	@return The RDF data model resulting from this processing and any previous
		processing.
	@exception URISyntaxException Thrown if a URI is syntactically incorrect.
	*/
	public RDF process(final Document document, final URI baseURI) throws URISyntaxException
	{
		return process(document.getDocumentElement(), baseURI); //process the data in the document element
	}

	/**Processes RDF serialized in an XML document. Searches the given element and
		all its children, processing data contained in every
		<code>&lt;rdf:RDF&gt;</code> data island.
	@param element The XML element that might contain RDF data.
	@return The RDF data model resulting from this processing and any previous
		processing.
	@exception URISyntaxException Thrown if a URI is syntactically incorrect.
	*/
	public RDF process(final Element element) throws URISyntaxException
	{
		return process(element, null);  //process this element without specifying a base URI
	}


	/**Processes RDF serialized in an XML document. Searches the given element and
		all its children, processing data contained in every
		<code>&lt;rdf:RDF&gt;</code> data island.
	@param element The XML element that might contain RDF data.
	@param baseURI The base URI, or <code>null</code> if the base URI is not
		known.
	@return The RDF data model resulting from this processing and any previous
		processing.
	@exception URISyntaxException Thrown if a URI is syntactically incorrect.
	*/
	public RDF process(final Element element, final URI baseURI) throws URISyntaxException
	{
		setBaseURI(baseURI);  //set the base URI
		if(RDF_NAMESPACE_URI.toString().equals(element.getNamespaceURI()) //if this element is in the RDF namespace G***fix better
			  && ELEMENT_RDF.equals(element.getLocalName())) //if this element indicates that the children are RDF
		{
			final NodeList childNodeList=element.getChildNodes(); //get a list of child nodes
			for(int i=0; i<childNodeList.getLength(); ++i)  //look at each child node
			{
				final Node childNode=childNodeList.item(i); //get a reference to this child node
				if(childNode.getNodeType()==Node.ELEMENT_NODE) //if this is an element
				{
					processRDF((Element)childNode);  //parse the contents of the RDF container element
				}
			}
		}
		else  //if this is a normal, non-RDF node
		{
			final NodeList childNodeList=element.getChildNodes(); //get a list of child nodes
			for(int i=0; i<childNodeList.getLength(); ++i)  //look at each child node
			{
				final Node childNode=childNodeList.item(i); //get a reference to this child node
				if(childNode.getNodeType()==Node.ELEMENT_NODE) //if this is an element
				{
					process((Element)childNode);  //parse the contents of the element, not knowing if this is an RDF element or not
				}
			}
		}
		return getRDF();  //return the RDF data collected
	}

	/**Processes the given element categorically as RDF, as if it were contained
		in an <code>&lt;rdf:RDF&gt;</code> element.
	@param element The XML element that represents RDF data.
	@exception URISyntaxException Thrown if a URI is syntactically incorrect.
	*/
	public void processRDF(final Element element) throws URISyntaxException //G***where is this used? does it really need to be public? if so, it probably needs a baseURI parameter version
	{
		//G***what do we do if <rdf:RDF> occurs inside <rdf:RDF>?
		processResource(element); //process the given element as an RDF resource
	}

	/**Processes the given element as representing an RDF resource.
	@param element The XML element that represents the RDF resource.
	@return The constructed resource the XML element represents.
	@exception URISyntaxException Thrown if a URI is syntactically incorrect.
	*/
	protected RDFResource processResource(final Element element) throws URISyntaxException
	{
		final URI elementNamespaceURI=element.getNamespaceURI()!=null ? new URI(element.getNamespaceURI()) : null; //get the element's namespace, or null if the element has no namespace
		final String elementLocalName=element.getLocalName(); //get the element's local name
//G***del Debug.trace("processing resource with XML element namespace: ", elementNamespaceURI); //G***del
//G***del Debug.trace("processing resource with XML local name: ", elementLocalName); //G***del
		//G***what do we do if <rdf:RDF> occurs inside <rdf:RDF>?
		/*G***bring back final */URI referenceURI;  //we'll determine the reference URI from the rdf:about or rdf:ID attribute
		{
			final String referenceURIValue=getRDFAttribute(element, ATTRIBUTE_ABOUT); //get the reference URI, if there is one
			final String anchorID=getRDFAttribute(element, ATTRIBUTE_ID); //get the anchor ID if there is one
		  Debug.assert(referenceURIValue==null || anchorID==null, "Resource cannot have both reference URI "+referenceURIValue+" and anchor ID "+anchorID+"."); //G***change to an actual RDF error
			if(referenceURIValue!=null) //if there is a reference URI
			{
//G***del Debug.trace("found reference URI: ", referenceURIValue);  //G***del
//G***del				//G***we need to normalize the reference URI
				referenceURI=XMLBase.resolveURI(new URI(referenceURIValue), element, getBaseURI());  //resolve the reference URI to the base URI
			}
			else if(anchorID!=null)  //if there is an anchor ID
			{
//G***del Debug.trace("found anchor ID: ", anchorID);  //G***del
				referenceURI=new URI(XMLBase.getBaseURI(element, getBaseURI()).toString()+URIConstants.FRAGMENT_SEPARATOR+anchorID);  //create a reference URI from the document base URI and the anchor ID	//G***make better with new URI methods
			}
			else  //if there is neither a resource ID nor an anchor ID
			{
				referenceURI=null;  //this is a blank node
			}
		}
//G***del Debug.trace("resulting reference URI: ", referenceURI);  //G***del
//G***del		final String anchorID=getRDFAttribute(element, ATTRIBUTE_ID); //get the anchor ID, if there is one
		final RDFResource resource;  //we'll create a resource and store it here
//G***del when works		final Resource resource=getRDF().locateResource(referenceURI); //get or create a new resource with the given reference URI
		  //if this is an <rdf:Description> element
		if(RDF_NAMESPACE_URI.equals(elementNamespaceURI) && ELEMENT_DESCRIPTION.equals(elementLocalName))
		{
//G***del Debug.trace("Is rdf:Description."); //G***del
			resource=getRDF().locateResource(referenceURI); //get or create a new resource with the given reference URI
		}
		else  //if this is not an <rdf:Description> element, we already know its type (but it may not have been added if the resource was created earlier without a type)
		{
//G***del Debug.trace("locating a resource with reference URI: ", referenceURI); //G***del
			  //get or create a new resource with the given reference URI and type;
				//  this allows a resource factory to create the appropriate type of
				//  resource object
			resource=getRDF().locateResource(referenceURI, elementNamespaceURI, elementLocalName);
/*G***del when works
			final Resource typeProperty=RDFUtilities.getTypeProperty(getRDF()); //get a rdf:type resource G***maybe create this beforehand somewhere
			final Resource typeValue=getRDF().locateResource(elementNamespaceURI, elementLocalName);  //get a resource for the value of the property
			resource.addProperty(typeProperty, typeValue);  //add the property to the resource
*/
				  //G***we need to just use the RDFUtilities to get the type, probably, to replace this early version
			final RDFResource typeProperty=RDFUtilities.locateTypeProperty(getRDF()); //get the rdf:type resource
//G***del Debug.trace("type property: ", typeProperty); //G***del
			RDFObject typeValue=resource.getPropertyValue(typeProperty); //get the value of the type that was added when the object was created
//G***del Debug.trace("type value: ", typeValue); //G***del
		  if(typeValue==null) //if the resource has already been created, but it does not have this type
			{
			  typeValue=getRDF().createResource(elementNamespaceURI, elementLocalName); //create a type value resource
			}
				//add a statement to our data model in the form {rdf:type, resource, elementName}
				//this will in most cases attempt to add the type value again, if it
				//  was constructed automatically when the resource was created, but
				//  the property will be seen as a duplicate and ignored
		  addStatement(typeProperty, resource, typeValue);
		}
		processAttributeProperties(resource, element, DESCRIPTION_CONTEXT);  //parse the attributes for the resource description
		processChildElementProperties(resource, element);	//parse the child elements as properties
		return resource;  //return the resource we created
	}

	/**Parses the child elements of the given element and assign them as
	  properties to the given resource.
	@param resource The resource to which the properties should be added.
	@param element The element that contains the attributes to be considered
		properties.
	@exception URISyntaxException Thrown if a URI is syntactically incorrect.
	*/
	protected void processChildElementProperties(final RDFResource resource, final Element element) throws URISyntaxException
	{
		final URI RDF_LI_REFERENCE_URI=RDFUtilities.createReferenceURI(RDF_NAMESPACE_URI, ELEMENT_LI);  //create a reference URI from the rdf:li element qualified name G***maybe make this static somewhere
		int memberCount=0; //show that we haven't found any container members, yet
		final NodeList childNodeList=element.getChildNodes(); //get a list of child nodes
		for(int i=0; i<childNodeList.getLength(); ++i)  //look at each child node
		{
			final Node childNode=childNodeList.item(i); //get a reference to this child node
			if(childNode.getNodeType()==Node.ELEMENT_NODE) //if this is an element
			{
				final RDFPropertyValuePair propertyValuePair=processProperty((Element)childNode);  //parse the element representing an RDF property
				final RDFResource property;  //we'll see whether we should convert <rdf:li>
				if(RDF_LI_REFERENCE_URI.equals(propertyValuePair.getProperty()))  //if this is a rdf:li property
				{
					++memberCount;  //show that we have another member
					//create a local name in the form "_X"
					final String propertyLocalName=CONTAINER_MEMBER_PREFIX+memberCount;
					property=getRDF().locateResource(RDF_NAMESPACE_URI, propertyLocalName); //use the revised member form as the property
				}
				else  //if this is a normal property
					property=(RDFResource)propertyValuePair.getProperty(); //just use the property as is
					//add a statement to our data model in the form {property, resource, value}
				addStatement(property, resource, propertyValuePair.getPropertyValue());
			}
		}
	}

	/**Parses the attributes of the given element and assign them as properties
	  to the given resource. Special RDF properties such as <code>rdf:about</code>
		are ignored.
	@param resource The resource to which the properties should be added.
	@param element The element that contains the attributes to be considered
		properties.
	@param context The context, <code>DESCRIPTION_CONTEXT</code>,
		<code>REFERENCE_CONTEXT</code>, <code>PROPERTY_AND_NODE_CONTEXT</code>,
		or <code>EMPTY_PROPERTY_CONTEXT</code>, describing whether the attributes
		are part of a resource description, a resource reference, or a reference to
		a blank node resource in short form, or the creation of a blank node from
		an empty property element, respectively.
	@exception URISyntaxException Thrown if an RDF URI is syntactically incorrect.
	*/
	protected void processAttributeProperties(final RDFResource resource, final Element element, final int context) throws URISyntaxException
	{
		final URI elementNamespaceURI=element.getNamespaceURI()!=null ? new URI(element.getNamespaceURI()) : null; //get the element's namespace, or null if there is no namespace URI
		final NamedNodeMap attributeNodeMap=element.getAttributes();  //get a map of the attributes
		for(int i=attributeNodeMap.getLength()-1; i>=0; --i)  //look at each of the attributes
		{
			final Attr attribute=(Attr)attributeNodeMap.item(i);  //get a reference to this attribute
		  final URI attributeNamespaceURI=attribute.getNamespaceURI()!=null ? new URI(attribute.getNamespaceURI()) : null; //get the attribute's namespace URI, or null if it has no namespace URI
			final String attributePrefix=attribute.getPrefix(); //get the attribute's prefix
		  final String attributeLocalName=attribute.getLocalName(); //get the attribute's local name
		  final String attributeValue=attribute.getValue(); //get the attribute's value
/*G***del
Debug.trace("processing attribute from namespace: ", attributeNamespaceURI);
Debug.trace("processing attribute from local name: ", attributeLocalName);
Debug.trace("processing attribute from value: ", attributeValue);
*/
				//ignore attributes with the "xmlns" prefix or in the xmlns namespace
			if(XMLConstants.XMLNS_NAMESPACE_PREFIX.equals(attributePrefix) || XMLConstants.XMLNS_NAMESPACE_URI.equals(attributeNamespaceURI))
			{
				continue;
			}
				//process attributes with the "xml" prefix (or in the xml namespace) specially
			else if(XMLConstants.XML_NAMESPACE_PREFIX.equals(attributePrefix) || XMLConstants.XML_NAMESPACE_URI.equals(attributeNamespaceURI))
			{
					//TODO add support for xml:lang
				continue;
			}
			  //ignore the rdf:about attribute in descriptions, disallow it in references
			else if(isRDFAttribute(ATTRIBUTE_ABOUT, elementNamespaceURI, attributeNamespaceURI, attributeLocalName))
			{
				switch(context)	//only allow this attribute in certain contexts
				{
					case REFERENCE_CONTEXT:	//rdf:about isn't allowed in a reference
					case EMPTY_PROPERTY_CONTEXT:	//rdf:about isn't allowed in an empty property element
					case PROPERTY_AND_NODE_CONTEXT: 	//rdf:about isn't allowed in parseType="Resource"
						Debug.error("rdf:about attribute is not allowed in a resource reference."); //G***fix with real exceptions
						return;
					case DESCRIPTION_CONTEXT:	//ignore rdf:about in descriptions
					default:
						continue;
				}
			}
			  //ignore the rdf:ID attribute in descriptions, disallow it in references
			else if(isRDFAttribute(ATTRIBUTE_ID, elementNamespaceURI, attributeNamespaceURI, attributeLocalName))
			{
				switch(context)	//only allow this attribute in certain contexts
				{
					case REFERENCE_CONTEXT:	//rdf:ID isn't allowed in a reference
					case EMPTY_PROPERTY_CONTEXT:	//rdf:ID isn't allowed in an empty property element
					case PROPERTY_AND_NODE_CONTEXT: 	//rdf:ID isn't allowed in parseType="Resource"
						Debug.error("rdf:ID attribute is not allowed in a resource reference."); //G***fix with real exceptions
						return;
					case DESCRIPTION_CONTEXT:	//ignore rdf:ID in descriptions
					default:
						continue;
				}
			}
			  //ignore the rdf:parseType attribute in references, disallow it in descriptions
			else if(isRDFAttribute(ATTRIBUTE_PARSE_TYPE, elementNamespaceURI, attributeNamespaceURI, attributeLocalName))
			{
				switch(context)	//only allow this attribute in certain contexts
				{
					case DESCRIPTION_CONTEXT:	//rdf:parseType isn't allowed in a description
					case REFERENCE_CONTEXT:	//rdf:parseType isn't allowed in references
					case EMPTY_PROPERTY_CONTEXT:	//rdf:parseType isn't allowed in an empty property element
						Debug.error("rdf:parseType attribute is not allowed in a resource description."); //G***fix with real exceptions
						return;
					case PROPERTY_AND_NODE_CONTEXT: 	//ignore rdf:parseType in the property-and-node context (that's the attribute that defined this context, after all)
					default:
						continue;
				}
			}
			  //ignore the rdf:resource attribute in reference, disallow it in descriptions
			else if(isRDFAttribute(ATTRIBUTE_RESOURCE, elementNamespaceURI, attributeNamespaceURI, attributeLocalName))
			{
				switch(context)	//only allow this attribute in certain contexts
				{
					case DESCRIPTION_CONTEXT:	//rdf:resource isn't allowed in descriptions
					case EMPTY_PROPERTY_CONTEXT:	//rdf:resource isn't allowed in an empty property element
					case PROPERTY_AND_NODE_CONTEXT: 	//rdf:ID isn't allowed in parseType="Resource"
						Debug.error("rdf:resource attribute is not allowed in a resource reference."); //G***fix with real exceptions
						return;
					case REFERENCE_CONTEXT:	//rdf:resource isn't allowed in a reference
					default:
						continue;
				}
			}
			else	//for all other attributes
			{
				switch(context)	//only allow this attribute in certain contexts
				{
					case REFERENCE_CONTEXT:	//normal attributes are allowed for a normal reference
					case PROPERTY_AND_NODE_CONTEXT: 	//normal attributes isn't allowed in the parseType="Resource" context
						Debug.error(attribute.getName()+" attribute is not allowed in a property-and-node context."); //G***fix with real exceptions
						return;
					default:
							//add a statement to our data model in the form {attributeName, resource, attributeValue}
					  addStatement(getRDF().locateResource(attributeNamespaceURI, attributeLocalName), resource, new RDFPlainLiteral(attributeValue));
				}
			}
		}
	}

	/**Processes the given element as representing an RDF property.
	@param element The XML element that represents the RDF property.
	@return A name/value pair the name of which is a property resource (the RDF
		statement predicate) and the value of which is a value resource or a literal
		(the RDF statement object).
	@exception URISyntaxException Thrown if an RDF URI is syntactically incorrect.
	*/
	protected RDFPropertyValuePair processProperty(final Element element) throws URISyntaxException
	{
		final URI elementNamespaceURI=element.getNamespaceURI()!=null ? new URI(element.getNamespaceURI()) : null; //get the element's namespace, or null if there is no namespace URI
		final String elementLocalName=element.getLocalName(); //get the element's local name
//G***del Debug.trace("processing property with XML element namespace: ", elementNamespaceURI); //G***del
//G***del Debug.trace("processing property with XML local name: ", elementLocalName); //G***del
		final RDFResource propertyResource=getRDF().locateResource(elementNamespaceURI, elementLocalName); //get a resource from the element name
		final RDFObject propertyValue;  //we'll assign the property value to this variable

		final String parseType=getRDFAttribute(element, ATTRIBUTE_PARSE_TYPE); //get the parse type, if there is one
		if(COLLECTION_PARSE_TYPE.equals(parseType))	//if this is a collection
		{
			//G***we should make sure there are no other attributes
			RDFListResource list=null;	//we'll process each element of the list

//G***fix			RDFListResource listResource=RDFListResource.create();	//we'll process each element of the list
			RDFListResource lastElementListResource=null;	//we'll keep track of the last element so that we can update it to point to the new list element
				//parse the child elements
			final NodeList childNodeList=element.getChildNodes(); //get a list of child nodes
			for(int i=0; i<childNodeList.getLength(); ++i)  //look at each child node
			{
				final Node childNode=childNodeList.item(i); //get a reference to this child node
				if(childNode.getNodeType()==Node.ELEMENT_NODE) //if this is an element
				{
					final RDFResource elementValue=processResource((Element)childNode); //process the child element as an RDF resource
					final RDFListResource elementListResource=new RDFListResource(getRDF(), elementValue);	//create a list for this element
							//G***do we want to add the type here, or somewhere automatically in RDFListResource? probably here for constistency
							//G***also what about addStatement(typeProperty, resource, typeValue)? check the way resources are created and types added above
					RDFUtilities.addType(getRDF(), elementListResource, RDF_NAMESPACE_URI, LIST_TYPE_NAME);	//show that the list is of type rdf:List 
					if(list==null)	//if this is the first element in the list
					{
						list=elementListResource;	//store that as the start of the list
					}
					else if(lastElementListResource!=null)	//if this is not the first element in the list (this check is logically unnecessary)
					{
						RDFListResource.setRest(getRDF(), lastElementListResource, elementListResource);	//point the last list element to this new element
					}
					lastElementListResource=elementListResource;	//remember which element we processed the last time around
				}
			}
					//TODO what about setting the rdf:List type of the rdf:nil resource? where would we best do that?
			propertyValue=list!=null ? list : RDFUtilities.locateNilResource(getRDF());	//if we found no elements for the list, use the empty list resource
		}
		else if(RESOURCE_PARSE_TYPE.equals(parseType))	//if this is a resource as a property-and-node
		{
//G***make sure this works when we implement node IDs			  final RDFResource propertyValueResource=getRDF().locateResource(getRDF().createAnonymousReferenceURI());  //get or create a resource from a generated anonymous reference URI
			final RDFResource propertyValueResource=getRDF().createResource(null);  //create a blank node resource
			processAttributeProperties(propertyValueResource, element, PROPERTY_AND_NODE_CONTEXT);  //parse the property attributes, which will simply create errors if there are any unexpected attributes
			processChildElementProperties(propertyValueResource, element);	//parse the child elements as properties
		  propertyValue=propertyValueResource;  //the resource value is our property value
		}
		else if(LITERAL_PARSE_TYPE.equals(parseType))	//if this is an XMLLiteral
		{
			//TODO process the attributes to make sure there are no unexpected attributes
			final Document document=XMLUtilities.createDocument(element);	//create a new document from a copy of the given element
			//G***do we want to ensure namespace declarations?
			final Element documentElement=document.getDocumentElement();	//get a reference to the document element
			final DocumentFragment documentFragment=XMLUtilities.extractChildren(documentElement);	//extract the children of the document element to a document fragment
			propertyValue=new RDFXMLLiteral(documentFragment);	//create an XML literal containing the document fragment, which now contains a copy of the information of the XML tree below the given element
		}
		else	//by default assume that we're parsing a resource as the property value
		{
			final String referenceURIValue=getRDFAttribute(element, ATTRIBUTE_RESOURCE); //get the reference URI of the referenced resource, if there is one
	//G***del Debug.trace("RDF attribute: ", referenceURIValue);  //G***del
			if(referenceURIValue!=null) //if there is a reference URI
			{
				final URI referenceURI=XMLBase.resolveURI(referenceURIValue, element, getBaseURI());  //resolve the reference URI to the base URI
			  final RDFResource propertyValueResource=getRDF().locateResource(referenceURI); //get or create a new resource with the given reference URI
				processAttributeProperties(propertyValueResource, element, REFERENCE_CONTEXT);  //parse the property attributes, assigning them to the property value
			  propertyValue=propertyValueResource;  //the resource value is our property value
			}
			else if(element.getChildNodes().getLength()==0) //if there are no child elements, this is a blank node
			{
//G***make sure this works when we implement node IDs			  final RDFResource propertyValueResource=getRDF().locateResource(getRDF().createAnonymousReferenceURI());  //get or create a resource from a generated anonymous reference URI
				final RDFResource propertyValueResource=getRDF().createResource(null);  //get or create a blank node resource
				processAttributeProperties(propertyValueResource, element, EMPTY_PROPERTY_CONTEXT);  //parse the property attributes, assigning them to the property value
			  propertyValue=propertyValueResource;  //the resource value is our property value
			}
			else  //if there is no reference URI, there is either a normal property description below, or a literal
			{
				//G***we should make sure there are no attributes
				Element childElement=null; //show that we haven't found any child elements, yet
					//parse the child elements
				final NodeList childNodeList=element.getChildNodes(); //get a list of child nodes
				for(int i=0; i<childNodeList.getLength(); ++i)  //look at each child node
				{
					final Node childNode=childNodeList.item(i); //get a reference to this child node
					if(childNode.getNodeType()==Node.ELEMENT_NODE) //if this is an element
					{
						if(childElement==null)  //if we haven't already found a child element
						{
							childElement=(Element)childNode;  //cast the child node to an element
						}
						else if(childElement!=null)  //if we've already found a child element
						{
							Debug.warn("Only one property value allowed for "+propertyResource); //G***fix with real error handling
						}
					}
				}
				if(childElement!=null)  //if we found a child element for the property value
				{
					propertyValue=processResource(childElement); //process the child element as an RDF resource, the value of the property in this case
				}
				else  //if we didn't find any child elements, the content is a literal
				{
						//get the xml:lang language tag, if there is one
					final String languageTag=XMLUtilities.getDefinedAttributeNS(element, XMLConstants.XML_NAMESPACE_URI.toString(), XMLConstants.ATTRIBUTE_LANG);
						//create a locale for the language if there is a language tag
					final Locale languageLocale=languageTag!=null ? LocaleUtilities.createLocale(languageTag) : null;
					propertyValue=new RDFPlainLiteral(XMLUtilities.getText(element, false));  //create a literal from the element's text
				}
			}
		}
//G***del Debug.trace("returning name/value pair: ", new NameValuePair(propertyResource, propertyValue)); //G***del
		return new RDFPropertyValuePair(propertyResource, propertyValue);  //return a name/value pair consisting of the property resource (the predicate) and its value (the object)
	}

	/**Determines if an element attribute is an RDF attribute, recognizing
		either prefixed or non-prefixed attributes.
	@param rdfAttributeLocalName The RDF attribute name to check for. 
	@param elementNamespaceURI The namespace of the element to which the
		attribute belongs.
	@param attributeNamespaceURI namespace of the RDF attribute.
	@param attributeLocalName The local name of the RDF attribute.
	@return <code>true</code> if this is the specified RDF attribute.
	*/
	protected boolean isRDFAttribute(final String rdfAttributeLocalName, final URI elementNamespaceURI, final URI attributeNamespaceURI, final String attributeLocalName)
	{
		if(rdfAttributeLocalName.equals(attributeLocalName))	//if the attribute has the correct local name
		{
			if(RDF_NAMESPACE_URI.equals(attributeNamespaceURI) || (attributeNamespaceURI==null && RDF_NAMESPACE_URI.equals(elementNamespaceURI)))
			{
				return true;	//show that this is the expected RDF attribute
			}
		}
		return false;	//show that this attribute is not the RDF attribute expected
	}

	/**Retrieves an RDF attribute from an element, if it exists, recognizing
		either prefixed or non-prefixed attributes. If the non-prefixed form is
		used, a warning is generated.
		It is assumed that this method will only be called once for a particular
		attribute, as each call could produce another warning.
	@param element The element being checked for attributes.
	@param attributeLocalName The local name of the RDF attribute to check for.
	@return The specified RDF attribute.
	*/
	protected String getRDFAttribute(final Element element, final String attributeLocalName)
	{
		if(element.hasAttributeNS(RDF_NAMESPACE_URI.toString(), attributeLocalName))  //if there is a prefixed attribute value
		{
		  return element.getAttributeNS(RDF_NAMESPACE_URI.toString(), attributeLocalName); //get the prefixed attribute value
		}
		else if(element.getNamespaceURI()!=null && element.getNamespaceURI().equals(RDF_NAMESPACE_URI.toString()) && element.hasAttributeNS(null, attributeLocalName)) //if there is a non-prefixed attribute value
		{
			Debug.warn("Non-prefixed rdf:"+attributeLocalName+" attribute deprecated."); //G***put in a real warning
		  return element.getAttributeNS(null, attributeLocalName); //return the non-prefixed attribute value
		}
		else  //if neither attribute is available
			return null;  //show that the RDF attribute is not available
	}

}