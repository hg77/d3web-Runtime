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

package de.d3web.scoring.inference;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.DiagnosisState;
import de.d3web.core.terminology.NamedObject;
import de.d3web.core.terminology.info.Property;
import de.d3web.scoring.DiagnosisScore;
import de.d3web.scoring.Score;

/**
 * Etablish-Refine-Strategy
 * 
 * @author pkluegl
 *
 */
public class ERSMethod extends PSSubMethod {
	
	private static ERSMethod instance = new ERSMethod();
	private ERSMethod() {
		super();
		setContributingToResult(true);
	}
	public static ERSMethod getInstance() {
		return instance;
	}
	
	/*
	 * key:				the case
	 * Object:			HashMap (called innerMap) 
	 * innerMap key:	parentDiagnosis
	 * innerMap Object:	list of ChangeOfDiagnosis
	 */
	private Map<XPSCase, Map<Diagnosis, List<ChangeOfDiagnosis>>> hDT_establishedDiagnoses = new HashMap<XPSCase, Map<Diagnosis,List<ChangeOfDiagnosis>>>();
	Class<PSMethodHeuristic> PSCONTEXT = PSMethodHeuristic.class;
	
	/**
	 * This class contains the score that was added to a diagnosis
	 * @author Peter Klügl
	 */
	private class ChangeOfDiagnosis {
		private Diagnosis diagnosis;
		private Score score;
		
		public ChangeOfDiagnosis(Diagnosis diagnosis, Score score) {
			setDiagnosis(diagnosis);
			setScore(score);
		}
		public Diagnosis getDiagnosis() {
			return diagnosis;
		}
		public void setDiagnosis(Diagnosis diagnosis) {
			this.diagnosis = diagnosis;
		}
		public Score getScore() {
			return score;
		}
		public void setScore(Score score) {
			this.score = score;
		}
		public String toString() {
			return "[Diagnose : "+diagnosis+"] - [Score : "+score+"]";
		}
	}
		
	
	/**
	 * initialization method for this PSMethod
	 */
	public void init(XPSCase theCase) {
	}
	
	/**
	 * propergates the new value of the given NamedObject for the given XPSCase
	 */
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
			if (change.getObject() instanceof Diagnosis) {
				Diagnosis diagnosis = (Diagnosis) change.getObject();
				checkHashMap(theCase);
				if (isEstablished(theCase, diagnosis)) {
					setAllChildrenToSuggested(theCase, diagnosis);
				} 
				else if(wasEstablished(theCase, diagnosis)){
					resetAllChildren(theCase, diagnosis);
				}
			}
		}	
	}
	
	
	
	
	
	/**
	 * 
	 * @param theCase
	 * @param parentDiagnosis
	 */
	private void setAllChildrenToSuggested(XPSCase theCase, Diagnosis parentDiagnosis) {
		Map<Diagnosis, List<ChangeOfDiagnosis>> innerMap = getInnerMap(theCase);
		List<? extends NamedObject> children = parentDiagnosis.getChildren();
		if (children == null || children.isEmpty())
			return;
		for (NamedObject object : children) {
			if(object instanceof Diagnosis) {
				Diagnosis eachDiagnosis = (Diagnosis) object;
				if (eachDiagnosis.getState(theCase, PSCONTEXT).equals(DiagnosisState.UNCLEAR)) {
					rememberChange(theCase, innerMap, parentDiagnosis, eachDiagnosis);
					setDiagnosisToSuggested(theCase, eachDiagnosis);
				}
			}
		}
	}
	/**
	 * @param innerMap
	 * @param parentDiagnosis
	 * @param diagnosis
	 */
	private void rememberChange(XPSCase theCase, Map<Diagnosis, List<ChangeOfDiagnosis>> innerMap, Diagnosis parentDiagnosis, Diagnosis diagnosis) {
		List<ChangeOfDiagnosis> listOfChanges = innerMap.get(parentDiagnosis);
		if(listOfChanges == null) {
			listOfChanges = new LinkedList<ChangeOfDiagnosis>();
			innerMap.put(parentDiagnosis, listOfChanges);
		}
		if (getChangeOf(listOfChanges, diagnosis) == null) {
			listOfChanges.add(new ChangeOfDiagnosis(diagnosis, getDifferenceToSuggested(theCase, diagnosis)));
		} else {
			getChangeOf(listOfChanges, diagnosis).setScore(getDifferenceToSuggested(theCase, diagnosis));
		}
		
	}
	
	
	/**
	 * adds a score to the diagnosis
	 * @param theCase
	 * @param diagnosis
	 */
	private DiagnosisScore setDiagnosisToSuggested(XPSCase theCase, Diagnosis diagnosis) {
		DiagnosisScore resultDS = diagnosis.getScore(theCase, PSCONTEXT).add(getDifferenceToSuggested(theCase, diagnosis));
		theCase.setValue(diagnosis, new Object[] { resultDS }, PSCONTEXT);
		return resultDS;
	}
	
	/**
	 * subtracts the added Scores
	 * @param theCase
	 * @param diagnosis
	 */
	private void resetAllChildren(XPSCase theCase, Diagnosis diagnosis) {
		Map<Diagnosis, List<ChangeOfDiagnosis>> innerMap = getInnerMap(theCase);
		List<ChangeOfDiagnosis> listOfChanges = innerMap.get(diagnosis);
		if(listOfChanges == null || listOfChanges.isEmpty())
			return;
		List<? extends NamedObject> children = diagnosis.getChildren();
		if (children == null)
			return;
		for (NamedObject object : children) {
			if(object instanceof Diagnosis) {
				Diagnosis eachChildren = (Diagnosis) object;
				ChangeOfDiagnosis change = getChangeOf(listOfChanges, eachChildren);
				if (change != null) {
					innerMap.remove(diagnosis);
					DiagnosisScore resultDS = eachChildren.getScore(theCase, PSCONTEXT).subtract(change.getScore());
					theCase.setValue(eachChildren, new Object[] { resultDS }, PSCONTEXT);
				}
			}
		}
	}
	

	private boolean isEstablished(XPSCase theCase, Diagnosis diagnosis) {
		return diagnosis.getState(theCase, PSCONTEXT).equals(DiagnosisState.ESTABLISHED);
	}
	private boolean wasEstablished(XPSCase theCase, Diagnosis diagnosis) {
		return hDT_establishedDiagnoses.get(theCase).containsKey(diagnosis);
	}
	
	/**
	 * 
	 * @return the Score, that a diagnosis needs to be suggested
	 */
	private double getScoreForSuggestedDiagnosis() {
		return 10;
	}
	
	
	/**
	 * 
	 * @param listOfChanges
	 * @param diagnosis
	 * @return the correct Tupel
	 */
	private ChangeOfDiagnosis getChangeOf(List<ChangeOfDiagnosis> listOfChanges, Diagnosis diagnosis) {
		for (ChangeOfDiagnosis change : listOfChanges) {
			if(change.getDiagnosis().equals(diagnosis))
				return change;
		}
		return null;
	}
	
	/**
	 * secures, that all keys are already available
	 * @param theCase
	 * @param diagnosis
	 */
	private void checkHashMap(XPSCase theCase) {
		if(!hDT_establishedDiagnoses.containsKey(theCase)) {
			Map<Diagnosis, List<ChangeOfDiagnosis>> innerMap = new HashMap<Diagnosis, List<ChangeOfDiagnosis>>();
			hDT_establishedDiagnoses.put(theCase, innerMap);
		}
	}
	
	/**
	 * 
	 * @param theCase
	 * @return HashMap (innerMap) of the changes of the child - diagnoses
 	 */
	private Map<Diagnosis, List<ChangeOfDiagnosis>> getInnerMap(XPSCase theCase) {
		return hDT_establishedDiagnoses.get(theCase);
	}
	/**
	 * 
	 * @param theCase
	 * @param diagnosis
	 * @return the Score that the diagnosis need to be suggested
	 */
	private Score getDifferenceToSuggested(XPSCase theCase, Diagnosis diagnosis) {		
		double diff = getScoreForSuggestedDiagnosis() - diagnosis.getScore(theCase, PSCONTEXT).getScore();
		//[HOTFIX]: Peter: hat hier nix zu suchen!!
		if (diff >= Score.P5x.getScore())
			return Score.P5x;
		else if (diff >= Score.P5.getScore()) 
			return Score.P5;
		else if (diff >= Score.P4.getScore())
			return Score.P4;
		else if (diff >= Score.P3.getScore())
			return Score.P3;
		else if (diff >= Score.P2.getScore())
			return Score.P2;
		else return Score.P1;
		
	}
		
	public boolean isActivated(XPSCase theCase) {
		Boolean b = (Boolean)theCase.getKnowledgeBase().getProperties().getProperty(Property.ESTABLISH_REFINE_STRATEGY);
		return  b != null && b.booleanValue(); 
	}
	
	
}