package org.politaktiv.articleutil;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

/**
 * Helper class for retrieving articles from the document library (Journal) of LifeRay
 */
public class articleJournalHelper {
	
	/**
	 * Retrieves all articles from a given scope/group/site. This will obtain only the latest
	 * versions of the articles. Furthermore, articles in the trash will be omitted.
	 * @param groupID specifies the scope/group/site
	 * @return a list of articles
	 * @throws SystemException
	 * @throws PortalException
	 */
	public static List<JournalArticle> getLatestArticles(long groupID) throws SystemException, PortalException {
		return getLatestArticles(groupID, null);
	}
	
	/**
	 * Retrieves all articles from the entire portal. <strong>Use with caution, long list!</strong> This will obtain only the latest
	 * versions of the articles. Furthermore, articles in the trash will be omitted.
	 * @return a list of articles
	 * @throws SystemException
	 * @throws PortalException
	 */
	public static List<JournalArticle> getLatestArticles() throws SystemException, PortalException {
		return getLatestArticles(-1, null);
	}
	
	/**
	 * Retrieves all articles from the entire portal, sorted by a comparator <strong>Use with caution, long list!</strong> This will obtain only the latest
	 * versions of the articles. Furthermore, articles in the trash will be omitted.
	 * @param comparator the comparator for sorting the articles
	 * @return a list of articles
	 * @throws SystemException
	 * @throws PortalException
	 */
	public static List<JournalArticle> getLatestArticles(Comparator<JournalArticle> comparator) throws SystemException, PortalException {
		return getLatestArticles(-1, comparator);
	}
	
	/**
	 * Retrieves all articles from a given scope/group/site, sorted by a comparator. This will obtain only the latest
	 * versions of the articles. Furthermore, articles in the trash will be omitted.
	 * @param groupID specifies the scope/group/site
	 * @param comparator the comparator for sorting the articles
	 * @return a list of articles
	 * @throws SystemException
	 * @throws PortalException
	 */
	public static List<JournalArticle> getLatestArticles(long groupID, Comparator<JournalArticle> comparator) throws SystemException, PortalException {
		
		List<JournalArticle> articleListComplete;
		if ( groupID >= 0) {
			// obtain all articles in this scope / groupID (will also return old versions of articles)
			articleListComplete = JournalArticleLocalServiceUtil.getArticles(groupID);
		} else {
			// obtain all articles in this LifeRay universe (ouch!)
			articleListComplete = JournalArticleLocalServiceUtil.getArticles();
		}
			
        
        LinkedList<JournalArticle> articleOnlyLatest = new LinkedList<JournalArticle>();
        
        // collect latest versions from all articles and also get rid of those in the trash
        for (JournalArticle art : articleListComplete) {
        	if( JournalArticleLocalServiceUtil.isLatestVersion(groupID,art.getArticleId(),art.getVersion()) &&
        			! art.isInTrash()  ) {
        		articleOnlyLatest.add(art);
        		
        	}
        }
        
        // sort
        if (comparator != null) {
        	Collections.sort(articleOnlyLatest, comparator);
        }
        
        return articleOnlyLatest;
		
		
	}
	
	

}
