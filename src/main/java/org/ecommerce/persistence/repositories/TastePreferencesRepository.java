package org.ecommerce.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ecommerce.persistence.models.TastePreferences;

/**
 * @author sergio
 */
public interface TastePreferencesRepository
		extends JpaRepository<TastePreferences, TastePreferences.TastePreferencesId> {
}
