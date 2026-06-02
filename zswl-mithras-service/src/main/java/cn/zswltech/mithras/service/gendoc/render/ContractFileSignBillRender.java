package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractMortgage;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledge;
import cn.zswltech.mithras.service.service.contract.ContractGuarantorService;
import cn.zswltech.mithras.service.service.contract.ContractMortgageService;
import cn.zswltech.mithras.service.service.contract.ContractPledgeService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2025/6/4
 * @description 文件签收单
 */
@Component
public class ContractFileSignBillRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        return Collections.emptySet();
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        return false;
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        return "";
    }

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        // 查询合同下指定类型的文件记录
        List<MaterialsList> all = SpringUtil.getBean(MaterialsListService.class).list(
                BusinessModuleEnum.CONTRACT.name(),
                ListUtil.of(ContractTypeEnum.MAIN_CONTRACT.name(), ContractTypeEnum.GUARANTEE_CONTRACT.name(), ContractTypeEnum.MORTGAGE_CONTRACT.name(), ContractTypeEnum.PLEDGE_CONTRACT.name(), ContractTypeEnum.CONSULTING_CONTRACT.name()),
                Collections.singletonList(contractBaseInfo.getId())
        );
        Map<String, Object> renderMap = new HashMap<>();
        renderMap.put("projectName", contractBaseInfo.getProjName());
        int index = 1;
        // 分类统计数量
        long mainContractCount = all.stream().filter(e -> Objects.equals(e.getMaterialsType(), ContractTypeEnum.MAIN_CONTRACT.name())).count();
        long guarantorContractCount = all.stream().filter(e -> Objects.equals(e.getMaterialsType(), ContractTypeEnum.GUARANTEE_CONTRACT.name())).count();
        long consultingContractCount = all.stream().filter(e -> Objects.equals(e.getMaterialsType(), ContractTypeEnum.CONSULTING_CONTRACT.name())).count();
        long mortgageContractCount = all.stream().filter(e -> Objects.equals(e.getMaterialsType(), ContractTypeEnum.MORTGAGE_CONTRACT.name())).count();
        long pledgeContractCount = all.stream().filter(e -> Objects.equals(e.getMaterialsType(), ContractTypeEnum.PLEDGE_CONTRACT.name())).count();
        // 主合同
        if (mainContractCount > 0) {
            renderMap.put("contractText" + index, ContractTypeEnum.MAIN_CONTRACT.getDisplay());
//            renderMap.put("contractCount" + index, mainContractCount);
            renderMap.put("contractCodeList" + index, contractBaseInfo.getContractCode());
            index++;
        }
        // 咨询合同
        if (consultingContractCount > 0) {
            renderMap.put("contractText" + index, ContractTypeEnum.CONSULTING_CONTRACT.getDisplay());
//            renderMap.put("contractCount" + index, consultingContractCount);
            renderMap.put("contractCodeList" + index, contractBaseInfo.getConsultingContractCode());
            index++;
        }
        // 保证合同
        if (guarantorContractCount > 0) {
            renderMap.put("contractText" + index, ContractTypeEnum.GUARANTEE_CONTRACT.getDisplay());
//            renderMap.put("contractCount" + index, guarantorContractCount);
            // 查询担保措施
            List<ContractGuarantor> contractGuarantorList = SpringUtil.getBean(ContractGuarantorService.class).listByContractId(contractBaseInfo.getId());
            List<String> guarantorContractCodeList = contractGuarantorList.stream().map(ContractGuarantor::getGuarantorContractCode).filter(StrUtil::isNotBlank).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(guarantorContractCodeList)) {
                renderMap.put("contractCodeList" + index, StrUtil.join("\n", guarantorContractCodeList));
            }
            index++;
        }
        // 抵押合同
        if (mortgageContractCount > 0) {
            renderMap.put("contractText" + index, ContractTypeEnum.MORTGAGE_CONTRACT.getDisplay());
//            renderMap.put("contractCount" + index, mortgageContractCount);
            // 查询抵押措施
            List<ContractMortgage> contractMortgageList = SpringUtil.getBean(ContractMortgageService.class).listByContractId(contractBaseInfo.getId());
            List<String> mortgageContractCodeList = contractMortgageList.stream().map(ContractMortgage::getMortgageContractCode).filter(StrUtil::isNotBlank).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(mortgageContractCodeList)) {
                renderMap.put("contractCodeList" + index, StrUtil.join("\n", mortgageContractCodeList));
            }
            index++;
        }
        // 质押合同
        if (pledgeContractCount > 0) {
            renderMap.put("contractText" + index, ContractTypeEnum.PLEDGE_CONTRACT.getDisplay());
//            renderMap.put("contractCount" + index, pledgeContractCount);
            // 查询质押措施
            List<ContractPledge> contractPledgeList = SpringUtil.getBean(ContractPledgeService.class).listByContractId(contractBaseInfo.getId());
            List<String> pledgeContractCodeList = contractPledgeList.stream().map(ContractPledge::getPledgeContractCode).filter(StrUtil::isNotBlank).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(pledgeContractCodeList)) {
                renderMap.put("contractCodeList" + index, StrUtil.join("\n", pledgeContractCodeList));
            }
        }
        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-合同清单", "文件签收单.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "文件签收单-" + contractBaseInfo.getProjName() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }
}
