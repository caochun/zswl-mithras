package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.customer.domain.enums.BaseProfileTypeEnum;
import cn.zswltech.mithras.customer.domain.enums.BizClientType;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.client.CorpAddressInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.datacompare.CompareFactoryEnum.contractTenantry;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description 抵押合同
 */
@Component
public class ClientRender extends AbstractContractRender<Client> {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CorpAddressInfoMapper corpAddressInfoMapper;
    @Value("${company-address}")
    private String companyAddress;

    private final String templateType = "基础资料";

    @Override
    public String render(OutputStream outputStream, Client client) throws Exception {
        String contractName = client.getContractType();
        InputStream inputStream = null;
        String fileName = null;
        Map<String, Object> renderMap = new HashMap<>();
        if(BaseProfileTypeEnum.commitment.name().equals(contractName)){
            // 资料真实性承诺书
            fileName = BaseProfileTypeEnum.commitment.getDisplay();
            String bizClientType = client.getBizClientType();
            String ext = "other".equals(bizClientType) ? "（承租人）" : ClientType.CORPORATION.name().equals(client.getClientType()) ? "（担保法人）" : "（担保自然人）";
            inputStream = SpringUtil.getBean(FileTemplateService.class).getTemplate(templateType,fileName + ext + ".docx");

        }else if(BaseProfileTypeEnum.corporation.name().equals(contractName)){
            // 法定代表人证明书
            fileName = BaseProfileTypeEnum.corporation.getDisplay();
            inputStream = SpringUtil.getBean(FileTemplateService.class).getTemplate(templateType,fileName + ".docx");
        }else if(BaseProfileTypeEnum.honest.name().equals(contractName)){
            // 廉洁自律告知书
            fileName = BaseProfileTypeEnum.honest.getDisplay();
            inputStream = SpringUtil.getBean(FileTemplateService.class).getTemplate(templateType,fileName + ".docx");
//            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(client.getContractId());
//            List<CorpAddressInfo> corpAddressInfos = corpAddressInfoMapper.selectList(Wrappers.<CorpAddressInfo>lambdaQuery()
//                    .eq(CorpAddressInfo::getClientId, contractBaseInfo.getClientId()));
            if(companyAddress == null){
                throw new MithrasException("获取公司地址失败");
            }
//            Map<String, String> addressMap = corpAddressInfos.stream().collect(Collectors.toMap(CorpAddressInfo::getAddressType, CorpAddressInfo::getDetail, (m1, m2) -> m1));
//            renderMap.put(RenderParameterKeyHolder.COMPANY_ADDRESS, Optional.ofNullable(addressMap.get(CorpAddressType.WORK_ADDRESS.name())).orElse(addressMap.get(CorpAddressType.REGISTRY_ADDRESS.name())));
            renderMap.put(RenderParameterKeyHolder.COMPANY_ADDRESS, companyAddress);
        }

        renderMap.put(ClientRender.RenderParameterKeyHolder.CLIENT_NAME, client.getClientName());

        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return fileName+"-"+client.getClientName()+".docx";
    }

    @Override
    protected Set<Long> signClientIds(Client client) {
        return new HashSet<>(ListUtil.of(client.getId()));
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(Client client) {
        FileTemplate templateRecord = getFileTemplate(client);
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    private FileTemplate getFileTemplate(Client client) {
        String contractName = client.getContractType();
        Assert.notBlank(contractName, () -> MithrasException.newException("未知的模版类型"));
        BaseProfileTypeEnum baseProfileTypeEnum = BaseProfileTypeEnum.ofName(contractName);
        Assert.notNull(baseProfileTypeEnum, () -> MithrasException.newException("未知的模版类型"));
        String fileName = BaseProfileTypeEnum.commitment.getDisplay();
        return getBean(FileTemplateService.class).getTemplateRecord(templateType, fileName + ".docx");
    }

    @Override
    protected String customTemplateKey(Client client) {
        FileTemplate templateRecord = getFileTemplate(client);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    public static class RenderParameterKeyHolder{
        private static final String CLIENT_NAME = "clientName";
        private static final String COMPANY_ADDRESS = "companyAddress";

    }

}
