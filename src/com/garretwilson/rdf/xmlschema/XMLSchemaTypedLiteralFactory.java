package com.garretwilson.rdf.xmlschema;

import java.net.URI;
import com.garretwilson.rdf.*;
import com.garretwilson.text.xml.schema.XMLSchemaConstants;

/**A factory to create typed literals within the XML schema namespace.
@author Garret Wilson
*/
public class XMLSchemaTypedLiteralFactory implements RDFTypedLiteralFactory, XMLSchemaConstants 
{

	/**Creates a typed literal from the provided lexical form based upon the
		datatype reference URI.
	<p>This implementation supports the following XML Schema data types, creating
		the indicated RDF typed literal classes:</p>
	<dl>
		<dt><code>http://www.w3.org/2001/XMLSchema#boolean</code></dt> <dd><code>BooleanLiteral</code></dd>
	</dl>
	@param lexicalForm The lexical form of the resource.
	@param datatypeURI The datatype reference URI of the datatype.
	@return A typed literal containing a value object mapped from the given
		lexical form and datatype
	@see BooleanLiteral
	*/
	public RDFTypedLiteral createTypedLiteral(final String lexicalForm, final URI datatypeURI)
	{
		if(BOOLEAN_DATATYPE_URI.equals(datatypeURI))	//boolean
		{
			return new BooleanLiteral(lexicalForm);	//create and return a boolean literal
		}
		else	//if we don't recognize the type
		{
			return null;	//show that we don't recognize the datatype URI 
		}
	}
}