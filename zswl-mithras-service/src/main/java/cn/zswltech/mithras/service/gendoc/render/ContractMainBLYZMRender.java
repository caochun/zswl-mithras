package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.lang.Assert;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.FactoringType;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author wangchuanhao
 * @date 2022/11/1
 * @description 融资保理合同（有追明）
 */
@Component
public class ContractMainBLYZMRender extends AbstractContractRender<ContractBaseInfo> {

    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        ProjectBizType projectBizType = Assert.notNull(ProjectBizType.of(contractBaseInfo.getBizType()), () -> MithrasException.newException("业务类型不能为空"));
        FactoringType factoringType = Assert.notNull(FactoringType.of(contractBaseInfo.getFactoringType()), () -> MithrasException.newException("保理类型不能为空"));
        Map<String, Object> renderMap = new HashMap<>();
        // 获取数据
        // 债权人列表、债务人列表、对应的客户列表、客户工商信息列表、客户地址信息、合同报价方案、收款信息
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        // 叶芳说债权人只考虑两个的情况
        List<ContractTenantry> creditorList = contractTenantryMap.values().stream().filter(t -> CreditorDebtorTypeEnum.CREDITOR.name().equals(t.getLesseeType())).limit(2).collect(Collectors.toList());
        List<ContractTenantry> debtorList = contractTenantryMap.values().stream().filter(t -> CreditorDebtorTypeEnum.DEBTOR.name().equals(t.getLesseeType())).collect(Collectors.toList());
        Set<Long> clientIdSet = contractTenantryMap.values().stream().map(ContractTenantry::getLesseeId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> creditorClientIdSet = creditorList.stream().map(ContractTenantry::getLesseeId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(clientIdSet);
        Map<Long, CorpCommerceInfoLib> corpCommerceInfoMap = businessDataRepository.getCorpCommerceMap(creditorClientIdSet);
        Map<Long, List<CorpAddressInfoLib>> corpAddressInfoMap = businessDataRepository.getCorpAddressMap(creditorClientIdSet);
        ContractFactoringPrice contractFactoringPrice = businessDataRepository.getContractFactoringPrice(contractBaseInfo.getId());
        List<ContractAccount> contractAccountList = businessDataRepository.listContractAccount(contractBaseInfo.getId(), ContractAccountUseEnum.BLSK);
        ContractAccount contractAccount = contractAccountList.stream().findFirst().orElse(new ContractAccount());
        // 获取主办数据
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfo.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfo.getProjSponsorUserId());

        // 第一债权人
        ContractTenantry firstCreditor = creditorList.get(0);
        Client firstCreditorClient = clientMap.getOrDefault(firstCreditor.getContactId(), new Client());
        CorpCommerceInfoLib firstCreditorCommerce = corpCommerceInfoMap.getOrDefault(firstCreditor.getContactId(), new CorpCommerceInfoLib());
        CorpContactInfoLib firstCreditorContact = Objects.isNull(firstCreditor.getContactId()) ? new CorpContactInfoLib() : this.getNewestContact(firstCreditor.getContactId());

        renderMap.put("contractCode", contractBaseInfo.getContractCode());
        renderMap.put("creditorName", firstCreditorClient.getClientName());
        renderMap.put("creditorRepresent", firstCreditorCommerce.getCorpRepresent());
        renderMap.put("creditorRegisterAddress", this.getCorpRegistryAddress(corpAddressInfoMap.get(firstCreditor.getLesseeId())));
        renderMap.put("creditorWorkAddress", this.getCorpWorkAddress(corpAddressInfoMap.get(firstCreditor.getLesseeId())));
        renderMap.put("creditorContact", firstCreditorContact.getName());
        renderMap.put("creditorContactMobile", firstCreditorContact.getTelephone());
        renderMap.put("creditorContactEmail", firstCreditorContact.getMail());
        renderMap.put("applyCreditAmount", this.toYuan(contractFactoringPrice.getContractAmount()));
        renderMap.put("creditAmountLoop", Objects.isNull(contractFactoringPrice.getCreditAmountLoop()) ? "" : Objects.equals(0, contractFactoringPrice.getCreditAmountLoop()) ? "不可循环" : "可循环");
        renderMap.put("debtorType", debtorList.size() == 0 ? "1" : "2");
        renderMap.put("debtorName", debtorList.size() == 0 ? "    " : debtorList.stream().map(d -> clientMap.get(d.getLesseeId()).getClientName()).collect(Collectors.joining("、")));
        renderMap.put("creditorBankName", contractAccount.getAccountName());
        renderMap.put("creditorAccountAddress", contractAccount.getAccountAddress());
        renderMap.put("creditorBankAccount", contractAccount.getAccountNum());
        // 填充主办信息
        renderMap.put("sponsorUserName", Optional.ofNullable(userVO).map(UserVO::getUserName).orElse(""));
        renderMap.put("sponsorUserMail", Optional.ofNullable(userVO).map(UserVO::getEmail).orElse(""));
        renderMap.put("sponsorUserPhone", Optional.ofNullable(sponsorPhone).orElse(""));

        InputStream inputStream;
        // 根据债权人数量使用不同模板文件（两个模板除了第二债权人参数外，其他参数一致）
        if (creditorList.size() == 1) {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-保理合同", "合同_保理合同_主合同_有追明_共同债权人.docx");
        } else {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-保理合同", "合同_保理合同_主合同_有追明_单一债权人.docx");

            // 填充第二债权人信息
            ContractTenantry secondCreditor = creditorList.get(1);
            Client secondCreditorClient = clientMap.getOrDefault(secondCreditor.getContactId(), new Client());
            CorpCommerceInfoLib secondCreditorCommerce = corpCommerceInfoMap.getOrDefault(secondCreditor.getContactId(), new CorpCommerceInfoLib());
            CorpContactInfoLib secondCreditorContact = Objects.isNull(secondCreditor.getContactId()) ? new CorpContactInfoLib() : this.getNewestContact(secondCreditor.getContactId());
            renderMap.put("secondCreditorName", secondCreditorClient.getClientName());
            renderMap.put("secondCreditorRepresent", secondCreditorCommerce.getCorpRepresent());
            renderMap.put("secondCreditorRegisterAddress", this.getCorpRegistryAddress(corpAddressInfoMap.get(secondCreditor.getLesseeId())));
            renderMap.put("secondCreditorWorkAddress", this.getCorpWorkAddress(corpAddressInfoMap.get(secondCreditor.getLesseeId())));
            renderMap.put("secondCreditorContact", secondCreditorContact.getName());
            renderMap.put("secondCreditorContactMobile", secondCreditorContact.getTelephone());
            renderMap.put("secondCreditorContactEmail", secondCreditorContact.getMail());
        }
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "1." + projectBizType.display + "合同-" + factoringType.display + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        return null;
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    private FileTemplate getFileTemplate(ContractBaseInfo contractBaseInfo) {
        // 债权人列表、债务人列表、对应的客户列表、客户工商信息列表、客户地址信息、合同报价方案、收款信息
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        // 叶芳说债权人只考虑两个的情况
        List<ContractTenantry> creditorList = contractTenantryMap.values().stream().filter(t -> CreditorDebtorTypeEnum.CREDITOR.name().equals(t.getLesseeType())).limit(2).collect(Collectors.toList());
        // 根据债权人数量使用不同模板文件（两个模板除了第二债权人参数外，其他参数一致）
        FileTemplate fileTemplate;
        if (creditorList.size() == 1) {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", "合同_保理合同_主合同_有追明_共同债权人.docx");
        } else {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", "合同_保理合同_主合同_有追明_单一债权人.docx");
        }
        return fileTemplate;
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
