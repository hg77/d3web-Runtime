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

package de.d3web.kernel.psMethods.diaFlux.flow;

import java.util.ArrayList;
import java.util.List;

import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondAnd;
import de.d3web.kernel.psMethods.questionSetter.ActionSetValue;

/**
 * 
 * @author Reinhard Hatko
 *
 */
public class FlowFactory {
	
	
	private static final FlowFactory instance;
	
	static {
		instance = new FlowFactory();
	}
	
	
	public static FlowFactory getInstance() {
		return instance;
	}
	
	
	private FlowFactory() {
		
	}
	
	
	public Flow createFlow(String id, String name, List<INode> nodes, List<IEdge> edges) {
		return new Flow(id, name, nodes, edges);
		
	}
	
	public INode createNode(String id, RuleAction action) {
		return new Node(id, action);
		
	}
	
	public IEdge createEdge(String id, INode startNode, INode endNode, AbstractCondition condition) {
		return new Edge(id, startNode, endNode, condition);
	}
	
	public INode createStartNode(String id, String name) {
		return new StartNode(id, name);
	}
	
	public INode createEndNode(String id, String name, RuleAction action) {
			
		
		return new EndNode(id, name, action);
	}
	
	
	//Fix after Refactoring
	public ActionSetValue createSetValueAction() {
		RuleComplex rule = new RuleComplex();
		rule.setId("FlowchartRule" + System.currentTimeMillis());
		
		ActionSetValue action = new ActionSetValue(rule);
		rule.setAction(action);
		rule.setCondition(new CondAnd(new ArrayList()));
		return action;
	}
	
	

}
