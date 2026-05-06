package com.siersi.consumptionbill.converter;

import com.siersi.consumptionbill.dto.UserDTO;
import com.siersi.consumptionbill.entity.User;
import com.siersi.consumptionbill.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    User toUser(UserDTO userDTO);

    UserVo toVo(User user);
}
