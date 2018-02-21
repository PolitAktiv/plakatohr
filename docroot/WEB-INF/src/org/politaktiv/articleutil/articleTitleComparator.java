package org.politaktiv.articleutil;

import java.util.Comparator;
import java.util.Locale;

import com.liferay.portlet.journal.model.JournalArticle;

public class articleTitleComparator implements Comparator<JournalArticle> {
	
	private Locale locale;
	
	public articleTitleComparator(Locale locale) {
		this.locale=locale;
	}
	

	@Override
	public int compare(JournalArticle o1, JournalArticle o2) {
		String t1  = o1.getTitle(locale,true);
		String t2  = o2.getTitle(locale,true);
		
		return t1.compareTo(t2);
	}

}
