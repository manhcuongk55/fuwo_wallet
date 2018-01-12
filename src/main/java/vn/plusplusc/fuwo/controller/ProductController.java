package vn.plusplusc.fuwo.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.plusplusc.fuwo.dao.OrderDAO;
import vn.plusplusc.fuwo.dao.ProductDAO;
import vn.plusplusc.fuwo.model.CartInfo;
import vn.plusplusc.fuwo.model.CustomerInfo;
import vn.plusplusc.fuwo.model.MyEntry;
import vn.plusplusc.fuwo.model.PaginationResult;
import vn.plusplusc.fuwo.model.Product;
import vn.plusplusc.fuwo.model.ProductInfo;
import vn.plusplusc.fuwo.util.Utils;

@Controller
// Enable Hibernate Transaction.
@Transactional
public class ProductController {

	@Autowired
	private OrderDAO orderDAO;

	@Autowired
	private ProductDAO productDAO;

	@RequestMapping({ "/productList" })
	@ResponseBody
	public Object listProductHandler(@RequestParam(value = "name", defaultValue = "") String likeName,
			@RequestParam(value = "page", defaultValue = "1") int page) {
		final int maxResult = 5;
		final int maxNavigationPage = 10;
		PaginationResult<ProductInfo> result = productDAO.queryProducts(page, //
				maxResult, maxNavigationPage, likeName);

		return result;
	}

	@RequestMapping({ "/buyProduct" })
	@ResponseBody
	public Object listProductHandler(HttpServletRequest request,
			@RequestParam(value = "code", defaultValue = "") String code) {
		ArrayList<MyEntry<String, Object>> arr = new ArrayList<MyEntry<String, Object>>();
		Product product = null;
		if (code != null && code.length() > 0) {
			product = productDAO.findProduct(code);
		}
		if (product != null) {

			// Cart info stored in Session.
			CartInfo cartInfo = Utils.getCartInSession(request);

			ProductInfo productInfo = new ProductInfo(product);

			cartInfo.addProduct(productInfo, 1);
			arr.add(new MyEntry<String, Object>("cartInfo", cartInfo));
			arr.add(new MyEntry<String, Object>("productInfo", productInfo));
			arr.add(new MyEntry<String, Object>("product", product));
		}

		return arr;
	}

	@RequestMapping({ "/shoppingCartRemoveProduct" })
	@ResponseBody
	public Object removeProductHandler(HttpServletRequest request,
			@RequestParam(value = "code", defaultValue = "") String code) {
		ArrayList<MyEntry<String, Object>> arr = new ArrayList<MyEntry<String, Object>>();
		Product product = null;
		if (code != null && code.length() > 0) {
			product = productDAO.findProduct(code);
		}
		if (product != null) {

			// Cart Info stored in Session.
			CartInfo cartInfo = Utils.getCartInSession(request);

			ProductInfo productInfo = new ProductInfo(product);

			cartInfo.removeProduct(productInfo);
			arr.add(new MyEntry<String, Object>("cartInfo", cartInfo));
			arr.add(new MyEntry<String, Object>("productInfo", productInfo));
			arr.add(new MyEntry<String, Object>("product", product));

		}
		// Redirect to shoppingCart page.
		return arr;
	}

	// POST: Update quantity of products in cart.
	@RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.POST)
	@ResponseBody
	public Object shoppingCartUpdateQty(HttpServletRequest request, @RequestBody CartInfo cartForm) {
		CartInfo cartInfo = Utils.getCartInSession(request);
		cartInfo.updateQuantity(cartForm);

		// Redirect to shoppingCart page.
		return cartInfo;
	}

	// GET: Show Cart
	@RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.GET)
	@ResponseBody
	public Object shoppingCartHandler(HttpServletRequest request) {
		CartInfo myCart = Utils.getCartInSession(request);
		return myCart;
	}

	// GET: Enter customer information.
	@RequestMapping(value = { "/shoppingCartCustomer" }, method = RequestMethod.GET)
	@ResponseBody
	public Object shoppingCartCustomerForm(HttpServletRequest request) {

		CartInfo cartInfo = Utils.getCartInSession(request);

		// Cart is empty.
		if (cartInfo.isEmpty()) {

			// Redirect to shoppingCart page.
			return cartInfo;
		}

		CustomerInfo customerInfo = cartInfo.getCustomerInfo();
		if (customerInfo == null) {
			customerInfo = new CustomerInfo();
		}
		return customerInfo;
	}

	// POST: Save customer information.
	@RequestMapping(value = { "/shoppingCartCustomer" }, method = RequestMethod.POST)
	@ResponseBody
	public Object shoppingCartCustomerSave(HttpServletRequest request, CustomerInfo customerForm) {

		CartInfo cartInfo = Utils.getCartInSession(request);

		cartInfo.setCustomerInfo(customerForm);

		// Redirect to Confirmation page.
		return cartInfo;
	}

	// GET: Review Cart to confirm.
	@RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.GET)
	@ResponseBody
	public Object shoppingCartConfirmationReview(HttpServletRequest request) {
		CartInfo cartInfo = Utils.getCartInSession(request);

		// Cart have no products.
		if (cartInfo.isEmpty()) {
			// Redirect to shoppingCart page.
			return cartInfo;
		} else if (!cartInfo.isValidCustomer()) {
			// Enter customer info.
			return cartInfo;
		}

		return cartInfo;
	}

	// POST: Send Cart (Save).
	@RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.POST)
	// Avoid UnexpectedRollbackException (See more explanations)
	@Transactional(propagation = Propagation.NEVER)
	@ResponseBody
	public Object shoppingCartConfirmationSave(HttpServletRequest request) {
		CartInfo cartInfo = Utils.getCartInSession(request);

		// Cart have no products.
		if (cartInfo.isEmpty()) {
			// Redirect to shoppingCart page.
			return "CartInfor is empty";
		} else if (!cartInfo.isValidCustomer()) {
			// Enter customer info.
			return "Customer is not valid";
		}
		try {
			orderDAO.saveOrder(cartInfo);
		} catch (Exception e) {
			// Need: Propagation.NEVER?
			return "shoppingCartConfirmation";
		}
		// Remove Cart In Session.
		Utils.removeCartInSession(request);

		// Store Last ordered cart to Session.
		Utils.storeLastOrderedCartInSession(request, cartInfo);

		// Redirect to successful page.
		return cartInfo;
	}

	@RequestMapping(value = { "/shoppingCartFinalize" }, method = RequestMethod.GET)
	@ResponseBody
	public Object shoppingCartFinalize(HttpServletRequest request) {

		CartInfo lastOrderedCart = Utils.getLastOrderedCartInSession(request);

		if (lastOrderedCart == null) {
			return "Last order Cart is null";
		}

		return lastOrderedCart;
	}

	@RequestMapping(value = { "/productImage" }, method = RequestMethod.GET)
	@ResponseBody
	public void productImage(HttpServletRequest request, HttpServletResponse response,@RequestParam("code") String code) throws IOException {
		Product product = null;
		if (code != null) {
			product = this.productDAO.findProduct(code);
		}
		if (product != null && product.getImage() != null) {
			response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			response.getOutputStream().write(product.getImage());
		}
		response.getOutputStream().close();
	}

}