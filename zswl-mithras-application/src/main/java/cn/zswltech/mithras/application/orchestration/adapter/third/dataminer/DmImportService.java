package cn.zswltech.mithras.application.orchestration.adapter.third.dataminer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.customer.enums.SubjectItemType;
import cn.zswltech.mithras.customer.enums.SubjectReportType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpSubjectItemMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpSubjectItem;
import cn.zswltech.mithras.third.dataminer.persistence.model.DmSubjectFieldMapping;
import cn.zswltech.mithras.third.dataminer.persistence.mapper.DmSubjectFieldMappingMapper;
import cn.zswltech.mithras.customer.application.client.CorpSubjectItemService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author dingqi
 * @date 2024/4/1
 * @description
 */
@Slf4j
@Service
public class DmImportService implements InitializingBean {
    private List<DmSubjectFieldMapping> capitalBalanceMapping;
    private List<DmSubjectFieldMapping> profitMapping;
    private List<DmSubjectFieldMapping> cashFlowMapping;

    @Transactional(rollbackFor = Throwable.class)
    public void importData() {
        ExcelReader excelReader = ExcelUtil.getReader(DmImportService.class.getResourceAsStream("/doc/DM财报数据录入名单.xlsx"));

        excelReader.setSheet("近三年年报_资产负债表");
        List<Map<String, Object>> dataList = excelReader.read(0, 2, 2445);
        this.doImport(dataList, SubjectItemType.CAPITAL_BALANCE);

        excelReader.setSheet("最近一季度报表_资产负债表");
        dataList = excelReader.read(0, 2, 111);
        this.doImport(dataList, SubjectItemType.CAPITAL_BALANCE);

        excelReader.setSheet("近三年年报_利润表");
        dataList = excelReader.read(0, 2, 1238);
        this.doImport(dataList, SubjectItemType.PROFIT);

        excelReader.setSheet("最近一季度报表_利润表");
        dataList = excelReader.read(0, 2, 112);
        this.doImport(dataList, SubjectItemType.PROFIT);

        excelReader.setSheet("近三年年报_现金流量表");
        dataList = excelReader.read(0, 2, 1244);
        this.doImport(dataList, SubjectItemType.CASH_FLOW);

        excelReader.setSheet("最近一季度报表_现金流量表");
        dataList = excelReader.read(0, 2, 110);
        this.doImport(dataList, SubjectItemType.CASH_FLOW);
    }

    private void doImport(List<Map<String, Object>> dataList, SubjectItemType subjectItemType) {
        if (CollectionUtil.isEmpty(dataList)) {
            return;
        }
        // 根据机构名称分组
        Map<String, List<Map<String, Object>>> orgMap = new HashMap<>(128);
        for (Map<String, Object> map : dataList) {
            String orgName = Optional.ofNullable(map.get("org_name")).map(Object::toString).orElse("");
            if (StrUtil.isBlank(orgName)) {
                continue;
            }
            List<Map<String, Object>> list = orgMap.get(orgName);
            if (Objects.isNull(list)) {
                list = new LinkedList<>();
                orgMap.put(orgName, list);
            }
            list.add(map);
        }
        // 处理机构
        for (Map.Entry<String, List<Map<String, Object>>> entry : orgMap.entrySet()) {
            String orgName = entry.getKey();
            // 找客户
            Client client = SpringUtil.getBean(ClientMapper.class).selectOne(Wrappers.<Client>lambdaQuery().eq(Client::getClientName, orgName).last(StringUtil.mysqlLimitOne()));
            if (Objects.isNull(client)) {
                log.info("没有找到名称为【{}】的客户信息", orgName);
                continue;
            }
            List<Map<String, Object>> list = entry.getValue();
            // 根据截止日期分组
            Map<DateTime, List<Map<String, Object>>> mapGroupByDate = new HashMap<>();
            for (Map<String, Object> m : list) {
                DateTime date = (DateTime) m.get("ed");
                if (Objects.isNull(date)) {
                    continue;
                }
                List<Map<String, Object>> listByDate = mapGroupByDate.get(date);
                if (Objects.isNull(listByDate)) {
                    listByDate = new LinkedList<>();
                    mapGroupByDate.put(date, listByDate);
                }
                listByDate.add(m);
            }
            // 根据日期和机构进行处理
            for (Map.Entry<DateTime, List<Map<String, Object>>> ent : mapGroupByDate.entrySet()) {
                List<Map<String, Object>> dataListByDate = ent.getValue();
                Map<String, Object> dataMap = this.choose(dataListByDate);
                if (Objects.isNull(dataMap)) {
                    log.info("【{}】没有找到符合导入条件的【{}】", orgName, subjectItemType.display());
                    continue;
                }
                // 判断是否存在对应报表期的报表
                int year = ent.getKey().getYear() + 1900;
                int month = ent.getKey().getMonth() + 1;
                int count = SpringUtil.getBean(CorpSubjectItemMapper.class).selectCount(
                        Wrappers.<CorpSubjectItem>lambdaQuery()
                        .eq(CorpSubjectItem::getClientId, client.getId())
                        .eq(CorpSubjectItem::getYear, year)
                        .eq(CorpSubjectItem::getQuarter, month)
                        .eq(CorpSubjectItem::getReportType, SubjectReportType.MERGED.name())
                        .eq(CorpSubjectItem::getSubjectType, subjectItemType.name())
                );
                if (count > 0) {
                    log.info("【{}】已经存在【{}】年【{}】月的【{}】", orgName, year, month, subjectItemType.display());
                    continue;
                }
                // 填充实体类
                List<CorpSubjectItem> toInsertList = this.build(client.getId(), year, month, dataMap, subjectItemType);
                if (CollectionUtil.isNotEmpty(toInsertList)) {
                    SpringUtil.getBean(CorpSubjectItemService.class).saveBatch(toInsertList);
                }
            }
        }
    }

    private Map<String, Object> choose(List<Map<String, Object>> dataList) {
        // 优先级：合并 > 合并调整 > 母公司 > 母公司调整
        // TODO 暴力写法，可以优化为排序权重进行排序
        for (Map<String, Object> map : dataList) {
            String s = Optional.ofNullable(map.get("statement_type_code")).map(Object::toString).orElse("");
            if (Objects.equals(s, "HB")) {
                return map;
            }
        }
        for (Map<String, Object> map : dataList) {
            String s = Optional.ofNullable(map.get("statement_type_code")).map(Object::toString).orElse("");
            if (Objects.equals(s, "HBTZ")) {
                return map;
            }
        }
        // 不要母公司和母公司调整
//        for (Map<String, Object> map : dataList) {
//            String s = Optional.ofNullable(map.get("statement_type_code")).map(Object::toString).orElse("");
//            if (Objects.equals(s, "MGS")) {
//                return map;
//            }
//        }
//        for (Map<String, Object> map : dataList) {
//            String s = Optional.ofNullable(map.get("statement_type_code")).map(Object::toString).orElse("");
//            if (Objects.equals(s, "MGSTZ")) {
//                return map;
//            }
//        }
        return null;
    }

    private List<CorpSubjectItem> build(Long clientId, int year, int month, Map<String, Object> columnMap, SubjectItemType subjectItemType) {
        List<DmSubjectFieldMapping> mappingList = null;
        switch (subjectItemType) {
            case CAPITAL_BALANCE: {
                mappingList = capitalBalanceMapping;
                break;
            }
            case PROFIT: {
                mappingList = profitMapping;
                break;
            }
            case CASH_FLOW: {
                mappingList = cashFlowMapping;
                break;
            }
        }
        if (CollectionUtil.isEmpty(mappingList)) {
            return null;
        }
        List<CorpSubjectItem> toInsertList = new LinkedList<>();
        for (DmSubjectFieldMapping mapping : mappingList) {
            CorpSubjectItem corpSubjectItem = new CorpSubjectItem();
            corpSubjectItem.setClientId(clientId);
            corpSubjectItem.setYear(year);
            corpSubjectItem.setQuarter(month);
            corpSubjectItem.setReportType(SubjectReportType.MERGED.name());
            corpSubjectItem.setSubjectType(subjectItemType.name());
            corpSubjectItem.setSubjectCode(mapping.getRzySubjectCode());
            corpSubjectItem.setSubjectName(mapping.getDmFieldComment());
            Object obj = columnMap.get(mapping.getDmFieldName());
            if (Objects.isNull(obj)) {
                corpSubjectItem.setSubjectValue(0L);
            } else {
                BigDecimal b;
                if (obj instanceof Number) {
                    b = new BigDecimal(obj.toString());
                    // 扩大10000倍，用毫厘存储
                    corpSubjectItem.setSubjectValue(b.multiply(BigDecimal.valueOf(10000L)).longValue());
                } else {
                    corpSubjectItem.setSubjectValue(0L);
                }
            }
            toInsertList.add(corpSubjectItem);
        }
        return toInsertList;
    }

    @Override
    public void afterPropertiesSet() {
        capitalBalanceMapping = SpringUtil.getBean(DmSubjectFieldMappingMapper.class).selectList(
                Wrappers.<DmSubjectFieldMapping>lambdaQuery()
                        .eq(DmSubjectFieldMapping::getRzySubjectType, SubjectItemType.CAPITAL_BALANCE.name())
                        .orderByAsc(DmSubjectFieldMapping::getId)
        );
        profitMapping = SpringUtil.getBean(DmSubjectFieldMappingMapper.class).selectList(
                Wrappers.<DmSubjectFieldMapping>lambdaQuery()
                        .eq(DmSubjectFieldMapping::getRzySubjectType, SubjectItemType.PROFIT.name())
                        .orderByAsc(DmSubjectFieldMapping::getId)
        );
        cashFlowMapping = SpringUtil.getBean(DmSubjectFieldMappingMapper.class).selectList(
                Wrappers.<DmSubjectFieldMapping>lambdaQuery()
                        .eq(DmSubjectFieldMapping::getRzySubjectType, SubjectItemType.CASH_FLOW.name())
                        .orderByAsc(DmSubjectFieldMapping::getId)
        );
    }
}
