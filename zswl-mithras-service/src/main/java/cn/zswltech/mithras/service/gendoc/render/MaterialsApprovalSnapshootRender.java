package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.dto.flow.search.ProcessHistoryRSP;
import cn.zswltech.mithras.service.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.filingmaterials.BusinessMaterialsDocNameEnum;
import cn.zswltech.mithras.service.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author lllin
 * @date 2025/12/2
 * @description 资料归档-业务资料归档-审批快照
 */
@Component
@Slf4j
public class MaterialsApprovalSnapshootRender extends AbstractBasicRender<HashMap> {
    @Resource
    Id2NameService id2NameService;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public String render(OutputStream outputStream, HashMap map) throws Exception {
        Assert.notNull(map.get(FilingMaterialsConstants.PROCESS_INSTANCE_ID), () -> MithrasException.newException("资料归档-业务资料-审批快照模板填充失败，不存在processInstanceId"));
        Assert.notNull(map.get(FilingMaterialsConstants.MODEL_NAME), () -> MithrasException.newException("资料归档-业务资料-审批快照模板填充失败，不存在modelName"));
        Assert.notNull(map.get(FilingMaterialsConstants.LIST), () -> MithrasException.newException("资料归档-业务资料-审批快照模板填充失败，不存在审批操作信息"));
        Map<String, Object> renderMap = new HashMap<>();
        String modelName = map.get(FilingMaterialsConstants.MODEL_NAME).toString();
        String processInstanceId = map.get(FilingMaterialsConstants.PROCESS_INSTANCE_ID).toString();
        /*项目主办*/
        renderMap.put(FilingMaterialsConstants.PROCESS_INSTANCE_ID, processInstanceId);
        renderMap.put(FilingMaterialsConstants.MODEL_NAME, modelName);
        List<ProcessHistoryRSP> list = (List<ProcessHistoryRSP>) map.get(FilingMaterialsConstants.LIST);
        List<Map<String, Object>> tableList = new ArrayList<>();
        Map<String, Object> listMap;
        for (ProcessHistoryRSP processHistoryRSP : list) {
            listMap = new HashMap<>();
            listMap.put("operateTime", Objects.isNull(processHistoryRSP.getOperateTime()) ? "" : processHistoryRSP.getOperateTime().format(dateTimeFormatter));
            listMap.put("taskNodeName", Objects.isNull(processHistoryRSP.getTaskNodeName()) ? "" : processHistoryRSP.getTaskNodeName());
            listMap.put("operatorName", Objects.isNull(processHistoryRSP.getOperatorName()) ? "" : processHistoryRSP.getOperatorName());
            listMap.put("typeName", Objects.isNull(processHistoryRSP.getTypeName()) ? "" : processHistoryRSP.getTypeName());
            listMap.put("message", Objects.isNull(processHistoryRSP.getMessage()) ? "" : processHistoryRSP.getMessage());
            tableList.add(listMap);
        }
        renderMap.put("table", tableList);
        LoopRowTableRenderPolicy rowTableRenderPolicy = new LoopRowTableRenderPolicy();
        Configure configure = Configure.builder().bind("table", rowTableRenderPolicy).build();
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate(BusinessMaterialsDocNameEnum.APPROVE_SNAPSHOT.templateType,
                BusinessMaterialsDocNameEnum.APPROVE_SNAPSHOT.display);
        XWPFTemplate template = XWPFTemplate.compile(inputStream, configure).render(renderMap);
        template.writeAndClose(outputStream);
        return modelName + "审批快照流程-" + processInstanceId + GlobalConstants.OFFICE_WORD_SUFFIX;
    }


}
