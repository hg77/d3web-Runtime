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

package de.d3web.dialog2.controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.dialog2.WebDialog;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.basics.knowledge.KnowledgeBaseRepository;
import de.d3web.dialog2.basics.layout.DialogLayout;
import de.d3web.dialog2.basics.settings.DialogSettings;
import de.d3web.dialog2.imagemap.ImageMapBean;
import de.d3web.dialog2.render.KBLoadRenderer;
import de.d3web.dialog2.util.DialogUtils;

public class KBLoadController {

	private String kbID;

	public static Logger logger = Logger.getLogger(KBLoadController.class);

	private KnowledgeBase loadedKb;

	public void checkMultimediaFiles() {

		// check if a dialoglayout.xml file is available for the kb
		if (DialogUtils.fileAvailableForKB(FacesContext.getCurrentInstance(),
				kbID, DialogLayout.LAYOUTFILE_STRING)) {
			logger.info("layoutfile available for kb");
			DialogUtils.getDialogLayout().init(kbID);
		} else {
			DialogUtils.getDialogLayout().init(null);
			logger.info("layoutfile NOT available for kb");
		}

		// check if a dialogsettings.xml file is available for the kb
		if (DialogUtils.fileAvailableForKB(FacesContext.getCurrentInstance(),
				kbID, DialogSettings.SETTINGS_FILENAME)) {
			logger.info("settingsfile available for kb");
			DialogUtils.getDialogSettings().init(kbID);
		} else {
			DialogUtils.getDialogSettings().init(null);
			logger.info("settingsfile NOT available for kb");
		}

		// check if imagemap.xml file is available for the kb
		if (DialogUtils.fileAvailableForKB(FacesContext.getCurrentInstance(),
				kbID, ImageMapBean.IMAGEMAP_FILE_STRING)) {
			DialogUtils.getImageMapBean().init();
			logger.info("imagemap file available for kb");
		} else {
			DialogUtils.getImageMapBean().reset();
			logger.info("imagemap file NOT available for kb");
		}

		// check if dialog.css file is available for the kb
		if (DialogUtils.fileAvailableForKB(FacesContext.getCurrentInstance(),
				kbID, DialogSettings.STYLESHEET_FILENAME)) {
			DialogUtils.getDialogSettings().setStyleSheetPath(
					"kbResources/" + kbID + "/multimedia/"
							+ DialogSettings.STYLESHEET_FILENAME);
			DialogUtils.getDialogSettings().setKbHasCSSFile(true);
			logger.info("css file available for kb");
		} else {
			DialogUtils.getDialogSettings().setStyleSheetPath(
					DialogSettings.STYLESHEET_PATH
							+ DialogSettings.STYLESHEET_FILENAME);
			logger.info("css file NOT available for kb");
		}

		DialogUtils.getProcessedQContainersBean().init();
	}

	public String getKbID() {
		return kbID;
	}

	public KnowledgeBase getLoadedKb() {
		return loadedKb;
	}

	public String loadKB() {

		// when no kbid selected -> show error msg.
		if (kbID == null) {
			FacesContext.getCurrentInstance().addMessage(
					KBLoadRenderer.KB_LOAD_ID + "_MSG",
					new FacesMessage(DialogUtils
							.getMessageFor("error.nokbselected")));
			FacesContext.getCurrentInstance().renderResponse();
			return null;
		}
		logger.info("Loading knowledgebase with ID '" + kbID + "'");

		loadedKb = KnowledgeBaseRepository.getInstance().getKnowledgeBase(kbID);
		WebDialog dia = DialogUtils.getDialog();
		// if the clicked kb is not the kb of the actual case -> start a new
		// case and dont parse settings and layout again...
		if (dia.getSession() != null
				&& dia.getSession().getKnowledgeBase().getId().equals(kbID)) {
			logger.info("Selected KB is already loaded... resuming case...");
			return DialogUtils.getPageDisplay().moveToQuestionPage();
		}

		if (!CaseManager.getInstance().hasCasesForKb(kbID)) {
			CaseManager.getInstance().loadCases(kbID);
		}

		checkMultimediaFiles();

		return dia.startNewCase();
	}

	public void setKbID(String kbID) {
		this.kbID = kbID;
	}

	public void setLoadedKb(KnowledgeBase loadedKb) {
		this.loadedKb = loadedKb;
	}

}
