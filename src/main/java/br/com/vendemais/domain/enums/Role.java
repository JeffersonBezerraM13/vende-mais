package br.com.vendemais.domain.enums;

public enum Role {
    ADMIN(0,"ROLE_ADMIN"),
    USER(1,"ROLE_USER");

    private Integer code;
    private String description;

    Role(Integer valor, String role) {
        this.code = valor;
        this.description = role;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Role toEnum(Integer code){
        if(code == null){
            return null;
        }
        for(Role p : Role.values()){
            if(p.getCode().equals(code)){
                return p;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + code);
    }
}
