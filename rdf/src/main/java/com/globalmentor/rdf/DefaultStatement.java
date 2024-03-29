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

import java.util.Objects;

import com.globalmentor.net.Resource;

/**
 * A default implementation of a statement in an RDF data model.
 * @author Garret Wilson
 */
public class DefaultStatement implements Statement {

	/** The statement subject. */
	private Resource subject;

	/** @return The statement subject. */
	public Resource getSubject() {
		return subject;
	}

	/**
	 * Sets the statement subject.
	 * @param subject The statement subject.
	 */
	void setSubject(final Resource subject) {
		this.subject = subject;
	}

	/** The statement predicate. */
	private Resource predicate;

	/** @return The statement predicate. */
	public Resource getPredicate() {
		return predicate;
	}

	/**
	 * Sets the statement predicate.
	 * @param predicate The statement predicate.
	 */
	void setPredicate(final Resource predicate) {
		this.predicate = predicate;
	}

	/** The statement object. */
	private Object object;

	/** @return The statement object. */
	public Object getObject() {
		return object;
	}

	/**
	 * Sets the statement object.
	 * @param object The statement object.
	 */
	void setObject(final Object object) {
		this.object = object;
	}

	/**
	 * Creates a new statement from a subject, predicate, and object resource.
	 * @param subject The subject of the statement.
	 * @param predicate The predicate of the statement.
	 * @param object The object of the statement.
	 */
	public DefaultStatement(final Resource subject, final Resource predicate, final Resource object) {
		setSubject(subject); //set the subject
		setPredicate(predicate); //set the predicate
		setObject(object); //set the object
	}

	/**
	 * Creates a new statement from subject and predicate resources and an object literal.
	 * @param subject The subject of the statement.
	 * @param predicate The predicate of the statement.
	 * @param objectLiteral The object of the statement.
	 */
	public DefaultStatement(final Resource subject, final Resource predicate, final RDFLiteral objectLiteral) {
		setSubject(subject); //set the subject
		setPredicate(predicate); //set the predicate
		setObject(objectLiteral); //set the object
	}

	/**
	 * @return A hash code value for the statement.
	 * @see #getSubject()
	 * @see #getPredicate()
	 * @see #getObject()
	 */
	public int hashCode() {
		final StringBuilder stringBuilder = new StringBuilder(); //create a string buffer to store string versions of the parts of the statement
		final Resource subject = getSubject(); //get the subject
		if(subject != null) { //if there is a subject
			stringBuilder.append(subject); //append the subject
		}
		final Resource predicate = getPredicate(); //get the predictae
		if(getPredicate() != null) { //if there is a predicate
			stringBuilder.append(predicate); //append the predicate
		}
		final Object object = getObject(); //get the object
		if(object != null) { //if there is an object
			stringBuilder.append(object); //append the object
		}
		return stringBuilder.toString().hashCode(); //return the hash code of all three parts of the statement as a string (StringBuilder apparently doesn't provide a specialized hashCode() implementation)
	}

	/**
	 * Compares statements based upon subject, predicate, and object.
	 * @param object The object with which to compare this statement; should be another statement.
	 * @return <code>true</code> if the subjects, predicates, and objects of the two statements are equal.
	 */
	public boolean equals(final Object object) {
		if(object instanceof Statement) { //if the other object is a statement
			final Statement statement2 = (Statement)object; //cast the object to a statement
			return Objects.equals(getSubject(), statement2.getSubject()) //compare subjects
					&& Objects.equals(getPredicate(), statement2.getPredicate()) //compare predicates
					&& Objects.equals(getObject(), statement2.getObject()); //compare objects
		}
		return false; //show that the statements do not match
	}

	/**
	 * @return A string representation of the statement in the form: "{subject, predicate, object}".
	 */
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder(); //create a new string buffer
		stringBuilder.append('{');
		stringBuilder.append('[').append(getSubject()).append(']');
		stringBuilder.append(',').append(' ');
		stringBuilder.append('[').append(getPredicate()).append(']');
		stringBuilder.append(',').append(' ');
		final Object object = getObject(); //get the object of the statement
		if(object instanceof Resource) //if the object is a resource
			stringBuilder.append('[').append(object).append(']'); //[resource]
		else if(object instanceof RDFLiteral) //if the object is a literal
			stringBuilder.append('"').append(((RDFLiteral)object).getLexicalForm()).append('"'); //"literal"
		stringBuilder.append('}');
		return stringBuilder.toString(); //return the string we just constructed
	}

}
