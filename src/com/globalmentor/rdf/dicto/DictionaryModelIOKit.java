package com.globalmentor.rdf.dicto;

import java.io.*;
import java.net.*;
import com.garretwilson.model.*;

import static com.globalmentor.rdf.dicto.DictoConstants.*;

import com.globalmentor.io.*;
import com.globalmentor.rdf.*;
import com.globalmentor.text.xml.XMLProcessor;

import org.w3c.dom.Document;

/**Class for loading and saving a Dicto dictionary.
@author Garret Wilson
@see DictionaryModel
*/
public class DictionaryModelIOKit extends AbstractIOKit<ResourceModel<Dictionary>>
{

	/**Default constructor.*/
	public DictionaryModelIOKit()
	{
		this(null, null);
	}

	/**URI input stream locator constructor.
	@param uriInputStreamable The implementation to use for accessing a URI for
		input, or <code>null</code> if the default implementation should be used.
	*/
	public DictionaryModelIOKit(final URIInputStreamable uriInputStreamable)
	{
		this(uriInputStreamable, null);
	}

	/**URI output stream locator constructor.
	@param uriOutputStreamable The implementation to use for accessing a URI for
		output, or <code>null</code> if the default implementation should be used.
	*/
	public DictionaryModelIOKit(final URIOutputStreamable uriOutputStreamable)
	{
		this(null, uriOutputStreamable);
	}

	/**Full constructor.
	@param uriInputStreamable The implementation to use for accessing a URI for
		input, or <code>null</code> if the default implementation should be used.
	@param uriOutputStreamable The implementation to use for accessing a URI for
		output, or <code>null</code> if the default implementation should be used.
	*/
	public DictionaryModelIOKit(final URIInputStreamable uriInputStreamable, final URIOutputStreamable uriOutputStreamable)
	{
		super(uriInputStreamable, uriOutputStreamable);	//construct the parent class
	}

	/**Loads a model from an input stream.
	@param inputStream The input stream from which to read the data.
	@param baseURI The base URI of the content, or <code>null</code> if no base
		URI is available.
	@throws IOException Thrown if there is an error reading the data.
	*/ 
	public ResourceModel<Dictionary> load(final InputStream inputStream, final URI baseURI) throws IOException
	{
		try
		{
			final RDF rdf=new RDF();  //create a new RDF data model
			rdf.registerResourceFactory(DICTO_NAMESPACE_URI, new DictoUtilities());  //register a factory for Dicto resource classes
			final XMLProcessor xmlProcessor=new XMLProcessor(this);	//create an XML processor using the correct input streams locator
			final Document document=xmlProcessor.parseDocument(inputStream, baseURI);	//parse the activity file
			document.normalize(); //normalize the package description document
			final RDFXMLProcessor rdfProcessor=new RDFXMLProcessor(rdf);	//create a new RDF processor
			rdfProcessor.processRDF(document, baseURI);  //parse the RDF from the document
				//get a dictionary from the data model
			final Dictionary dictionary=(Dictionary)RDFUtilities.getResourceByType(rdf, DICTO_NAMESPACE_URI, DICTIONARY_CLASS_NAME);
			if(dictionary==null)	//if there is no dictionary
			{
				throw new IOException("No dictionary found.");	//G**i18n
			}
			return new ResourceModel<Dictionary>(dictionary, baseURI, this);	//return a new dictionary model with the dictionary, if we loaded one
		}
		catch(URISyntaxException uriSyntaxException)	//if any of the URIs were incorrect
		{
			throw (IOException)new IOException(uriSyntaxException.getMessage()).initCause(uriSyntaxException);	//convert the exception into an IO exception
		}
	}
	
	/**Saves a model to an output stream.
	@param model The model the data of which will be written to the given output stream.
	@param outputStream The output stream to which to write the model content.
	@throws IOException Thrown if there is an error writing the model.
	*/
	public void save(final ResourceModel<Dictionary> model, final OutputStream outputStream) throws IOException
	{
		throw new IOException("Dictionary save is not yet implemented");
		/*G***fix when needed
				//G***create a BOMWriter that does both steps

				outputStream.write(CharacterEncodingConstants.BOM_UTF_8);	//write the UTF-8 byte order mark
				final Writer writer=new OutputStreamWriter(outputStream, CharacterEncodingConstants.UTF_8);	//create a UTF-8 writer
				final JTextPane textPane=getTextPane();	//get a reference to the text pane
				final Document document=textPane.getDocument();	//get the document currently loaded into the text pane
				try
				{
					textPane.getEditorKit().write(writer, document, 0, document.getLength());	//have the editor kit write the document to the writer
//		G***del when works			textPane.getEditorKit().write(outputStream, document, 0, document.getLength());	//have the editor kit write the document to the output stream
				}
				catch(BadLocationException badLocationException)
				{
					Debug.error(badLocationException);	//this should never occcur
				}	
				setModified(false);	//show that the information has not been modified, as we just saved it
		*/
	}
}
