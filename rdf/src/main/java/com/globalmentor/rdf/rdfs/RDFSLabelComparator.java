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

package com.globalmentor.rdf.rdfs;

import com.globalmentor.rdf.*;

import java.util.*;

/**
 * A comparator that compares resources based upon labels, if available.
 * @author Garret Wilson
 */
public class RDFSLabelComparator implements Comparator<RDFResource> {

	/**
	 * {@inheritDoc}
	 * @throws ClassCastException Thrown if the specified objects' types are not <code>RDFResource</code>.
	 * @see RDFSResources#getLabel(RDFResource)
	 */
	@Override
	public int compare(final RDFResource resource1, final RDFResource resource2) throws ClassCastException {
		final RDFLiteral label1 = RDFSResources.getLabel(resource1); //get the labels, if any
		final RDFLiteral label2 = RDFSResources.getLabel(resource2);
		if(label1 != null && label2 != null) { //if both resources have labels
			return label1.compareTo(label2); //let the labels compare themselves---they know how to correctly use collators for comparison
		} else { //if both resources don't have labels
			//TODO fix comparator sorting for no labels; use inherent resource comparison, once that's fixed
			if(resource1.getURI() != null && resource2.getURI() != null) //if both resources have reference URIs				
				return resource1.getURI().compareTo(resource2.getURI()); //compare reference URIs
			else
				//if one of the two resources doesn't have a reference URI
				return resource1.hashCode() - resource2.hashCode(); //make an arbitrary comparison TODO improve
		}
	}

}
