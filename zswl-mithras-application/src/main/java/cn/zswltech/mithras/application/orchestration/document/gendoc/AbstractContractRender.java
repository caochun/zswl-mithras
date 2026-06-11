package cn.zswltech.mithras.application.orchestration.document.gendoc;

import cn.zswltech.mithras.contract.gendoc.AbstractBasicRender;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.customer.enums.CorpAddressType;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.GuaranteeMethodEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.IContractSignInfo;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpContactInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.leaseholdproperty.application.contract.ContractLeaseItemService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.data.*;
import com.google.common.base.Joiner;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description
 */
public abstract class AbstractContractRender<T> extends AbstractBasicRender<T> implements IContractSignInfo<T> {
    private static final String GUARANTEE_TEXT_TEMPLATE = "%s.【%s】为本合同项下乙方履行义务的【%s】保证人。保证人对乙方在本合同项下的全部义务向甲方提供【%s】保证担保，具体内容详见甲方与【%s】签订的编号为【%s】的《保证合同》。";
    private static final String GUARANTEE_TEXT_TEMPLATE_DEFAULT = "1.【                  】为本合同项下乙方履行义务的【        】保证人。保证人对乙方在本合同项下的全部义务向甲方提供【        】保证担保，具体内容详见甲方与【                  】签订的编号为【                 】的《保证合同》。";

    @Resource
    protected ContractBaseInfoService contractBaseInfoService;
    @Resource
    protected ContractTenantryService contractTenantryService;
    @Resource
    protected CorpContactInfoLibMapper corpContactInfoLibMapper;

    @Override
    public Set<Long> signatories(T t) {
        Set<Long> signClientIds = this.signClientIds(t);
        if (Objects.isNull(signClientIds)) {
            signClientIds = new HashSet<>();
        }
        if (this.needSignByMyself()) {
            // 用0代表我方
            signClientIds.add(0L);
        }
        return signClientIds;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isShowFile(Object t) {
        if (t == null) {
            throw new IllegalArgumentException("Parameter is not of type T");
        }
        return this.customShowFile((T) t);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getTemplateKey(Object t) {
        if (t == null) {
            throw new IllegalArgumentException("Parameter is not of type T");
        }
        return this.customTemplateKey((T) t);
    }

    protected abstract Set<Long> signClientIds(T t);

    protected abstract boolean needSignByMyself();

    protected abstract boolean customShowFile(T t);

    protected abstract String customTemplateKey(T t);

    protected String getContractTenantryType(ContractBaseInfo contractBaseInfo) {
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType()) || ProjectBizType.ZZ.name().equals(contractBaseInfo.getBizType())) {
            return LesseeTypeEnum.MAIN_LESSSEE.name();
        } else {
            return CreditorDebtorTypeEnum.CREDITOR.name();
        }
    }

    protected ContractBaseInfo getContractBaseInfo(Long contractId) {
        return contractBaseInfoService.getById(contractId);
    }

    protected List<ContractTenantry> listContractTenantry(Long contractId) {
        LambdaQueryWrapper<ContractTenantry> query = Wrappers.lambdaQuery();
        query.eq(ContractTenantry::getContractId, contractId);
        return contractTenantryService.list(query);
    }

    protected ContractTenantry getMainTenantry(Long contractId, String type) {
        LambdaQueryWrapper<ContractTenantry> query = Wrappers.lambdaQuery();
        query.eq(ContractTenantry::getContractId, contractId);
        query.eq(ContractTenantry::getLesseeType, type);
        query.last(StringUtil.mysqlLimitOne());
        query.orderByAsc(ContractTenantry::getId);
        return contractTenantryService.getOne(query);
    }

    protected CorpContactInfoLib getMainContact(Long clientId) {
        List<CorpContactInfoLib> corpContactInfoList = businessDataRepository.getCorpContactInfo(clientId);
        // 找到主联系人
        for (CorpContactInfoLib corpContactInfo : corpContactInfoList) {
            if (corpContactInfo.getMain()) {
                return corpContactInfo;
            }
        }
        return null;
    }

    protected CorpContactInfoLib getNewestContact(Long contactInfoLibId) {
//        CorpContactInfoLib corpContactInfoLib = corpContactInfoLibMapper.selectById(contactInfoLibId);
//        Client client = businessDataRepository.getClient(corpContactInfoLib.getClientId());
//        Assert.notNull(client, () -> MithrasException.newException("客户不存在"));
//        // 尝试寻找最新版本的联系人
//        LambdaQueryWrapper<CorpContactInfoLib> query = Wrappers.lambdaQuery();
//        query.eq(CorpContactInfoLib::getOriginId, corpContactInfoLib.getOriginId());
//        query.eq(CorpContactInfoLib::getVersion, client.getNewestVersion());
//        query.orderByDesc(ClientBaseModel::getId);
//        query.last(StringUtil.mysqlLimitOne());
//        CorpContactInfoLib newest = corpContactInfoLibMapper.selectOne(query);
//        Assert.notNull(newest, () -> MithrasException.newException("联系人信息不存在"));
//        return newest;
        // 利用版本表id查找对应编辑区最新数据存在问题（编辑区可能已经被删除等），直接使用当时保存的版本
        return corpContactInfoLibMapper.selectById(contactInfoLibId);
    }

    protected String getCorpRegistryAddress(List<CorpAddressInfoLib> corpAddressInfoList) {
        if (CollectionUtils.isEmpty(corpAddressInfoList)) {
            return "";
        }
        for (CorpAddressInfo corpAddressInfo : corpAddressInfoList) {
            if (CorpAddressType.REGISTRY_ADDRESS.name().equals(corpAddressInfo.getAddressType())) {
                return this.appendAddress(corpAddressInfo);
            }
        }
        return "";
    }

    protected String getCorpWorkAddress(List<CorpAddressInfoLib> corpAddressInfoList) {
        if (CollectionUtils.isEmpty(corpAddressInfoList)) {
            return "";
        }
        for (CorpAddressInfo corpAddressInfo : corpAddressInfoList) {
            if (CorpAddressType.WORK_ADDRESS.name().equals(corpAddressInfo.getAddressType())) {
                return this.appendAddress(corpAddressInfo);
            }
        }
        return "";
    }

    protected String renderGuarantorText(List<ContractGuarantor> contractGuarantorList) {
        if (CollectionUtil.isEmpty(contractGuarantorList)) {
            return GUARANTEE_TEXT_TEMPLATE_DEFAULT;
        }
        List<Long> guarantorIds = new LinkedList<>();
        for (ContractGuarantor contractGuarantor : contractGuarantorList) {
            Assert.notBlank(contractGuarantor.getGuarantorContractCode(), () -> MithrasException.newException("保证合同编号不能为空"));
            guarantorIds.addAll(JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class));
        }
        // 查询担保人信息
        Map<Long, Client> guarantorMap = businessDataRepository.getClientMap(guarantorIds);
        // 查询保证合同编号
        Map<Long, String> guarantorContractCodeMap = contractGuarantorList.stream().collect(Collectors.toMap(ContractGuarantor::getId, ContractGuarantor::getGuarantorContractCode));
        // 拼接担保方式文案
        List<String> text = new ArrayList<>(contractGuarantorList.size());
        for (int i = 0; i < contractGuarantorList.size(); i++) {
            ContractGuarantor contractGuarantor = contractGuarantorList.get(i);
            String sequence = String.valueOf(i + 1);
            List<Long> ids = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
            List<String> guarantorNameList = new ArrayList<>(ids.size());
            for (Long id : ids) {
                Client guarantor = guarantorMap.get(id);
                if (Objects.isNull(guarantor)) {
                    throw new MithrasException("没有找到担保人id为【" + id + "】的相关客户信息");
                }
                guarantorNameList.add(guarantor.getClientName());
            }
            String names = Joiner.on("、").join(guarantorNameList);
            String code = guarantorContractCodeMap.get(contractGuarantor.getId());
            if (StringUtils.isEmpty(contractGuarantor.getGuaranteeMethod())) {
                throw new MithrasException("请填写担保方式");
            }
            GuaranteeMethodEnum guaranteeMethodEnum = GuaranteeMethodEnum.of(contractGuarantor.getGuaranteeMethod());
            String type = Optional.ofNullable(guaranteeMethodEnum).map(GuaranteeMethodEnum::display).orElse("         ");
            text.add(String.format(GUARANTEE_TEXT_TEMPLATE, sequence, names, type, type, names, code));
        }
        return Joiner.on("\n").join(text);
    }

    protected TableRenderData renderShipTable(ContractBaseInfo contractBaseInfo) {
        // 查询租赁物清单
        List<ContractLeaseItem> contractLeaseItemList = SpringUtil.getBean(ContractLeaseItemService.class).listByContractId(contractBaseInfo.getId());
        if (CollectionUtil.isEmpty(contractLeaseItemList)) {
            throw new MithrasException("租赁物清单为空");
        }
        List<RowRenderData> tableDataList = new ArrayList<>();
        MergeCellRule.MergeCellRuleBuilder mergeCellRuleBuilder = MergeCellRule.builder();
        for (int i = 0; i < contractLeaseItemList.size(); i++) {
            ContractLeaseItem contractLeaseItem = contractLeaseItemList.get(i);
            Map<String, Object> cellMap = JSONUtil.toBean(contractLeaseItem.getRowData(), Map.class);
            tableDataList.add(Rows.of(" 1", " 租赁船舶名称", Optional.ofNullable(cellMap.get("租赁船舶名称")).map(e -> " " + e.toString()).orElse("")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("船型")).map(e -> " 船型：" + e.toString()).orElse(" 船型：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("船籍港")).map(e -> " 船籍港：" + e.toString()).orElse(" 船籍港：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("船级")).map(e -> " 船级：" + e.toString()).orElse(" 船级：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("总长")).map(e -> " 总长：" + e.toString()).orElse(" 总长：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("型宽")).map(e -> " 型宽：" + e.toString()).orElse(" 型宽：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("型深")).map(e -> " 型深：" + e.toString()).orElse(" 型深：")).verticalCenter().create());
            Object identity;
            if (cellMap.get("船舶识别号") != null) {
                identity = cellMap.get("船舶识别号");
            } else if (cellMap.get("唯一识别号") != null) {
                identity = cellMap.get("唯一识别号");
            } else {
                identity = null;
            }
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(identity).map(e -> " 船舶识别号：" + identity.toString()).orElse(" 船舶识别号：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("呼号/登记号")).map(e -> " 呼号/登记号：" + e.toString()).orElse(" 呼号/登记号：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("总吨")).map(e -> " 总吨：" + e.toString()).orElse(" 总吨：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("净吨")).map(e -> " 净吨：" + e.toString()).orElse(" 净吨：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("吃水")).map(e -> " 吃水：" + e.toString()).orElse(" 吃水：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("建造厂家")).map(e -> " 建造厂家：" + e.toString()).orElse(" 建造厂家：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 2", " 租赁船舶描述", Optional.ofNullable(cellMap.get("建造日期")).map(e -> " 建造日期：" + e.toString()).orElse(" 建造日期：")).verticalCenter().create());
            tableDataList.add(Rows.of(" 3", " 租赁船舶原值（元）", Optional.ofNullable(cellMap.get("账面原值（元）")).map(e -> " " + e.toString()).orElse("")).verticalCenter().create());
            tableDataList.add(Rows.of(" 4", " 租赁船舶净值/评估价值（元）", Optional.ofNullable(cellMap.get("评估净值（元）")).map(e -> " " + e.toString()).orElse("")).verticalCenter().create());
            // 合并单元格
            mergeCellRuleBuilder.map(MergeCellRule.Grid.of(i * 16 + 1, 0), MergeCellRule.Grid.of(i * 16 + 13, 0));
            mergeCellRuleBuilder.map(MergeCellRule.Grid.of(i * 16 + 1, 1), MergeCellRule.Grid.of(i * 16 + 13, 1));
        }
        // 表格
        return Tables.of(tableDataList.toArray(new RowRenderData[0]))
                .mergeRule(mergeCellRuleBuilder.build())
                .width(14.0D, new double[]{1.0D, 5.0D, 8.0D})
                .create();
    }

    protected List<String> listRelatedContractCode(ContractBaseInfo contractBaseInfo, String relatedContractCodeJsonStr) {
        List<String> result = new LinkedList<>();
        result.add(contractBaseInfo.getContractCode());
        if (StrUtil.isNotBlank(relatedContractCodeJsonStr)) {
            List<String> list = JSONUtil.toList(relatedContractCodeJsonStr, String.class);
            if (CollectionUtil.isNotEmpty(list)) {
                result.addAll(list);
            }
        }
        return result;
    }

    protected List<String> listContractTenantryName(Long contractId) {
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractTenantryList)) {
            return Collections.emptyList();
        }
        return contractTenantryList.stream().map(ContractTenantry::getLesseeName).collect(Collectors.toList());
    }

}
