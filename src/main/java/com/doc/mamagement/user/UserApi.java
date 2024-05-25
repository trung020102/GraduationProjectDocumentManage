package com.doc.mamagement.user;

import com.doc.mamagement.entity.user.Role;
import com.doc.mamagement.security.RoleAuthorization;
import com.doc.mamagement.security.UserPrincipal;
import com.doc.mamagement.user.dto.*;
import com.doc.mamagement.utility.datatable.DatatableResult;
import com.doc.mamagement.utility.file_handling.StorageService;
import com.doc.mamagement.utility.validation.user.RuleAnnotation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.rananu.spring.mvc.annotation.Result;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Validated /* Put this annotation here to revoke custom message in @ValidationRuleAnnotation.CheckAvatar */
public class UserApi {
    private final UserService userService;
    private final StorageService storageService;

    @RoleAuthorization.AdminAuthorization
    @PostMapping("/register")
    /* Create new user */
    @Result(message = "message.success.user.create")
    public void registerNewUser(@Valid @RequestBody UserRegister registerData) {
        userService.createNewUser(registerData);
    }

    @RoleAuthorization.AdminAuthorization
    @PostMapping
    /* Get all users for admin page */
    /* Do not use @Result in order to return correct response structure for datatable */
    public ResponseEntity<?> findAllUsersByFilter(@RequestBody UserFilter userFilter) {
        DatatableResult<UserInfoResult> datatableResult = userService.findAllByFilter(userFilter);
        return ResponseEntity.ok(datatableResult);
    }

    @RoleAuthorization.AdminAuthorization
    @PutMapping("/admin/update-user")
    /* Update user info by admin */
    @Result(message = "message.success.user.updateInfo")
    public void updateUserByAdmin(@Valid @RequestBody UserInfoParamByAdmin userInFoParamByAdmin) {
        userService.updateUserByAdmin(userInFoParamByAdmin);
    }

    @RoleAuthorization.AdminAuthorization
    @PutMapping("/{id}/change-status")
    /* Update current user status */
    @Result(message = "message.success.user.updateStatus")
    public void changeUserStatus(@PathVariable Long id) {
        userService.updateUserStatusById(id);
    }

    @RoleAuthorization.AdminAuthorization
    @GetMapping("/getAllRoles")
    /* Get all user roles */
    @Result
    public List<Role> getAllRoles() {
        return userService.getAllRoles();
    }

    @RoleAuthorization.AdminAuthorization
    @GetMapping("/getFileSample")
    /* Get user import sample file */
    public ResponseEntity<?> getUserImportFileSample() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + UserService.IMPORT_FILE_SAMPLE_NAME
        );

        return new ResponseEntity<>(
                storageService.loadAsResource(UserService.IMPORT_FILE_SAMPLE_NAME, null),
                header,
                HttpStatus.OK
        );
    }

    @RoleAuthorization.AuthenticatedUser
    @GetMapping("/{id}")
    @Result
    /* Get user detail for user editing feature */
    public UserInfoResult findByUser(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.findByUser(principal.getId());
    }

    @RoleAuthorization.AuthenticatedUser
    @PutMapping("/change-password")
    /* Update current user password */
    @Result(message = "message.success.user.updatePassword")
    public void changePassword(
            @Valid @RequestBody UserPasswordParam userPasswordParam,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.updatePasswordById(principal.getId(), userPasswordParam.getNewPassword());
    }

    @RoleAuthorization.AuthenticatedUser
    @PutMapping
    /* Update current user information */
    @Result(message = "message.success.user.updateInfo")
    public void updateUserInfo(
            @Valid @RequestBody UserInfoParam userInfoParam,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.updateUserInfoById(userInfoParam, principal.getId());
    }

    @RoleAuthorization.AuthenticatedUser
    @PostMapping("/import")
    /* Create bulk user by data from importing file */
    @Result(message = "message.success.user.import")
    public void importNewUser(@RequestParam MultipartFile file) {
        userService.importFile(file);
    }

    @RoleAuthorization.AuthenticatedUser
    @PutMapping("/saveAvatar")
    /* Save user avatar */
    @Result(message = "message.success.user.saveAvatar")
    public Map<String, String> saveAvatar(
            @RequestParam @RuleAnnotation.CheckAvatar MultipartFile avatar,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return userService.saveAvatarById(avatar, principal.getId());
    }

    @RoleAuthorization.AuthenticatedUser
    @GetMapping(value = "/getAvatar/{fileName}")
    /* Get user avatar by saved file name */
    public byte[] getAvatar(@PathVariable String fileName) throws IOException {
        return storageService.loadAsResource(fileName, UserService.AVATAR_FOLDER).getContentAsByteArray();
    }
}
