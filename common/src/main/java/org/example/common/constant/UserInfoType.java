package org.example.common.constant;

import lombok.Getter;

@Getter
public enum UserInfoType {
    // 邮箱
    EMAIL(0),
    // 手机号
    PHONE(1);

    private Integer infoType;
    UserInfoType(Integer infoType){
        this.infoType = infoType;
    }
}
