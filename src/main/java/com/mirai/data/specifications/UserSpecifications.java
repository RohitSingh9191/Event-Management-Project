package com.mirai.data.specifications;

import com.mirai.constants.PolicyEnum;
import com.mirai.data.entities.Users;
import com.mirai.models.request.UserFilters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class UserSpecifications {
    public static Specification<Users> searchUsers(UserFilters userFilters) {
        Specification<Users> spec = Specification.where(null);

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

        String sortBy = null;
        String orderBy = null;
        if (userFilters.getSortBy() != null) {
            sortBy = userFilters.getSortBy();
            if (sortBy.isEmpty()) sortBy = "name";
        }

        if (userFilters.getOrderBy() != null) {
            orderBy = userFilters.getOrderBy();
            if (orderBy.isEmpty()) orderBy = "asc";
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
}
