package com.garretwilson.rdf.dublincore;

import java.text.*;
import java.util.*;
import com.garretwilson.text.*;
import com.garretwilson.rdf.*;
import com.garretwilson.util.*;

/**Utilities for working with Dublin Core stored in RDF.
@author Garret Wilson
*/
public class DCUtilities implements DCConstants
{

	/**@return A new date format for formatting W3C-style dates.*/
	public final static DateFormat createDateFormat()  //G***put this in an appropriate package
	{
		return new W3CDateFormat(W3CDateFormat.DATE_STYLE);	//return a W3C date and time formatter for just the date 
//G***del		return new SimpleDateFormat(W3C_DATE_FORMAT); //return a new simple date formatter with the W3C date template
	}

	/**Adds a <code>dc:creator</code> property with the given value to the
		resource.
	@param rdf The RDF data model to be used as a property factory.
	@param resource The resource to which the property should be added.
	@param value The property value to add.
	@return The added literal property value.
	*/
	public static Literal addCreator(final RDF rdf, final RDFResource resource, final String value)
	{
		return RDFUtilities.addProperty(rdf, resource, DCMI11_ELEMENTS_NAMESPACE_URI, DC_CREATOR_PROPERTY_NAME, value);
	}

	/**Adds a <code>dc:contributor</code> property with the given value to the
		resource.
	@param rdf The RDF data model to be used as a property factory.
	@param resource The resource to which the property should be added.
	@param value The property value to add.
	@return The added literal property value.
	*/
	public static Literal addContributor(final RDF rdf, final RDFResource resource, final String value)
	{
		return RDFUtilities.addProperty(rdf, resource, DCMI11_ELEMENTS_NAMESPACE_URI, DC_CONTRIBUTOR_PROPERTY_NAME, value);
	}

	/**Adds a <code>dc:date</code> property with the given value to the
		resource.
	@param rdf The RDF data model to be used as a property factory.
	@param resource The resource to which the property should be added.
	@param date The property value to add.
	@return The added literal property value.
	*/
	public static Literal addDate(final RDF rdf, final RDFResource resource, final Date date)
	{
		  //G***fix
//G***del		final DateFormat dateFormat=DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT); //G***testing
		return RDFUtilities.addProperty(rdf, resource, DCMI11_ELEMENTS_NAMESPACE_URI, DC_DATE_PROPERTY_NAME, createDateFormat().format(date));
	}

	/**Adds a <code>dc:description</code> property with the given value to the
		resource.
	@param rdf The RDF data model to be used as a property factory.
	@param resource The resource to which the property should be added.
	@param value The property value to add.
	@return The added literal property value.
	*/
	public static Literal addDescription(final RDF rdf, final RDFResource resource, final String value)
	{
		return RDFUtilities.addProperty(rdf, resource, DCMI11_ELEMENTS_NAMESPACE_URI, DC_DESCRIPTION_PROPERTY_NAME, value);
	}

	/**Adds a <code>dc:identifier</code> property with the given value to the
		resource.
	@param rdf The RDF data model to be used as a property factory.
	@param resource The resource to which the property should be added.
	@param value The property value to add.
	@return The added literal property value.
	*/
	public static Literal addIdentifier(final RDF rdf, final RDFResource resource, final String value)
	{
		return RDFUtilities.addProperty(rdf, resource, DCMI11_ELEMENTS_NAMESPACE_URI, DC_IDENTIFIER_PROPERTY_NAME, value);
	}

	/**Adds a <code>dc:language</code> property with the given value to the
		resource.
	@param rdf The RDF data model to be used as a property factory.
	@param resource The resource to which the property should be added.
	@param locale The property value to add.
	@return The added literal property value.
	*/
	public static Literal addLanguage(final RDF rdf, final RDFResource resource, final Locale locale)
	{
		  //G***fix
//G***del		final DateFormat dateFormat=DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT); //G***testing
		final String languageString=LocaleUtilities.getLanguageTag(locale);	//get the language tag for this locale
//G***del Debug.trace("DC language string: ", languageString);  //G***del
		return RDFUtilities.addProperty(rdf, resource, DCMI11_ELEMENTS_NAMESPACE_URI, DC_LANGUAGE_PROPERTY_NAME, languageString);
	}

	/**Adds a <code>dc:publisher</code> property with the given value to the
		resource.
	@param rdf The RDF data model to be used as a property factory.
	@param resource The resource to which the property should be added.
	@param value The property value to add.
	@return The added literal property value.
	*/
	public static Literal addPublisher(final RDF rdf, final RDFResource resource, final String value)
	{
		return RDFUtilities.addProperty(rdf, resource, DCMI11_ELEMENTS_NAMESPACE_URI, DC_PUBLISHER_PROPERTY_NAME, value);
	}

	/**Adds a <code>dc:rights</code> property with the given value to the
		resource.
	@param rdf The RDF data model to be used as a property factory.
	@param resource The resource to which the property should be added.
	@param value The property value to add.
	@return The added literal property value.
	*/
	public static Literal addRights(final RDF rdf, final RDFResource resource, final String value)
	{
		return RDFUtilities.addProperty(rdf, resource, DCMI11_ELEMENTS_NAMESPACE_URI, DC_RIGHTS_PROPERTY_NAME, value);
	}

	/**Adds a <code>dc:source</code> property with the given value to the
		resource.
	@param rdf The RDF data model to be used as a property factory.
	@param resource The resource to which the property should be added.
	@param value The property value to add.
	@return The added literal property value.
	*/
	public static Literal addSource(final RDF rdf, final RDFResource resource, final String value)
	{
		return RDFUtilities.addProperty(rdf, resource, DCMI11_ELEMENTS_NAMESPACE_URI, DC_SOURCE_PROPERTY_NAME, value);
	}

	/**Adds a <code>dc:title</code> property with the given value to the
		resource.
	@param rdf The RDF data model to be used as a property factory.
	@param resource The resource to which the property should be added.
	@param value The property value to add.
	@return The added literal property value.
	*/
	public static Literal addTitle(final RDF rdf, final RDFResource resource, final String value)
	{
		return RDFUtilities.addProperty(rdf, resource, DCMI11_ELEMENTS_NAMESPACE_URI, DC_TITLE_PROPERTY_NAME, value);
	}

	/**Returns the value of the first <code>dc:creator</code> property.
	@param resource The resource the property of which should be located.
	@return The value of the first <code>dc:creator</code> property, or
		<code>null</code> if no such property exists.
	*/
	public static RDFObject getCreator(final RDFResource resource)
	{
		return resource.getPropertyValue(DCMI11_ELEMENTS_NAMESPACE_URI, DC_CREATOR_PROPERTY_NAME);
	}

	/**Returns the value of the first <code>dc:description</code> property.
	@param resource The resource the property of which should be located.
	@return The value of the first <code>dc:description</code> property, or
		<code>null</code> if no such property exists.
	*/
	public static RDFObject getDescription(final RDFResource resource)
	{
		return resource.getPropertyValue(DCMI11_ELEMENTS_NAMESPACE_URI, DC_DESCRIPTION_PROPERTY_NAME);
	}

	/**Returns the value of the first <code>dc:Language</code> property.
	@param resource The resource the property of which should be located.
	@return The value of the first <code>dc:language</code> property, or
		<code>null</code> if no such property exists.
	*/
	public static RDFObject getLanguage(final RDFResource resource) //G***maybe fix to return a Locale
	{
		return resource.getPropertyValue(DCMI11_ELEMENTS_NAMESPACE_URI, DC_LANGUAGE_PROPERTY_NAME);
	}

	/**Returns the value of the first <code>dc:title</code> property.
	@param resource The resource the property of which should be located.
	@return The value of the first <code>dc:title</code> property, or
		<code>null</code> if no such property exists.
	*/
	public static RDFObject getTitle(final RDFResource resource)
	{
		return resource.getPropertyValue(DCMI11_ELEMENTS_NAMESPACE_URI, DC_TITLE_PROPERTY_NAME);
	}

	/**Returns the value of the first <code>dc:rights</code> property.
	@param resource The resource the property of which should be located.
	@return The value of the first <code>dc:rights</code> property, or
		<code>null</code> if no such property exists.
	*/
	public static RDFObject getRights(final RDFResource resource)
	{
		return resource.getPropertyValue(DCMI11_ELEMENTS_NAMESPACE_URI, DC_RIGHTS_PROPERTY_NAME);
	}

}