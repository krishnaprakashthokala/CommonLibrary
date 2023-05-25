package org.ecommerce.web.recommendation.rescorers;

import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.ecommerce.persistence.models.ConsumerTypeEnum;
import org.ecommerce.persistence.models.Gender;
import org.ecommerce.persistence.models.Product;
import org.ecommerce.persistence.models.ProductStatusEnum;
import org.ecommerce.persistence.repositories.ProductRepository;

/**
 * @author sergio
 */
public class ConsumerTypeRescorer implements IDRescorer {

	private ProductRepository productRepository;
	private Gender gender;
	private Integer age;

	public ConsumerTypeRescorer(ProductRepository productRepository, Gender gender, Integer age) {
		this.productRepository = productRepository;
		this.gender = gender;
		this.age = age;
	}

	@Override
	public double rescore(long itemId, double originalScore) {
		Product product = productRepository.findOne(itemId);
		if (product.getConsumerType().equals(ConsumerTypeEnum.UNISEX)
				|| product.getConsumerType().equals(ConsumerTypeEnum.MAN) && gender.equals(Gender.MAN)
				|| product.getConsumerType().equals(ConsumerTypeEnum.WOMAN) && gender.equals(Gender.WOMAN)
				|| product.getConsumerType().equals(ConsumerTypeEnum.LITTLE_BOY_OR_GIRL) && age < 14) {
			originalScore *= 1.2;
		}
		return originalScore;
	}

	@Override
	public boolean isFiltered(long itemId) {
		Product product = productRepository.findOne(itemId);
		return product.getStatus().equals(ProductStatusEnum.PUBLISHED);
	}

}
