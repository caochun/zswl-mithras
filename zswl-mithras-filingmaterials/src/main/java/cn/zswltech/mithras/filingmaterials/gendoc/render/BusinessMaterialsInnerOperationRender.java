package cn.zswltech.mithras.filingmaterials.gendoc.render;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.filingmaterials.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.filingmaterials.application.port.FilingMaterialsContractInfoPort;
import cn.zswltech.mithras.filingmaterials.application.port.model.FilingMaterialsContractInfo;
import cn.zswltech.mithras.filingmaterials.enums.BusinessMaterialsDocNameEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author lllin
 * @date 2025/12/2
 * @description 资料归档-业务资料归档-内部操作资料
 */
@Component
@Slf4j
public class BusinessMaterialsInnerOperationRender {
    @Resource
    UserNameResolver userNameResolver;
    @Resource
    private FilingMaterialsContractInfoPort filingMaterialsContractInfoPort;

    public String render(OutputStream outputStream, HashMap map) throws Exception {
        Assert.notNull(map.get(FilingMaterialsConstants.CONTRACT_ID), () -> MithrasException.newException("资料归档-业务资料-基础资料模板填充失败，不存在合同ID"));
        Assert.notNull(map.get(FilingMaterialsConstants.TEMPLATE_TYPE), () -> MithrasException.newException("资料归档-业务资料-基础资料模板填充失败，不存在模板"));
        Long contractId = Long.valueOf(map.get(FilingMaterialsConstants.CONTRACT_ID).toString());
        BusinessMaterialsDocNameEnum templateType = (BusinessMaterialsDocNameEnum) map.get(FilingMaterialsConstants.TEMPLATE_TYPE);
        FilingMaterialsContractInfo contractInfo = filingMaterialsContractInfoPort.getById(contractId);
        Assert.notNull(contractInfo, () -> MithrasException.newException("资料归档-业务资料-基础资料模板填充失败,合同信息不存在"));

        Map<String, Object> renderMap = new HashMap<>();
        renderMap.put(FilingMaterialsConstants.CONTRACT_CODE, contractInfo.getContractCode());
        /*项目主办*/
        String belongName = userNameResolver.sysUserId2NameSingle(contractInfo.getProjSponsorUserId());
        if(map.containsKey(FilingMaterialsConstants.GENERATE_MANAGE_FLAG)
                && Objects.equals(YesOrNoNumberEnum.NO.getCode(),Integer.valueOf(map.get(FilingMaterialsConstants.GENERATE_MANAGE_FLAG).toString()))){
            belongName = "   ";
        }
        renderMap.put(FilingMaterialsConstants.BELONG_NAME, belongName);
        renderMap.put(FilingMaterialsConstants.PROJ_NAME, contractInfo.getProjName());
        /*动态生成表格额外处理*/
        List<SelectRSP> list = (List<SelectRSP>) map.get("innerTableList");
        list.removeIf(selectRSP -> Objects.equals(FilingMaterialsConstants.BASIC_INFORMATION, selectRSP.getValue()));
        List<Map<String, Object>> tableList = new ArrayList<>();
        Map<String, Object> listMap;
        for (int i = 0; i < list.size(); i++) {
            listMap = new HashMap<>();
            listMap.put("no", String.valueOf(i + 1));
            listMap.put("materialsType", list.get(i).getLabel());
            listMap.put("ori", "");
            listMap.put("copy", "");
            listMap.put("remark", "");
            tableList.add(listMap);
        }
        renderMap.put("li", tableList);
        BusinessMaterialsDocNameEnum materialDocNameEnum = (BusinessMaterialsDocNameEnum) map.get(FilingMaterialsConstants.TEMPLATE_TYPE);
        LoopRowTableRenderPolicy rowTableRenderPolicy = new LoopRowTableRenderPolicy();
        Configure configure = Configure.builder().bind("li", rowTableRenderPolicy).build();
        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate(materialDocNameEnum.templateType, materialDocNameEnum.display);
        XWPFTemplate template = XWPFTemplate.compile(inputStream, configure).render(renderMap);
        template.writeAndClose(outputStream);
        return templateType.docName + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

}
