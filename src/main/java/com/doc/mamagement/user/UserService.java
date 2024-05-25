package com.doc.mamagement.user;

import com.doc.mamagement.entity.document.Document;
import com.doc.mamagement.entity.user.Role;
import com.doc.mamagement.entity.user.RoleConstant;
import com.doc.mamagement.entity.user.User;
import com.doc.mamagement.user.dto.*;
import com.doc.mamagement.utility.Helper;
import com.doc.mamagement.utility.datatable.DatatableResult;
import com.doc.mamagement.utility.file_handling.StorageService;
import com.doc.mamagement.web.exception.UnprocessableException;
import com.doc.mamagement.web.exception.NoPermissionException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    public static final String IMPORT_FILE_SAMPLE_NAME = "user_import_sample.xlsx";
    public static final String AVATAR_FOLDER = "avatar";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    public void createNewUser(UserRegister registerData) {
        User newUser = userMapper.toEntity(registerData);
        userRepository.save(newUser).block();
    }

    public Map<String, String> saveAvatarById(MultipartFile avatar, Long id) {
        User user = findByIdFromRepo(id);
        String userAvatar = user.getAvatar();

        if (userAvatar == null || userAvatar.isEmpty()) {
            String fileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(avatar.getOriginalFilename());
            storageService.store(avatar, AVATAR_FOLDER, fileName);
            user.setAvatar(fileName);
            userRepository.save(user).block();
            return Map.of("fileName", user.getAvatar());
        }

        storageService.store(avatar, AVATAR_FOLDER, userAvatar);
        return Map.of("fileName", userAvatar);
    }

    public UserInfoResult findByUser(Long id) {
        User user = findByIdFromRepo(id);
        return userMapper.toDTO(user);
    }

    public DatatableResult<UserInfoResult> findAllByFilter(UserFilter userFilter) {
        List<User> users = userRepository.findAllWithRole().collectList().block();

        if (users == null || users.isEmpty())
            return new DatatableResult<>();

        String keyword = userFilter.getKeyword().toLowerCase();
        List<String> attributesToSearch = userFilter.getKeywordBy();
        List<String> rolesFilter = userFilter.getRoles();
        List<Boolean> statusFilter = userFilter.getStatus();
        LocalDateTime dateFilterStart = userFilter.getStartDate().atStartOfDay();
        LocalDateTime dateFilterEnd = userFilter.getEndDate().atStartOfDay().with(LocalTime.MAX);

        List<User> filteredUsers = users.stream()
                .filter(userInfoResult -> {
                    /*
                            - Filter the list of UserInfoResult based on whether any of the specified attribute of
                        UserInfoResult contain the keyword.
                            - The getValueFromObjectAttribute method helps to retrieve the value of the specified
                        attribute from an object dynamically.
                    */
                    boolean matchInAttributes = attributesToSearch.stream()
                            .map(attribute -> Helper.getValueFromObjectAttribute(userInfoResult, attribute))
                            .anyMatch(value -> value != null && value.toLowerCase().contains(keyword));

                    boolean matchRoles = rolesFilter.isEmpty() || rolesFilter.contains(userInfoResult.getRole().getCode());
                    boolean matchStatus = statusFilter.isEmpty() || statusFilter.contains((userInfoResult.getIsDisabled()));
                    boolean matchDateTimeBetween = userInfoResult.getCreatedAt().isAfter(dateFilterStart)
                            && userInfoResult.getCreatedAt().isBefore(dateFilterEnd);

                    return matchInAttributes && matchRoles && matchStatus && matchDateTimeBetween;
                })
                .toList();

        List<UserInfoResult> userInfoResults = filteredUsers.stream().map(userMapper::toDTO).toList();

        /* Calculate the size of each page and get page data from the userInfoResults according to the size */
        int start = userFilter.getStart();
        int length = userFilter.getLength();
        int page = start / length;
        Pageable pageRequest = PageRequest.of(page, length);
        int end = Math.min((start + pageRequest.getPageSize()), userInfoResults.size());
        List<UserInfoResult> pageContent = userInfoResults.subList(start, end);

        /* Response data for datatable rendering */
        DatatableResult<UserInfoResult> datatableResult = new DatatableResult<>();
        datatableResult.setData(pageContent);
        datatableResult.setDraw(userFilter.getDraw());
        Long totalRecord = (long) userInfoResults.size();
        datatableResult.setRecordsFiltered(totalRecord);
        datatableResult.setRecordsTotal(totalRecord);

        return datatableResult;
    }

    public void updatePasswordById(Long id, String newPassword) {
        User user = findByIdFromRepo(id);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user).block();
    }

    public void updateUserByAdmin(UserInfoParamByAdmin userInfoParamByAdmin) {
        User user = userRepository.findByUsername(userInfoParamByAdmin.getUsername())
                .switchIfEmpty(Mono.error(new UnprocessableException("message.fail.user.find")))
                .block();

        if (user.getRole().getCode().equals(RoleConstant.ADMIN.name()))
            throw new NoPermissionException("error.403");

        /* Handle new password */
        String newPassword = userInfoParamByAdmin.getNewPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        /* Handle new role */
        RoleConstant newRole = RoleConstant.parse(userInfoParamByAdmin.getRole());
        user.setRole(new Role(newRole.name(), newRole.getValue()));

        /* Handle new status */
        user.setIsDisabled(userInfoParamByAdmin.getIsDisabled());

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user).block();
    }

    public void updateUserStatusById(Long id) {
        User user = findByIdFromRepo(id);
        user.setIsDisabled(!user.getIsDisabled());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user).block();
    }

    public void updateUserInfoById(UserInfoParam userInfoParam, Long id) {
        User user = findByIdFromRepo(id);
        user.setFullName(userInfoParam.getFullName());
        user.setEmail(userInfoParam.getEmail());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user).block();
    }

    public User findByIdFromRepo(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UnprocessableException("message.fail.user.find")))
                .block();
    }

    public List<Role> getAllRoles() {
        return Arrays.stream(RoleConstant.values()).map(item -> new Role(item.name(), item.getValue())).toList();
    }

    public void importFile(MultipartFile userAccFile) {
        List<UserRegister> userRegisterList = new ArrayList<>();
        Workbook workbook;
        Sheet sheet;
        try {
            workbook = new XSSFWorkbook(userAccFile.getInputStream());
            workbook.close();
        } catch (Exception e) {
            throw new UnprocessableException("message.error.file");
        }

        sheet = workbook.getSheetAt(0);
        int usernameColumnIndex = 0;

        for (Row row : sheet) {
            String username = null;
            String password = null;
            String fullName = null;
            String role = null;

            if (row.getRowNum() == 0)
                continue;

            for (Cell cell : row) {
                if (cell.getColumnIndex() == usernameColumnIndex) {
                    username = getCellValue(cell);
                }

                if (cell.getColumnIndex() == usernameColumnIndex + 1) {
                    password = getCellValue(cell);
                }

                if (cell.getColumnIndex() == usernameColumnIndex + 2) {
                    fullName = getCellValue(cell);
                }

                if (cell.getColumnIndex() == usernameColumnIndex + 3) {
                    role = getCellValue(cell);
                }
            }

            if (username == null || password == null || role == null) {
                continue;
            }

            userRegisterList.add(new UserRegister(username, password, fullName, role));
        }

        List<User> users = new ArrayList<>(userRegisterList.stream().map(userMapper::toEntity).toList());
        List<User> existedUsers = userRepository.findAll().collectList().block();

        users.removeAll(existedUsers);
        userRepository.saveAll(users).collectList().block();
    }

    public String getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getRichStringCellValue().getString().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> null;
        };
    }
    public List<UserSelection> getAllUserSelection(){
        List<User> users = userRepository.findAll().collectList().block();

        return users
                .stream()
                .filter(user -> user.getRole().getCode().equals(RoleConstant.USER.name()))
                .map(userMapper::toUserSelection)
                .toList();
    }
}

