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

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;

import com.globalmentor.io.*;
import com.globalmentor.model.DefaultModifiable;
import com.globalmentor.model.Modifiable;
import com.globalmentor.text.xml.XMLSerializer;
import com.globalmentor.text.xml.XML;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**A modifiable object that knows how to store and retrieve itself as RDF.
<p>The data storage and retrieval operations of this class are thread-safe.</p>
<p>Bound properties:</p>
<ul>
	<li><code>Modifiable.MODIFIED_PROPERTY</code> ("modified")</li>
</ul>
@author Garret Wilson
@see Modifiable#MODIFIED_PROPERTY
@deprecated
*/
public abstract class AbstractRDFStorage extends DefaultModifiable implements URIInputStreamable, URIOutputStreamable 
{
	/**The location where the RDF should be stored.*/
	private final URI storageURI;
	
		/**@return The location where the RDF should be stored.*/
		public URI getStorageURI() {return storageURI;}

		/**The map of prefixes, keyed by namespace URIs.*/
		private final Map<URI, String> namespaceURIPrefixMap=new HashMap<URI, String>();

			/**Registers the given prefix to be used with the given namespace URI.
				If a prefix is already registered with the given namespace, it is
				replaced with this prefix.
			@param namespaceURI The XML namespace URI.
			@param prefix The serialization prefix to use with the given namespace.
			*/
			public void registerNamespacePrefix(final URI namespaceURI, final String prefix)
			{
				namespaceURIPrefixMap.put(namespaceURI, prefix);	//store the prefix in the map, keyed to the URI
			}

			/**Unregisters the prefix for the given namespace URI.
				If no prefix is registered for the given namespace, no action occurs.
			@param namespaceURI The XML namespace URI.
			*/
			public void unregisterNamespacePrefix(final String namespaceURI, final String prefix)
			{
				namespaceURIPrefixMap.remove(namespaceURI);	//remove whatever prefix is registered with this namespace, if any
			}

	/**A map of resource factories, keyed to namespace URI.*/
	private final Map<URI, RDFResourceFactory> resourceFactoryMap=new HashMap<URI, RDFResourceFactory>();

		/**Registers a resource factory to be used to create resources with a type
			from the specified namespace. If a resource factory is already registered
			for this namespace, it will be replaced.
		<p>Registered resource factories will be registered with the created RDF
			data model.</p> 
		@param typeNamespaceURI The namespace of the resource type for which this
			factory should be used to create objects.
		@param factory The resource factory that will be used to create resources
			of types from this this namespace.
		*/
		public void registerResourceFactory(final URI typeNamespaceURI, final RDFResourceFactory factory)
		{
			resourceFactoryMap.put(typeNamespaceURI, factory);
		}

		/**Removes the resource factory being used to create resources with a type
			from the specified namespace. If there is no resource factory registered
			for this namespace, no action will be taken.
		@param typeNamespaceURI The namespace of the resource type for which this
			factory should be used to create objects.
		*/
		public void unregisterResourceFactory(final URI typeNamespaceURI)
		{
			resourceFactoryMap.remove(typeNamespaceURI);
		}

	/**Storage location constructor.
	@param storageURI The location where the RDF should be stored.
	*/
	public AbstractRDFStorage(final URI storageURI)
	{
		this.storageURI=storageURI;	//save the storage URI
	}
	
	/**@return An XML serializer appropriately configured for storing the RDF XML.
	<p>Registered namespace prefixes are registered with the {@link RDFXMLGenerator}.</p>
	@see RDFXMLGenerator#registerNamespacePrefix(URI, String)
	*/
	protected RDFXMLGenerator getRDFXMLGenerator()
	{
		final RDFXMLGenerator rdfXMLGenerator=new RDFXMLGenerator();	//create an object to convert the RDF data model to an XML data model
		final Iterator<Map.Entry<URI, String>> namespaceURIPrefixEntryIterator=namespaceURIPrefixMap.entrySet().iterator();	//get an iterator to look through all namespace URIs and prefixes
		while(namespaceURIPrefixEntryIterator.hasNext())	//while there are more prefixes
		{
			final Map.Entry<URI, String> namespaceURIPrefixEntry=namespaceURIPrefixEntryIterator.next();	//get the next entry
			final URI namespaceURI=namespaceURIPrefixEntry.getKey();	//get the namespace URI
			final String namespacePrefix=namespaceURIPrefixEntry.getValue();	//get the namespace prefix
			rdfXMLGenerator.registerNamespacePrefix(namespaceURI, namespacePrefix);	//register this prefix for the this namespace
		}
		return rdfXMLGenerator;	//return the XMLifier we created
	}
	
	/**@return An XML serializer appropriately configured for storing the RDF XML.*/
	protected XMLSerializer getXMLSerializer()
	{
		return new XMLSerializer(true);	//create a formatted XML serializer
	}

	/**@return An XML processor appropriately configured for parsing XML.*/
	protected DocumentBuilder getDocumentBuilder()
	{
		return XML.createDocumentBuilder(true);	//create an XML processor
	}
	
	/**@return An RDF processor appropriately configured for processing RDF
		stored in an XML data model.
	<p>Registered resource factories are registered with the RDF data model.</p>
	@see RDF#registerResourceFactory(URI, RDFResourceFactory)
	*/
	protected RDFXMLProcessor getRDFXMLProcessor()
	{
		final RDF rdf=new RDF();	//create a new RDF data model
		final Iterator<Map.Entry<URI, RDFResourceFactory>> resourceFactoryEntryIterator=resourceFactoryMap.entrySet().iterator();	//get an iterator to look through all resource factories
		while(resourceFactoryEntryIterator.hasNext())	//while there are more resource factories
		{
			final Map.Entry<URI, RDFResourceFactory> resourceFactoryEntry=resourceFactoryEntryIterator.next();	//get the next entry
			final URI typeNamespaceURI=resourceFactoryEntry.getKey();	//get the type namespace URI
			final RDFResourceFactory resourceFactory=resourceFactoryEntry.getValue();	//get the resource factory associated with this type
			rdf.registerResourceFactory(typeNamespaceURI, resourceFactory);	//register this resource factory for this type namespace
		}
		return new RDFXMLProcessor(rdf);	//create an RDF XML processor, using the RDF data model we created and configured
	}

	/**@return The RDF data model that represents the information to be stored.*/
	public abstract RDF getRDF();

	/**Extracts the information from the RDF data model.
	@param rdf The RDF data model that has been retrieved from storage.
	*/
	public abstract void setRDF(final RDF rdf);

	/**Stores the information in RDF at the storage URI.
	<p>After this method, the <code>modified</code> bound property will be
		<code>false</code>.</p>
	@exception IOException Thrown if there is a problem storing the information.
	@see #getStorageURI()
	*/
	public void store() throws IOException
	{
		final RDF rdf=getRDF();	//get the RDF data model representing the data
			//create an XML document from the RDF
		final Document document=getRDFXMLGenerator().createDocument(rdf, XML.createDocumentBuilder(true).getDOMImplementation());  //TODO try to make this XML parser agnostic
			//make sure all the registered namespaces are declared on the document element just to make things look nice in the serialization
		final Element documentElement=document.getDocumentElement();	//get the XML document element
		final Iterator<Map.Entry<URI, String>> namespaceURIPrefixEntryIterator=namespaceURIPrefixMap.entrySet().iterator();	//get an iterator to look through all namespace URIs and prefixes
		while(namespaceURIPrefixEntryIterator.hasNext())	//while there are more prefixes
		{
			final Map.Entry<URI, String> namespaceURIPrefixEntry=namespaceURIPrefixEntryIterator.next();	//get the next entry
			final URI namespaceURI=namespaceURIPrefixEntry.getKey();	//get the namespace URI
			final String namespacePrefix=namespaceURIPrefixEntry.getValue();	//get the namespace prefix
				//make sure this xmlns:prefix attribute is declared
			XML.ensureNamespaceDeclaration(document.getDocumentElement(), namespacePrefix, namespaceURI.toString());
		}
		store(document);	//store the XML document
		setModified(false);	//show that we are no longer modified
	}

	/**Stores the RDF information at the storage URI.
	@param document The XML document that contains the RDF data to store
	@exception IOException Thrown if there is a problem storing the information.
	@see #getStorageURI()
	*/
	protected void store(final Document document) throws IOException
	{
		store(document, getStorageURI());	//store the information at the storage URI
	}

	/**Stores the RDF information at the given URI.
	@param document The XML document that contains the RDF data to store
	@param uri The URI at which the information should be stored
	@exception IOException Thrown if there is a problem storing the information.
	@see #getStorageURI()
	*/
	protected synchronized void store(final Document document, final URI uri) throws IOException
	{
		final XMLSerializer xmlSerializer=getXMLSerializer();	//get an XML serializer
		final OutputStream outputStream=new BufferedOutputStream(getOutputStream(uri));	//get a buffered output stream to the URI
		try
		{
			xmlSerializer.serialize(document, outputStream);	//serialize the XML document to the output stream	
		}
		finally
		{
			outputStream.close();	//always close the output stream
		}
	}
	
	/**Retrieves the information from RDF stored at the storage URI.
	<p>After this method, the <code>modified</code> bound property will be
		<code>false</code>.</p>
	@exception IOException Thrown if there is a problem retrieving the information.
	@see #getStorageURI()
	*/
	public void retrieve() throws IOException
	{
		retrieve(getStorageURI());	//retrieve the information from the storage URI
		setModified(false);	//show that we are no longer modified
	}
	
	/**Retrieves the information from RDF stored at the given URI.
	@param uri The URI at which the information is be stored
	@exception IOException Thrown if there is a problem retrieving the information.
	*/
	public void retrieve(final URI uri) throws IOException
	{
		final Document document=retrieveXML(uri);	//retrieve an XML document from the storage
		try
		{
			final RDF rdf=retrieveRDF(document);	//retrieve the RDF from the document
			setRDF(rdf);	//set the RDF we retrieved
		}
		catch(URISyntaxException uriSyntaxException)	//if there was a problem retrieving the RDF from the XML
		{
			final IOException ioException=new IOException(uriSyntaxException.getMessage());	//create an I/O exception from the URI syntax exception
			ioException.initCause(uriSyntaxException);	//show what caused the I/O exception
			throw ioException;	//throw the new I/O exception in place of the one we caught
		}
	}
	
	/**Retrieves an RDF data model from the given XML document.
	@param document The XML document containing RDF.
	@return An RDF data model representing the RDF contained in the XML document.
	@exception URISyntaxException Thrown if a URI is syntactically incorrect.	
	*/
	protected RDF retrieveRDF(final Document document) throws URISyntaxException
	{
		final RDFXMLProcessor rdfXMLProcessor=getRDFXMLProcessor();	//get the processor for processing RDF from the XML document
		return rdfXMLProcessor.processRDF(document);	//process the RDF from the XML and return the RDF data model
	}
	
	/**Retrieves an XML document from a URI.
	@param uri The URI from which the information should be retrieved.
	@return An XML document tree representing the information.
	@exception IOException Thrown if there is a problem retrieving the information.
	*/
	protected synchronized Document retrieveXML(final URI uri) throws IOException
	{
		final DocumentBuilder documentBuilder=getDocumentBuilder();	//get an XML processor
		final InputStream inputStream=new BufferedInputStream(getInputStream(uri));	//get a buffered input stream from the URI
		try
		{
			return documentBuilder.parse(inputStream, uri.toString());	//read an XML document from the input stream and return it	
		}
		catch(final SAXException saxException)
		{
			throw new IOException(saxException.getMessage(), saxException);
		}
		finally
		{
			inputStream.close();	//always close the input stream
		}
	}
}
