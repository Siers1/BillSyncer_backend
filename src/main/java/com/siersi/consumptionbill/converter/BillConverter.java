package com.siersi.consumptionbill.converter;

import com.siersi.consumptionbill.dto.UpdateBillRequest;
import com.siersi.consumptionbill.entity.Bill;
import com.siersi.consumptionbill.vo.BillVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 账单对象转换器接口
 * 使用MapStruct框架实现不同层次对象之间的转换
 * 负责账单实体、DTO和VO之间的相互转换
 * 
 * @author siersi
 * @version 1.0
 */
@Mapper
public interface BillConverter {
    
    /**
     * 转换器实例，用于获取转换器的实现
     */
    BillConverter INSTANCE = Mappers.getMapper(BillConverter.class);

    /**
     * 将账单实体转换为账单视图对象
     * 
     * @param bill 账单实体对象
     * @return 账单视图对象
     */
    BillVo ToBillVo(Bill bill);

    /**
     * 将账单请求DTO转换为账单实体
     * 特殊映射：将billId字段映射到id字段
     * 
     * @param updateBillRequest 账单请求DTO对象
     * @return 账单实体对象
     */
    @Mapping(target = "id", source = "billId")
    Bill ToBill(UpdateBillRequest updateBillRequest);
}
