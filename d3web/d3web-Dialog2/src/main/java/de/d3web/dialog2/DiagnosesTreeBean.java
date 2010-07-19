/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.dialog2;

import java.util.List;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.session.Session;
import de.d3web.dialog2.util.DialogUtils;

public class DiagnosesTreeBean {

	public static final String STANDARD_TYPE = "diagnosisTree";

	public static final String EXCLUDED_TYPE = "diagExcl";

	public static final String ESTABLISHED_TYPE = "diagEstab";

	public static final String SUGGESTED_TYPE = "diagSugg";

	private TreeModel diagTreeModel;

	private HtmlTree diagTree;

	public void checkNodeStyles(Session session) {
		TreeNode root = diagTreeModel.getNodeById("0");
		checkNodeStylesRecursive(root, session);

	}

	private void checkNodeStylesRecursive(TreeNode node, Session session) {
		Solution actual = session.getKnowledgeBase().searchSolution(
				node.getIdentifier());
		Rating state = session.getBlackboard().getRating(actual);
		if (state.hasState(State.ESTABLISHED)) {
			node.setType(DiagnosesTreeBean.ESTABLISHED_TYPE);
		}
		else if (state.hasState(State.EXCLUDED)) {
			node.setType(DiagnosesTreeBean.EXCLUDED_TYPE);
		}
		else if (state.hasState(State.SUGGESTED)) {
			node.setType(DiagnosesTreeBean.SUGGESTED_TYPE);
		}
		else {
			node.setType(DiagnosesTreeBean.STANDARD_TYPE);
		}
		if (node.getChildCount() > 0) {
			for (int i = 0; i < node.getChildCount(); i++) {
				checkNodeStylesRecursive((TreeNode) node.getChildren().get(i),
						session);
			}
		}
	}

	private void createTreeRecursive(Solution diag, TreeNode parentNode) {
		parentNode.setIdentifier(diag.getId());
		if (diag.getChildren().length == 0) {
			parentNode.setLeaf(true);
			return;
		}
		else {
			TerminologyObject[] childrenList = diag.getChildren();
			for (int i = 0; i < childrenList.length; i++) {
				Solution diagChild = (Solution) childrenList[i];
				TreeNode newNode = new TreeNodeBase(
						DiagnosesTreeBean.STANDARD_TYPE, diagChild.getName(),
						false);
				parentNode.getChildren().add(newNode);
				createTreeRecursive(diagChild, newNode);
			}
		}
	}

	public boolean getDiagnosesAvailable() {
		Session session = DialogUtils.getDialog().getSession();
		List<Solution> established = session.getBlackboard()
				.getSolutions(State.ESTABLISHED);
		List<Solution> suggested = session.getBlackboard()
				.getSolutions(State.SUGGESTED);
		List<Solution> excluded = session.getBlackboard()
				.getSolutions(State.EXCLUDED);
		if (established.size() != 0 || suggested.size() != 0
				|| excluded.size() != 0) {
			return true;
		}
		return false;
	}

	public HtmlTree getDiagTree() {
		return diagTree;
	}

	public TreeModel getDiagTreeModel() {
		return diagTreeModel;
	}

	public void init() {
		Session session = DialogUtils.getDialog().getSession();
		initTreeModel(session);
		diagTree = new HtmlTree();
		diagTree.setModel(diagTreeModel);
		diagTree.expandAll();
		// check nodes
		checkNodeStyles(session);
	}

	private void initTreeModel(Session session) {
		Solution rootDiag = session.getKnowledgeBase().getRootSolution();
		TreeNode treeData = new TreeNodeBase(DiagnosesTreeBean.STANDARD_TYPE,
				rootDiag.getName(), false);
		createTreeRecursive(rootDiag, treeData);
		diagTreeModel = new TreeModelBase(treeData);
	}

	public void setDiagTree(HtmlTree diagTree) {
		this.diagTree = diagTree;
	}

	public void setDiagTreeModel(TreeModel diagTreeModel) {
		this.diagTreeModel = diagTreeModel;
	}
}
