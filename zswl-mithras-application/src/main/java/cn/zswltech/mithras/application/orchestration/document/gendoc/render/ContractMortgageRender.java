package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.customer.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.core.ContractMortgageItemService;
import cn.zswltech.mithras.contract.core.ContractRentEstimateService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
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
 * @date 2022/8/22
 * @description 抵押合同
 */
@Component
public class ContractMortgageRender extends AbstractContractRender<ContractMortgage> {
    private static final String CORPORATION_TEMPLATE = "抵押人：%s（盖章）\n\n    法定代表人/负责人（签字/签章）/授权代表（签字）：";

    @Resource
    private ContractMortgageItemService contractMortgageItemService;
    @Resource
    private ContractRentEstimateService contractRentEstimateService;
    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;

    @Override
    public String render(OutputStream outputStream, ContractMortgage contractMortgage) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        Long contractId = contractMortgage.getContractId();
        ContractBaseInfo contractBaseInfo = this.getContractBaseInfo(contractId);
        renderMap.put(RenderParameterKeyHolder.MORTGAGE_CONTRACT_CODE, contractMortgage.getMortgageContractCode());
        ClientType clientType = ClientType.of(contractMortgage.getMortgageType());
        if (Objects.isNull(clientType)) {
            throw new MithrasException("未知的抵押人类型");
        }
        List<Long> clientIds = JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class);
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(clientIds);
        List<String> mortgagaNameList = clientMap.values().stream().map(Client::getClientName).collect(Collectors.toList());
        renderMap.put(RenderParameterKeyHolder.MORTGAGE_NAME, Joiner.on("、").join(mortgagaNameList));

        // 获取主办数据
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfo.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfo.getProjSponsorUserId());

        // 填充主办信息
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_NAME, Optional.ofNullable(userVO).map(UserVO::getUserName).orElse(""));
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_MAIL, Optional.ofNullable(userVO).map(UserVO::getEmail).orElse(""));
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_PHONE, Optional.ofNullable(sponsorPhone).orElse(""));

        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-抵押合同", "合同_抵押合同_动产_一般抵押.docx");
        List<String> mortgageTextList = new LinkedList<>();
        TableRenderData tableRenderData;
        if (Objects.equals(clientType.name(), ClientType.CORPORATION.name())) {
            tableRenderData = this.corporationTable(clientIds, clientMap);
        } else {
            tableRenderData = this.normalTable(clientIds, clientMap);
        }
        renderMap.put(RenderParameterKeyHolder.MORTGAGE_TABLE, tableRenderData);
        for (Long clientId : clientIds) {
            Client client = clientMap.get(clientId);
            mortgageTextList.add(String.format(CORPORATION_TEMPLATE, Optional.ofNullable(client).map(Client::getClientName).orElse("")));
        }
        renderMap.put(RenderParameterKeyHolder.MORTGAGE_TEXT_LIST, Joiner.on("\n\n\n\n\n\n\n\n    ").join(mortgageTextList));
//        ContractTenantry contractTenantry = this.getMainTenantry(contractId, this.getContractTenantryType(contractBaseInfo));
//        Client clientNotNull = businessDataRepository.getClientNotNull(Optional.ofNullable(contractTenantry).map(ContractTenantry::getContactId).orElse(null));
        renderMap.put(RenderParameterKeyHolder.MAIN_LESSEE_NAME, StrUtil.join("、", this.listContractTenantryName(contractId)));
        renderMap.put(RenderParameterKeyHolder.RELATED_CONTRACT_CODE, this.listRelatedContractCode(contractBaseInfo, contractMortgage.getRelatContracts()));
        // 查询概算租金表
        List<ContractRentEstimate> contractRentEstimateList = contractRentEstimateService.listByContractId(contractId, null);
        if (Objects.equals(contractBaseInfo.getBizType(), ProjectBizType.ZL.name()) || Objects.equals(contractBaseInfo.getBizType(), ProjectBizType.ZZ.name())) {
            // 租赁/转租赁合同需要校验概算租金表是否存在
            Assert.notEmpty(contractRentEstimateList, () -> MithrasException.newException("概算租金表不能为空"));
        }
        if (!CollectionUtils.isEmpty(contractRentEstimateList)) {
            // 计算租金之和
            long totalRent = 0L;
            for (ContractRentEstimate contractRentEstimate : contractRentEstimateList) {
                if (Objects.nonNull(contractRentEstimate.getRent())) {
                    totalRent = totalRent + contractRentEstimate.getRent();
                }
            }
            BigDecimal totalRentBigDecimal = NumberUtil.div(String.valueOf(totalRent), GlobalConstants.MONEY_MULTIPLE);
            renderMap.put(RenderParameterKeyHolder.TOTAL_RENT_ESTIMATE_CN, NumberChineseFormatter.format(totalRentBigDecimal.doubleValue(), true, true));
            renderMap.put(RenderParameterKeyHolder.TOTAL_RENT_ESTIMATE, this.toYuan(totalRent));
        }
        // 查询抵押物清单
        List<ContractMortgageItem> contractMortgageItemList = contractMortgageItemService.listByMortgageId(contractMortgage.getId());
        if (CollectionUtils.isEmpty(contractMortgageItemList)) {
            renderMap.put(RenderParameterKeyHolder.TOTAL_MORTGAGE_ITEM_ASSESSED_VALUE_CN, "                ");
            renderMap.put(RenderParameterKeyHolder.TOTAL_MORTGAGE_ITEM_ASSESSED_VALUE, "                ");
        } else {
            long totalAssessedValue = 0L;
            for (ContractMortgageItem contractMortgageItem : contractMortgageItemList) {
                if (Objects.nonNull(contractMortgageItem.getAssessedNetValue())) {
                    totalAssessedValue = totalAssessedValue + contractMortgageItem.getAssessedNetValue();
                }
            }
            BigDecimal totalAssessedValueBigDecimal = NumberUtil.div(String.valueOf(totalAssessedValue), GlobalConstants.MONEY_MULTIPLE);
            renderMap.put(RenderParameterKeyHolder.TOTAL_MORTGAGE_ITEM_ASSESSED_VALUE_CN, NumberChineseFormatter.format(totalAssessedValueBigDecimal.doubleValue(), true, true));
            renderMap.put(RenderParameterKeyHolder.TOTAL_MORTGAGE_ITEM_ASSESSED_VALUE, this.toYuan(totalAssessedValue));
        }
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return ContractTypeEnum.MORTGAGE_CONTRACT.getDisplay() + "-" + Joiner.on("、").join(mortgagaNameList) + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @Override
    protected Set<Long> signClientIds(ContractMortgage contractMortgage) {
        if (StrUtil.isBlank(contractMortgage.getMortgageIds())) {
            return null;
        }
        List<Long> ids = JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class);
        return new HashSet<>(ids);
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractMortgage contractMortgage) {
        return false;
    }

    @Override
    protected String customTemplateKey(ContractMortgage contractMortgage) {
        return "";
    }

    private static class RenderParameterKeyHolder {
        // 抵押合同编号
        public static final String MORTGAGE_CONTRACT_CODE = "mortgageContractCode";
        // 抵押人名称
        public static final String MORTGAGE_NAME = "mortgageName";
        // 主承租人名称
        public static final String MAIN_LESSEE_NAME = "mainLesseeName";
        // 主合同编号
        public static final String RELATED_CONTRACT_CODE = "relatedContractCode";
        // 概算租金表合计金额，大写
        public static final String TOTAL_RENT_ESTIMATE_CN = "totalRentEstimateCN";
        // 概算租金表合计金额，小写
        public static final String TOTAL_RENT_ESTIMATE = "totalRentEstimate";
        // 抵押物清单合计评估价值，大写
        public static final String TOTAL_MORTGAGE_ITEM_ASSESSED_VALUE_CN = "totalMortgageItemAssessedValueCN";
        // 抵押物清单合计评估价值，小写
        public static final String TOTAL_MORTGAGE_ITEM_ASSESSED_VALUE = "totalMortgageItemAssessedValue";
        // 项目主办名称
        public static final String SPONSOR_USER_NAME = "sponsorUserName";
        // 项目主办邮箱
        public static final String SPONSOR_USER_MAIL = "sponsorUserMail";
        // 项目主办手机
        public static final String SPONSOR_USER_PHONE = "sponsorUserPhone";
        // 抵押人表格
        public static final String MORTGAGE_TABLE = "mortgageTable";
        // 抵押人落款文本列表
        public static final String MORTGAGE_TEXT_LIST = "mortgageTextList";
    }

    private TableRenderData corporationTable(Collection<Long> clientIds, Map<Long, Client> clientMap) {
        List<RowRenderData> rowRenderDataList = new LinkedList<>();
        for (Long clientId : clientIds) {
            Client client = clientMap.get(clientId);
            CorpCommerceInfoLib corpCommerceInfoLib = businessDataRepository.getCorpCommerceInfo(client.getId());
            List<CorpAddressInfoLib> corpAddressInfoList = businessDataRepository.getCorpAddressInfo(client.getId());
            CorpContactInfoLib corpContactInfoLib = this.getMainContact(clientId);
            rowRenderDataList.add(Rows.of("抵押人名称：" + client.getClientName(), "").rowAtleastHeight(0.7).verticalCenter().create());
            rowRenderDataList.add(Rows.of("法定代表人：" + Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getCorpRepresent).orElse(""), "").rowAtleastHeight(0.7).verticalCenter().create());
            rowRenderDataList.add(Rows.of("联系地址：" + Optional.ofNullable(corpAddressInfoList).map(this::getCorpRegistryAddress).orElse(""), "").rowAtleastHeight(0.7).verticalCenter().create());
            rowRenderDataList.add(Rows.of("联系人：" + Optional.ofNullable(corpContactInfoLib).map(CorpContactInfoLib::getName).orElse(""), "联系人手机：" + Optional.ofNullable(corpContactInfoLib).map(CorpContactInfoLib::getTelephone).orElse("")).rowAtleastHeight(0.7).verticalCenter().create());
            rowRenderDataList.add(Rows.of("电子邮箱：" + Optional.ofNullable(corpContactInfoLib).map(CorpContactInfoLib::getMail).orElse(""), "固定电话：" + Optional.ofNullable(corpContactInfoLib).map(CorpContactInfoLib::getLandlineTelephone).orElse("\\")).rowAtleastHeight(0.7).verticalCenter().create());
        }
        RowRenderData[] rowRenderDataArray = new RowRenderData[rowRenderDataList.size()];
        rowRenderDataList.toArray(rowRenderDataArray);
        MergeCellRule.MergeCellRuleBuilder builder = MergeCellRule.builder();
        for (int i = 0; i < clientIds.size(); i++) {
            builder.map(MergeCellRule.Grid.of(i * 5,0), MergeCellRule.Grid.of(i * 5, 1));
            builder.map(MergeCellRule.Grid.of(i * 5 + 1,0), MergeCellRule.Grid.of(i * 5 + 1, 1));
            builder.map(MergeCellRule.Grid.of(i * 5 + 2,0), MergeCellRule.Grid.of(i * 5 + 2, 1));
        }
        return Tables.of(rowRenderDataArray).mergeRule(builder.build()).create();
    }

    private TableRenderData normalTable(Collection<Long> clientIds, Map<Long, Client> clientMap) {
        List<RowRenderData> rowRenderDataList = new LinkedList<>();
        for (Long clientId : clientIds) {
            Client client = clientMap.get(clientId);
            NormalBaseInfo normalBaseInfo = businessDataRepository.getNormalBaseInfo(clientId);
            rowRenderDataList.add(Rows.of("抵押人名称：" + client.getClientName(), "").rowAtleastHeight(0.7).verticalCenter().create());
            rowRenderDataList.add(Rows.of("联系地址：" + Optional.ofNullable(normalBaseInfo).map(NormalBaseInfo::getHomeAddress).orElse(""), "").rowAtleastHeight(0.7).verticalCenter().create());
            // 自然人联系人填写自己
            rowRenderDataList.add(Rows.of("联系人：" + Optional.ofNullable(client).map(Client::getClientName).orElse(""), "联系人手机：" + Optional.ofNullable(normalBaseInfo).map(NormalBaseInfo::getMobileNumber).orElse("")).rowAtleastHeight(0.7).verticalCenter().create());
            rowRenderDataList.add(Rows.of("电子邮箱：" + Optional.ofNullable(normalBaseInfo).map(NormalBaseInfo::getMail).orElse(""), "固定电话：\\").rowAtleastHeight(0.7).verticalCenter().create());
        }
        RowRenderData[] rowRenderDataArray = new RowRenderData[rowRenderDataList.size()];
        rowRenderDataList.toArray(rowRenderDataArray);
        MergeCellRule.MergeCellRuleBuilder builder = MergeCellRule.builder();
        for (int i = 0; i < clientIds.size(); i++) {
            builder.map(MergeCellRule.Grid.of(i * 4,0), MergeCellRule.Grid.of(i * 4, 1));
            builder.map(MergeCellRule.Grid.of(i * 4 + 1,0), MergeCellRule.Grid.of(i * 4 + 1, 1));
        }
        return Tables.of(rowRenderDataArray).mergeRule(builder.build()).create();
    }
}
