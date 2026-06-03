package cn.zswltech.mithras.service.gendoc;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.customer.externaldata.environmentpenalty.infrastructure.model.EnvironmentPenalty;
import cn.zswltech.mithras.customer.externaldata.tianyancha.infrastructure.model.*;
import cn.zswltech.mithras.customer.externaldata.zhongdeng.infrastructure.model.ZhongdengInfo;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import cn.zswltech.mithras.customer.domain.enums.RelationshipType;
import cn.zswltech.mithras.customer.domain.enums.ShareholderType;
import cn.zswltech.mithras.customer.domain.enums.SubjectQuarterType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.data.Tables;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author dingqi
 * @date 2022/8/5
 * @description
 */
public abstract class AbstractProjReviewReportRender<T> extends AbstractBasicRender<T> {
    protected TableRenderData renderGuaranteesTable(Map<Long, Client> clientMap, Map<Long, CorpCommerceInfoLib> corpCommerceInfoMap, List<Long> guaranteeIdList) {
        int rows = guaranteeIdList.size() + 1;
        int columns = 5;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "保证人名称";
        tableData[0][1] = "企业类别";
        tableData[0][2] = "保证类型";
        tableData[0][3] = "关联关系";
        tableData[0][4] = "保证金额";
        for (int i = 0; i < guaranteeIdList.size(); i++) {
            int currentRow = i + 1;
            Client client = clientMap.get(guaranteeIdList.get(i));
            if (Objects.isNull(client)) {
                continue;
            }
            CorpCommerceInfo corpCommerceInfo = corpCommerceInfoMap.get(client.getId());
            if (Objects.isNull(corpCommerceInfo)) {
                continue;
            }
            tableData[currentRow][0] = StringUtils.isEmpty(client.getClientName()) ? "" : client.getClientName();
            String economyTypeName = businessDataRepository.getEconomyTypeNameFromLocalCache(corpCommerceInfo.getEconomyType());
            tableData[currentRow][1] = StringUtils.isEmpty(economyTypeName) ? "" : economyTypeName;
            tableData[currentRow][2] = "";
            tableData[currentRow][3] = "";
            tableData[currentRow][4] = "";
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-财务信息 表格
     *
     * @param corpSubjectItemList 某种确定类型的财务数据并且已经按照要求排序完成
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderFinancialTable(List<CorpSubjectItemListRSP> corpSubjectItemList) {
        LinkedHashMap<String, String> allSubjectMap = new LinkedHashMap<>(256);
        Map<Integer, Map<String, CorpSubjectItemListRSP.SubjectItem>> corpSubjectItemMap = new HashMap<>();
        List<Integer> years = new ArrayList<>(4);
        List<String> yearHeaders = new ArrayList<>(4);
        int loopTimes = Math.min(corpSubjectItemList.size(), 4);
        for (int i = 0; i < loopTimes; i++) {
            CorpSubjectItemListRSP rsp = corpSubjectItemList.get(i);
            years.add(rsp.getYear());
            SubjectQuarterType subjectQuarterType = SubjectQuarterType.getByValue(rsp.getQuarter());
            if (Objects.nonNull(subjectQuarterType)) {
                yearHeaders.add(rsp.getYear() + "-" + subjectQuarterType.display);
            } else {
                yearHeaders.add(rsp.getYear().toString());
            }
            corpSubjectItemMap.put(rsp.getYear(), new HashMap<>(256));
            if (!CollectionUtils.isEmpty(rsp.getItemList())) {
                for (CorpSubjectItemListRSP.SubjectItem subjectItem : rsp.getItemList()) {
                    allSubjectMap.put(subjectItem.getSubjectCode(), subjectItem.getSubjectName());
                    corpSubjectItemMap.get(rsp.getYear()).put(subjectItem.getSubjectCode(), subjectItem);
                }
            }
        }
        // subjectCode处理
        List<String> allSubjectCodeList = new ArrayList<>(allSubjectMap.keySet());
        // 排序
//        allSubjectCodeList.sort(String::compareToIgnoreCase);
        // 开始拼表
        int rows = allSubjectCodeList.size() + 1;
        int columns = years.size() + 1;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "项目";
        for (int i = 0; i < years.size(); i++) {
            tableData[0][i + 1] = yearHeaders.get(i);
        }
        for (int i = 0; i < allSubjectCodeList.size(); i++) {
            int currentRow = i + 1;
            String subjectCode = allSubjectCodeList.get(i);
            String subjectName = allSubjectMap.get(subjectCode);
            tableData[currentRow][0] = subjectName;
            for (int j = 0; j < years.size(); j++) {
                int currentColumn = j + 1;
                CorpSubjectItemListRSP.SubjectItem subjectItem = corpSubjectItemMap.get(years.get(j)).get(subjectCode);
                if (Objects.isNull(subjectItem)) {
                    tableData[currentRow][currentColumn] = "-";
                } else {
                    tableData[currentRow][currentColumn] = this.toWan(subjectItem.getSubjectValue());
                }
            }
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-公开信息-中登网 表格
     *
     * @param zhongdengInfoList 中登网信息列表
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderZhongDengInfoTable(List<ZhongdengInfo> zhongdengInfoList) {
        int rows = zhongdengInfoList.size() + 1;
        int columns = 6;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "交易业务类型";
        tableData[0][1] = "授信机构";
        tableData[0][2] = "金额（万元）";
        tableData[0][3] = "登记日期";
        tableData[0][4] = "登记到期日";
        tableData[0][5] = "期限（年）";
        for (int i = 0; i < zhongdengInfoList.size(); i++) {
            int currentRow = i + 1;
            ZhongdengInfo zhongdengInfo = zhongdengInfoList.get(i);
            tableData[currentRow][0] = StringUtils.isEmpty(zhongdengInfo.getTradeBusinessType()) ? "" : zhongdengInfo.getTradeBusinessType();
            tableData[currentRow][1] = StringUtils.isEmpty(zhongdengInfo.getCreditOrg()) ? "" : zhongdengInfo.getCreditOrg();
            tableData[currentRow][2] = Objects.nonNull(zhongdengInfo.getAmount()) ? this.toWan(zhongdengInfo.getAmount()) : "";
            tableData[currentRow][3] = Objects.nonNull(zhongdengInfo.getRegDate()) ? LocalDateTimeUtil.format(zhongdengInfo.getRegDate(), DatePattern.NORM_DATE_PATTERN) : "";
            tableData[currentRow][4] = Objects.nonNull(zhongdengInfo.getRegExpireDate()) ? LocalDateTimeUtil.format(zhongdengInfo.getRegExpireDate(), DatePattern.NORM_DATE_PATTERN) : "";
            tableData[currentRow][5] = Objects.nonNull(zhongdengInfo.getTerm()) ? zhongdengInfo.getTerm().toString() : "";
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-公开信息-失信人 表格
     *
     * @param tycDishonestList 失信人列表
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderTycDishonestTable(List<TycDishonest> tycDishonestList) {
        int rows = tycDishonestList.size() + 1;
        int columns = 7;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "立案日期";
        tableData[0][1] = "案号";
        tableData[0][2] = "执行依据文号";
        tableData[0][3] = "执行法院";
        tableData[0][4] = "失信行为";
        tableData[0][5] = "履行情况";
        tableData[0][6] = "发布日期";
        for (int i = 0; i < tycDishonestList.size(); i++) {
            int currentRow = i + 1;
            TycDishonest tycDishonest = tycDishonestList.get(i);
            tableData[currentRow][0] = Objects.nonNull(tycDishonest.getRegDate()) ? LocalDateTimeUtil.format(tycDishonest.getRegDate(), DatePattern.NORM_DATE_PATTERN) : "";
            tableData[currentRow][1] = StringUtils.isEmpty(tycDishonest.getCaseCode()) ? "" : tycDishonest.getCaseCode();
            tableData[currentRow][2] = StringUtils.isEmpty(tycDishonest.getGistId()) ? "" : tycDishonest.getGistId();
            tableData[currentRow][3] = StringUtils.isEmpty(tycDishonest.getGistUnit()) ? "" : tycDishonest.getGistUnit();
            tableData[currentRow][4] = StringUtils.isEmpty(tycDishonest.getDisruptTypeName()) ? "" : tycDishonest.getDisruptTypeName();
            tableData[currentRow][5] = StringUtils.isEmpty(tycDishonest.getPerformance()) ? "" : tycDishonest.getPerformance();
            tableData[currentRow][6] = Objects.nonNull(tycDishonest.getPublishDate()) ? LocalDateTimeUtil.format(tycDishonest.getPublishDate(), DatePattern.NORM_DATE_PATTERN) : "";
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-公开信息-被执行人 表格
     *
     * @param tycZhixingInfoList 被执行人列表
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderTycPerformedTable(List<TycZhixingInfo> tycZhixingInfoList) {
        int rows = tycZhixingInfoList.size() + 1;
        int columns = 4;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "立案日期";
        tableData[0][1] = "案号";
        tableData[0][2] = "执行标的（元）";
        tableData[0][3] = "执行法院";
        for (int i = 0; i < tycZhixingInfoList.size(); i++) {
            int currentRow = i + 1;
            TycZhixingInfo tycZhixingInfo = tycZhixingInfoList.get(i);
            tableData[currentRow][0] = Objects.nonNull(tycZhixingInfo.getCaseCreateTime()) ? LocalDateTimeUtil.format(tycZhixingInfo.getCaseCreateTime(), DatePattern.NORM_DATE_PATTERN) : "";
            tableData[currentRow][1] = StringUtils.isEmpty(tycZhixingInfo.getCaseCode()) ? "" : tycZhixingInfo.getCaseCode();
            tableData[currentRow][2] = StringUtils.isEmpty(tycZhixingInfo.getExecMoney()) ? "" : tycZhixingInfo.getExecMoney();
            tableData[currentRow][3] = StringUtils.isEmpty(tycZhixingInfo.getExecCourtName()) ? "" : tycZhixingInfo.getExecCourtName();
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-公开信息-限制消费令 表格
     *
     * @param tycConsumptionRestrictionList 限制消费令数据
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderTycConsumptionRestrictionTable(List<TycConsumptionRestriction> tycConsumptionRestrictionList) {
        int rows = tycConsumptionRestrictionList.size() + 1;
        int columns = 6;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "立案日期";
        tableData[0][1] = "案号";
        tableData[0][2] = "限制消费对象";
        tableData[0][3] = "关联限制消费对象";
        tableData[0][4] = "申请人信息";
        tableData[0][5] = "发布日期";
        for (int i = 0; i < tycConsumptionRestrictionList.size(); i++) {
            int currentRow = i + 1;
            TycConsumptionRestriction tycConsumptionRestriction = tycConsumptionRestrictionList.get(i);
            tableData[currentRow][0] = Objects.nonNull(tycConsumptionRestriction.getCaseCreateTime()) ? LocalDateTimeUtil.format(tycConsumptionRestriction.getCaseCreateTime(), DatePattern.NORM_DATE_PATTERN) : "";
            tableData[currentRow][1] = StringUtils.isEmpty(tycConsumptionRestriction.getCaseCode()) ? "" : tycConsumptionRestriction.getCaseCode();
            tableData[currentRow][2] = StringUtils.isEmpty(tycConsumptionRestriction.getXname()) ? "" : tycConsumptionRestriction.getXname();
            tableData[currentRow][3] = StringUtils.isEmpty(tycConsumptionRestriction.getQyinfoAlias()) ? "" : tycConsumptionRestriction.getQyinfoAlias();
            tableData[currentRow][4] = StringUtils.isEmpty(tycConsumptionRestriction.getApplicant()) ? "" : tycConsumptionRestriction.getApplicant();
            tableData[currentRow][5] = Objects.nonNull(tycConsumptionRestriction.getPublishDate()) ? LocalDateTimeUtil.format(tycConsumptionRestriction.getPublishDate(), DatePattern.NORM_DATE_PATTERN) : "";
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-公开信息-司法协助 表格
     *
     * @param tycJudicialList 司法协助数据
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderTycJudicialTable(List<TycJudicial> tycJudicialList) {
        int rows = tycJudicialList.size() + 1;
        int columns = 7;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "公示日期";
        tableData[0][1] = "执行通知文书号";
        tableData[0][2] = "被执行人";
        tableData[0][3] = "股权被执行的企业";
        tableData[0][4] = "股权数额（万元）";
        tableData[0][5] = "执行法院";
        tableData[0][6] = "类型";
        for (int i = 0; i < tycJudicialList.size(); i++) {
            int currentRow = i + 1;
            TycJudicial tycJudicial = tycJudicialList.get(i);
            tableData[currentRow][0] = StringUtils.isEmpty(tycJudicial.getPublicityDate()) ? "" : tycJudicial.getPublicityDate();
            tableData[currentRow][1] = StringUtils.isEmpty(tycJudicial.getExecuteNoticeNum()) ? "" : tycJudicial.getExecuteNoticeNum();
            tableData[currentRow][2] = StringUtils.isEmpty(tycJudicial.getExecutedPerson()) ? "" : tycJudicial.getExecutedPerson();
            tableData[currentRow][3] = StringUtils.isEmpty(tycJudicial.getStockExecutedCompany()) ? "" : tycJudicial.getStockExecutedCompany();
            tableData[currentRow][4] = StringUtils.isEmpty(tycJudicial.getEquityAmount()) ? "" : tycJudicial.getEquityAmount();
            tableData[currentRow][5] = StringUtils.isEmpty(tycJudicial.getExecutiveCourt()) ? "" : tycJudicial.getExecutiveCourt();
            tableData[currentRow][6] = StringUtils.isEmpty(tycJudicial.getTypeState()) ? "" : tycJudicial.getTypeState();
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-公开信息-经营异常 表格
     *
     * @param tycAbnormalList 经营异常数据
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderTycAbnormalTable(List<TycAbnormal> tycAbnormalList) {
        int rows = tycAbnormalList.size() + 1;
        int columns = 5;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "列入日期";
        tableData[0][1] = "列入原因";
        tableData[0][2] = "作出决定机关";
        tableData[0][3] = "移出日期";
        tableData[0][4] = "移出原因";
        for (int i = 0; i < tycAbnormalList.size(); i++) {
            int currentRow = i + 1;
            TycAbnormal tycAbnormal = tycAbnormalList.get(i);
            tableData[currentRow][0] = StringUtils.isEmpty(tycAbnormal.getPutDate()) ? "" : tycAbnormal.getPutDate();
            tableData[currentRow][1] = StringUtils.isEmpty(tycAbnormal.getPutReason()) ? "" : tycAbnormal.getPutReason();
            tableData[currentRow][2] = StringUtils.isEmpty(tycAbnormal.getPutDepartment()) ? "" : tycAbnormal.getPutDepartment();
            tableData[currentRow][3] = StringUtils.isEmpty(tycAbnormal.getRemoveDate()) ? "" : tycAbnormal.getRemoveDate();
            tableData[currentRow][4] = StringUtils.isEmpty(tycAbnormal.getRemoveReason()) ? "" : tycAbnormal.getRemoveReason();
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-公开信息-环境处罚 表格
     *
     * @param environmentPenaltyList 环境处罚数据
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderEnvironmentTable(List<EnvironmentPenalty> environmentPenaltyList) {
        int rows = environmentPenaltyList.size() + 1;
        int columns = 8;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "处罚日期";
        tableData[0][1] = "决定文书号";
        tableData[0][2] = "处罚事由";
        tableData[0][3] = "处罚结果";
        tableData[0][4] = "处罚金额（万元）";
        tableData[0][5] = "处罚单位";
        tableData[0][6] = "数据来源";
        tableData[0][7] = "执行情况";
        for (int i = 0; i < environmentPenaltyList.size(); i++) {
            int currentRow = i + 1;
            EnvironmentPenalty environmentPenalty = environmentPenaltyList.get(i);
            tableData[currentRow][0] = Objects.nonNull(environmentPenalty.getPenaltyTime()) ? LocalDateTimeUtil.format(environmentPenalty.getPenaltyTime(), DatePattern.NORM_DATE_PATTERN) : "";
            tableData[currentRow][1] = StringUtils.isEmpty(environmentPenalty.getPunishNumber()) ? "" : environmentPenalty.getPunishNumber();
            tableData[currentRow][2] = StringUtils.isEmpty(environmentPenalty.getReason()) ? "" : environmentPenalty.getReason();
            tableData[currentRow][3] = StringUtils.isEmpty(environmentPenalty.getResult()) ? "" : environmentPenalty.getResult();
            tableData[currentRow][4] = Objects.nonNull(environmentPenalty.getAmount()) ? this.toWan(environmentPenalty.getAmount()) : "";
            tableData[currentRow][5] = StringUtils.isEmpty(environmentPenalty.getDepartmentName()) ? "" : environmentPenalty.getDepartmentName();
            tableData[currentRow][6] = StringUtils.isEmpty(environmentPenalty.getSource()) ? "" : environmentPenalty.getSource();
            tableData[currentRow][7] = StringUtils.isEmpty(environmentPenalty.getInfo()) ? "" : environmentPenalty.getInfo();
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-公开信息-行政处罚 表格
     *
     * @param punishmentInfoList 行政处罚数据
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderTycPunishmentInfoTable(List<TycPunishmentInfo> punishmentInfoList) {
        int rows = punishmentInfoList.size() + 1;
        int columns = 6;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "处罚日期";
        tableData[0][1] = "决定文书号";
        tableData[0][2] = "处罚事由/违法行为类型";
        tableData[0][3] = "处罚结果/内容";
        tableData[0][4] = "处罚单位";
        tableData[0][5] = "数据来源";
        for (int i = 0; i < punishmentInfoList.size(); i++) {
            int currentRow = i + 1;
            TycPunishmentInfo tycPunishmentInfo = punishmentInfoList.get(i);
            tableData[currentRow][0] = StringUtils.isEmpty(tycPunishmentInfo.getDecisionDate()) ? "" : tycPunishmentInfo.getDecisionDate();
            tableData[currentRow][1] = StringUtils.isEmpty(tycPunishmentInfo.getPunishNumber()) ? "" : tycPunishmentInfo.getPunishNumber();
            tableData[currentRow][2] = StringUtils.isEmpty(tycPunishmentInfo.getReason()) ? "" : tycPunishmentInfo.getReason();
            tableData[currentRow][3] = StringUtils.isEmpty(tycPunishmentInfo.getContent()) ? "" : tycPunishmentInfo.getContent();
            tableData[currentRow][4] = StringUtils.isEmpty(tycPunishmentInfo.getDepartmentName()) ? "" : tycPunishmentInfo.getDepartmentName();
            tableData[currentRow][5] = StringUtils.isEmpty(tycPunishmentInfo.getSource()) ? "" : tycPunishmentInfo.getSource();
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-公开信息-股权出质 表格
     *
     * @param equityInfoList 股权出质数据
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderTycEquityInfoTable(List<TycEquityInfo> equityInfoList) {
        int rows = equityInfoList.size() + 1;
        int columns = 6;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "股权出质设立登记日期";
        tableData[0][1] = "登记编号";
        tableData[0][2] = "出质人";
        tableData[0][3] = "出质股权标的企业";
        tableData[0][4] = "出质股权数额";
        tableData[0][5] = "状态";
        for (int i = 0; i < equityInfoList.size(); i++) {
            int currentRow = i + 1;
            TycEquityInfo tycEquityInfo = equityInfoList.get(i);
            tableData[currentRow][0] = Objects.nonNull(tycEquityInfo.getRegDate()) ? LocalDateTimeUtil.format(tycEquityInfo.getRegDate(), DatePattern.NORM_DATE_PATTERN) : "";
            tableData[currentRow][1] = StringUtils.isEmpty(tycEquityInfo.getRegNumber()) ? "" : tycEquityInfo.getRegNumber();
            tableData[currentRow][2] = StringUtils.isEmpty(tycEquityInfo.getPledgee()) ? "" : tycEquityInfo.getPledgee();
            if (StringUtils.isEmpty(tycEquityInfo.getTargetCompanyJson())) {
                tableData[currentRow][3] = "";
            } else {
                JSONObject jsonObject = JSONUtil.parseObj(tycEquityInfo.getTargetCompanyJson());
                String name = jsonObject.getStr("name");
                tableData[currentRow][3] = StringUtils.isEmpty(name) ? "" : name;
            }
            tableData[currentRow][4] = StringUtils.isEmpty(tycEquityInfo.getEquityAmount()) ? "" : tycEquityInfo.getEquityAmount();
            tableData[currentRow][5] = StringUtils.isEmpty(tycEquityInfo.getState()) ? "" : tycEquityInfo.getState();
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-公开信息-动产质押 表格
     *
     * @param mortgageInfoList 动产质押数据
     * @return 渲染表格模型数据
     */
    protected TableRenderData renderTycMortgageInfoTable(List<TycMortgageInfo> mortgageInfoList) {
        int rows = mortgageInfoList.size() + 1;
        int columns = 8;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "登记日期";
        tableData[0][1] = "登记编号";
        tableData[0][2] = "抵押权人";
        tableData[0][3] = "所有权或使用归属权";
        tableData[0][4] = "被担保债权类型";
        tableData[0][5] = "被担保债券数额";
        tableData[0][6] = "债务人履行债务的期限";
        tableData[0][7] = "登记机关";
        for (int i = 0; i < mortgageInfoList.size(); i++) {
            int currentRow = i + 1;
            TycMortgageInfo tycMortgageInfo = mortgageInfoList.get(i);
            tableData[currentRow][0] = StringUtils.isEmpty(tycMortgageInfo.getRegDate()) ? "" : tycMortgageInfo.getRegDate();
            tableData[currentRow][1] = StringUtils.isEmpty(tycMortgageInfo.getRegNum()) ? "" : tycMortgageInfo.getRegNum();
            if (StringUtils.isEmpty(tycMortgageInfo.getPeopleInfoJson())) {
                tableData[currentRow][2] = "";
            } else {
                JSONArray jsonArray = JSONUtil.parseArray(tycMortgageInfo.getPeopleInfoJson());
                String peopleName = jsonArray.getJSONObject(0).getStr("peopleName");
                tableData[currentRow][2] = peopleName;
            }
            if (StringUtils.isEmpty(tycMortgageInfo.getPawnInfoJson())) {
                tableData[currentRow][3] = "";
            } else {
                JSONArray jsonArray = JSONUtil.parseArray(tycMortgageInfo.getPawnInfoJson());
                String ownership = jsonArray.getJSONObject(0).getStr("ownership");
                tableData[currentRow][3] = ownership;
            }
            tableData[currentRow][4] = StringUtils.isEmpty(tycMortgageInfo.getType()) ? "" : tycMortgageInfo.getType();
            tableData[currentRow][5] = StringUtils.isEmpty(tycMortgageInfo.getAmount()) ? "" : tycMortgageInfo.getAmount();
            tableData[currentRow][6] = StringUtils.isEmpty(tycMortgageInfo.getTerm()) ? "" : tycMortgageInfo.getTerm();
            tableData[currentRow][7] = StringUtils.isEmpty(tycMortgageInfo.getRegDepartment()) ? "" : tycMortgageInfo.getRegDepartment();
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-关联企业 表格
     *
     * @param corpRelatedEnterpriseList 关联企业数据
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderCorpRelatedEnterpriseTable(List<CorpRelatedEnterpriseLib> corpRelatedEnterpriseList) {
        int rows = corpRelatedEnterpriseList.size() + 1;
        int columns = 5;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "关联企业名称";
        tableData[0][1] = "成立年份";
        tableData[0][2] = "关联关系";
        tableData[0][3] = "持股比例（%）";
        tableData[0][4] = "存续状态";
        for (int i = 0; i < corpRelatedEnterpriseList.size(); i++) {
            int currentRow = i + 1;
            CorpRelatedEnterprise corpRelatedEnterprise = corpRelatedEnterpriseList.get(i);
            tableData[currentRow][0] = StringUtils.isEmpty(corpRelatedEnterprise.getEnterpriseName()) ? "" : corpRelatedEnterprise.getEnterpriseName();
            tableData[currentRow][1] = Objects.nonNull(corpRelatedEnterprise.getEstablishDate()) ? LocalDateTimeUtil.format(corpRelatedEnterprise.getEstablishDate(), DatePattern.NORM_DATE_PATTERN) : "";
            RelationshipType relationshipType = RelationshipType.of(corpRelatedEnterprise.getRelationship());
            tableData[currentRow][2] = Objects.nonNull(relationshipType) ? relationshipType.display : "";
            tableData[currentRow][3] = Objects.nonNull(corpRelatedEnterprise.getShareholdingRatio()) ? this.toYuan(corpRelatedEnterprise.getShareholdingRatio()) : "";
            tableData[currentRow][4] = businessDataRepository.getContinuousTypeNameFromLocalCache(corpRelatedEnterprise.getContinuousStatus());
        }
        return Tables.of(tableData).create();
    }

    /**
     * 客户信息-股东信息 表格
     *
     * @param corpShareholderInfoList 股东信息数据
     * @return 渲染表格数据模型
     */
    protected TableRenderData renderCorpShareholderTable(List<CorpShareholderInfoLib> corpShareholderInfoList) {
        int rows = corpShareholderInfoList.size() + 1;
        int columns = 7;
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "股东类型";
        tableData[0][1] = "股东名称";
        tableData[0][2] = "认缴金额（万元）";
        tableData[0][3] = "实缴金额（万元）";
        tableData[0][4] = "认缴出资方式";
        tableData[0][5] = "认缴出资比例（%）";
        tableData[0][6] = "是否实际控制人";
        for (int i = 0; i < corpShareholderInfoList.size(); i++) {
            int currentRow = i + 1;
            CorpShareholderInfo corpShareholderInfo = corpShareholderInfoList.get(i);
            ShareholderType shareholderType = ShareholderType.of(corpShareholderInfo.getShareholderType());
            tableData[currentRow][0] = Objects.nonNull(shareholderType) ? shareholderType.display : "";
            tableData[currentRow][1] = StringUtils.isEmpty(corpShareholderInfo.getShareholderName()) ? "" : corpShareholderInfo.getShareholderName();
            tableData[currentRow][2] = Objects.nonNull(corpShareholderInfo.getPaidTotal()) ? this.toWan(corpShareholderInfo.getPaidTotal()) : "";
            tableData[currentRow][3] = Objects.nonNull(corpShareholderInfo.getActualPaidTotal()) ? this.toWan(corpShareholderInfo.getActualPaidTotal()) : "";
            tableData[currentRow][4] = StringUtils.isEmpty(corpShareholderInfo.getCapitalWay()) ? "" : corpShareholderInfo.getCapitalWay();
            tableData[currentRow][5] = Objects.nonNull(corpShareholderInfo.getCapitalPercent()) ? this.toYuan(corpShareholderInfo.getCapitalPercent()) : "";
            tableData[currentRow][6] = Objects.equals(corpShareholderInfo.getRealController(), Boolean.TRUE) ? "是" : "否";
        }
        return Tables.of(tableData).create();
    }

    protected List<Long> transformIdList(JSONArray jsonArray) {
        List<Long> lesseeIdList = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            lesseeIdList.add(item.getLong("clientId"));
        }
        return lesseeIdList;
    }
}
