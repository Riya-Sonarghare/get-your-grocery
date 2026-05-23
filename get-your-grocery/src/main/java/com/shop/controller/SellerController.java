package com.shop.controller;

import com.shop.dto.ProductDto;
import com.shop.dto.UserDto;
import com.shop.entity.Order;
import com.shop.entity.Product;
import com.shop.entity.User;
import com.shop.service.OrderService;
import com.shop.service.ProductService;
import com.shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    // ── Session helpers ───────────────────────────────────────────────

    private boolean isSellerLoggedIn(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String role  = (String) session.getAttribute("role");
        return userId != null && "SELLER".equals(role);
    }

    private Long sellerId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    /** Adds navbar-related attributes to every model */
    private void addNavAttributes(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role",     session.getAttribute("role"));
    }

    // ── Dashboard – list seller's products ───────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isSellerLoggedIn(session)) return "redirect:/login";

        List<Product> products = productService.getProductsBySeller(sellerId(session));
        model.addAttribute("products", products);
        addNavAttributes(session, model);
        return "seller/dashboard";
    }

    // ── Add Product ───────────────────────────────────────────────────

    @GetMapping("/products/add")
    public String addProductForm(HttpSession session, Model model) {
        if (!isSellerLoggedIn(session)) return "redirect:/login";

        model.addAttribute("productDto", new ProductDto());
        addNavAttributes(session, model);
        return "seller/add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute ProductDto productDto,
                             HttpSession session, Model model) {
        if (!isSellerLoggedIn(session)) return "redirect:/login";

        try {
            productService.addProduct(productDto, sellerId(session));
            return "redirect:/seller/dashboard?success=Product+added+successfully";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("productDto", productDto);
            addNavAttributes(session, model);
            return "seller/add-product";
        }
    }

    // ── Edit Product ──────────────────────────────────────────────────

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id,
                                  HttpSession session, Model model) {
        if (!isSellerLoggedIn(session)) return "redirect:/login";

        Product product = productService.findById(id);

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategory(product.getCategory());
        dto.setAvailable(product.isAvailable());

        model.addAttribute("productDto", dto);
        addNavAttributes(session, model);
        return "seller/edit-product";
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id,
                              @ModelAttribute ProductDto productDto,
                              HttpSession session, Model model) {
        if (!isSellerLoggedIn(session)) return "redirect:/login";

        try {
            productService.updateProduct(id, productDto, sellerId(session));
            return "redirect:/seller/dashboard?success=Product+updated+successfully";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("productDto", productDto);
            addNavAttributes(session, model);
            return "seller/edit-product";
        }
    }

    // ── Delete Product ────────────────────────────────────────────────

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        if (!isSellerLoggedIn(session)) return "redirect:/login";

        productService.deleteProduct(id, sellerId(session));
        return "redirect:/seller/dashboard?success=Product+deleted";
    }

    // ── Orders Received ───────────────────────────────────────────────

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        if (!isSellerLoggedIn(session)) return "redirect:/login";

        List<Order> orders = orderService.getOrdersBySeller(sellerId(session));
        model.addAttribute("orders", orders);
        addNavAttributes(session, model);
        return "seller/orders";
    }

    @PostMapping("/orders/{id}/deliver")
    public String deliverOrder(@PathVariable Long id, HttpSession session) {
        if (!isSellerLoggedIn(session)) return "redirect:/login";

        orderService.deliverOrder(id, sellerId(session));
        return "redirect:/seller/orders?success=Order+marked+as+delivered";
    }

    // ── Profile & Earnings ────────────────────────────────────────────

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        if (!isSellerLoggedIn(session)) return "redirect:/login";

        User seller        = userService.findById(sellerId(session));
        double earnings    = orderService.getTodayEarnings(sellerId(session));

        model.addAttribute("seller", seller);
        model.addAttribute("earningsToday", earnings);
        addNavAttributes(session, model);
        return "seller/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute UserDto userDto,
                                HttpSession session) {
        if (!isSellerLoggedIn(session)) return "redirect:/login";

        try {
            userService.updateProfile(sellerId(session), userDto);
            return "redirect:/seller/profile?success=Profile+updated+successfully";
        } catch (Exception e) {
            return "redirect:/seller/profile?error=" + e.getMessage();
        }
    }
}
