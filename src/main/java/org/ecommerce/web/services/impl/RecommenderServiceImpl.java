package org.ecommerce.web.services.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.ecommerce.config.qualifiers.AnonymousViewHistoryRecommender;
import org.ecommerce.config.qualifiers.CollaborativeFilteringRecommender;
import java.util.List;
import java.util.Set;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.impl.model.BooleanUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.PlusAnonymousUserDataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ecommerce.persistence.models.ProductLine;
import org.ecommerce.persistence.models.User;
import org.ecommerce.persistence.models.TasteBoolPreference;
import org.ecommerce.persistence.models.TastePreferences;
import org.ecommerce.persistence.repositories.ProductRepository;
import org.ecommerce.persistence.repositories.TasteBoolPreferencesRepository;
import org.ecommerce.persistence.repositories.UserRepository;
import org.ecommerce.web.admin.exceptions.UserNotFoundException;
import org.ecommerce.web.frontend.exceptions.RecommendationException;
import org.ecommerce.web.models.anonymous.AnonymousSession;
import org.ecommerce.web.recommendation.rescorers.ConsumerTypeRescorer;
import org.ecommerce.web.services.RecommenderService;
import org.ecommerce.persistence.repositories.ProductLineRepository;
import org.ecommerce.persistence.repositories.TastePreferencesRepository;

/**
 * @author sergio
 */
@Service("recommenderService")
public class RecommenderServiceImpl implements RecommenderService {

	@Autowired
	private AnonymousSession anonymousSession;

	@Autowired
	@AnonymousViewHistoryRecommender
	private Recommender anonymousViewHistoryRecommender;

	@Autowired
	@CollaborativeFilteringRecommender
	private Recommender collaborativeFilteringRecommender;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductLineRepository productLineRepository;

	@Autowired
	private TasteBoolPreferencesRepository tasteBoolPreferencesRepository;

	@Autowired
	private TastePreferencesRepository tastePreferencesRepository;

	@Override
	public void addProductViewedToAnonymousUserHistory(Long productId) {
		anonymousSession.trackViewedProduct(productId);
	}

	@Override
	public void addProductViewedToUserHistory(Long userId, Long productId) {
		User user = userRepository.findOne(userId);
		ProductLine productLine = productLineRepository.findOne(productId);
		TasteBoolPreference preference = new TasteBoolPreference(user, productLine);
		tasteBoolPreferencesRepository.save(preference);
	}

	@Override
	public List<Long> recommendForAnonymousUser(int howMany) {
		List<Long> productLinesRecommended = null;
		Set<Long> trackedProducts = anonymousSession.getTrackedProducts();
		Long[] productsId = trackedProducts.<Long>toArray(new Long[trackedProducts.size()]);
		BooleanUserPreferenceArray anonymousPrefs = new BooleanUserPreferenceArray(productsId.length);
		anonymousPrefs.setUserID(0, PlusAnonymousUserDataModel.TEMP_USER_ID);
		for (int i = 0; i < productsId.length; i++) {
			anonymousPrefs.setItemID(i, productsId[i]);
		}
		PlusAnonymousUserDataModel plusAnonymousUserDataModel = (PlusAnonymousUserDataModel) anonymousViewHistoryRecommender
				.getDataModel();
		plusAnonymousUserDataModel.setTempPrefs(anonymousPrefs);
		try {
			List<RecommendedItem> recommendations = anonymousViewHistoryRecommender
					.recommend(PlusAnonymousUserDataModel.TEMP_USER_ID, howMany);
			productLinesRecommended = Lists.transform(recommendations, new Function<RecommendedItem, Long>() {
				@Override
				public Long apply(RecommendedItem item) {
					return item.getItemID();
				}
			});
		} catch (TasteException ex) {
			throw new RecommendationException();
		} finally {
			plusAnonymousUserDataModel.clearTempPrefs();
		}
		return productLinesRecommended;
	}

	@Override
	public List<Long> recommendForUser(Long userId, int howMany) {
		List<Long> productLinesRecommended = null;
		User user = userRepository.findOne(userId);
		if (user == null) {
			throw new UserNotFoundException();
		}
		IDRescorer rescorer = new ConsumerTypeRescorer(productRepository, user.getGender(), user.getAge());
		try {
			List<RecommendedItem> recommendations = collaborativeFilteringRecommender.recommend(userId, howMany,
					rescorer);
			productLinesRecommended = Lists.transform(recommendations, new Function<RecommendedItem, Long>() {
				@Override
				public Long apply(RecommendedItem item) {
					return item.getItemID();
				}
			});
		} catch (TasteException ex) {
			throw new RecommendationException();
		}
		return productLinesRecommended;
	}

	@Override
	public void addPreference(User user, ProductLine productLine, Float preference) {
		TastePreferences tastePreferences = new TastePreferences(user, productLine, preference);
		tastePreferencesRepository.save(tastePreferences);
	}

	@Override
	public void removePreference(Long userId, Long productLineId) {
		tastePreferencesRepository.delete(new TastePreferences.TastePreferencesId(userId, productLineId));
	}
}
