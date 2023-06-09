package org.ecommerce.persistence.listeners;

import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ecommerce.persistence.models.ProductLine;
import org.ecommerce.persistence.models.Review;
import org.ecommerce.persistence.models.ReviewStatusEnum;
import org.ecommerce.web.services.RecommenderService;
import org.ecommerce.web.utils.SpringApplicationContext;

/**
 *
 * @author sergio
 */
public class ReviewEntityListener {

	private static Logger logger = LoggerFactory.getLogger(ReviewEntityListener.class);

	@PreUpdate
	public void preUpdate(Review reviewEntity) {

		RecommenderService recommenderService = SpringApplicationContext.getBean(RecommenderService.class);

		logger.info("PreUpdate Review New Status -> " + reviewEntity.getStatus().name());
		logger.info("PreUpdate Review Old Status -> " + reviewEntity.getPrevStatus().name());
		ProductLine productLine = reviewEntity.getProduct().getProductLines().get(0);
		if (reviewEntity.getPrevStatus().equals(ReviewStatusEnum.APPROVED)) {
			logger.info("Remove Preference ...");
			recommenderService.removePreference(reviewEntity.getUser().getId(), productLine.getId());
		} else if ((reviewEntity.getPrevStatus().equals(ReviewStatusEnum.PENDING)
				|| reviewEntity.getPrevStatus().equals(ReviewStatusEnum.REJECTED))
				&& reviewEntity.getStatus().equals(ReviewStatusEnum.APPROVED)) {
			logger.info("Add Preference ...");
			recommenderService.addPreference(reviewEntity.getUser(), productLine, reviewEntity.getRating());
		}
	}

	@PostLoad
	private void saveCurrentStatus(Review reviewEntity) {
		reviewEntity.setPrevStatus(reviewEntity.getStatus());
	}
}
