package com.flybird.nestwise.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flybird.nestwise.clients.banks.kredobank.dto.LoginResponse;
import com.flybird.nestwise.domain.Goal;
import com.flybird.nestwise.domain.User;
import com.flybird.nestwise.dto.GoalRequestDto;
import com.flybird.nestwise.dto.GoalResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.function.Function;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Component
public class MappingUtil {
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public Goal toDomain(GoalRequestDto requestDto, Long userId, Function<Long, Goal> goalExtractor, Function<Long, User> userExtractor) {
        var domain = modelMapper.map(requestDto, Goal.class);

        var parentId = requestDto.getParentId();
        if (nonNull(parentId)) {
            domain.setParent(goalExtractor.apply(parentId));
        }
        domain.setCreatedBy(userId);
        domain.setUser(userExtractor.apply(userId));
        domain.setCreatedAt(Instant.now());

        return domain;
    }

    public GoalResponseDto toDto(Goal goal) {
        return modelMapper.map(goal, GoalResponseDto.class);
    }

    public LoginResponse toDto(InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, LoginResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
