package com.mirai.models.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CheckedInUserResponseList {
    private Integer totalCount;
    private List<CheckedInUserResponse> checkedInUserResponses;
}
