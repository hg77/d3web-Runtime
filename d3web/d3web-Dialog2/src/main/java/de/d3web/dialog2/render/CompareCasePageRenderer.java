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

package de.d3web.dialog2.render;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.ajax4jsf.ajax.html.HtmlAjaxCommandLink;
import org.apache.log4j.Logger;
import org.apache.myfaces.component.html.ext.HtmlInputHidden;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.component.html.UICompareCasePage;
import de.d3web.dialog2.controller.CompareCaseController;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.psMethods.compareCase.CompareCaseException;
import de.d3web.kernel.psMethods.compareCase.comparators.ComparatorResult;
import de.d3web.kernel.psMethods.compareCase.facade.ComparisonResultRepository;
import de.d3web.kernel.psMethods.compareCase.facade.DetailledResult;
import de.d3web.kernel.psMethods.compareCase.facade.SimpleResult;

public class CompareCasePageRenderer extends Renderer {

	public static Logger logger = Logger
			.getLogger(CompareCasePageRenderer.class);

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		Session theCase = DialogUtils.getDialog().getSession();

		CompareCaseController compareCase = DialogUtils.getCompareCaseBean();
		ComparisonResultRepository crepos = new ComparisonResultRepository();
		crepos.setCurrentCase(theCase);
		crepos.setCompareMode(compareCase.retrieveCompareModeFromParam());

		String compMethod;

		String compID;

		// if comparecase gets started...
		String initcompType = (String) ((UICompareCasePage) component)
				.getValue();
		if (initcompType != null && initcompType.equals("new")) {
			compMethod = "simple";
			compID = "0";
		}
		else {
			HtmlInputHidden method = (HtmlInputHidden) context.getViewRoot()
					.findComponent("compareCaseForm:compType");
			if (method != null) {
				compMethod = (String) method.getValue();
			}
			else {
				compMethod = "simple";
			}

			HtmlInputHidden cID = (HtmlInputHidden) context.getViewRoot()
					.findComponent("compareCaseForm:compID");
			if (cID != null) {
				compID = (String) cID.getValue();
			}
			else {
				compID = "0";
			}
		}

		if (compMethod.equals("simple")) {
			renderSimple(writer, component, theCase, crepos, compareCase);
		}
		else if (compMethod.equals("container")) {
			renderContainer(writer, component, theCase, crepos, compareCase,
					compID);
		}
		else if (compMethod.equals("detailled")) {
			renderDetailled(writer, component, theCase, crepos, compareCase,
					compID);
		}
		else {
			renderErrorMsg(writer, component);
		}
	}

	private Collection<Choice> formatAnswersForOutput(Collection<Choice> answers) {
		if (answers == null) {
			answers = new LinkedList<Choice>();
		}
		return answers;
	}

	private String formatDouble(double d, boolean forDetailledOrContainer) {
		if (forDetailledOrContainer) {
			double k = (int) (d * 100);
			double res = k / 100.0;
			return Double.toString(res);
		}
		else {
			double k = (int) (d * 1000);
			double res = k / 10.0;
			return Double.toString(res);
		}
	}

	private void renderAnswerValue(ResponseWriter writer,
			UIComponent component, Value ans, Session theCase)
			throws IOException {
		writer.startElement("p", component);
		writer.writeText(ans.getValue().toString(), "value");
		writer.endElement("p");
	}

	private void renderBackButton(UIComponent component) throws IOException {
		UIComponent facet = component.getFacet("backbutton");
		if (facet != null) {
			DialogRenderUtils.renderChild(FacesContext.getCurrentInstance(),
					facet);
		}
	}

	private void renderContainer(ResponseWriter writer, UIComponent component,
			Session theCase, ComparisonResultRepository crepos,
			CompareCaseController compareCase, String compID)
			throws IOException {
		String kbid = theCase.getKnowledgeBase().getId();

		List<DetailledResult> results = null;
		try {
			results = crepos.getDetailledResults(CaseManager.getInstance()
					.getCase(kbid, compID));
		}
		catch (CompareCaseException e) {
			logger.warn(e);
			renderErrorMsg(writer, component);
		}
		if ((results != null) && !results.isEmpty()) {

			DialogRenderUtils.renderTableWithClass(writer, component,
					"compareCaseTable");
			writer.writeAttribute("id", component.getClientId(FacesContext
					.getCurrentInstance())
					+ "_cont", "id");
			DialogRenderUtils.renderTableHeadRow(writer, component,
					new String[] {
					DialogUtils.getMessageFor("cbr.container.name"),
					DialogUtils.getMessageFor("cbr.similarity") });
			Iterator<DetailledResult> iter = results.iterator();

			while (iter.hasNext()) {
				DetailledResult detres = iter.next();

				writer.startElement("tr", component);

				writer.startElement("td", component);
				writer.writeAttribute("id", "contname_"
						+ detres.getContainerId(), "id");
				writer.writeText(detres.getContainerName(), "value");
				writer.endElement("td");

				writer.startElement("td", component);
				writer.writeAttribute("id", "contsim_"
						+ detres.getContainerId(), "id");
				renderSimilarityText(detres.getContainerSimilarity(), detres
						.getMaxContainerPoints(), detres
						.getReachedContainerPoints());
				writer.endElement("td");

				writer.endElement("tr");
			}
			writer.endElement("table");
		}
		else {
			renderNoCaseMessage(writer, component);
		}
		renderBackButton(component);
	}

	private void renderDetailled(ResponseWriter writer, UIComponent component,
			Session theCase, ComparisonResultRepository crepos,
			CompareCaseController compareCase, String caseid)
			throws IOException {
		String kbid = theCase.getKnowledgeBase().getId();

		List<DetailledResult> results = null;

		try {
			results = crepos.getDetailledResults(CaseManager.getInstance()
					.getCase(kbid, caseid));
		}
		catch (CompareCaseException e) {
			logger.warn(e);
			renderErrorMsg(writer, component);
		}

		if ((results != null) && !results.isEmpty()) {

			// "show unknown" button
			renderShowUnknownLink(component, compareCase);

			DialogRenderUtils.renderTableWithClass(writer, component,
					"compareCaseTable");
			writer.writeAttribute("width", "100%", "width");
			writer.writeAttribute("id", component.getClientId(FacesContext
					.getCurrentInstance())
					+ "_det", "id");

			DialogRenderUtils
					.renderTableHeadRow(
					writer,
					component,
					new String[] {
					DialogUtils
							.getMessageFor("cbr.detailled.name"),
					DialogUtils
							.getMessageFor("cbr.detailled.currentvalue"),
					DialogUtils
							.getMessageFor("cbr.detailled.casevalue"),
					DialogUtils.getMessageFor("cbr.similarity") });

			Iterator<DetailledResult> iter = results.iterator();
			while (iter.hasNext()) {
				DetailledResult detres = iter.next();

				writer.startElement("tr", component);
				writer.writeAttribute("class", "qContainer", "class");
				writer.startElement("td", component);
				writer.writeAttribute("id", "contname_"
						+ detres.getContainerId(), "id");
				writer.writeText(detres.getContainerName(), "value");
				writer.endElement("td");

				DialogRenderUtils.renderEmptyTableCell(writer, component);

				DialogRenderUtils.renderEmptyTableCell(writer, component);

				writer.startElement("td", component);
				writer.writeAttribute("id", "contsim_"
						+ detres.getContainerId(), "id");
				renderSimilarityText(detres.getContainerSimilarity(), detres
						.getMaxContainerPoints(), detres
						.getReachedContainerPoints());
				writer.endElement("td");

				writer.endElement("tr");

				Iterator<ComparatorResult> questions = detres
						.getDetailledQuestionResults().iterator();
				while (questions.hasNext()) {
					ComparatorResult cr = questions.next();
					Question question = cr.getQueryQuestion();
					if (question == null) {
						question = cr.getStoredQuestion();
					}
					Value queryAnswers = cr.getQueryValue();
					Value storedAnswers = cr.getStoredValue();

					boolean existQueryAnswers = (queryAnswers != null)
							&& !(queryAnswers instanceof UndefinedValue);
					boolean existStoredAnswers = (storedAnswers != null)
							&& !(storedAnswers instanceof UndefinedValue);

					boolean showQuestion = compareCase.isShowUnknown()
							|| !(Unknown.assignedTo(queryAnswers) && Unknown.assignedTo(storedAnswers));

					if ((existQueryAnswers || existStoredAnswers)
							&& showQuestion) {

						writer.startElement("tr", component);
						writer.startElement("td", component);
						writer.writeAttribute("id",
								"qname_" + question.getId(), "id");
						writer.writeText(question.getName(), "value");
						writer.endElement("td");

						// Aktuelle Answers
						writer.startElement("td", component);
						writer.writeAttribute("id", "qansquery_"
								+ question.getId(), "id");
						if (queryAnswers == null
								|| queryAnswers instanceof UndefinedValue) {
							writer.write("-");
						}
						else {
							renderAnswerValue(writer, component, queryAnswers, theCase);
						}
						writer.endElement("td");

						// Case-Answers
						writer.startElement("td", component);
						writer.writeAttribute("id", "qansstored_"
								+ question.getId(), "id");
						if (storedAnswers == null
								|| storedAnswers instanceof UndefinedValue) {
							writer.write("-");
						}
						else {
							renderAnswerValue(writer, component, storedAnswers, theCase);
						}
						writer.endElement("td");

						// Similarity
						writer.startElement("td", component);
						writer.writeAttribute("id", "qanssim_"
								+ question.getId(), "id");
						renderSimilarityText(cr.getSimilarity(), cr
								.getMaxPoints(), cr.getReachedPoints());
						writer.endElement("td");

						writer.endElement("tr");
					}

				}
			}
			writer.endElement("table");
		}
		else {
			renderNoCaseMessage(writer, component);
		}
		renderBackButton(component);
	}

	private void renderErrorMsg(ResponseWriter writer, UIComponent component)
			throws IOException {
		writer.startElement("p", component);
		writer
				.writeText(DialogUtils.getMessageFor("cbr.errormessage"),
				"value");
		writer.endElement("p");
	}

	private void renderNoCaseMessage(ResponseWriter writer,
			UIComponent component) throws IOException {
		writer.startElement("h3", component);
		writer.writeText(DialogUtils.getMessageFor("cbr.nocasemessage"),
				"value");
		writer.endElement("h3");
	}

	private void renderShowUnknownLink(UIComponent component,
			CompareCaseController compareCase) throws IOException {
		// ResponseWriter writer =
		// FacesContext.getCurrentInstance().getResponseWriter();
		// DialogRenderUtils.renderTable(writer, component);
		// writer.writeAttribute("width", "100%", "width");
		// writer.startElement("tr", component);
		// writer.startElement("td", component);
		// writer.writeAttribute("align", "right", "align");
		// writer.startElement("div", component);

		if (compareCase.isShowUnknown()) {
			UIComponent facet = component.getFacet("hideUnknown");
			if (facet != null) {
				DialogRenderUtils.renderChild(
						FacesContext.getCurrentInstance(), facet);
			}
		}
		else {
			UIComponent facet = component.getFacet("showUnknown");
			if (facet != null) {
				DialogRenderUtils.renderChild(
						FacesContext.getCurrentInstance(), facet);
			}
		}
		// writer.endElement("td");
		// writer.endElement("tr");
		// writer.endElement("table");
	}

	private void renderSimilarityText(double similarity, double maxPoints,
			double reachedPoints) throws IOException {
		ResponseWriter writer = FacesContext.getCurrentInstance()
				.getResponseWriter();
		writer.writeText(formatDouble(similarity, true), "value");
		writer.writeText(" = ", "value");
		writer.writeText(formatDouble(maxPoints, true), "value");
		writer.writeText(" * ", "value");
		writer.writeText(formatDouble(reachedPoints, true), "value");
	}

	private void renderSimple(ResponseWriter writer, UIComponent component,
			Session theCase, ComparisonResultRepository crepos,
			CompareCaseController compareCase) throws IOException {
		List<SimpleResult> cases = null;
		try {
			cases = crepos.getSimpleResults(CaseManager.getInstance()
					.getCasesForKb(theCase.getKnowledgeBase().getId()));
		}
		catch (Exception e) {
			logger.error(e);
			renderErrorMsg(writer, component);
		}
		if ((cases != null) && !cases.isEmpty()) {

			DialogRenderUtils.renderTableWithClass(writer, component,
					"compareCaseTable");
			writer.writeAttribute("id", component.getClientId(FacesContext
					.getCurrentInstance())
					+ "_simple", "id");
			// tableheadline rendern...
			DialogRenderUtils.renderTableHeadRow(writer, component,
					new String[] {
					DialogUtils.getMessageFor("cbr.simple.name"),
					DialogUtils.getMessageFor("cbr.similarity"),
					DialogUtils.getMessageFor("cbr.simple.solution"),
					DialogUtils.getMessageFor("cbr.simple.choice") });

			int index = 0;
			Iterator<SimpleResult> iter = cases.iterator();
			while (iter.hasNext() && (index < 10)) {
				index++;

				SimpleResult simres = iter.next();

				String caseName = "noName";
				try {
					caseName = simres.getCase().getDCMarkup().getContent(
							DCElement.TITLE);
				}
				catch (Exception x) {
					logger.warn("Case has no name!");
				}

				writer.startElement("tr", component);

				writer.startElement("td", component);
				writer.writeAttribute("id", "casename_" + caseName, "id");
				writer.writeText(caseName, "value");
				writer.endElement("td");

				writer.startElement("td", component);
				writer.writeAttribute("id", "casesimilarity_" + caseName, "id");
				writer.writeText(formatDouble(simres.getSimilarity(), false),
						"value");
				writer.writeText(" %", "value");
				writer.endElement("td");

				writer.startElement("td", component);
				writer.writeAttribute("id", "casesolutions_" + caseName, "id");
				Iterator<Solution> diags = simres.getDiagnoses().iterator();
				if (!diags.hasNext()) {
					writer.writeText(DialogUtils
							.getMessageFor("cbr.simple.nosolution"), "value");
				}
				while (diags.hasNext()) {
					Solution diag = diags.next();
					writer.startElement("p", component);
					writer.writeAttribute("id", "casesolution_" + caseName
							+ "_" + diag.getId(), "id");
					writer.writeText(diag.getName(), "value");
					writer.endElement("p");
				}
				writer.endElement("td");

				writer.startElement("td", component);

				writer.startElement("div", component);
				String id = simres.getCase().getId();
				writer.writeAttribute("class", "link qcont_" + id, "class");
				HtmlAjaxCommandLink contFacet = (HtmlAjaxCommandLink) component
						.getFacet("startContainerButton");
				contFacet.setOnclick("startCBRContainer('" + id + "')");
				DialogRenderUtils.renderChild(
						FacesContext.getCurrentInstance(), contFacet);
				writer.endElement("div");

				writer.startElement("div", component);
				writer.writeAttribute("class", "link det_" + id, "class");
				HtmlAjaxCommandLink detFacet = (HtmlAjaxCommandLink) component
						.getFacet("startDetailledButton");
				detFacet.setOnclick("startCBRDetailled('" + id + "')");
				DialogRenderUtils.renderChild(
						FacesContext.getCurrentInstance(), detFacet);
				writer.endElement("div");
				writer.endElement("td");
			}
			writer.endElement("table");
		}
		else {
			renderNoCaseMessage(writer, component);
		}
	}

}
