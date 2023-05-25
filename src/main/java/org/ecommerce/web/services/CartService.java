package org.ecommerce.web.services;

import java.util.Set;
import org.ecommerce.web.models.shop.Cart;
import org.ecommerce.web.models.shop.CartItem;

/**
 * @author sergio
 */
public interface CartService {
	Cart getCart();

	Set<CartItem> getAllItem();

	Double getSubtotal();

	void addItem(CartItem item);

	void updateItem(CartItem item);

	void updateItems(Set<CartItem> item);

	void removeAllItems();

	CartItem getCartItemByProductLine(Long id);

	boolean isEmpty();
}
