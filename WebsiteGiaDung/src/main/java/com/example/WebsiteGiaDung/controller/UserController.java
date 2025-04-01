package com.example.WebsiteGiaDung.controller;

import com.example.WebsiteGiaDung.model.User;
import com.example.WebsiteGiaDung.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller //
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "session", required = false) String session,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if ("invalid".equals(session)) {
            model.addAttribute("error", "Phiên của bạn không còn hợp lệ. Vui lòng đăng nhập lại.");
        }
        if (error != null) {
            model.addAttribute("error", "Tên người dùng hoặc mật khẩu không chính xác!");
        }
        if (logout != null) {
            model.addAttribute("message", "Bạn đã đăng xuất thành công.");
        }
        return "Users/login"; // Đảm bảo rằng đường dẫn này trỏ đến tệp login.html
    }


    @GetMapping("/admin")
    public String showAdmin (Model model, @AuthenticationPrincipal OAuth2AuthenticationToken authentication) {
        if (authentication != null) {
//            // Lấy thông tin user từ OAuth2
//            String email = authentication.getPrincipal().getAttribute("email");
//            // Truyền dữ liệu vào model
//            model.addAttribute("email", email);
        }
        return "Admin/dashboard";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "Users/register";
    }


    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "Users/error"; // Quay lại trang đăng ký nếu có lỗi
        }

        // Lấy URL của trang hiện tại
        String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");

        // Gọi phương thức đăng ký của UserService để xử lý
        userService.register(user, siteURL);
        userService.setDefaultRole(user.getUsername());

        // Thêm thông báo thành công vào model
        model.addAttribute("message", "Vui lòng kiểm tra email để xác nhận tài khoản của bạn.");

        // Chuyển hướng tới trang xác nhận đăng ký thành công
        return "Users/register_success";
    }


    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, Model model) {
        String username = userService.getUsernameByVerificationCode(code);
        if (username != null && userService.verify(code)) {
            userService.setDefaultRole(username);
            model.addAttribute("message", "Xác thực email thành công!");
        } else {
            model.addAttribute("message", "Mã xác thực không hợp lệ hoặc đã hết hạn.");
        }
        return "Users/verification_result";
    }

    /*    @GetMapping("/login-success")
    public String loginSuccess() {
        return "Users/loginsuccess"; // Trả về view success.html khi đăng nhập thành công
    }*/
/*    @GetMapping("/login/oauth2/code/google")
    public String loginCallback(@AuthenticationPrincipal OAuth2User oauth2User) {
        // Xử lý người dùng sau khi đăng nhập thành công với Google
        String email = oauth2User.getAttribute("email");

        return "redirect:/index";
    }*/
    @GetMapping("/login/oauth2/code/google")
    public String loginCallback(@AuthenticationPrincipal OAuth2AuthenticationToken authentication,
                                HttpSession session) {
        OAuth2User authenticatedUser = authentication.getPrincipal(); // Đổi tên biến
        String email = authenticatedUser.getAttribute("email");

        // Lấy user từ cơ sở dữ liệu
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername("default_username"); // Hoặc lấy username từ Google nếu có
            userService.save(user);
        }

        // Lưu username vào session
        session.setAttribute("username", user.getUsername());

        return "redirect:/index";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "Admin/user-admin-list";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "Admin/update-user";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        userService.updateUser(id, user);
        return "redirect:/users";
    }

    @PostMapping("/users/update-norm/{id}")
    public String updateUserNorm(@PathVariable Long id, @ModelAttribute User user) {
        userService.updateUser(id, user);
        return "Users/user-detail";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id); // Add logic to delete the user by ID.
        return "redirect:/users"; // Redirect back to the user list.
    }

}
