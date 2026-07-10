package com.siersi.consumptionbill.converter;

import com.siersi.consumptionbill.dto.UpdateBillRequest;
import com.siersi.consumptionbill.entity.Bill;
import com.siersi.consumptionbill.vo.BillVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T15:42:25+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
public class BillConverterImpl implements BillConverter {

    @Override
    public BillVo ToBillVo(Bill bill) {
        if ( bill == null ) {
            return null;
        }

        BillVo billVo = new BillVo();

        billVo.setId( bill.getId() );
        billVo.setBillName( bill.getBillName() );
        billVo.setCreateTime( bill.getCreateTime() );

        return billVo;
    }

    @Override
    public Bill ToBill(UpdateBillRequest updateBillRequest) {
        if ( updateBillRequest == null ) {
            return null;
        }

        Bill bill = new Bill();

        bill.setId( updateBillRequest.getBillId() );
        bill.setBillName( updateBillRequest.getBillName() );
        bill.setValid( updateBillRequest.getValid() );

        return bill;
    }
}
