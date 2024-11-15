package com.mirai.data.specifications;

import com.mirai.constants.CheckStatus;
import com.mirai.constants.PolicyEnum;
import com.mirai.constants.UserStatus;
import com.mirai.data.entities.Checkin;
import com.mirai.data.entities.Users;
import com.mirai.models.request.CheckInFilters;
import com.mirai.models.request.UserFilters;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class UserSpecifications {
    public static Specification<Users> searchUsers(UserFilters userFilters) {
        Specification<Users> spec = Specification.where(null);

        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.isNull(root.get("status")),
                criteriaBuilder.notEqual(root.get("status"), UserStatus.INACTIVE.name())));

        String id = userFilters.getId();
        if (id != null && !id.isEmpty()) {
            spec = spec.and(UserSpecifications.withId(id));
        }

        // Name
        String name = userFilters.getName();
        if (name != null && !name.isEmpty()) {
            spec = spec.and(UserSpecifications.withName(name));
        }

        // Email
        String email = userFilters.getEmail();
        if (email != null && !email.isEmpty()) {
            spec = spec.and(UserSpecifications.withEmail(email));
        }

        // Phone
        String phone = userFilters.getPhone();
        if (phone != null && !phone.isEmpty()) {
            spec = spec.and(UserSpecifications.withPhone(phone));
        }

        // Company
        String company = userFilters.getCompany();
        if (company != null && !company.isEmpty()) {
            spec = spec.and(UserSpecifications.withCompany(company));
        }

        // Designation
        String designation = userFilters.getDesignation();
        if (designation != null && !designation.isEmpty()) {
            spec = spec.and(UserSpecifications.withDesignation(designation));
        }

        // Type
        String type = userFilters.getType();
        if (type != null && !type.isEmpty()) {
            spec = spec.and(UserSpecifications.withType(type));
        }

        // Policy Type
        String policyType = userFilters.getPolicyType();
        if (policyType != null && !policyType.isEmpty()) {
            spec = spec.and(UserSpecifications.withPolicyType(policyType));
        }
        // Status
        String status = userFilters.getStatus();
        if (status != null && !status.isEmpty()) {
            spec = spec.and(UserSpecifications.withStatus(status.toUpperCase()));
        }

        // checkIn
        String checkIn = userFilters.getCheckIn();
        if (checkIn != null) {
            spec = spec.and(UserSpecifications.withCheckIn(checkIn));
        }

        String sortBy = null;
        String orderBy = null;
        if (userFilters.getSortBy() == null || userFilters.getSortBy().isEmpty()) {
            sortBy = "modifiedAt";
        } else {
            sortBy = userFilters.getSortBy();
        }

        if (userFilters.getOrderBy() == null || userFilters.getOrderBy().isEmpty()) {
            orderBy = "desc";

        } else {
            orderBy = userFilters.getOrderBy();
        }

        if (sortBy != null && sortBy.equalsIgnoreCase("modifiedAt")) {
            if (orderBy.equalsIgnoreCase("desc")) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.desc(root.get("modifiedAt")));
                    return criteriaBuilder.conjunction();
                });
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.asc(root.get("modifiedAt")));
                    return criteriaBuilder.conjunction();
                });
            }
        }

        if (sortBy != null && sortBy.equalsIgnoreCase("name")) {
            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.desc(root.get("name")));
                    return criteriaBuilder.conjunction();
                });
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.asc(root.get("name")));
                    return criteriaBuilder.conjunction();
                });
            }
        }

        if (sortBy != null && sortBy.equalsIgnoreCase("id")) {
            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.desc(root.get("id")));
                    return criteriaBuilder.conjunction();
                });
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.asc(root.get("id")));
                    return criteriaBuilder.conjunction();
                });
            }
        }

        if (sortBy != null && sortBy.equalsIgnoreCase("email")) {
            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.desc(root.get("email")));
                    return criteriaBuilder.conjunction();
                });
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.asc(root.get("email")));
                    return criteriaBuilder.conjunction();
                });
            }
        }

        if (sortBy != null && sortBy.equalsIgnoreCase("phone")) {
            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.desc(root.get("phone")));
                    return criteriaBuilder.conjunction();
                });
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.asc(root.get("phone")));
                    return criteriaBuilder.conjunction();
                });
            }
        }

        if (sortBy != null && sortBy.equalsIgnoreCase("company")) {
            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.desc(root.get("company")));
                    return criteriaBuilder.conjunction();
                });
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.asc(root.get("company")));
                    return criteriaBuilder.conjunction();
                });
            }
        }
        if (sortBy != null && sortBy.equalsIgnoreCase("designation")) {
            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.desc(root.get("designation")));
                    return criteriaBuilder.conjunction();
                });
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.asc(root.get("designation")));
                    return criteriaBuilder.conjunction();
                });
            }
        }

        if (sortBy != null && sortBy.equalsIgnoreCase("type")) {
            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.desc(root.get("type")));
                    return criteriaBuilder.conjunction();
                });
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    query.orderBy(criteriaBuilder.asc(root.get("type")));
                    return criteriaBuilder.conjunction();
                });
            }
        }

        log.info("Specification created successfully");
        return spec;
    }

    private static Specification<Users> withStatus(String status) {
        log.info("Filtering users by email: {}", status);
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<Users> withId(String id) {
        int userId = Integer.parseInt(id);
        log.info("Start of hasId method. Filtering companies by ID: {}", id);
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), userId);
    }

    private static Specification<Users> withName(String name) {
        log.info("Filtering users by name: {}", name);
        return (root, query, criteriaBuilder) -> {
            String namePattern = "%" + name.toLowerCase() + "%"; // Convert search term to lowercase
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), namePattern);
        };
    }

    private static Specification<Users> withEmail(String email) {
        log.info("Filtering users by email: {}", email);
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
    }

    private static Specification<Users> withPhone(String phone) {
        log.info("Filtering users by phone: {}", phone);
        String formattedPhone = phone.replaceAll("[^\\d]", "");
        return (root, query, criteriaBuilder) -> {
            if (formattedPhone.isEmpty()) {
                return null; // Return null if phone number is empty after formatting
            }
            return criteriaBuilder.equal(root.get("phone"), formattedPhone);
        };
    }

    private static Specification<Users> withCompany(String company) {
        log.info("Filtering users by company: {}", company);
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("company"), company);
    }

    private static Specification<Users> withDesignation(String designation) {
        log.info("Filtering users by designation: {}", designation);
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("designation"), designation);
    }

    private static Specification<Users> withType(String type) {
        log.info("Filtering users by type: {}", type);
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), type);
    }

    private static Specification<Users> withPolicyType(String policyType) {
        log.info("Filtering users by policy type: {}", policyType);
        Boolean policy;
        if (PolicyEnum.ACCEPT.name().equalsIgnoreCase(policyType)) {
            policy = true;
        } else if (PolicyEnum.REJECT.name().equalsIgnoreCase(policyType)) {
            policy = false;
        } else {
            policy = null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isPolicyAccept"), policy);
    }

    private static Specification<Users> withCheckIn(String checkIn) {
        log.info("Filtering users by check-in: {}", checkIn);
        return (root, query, criteriaBuilder) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<Checkin> checkinRoot = subquery.from(Checkin.class);
            subquery.select(checkinRoot.get("userId"));

            if (checkIn.equalsIgnoreCase(CheckStatus.IN.name())) {
                subquery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(checkinRoot.get("userId"), root.get("id")),
                        criteriaBuilder.equal(checkinRoot.get("status"), CheckStatus.IN.name())));
                return criteriaBuilder.exists(subquery);
            } else if (checkIn.equalsIgnoreCase(CheckStatus.OUT.name())) {
                subquery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(checkinRoot.get("userId"), root.get("id")),
                        criteriaBuilder.equal(checkinRoot.get("status"), CheckStatus.OUT.name())));
                return criteriaBuilder.exists(subquery);
            } else if (checkIn.equalsIgnoreCase(CheckStatus.NOTIN.name())) {
                subquery.where(criteriaBuilder.equal(checkinRoot.get("userId"), root.get("id")));
                return criteriaBuilder.not(criteriaBuilder.exists(subquery));
            }
            return null;
        };
    }

    //    public static Specification<Users> searchCheckUsers(CheckInFilters CheckInFilters) {
    //        Specification<Users> spec = Specification.where(null);
    //
    //
    //        // Name
    //        String name = CheckInFilters.getName();
    //        if (name != null && !name.isEmpty()) {
    //            spec = spec.and(UserSpecifications.withName(name));
    //        }
    //
    //
    //        // checkIn
    //        String checkIn = CheckInFilters.getCheckIn();
    //        if (checkIn != null) {
    //            spec = spec.and(UserSpecifications.withCheckIn(checkIn));
    //        }
    //
    //        String sortBy = null;
    //        String orderBy = null;
    //        if (CheckInFilters.getSortBy() == null || CheckInFilters.getSortBy().isEmpty()) {
    //            sortBy = "modifiedAt";
    //        } else {
    //            sortBy = CheckInFilters.getSortBy();
    //        }
    //
    //        if (CheckInFilters.getOrderBy() == null || CheckInFilters.getOrderBy().isEmpty()) {
    //            orderBy = "desc";
    //
    //        } else {
    //            orderBy = CheckInFilters.getOrderBy();
    //        }
    //
    //        if (sortBy != null && sortBy.equalsIgnoreCase("checkIn")) {
    //            if (orderBy.equalsIgnoreCase("desc")) {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.desc(root.get("checkIn")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            } else {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.asc(root.get("checkIn")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            }
    //        }
    //
    //        if (sortBy != null && sortBy.equalsIgnoreCase("name")) {
    //            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.desc(root.get("name")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            } else {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.asc(root.get("name")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            }
    //        }
    //
    //        if (sortBy != null && sortBy.equalsIgnoreCase("id")) {
    //            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.desc(root.get("id")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            } else {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.asc(root.get("id")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            }
    //        }
    //
    //
    //        if (sortBy != null && sortBy.equalsIgnoreCase("phone")) {
    //            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.desc(root.get("phone")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            } else {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.asc(root.get("phone")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            }
    //        }
    //
    //        if (sortBy != null && sortBy.equalsIgnoreCase("company")) {
    //            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.desc(root.get("company")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            } else {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.asc(root.get("company")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            }
    //        }
    //        if (sortBy != null && sortBy.equalsIgnoreCase("designation")) {
    //            if (orderBy != null && orderBy.equalsIgnoreCase("desc")) {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.desc(root.get("designation")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            } else {
    //                spec = spec.and((root, query, criteriaBuilder) -> {
    //                    query.orderBy(criteriaBuilder.asc(root.get("designation")));
    //                    return criteriaBuilder.conjunction();
    //                });
    //            }
    //        }
    //        log.info("Specification created successfully");
    //        return spec;
    //    }

    public static Specification<Checkin> searchCheckins(CheckInFilters checkInFilters) {
        Specification<Checkin> spec = Specification.where(null);

        boolean hasFilters = false;

        // Name filter using subquery
        if (checkInFilters.getName() != null && !checkInFilters.getName().isEmpty()) {
            spec = spec.and(withUserName(checkInFilters.getName()));
            hasFilters = true;
        }

        // Check-in status filter
        if (checkInFilters.getCheckIn() != null && !checkInFilters.getCheckIn().isEmpty()) {
            spec = spec.and(withCheckInStatus(checkInFilters.getCheckIn()));
            hasFilters = true;
        }

        // Sorting logic
        String sortBy = checkInFilters.getSortBy() != null ? checkInFilters.getSortBy() : "checkinTime";
        String orderBy = checkInFilters.getOrderBy() != null ? checkInFilters.getOrderBy() : "desc";

        spec = spec.and(orderByField(sortBy, orderBy));

        // If no filters are provided, match all records
        if (!hasFilters) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        }

        log.info("Specification for Checkin created successfully");
        return spec;
    }

    private static Specification<Checkin> withUserName(String name) {
        return (root, query, criteriaBuilder) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<Users> userRoot = subquery.from(Users.class);
            subquery.select(userRoot.get("id"));
            String namePattern = "%" + name.toLowerCase() + "%";
            subquery.where(criteriaBuilder.like(criteriaBuilder.lower(userRoot.get("name")), namePattern));
            return criteriaBuilder.in(root.get("userId")).value(subquery);
        };
    }

    private static Specification<Checkin> withCheckInStatus(String checkInStatus) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), checkInStatus);
    }

    private static Specification<Checkin> orderByField(String sortBy, String orderBy) {
        return (root, query, criteriaBuilder) -> {
            if (sortBy.equalsIgnoreCase("checkinTime")) {
                if (orderBy.equalsIgnoreCase("desc")) {
                    query.orderBy(criteriaBuilder.desc(root.get("checkinTime")));
                } else {
                    query.orderBy(criteriaBuilder.asc(root.get("checkinTime")));
                }
            } else if (sortBy.equalsIgnoreCase("name")) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<Users> userRoot = subquery.from(Users.class);
                subquery.select(userRoot.get("name"));
                subquery.where(criteriaBuilder.equal(userRoot.get("id"), root.get("userId")));
                if (orderBy.equalsIgnoreCase("desc")) {
                    query.orderBy(criteriaBuilder.desc(subquery.getSelection()));
                } else {
                    query.orderBy(criteriaBuilder.asc(subquery.getSelection()));
                }
            } else if (sortBy.equalsIgnoreCase("email")) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<Users> userRoot = subquery.from(Users.class);
                subquery.select(userRoot.get("email"));
                subquery.where(criteriaBuilder.equal(userRoot.get("id"), root.get("userId")));
                if (orderBy.equalsIgnoreCase("desc")) {
                    query.orderBy(criteriaBuilder.desc(subquery.getSelection()));
                } else {
                    query.orderBy(criteriaBuilder.asc(subquery.getSelection()));
                }
            } else if (sortBy.equalsIgnoreCase("phone")) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<Users> userRoot = subquery.from(Users.class);
                subquery.select(userRoot.get("phone"));
                subquery.where(criteriaBuilder.equal(userRoot.get("id"), root.get("userId")));
                if (orderBy.equalsIgnoreCase("desc")) {
                    query.orderBy(criteriaBuilder.desc(subquery.getSelection()));
                } else {
                    query.orderBy(criteriaBuilder.asc(subquery.getSelection()));
                }
            } else if (sortBy.equalsIgnoreCase("company")) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<Users> userRoot = subquery.from(Users.class);
                subquery.select(userRoot.get("company"));
                subquery.where(criteriaBuilder.equal(userRoot.get("id"), root.get("userId")));
                if (orderBy.equalsIgnoreCase("desc")) {
                    query.orderBy(criteriaBuilder.desc(subquery.getSelection()));
                } else {
                    query.orderBy(criteriaBuilder.asc(subquery.getSelection()));
                }
            } else if (sortBy.equalsIgnoreCase("designation")) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<Users> userRoot = subquery.from(Users.class);
                subquery.select(userRoot.get("designation"));
                subquery.where(criteriaBuilder.equal(userRoot.get("id"), root.get("userId")));
                if (orderBy.equalsIgnoreCase("desc")) {
                    query.orderBy(criteriaBuilder.desc(subquery.getSelection()));
                } else {
                    query.orderBy(criteriaBuilder.asc(subquery.getSelection()));
                }
            } else if (sortBy.equalsIgnoreCase("id")) {
                if (orderBy.equalsIgnoreCase("desc")) {
                    query.orderBy(criteriaBuilder.desc(root.get("id")));
                } else {
                    query.orderBy(criteriaBuilder.asc(root.get("id")));
                }
            }

            return criteriaBuilder.conjunction();
        };
    }
}
