package com.siersi.consumptionbill.converter;

import com.siersi.consumptionbill.dto.AddRecordRequest;
import com.siersi.consumptionbill.dto.UpdateRecordRequest;
import com.siersi.consumptionbill.entity.Record;
import com.siersi.consumptionbill.vo.RecordVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T15:42:25+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
public class RecordConverterImpl implements RecordConverter {

    @Override
    public Record ToRecord(RecordVo recordVo) {
        if ( recordVo == null ) {
            return null;
        }

        Record record = new Record();

        record.setId( recordVo.getId() );
        record.setBillId( recordVo.getBillId() );
        record.setCreatorId( recordVo.getCreatorId() );
        record.setItemName( recordVo.getItemName() );
        record.setItemPrice( recordVo.getItemPrice() );
        record.setConsumptionType( recordVo.getConsumptionType() );
        record.setPaymentMethod( recordVo.getPaymentMethod() );
        record.setConsumptionDate( recordVo.getConsumptionDate() );
        record.setComment( recordVo.getComment() );
        record.setCreateTime( recordVo.getCreateTime() );

        return record;
    }

    @Override
    public RecordVo ToRecordVo(Record record) {
        if ( record == null ) {
            return null;
        }

        RecordVo recordVo = new RecordVo();

        recordVo.setId( record.getId() );
        recordVo.setBillId( record.getBillId() );
        recordVo.setCreatorId( record.getCreatorId() );
        recordVo.setItemName( record.getItemName() );
        recordVo.setItemPrice( record.getItemPrice() );
        recordVo.setConsumptionType( record.getConsumptionType() );
        recordVo.setPaymentMethod( record.getPaymentMethod() );
        recordVo.setConsumptionDate( record.getConsumptionDate() );
        recordVo.setComment( record.getComment() );
        recordVo.setCreateTime( record.getCreateTime() );

        return recordVo;
    }

    @Override
    public Record ToRecord(AddRecordRequest addRecordRequest) {
        if ( addRecordRequest == null ) {
            return null;
        }

        Record record = new Record();

        record.setBillId( addRecordRequest.getBillId() );
        record.setCreatorId( addRecordRequest.getCreatorId() );
        record.setItemName( addRecordRequest.getItemName() );
        record.setItemPrice( addRecordRequest.getItemPrice() );
        record.setConsumptionType( addRecordRequest.getConsumptionType() );
        record.setPaymentMethod( addRecordRequest.getPaymentMethod() );
        record.setConsumptionDate( addRecordRequest.getConsumptionDate() );
        record.setComment( addRecordRequest.getComment() );

        return record;
    }

    @Override
    public Record ToRecord(UpdateRecordRequest updateRecordRequest) {
        if ( updateRecordRequest == null ) {
            return null;
        }

        Record record = new Record();

        record.setId( updateRecordRequest.getId() );
        record.setItemName( updateRecordRequest.getItemName() );
        record.setItemPrice( updateRecordRequest.getItemPrice() );
        record.setConsumptionType( updateRecordRequest.getConsumptionType() );
        record.setPaymentMethod( updateRecordRequest.getPaymentMethod() );
        record.setConsumptionDate( updateRecordRequest.getConsumptionDate() );
        record.setComment( updateRecordRequest.getComment() );
        record.setValid( updateRecordRequest.getValid() );

        return record;
    }
}
