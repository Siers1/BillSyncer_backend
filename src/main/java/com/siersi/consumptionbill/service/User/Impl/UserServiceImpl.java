package com.siersi.consumptionbill.service.User.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.siersi.consumptionbill.converter.UserConverter;
import com.siersi.consumptionbill.dto.LoginRequest;
import com.siersi.consumptionbill.dto.PasswordDTO;
import com.siersi.consumptionbill.dto.UserDTO;
import com.siersi.consumptionbill.entity.User;
import com.siersi.consumptionbill.exception.BusinessException;
import com.siersi.consumptionbill.enums.BusinessExceptionEnum;
import com.siersi.consumptionbill.mapper.UserMapper;
import com.siersi.consumptionbill.service.User.UserService;
import com.siersi.consumptionbill.utils.JwtUtil;
import com.siersi.consumptionbill.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑，包括注册、登录验证、密码加密、用户信息查询等功能
 * 使用MD5+盐值的方式对用户密码进行加密存储
 * 
 * @author siersi
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 密码加密的盐值，从配置文件中读取
     */
    @Value("${jwt.salt}")
    private String salt;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    /**
     * 用户注册实现
     * 检查账号是否已存在，如果不存在则创建新用户
     * 密码使用MD5+盐值进行加密存储
     * 
     * @param loginRequest 包含账号密码的注册请求
     * @throws BusinessException 当账号已存在时抛出异常
     */
    @Override
    public void register(LoginRequest loginRequest) {
        // 检查账号是否已存在
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(User::getAccount).eq(loginRequest.getAccount());

        User user = userMapper.selectOneByQuery(queryWrapper);
        if (user != null) {
            throw new BusinessException(BusinessExceptionEnum.REGISTERED);
        }

        // 使用MD5+盐值加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((salt + loginRequest.getPassword()).getBytes());

        // 创建新用户
        User newUser = new User();
        newUser.setAccount(loginRequest.getAccount());
        newUser.setPassword(encryptPassword);

        userMapper.insertSelective(newUser);
    }

    /**
     * 用户登录验证实现
     * 验证用户账号、密码、账号状态等信息
     * 
     * @param loginRequest 包含账号密码的登录请求
     * @throws BusinessException 当账号未注册、已被封禁或密码错误时抛出异常
     */
    @Override
    public void login(LoginRequest loginRequest) {
        // 对输入密码进行加密，用于与数据库中的密码比较
        String encryptPassword = DigestUtils.md5DigestAsHex((salt + loginRequest.getPassword()).getBytes());

        // 根据账号查询用户信息
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(User::getAccount).eq(loginRequest.getAccount());

        User user = userMapper.selectOneByQuery(queryWrapper);

        // 检查账号是否存在
        if (user == null) {
            throw new BusinessException(BusinessExceptionEnum.UN_REGISTERED);
        }

        // 检查账号是否被封禁
        if (user.getValid() == 0) {
            throw new BusinessException(BusinessExceptionEnum.UN_VALID);
        }

        // 验证密码是否正确
        if (!encryptPassword.equals(user.getPassword())) {
            throw new BusinessException(BusinessExceptionEnum.PASSWORD_WRONG);
        }
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        Long id = userDTO.getId();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(User::getId).eq(id);

        if (userMapper.selectOneByQuery(queryWrapper) == null) {
            throw new BusinessException("用户不存在");
        }

        User user = UserConverter.INSTANCE.toUser(userDTO);
        userMapper.update(user);
    }

    @Override
    public void changePassword(PasswordDTO passwordDTO, String authorization) {
        Long userId = getIdByAuthorization(authorization);
        User user = userMapper.selectOneById(userId);

        String oldPassword = DigestUtils.md5DigestAsHex((salt + passwordDTO.getOldPassword()).getBytes());
        String newPassword = DigestUtils.md5DigestAsHex((salt + passwordDTO.getNewPassword()).getBytes());

        if (!user.getPassword().equals(oldPassword)) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(newPassword);
        userMapper.update(user);
    }

    /**
     * 根据账号获取用户ID
     * 
     * @param account 用户账号
     * @return 用户ID
     */
    @Override
    public Long getIdByAccount(String account) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(User::getAccount).eq(account);

        return userMapper.selectOneByQuery(queryWrapper).getId();
    }

    /**
     * 根据授权令牌获取用户ID
     * 从JWT令牌中解析出用户账号，再根据账号获取用户ID
     * 
     * @param authorization 授权令牌（包含Bearer前缀）
     * @return 用户ID
     */
    @Override
    public Long getIdByAuthorization(String authorization) {
        return getIdByAccount(jwtUtil.getAccountFromToken(jwtUtil.extractTokenFromAuthorization(authorization)));
    }

    /**
     * 根据用户ID获取用户详细信息
     * 将User实体转换为UserVo视图对象返回
     * 
     * @param id 用户ID
     * @return 用户视图对象，包含用户的基本信息
     */
    @Override
    public UserVo getUserById(Long id) {
        User user = getById(id);
        return UserConverter.INSTANCE.toVo(user);
    }
}
