package cn.zswltech.mithras.service.convert.payment;

import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentPlanedDetailLib;
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
