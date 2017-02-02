package org.msh.pharmadex.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.msh.pharmadex.dao.ReviewQDAO;
import org.msh.pharmadex.dao.iface.ReviewDAO;
import org.msh.pharmadex.domain.ReviewQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to edit review questions
 * @author Alex Kurasoff
 *
 */
@Service
public class EditReviewService {
	@Autowired
	private ReviewQDAO reviewQDAO;
		
	public ReviewQDAO getReviewQDAO() {
		return reviewQDAO;
	}

	public void setReviewQDAO(ReviewQDAO reviewQDAO) {
		this.reviewQDAO = reviewQDAO;
	}

	/**
	 * load all questions
	 * @return 
	 */
	public List<ReviewQuestion> findAll() {
		List<ReviewQuestion> ret = getReviewQDAO().findAll();
		Collections.sort(ret, new Comparator<ReviewQuestion>() {
			@Override
			public int compare(ReviewQuestion o1, ReviewQuestion o2) {
				return o1.getOrd().compareTo(o2.getOrd());
			}
		});
		return ret; 
	}
	/**
	 * Save a given item to the database
	 * @param item
	 */
	public void updateList(ReviewQuestion item) {
		getReviewQDAO().save(item);
		
	}
	
}
