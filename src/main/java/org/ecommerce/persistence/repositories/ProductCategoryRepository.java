package org.ecommerce.persistence.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ecommerce.persistence.models.ProductCategory;

/**
 * @author sergio
 */
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
	ProductCategory findBySlug(String slug);

	List<ProductCategory> findByParentIsNull();
}
