package org.ecommerce.persistence.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ecommerce.persistence.models.ProductLine;
import org.ecommerce.persistence.projection.ProductLineView;

/**
 * @author sergio
 */
public interface ProductLineRepository extends JpaRepository<ProductLine, Long> {
	List<ProductLineView> findByProductIdAndIdNotAndStockGreaterThan(Long productId, Long id, Integer stock);

	List<ProductLine> findByProductId(Long id);

	List<ProductLine> findByProductName(String name);
}
