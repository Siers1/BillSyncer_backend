package com.siersi.consumptionbill.converter;

import com.siersi.consumptionbill.dto.UserDTO;
import com.siersi.consumptionbill.entity.User;
import com.siersi.consumptionbill.vo.UserVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T15:42:25+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
public class UserConverterImpl implements UserConverter {

    @Override
    public User toUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setId( userDTO.getId() );
        user.setUsername( userDTO.getUsername() );
        user.setAvatar( userDTO.getAvatar() );

        return user;
    }

    @Override
    public UserVo toVo(User user) {
        if ( user == null ) {
            return null;
        }

        UserVo userVo = new UserVo();

        userVo.setId( user.getId() );
        userVo.setAccount( user.getAccount() );
        userVo.setUsername( user.getUsername() );
        userVo.setAvatar( user.getAvatar() );
        userVo.setCreateTime( user.getCreateTime() );
        userVo.setUpdateTime( user.getUpdateTime() );

        return userVo;
    }
}
