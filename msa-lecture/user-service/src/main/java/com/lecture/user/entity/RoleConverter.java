package com.lecture.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * User.Role(BUYER/PROVIDER) ↔ users.role 컬럼(STUDENT/INSTRUCTOR) 변환기.
 *
 * 왜 컬럼값은 그대로 두는가:
 * auth-server(msa-lecture/auth-server:1.0)는 소스가 없는 prebuilt 이미지이고,
 * 내부 com.lecture.auth.model.User$Role 이 STUDENT/INSTRUCTOR 로 하드코딩된 채
 * 같은 users 테이블을 읽는다(로그인 시 findByEmail). 컬럼에 BUYER를 쓰는 순간
 * auth-server가 enum 파싱에 실패해 해당 계정 로그인이 깨진다.
 *
 * 따라서 물리 저장값만 레거시로 고정하고, 애플리케이션·API 응답·프론트는
 * 전부 BUYER/PROVIDER 로 통일한다.
 */
@Converter
public class RoleConverter implements AttributeConverter<User.Role, String> {

    private static final String LEGACY_BUYER = "STUDENT";
    private static final String LEGACY_PROVIDER = "INSTRUCTOR";

    @Override
    public String convertToDatabaseColumn(User.Role role) {
        if (role == null) return null;
        return role == User.Role.PROVIDER ? LEGACY_PROVIDER : LEGACY_BUYER;
    }

    @Override
    public User.Role convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return switch (dbValue) {
            case LEGACY_PROVIDER, "PROVIDER" -> User.Role.PROVIDER;
            case LEGACY_BUYER, "BUYER" -> User.Role.BUYER;
            default -> throw new IllegalStateException("알 수 없는 role 컬럼값: " + dbValue);
        };
    }
}
