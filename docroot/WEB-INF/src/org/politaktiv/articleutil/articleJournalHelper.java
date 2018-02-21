package org.politaktiv.articleutil;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

public class articleJournalHelper {
	
	public static List<JournalArticle> getLatestArticles(long groupID) throws SystemException, PortalException {
		
		return getLatestArticles(groupID, null);
		
		
	}

	public static List<JournalArticle> getLatestArticles(long groupID, Comparator<JournalArticle> comparator) throws SystemException, PortalException {
		
		// obtain all articles in this scope / groupID (will also return old versions of articles)
        List<JournalArticle> articleListComplete = JournalArticleLocalServiceUtil.getArticles(groupID);
        
        LinkedList<JournalArticle> articleOnlyLatest = new LinkedList<JournalArticle>();
        
        // collect latest versions from all articles
        for (JournalArticle art : articleListComplete) {
        	if(JournalArticleLocalServiceUtil.isLatestVersion(groupID,art.getArticleId(),art.getVersion())) {
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
