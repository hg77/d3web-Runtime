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

package de.d3web.core.knowledge.terminology.info.abnormality;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;

/**
 * Represents the abnormality of a symptom Creation date: (06.08.2001 15:51:58)
 * 
 * @author: Norman Brümmer
 */
public class DefaultAbnormality implements Abnormality {

	private final Map<Value, Double> values = new HashMap<Value, Double>();

	/**
	 * with this method you can add an answer-abnorm.Value pair Creation date:
	 * (06.08.2001 16:25:46)
	 * 
	 * @param ans de.d3web.kernel.domainModel.Answer
	 * @param value double
	 */
	public void addValue(Value ans, double value) {
		// TODO other Values than ChoiceValues are not supported by the
		// persistence (no reference to the question available), throw an
		// exception?
		if (ans instanceof MultipleChoiceValue) {
			MultipleChoiceValue mcv = (MultipleChoiceValue) ans;
			for (ChoiceID cid : mcv.getChoiceIDs()) {
				values.put(new ChoiceValue(cid), new Double(value));
			}
		}
		else {
			values.put(ans, new Double(value));
		}
	}

	/**
	 * Returns the abnormality to the given answer Creation date: (06.08.2001
	 * 16:28:14)
	 * 
	 * @return double
	 * @param ans de.d3web.kernel.domainModel.Answer
	 */
	@Override
	public double getValue(Value ans) {
		Double ret = values.get(ans);
		if (ret != null) {
			return ret.doubleValue();
		}

		if (ans instanceof MultipleChoiceValue) {
			double max = -1f;
			Collection<ChoiceID> choiceIDs = ((MultipleChoiceValue) ans).getChoiceIDs();
			for (ChoiceID choiceID : choiceIDs) {
				ret = values.get(new ChoiceValue(choiceID));
				if (ret == null) return A5;
				double abnorm = ret;
				if (abnorm >= A5) return A5;
				max = Math.max(max, abnorm);
			}
			if (max >= A0) return max;
		}

		return A5;
	}

	public boolean isSet(Value ans) {
		return (values.get(ans) != null);
	}

	public Set<Value> getAnswerSet() {
		return values.keySet();
	}

	/**
	 * Sets the Abnormality of the Question for the given the Value
	 * 
	 * @created 25.06.2010
	 * @param q Question
	 * @param value Value
	 * @param abnormality Abnormality
	 */
	public static void setAbnormality(Question q, Value value, double abnormality) {
		InfoStore infoStore = q.getInfoStore();
		DefaultAbnormality abnormalitySlice = infoStore.getValue(BasicProperties.DEFAULT_ABNORMALITIY);
		if (abnormalitySlice == null) {
			abnormalitySlice = new DefaultAbnormality();
			infoStore.addValue(BasicProperties.DEFAULT_ABNORMALITIY, abnormalitySlice);
		}
		abnormalitySlice.addValue(value, abnormality);
	}

	public static DefaultAbnormality valueOf(String s) {
		DefaultAbnormality defaultAbnormality = new DefaultAbnormality();
		String[] abnormalities = s.split(";");
		for (String abnormalityString : abnormalities) {
			if (abnormalityString.trim().isEmpty()) continue;
			int lastColon = abnormalityString.lastIndexOf(":");
			double abnormality = Double.parseDouble(abnormalityString.substring(lastColon + 1).replace(
					",", ".").trim());
			defaultAbnormality.addValue(
					new ChoiceValue(new ChoiceID(abnormalityString.substring(0, lastColon).trim())),
					abnormality);
		}
		return defaultAbnormality;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Value, Double> entry : values.entrySet()) {
			sb.append(entry.getKey().toString());
			sb.append(":");
			sb.append(entry.getValue());
			sb.append(";");
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		}
		else {
			return "";
		}
	}

}