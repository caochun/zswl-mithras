package cn.zswltech.mithras.service.service.newftp.convert;

import cn.zswltech.mithras.service.service.newftp.model.config.NewFtpQuarterlyBasePricingTemplateConfig;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpQuarterlyBasePricingDraft;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author zhaozhengkang
 * @description 季度指导基础定价
 * @date 2023-05-21
 */
@Mapper(componentModel = "spring")
public interface NewFtpQuarterlyBasePricingConfigConverter {

    @Mapping(target = "templateId", source = "id")
    NewFtpQuarterlyBasePricingDraft template2Entity(NewFtpQuarterlyBasePricingTemplateConfig template);
}
