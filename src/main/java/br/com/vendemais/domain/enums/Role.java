package br.com.vendemais.domain.enums;

public enum Role {
    ADMIN(0, "ROLE_ADMIN"),
    USER(1, "ROLE_USER");

    private final Integer code;
    private final String authority;

    Role(Integer code, String authority) {
        this.code = code;
        this.authority = authority;
    }

    public Integer getCode() {
        return code;
    }

    public String getAuthority() {
        return authority;
    }

    public static Role toEnum(Integer code) {
        if (code == null) {
            return null;
        }

        for (Role role : Role.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }

        throw new IllegalArgumentException("Invalid role code: " + code);
    }
}