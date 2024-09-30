package org.product_delivery_backend.mapper;

import org.mapstruct.Mapper;
import org.product_delivery_backend.DTO.userDto.UserProfileDto;
import org.product_delivery_backend.DTO.userDto.UserRequestDto;
import org.product_delivery_backend.DTO.userDto.UserResponseDto;
import org.product_delivery_backend.entity.User;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDto userRequestDto);

    UserResponseDto toResponse(User user);

    List<UserResponseDto> toResponseList(List<User> users);

    //UserProfileDto toUserProfileDto(User user);
}
