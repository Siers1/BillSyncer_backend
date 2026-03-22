package com.siersi.consumptionbill.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 账单请求数据传输对象
 * 用于封装账单更新时提交的信息
 * 包含账单ID、账单名称和有效性状态
 * 
 * @author siersi
 * @version 1.0
 */
@Data
public class UpdateBillRequest {

    /**
     * 账单ID，更新时必须提供，不能为空
     */
    @NotNull(message = "账本不能为空")
    private Long billId;

    /**
     * 账单名称
     */
    private String billName;

    /**
     * 有效性标志，0-无效，1-有效
     */
    private Integer valid;
}
