package com.xpg.usercenter.model.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 7233161158225035806L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;


}
