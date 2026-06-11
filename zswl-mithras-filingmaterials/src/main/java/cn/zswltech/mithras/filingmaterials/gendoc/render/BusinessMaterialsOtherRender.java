package cn.zswltech.mithras.filingmaterials.gendoc.render;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.filingmaterials.OtherFilingRenderDTO;
import cn.zswltech.mithras.filingmaterials.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.filingmaterials.enums.BusinessMaterialsDocNameEnum;
import cn.zswltech.mithras.contract.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author lllin
 * @date 2026/1/19
 * @description 资料归档-其他资料归档
 */
@Component
@Slf4j
public class BusinessMaterialsOtherRender extends AbstractBasicRender<HashMap> {
    @Resource
    Id2NameService id2NameService;

    @Override
    public String render(OutputStream outputStream, HashMap map) throws Exception {
        Assert.notNull(map.get(FilingMaterialsConstants.OBJECT), () -> MithrasException.newException("其他资料归档-基础资料模板填充失败，填充参数对象不存在"));
        Assert.notNull(map.get(FilingMaterialsConstants.TEMPLATE_TYPE), () -> MithrasException.newException("其他资料归档-基础资料模板填充失败，不存在模板"));
        OtherFilingRenderDTO otherFilingRenderDTO = (OtherFilingRenderDTO) map.get(FilingMaterialsConstants.OBJECT);
        Map<String, Object> renderMap = new HashMap<>();
        String belongName = id2NameService.sysUserId2NameSingle(otherFilingRenderDTO.getBelongUserId());
        renderMap.put(FilingMaterialsConstants.BELONG_NAME, belongName);
        renderMap.put(FilingMaterialsConstants.PROJ_NAME, otherFilingRenderDTO.getProjName());

        BusinessMaterialsDocNameEnum materialDocNameEnum = (BusinessMaterialsDocNameEnum) map.get(FilingMaterialsConstants.TEMPLATE_TYPE);
        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate(materialDocNameEnum.templateType, materialDocNameEnum.display);
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return materialDocNameEnum.getDocName() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

}
