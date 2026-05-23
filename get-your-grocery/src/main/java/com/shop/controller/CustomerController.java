package com.shop.controller;

import com.shop.dto.CheckoutDto;
import com.shop.dto.UserDto;
import com.shop.entity.Order;
import com.shop.entity.Product;
import com.shop.entity.SavedItem;
import com.shop.entity.User;
import com.shop.service.OrderService;
import com.shop.service.ProductService;
import com.shop.service.SavedItemService;
import com.shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final ProductService   productService;
    private final OrderService     orderService;
    private final SavedItemService savedItemService;
    private final UserService      userService;

    // ── Session helpers ───────────────────────────────────────────────

    private boolean isCustomerLoggedIn(HttpSession session) {
        Long userId  = (Long)   session.getAttribute("userId");
        String role  = (String) session.getAttribute("role");
        return userId != null && "CUSTOMER".equals(role);
    }

    private Long customerId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    private void addNavAttributes(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role",     session.getAttribute("role"));
    }

    // ── Dashboard – browse all available products ─────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isCustomerLoggedIn(session)) return "redirect:/login";

        List<Product> products = productService.getAllAvailableProducts();
        model.addAttribute("products", products);
        addNavAttributes(session, model);
        return "customer/dashboard";
    }

    // ── Checkout ──────────────────────────────────────────────────────

    @GetMapping("/checkout/{productId}")
    public String checkoutForm(@PathVariable Long productId,
                               HttpSession session, Model model) {
        if (!isCustomerLoggedIn(session)) return "redirect:/login";

        User    customer = userService.findById(customerId(session));
        Product product  = productService.findById(productId);

        // Pre-fill checkout form with customer profile data
        CheckoutDto checkoutDto = new CheckoutDto();
        checkoutDto.setProductId(productId);
        checkoutDto.setCustomerUsername(customer.getUsername());
        checkoutDto.setDeliveryAddress(customer.getAddress());
        checkoutDto.setContactNumber(customer.getContactNumber());
        checkoutDto.setQuantity(1);

        model.addAttribute("checkoutDto", checkoutDto);
        model.addAttribute("product", product);
        addNavAttributes(session, model);
        return "customer/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@ModelAttribute CheckoutDto checkoutDto,
                             HttpSession session, Model model) {
        if (!isCustomerLoggedIn(session)) return "redirect:/login";

        try {
            orderService.placeOrder(checkoutDto, customerId(session));
            return "redirect:/customer/orders?success=Order+placed+successfully";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("product", productService.findById(checkoutDto.getProductId()));
            model.addAttribute("checkoutDto", checkoutDto);
            addNavAttributes(session, model);
            return "customer/checkout";
        }
    }

    // ── My Orders ─────────────────────────────────────────────────────

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        if (!isCustomerLoggedIn(session)) return "redirect:/login";

        List<Order> orders = orderService.getOrdersByCustomer(customerId(session));
        model.addAttribute("orders", orders);
        addNavAttributes(session, model);
        return "customer/orders";
    }

    // ── Saved Items ───────────────────────────────────────────────────

    @GetMapping("/saved")
    public String savedItems(HttpSession session, Model model) {
        if (!isCustomerLoggedIn(session)) return "redirect:/login";

        List<SavedItem> savedItems = savedItemService.getSavedItems(customerId(session));
        model.addAttribute("savedItems", savedItems);
        addNavAttributes(session, model);
        return "customer/saved";
    }

    @PostMapping("/saved/add/{productId}")
    public String saveItem(@PathVariable Long productId, HttpSession session) {
        if (!isCustomerLoggedIn(session)) return "redirect:/login";

        savedItemService.saveItem(customerId(session), productId);
        return "redirect:/customer/dashboard?success=Item+saved";
    }

    @PostMapping("/saved/remove/{savedItemId}")
    public String removeSavedItem(@PathVariable Long savedItemId, HttpSession session) {
        if (!isCustomerLoggedIn(session)) return "redirect:/login";

        savedItemService.removeSavedItem(savedItemId, customerId(session));
        return "redirect:/customer/saved";
    }

    // ── Profile ───────────────────────────────────────────────────────

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        if (!isCustomerLoggedIn(session)) return "redirect:/login";

        User customer = userService.findById(customerId(session));
        model.addAttribute("customer", customer);
        addNavAttributes(session, model);
        return "customer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute UserDto userDto, HttpSession session) {
        if (!isCustomerLoggedIn(session)) return "redirect:/login";

        try {
            userService.updateProfile(customerId(session), userDto);
            return "redirect:/customer/profile?success=Profile+updated+successfully";
        } catch (Exception e) {
            return "redirect:/customer/profile?error=" + e.getMessage();
        }
    }
}
