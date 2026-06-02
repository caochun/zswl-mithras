package cn.zswltech.mithras.ftp.newftp.convert;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpMonthlyGuidanceTemplateConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyGuidanceDraft;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyGuidanceTemplateDraft;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author zhaozhengkang
 * @description ftp报价表
 * @date 2023-05-21
 */
@Mapper(componentModel = "spring")
public interface NewFtpMonthlyGuidanceConfigConverter {

    @Mapping(target = "templateId", source = "id")
    NewFtpMonthlyGuidanceDraft template2Entity(NewFtpMonthlyGuidanceTemplateConfig template);
}
