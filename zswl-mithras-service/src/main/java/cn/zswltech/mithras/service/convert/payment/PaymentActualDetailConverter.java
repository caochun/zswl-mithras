package cn.zswltech.mithras.service.convert.payment;

import cn.zswltech.mithras.api.payment.writeoff.ActualDetailPostReq;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailDto;
import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetailUnconfirmed;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 10:18
 */
@Mapper(componentModel = "spring",uses = TypeConversionWorker.class)
public interface PaymentActualDetailConverter {

    ActualDetailDto entityToDto(PaymentActualDetail detail);
    ActualDetailDto unconfirmedEntityToDto(PaymentActualDetailUnconfirmed detail);
    List<ActualDetailDto> entitiesToDtos(List<PaymentActualDetail> records);
    List<ActualDetailDto> unconfirmedEntitiesToDtos(List<PaymentActualDetailUnconfirmed> records);
    
    PaymentActualDetail dtoToEntity(ActualDetailDto req);

    PaymentActualDetail postReqToEntity(ActualDetailPostReq addReq);
}
