package cn.zswltech.mithras.service.convert.payment;

import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListRsp;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/17 15:20
 */
@Mapper(componentModel = "spring")
public interface PaymentMaterialsListConverter {

    @Mapping(target = "fileName", source = "filename")
    @Mapping(source = "createTime", target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    PaymentMaterialsListRsp entityToRsp(MaterialsList entity);

    List<PaymentMaterialsListRsp> entitiesToRsp(List<MaterialsList> entitis);
}
