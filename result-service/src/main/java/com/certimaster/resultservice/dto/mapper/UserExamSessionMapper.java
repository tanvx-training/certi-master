package com.certimaster.resultservice.dto.mapper;

import com.certimaster.resultservice.dto.response.UserExamSessionResponse;
import com.certimaster.resultservice.entity.UserExamSession;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for UserExamSession entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserExamSessionMapper {

    UserExamSessionResponse toResponse(UserExamSession entity);

    List<UserExamSessionResponse> toResponseList(List<UserExamSession> entities);
}
