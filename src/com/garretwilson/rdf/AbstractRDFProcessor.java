package com.garretwilson.rdf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import com.garretwilson.lang.JavaConstants;
import com.garretwilson.net.DefaultResource;
import com.garretwilson.net.Resource;

import static com.garretwilson.rdf.RDFConstants.*;
import static com.garretwilson.rdf.RDFUtilities.*;
import com.garretwilson.util.Debug;

/**Base class for RDF processors.
	Each instance of an RDF processor maintains an internal
	RDF data model throughout its lifetime that is continually updated with
	every new RDF processing that occurs.
<p>The RDF processor maintains RDF data in two separate formats: the RDF
	data model <code>RDF</code>, as well as a list of statements used to create
	the data model. The RDF data model may be replaced and its members modified,
	but these actions will not update the list of RDF statements. The RDF
	statements are only generated by the RDF processor itself as it parses
	RDF serializations, and are available to give information on the parser
	actions.</p>
<p>An RDF processor takes the following steps:</p>
<ol>
	<li>All literals are created and stored in statements.</li>
	<li>All typed resources are created and stored in the RDF data model and
		in statements.</li>
	<li>After processing is ended, all untyped subjects and predicates that
		have not yet been created are created, preferably by using any type
		that has become available through processing the RDF instance.</li>
	<li>All statements are processed, connecting the resources and their
		properties.</li>
</ol>
@author Garret Wilson
*/
public abstract class AbstractRDFProcessor
{

	/**The RDF data model that is filled by the processor.*/
	private RDF rdf;

		/**@return The RDF data model being constructed by the RDF processor.*/
		public RDF getRDF() {return rdf;}

//TODO del when works		/**Sets the RDF data model, resets the statement list, and updates the base URI to that of the RDF.

		/**Sets the RDF data model.
		The list of statements is reset.
		@param newRDF The RDF data model to use.
		*/
		public void setRDF(final RDF newRDF)
		{
			rdf=newRDF; //set the RDF data model
//TODO del when works			setBaseURI(rdf.getBaseURI());	//use the same base URI
			statementSet.clear();  //clear the set of statements
		}

	/**The base URI of the RDF being processed, or <code>null</code> if
		no base URI was specified.
	*/
	private URI baseURI=null;

		/**Sets the base URI for the RDF being processed.
		@param newBaseURI The base URI of the RDF being processed, or
			<code>null</code> if no base URI was specified.
		*/
		public void setBaseURI(final URI newBaseURI)
		{
			baseURI=newBaseURI; //set the base URI
		}

		/**@return The base URI of the RDF being processed, or "online:" if no
			base URI is known.
		*/
		public URI getBaseURI()
		{
			return baseURI!=null ? baseURI : URI.create("online:/");	//return the base URI if we know it	//TODO use a constant
		}

	/**The map of resource proxies keyed to reference URIs.*/
	private final Map<URI, ResourceProxy> referenceURIResourceProxyMap=new HashMap<URI, ResourceProxy>();

		/**Retrieves a resource proxy to represent a resource with the given
			reference URI. If such a proxy already exists, it will be returned;
			otherwise, a new one will be created.
		@param referenceURI The reference URI of the resource the proxy should
			represent.
		*/
		protected ResourceProxy getResourceProxy(final URI referenceURI)
		{
			ResourceProxy resourceProxy=referenceURIResourceProxyMap.get(referenceURI);	//see if there is a proxy associated with the reference URI
			if(resourceProxy==null)	///if there is no such resource proxy
			{
				resourceProxy=new ResourceProxy(referenceURI);	//create a new resource proxy
				referenceURIResourceProxyMap.put(referenceURI, resourceProxy);	//store the resource proxy in the map for next time
			}
			return resourceProxy;	//return the proxy we found or created
		}

	/**The map of resource proxies keyed to node IDs.*/
	private final Map<String, ResourceProxy> nodeIDResourceProxyMap=new HashMap<String, ResourceProxy>();

		/**Retrieves a resource proxy to represent a resource with the given
			node ID. If such a proxy already exists, it will be returned;
			otherwise, a new one will be created.
		@param nodeID The node ID of the resource the proxy should
			represent.
		*/
		protected ResourceProxy getResourceProxy(final String nodeID)
		{
			ResourceProxy resourceProxy=nodeIDResourceProxyMap.get(nodeID);	//see if there is a proxy associated with the node ID
			if(resourceProxy==null)	///if there is no such resource proxy
			{
				resourceProxy=new ResourceProxy(nodeID);	//create a new resource proxy
				nodeIDResourceProxyMap.put(nodeID, resourceProxy);	//store the resource proxy in the map for next time
			}
			return resourceProxy;	//return the proxy we found or created
		}

	/**The map of RDF resources keyed to resource proxies.*/ 
	private final Map<ResourceProxy, RDFResource> proxiedRDFResourceMap=new HashMap<ResourceProxy, RDFResource>();

		/**Retrieves a resource associated with the given resource proxy, if any.
		@param resourceProxy The object standing in for the resource.
		@return The resource associated with the given resource proxy, or
			<code>null</code> if there are no resources associated with the given
			resource proxy.
		*/
		protected RDFResource getProxiedRDFResource(final ResourceProxy resourceProxy)
		{
			return proxiedRDFResourceMap.get(resourceProxy); //get any resource associated with the resource proxy
		}

		/**Stores a reference to a resource keyed to a resource proxy.
		@param resourceProxy The object standing in for the resource.
		@param resource The resource the proxy represents.
		*/
		protected void putProxiedRDFResource(final ResourceProxy resourceProxy, final RDFResource resource)
		{
			proxiedRDFResourceMap.put(resourceProxy, resource);	//store the resource keyed to the resource proxy
		}

	/**The next number to use when generating node IDs.*/
	private long nextNodeIDTag=1;

	/**@return A unique node ID appropriate for a new node.*/
	protected String generateNodeID()
	{
		return AbstractRDFProcessor.class.getName()+JavaConstants.PACKAGE_SEPARATOR+"nodeID"+(nextNodeIDTag++);	//use the next node ID tag and increments TODO use a constant
	}

	/**The set of all statements used to create the resources.*/
	private final Set<Statement> statementSet=new HashSet<Statement>();

	/**Adds a statement to the list of statements.
	If an equivalent statement already exists in the list, no action occurs.
	@param statement The statement to add.
	*/
	protected void addStatement(final Statement statement)
	{
	  statementSet.add(statement); //add the statement to the set
	}

	/**@return A read-only iterator of all statements collected and processed by the processor.
	*/
	public Iterator<Statement> getStatementIterator()
	{
		return Collections.unmodifiableSet(statementSet).iterator();  //create an unmodifiable list set the statement set and return an iterator to it
	}

	/**Clear all collected RDF statements.*/
	public void clearStatements()
	{
		statementSet.clear();	//clear the set of statements
	}

	/**Default constructor.*/
	public AbstractRDFProcessor()
	{
		this(new RDF());  //create an RDF data model to use
	}

	/**Constructor that specifies an existing data model to continue filling.
	@param newRDF The RDF data model to use.
	*/
	public AbstractRDFProcessor(final RDF newRDF)
	{
		setRDF(newRDF);  //set the RDF data model and the base URI
	}

	/**Resets the processor by clearing all temporary references and maps, 
		such as associations between resource proxies and RDF resources.
		The set of statements is left undisturbed.
	*/
	public void reset()
	{
		referenceURIResourceProxyMap.clear();	//clear our map of resource proxies keyed to reference URIs
		nodeIDResourceProxyMap.clear();	//clear our map of resource proxies keyed to node IDs
		proxiedRDFResourceMap.clear();	//clear our map of RDF resources keyed to resource proxies
		nextNodeIDTag=1;	//reset our counter that keeps track of the next node ID to use
	}

	/**Iterates through all collected statements and, for any resources in the
		statement that are only proxies for RDF resources, creates appropriate
		resources, using any provided types in other statements if possible.
	@see #ResourceProxy
	*/
	public void createResources()
	{
		createResources(null);	//create resources without keeping track of any resource in particular
	}

	/**Iterates through all collected statements and, for any resources in the
		statement that are only proxies for RDF resources, creates appropriate
		resources, using any provided types in other statements if possible.
	The final created RDF resource of the given resource is returned.
	@param resource The resource, either an RDF resource or a proxy, for which an unproxied RDF resource should be returned,
		or <code>null</code> if no specific resource should be returned.
	@return The created resource from the given resource proxy, or the given resource itself if the resource is already an RDF resource.
	@exception IllegalArgumentException if a resource was given that is not one of the resources in the list of RDF statements.
	@see ResourceProxy
	*/
	public RDFResource createResources(final Resource resource)	//TODO maybe check to make sure any RDFResource passed to us is really one of the ones in the list of statements
	{
		RDFResource rdfResource=resource instanceof RDFResource ? (RDFResource)resource : null;	//if the given resource is already an RDF resource, there's nothing to unproxy
		final Iterator<Statement> statementIterator=getStatementIterator();	//get an iterator to statements
		while(statementIterator.hasNext())	//while there are more statements
		{
			final Statement statement=statementIterator.next();	//get the next statement
			if(statement instanceof DefaultStatement)	//if this is a default statement
			{
				final DefaultStatement defaultStatement=(DefaultStatement)statement;	//cast the statement to one we can change	
				final Resource subject=defaultStatement.getSubject();	//get the statement subject
				if(subject instanceof ResourceProxy)	//if the subject is just a proxy
				{
						//unproxy the resource by either retrieving an already-created resource or creating a new one
					final RDFResource subjectRDFResource=unproxyRDFResource((ResourceProxy)subject);
					defaultStatement.setSubject(subjectRDFResource);	//update the subject to the unproxied RDF resource
					if(subject==resource)	//if the subject is the resource we are supposed to keep track of
					{
						rdfResource=subjectRDFResource;	//show that we now have an RDF resource for the proxy
					}
				}
				final Resource predicate=defaultStatement.getPredicate();	//get the statement predicate
				if(predicate instanceof ResourceProxy)	//if the predicate is just a proxy
				{
						//unproxy the resource by either retrieving an already-created resource or creating a new one
					final RDFResource predicateRDFResource=unproxyRDFResource((ResourceProxy)predicate);
					defaultStatement.setPredicate(predicateRDFResource); //update the predicate to the unproxied RDF resource
					if(predicate==resource)	//if the predicate is the resource we are supposed to keep track of
					{
						rdfResource=predicateRDFResource;	//show that we now have an RDF resource for the proxy
					}
				}
				final Object object=defaultStatement.getObject();	//get the statement object
				if(object instanceof ResourceProxy)	//if the object is just a proxy
				{
						//unproxy the resource by either retrieving an already-created resource or creating a new one
					final RDFResource objectRDFResource=unproxyRDFResource((ResourceProxy)object);
					defaultStatement.setObject(objectRDFResource);
					if(object==resource)	//if the object is the resource we are supposed to keep track of
					{
						rdfResource=objectRDFResource;	//show that we now have an RDF resource for the proxy
					}
				}
			}
		}
		if(resource!=null && rdfResource==null)	//if we were unable to find a suitable RDF resource for a valid given resource, the given resource proxy must have not been in the list of statements
		{
			throw new IllegalArgumentException("Resource "+resource+" unknown in list of statements.");
		}
		return rdfResource;	//return the resource we unproxied if needed
	}

	/**For the given resource proxy, returns the existing RDF resource the
		proxy represents or, if there is no target, creates a new RDF resource for
		the proxy. If the latter, the statements are iterated to determine an
		appropriate type for the new resource, if possible.
	@param resourceProxy The resource that represents the RDF resource by
		reference URI or node ID.
	@return A
	 */
	protected RDFResource unproxyRDFResource(final ResourceProxy resourceProxy)
	{
		RDFResource resource=getProxiedRDFResource(resourceProxy);	//see if we already have a resource represented by the proxy
		if(resource==null)	//if we have no such resource, create one; first, look for an appropriate type
		{
			final Iterator<Statement> statementIterator=getStatementIterator();	//get an iterator to statements
			while(resource==null && statementIterator.hasNext())	//while there are more statements and we haven't created a resource
			{
				final Statement statement=statementIterator.next();	//get the next statement
					//if this is a statement in the form, {resource proxy, rdf:type, XXX}
				if(statement.getSubject().equals(resourceProxy)	//if this statement has this resource proxy as its subject
						&& TYPE_PROPERTY_REFERENCE_URI.equals(statement.getPredicate().getURI()))	//if this statement has a predicate of rdf:type
				{
					final RDFResource typeValueRDFResource;	//we'll find a resource to use as the type value
					final Object typeValueResource=statement.getObject();	//get the type value, which may be a resource or a literal
					if(typeValueResource instanceof RDFResource)	//if the type value is already an RDF resource
					{
						typeValueRDFResource=(RDFResource)typeValueResource;	//use the type value already in place
					}
					else if(typeValueResource instanceof ResourceProxy)	//if the type value is only resource proxy
					{
						typeValueRDFResource=unproxyRDFResource((ResourceProxy)typeValueResource);	//unproxy the type value (note that this will not replace the proxy in the statement, but it will create the resource and associate it with the proxy so that when it does come time to replace the proxy, it will already be there)
					}
					else	//if we don't recognize the value
					{
						continue;	//go to the next statement---we even though this is a type statement, there's no type value we can use
					}
						//if the type value has a separate namespace URI and local name we can use for creating a resource from a factory
					final URI typeNamespaceURI=getNamespaceURI(typeValueRDFResource.getURI());	//see if we can get a namespace URI for the type
					final String typeLocalName=getLocalName(typeValueRDFResource.getURI());	//see if we can get a local name for the type
					if(typeNamespaceURI!=null && typeLocalName!=null)	//if we have both a type and local name
					{
							//try to create a resource using the appropriate resource factory, creating a default resource if no factory could generate one 
						resource=getRDF().createTypedResource(resourceProxy.getURI(), typeNamespaceURI, typeLocalName);
					}
				}
			}
			if(resource==null)	//if we couldn't create a resource from a statement that provided the type
			{
				resource=getRDF().createResource(resourceProxy.getURI());	//create a default resource from the proxy
			}
			putProxiedRDFResource(resourceProxy, resource);	//associate the resource with the resource proxy so we won't have to go through all this the next time 
		}
		return resource;	//return the resource that was either already the target of the proxy, or that we created and associated with the proxy
	}

	/**Processes all statements by creating associations between resources that
		reflect the assertions contained in the statements.
	*/
	public void processStatements()
	{
//TODO del Debug.trace("ready to process statements");
		final Iterator<Statement> statementIterator=getStatementIterator();	//get an iterator to statements
		while(statementIterator.hasNext())	//while there are more statements
		{
			final Statement statement=(Statement)statementIterator.next();	//get the next statement
		//TODO del Debug.trace("here's a statement:", statement);
			final Resource subject=statement.getSubject();	//get the statement subject
			final Resource predicate=statement.getPredicate();	//get the statement predicate
				//if both the subject and predicate of the statement are RDF resources
			if(subject instanceof RDFResource && predicate instanceof RDFResource)
			{
				final RDFResource rdfSubject=(RDFResource)subject;	//cast the subject to an RDF resource
				final RDFResource rdfPredicate=(RDFResource)predicate;	//cast the predicate to an RDF resource
				final Object object=statement.getObject();	//get the statement object
				if(object instanceof RDFObject)	//if the object is an RDF object
				{
				//TODO del Debug.trace("ready to add predicate to subject:", rdfSubject);
					rdfSubject.addProperty(rdfPredicate, (RDFObject)object);	//process this statement by adding the predicate and object to the subject as a property
				}
			}
		}		
	}

	/**A class which represents a resource using either a reference URI or a
		node ID, but not both. 
	@author Garret Wilson
	*/
	protected static class ResourceProxy extends DefaultResource 
	{

		/**The node ID of the resource, or <code>null</code> if there is a reference URI.*/
		private final String nodeID;
	
			/**@return The node ID of the resource, or <code>null</code> if there is a reference URI.*/
			public String getNodeID() {return nodeID;}

		/**Reference URI constructor.
		@param referenceURI The reference URI of the resource.
		*/
		private ResourceProxy(final URI referenceURI)
		{
			this(referenceURI, null);	//construct the proxy with only a reference URI
		}

		/**Node ID constructor.
		@param nodeID The node ID of the resource
		*/
		private ResourceProxy(final String nodeID)
		{
			this(null, nodeID);	//construct the proxy with only a node ID
		}

		/**Full constructor.
		@param referenceURI The reference URI of the resource, or <code>null</code>
			if there is no reference URI but there is a node ID.
		@param nodeID The node ID of the resource, or <code>null</code> if there is
			a reference URI.
		*/
		private ResourceProxy(final URI referenceURI, final String nodeID)	//TODO remove this constructor
		{
			super(referenceURI);	//construct the parent class
/*G***create checks here, throwing IllegalArgumentExceptions appropriately instead of assertions
			assert subjectProxy.getReferenceURI()!=null || subjectProxy.getNodeID()!=null : "Subject proxy must have either a reference URI or a node ID.";
			assert subjectProxy.getReferenceURI()==null || subjectProxy.getNodeID()==null : "Subject proxy must not have both reference URI and node ID.";
			assert objectProxy.getReferenceURI()!=null || objectProxy.getNodeID()!=null : "Object proxy must have either a reference URI or a node ID.";
			assert objectProxy.getReferenceURI()==null || objectProxy.getNodeID()==null : "Object proxy must not have both reference URI and node ID.";
*/
			this.nodeID=nodeID;	//store the node ID
		}

		/**@return A hash code value for the resource proxy.
		@see DefaultRDFResource#getURI()
		@see #getNodeID()
		*/
		public int hashCode()
		{
			if(getURI()!=null)	//if we have a reference URI
			{
				return super.hashCode();	//return the default hash code
			}
			else	//if there is no reference URI, there must be a node ID
			{
				assert getNodeID()!=null : "Resource proxy must have either a reference URI or node ID."; 
				return getNodeID().hashCode();	//return the node ID's hash code
			}
		}

		/**Compares statements based upon reference URI and node ID.
		@param object The object with which to compare this statement; should be
			another resource proxy.
		@return <code>true<code> if the reference URI or node IDs of the two
			statements are equal.
		*/
		public boolean equals(final Object object)
		{
			if(getURI()!=null)	//if we have a reference URI
			{
				return super.equals(object);	//compare using the default resouce functionality
			}
			else	//if we don't have a reference URI, we must have a node ID; compare node IDs
			{
				assert getNodeID()!=null : "Resource proxy must have either a reference URI or node ID."; 
				if(object instanceof ResourceProxy)	//if the other object is resource proxy
				{
					return getNodeID().equals(((ResourceProxy)object).getNodeID());	//compare node IDs
				}
			}
			return false;	//show that the statements do not match
		}

		/**Returns a string representation of the resource.
		<p>This version returns default string representation if there is a
			reference URI; otherwise, the node ID is returned.</p>
		@return A string representation of the resource.
		*/
		public String toString()
		{
			return getURI()!=null ? super.toString() : getNodeID();	//return the reference URI, if available; otherwise, return the node ID
		}

	}

}