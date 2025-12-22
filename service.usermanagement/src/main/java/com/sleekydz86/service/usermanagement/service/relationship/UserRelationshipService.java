package com.sleekydz86.service.usermanagement.service.relationship;

import com.sleekydz86.service.usermanagement.common.ServiceResponse;
import com.sleekydz86.service.usermanagement.dto.UserDto;

public interface UserRelationshipService {
    ServiceResponse<Integer> updateGuardianMapping(UserDto dto);
    ServiceResponse<Integer> updateDoctorMapping(UserDto dto);
}

