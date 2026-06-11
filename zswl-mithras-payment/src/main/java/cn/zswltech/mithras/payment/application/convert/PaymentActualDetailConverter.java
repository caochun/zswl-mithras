package cn.zswltech.mithras.payment.application.convert;

import cn.zswltech.mithras.api.payment.writeoff.ActualDetailPostReq;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailDto;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetailUnconfirmed;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 10:18
 */
@Mapper(componentModel = "spring",uses = PaymentTypeConversionWorker.class)
public interface PaymentActualDetailConverter {

    ActualDetailDto entityToDto(PaymentActualDetail detail);
    ActualDetailDto unconfirmedEntityToDto(PaymentActualDetailUnconfirmed detail);
    List<ActualDetailDto> entitiesToDtos(List<PaymentActualDetail> records);
    List<ActualDetailDto> unconfirmedEntitiesToDtos(List<PaymentActualDetailUnconfirmed> records);
    
    PaymentActualDetail dtoToEntity(ActualDetailDto req);

    PaymentActualDetail postReqToEntity(ActualDetailPostReq addReq);
}
