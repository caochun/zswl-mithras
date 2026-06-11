package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.customer.enums.CorpAddressType;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountPayeeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.document.mapper.model.FileTemplate;
import cn.zswltech.mithras.customer.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2024/10/16
 * @description 船舶_买卖合同_回租
 */
@Component
public class ContractMainShipTradeZLHZRender extends AbstractContractRender<ContractBaseInfo> {
    private static final String COLLECT_MONEY_ACCOUNT_TEXT_TEMPLATE = "户名：[%s]；开户行：[%s]；账号：[%s]";

    @Resource
    private ClientService clientService;
    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>(512);
        // 先查询一些基础公共参数
        ContractTenantry contractTenantry = contractTenantryService.getMain(contractBaseInfo.getId());
        if (Objects.isNull(contractTenantry)) {
            throw new MithrasException("主承租人不存在");
        }
        Client client = clientService.getById(contractTenantry.getLesseeId());
        if (Objects.isNull(client)) {
            throw new MithrasException("主承租人客户信息不存在");
        }
        renderMap.put("contractCode", contractBaseInfo.getContractCode().replace("租", "买"));
        renderMap.put("firstLesseeName", client.getClientName());
        // 填充主合同
        this.fillMain(renderMap, contractBaseInfo, contractTenantry);
        // 填充附件一：租赁船舶清单
        this.fillShipInfo(renderMap, contractBaseInfo);
        // 填充附件二：船舶交接书
        this.fillShipHandoverLetter(renderMap, contractTenantry);
        // 填充附件三：付款通知书
        this.fillPayNotice(renderMap, contractBaseInfo);
        // 渲染文档
//        InputStream inputStream = FileUtil.getInputStream("/Users/mockorz/Downloads/船舶_买卖合同_回租.docx");
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "船舶_买卖合同_回租.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "船舶买卖合同（回租）" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private void fillMain(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo, ContractTenantry firstTenantry) {
        renderMap.put("mainContractCode", contractBaseInfo.getContractCode());
        // 获取合同承租人信息
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        if (CollectionUtils.isEmpty(contractTenantryMap)) {
            throw new MithrasException("没有找到任何承租人信息");
        }
        // 批量查询相关信息
        List<Long> clientIds = Collections.singletonList(firstTenantry.getLesseeId());
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(clientIds);
        Map<Long, CorpCommerceInfoLib> corpCommerceInfoMap = businessDataRepository.getCorpCommerceMap(clientIds);
        Map<Long, List<CorpAddressInfoLib>> corpAddressInfoMap = businessDataRepository.getCorpAddressMap(clientIds);
        // 获取主办数据
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfo.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfo.getProjSponsorUserId());

        // 填充主办信息
        renderMap.put("sponsorUserName", Optional.ofNullable(userVO).map(UserVO::getUserName).orElse(""));
        renderMap.put("sponsorUserMail", Optional.ofNullable(userVO).map(UserVO::getEmail).orElse(""));
        renderMap.put("sponsorUserPhone", Optional.ofNullable(sponsorPhone).orElse(""));

        // 填充第一承租人信息
        Client firstClient = clientMap.get(firstTenantry.getLesseeId());
        renderMap.put("firstLesseeName", firstClient.getClientName());
        CorpCommerceInfo firstCorpCommerceInfo = corpCommerceInfoMap.get(firstClient.getId());
        if (Objects.nonNull(firstCorpCommerceInfo)) {
            renderMap.put("firstLesseeLegalRepresentative", firstCorpCommerceInfo.getCorpRepresent());
        }
        List<CorpAddressInfoLib> firstCorpAddressInfoList = corpAddressInfoMap.get(firstClient.getId());
        if (!CollectionUtils.isEmpty(firstCorpAddressInfoList)) {
            renderMap.put("firstLesseeAddress", this.getCorpRegistryAddress(firstCorpAddressInfoList));
        }
        if (Objects.nonNull(firstTenantry.getContactId())) {
            CorpContactInfoLib firstCorpContactInfo = this.getNewestContact(firstTenantry.getContactId());
            if (Objects.nonNull(firstCorpContactInfo)) {
                renderMap.put("firstLesseeContact", firstCorpContactInfo.getName());
                renderMap.put("firstLesseeContactMobile", firstCorpContactInfo.getTelephone());
                renderMap.put("firstLesseeEmail", firstCorpContactInfo.getMail());
                renderMap.put("firstLesseeContactTelephone", StrUtil.isBlank(firstCorpContactInfo.getLandlineTelephone()) ? "\\" : firstCorpContactInfo.getLandlineTelephone());
            }
        }
        // 查询报价方案
        ContractLeasePrice contractLeasePrice = businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        if (Objects.nonNull(contractLeasePrice)) {
            BigDecimal applyCreditAmount = NumberUtil.div(contractLeasePrice.getApplyCreditAmount().toString(), GlobalConstants.MONEY_MULTIPLE);
            renderMap.put("contractAmountCN", NumberChineseFormatter.format(applyCreditAmount.doubleValue(), true, true));
            renderMap.put("contractAmount", this.toYuan(contractLeasePrice.getApplyCreditAmount()));
        }
        // 收款账户
        List<ContractAccount> contractAccountList = businessDataRepository.listContractAccount(contractBaseInfo.getId(), ContractAccountUseEnum.ZLSK);
        // 区分甲方和乙方
        List<ContractAccount> yiContractAccountList = new LinkedList<>();
        for (ContractAccount contractAccount : contractAccountList) {
            if (Objects.equals(contractAccount.getPayeeType(), ContractAccountPayeeTypeEnum.YI.name())) {
                yiContractAccountList.add(contractAccount);
            }
        }
        // 填充乙方账户
        if (!CollectionUtils.isEmpty(yiContractAccountList)) {
            List<String> accounts = new ArrayList<>(contractAccountList.size());
            for (ContractAccount contractAccount : yiContractAccountList) {
                String accountText = String.format(COLLECT_MONEY_ACCOUNT_TEXT_TEMPLATE, contractAccount.getAccountName(), contractAccount.getAccountAddress(), contractAccount.getAccountNum());
                accounts.add(accountText);
            }
            renderMap.put("collectMoneyAccountList", Joiner.on("\n").join(accounts));
        } else {
            renderMap.put("collectMoneyAccountList", "户名：[        ]；开户行：[           ]；账号：[        ]");
        }
    }

    private void fillShipInfo(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {
        renderMap.put("shipInfoTable", this.renderShipTable(contractBaseInfo));
    }

    private void fillShipHandoverLetter(Map<String, Object> renderMap, ContractTenantry firstTenantry) {
        // 取承租人注册地址
        List<CorpAddressInfoLib> corpAddressInfoLibList = businessDataRepository.getCorpAddressInfo(firstTenantry.getLesseeId());
        if (CollectionUtil.isNotEmpty(corpAddressInfoLibList)) {
            Optional<CorpAddressInfoLib> optional = corpAddressInfoLibList.stream().filter(e -> Objects.equals(e.getAddressType(), CorpAddressType.REGISTRY_ADDRESS.name())).findFirst();
            if (optional.isPresent()) {
                CorpAddressInfoLib corpAddressInfoLib = optional.get();
                String province = businessDataRepository.getAddressNameFromLocalCache(corpAddressInfoLib.getProvince());
                String city = businessDataRepository.getAddressNameFromLocalCache(corpAddressInfoLib.getCity());
                String district = businessDataRepository.getAddressNameFromLocalCache(corpAddressInfoLib.getDistrict());
                renderMap.put("firstLesseeRegistryAddress", province + city + district + corpAddressInfoLib.getDetail());
            }
        }
    }

    private void fillPayNotice(Map<String, Object> renderMap, ContractBaseInfo contractBaseInfo) {

    }

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
        return contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toSet());
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "船舶_买卖合同_回租.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "船舶_买卖合同_回租.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
