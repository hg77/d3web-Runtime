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

package de.d3web.diaFlux.inference;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.PostHookablePSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EdgeMap;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowRun;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.NodeList;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;

/**
 * 
 * @author Reinhard Hatko
 * @created: 10.09.2009
 * 
 */
public class FluxSolver implements PostHookablePSMethod, SessionObjectSource<DiaFluxCaseObject> {

	public final static KnowledgeKind<EdgeMap> DEPENDANT_EDGES = new KnowledgeKind<EdgeMap>(
			"DEPENDANT_EDGES", EdgeMap.class);
	public final static KnowledgeKind<NodeList> DEPENDANT_NODES = new KnowledgeKind<NodeList>(
			"DEPENDANT_NODES", NodeList.class);

	public static final Object SNAPSHOT_SOURCE = SnapshotNode.class;

	public FluxSolver() {
	}

	@Override
	public void init(Session session) {

		if (!DiaFluxUtils.isFlowCase(session)) {
			return;
		}

		Logger.getLogger(FluxSolver.class.getName()).fine(
				"Initing FluxSolver with case: " + session);

		try {
			session.getPropagationManager().openPropagation();

			List<StartNode> list = DiaFluxUtils.getAutostartNodes(session.getKnowledgeBase());

			for (StartNode startNode : list) {
				start(session, startNode);
			}

		}
		finally {
			session.getPropagationManager().commitPropagation();
		}

	}

	public static void start(Session session, StartNode startNode) {

		Logger.getLogger(FluxSolver.class.getName()).fine(
				"Activating startnode '" + startNode.getName() + "' of flow '"
						+ startNode.getFlow().getName() + "'.");

		FlowRun run = new FlowRun();
		run.addStartNode(startNode);
		DiaFluxUtils.getDiaFluxCaseObject(session).addRun(run);
		activateNode(startNode, run, session);
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {

		if (!DiaFluxUtils.isFlowCase(session)) {
			return;
		}

		Logger.getLogger(FluxSolver.class.getName()).fine("Start propagating: " + changes);
		List<FlowRun> runs = DiaFluxUtils.getDiaFluxCaseObject(session).getRuns();

		for (PropagationEntry propagationEntry : changes) {

			// strategic entries do not matter so far...
			if (propagationEntry.isStrategic()) {
				continue;
			}

			TerminologyObject object = propagationEntry.getObject();

			EdgeMap slice = object.getKnowledgeStore().getKnowledge(DEPENDANT_EDGES);

			// TO does not occur in any edge
			if (slice == null) {
				continue;
			}

			// These lists contain the edges that have to be de-/activated
			// the edges are processed afterwards in the right order,
			// at first the deactivations, then the activations
			List<Edge> activate = new LinkedList<Edge>();
			List<Edge> deactivate = new LinkedList<Edge>();

			// iterate over all edges that contain the changed TO
			for (Edge edge : slice.getEdges()) {

				Node start = edge.getStartNode();
				Node end = edge.getEndNode();

				for (FlowRun flowRun : runs) {
					boolean active = evalEdge(session, edge);
					if (active) {
						// begin node is not active, do nothing
						if (flowRun.isActive(start)) {
							// Edge was not active before
							if (!flowRun.isActivated(end)) {
								activate.add(edge);
							}
						}
					}
					else {
						// start node is not active (maybe was before)
						if (flowRun.isActivated(end)) {
							// if the end node is active
							boolean support = checkSupport(session, end, flowRun);
							// ...but is no longer supported by any other edge
							// (then the start node of the edge was active)
							if (!support) {
								// we also have to deactivate the end node
								deactivate.add(edge);
							}
						}
					}
				}
			}

			// now process the edges accordingly
			for (FlowRun flowRun : runs) {

				for (Edge edge : deactivate) {
					deactivateNode(edge.getEndNode(), flowRun, session);
				}

				for (Edge edge : activate) {
					// we need to check the activation state of the start
					// node again, as it may no longer be active due to the
					// deactivations just made
					if (flowRun.isActive(edge.getStartNode())) {
						activateNode(edge.getEndNode(), flowRun, session);
					}
				}
			}

		}

		// check backward knowledge: nodes that uses other objects to calculate
		// e.g. a formula
		for (PropagationEntry propagationEntry : changes) {
			TerminologyObject object = propagationEntry.getObject();
			NodeList knowledge =
					object.getKnowledgeStore().getKnowledge(DEPENDANT_NODES);

			if (knowledge == null) continue;

			for (Node node : knowledge) {
				for (FlowRun run : DiaFluxUtils.getDiaFluxCaseObject(session).getRuns()) {
					if (run.isActive(node) && node.isReevaluate(session)) {
						activate(session, node, run);
					}
				}
			}
		}
		Logger.getLogger(FluxSolver.class.getName()).fine("Finished propagating.");

	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param session
	 * @param end
	 * @param flowRun
	 */
	public static boolean checkSupport(Session session, Node end, FlowRun flowRun) {
		List<Edge> inc = end.getIncomingEdges();
		for (Edge edge : inc) {
			if (evalEdge(session, edge)) {
				if (flowRun.isActive(edge.getStartNode())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param node
	 * @param flowRun
	 * @param session
	 */
	public static void deactivateNode(Node node, FlowRun flowRun, Session session) {
		flowRun.remove(node);
		deactivate(session, node, flowRun);
		checkSuccessorsOnDeactivation(node, flowRun, session);
	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param end
	 * @param flowRun
	 * @param session
	 */
	public static void checkSuccessorsOnDeactivation(Node end, FlowRun flowRun, Session session) {
		for (Edge out : end.getOutgoingEdges()) {
			if (flowRun.isActive(out.getEndNode())) {
				if (!checkSupport(session, out.getEndNode(), flowRun)) {
					deactivateNode(out.getEndNode(), flowRun, session);
				}
			}
		}
	}

	@Override
	public DiaFluxCaseObject createSessionObject(Session session) {
		return new DiaFluxCaseObject();
	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param node
	 * @param flowRun
	 * @param session
	 */
	public static void activateNode(Node node, FlowRun flowRun, Session session) {
		boolean alreadyDone = flowRun.isActivated(node);
		if (alreadyDone) {
			return;
		}
		flowRun.add(node);
		activate(session, node, flowRun);
		// propagating after snapshot nodes startes in postpropagation
		if (!(node instanceof SnapshotNode)) {
			checkSuccessorsOnActivation(node, flowRun, session);
		}
	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param end
	 * @param flowRun
	 * @param session
	 */
	public static void checkSuccessorsOnActivation(Node end, FlowRun flowRun, Session session) {
		for (Edge out : end.getOutgoingEdges()) {
			if (!evalEdge(session, out)) {
				continue;
			}
			activateNode(out.getEndNode(), flowRun, session);
		}
	}

	/**
	 * 
	 * @created 17.02.2011
	 * @param session
	 * @param edge
	 * @return
	 */
	public static boolean evalEdge(Session session, Edge edge) {
		if (!FluxSolver.evalToTrue(session, edge.getStartNode().getEdgePrecondition())) return false;
		return evalToTrue(session, edge.getCondition());
	}

	@Override
	public void postPropagate(Session session) {
		if (!DiaFluxUtils.isFlowCase(session)) {
			return;
		}
		DiaFluxCaseObject caseObject = DiaFluxUtils.getDiaFluxCaseObject(session);

		// get all entered snapshots ES
		// get all flows that entered a snapshot (in ES): SF
		// get all nodes to be snap-shoted (from all flows in SF): SnappyNodes
		// for all n in SnappyNodes
		// ...snapshot n
		// ...deactivate n
		// for all shotshot nodes s in ES
		// ...create new flow with s and parents as start node
		// remove all flows f in SF from the active flows

		Collection<SnapshotNode> enteredSnapshots = caseObject.getActivatedSnapshots(session);
		if (enteredSnapshots.isEmpty()) {
			return;
		}

		Map<FlowRun, Collection<SnapshotNode>> snappyFlows = getFlowRunsWithEnteredSnapshot(
				enteredSnapshots, caseObject);
		Logger.getLogger(FluxSolver.class.getName()).fine("Taking snapshots: " + snappyFlows);

		// Calculate new flow runs (before changing anything in the session)
		Collection<FlowRun> newRuns = new HashSet<FlowRun>();
		for (SnapshotNode snapshotNode : enteredSnapshots) {
			FlowRun run = new FlowRun();
			run.addStartNode(snapshotNode);
			Collection<Node> snappyNodes =
					getActiveNodesLeadingToSnapshopNode(snapshotNode, snappyFlows.keySet());
			addParentsToStartNodes(run, snapshotNode, snappyNodes, session);
			newRuns.add(run);
			// inform the flux solver that this snapshot node has been
			// snapshoted
			caseObject.snapshotDone(snapshotNode, session);
		}

		// Make snapshot of all related nodes
		for (FlowRun flow : snappyFlows.keySet()) {
			Collection<Node> activeNodes = flow.getActiveNodes();
			for (Node node : activeNodes) {
				node.takeSnapshot(session);
			}
			for (Node node : activeNodes) {
				node.retract(session, flow);
			}
		}

		// Remove the old/snapshoted flow runs
		for (FlowRun run : snappyFlows.keySet()) {
			caseObject.removeRun(run);
		}

		// Add and propagate the new flow runs
		for (FlowRun run : newRuns) {
			caseObject.addRun(run);
			for (Node node : run.getActiveNodesOfClass(SnapshotNode.class)) {
				checkSuccessorsOnActivation(node, run, session);
			}
		}

	}



	/**
	 * Creates a collection of all nodes that are active in any flow run that
	 * leads towards this snapshot node.
	 * 
	 * @created 28.02.2011
	 * @param snapshotNode the target snapshot node
	 * @param caseObject the case object of this session
	 * @return the list of active snapshots
	 */
	public static Collection<Node> getActiveNodesLeadingToSnapshopNode(SnapshotNode snapshotNode, Collection<FlowRun> snapshotFlows) {
		Collection<Node> result = new HashSet<Node>();
		for (FlowRun run : snapshotFlows) {
			if (run.isActivated(snapshotNode)) {
				result.addAll(run.getActiveNodes());
			}
		}
		return result;
	}

	private void addParentsToStartNodes(FlowRun run, SnapshotNode snapshotNode, Collection<Node> snappyNodes, Session session) {
		// compute parents of snapshotNode
		// compute callstack of these parents
		Collection<Node> parents = new HashSet<Node>();
		computeParentsRecursive(snapshotNode, parents, snappyNodes);

		for (Node parent : parents) {
			if (hasIncomingActivation(parent, snappyNodes, session)
					|| hasNotLeft(parent, snappyNodes, session)) {
				run.addStartNode(parent);
			}
		}
	}

	private boolean hasNotLeft(Node node, Collection<Node> allActiveNodes, Session session) {
		if (allActiveNodes.contains(node)) {
			for (Edge edge : node.getOutgoingEdges()) {
				if (evalToTrue(session, edge.getCondition())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private static boolean hasIncomingActivation(Node child, Collection<Node> allActiveNodes, Session session) {
		for (Edge edge : child.getIncomingEdges()) {
			if (allActiveNodes.contains(edge.getStartNode())
					&& evalToTrue(session, edge.getCondition())) {
				return true;
			}
		}
		return false;
	}

	private static void computeParentsRecursive(Node child, Collection<Node> result, Collection<Node> allNodes) {
		Flow calledFlow = child.getFlow();
		for (Node node : allNodes) {
			if (node instanceof ComposedNode) {
				String calledFlowname = ((ComposedNode) node).getCalledFlowName();
				if (calledFlow.getName().equals(calledFlowname)) {
					result.add(node);
					computeParentsRecursive(node, result, allNodes);
				}
			}
		}
	}

	public static Map<FlowRun, Collection<SnapshotNode>>
			getFlowRunsWithEnteredSnapshot(Collection<SnapshotNode> enteredSnapshots,
					DiaFluxCaseObject caseObject) {
		Map<FlowRun, Collection<SnapshotNode>> snappyRuns = new HashMap<FlowRun, Collection<SnapshotNode>>();

		for (FlowRun flowRun : caseObject.getRuns()) {
			for (SnapshotNode snapshotNode : enteredSnapshots) {
				if (flowRun.isActive(snapshotNode)) {
					Collection<SnapshotNode> snapshotNodes = snappyRuns.get(flowRun);
					if (snapshotNodes == null) {
						snapshotNodes = new HashSet<SnapshotNode>();
					}
					snapshotNodes.add(snapshotNode);
					snappyRuns.put(flowRun, snapshotNodes);
				}
			}
		}
		return snappyRuns;
	}

	public static boolean evalToTrue(Session session, Condition condition) {
		try {
			return condition.eval(session);
		}
		catch (NoAnswerException e) {
			return false;
		}
		catch (UnknownAnswerException e) {
			return false;
		}
	}

	public static void deactivate(Session session, Node node, FlowRun flowRun) {
		Logger.getLogger(FluxSolver.class.getName()).fine("Deactivating node: " + node);

		node.retract(session, flowRun);
	}

	public static void activate(Session session, Node node, FlowRun flowRun) {
		Logger.getLogger(FluxSolver.class.getName()).fine("Activating node: " + node);

		node.execute(session, flowRun);
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		if (facts[0].getValue() instanceof Indication) {
			return Facts.mergeIndicationFacts(facts);
		}
		else {

			for (Fact fact : facts) {
				if (!(fact.getSource() == SNAPSHOT_SOURCE)) {
					return fact;
				}
			}
			return facts[0];
		}
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.strategic || type == Type.problem;
	}

	@Override
	public double getPriority() {
		// default priority
		return 5;
	}

}
