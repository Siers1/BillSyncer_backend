package com.siersi.consumptionbill.vo;

import lombok.Data;

@Data
public class TokenVo {
    private String accessToken;
    private String refreshToken;
}
