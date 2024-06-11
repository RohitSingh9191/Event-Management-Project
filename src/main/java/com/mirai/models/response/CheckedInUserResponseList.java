package com.mirai.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CheckedInUserResponseList {
    private Integer totalCount;
    private List<CheckedInUserResponse> checkedInUserResponses;
}
