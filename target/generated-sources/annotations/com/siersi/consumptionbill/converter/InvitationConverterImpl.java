package com.siersi.consumptionbill.converter;

import com.siersi.consumptionbill.dto.InvitationRequest;
import com.siersi.consumptionbill.entity.Invitation;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T15:42:25+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
public class InvitationConverterImpl implements InvitationConverter {

    @Override
    public Invitation toInvitation(InvitationRequest invitationRequest) {
        if ( invitationRequest == null ) {
            return null;
        }

        Invitation invitation = new Invitation();

        invitation.setBillId( invitationRequest.getBillId() );
        invitation.setInviterId( invitationRequest.getInviterId() );
        invitation.setInviteeId( invitationRequest.getInviteeId() );

        return invitation;
    }
}
