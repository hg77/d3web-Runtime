/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/*
 * Created on 07.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.d3web.shared;




import java.util.Enumeration;
import java.util.Hashtable;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.core.session.Session;
import de.d3web.shared.PSMethodShared;

/**
 * @author heckel
 */
public class LocalWeight implements KnowledgeSlice {

	public static double G0 = 0;
	public static double G1 = 1;
	public static double G2 = 2;
	public static double G3 = 4;
	public static double G4 = 8;
	public static double G5 = 16;
	public static double G6 = 32;
	public static double G7 = 64;
	
	private Question q;
	private Solution d;
	private Hashtable<Value, Double> values = null;
	
	/**
	 * Weight constructor comment.
	 */
	public LocalWeight() {
		super();
		values = new Hashtable<Value, Double>();
	}
	
	public void setValue(Value ans, double value) {
		values.put(ans, new Double(value));
	}

	
	public double getValue(Value ans) {
		Double ret = values.get(ans);
		if (ret != null)
		{
			return ret.doubleValue();
		}
		return G0;
	}
	
	public void setQuestion(de.d3web.core.knowledge.terminology.Question newQuestion){
	if (q != null) {
		q.removeKnowledge(
			getProblemsolverContext(),
			this,
			PSMethodShared.SHARED_LOCAL_WEIGHT);
	}
	q = newQuestion;
	if (newQuestion != null) {
		q.addKnowledge(
			getProblemsolverContext(),
			this,
			PSMethodShared.SHARED_LOCAL_WEIGHT);
	}
}
	
	public Question getQuestion(){
		return q;
	}

	public void setDiagnosis(de.d3web.core.knowledge.terminology.Solution newDiagnosis){
		if (d != null) {
			d.removeKnowledge(
				getProblemsolverContext(),
				this,
				PSMethodShared.SHARED_LOCAL_WEIGHT);
		}
		d = newDiagnosis;
		if (newDiagnosis != null) {
			q.addKnowledge(
				getProblemsolverContext(),
				this,
				PSMethodShared.SHARED_LOCAL_WEIGHT);
		}
	}
	
	public Solution getDiagnosis(){
		return d;
	}
	
	
	/* (non-Javadoc)
	 * @see de.d3web.kernel.domainModel.KnowledgeSlice#getId()
	 */
	public java.lang.String getId() {
		return "W"+getQuestion().getId();
	}

	/**
	 * Returns the class of the PSMethod in which this
	 * KnowledgeSlice makes sense.
	 * @return java.lang.Class PSMethod class
	 */
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodShared.class;
	}

	/* (non-Javadoc)
	 * @see de.d3web.kernel.domainModel.KnowledgeSlice#isUsed(de.d3web.kernel.Session)
	 */
	public boolean isUsed(Session theCase) {
		return true;
	}
	
	public static double convertConstantStringToValue(String c) {

		if (c.equalsIgnoreCase("G0")) {
			return G0;
		}
		if (c.equalsIgnoreCase("G1")) {
			return G1;
		}
		if (c.equalsIgnoreCase("G2")) {
			return G2;
		}
		if (c.equalsIgnoreCase("G3")) {
			return G3;
		}
		if (c.equalsIgnoreCase("G4")) {
			return G4;
		}
		if (c.equalsIgnoreCase("G5")) {
			return G5;
		}
		if (c.equalsIgnoreCase("G6")) {
			return G6;
		}
		if (c.equalsIgnoreCase("G7")) {
			return G7;
		}

		return 0;
	}
	public static String convertValueToConstantString(double value) {

		if (value < G1) {
			return "G0";
		}
		if (value < G2) {
			return "G1";
		}
		if (value < G3) {
			return "G2";
		}
		if (value < G4) {
			return "G3";
		}
		if (value < G5) {
			return "G4";
		}
		if (value < G6) {
			return "G5";
		}
		if (value < G7) {
			return "G6";
		}
		return "G7";
	}

	public void remove() {
		setQuestion(null);
		setDiagnosis(null);
	}
	
	public Enumeration<Value> getAnswerEnumeration() {
		return values.keys();
	}

}