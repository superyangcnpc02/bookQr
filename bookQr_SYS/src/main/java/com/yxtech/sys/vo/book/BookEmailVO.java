package com.yxtech.sys.vo.book;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 审核邮箱VO
 * Created by yanfei on 2015/11/3.
 */
public class BookEmailVO {
    @NotBlank(message = "邮箱地址不能为空")
    private String email;
    @NotBlank(message = "提示信息不能为空")
    private String tip;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
