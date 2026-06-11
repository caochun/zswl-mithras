package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.filingmaterials.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.filingmaterials.enums.BusinessMaterialsDocNameEnum;
import cn.zswltech.mithras.contract.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static cn.hutool.core.text.CharSequenceUtil.join;
import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author lllin
 * @date 2025/12/2
 * @description 资料归档-业务资料归档-基础信息资料
 */
@Component
@Slf4j
public class BusinessMaterialsRender extends AbstractBasicRender<HashMap> {
    @Resource
    Id2NameService id2NameService;

    @Override
    public String render(OutputStream outputStream, HashMap map) throws Exception {
        Assert.notNull(map.get(FilingMaterialsConstants.CONTRACT_ID), () -> MithrasException.newException("资料归档-业务资料-基础资料模板填充失败，不存在合同ID"));
        Assert.notNull(map.get(FilingMaterialsConstants.TEMPLATE_TYPE), () -> MithrasException.newException("资料归档-业务资料-基础资料模板填充失败，不存在模板"));
        Long contractId = Long.valueOf(map.get(FilingMaterialsConstants.CONTRACT_ID).toString());
        BusinessMaterialsDocNameEnum templateType = (BusinessMaterialsDocNameEnum) map.get(FilingMaterialsConstants.TEMPLATE_TYPE);
        ContractBaseInfo contractBaseInfo = getBean(ContractBaseInfoService.class).getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("资料归档-业务资料-基础资料模板填充失败,合同信息不存在"));

        Map<String, Object> renderMap = new HashMap<>();
        String docName = templateType.docName + GlobalConstants.OFFICE_WORD_SUFFIX;
        renderMap.put(FilingMaterialsConstants.CONTRACT_CODE, contractBaseInfo.getContractCode());
        /*项目主办*/
        String belongName = id2NameService.sysUserId2NameSingle(contractBaseInfo.getProjSponsorUserId());
        if(map.containsKey(FilingMaterialsConstants.GENERATE_MANAGE_FLAG)
                && Objects.equals(YesOrNoNumberEnum.NO.getCode(),Integer.valueOf(map.get(FilingMaterialsConstants.GENERATE_MANAGE_FLAG).toString()))){
            belongName = "   ";
        }
        renderMap.put(FilingMaterialsConstants.BELONG_NAME, belongName);
        renderMap.put(FilingMaterialsConstants.PROJ_NAME, contractBaseInfo.getProjName());
        /*交易结构额外处理*/
        if (StrUtil.equalsAny(templateType.name(), BusinessMaterialsDocNameEnum.BUSINESS_LESSEE_BASIC.name(),
                BusinessMaterialsDocNameEnum.BUSINESS_IND_GUARANTOR_BASIC.name(),
                BusinessMaterialsDocNameEnum.BUSINESS_ENT_GUARANTOR_BASIC.name())) {
            Object object = map.get(FilingMaterialsConstants.CLIENT_ID);
            Assert.notNull(object, () -> MithrasException.newException("资料归档-业务资料-基础资料模板填充失败，不存在客户ID"));
            Long clientId = Long.valueOf(object.toString());
            Client client = SpringUtil.getBean(ClientService.class).getById(clientId);
            Assert.notNull(client, () -> MithrasException.newException("合同下不存在该交易主体[" + clientId + "]"));
            renderMap.put(FilingMaterialsConstants.CLIENT_NAME, client.getClientName());
            docName = client.getClientName() +"-"+ templateType.docName + GlobalConstants.OFFICE_WORD_SUFFIX;
        }
        BusinessMaterialsDocNameEnum materialDocNameEnum = (BusinessMaterialsDocNameEnum) map.get(FilingMaterialsConstants.TEMPLATE_TYPE);
        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate(materialDocNameEnum.templateType, materialDocNameEnum.display);
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return docName;
    }

}
