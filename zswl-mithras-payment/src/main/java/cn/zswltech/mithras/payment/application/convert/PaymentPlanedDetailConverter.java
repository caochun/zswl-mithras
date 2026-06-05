package cn.zswltech.mithras.payment.application.convert;

import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentPlanedDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentPlanedDetailLib;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 10:18
 */
@Mapper(componentModel = "spring")
public interface PaymentPlanedDetailConverter {

    PaymentPlanedDetail dtoToEntity(PlanedDetailDto req);
    List<PaymentPlanedDetail> dtosToEntities(List<PlanedDetailDto> dtos);

    PlanedDetailDto entityToDto(PaymentPlanedDetail entity);
    List<PlanedDetailDto> entitiesToDtos(List<PaymentPlanedDetail> records);

    PaymentPlanedDetailLib entity2Lib(PaymentPlanedDetail f);

    PaymentPlanedDetail lib2Entity(PaymentPlanedDetailLib t);

    PlanedDetailDto lib2Dto(PaymentPlanedDetailLib f);
}
