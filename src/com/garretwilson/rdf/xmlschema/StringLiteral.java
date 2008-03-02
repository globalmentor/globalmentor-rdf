package com.garretwilson.rdf.xmlschema;

import com.garretwilson.rdf.*;
import static com.globalmentor.java.Objects.*;
import static com.globalmentor.text.xml.schema.XMLSchemaConstants.*;

/**An RDF typed literal that represents an XML Schema string.
@author Garret Wilson
@see String
*/
public class StringLiteral extends RDFTypedLiteral<String>
{

	/**Constructs a string literal using the datatype <code>http://www.w3.org/2001/XMLSchema#string</code>.
	@param string The <code>String</code> object representing the value of the literal.
	*/
	public StringLiteral(final String string)
	{
		super(string, STRING_DATATYPE_URI);	//save the string object as the value, specifying the XML Schema boolean datatype URI
	}

	/**Determines if the RDF object is a string literal and, if so, casts the object to string boolean literal and returns it.
	@param rdfObject The RDF object in question.
	@return The RDF object as a string literal, or <code>null</code> if the object is not a string literal or the object is <code>null</code>.
	*/
/*TODO del if not needed
	public static StringLiteral asStringLiteral(final RDFObject rdfObject)
	{
		return asInstance(rdfObject, StringLiteral.class);	//cast the object to a string literal if we can
	}
*/

	/**Determines if the RDF object is a string literal and, if so, casts the 
		object to a string literal and returns its value; otherwise, returns
		<code>false</code>.
	@param rdfObject The RDF object in question.
	@return The boolean value of the boolean literal, or <code>false</code> if the
		object is not a boolean literal or the object is <code>null</code>.
	*/
/*TODO del if not needed
	public static boolean asBooleanValue(final RDFObject rdfObject)
	{
		return asBooleanValue(rdfObject, false);	//return the boolean value, defaulting to false if the given object is not a boolean literal
	}
*/

	/**Determines if the RDF object is a boolean literal and, if so, casts the 
		object to a boolean literal and returns its value; otherwise, returns
		the given default.
	@param rdfObject The RDF object in question.
	@param defaultValue The default boolean value to return if the object is not a boolean literal.
	@return The boolean value of the boolean literal, or <var>defaultValue</var> if the
		object is not a boolean literal or the object is <code>null</code>.
	*/
/*TODO del if not needed
	public static boolean asBooleanValue(final RDFObject rdfObject, final boolean defaultValue)
	{
		final BooleanLiteral booleanLiteral=asBooleanLiteral(rdfObject);	//cast the object to a boolean literal, if it is one
		return booleanLiteral!=null ? booleanLiteral.getValue().booleanValue() : defaultValue;	//return the boolean value of the boolean literal, or the default value if there is no boolean literal
	}
*/
}