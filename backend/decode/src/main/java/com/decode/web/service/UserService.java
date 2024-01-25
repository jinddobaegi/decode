package com.decode.web.service;

import com.decode.web.dto.UserDto;
import com.decode.web.entity.UserEntity;
import java.util.List;

public interface UserService {

  public UserDto getUserById(Long id);
  public List<UserDto> getAllUser();

}