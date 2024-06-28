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
public class SpeakerResponseList {
    private Integer totalCount;
    private List<SpeakerResponse> speakerResponse;
}
