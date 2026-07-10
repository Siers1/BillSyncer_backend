package com.siersi.consumptionbill.converter;

import com.siersi.consumptionbill.dto.UserBillDTO;
import com.siersi.consumptionbill.entity.UserBill;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T15:42:25+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
public class UserBillConverterImpl implements UserBillConverter {

    @Override
    public UserBill ToUserBill(UserBillDTO userBillDTO) {
        if ( userBillDTO == null ) {
            return null;
        }

        UserBill userBill = new UserBill();

        userBill.setBillId( userBillDTO.getBillId() );
        userBill.setUserId( userBillDTO.getUserId() );

        return userBill;
    }
}
