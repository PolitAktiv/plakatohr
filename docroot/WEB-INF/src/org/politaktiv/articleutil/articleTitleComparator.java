package org.politaktiv.articleutil;

import java.util.Comparator;
import java.util.Locale;

import com.liferay.portlet.journal.model.JournalArticle;

/**
 * Comparator that allows to sort a list of journal article by title (alphabetically)
 */
public class articleTitleComparator implements Comparator<JournalArticle> {
	
	private Locale locale;
	private boolean reverse;
	
	/**
	 * Constructs a new comparator. The locale is used to retrieve the titles in the given language.
	 * If a article does not have a title in that language, defaults will be used for that article.
	 * @param locale the locale to use for obtaining each article's title.
	 */
	public articleTitleComparator(Locale locale) {
		this(locale, false);
	}
	
	/**
	 * Constructs a new comparator. The locale is used to retrieve the titles in the given language.
	 * If a article does not have a title in that language, defaults will be used for that article.
	 * @param locale the locale to use for obtaining each article's title.
	 * @param reverse if true, alphabetical order will be reversed.
	 */
	public articleTitleComparator(Locale locale, boolean reverse) {
		this.locale=locale;
		this.reverse = reverse;
	}
	

	@Override
	public int compare(JournalArticle o1, JournalArticle o2) {
		String t1  = o1.getTitle(locale,true);
		String t2  = o2.getTitle(locale,true);
		
		if (reverse) {
			return t2.compareTo(t1);
		} else {
			return t1.compareTo(t2);
		}	
	}

}
