package de.d3web.kernel.psMethods.SCMCBR;
import java.util.Collection;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.dynamicObjects.CaseDiagnosis;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PSMethodAdapter;
import de.d3web.kernel.psMethods.PropagationEntry;


public class PSMethodSCMCBR extends PSMethodAdapter {
	
	private static PSMethodSCMCBR instance = null;

	private PSMethodSCMCBR() {
	}
	
	public static PSMethodSCMCBR getInstance() {
		if (instance  == null) {
			instance = new PSMethodSCMCBR();
		}
		return instance;
	}
	
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		List<? extends KnowledgeSlice> models = diagnosis.getKnowledge(PSMethodSCMCBR.class, SCMCBRModel.SCMCBR);
		if (models == null || models.size() == 0) return DiagnosisState.UNCLEAR; 
		SCMCBRModel model = (SCMCBRModel) models.get(0);
		return model.getState(theCase);
	}
	
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		// TODO: implement well, as defined below
//		Set<XCLModel> modelsToUpdate = new HashSet<XCLModel>();
//		for (PropagationEntry change : changes) {
//			NamedObject nob = change.getObject();
//			List<? extends KnowledgeSlice> models = nob.getKnowledge(PSMethodXCL.class, XCLModel.XCL_CONTRIBUTED_MODELS);
//			if (models != null)  {
//				for (KnowledgeSlice model : models) {
//					modelsToUpdate.add((XCLModel) model);
//				}
//			}
//		}
//		for (XCLModel model : modelsToUpdate) {
//			DiagnosisState state = model.getState(theCase);
//			theCase.setValue(model.getSolution(), new DiagnosisState[]{state}, this.getClass());
//			model.notifyListeners(theCase, model);
//		}

		// TODO: remove this hack
		//only update if there is at least one question
		boolean hasQuestion = false;
		for (PropagationEntry change : changes) {
			if (change.getObject() instanceof Question) hasQuestion = true;
		}
		if (!hasQuestion) return;
		Collection<KnowledgeSlice> models = theCase.getKnowledgeBase().getAllKnowledgeSlicesFor(PSMethodSCMCBR.class);
		for (KnowledgeSlice knowledgeSlice : models) {
			if(knowledgeSlice instanceof SCMCBRModel) {
				SCMCBRModel model = (SCMCBRModel) knowledgeSlice;
				
				//Quick fix for ClassCastException:
				Object o =  ((CaseDiagnosis) theCase.getCaseObject(model.getSolution())).getValue(this.getClass());
				DiagnosisState oldState = null;
				if(o instanceof DiagnosisState) {
					oldState =(DiagnosisState)o;
				}
				if(o instanceof DiagnosisScore) {
					DiagnosisScore oldScore = (DiagnosisScore)o;
					oldState = DiagnosisState.getState(oldScore);
				}
				
				
				
				// TODO: split getState into getState and refreshState
				//DiagnosisState oldState = model.getState(theCase);
				//model.refreshState(theCase);
				DiagnosisState newState = model.getState(theCase);
				if (!oldState.equals(newState)) {
					theCase.setValue(model.getSolution(), new DiagnosisState[]{newState}, this.getClass());
				}
				model.notifyListeners(theCase, model);
			}
		} 
	}
	
}