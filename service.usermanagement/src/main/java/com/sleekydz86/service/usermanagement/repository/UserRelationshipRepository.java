package com.sleekydz86.service.usermanagement.repository;

import com.sleekydz86.service.usermanagement.dto.UserDto;

public interface UserRelationshipRepository {
    int updateGuardianMapping(UserDto dto);
    int updateDoctorMapping(UserDto dto);
    int countSearchParent(UserDto dto);
    int deleteParentMapping(UserDto dto);
}

