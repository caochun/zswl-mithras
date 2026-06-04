package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.lang.Assert;

import cn.zswltech.mithras.dto.filingmaterials.AfterLeasingRenderDTO;
import cn.zswltech.mithras.filingmaterials.domain.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.filingmaterials.domain.enums.BusinessMaterialsDocNameEnum;
import cn.zswltech.mithras.service.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
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
 * @description 资料归档-租后资料归档
 */
@Component
@Slf4j
public class BusinessMaterialsAfterRender extends AbstractBasicRender<HashMap> {
    @Resource
    Id2NameService id2NameService;

    @Override
    public String render(OutputStream outputStream, HashMap map) throws Exception {
        Assert.notNull(map.get(FilingMaterialsConstants.OBJECT), () -> MithrasException.newException("租后资料归档-基础资料模板填充失败，填充参数对象不存在"));
        Assert.notNull(map.get(FilingMaterialsConstants.TEMPLATE_TYPE), () -> MithrasException.newException("租后资料归档-基础资料模板填充失败，不存在模板"));
        AfterLeasingRenderDTO afterLeasingRenderDTO = (AfterLeasingRenderDTO) map.get(FilingMaterialsConstants.OBJECT);
        Map<String, Object> renderMap = new HashMap<>();
        renderMap.put(FilingMaterialsConstants.CONTRACT_CODE, afterLeasingRenderDTO.getContractCode());
        String belongName = id2NameService.sysUserId2NameSingle(afterLeasingRenderDTO.getAssetUserId());
        renderMap.put(FilingMaterialsConstants.BELONG_NAME, belongName);
        //项目名称不填充，业务手动填写
//        renderMap.put(FilingMaterialsConstants.PROJ_NAME, afterLeasingRenderDTO.getProjName());
        renderMap.put(FilingMaterialsConstants.YEAR, afterLeasingRenderDTO.getYear());
        renderMap.put(FilingMaterialsConstants.PHASE, afterLeasingRenderDTO.getPhase());

        BusinessMaterialsDocNameEnum materialDocNameEnum = (BusinessMaterialsDocNameEnum) map.get(FilingMaterialsConstants.TEMPLATE_TYPE);
        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate(materialDocNameEnum.templateType, materialDocNameEnum.display);
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return afterLeasingRenderDTO.getPlanName() + "_" + afterLeasingRenderDTO.getCheckWay() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

}
