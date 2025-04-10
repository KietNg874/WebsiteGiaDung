    package com.example.WebsiteGiaDung.controller;

    import com.example.WebsiteGiaDung.OrderStatus;
    import com.example.WebsiteGiaDung.model.*;
    import com.example.WebsiteGiaDung.payment.Config;
    import com.example.WebsiteGiaDung.repository.CartRepository;
    import com.example.WebsiteGiaDung.repository.OrderDetailRepository;
    import com.example.WebsiteGiaDung.repository.OrderRepository;
    import com.example.WebsiteGiaDung.service.CartService;
    import com.example.WebsiteGiaDung.service.OrderDetailService;
    import com.example.WebsiteGiaDung.service.OrderService;
    import com.example.WebsiteGiaDung.service.ProductService;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.stereotype.Controller;
    import org.springframework.stereotype.Service;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;

    import java.io.IOException;
    import java.net.URLEncoder;
    import java.nio.charset.StandardCharsets;
    import java.text.SimpleDateFormat;
    import java.util.*;


    @Controller
    @RequestMapping("/payments")
    public class    PaymentController {
        @Autowired
        private ProductService productService;
        @Autowired
        private OrderService orderService;
        @Autowired
        private OrderDetailService orderDetailService;
        @Autowired
        private CartService cartService;
        @Autowired
        private OrderRepository orderRepository;
        @Autowired
        private OrderDetailRepository orderDetailRepository;
        @Autowired
        private CartRepository cartRepository;
        @PostMapping("/vnpay")
        public String createPayment(HttpServletRequest req, @RequestParam("amount") long amount,@RequestParam("fullAddress") String address,@RequestParam("customerName") String customerName) throws IOException {
            req.getSession().setAttribute("customerName", customerName);
            req.getSession().setAttribute("fullAddress", address);
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String orderType = "other";
            Map<String, String> params = new HashMap<>();
            /*  long amount = Integer.parseInt("1000000");*/
            String bankCode = params.get(" ");
            amount = amount * 100L; // Chuyển thành đơn vị VNPay (VND * 100)

            String vnp_TxnRef = Config.getRandomNumber(8);
            String vnp_IpAddr = Config.getIpAddress(req);

            String vnp_TmnCode = Config.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");

            if (bankCode != null && !bankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType", orderType);

            String locate = params.get("language");
            if (locate != null && !locate.isEmpty()) {
                vnp_Params.put("vnp_Locale", locate);
            } else {
                vnp_Params.put("vnp_Locale", "vn");
            }
            vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    // Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

            return "redirect:" + paymentUrl;
        }
        @GetMapping("/return")
        public String paymentReturn(HttpServletRequest request,
                                    Authentication authentication,
                                    Model model) {
            HttpSession session = request.getSession();
            String customerName = (String) session.getAttribute("customerName");
            String address = (String) session.getAttribute("fullAddress");

            if (customerName == null || address == null) {
                return "redirect:/checkout"; // Quay lại nếu thiếu thông tin.
            }

            // Lấy thông tin từ tham số URL.
            Map<String, String> params = new HashMap<>();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                params.put(paramName, request.getParameter(paramName));
            }

            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            if ("00".equals(vnp_ResponseCode) && authentication != null) {
                User user = (User) authentication.getPrincipal();
                Optional<Cart> optionalCart = cartRepository.findByUser(user);
                if (optionalCart.isPresent()) {
                    Cart cart = optionalCart.get();
                    // Tạo đơn hàng
                    Order order = new Order();
                    order.setCustomerName(customerName);
                    order.setDiaChi(address);
                    order.setUser(user);
                    order.setPhone(user.getPhone());
                    order.setAmount(Long.parseLong(params.get("vnp_Amount")) / 100);
                    orderRepository.save(order);

                    // Lưu chi tiết đơn hàng
                    for (CartItem item : cart.getItems()) {
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setOrder(order);
                        orderDetail.setProduct(item.getProduct());
                        orderDetail.setQuantity(item.getQuantity());
                        orderDetailRepository.save(orderDetail);
                    }

                    cart.getItems().clear();
                    cartRepository.save(cart);

                    return "payments/order-confirmation";
                }
            }
            return "payments/payment-error";
        }


    }


