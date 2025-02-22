package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.MinioService;
import com.example.cloneInstragram.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller()
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private MinioService minioService;

    @GetMapping
    public String showProfile(Model model, @AuthenticationPrincipal User user) {
        UserDTO userDTO = userService.getUserDTO(user);
        model.addAttribute("user", userDTO);
        return "profile";
    }
    @GetMapping("/edit")
    public String showEditProfile(Model model, @AuthenticationPrincipal User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setBio(user.getBio());
        userDTO.setProfilePicture(user.getProfilePicture()); // присваиваем значения из User

        model.addAttribute("user", userDTO); // передаем DTO в шаблон
        return "userEdit"; // имя шаблона
    }

    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam String username,
                                @RequestParam(required = true) String password,
                                @RequestParam("profile_picture") MultipartFile profilePicture,
                                @RequestParam String bio,
                                RedirectAttributes redirectAttributes) {
        try {
            String profilePicturePath = null;
            if (!profilePicture.isEmpty()) {
                profilePicturePath = minioService.uploadFile(profilePicture);
            }
            userService.updateUser(user, username, bio, profilePicturePath, password);
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Ошибка загрузки файла!");
            return "redirect:/profile/edit";
        }
    }
}
