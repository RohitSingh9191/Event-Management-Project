package com.mirai.utils;

import com.mirai.exception.customException.ApplicationErrorCode;
import com.mirai.exception.customException.MiraiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
public class MiraiUtils {
    /**
     * Creates a pageable object based on the provided limit and offset values.
     *
     * @param limit  Maximum number of records to retrieve.
     * @param offset Number of records to skip from the beginning.
     * @return Pageable object for pagination.
     * @throws  MiraiException limit or offset is less than zero.
     */
    public static Pageable createPageable(String limit, String offset) {
        log.info("Start of createPageable method. Creating Pageable object with limit: {}, offset: {}", limit, offset);
        int limitValue = limit == null || limit.isBlank() || limit.equals("0") ? 5 : Integer.parseInt(limit);
        int offsetValue = offset == null || offset.isBlank() || offset.equals("0")
                ? 0
                : Integer.parseInt(offset); // || Integer.parseInt(offset) < 0

        if (limitValue == 0) limitValue = 5;

        if (limitValue < 0 || offsetValue < 0) {
            throw new MiraiException(ApplicationErrorCode.LIMIT_OFFSET_NOT_VALID);
        }
        log.info(
                "End of createPageable method. Created Pageable object with limit: {}, offset: {}",
                limitValue,
                offsetValue);
        return PageRequest.of(offsetValue, limitValue);
    }
}
