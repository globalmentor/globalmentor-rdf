package com.garretwilson.rdf;

import java.text.Collator;

/**Represents an RDF literal, either plain or typed.
@author Garret Wilson
*/
public abstract class RDFLiteral implements RDFObject, Comparable
{

	/**@return The lexical form, a Unicode string in Normal Form C.*/
	public abstract String getLexicalForm();

	/**@return A hashcode value composed from the value.*/
	public int hashCode()
	{
		return getLexicalForm().hashCode();  //return the hash code of the value
	}

	/**@return A string representation of the literal's lexical form.*/
	public String toString()
	{
		return getLexicalForm();  //return the lexical form
	}

	/**Initializes a collator for comparison.
	@param collator The collator to initialize.
	@return The initialized collator.
	*/
	protected static Collator initializeCollator(final Collator collator)
	{
		collator.setStrength(Collator.PRIMARY);	//sort according to primary differences---ignore accents and case differences
		collator.setDecomposition(Collator.FULL_DECOMPOSITION);	//fully decompose Unicode characters to get the best comparison
		return collator;	//return the initialized collator
	}

	/**Returns an initialized collator appropriate for comparing this literal to
		another.
	<p>This version returns the default collator for the default locale.</p>
	@return A collator appropriate for comparing this literal to the given literal.
	@see #initializeCollator(Collator)
	*/  
	protected Collator getCollator(final RDFLiteral literal)
	{
		return initializeCollator(Collator.getInstance());	//get a collator using whatever locale we're in and initialize it
	}

	/**Compares this object to another object.
	<p>This version determines order based the lexical form of the literal,
		using the appropriate collator returned by
		<code>getCollator(RDFLiteral)</code>.</p>
	@param object The object with which to compare the object. This must be
		another <code>RDFLiteral</code> object.
	@return A negative integer, zero, or a positive integer if the first resource
		lexical form is less than, equal to, or greater than the lexical form of
		the second literal, respectively.
	@exception ClassCastException Thrown if the specified object's type is not
		<code>RDFLiteral</code>.
	@see #getCollator(RDFLiteral)
	*/
	public int compareTo(Object object) throws ClassCastException
	{
		final RDFLiteral literal=(RDFLiteral)object;	//cast the object to a literal
		final Collator collator=getCollator(literal);	//get a collator with which to compare the literals
		return collator.compare(getLexicalForm(), literal.getLexicalForm());	//compare the lexical forms of the two literals 
	}

}