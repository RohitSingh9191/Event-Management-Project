package com.mirai.models.request;

import lombok.Data;

@Data
public class CheckInFilters {
    private String name;
    private String checkIn;
    private String sortBy;
    private String orderBy;
    private String limit;
    private String offset;
}
