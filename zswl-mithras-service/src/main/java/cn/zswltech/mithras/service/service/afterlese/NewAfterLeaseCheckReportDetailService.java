package cn.zswltech.mithras.service.service.afterlese;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSRSP;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckReportTemplateVersionEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.service.enums.afterlease.SaveStatusEnum;
import cn.zswltech.mithras.service.enums.afterlease.SaveTypeEnum;
import cn.zswltech.mithras.service.mapper.afterlease.NewAfterLeaseCheckReportDetailMapper;
import cn.zswltech.mithras.service.mapper.model.afterlease.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.afterlease.NewAfterLeaseCheckReportDetailLibService;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.impl.NewAfterLeaseCheckReportDetailLibHandler;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/11/14
 * @description
 */
@Slf4j
@Service
public class NewAfterLeaseCheckReportDetailService extends ServiceImpl<NewAfterLeaseCheckReportDetailMapper, NewAfterLeaseCheckReportDetail> {
    public static final int TYPE_CONTENT = 1;
    public static final int TYPE_SUMMARY = 2;

    @Resource
    private AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;
    @Resource
    private NewAfterLeaseCheckReportFieldConfigService afterLeaseCheckReportFieldRemarkService;
    @Resource
    private NewAfterLeaseCheckReportDetailLibService afterLeaseCheckReportDetailLibService;
    @Resource
    private NewAfterLeaseCheckReportDetailLibHandler afterLeaseCheckReportDetailLibHandler;

    public NewAfterLeaseCheckReportDetail getOneByCheckPlanClientId(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportDetail> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportDetail::getCheckPlanClientId, checkPlanClientId);
        query.orderByDesc(NewAfterLeaseCheckReportDetail::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public boolean saveReportDetail(AfterLeaseCheckReportCSREQ req, int type) {
        NewAfterLeaseCheckReportMeta afterLeaseCheckReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(req.getCheckPlanClientId());
        if (Objects.isNull(afterLeaseCheckReportMeta)) {
            throw new MithrasException("没有找到对应租后检查报告的元数据信息");
        }
        Map<String, NewAfterLeaseCheckReportFieldConfig> fieldRemarkMap = afterLeaseCheckReportFieldRemarkService.getMapByReportType(afterLeaseCheckReportMeta.getReportType());
        NewAfterLeaseCheckReportDetail reportDetail = this.getOneByCheckPlanClientId(req.getCheckPlanClientId());
        if (Objects.isNull(reportDetail)) {
            throw new MithrasException("没有找到对应租后检查报告的详情信息");
        }
        List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList = req.getContentList().stream().map(e -> {
            NewAfterLeaseCheckReportFieldConfig fieldConfig = fieldRemarkMap.get(e.getFieldName());
            if (Objects.isNull(fieldConfig)) {
                log.error("没有找到对应的Field配置信息[{}]", JSONUtil.toJsonStr(e));
                throw new MithrasException("处理失败，存在未定义的字段:" + e.getFieldName());
            }
            NewAfterLeaseCheckReportDetail.FieldData fieldData = BeanUtil.copyProperties(e, NewAfterLeaseCheckReportDetail.FieldData.class);
            fieldData.setFieldRemark(fieldConfig.getFieldRemark());
            fieldData.setFieldType(fieldConfig.getFieldType());
            if (StrUtil.isNotBlank(fieldConfig.getFieldOption())) {
                fieldData.setFieldOption(JSONUtil.toList(fieldConfig.getFieldOption(), OptionData.class));
            }
            return fieldData;
        }).collect(Collectors.toList());
        // 按需保存
        if (type == TYPE_CONTENT) {
            if (CharSequenceUtil.equals(SaveTypeEnum.SAVE.name(), req.getSaveType())) {
                // 校验
                this.checkContent(afterLeaseCheckReportMeta, fieldDataList);
                reportDetail.setReportContentSaveStatus(SaveStatusEnum.SAVED.name());
            } else {
                reportDetail.setReportContentSaveStatus(SaveStatusEnum.NO_SAVE.name());
            }
            reportDetail.setReportContent(JSONUtil.toJsonStr(fieldDataList));
        } else if (type == TYPE_SUMMARY) {
            if (CharSequenceUtil.equals(SaveTypeEnum.SAVE.name(), req.getSaveType())) {
                this.checkSummary(afterLeaseCheckReportMeta, fieldDataList);
                reportDetail.setReportSummarySaveStatus(SaveStatusEnum.SAVED.name());
            } else {
                reportDetail.setReportSummarySaveStatus(SaveStatusEnum.NO_SAVE.name());
            }
            reportDetail.setReportSummary(JSONUtil.toJsonStr(fieldDataList));
        } else {
            throw new RuntimeException("未定义的数据保存类型");
        }
        return this.updateById(reportDetail);
    }

    public AfterLeaseCheckReportCSRSP getReportDetail(Long checkPlanClientId, String version, int type) {
        NewAfterLeaseCheckReportDetail detail = null;
        if (StrUtil.isBlank(version)) {
            detail = this.getOneByCheckPlanClientId(checkPlanClientId);
        } else {
            NewAfterLeaseCheckReportDetailLib detailLib = afterLeaseCheckReportDetailLibService.getOneByMainIdVersion(checkPlanClientId, version);
            if (Objects.nonNull(detailLib)) {
                detail = afterLeaseCheckReportDetailLibHandler.actualLib2Entity(detailLib);
            }
        }

        AfterLeaseCheckReportCSRSP rsp = new AfterLeaseCheckReportCSRSP();
        rsp.setCheckPlanClientId(checkPlanClientId);
        if (Objects.isNull(detail)) {
            rsp.setContentList(Collections.emptyList());
            return rsp;
        }
        String fieldDataStr;
        if (type == TYPE_CONTENT) {
            fieldDataStr = detail.getReportContent();
        } else if (type == TYPE_SUMMARY) {
            fieldDataStr = detail.getReportSummary();
        } else {
            throw new RuntimeException("未定义的数据保存类型");
        }
        if (StrUtil.isBlank(fieldDataStr)) {
            rsp.setContentList(Collections.emptyList());
        } else {
            List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList = JSONUtil.toList(fieldDataStr, NewAfterLeaseCheckReportDetail.FieldData.class);
            List<AfterLeaseCheckReportCSRSP.Content> contentList = fieldDataList.stream().map(e -> BeanUtil.copyProperties(e, AfterLeaseCheckReportCSRSP.Content.class)).collect(Collectors.toList());
            rsp.setContentList(contentList);
        }
        return rsp;
    }

    public void checkContent(NewAfterLeaseCheckReportMeta checkReportMeta, List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        String reportType = checkReportMeta.getReportType();
        String templateVersion = checkReportMeta.getReportTemplateVersion();
        // TODO 必填校验先写死，后面重新设计检查报告模板，做成可配置校验
        if (Objects.equals(AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name(), reportType)) {
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.NON_PUBLIC_V1.getTemplateType())) {
                this.checkContentNonPublicV1(contentList);
            }
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.NON_PUBLIC_V2.getTemplateType())) {
                this.checkContentNonPublicV2(contentList);
            }
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.NON_PUBLIC_V3.getTemplateType())) {
                this.checkContentNonPublicV3(contentList);
            }
        }
        if (Objects.equals(AfterLeaseCheckReportTypeEnum.PUBLIC.name(), reportType)) {
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.PUBLIC_V1.getTemplateType())) {
                this.checkContentPublicV1(contentList);
            }
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.PUBLIC_V2.getTemplateType())) {
                this.checkContentPublicV2(contentList);
            }
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.PUBLIC_V3.getTemplateType())) {
                this.checkContentPublicV3(contentList);
            }
        }
        if (Objects.equals(AfterLeaseCheckReportTypeEnum.LOW_RISK.name(), reportType)) {
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.LOW_RISK_V3.getTemplateType())) {
                this.checkContentLowV3(contentList);
            }
        }
        // 新增两个模版的校验
        if (Objects.equals(AfterLeaseCheckReportTypeEnum.BUS.name(), reportType)) {
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.BUS_V3.getTemplateType())) {
                this.checkContentBusV3(contentList);
            }
        }
        if (Objects.equals(AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name(), reportType)) {
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.STATE_OWNED_ASSET_V3.getTemplateType())) {
                this.checkContentStateOwnedAssetV3(contentList);
            }
        }
    }

    private void checkContentStateOwnedAssetV3(List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        if (CollUtil.isEmpty(contentList)) {
            throw MithrasException.newException("检查内容模块数据不能为空");
        }
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> map = contentList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, Function.identity()));
        // 承租人经营情况分析 文本域 必填
//        NewAfterLeaseCheckReportDetail.FieldData b_c_1_01 = map.get("SOA_C_1_01");
//        if (b_c_1_01 == null || CharSequenceUtil.isBlank(b_c_1_01.getFieldValue())) {
//            throw MithrasException.newException("检查内容模块数据不能为空");
//        }
//        StringBuilder sb = new StringBuilder();
//        // 承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况 选择："1": 是 "0": 否 情况说明必填
//        NewAfterLeaseCheckReportDetail.FieldData bC301 = map.get("SOA_C_3_01");
//        if (bC301 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC301.getFieldValue())
//                && CharSequenceUtil.isBlank(bC301.getSituationExplain())) {
//            sb.append("指标<承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况> 选择：是 时，情况说明必填\\n");
//        }
//        NewAfterLeaseCheckReportDetail.FieldData bC302 = map.get("SOA_C_3_02");
//        if (bC302 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC302.getFieldValue())
//                && CharSequenceUtil.isBlank(bC302.getSituationExplain())) {
//            sb.append("指标<承租人本期是否出现住所、通讯地址、联系人、联系方式变更> 选择：是 时，情况说明必填\\n");
//        }
//        NewAfterLeaseCheckReportDetail.FieldData bC303 = map.get("SOA_C_3_03");
//        if (bC303 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC303.getFieldValue())
//                && CharSequenceUtil.isBlank(bC303.getSituationExplain())) {
//            sb.append("指标<承租人主要职能定位及经营业务是否发生重大变化> 选择：是 时，情况说明必填\\n");
//        }
//        NewAfterLeaseCheckReportDetail.FieldData bC304 = map.get("SOA_C_3_04");
//        if (bC304 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC304.getFieldValue())
//                && CharSequenceUtil.isBlank(bC304.getSituationExplain())) {
//            sb.append("指标<承租人融资渠道是否通畅> 选择：是 时，情况说明必填\\n");
//        }
//        NewAfterLeaseCheckReportDetail.FieldData bC305 = map.get("SOA_C_3_05");
//        if (bC305 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC305.getFieldValue())
//                && CharSequenceUtil.isBlank(bC305.getSituationExplain())) {
//            sb.append("指标<是否存在被关闭或划转兼并的明确安排> 选择：是 时，情况说明必填\\n");
//        }
//
//        //承租人舆情信息-是否存在重大负面舆情 选择：是 时，情况说明必填
//        NewAfterLeaseCheckReportDetail.FieldData bC401 = map.get("SOA_C_9_01");
//        if (bC401 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC401.getFieldValue())) {
//            NewAfterLeaseCheckReportDetail.FieldData bC402 = map.get("SOA_C_9_02");
//            if (bC402 == null || CharSequenceUtil.isBlank(bC402.getFieldValue())) {
//                sb.append("指标<承租人舆情信息-是否存在重大负面舆情> 选择：是 时，情况说明必填\\n");
//            }
//        }
//        // 由于企业担保人和自然人担保人可能分别存在多个，所以需要分组校验
//        Map<Integer, List<NewAfterLeaseCheckReportDetail.FieldData>> stringListMap = contentList.stream()
//                .filter(fieldData -> fieldData.getFieldName().startsWith("SOA_C_6_"))
//                .collect(Collectors.groupingBy(NewAfterLeaseCheckReportDetail.FieldData::getModuleIndex));
//        if (CollUtil.isNotEmpty(stringListMap)) {
//            for (Map.Entry<Integer, List<NewAfterLeaseCheckReportDetail.FieldData>> entry : stringListMap.entrySet()) {
//                List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList = entry.getValue();
//                Map<String, NewAfterLeaseCheckReportDetail.FieldData> dataMap = fieldDataList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, Function.identity()));
//                // 法人担保人
//                NewAfterLeaseCheckReportDetail.FieldData bC604 = dataMap.get("SOA_C_6_04");
//                if (Objects.nonNull(bC604) && CharSequenceUtil.equals(bC604.getFieldValue(), YesOrNoNumberEnum.YES.getCode().toString())) {
//                    NewAfterLeaseCheckReportDetail.FieldData bC60201 = dataMap.get("SOA_C_6_02_01");
//                    if (Objects.isNull(bC60201) || CharSequenceUtil.isBlank(bC60201.getFieldValue())) {
//                        sb.append("指标<检查内容-担保人经营情况-担保人经营情况分析>内容不能为空\\");
//                    }
//                    NewAfterLeaseCheckReportDetail.FieldData bC60105 = dataMap.get("SOA_C_6_01_05");
//                    if (bC60105 != null && (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60105.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60105.getSituationExplain()))) {
//                        sb.append("指标<检查内容-担保人经营情况-是否存在被关闭或划转兼并的明确安排>选择 是 时，情况说明不能为空\\n");
//                    }
//
//                    NewAfterLeaseCheckReportDetail.FieldData bC60104 = dataMap.get("SOA_C_6_01_04");
//                    if (bC60104 != null && (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60104.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60104.getSituationExplain()))) {
//                        sb.append("指标<检查内容-担保人经营情况-担保人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况>选择 是 时，情况说明不能为空\\n");
//                    }
//
//                    NewAfterLeaseCheckReportDetail.FieldData bC60103 = dataMap.get("SOA_C_6_01_03");
//                    if (bC60103 != null && (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60103.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60103.getSituationExplain()))) {
//                        sb.append("指标<检查内容-担保人经营情况-担保人本期是否出现住所、通讯地址、联系人、联系方式变更>选择 是 时，情况说明不能为空\\n");
//                    }
//
//                    NewAfterLeaseCheckReportDetail.FieldData bC60102 = dataMap.get("SOA_C_6_01_02");
//                    if (bC60102 != null && (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60102.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60102.getSituationExplain()))) {
//                        sb.append("指标<检查内容-担保人经营情况-担保人主要职能定位及经营业务是否发生重大变化>选择 是 时，情况说明不能为空\\n");
//                    }
//
//                    NewAfterLeaseCheckReportDetail.FieldData bC60101 = dataMap.get("SOA_C_6_01_01");
//                    if (bC60101 != null && (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60101.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60101.getSituationExplain()))) {
//                        sb.append("指标<检查内容-担保人经营情况-担保人融资渠道是否通畅>选择 是 时，情况说明不能为空\\n");
//                    }
//                }
//                // 自然人担保人
//                NewAfterLeaseCheckReportDetail.FieldData bC60601 = dataMap.get("SOA_C_6_06_01");
//                if (bC60601 != null) {
//                    NewAfterLeaseCheckReportDetail.FieldData bC60802 = dataMap.get("SOA_C_6_08_02");
//                    if (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60802.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60802.getSituationExplain())) {
//                        sb.append("指标<检查内容-担保人经营情况-自然人担保人分析-是否存在相关负面消息>选择 是 时，指标<负面信息及对担保能力的影响>的情况说明不能为空\\n");
//                    }
//                }
//            }
//        }
//        // 租赁物情况-租赁物是否正常使用
//        String SOA_C_9_01 = this.getSpecificContent("SOA_C_9_01", map);
//        // 租赁物情况-承租人是否对租赁物计提折旧
//        String SOA_C_9_02 = this.getSpecificContent("SOA_C_9_02", map);
//        // 租赁物情况-租赁物是否再次销售/转让
//        String SOA_C_9_03 = this.getSpecificContent("SOA_C_9_03", map);
//        // 租赁物情况-租赁物是否转租
//        String SOA_C_9_04 = this.getSpecificContent("SOA_C_9_04", map);
//        // 租赁物情况-租赁物是否发生过重大停产停运、重大故障、重大维修情况
//        String SOA_C_9_05 = this.getSpecificContent("SOA_C_9_05", map);
//        // 租赁物情况-租赁物是否发生过升级换代、重大改造
//        String SOA_C_9_06 = this.getSpecificContent("SOA_C_9_06", map);
//        // 租赁物情况-租赁物是否抵押/质押/留置
//        String SOA_C_9_07 = this.getSpecificContent("SOA_C_9_07", map);
//        // 租赁物情况-租赁物是否用于投资入股、抵偿
//        String SOA_C_9_08 = this.getSpecificContent("SOA_C_9_08", map);
//        // 租赁物情况-租赁物是否用于诉讼担保/保全等
//        String SOA_C_9_09 = this.getSpecificContent("SOA_C_9_09", map);
//        // 租赁物情况-承租人是否对租赁物定期保养、维护
//        String SOA_C_9_10 = this.getSpecificContent("SOA_C_9_10", map);
//        // 租赁物情况-租赁物的位置是否被移动
//        String SOA_C_9_11 = this.getSpecificContent("SOA_C_9_11", map);
//        // 租赁物情况-是否存在其他损害租赁物所有权行为
//        String SOA_C_9_12 = this.getSpecificContent("SOA_C_9_12", map);
//        if (Objects.equals(SOA_C_9_01, YesOrNoNumberEnum.NO.getCode().toString())
//                || Objects.equals(SOA_C_9_02, YesOrNoNumberEnum.NO.getCode().toString())
//                || Objects.equals(SOA_C_9_03, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(SOA_C_9_04, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(SOA_C_9_05, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(SOA_C_9_06, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(SOA_C_9_07, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(SOA_C_9_08, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(SOA_C_9_09, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(SOA_C_9_10, YesOrNoNumberEnum.NO.getCode().toString())
//                || Objects.equals(SOA_C_9_11, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(SOA_C_9_12, YesOrNoNumberEnum.YES.getCode().toString())) {
//            // 租赁物情况-租赁物情况补充说明
//            String SOA_C_9_13 = this.getSpecificContent("SOA_C_9_13", map);
//            if (StrUtil.isBlank(SOA_C_9_13)) {
//                throw new MithrasException("<租赁物情况-租赁物情况补充说明>不能为空");
//            }
//        }
//        if (sb.length() > 0) {
//            sb.insert(0, "【检查内容】数据校验出现异常：\\n：");
//            throw new MithrasException(sb.toString());
//        }
    }

    private void checkContentBusV3(List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        if (CollUtil.isEmpty(contentList)) {
            throw MithrasException.newException("检查内容模块数据不能为空");
        }
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> map = contentList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, Function.identity()));
        // 承租人经营情况分析 文本域 必填
//        NewAfterLeaseCheckReportDetail.FieldData b_c_1_01 = map.get("B_C_1_01");
//        if (b_c_1_01 == null || CharSequenceUtil.isBlank(b_c_1_01.getFieldValue())) {
//            throw MithrasException.newException("检查内容模块数据不能为空");
//        }
//        StringBuilder sb = new StringBuilder();
//        // 承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况 选择："1": 是 "0": 否 情况说明必填
//        NewAfterLeaseCheckReportDetail.FieldData bC301 = map.get("B_C_3_01");
//        if (bC301 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC301.getFieldValue())
//                && CharSequenceUtil.isBlank(bC301.getSituationExplain())) {
//            sb.append("指标<承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况> 选择：是 时，情况说明必填\\n");
//        }
//        NewAfterLeaseCheckReportDetail.FieldData bC302 = map.get("B_C_3_02");
//        if (bC302 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC302.getFieldValue())
//                && CharSequenceUtil.isBlank(bC302.getSituationExplain())) {
//            sb.append("指标<承租人本期是否出现住所、通讯地址、联系人、联系方式变更> 选择：是 时，情况说明必填\\n");
//        }
//        NewAfterLeaseCheckReportDetail.FieldData bC303 = map.get("B_C_3_03");
//        if (bC303 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC303.getFieldValue())
//                && CharSequenceUtil.isBlank(bC303.getSituationExplain())) {
//            sb.append("指标<承租人主要职能定位及经营业务是否发生重大变化> 选择：是 时，情况说明必填\\n");
//        }
//        NewAfterLeaseCheckReportDetail.FieldData bC304 = map.get("B_C_3_04");
//        if (bC304 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC304.getFieldValue())
//                && CharSequenceUtil.isBlank(bC304.getSituationExplain())) {
//            sb.append("指标<承租人融资渠道是否通畅> 选择：是 时，情况说明必填\\n");
//        }
//        NewAfterLeaseCheckReportDetail.FieldData bC305 = map.get("B_C_3_05");
//        if (bC305 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC305.getFieldValue())
//                && CharSequenceUtil.isBlank(bC305.getSituationExplain())) {
//            sb.append("指标<是否存在被关闭或划转兼并的明确安排> 选择：是 时，情况说明必填\\n");
//        }
//
//        //承租人舆情信息-是否存在重大负面舆情 选择：是 时，情况说明必填
//        NewAfterLeaseCheckReportDetail.FieldData bC401 = map.get("B_C_9_01");
//        if (bC401 != null && YesOrNoNumberEnum.YES.getCode().toString().equals(bC401.getFieldValue())) {
//            NewAfterLeaseCheckReportDetail.FieldData bC402 = map.get("B_C_9_02");
//            if (bC402 == null || CharSequenceUtil.isBlank(bC402.getFieldValue())) {
//                sb.append("指标<承租人舆情信息-是否存在重大负面舆情> 选择：是 时，情况说明必填\\n");
//            }
//        }
//        // 由于企业担保人和自然人担保人可能分别存在多个，所以需要分组校验
//        Map<Integer, List<NewAfterLeaseCheckReportDetail.FieldData>> stringListMap = contentList.stream()
//                .filter(fieldData -> fieldData.getFieldName().startsWith("B_C_6_"))
//                .collect(Collectors.groupingBy(NewAfterLeaseCheckReportDetail.FieldData::getModuleIndex));
//        if (CollUtil.isNotEmpty(stringListMap)) {
//            for (Map.Entry<Integer, List<NewAfterLeaseCheckReportDetail.FieldData>> entry : stringListMap.entrySet()) {
//                List<NewAfterLeaseCheckReportDetail.FieldData> fieldDataList = entry.getValue();
//                Map<String, NewAfterLeaseCheckReportDetail.FieldData> dataMap = fieldDataList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, Function.identity()));
//                // 法人担保人
//                NewAfterLeaseCheckReportDetail.FieldData bC604 = dataMap.get("B_C_6_04");
//                if (Objects.nonNull(bC604) && CharSequenceUtil.equals(bC604.getFieldValue(), YesOrNoNumberEnum.YES.getCode().toString())) {
//                    NewAfterLeaseCheckReportDetail.FieldData bC60201 = dataMap.get("B_C_6_02_01");
//                    if (Objects.isNull(bC60201) || CharSequenceUtil.isBlank(bC60201.getFieldValue())) {
//                        sb.append("指标<检查内容-担保人经营情况-担保人经营情况分析>内容不能为空\\");
//                    }
//                    NewAfterLeaseCheckReportDetail.FieldData bC60105 = dataMap.get("B_C_6_01_05");
//                    if (bC60105 != null && (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60105.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60105.getSituationExplain()))) {
//                        sb.append("指标<检查内容-担保人经营情况-是否存在被关闭或划转兼并的明确安排>选择 是 时，情况说明不能为空\\n");
//                    }
//
//                    NewAfterLeaseCheckReportDetail.FieldData bC60104 = dataMap.get("B_C_6_01_04");
//                    if (bC60104 != null && (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60104.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60104.getSituationExplain()))) {
//                        sb.append("指标<检查内容-担保人经营情况-担保人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况>选择 是 时，情况说明不能为空\\n");
//                    }
//
//                    NewAfterLeaseCheckReportDetail.FieldData bC60103 = dataMap.get("B_C_6_01_03");
//                    if (bC60103 != null && (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60103.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60103.getSituationExplain()))) {
//                        sb.append("指标<检查内容-担保人经营情况-担保人本期是否出现住所、通讯地址、联系人、联系方式变更>选择 是 时，情况说明不能为空\\n");
//                    }
//
//                    NewAfterLeaseCheckReportDetail.FieldData bC60102 = dataMap.get("B_C_6_01_02");
//                    if (bC60102 != null && (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60102.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60102.getSituationExplain()))) {
//                        sb.append("指标<检查内容-担保人经营情况-担保人主要职能定位及经营业务是否发生重大变化>选择 是 时，情况说明不能为空\\n");
//                    }
//
//                    NewAfterLeaseCheckReportDetail.FieldData bC60101 = dataMap.get("B_C_6_01_01");
//                    if (bC60101 != null && (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60101.getFieldValue())
//                            && CharSequenceUtil.isBlank(bC60101.getSituationExplain()))) {
//                        sb.append("指标<检查内容-担保人经营情况-担保人融资渠道是否通畅>选择 是 时，情况说明不能为空\\n");
//                    }
//                }
//                // 自然人担保人
//                NewAfterLeaseCheckReportDetail.FieldData bC60601 = dataMap.get("B_C_6_06_01");
//                if (bC60601 != null) {
//                    NewAfterLeaseCheckReportDetail.FieldData bC60802 = dataMap.get("B_C_6_08_02");
//                    if (CharSequenceUtil.equals(YesOrNoNumberEnum.YES.getCode().toString(), bC60802.getFieldValue())
//                    && CharSequenceUtil.isBlank(bC60802.getSituationExplain())) {
//                        sb.append("指标<检查内容-担保人经营情况-自然人担保人分析-是否存在相关负面消息>选择 是 时，指标<负面信息及对担保能力的影响>的情况说明不能为空\\n");
//                    }
//                }
//            }
//        }
//        // 租赁物情况-租赁物是否正常使用
//        String B_C_9_01 = this.getSpecificContent("B_C_9_01", map);
//        // 租赁物情况-承租人是否对租赁物计提折旧
//        String B_C_9_02 = this.getSpecificContent("B_C_9_02", map);
//        // 租赁物情况-租赁物是否再次销售/转让
//        String B_C_9_03 = this.getSpecificContent("B_C_9_03", map);
//        // 租赁物情况-租赁物是否转租
//        String B_C_9_04 = this.getSpecificContent("B_C_9_04", map);
//        // 租赁物情况-租赁物是否发生过重大停产停运、重大故障、重大维修情况
//        String B_C_9_05 = this.getSpecificContent("B_C_9_05", map);
//        // 租赁物情况-租赁物是否发生过升级换代、重大改造
//        String B_C_9_06 = this.getSpecificContent("B_C_9_06", map);
//        // 租赁物情况-租赁物是否抵押/质押/留置
//        String B_C_9_07 = this.getSpecificContent("B_C_9_07", map);
//        // 租赁物情况-租赁物是否用于投资入股、抵偿
//        String B_C_9_08 = this.getSpecificContent("B_C_9_08", map);
//        // 租赁物情况-租赁物是否用于诉讼担保/保全等
//        String B_C_9_09 = this.getSpecificContent("B_C_9_09", map);
//        // 租赁物情况-承租人是否对租赁物定期保养、维护
//        String B_C_9_10 = this.getSpecificContent("B_C_9_10", map);
//        // 租赁物情况-租赁物的位置是否被移动
//        String B_C_9_11 = this.getSpecificContent("B_C_9_11", map);
//        // 租赁物情况-是否存在其他损害租赁物所有权行为
//        String B_C_9_12 = this.getSpecificContent("B_C_9_12", map);
//        if (Objects.equals(B_C_9_01, YesOrNoNumberEnum.NO.getCode().toString())
//                || Objects.equals(B_C_9_02, YesOrNoNumberEnum.NO.getCode().toString())
//                || Objects.equals(B_C_9_03, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(B_C_9_04, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(B_C_9_05, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(B_C_9_06, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(B_C_9_07, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(B_C_9_08, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(B_C_9_09, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(B_C_9_10, YesOrNoNumberEnum.NO.getCode().toString())
//                || Objects.equals(B_C_9_11, YesOrNoNumberEnum.YES.getCode().toString())
//                || Objects.equals(B_C_9_12, YesOrNoNumberEnum.YES.getCode().toString())) {
//            // 租赁物情况-租赁物情况补充说明
//            String B_C_9_13 = this.getSpecificContent("B_C_9_13", map);
//            if (StrUtil.isBlank(B_C_9_13)) {
//                throw new MithrasException("<租赁物情况-租赁物情况补充说明>不能为空");
//            }
//        }
//        if (sb.length() > 0) {
//            sb.insert(0, "【检查内容】数据校验出现异常：\\n：");
//            throw new MithrasException(sb.toString());
//        }
    }

    public void checkSummary(NewAfterLeaseCheckReportMeta checkReportMeta, List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        String reportType = checkReportMeta.getReportType();
        String templateVersion = checkReportMeta.getReportTemplateVersion();
        // TODO 必填校验先写死，后面重新设计检查报告模板，做成可配置校验
        if (Objects.equals(AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name(), reportType)) {
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.NON_PUBLIC_V1.getTemplateType())) {
                this.checkSummaryNonPublicV1(contentList);
            }
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.NON_PUBLIC_V2.getTemplateType())) {
                this.checkSummaryNonPublicV2(contentList);
            }
        }
        if (Objects.equals(AfterLeaseCheckReportTypeEnum.PUBLIC.name(), reportType)) {
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.PUBLIC_V1.getTemplateType())) {
                this.checkSummaryPublicV1(contentList);
            }
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.PUBLIC_V2.getTemplateType())) {
                this.checkSummaryPublicV2(contentList);
            }
        }
        // 新增两个模板
        if (Objects.equals(AfterLeaseCheckReportTypeEnum.BUS.name(), reportType)) {
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.BUS_V3.getTemplateType())) {
                this.checkSummaryBusV3(contentList);
            }
        }
        if (Objects.equals(AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name(), reportType)) {
            if (Objects.equals(templateVersion, AfterLeaseCheckReportTemplateVersionEnum.STATE_OWNED_ASSET_V3.getTemplateType())) {
                this.checkSummaryStateOwnedAssetV3(contentList);
            }
        }
    }

    private void checkSummaryStateOwnedAssetV3(List<NewAfterLeaseCheckReportDetail.FieldData> summaryList) {
        commonMustFillVerify(summaryList);
    }

    private void checkSummaryBusV3(List<NewAfterLeaseCheckReportDetail.FieldData> summaryList) {
        commonMustFillVerify(summaryList);
    }

    /**
     * 检查总结通用必填项校验
     *
     * @param summaryList 总结数据
     */
    private static void commonMustFillVerify(List<NewAfterLeaseCheckReportDetail.FieldData> summaryList) {
        if (CollUtil.isEmpty(summaryList)) {
            throw MithrasException.newException("【检查总结】数据内容不存在");
        }
        StringBuilder sb = new StringBuilder();
        for (NewAfterLeaseCheckReportDetail.FieldData data : summaryList) {
            if (CharSequenceUtil.isBlank(data.getFieldValue())) {
                sb.append("指标：【").append(data.getFieldRemark().replaceAll("@", "-"))
                        .append("】").append("不能为空!").append("\\n");
            }
        }
        if (sb.length() > 0) {
            sb.insert(0, "【检查总结】数据校验出现异常：\\n");
            throw MithrasException.newException(sb.toString());
        }
    }

    private String getSpecificContent(String fieldName, Map<String, NewAfterLeaseCheckReportDetail.FieldData> map) {
        return Optional.ofNullable(map.get(fieldName)).map(NewAfterLeaseCheckReportDetail.FieldData::getFieldName).orElse(null);
    }

    private void checkContentNonPublicV2(List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> map = contentList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e));
        // 有权机构审批意见落实情况-是否落实审批条件
        String NP_C_1_01_01 = this.getSpecificContent("NP_C_1_01_01", map);
        if (Objects.equals(NP_C_1_01_01, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 有权机构审批意见落实情况-若未落实，具体原因
            String NP_C_1_01_02 = this.getSpecificContent("NP_C_1_01_02", map);
            if (StrUtil.isBlank(NP_C_1_01_02)) {
                throw new MithrasException("<有权机构审批意见落实情况-若未落实，具体原因>不能为空");
            }
        }
        // 客户经营情况分析-主体资格分析-主体资格是否发生重大变化
        String NP_C_2_01_01 = this.getSpecificContent("NP_C_2_01_01", map);
        // 客户经营情况分析-主体资格分析-是否发生歇业、解散、停业整顿、被吊销营业执照或被撤销等情况
        String NP_C_2_01_02 = this.getSpecificContent("NP_C_2_01_02", map);
        // 客户经营情况分析-主体资格分析-注册资本、经营住所、经营范围、法定代表人是否发生变化
        String NP_C_2_01_03 = this.getSpecificContent("NP_C_2_01_03", map);
        // 客户经营情况分析-主体资格分析-承租人具备偿还租金意愿
        String NP_C_2_01_04 = this.getSpecificContent("NP_C_2_01_04", map);
        if (Objects.equals(NP_C_2_01_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_04, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 客户经营情况分析-主体资格分析-若发生重大变化，具体情况
            String NP_C_1_01_05 = this.getSpecificContent("NP_C_1_01_05", map);
            if (StrUtil.isBlank(NP_C_1_01_05)) {
                throw new MithrasException("<客户经营情况分析-主体资格分析-若发生重大变化，具体情况>不能为空");
            }
        }
        // 客户经营情况分析-行业分析
        String NP_C_2_02 = this.getSpecificContent("NP_C_2_02", map);
        if (StrUtil.isBlank(NP_C_2_02)) {
            throw new MithrasException("<客户经营情况分析-行业分析>不能为空");
        }
        // 客户经营情况分析-经营状况分析-具体分析
        String NP_C_2_04_02 = this.getSpecificContent("NP_C_2_04_02", map);
        if (StrUtil.isBlank(NP_C_2_04_02)) {
            throw new MithrasException("<客户经营情况分析-经营状况分析-具体分析>不能为空");
        }
        // 客户经营状态分析-其他情况搜集与分析-是否出现重大经济纠纷、诉讼、破产等情况
        String NP_C_2_05_01 = this.getSpecificContent("NP_C_2_05_01", map);
        if (Objects.equals(NP_C_2_05_01, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 客户经营状态分析-其他情况搜集与分析-具体分析
            String NP_C_2_05_02 = this.getSpecificContent("NP_C_2_05_02", map);
            if (StrUtil.isBlank(NP_C_2_05_02)) {
                throw new MithrasException("<客户经营状态分析-其他情况搜集与分析-具体分析>不能为空");
            }
        }
        // 客户经营状态分析-租赁资金使用情况
        String NP_C_2_07 = this.getSpecificContent("NP_C_2_07", map);
        if (StrUtil.isBlank(NP_C_2_07)) {
            throw new MithrasException("<客户经营分析状态-租赁资金使用情况>不能为空");
        }
        // 第二还款来源分析-担保人分析-主体资格是否发生重大变化
        String NP_C_3_01_01 = this.getSpecificContent("NP_C_3_01_01", map);
        // 第二还款来源分析-担保人分析-是否发生歇业、解散、停业整顿、被吊销营业执照或被撤销等情况
        String NP_C_3_01_02 = this.getSpecificContent("NP_C_3_01_02", map);
        // 第二还款来源分析-担保人分析-注册资本、经营住所、经营范围、法定代表人是否发生变化
        String NP_C_3_01_03 = this.getSpecificContent("NP_C_3_01_03", map);
        // 第二还款来源分析-担保人分析-担保人具备偿还租金意愿
        String NP_C_3_01_04 = this.getSpecificContent("NP_C_3_01_04", map);
        if (Objects.equals(NP_C_3_01_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_04, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 第二还款来源分析-担保人分析-若发生重大变化，具体情况
            String NP_C_3_01_05 = this.getSpecificContent("NP_C_3_01_05", map);
            if (StrUtil.isBlank(NP_C_3_01_05)) {
                throw new MithrasException("<第二还款来源分析-担保人分析-若发生重大变化，具体情况>不能为空");
            }
        }
        // 第二还款来源分析-担保人分析-担保人经营情况分析
        String NP_C_3_02_01 = this.getSpecificContent("NP_C_3_02_01", map);
        if (StrUtil.isBlank(NP_C_3_02_01)) {
            throw new MithrasException("<第二还款来源分析-担保人分析-担保人经营情况分析>不能为空");
        }
        // 第二还款来源分析-担保人分析-抵质押物分析
        String NP_C_3_02 = this.getSpecificContent("NP_C_3_02", map);
        if (StrUtil.isBlank(NP_C_3_02)) {
            throw new MithrasException("<第二还款来源分析-担保人分析-抵质押物分析>不能为空");
        }
        // 租赁物情况-租赁物是否正常使用
        String NP_C_4_01 = this.getSpecificContent("NP_C_4_01", map);
        // 租赁物情况-承租人是否对租赁物计提折旧
        String NP_C_4_02 = this.getSpecificContent("NP_C_4_02", map);
        // 租赁物情况-租赁物是否再次销售/转让
        String NP_C_4_03 = this.getSpecificContent("NP_C_4_03", map);
        // 租赁物情况-租赁物是否转租
        String NP_C_4_04 = this.getSpecificContent("NP_C_4_04", map);
        // 租赁物情况-租赁物是否发生过重大停产停运、重大故障、重大维修情况
        String NP_C_4_05 = this.getSpecificContent("NP_C_4_05", map);
        // 租赁物情况-租赁物是否发生过升级换代、重大改造
        String NP_C_4_06 = this.getSpecificContent("NP_C_4_06", map);
        // 租赁物情况-租赁物是否抵押/质押/留置
        String NP_C_4_07 = this.getSpecificContent("NP_C_4_07", map);
        // 租赁物情况-租赁物是否用于投资入股、抵偿
        String NP_C_4_08 = this.getSpecificContent("NP_C_4_08", map);
        // 租赁物情况-租赁物是否用于诉讼担保/保全等
        String NP_C_4_09 = this.getSpecificContent("NP_C_4_09", map);
        // 租赁物情况-承租人是否对租赁物定期保养、维护
        String NP_C_4_10 = this.getSpecificContent("NP_C_4_10", map);
        // 租赁物情况-租赁物的位置是否被移动
        String NP_C_4_11 = this.getSpecificContent("NP_C_4_11", map);
        // 租赁物情况-是否存在其他损害租赁物所有权行为
        String NP_C_4_12 = this.getSpecificContent("NP_C_4_12", map);
        if (Objects.equals(NP_C_4_01, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_02, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_04, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_05, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_06, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_07, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_08, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_09, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_10, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_11, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_12, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 租赁物情况-租赁物情况补充说明
            String NP_C_4_13 = this.getSpecificContent("NP_C_4_13", map);
            if (StrUtil.isBlank(NP_C_4_13)) {
                throw new MithrasException("<租赁物情况-租赁物情况补充说明>不能为空");
            }
        }
    }

    private void checkContentNonPublicV3(List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> map = contentList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e, (a, b) -> a));
        // 有权机构审批意见落实情况-是否落实审批条件
        String NP_C_1_01_01 = this.getSpecificContent("NP_C_1_01_01", map);
        if (Objects.equals(NP_C_1_01_01, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 有权机构审批意见落实情况-若未落实，具体原因
            String NP_C_1_01_02 = this.getSpecificContent("NP_C_1_01_02", map);
            if (StrUtil.isBlank(NP_C_1_01_02)) {
                throw new MithrasException("<有权机构审批意见落实情况-若未落实，具体原因>不能为空");
            }
        }
        // 客户经营情况分析-主体资格分析-主体资格是否发生重大变化
        String NP_C_2_01_01 = this.getSpecificContent("NP_C_2_01_01", map);
        // 客户经营情况分析-主体资格分析-是否发生歇业、解散、停业整顿、被吊销营业执照或被撤销等情况
        String NP_C_2_01_02 = this.getSpecificContent("NP_C_2_01_02", map);
        // 客户经营情况分析-主体资格分析-注册资本、经营住所、经营范围、法定代表人是否发生变化
        String NP_C_2_01_03 = this.getSpecificContent("NP_C_2_01_03", map);
        // 客户经营情况分析-主体资格分析-承租人具备偿还租金意愿
        String NP_C_2_01_04 = this.getSpecificContent("NP_C_2_01_04", map);
        if (Objects.equals(NP_C_2_01_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_04, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 客户经营情况分析-主体资格分析-若发生重大变化，具体情况
            String NP_C_1_01_05 = this.getSpecificContent("NP_C_1_01_05", map);
            if (StrUtil.isBlank(NP_C_1_01_05)) {
                throw new MithrasException("<客户经营情况分析-主体资格分析-若发生重大变化，具体情况>不能为空");
            }
        }

        // 客户经营情况分析-经营状况分析-具体分析
        String NP_C_2_04_02 = this.getSpecificContent("NP_C_2_04_02", map);
        if (StrUtil.isBlank(NP_C_2_04_02)) {
            throw new MithrasException("<客户经营情况分析-经营状况分析-具体分析>不能为空");
        }
        // 第二还款来源分析-担保人分析-主体资格是否发生重大变化
        String NP_C_3_01_01 = this.getSpecificContent("NP_C_3_01_01", map);
        // 第二还款来源分析-担保人分析-是否发生歇业、解散、停业整顿、被吊销营业执照或被撤销等情况
        String NP_C_3_01_02 = this.getSpecificContent("NP_C_3_01_02", map);
        // 第二还款来源分析-担保人分析-注册资本、经营住所、经营范围、法定代表人是否发生变化
        String NP_C_3_01_03 = this.getSpecificContent("NP_C_3_01_03", map);
        // 第二还款来源分析-担保人分析-担保人具备偿还租金意愿
        String NP_C_3_01_04 = this.getSpecificContent("NP_C_3_01_04", map);
        if (Objects.equals(NP_C_3_01_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_04, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 第二还款来源分析-担保人分析-若发生重大变化，具体情况
            String NP_C_3_01_05 = this.getSpecificContent("NP_C_3_01_05", map);
            if (StrUtil.isBlank(NP_C_3_01_05)) {
                throw new MithrasException("<第二还款来源分析-担保人分析-若发生重大变化，具体情况>不能为空");
            }
        }
        // 租赁物情况-租赁物是否正常使用
        String NP_C_4_01 = this.getSpecificContent("NP_C_4_01", map);
        // 租赁物情况-承租人是否对租赁物计提折旧
        String NP_C_4_02 = this.getSpecificContent("NP_C_4_02", map);
        // 租赁物情况-租赁物是否再次销售/转让
        String NP_C_4_03 = this.getSpecificContent("NP_C_4_03", map);
        // 租赁物情况-租赁物是否转租
        String NP_C_4_04 = this.getSpecificContent("NP_C_4_04", map);
        // 租赁物情况-租赁物是否发生过重大停产停运、重大故障、重大维修情况
        String NP_C_4_05 = this.getSpecificContent("NP_C_4_05", map);
        // 租赁物情况-租赁物是否发生过升级换代、重大改造
        String NP_C_4_06 = this.getSpecificContent("NP_C_4_06", map);
        // 租赁物情况-租赁物是否抵押/质押/留置
        String NP_C_4_07 = this.getSpecificContent("NP_C_4_07", map);
        // 租赁物情况-租赁物是否用于投资入股、抵偿
        String NP_C_4_08 = this.getSpecificContent("NP_C_4_08", map);
        // 租赁物情况-租赁物是否用于诉讼担保/保全等
        String NP_C_4_09 = this.getSpecificContent("NP_C_4_09", map);
        // 租赁物情况-承租人是否对租赁物定期保养、维护
        String NP_C_4_10 = this.getSpecificContent("NP_C_4_10", map);
        // 租赁物情况-租赁物的位置是否被移动
        String NP_C_4_11 = this.getSpecificContent("NP_C_4_11", map);
        // 租赁物情况-是否存在其他损害租赁物所有权行为
        String NP_C_4_12 = this.getSpecificContent("NP_C_4_12", map);
        if (Objects.equals(NP_C_4_01, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_02, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_04, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_05, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_06, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_07, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_08, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_09, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_10, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_11, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_12, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 租赁物情况-租赁物情况补充说明
            String NP_C_4_13 = this.getSpecificContent("NP_C_4_13", map);
            if (StrUtil.isBlank(NP_C_4_13)) {
                throw new MithrasException("<租赁物情况-租赁物情况补充说明>不能为空");
            }
        }
    }

    private void checkContentLowV3(List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> map = contentList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e, (a, b) -> a));
        // 有权机构审批意见落实情况-是否落实审批条件
        String NP_C_1_01_01 = this.getSpecificContent("NP_C_1_01_01", map);
        if (Objects.equals(NP_C_1_01_01, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 有权机构审批意见落实情况-若未落实，具体原因
            String NP_C_1_01_02 = this.getSpecificContent("NP_C_1_01_02", map);
            if (StrUtil.isBlank(NP_C_1_01_02)) {
                throw new MithrasException("<有权机构审批意见落实情况-若未落实，具体原因>不能为空");
            }
        }
        // 客户经营情况分析-主体资格分析-主体资格是否发生重大变化
        String NP_C_2_01_01 = this.getSpecificContent("NP_C_2_01_01", map);
        // 客户经营情况分析-主体资格分析-是否发生歇业、解散、停业整顿、被吊销营业执照或被撤销等情况
        String NP_C_2_01_02 = this.getSpecificContent("NP_C_2_01_02", map);
        // 客户经营情况分析-主体资格分析-注册资本、经营住所、经营范围、法定代表人是否发生变化
        String NP_C_2_01_03 = this.getSpecificContent("NP_C_2_01_03", map);
        // 客户经营情况分析-主体资格分析-承租人具备偿还租金意愿
        String NP_C_2_01_04 = this.getSpecificContent("NP_C_2_01_04", map);
        if (Objects.equals(NP_C_2_01_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_04, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 客户经营情况分析-主体资格分析-若发生重大变化，具体情况
            String NP_C_1_01_05 = this.getSpecificContent("NP_C_1_01_05", map);
            if (StrUtil.isBlank(NP_C_1_01_05)) {
                throw new MithrasException("<客户经营情况分析-主体资格分析-若发生重大变化，具体情况>不能为空");
            }
        }

        // 客户经营情况分析-经营状况分析-具体分析
        String NP_C_2_04_02 = this.getSpecificContent("NP_C_2_04_02", map);
        if (StrUtil.isBlank(NP_C_2_04_02)) {
            throw new MithrasException("<客户经营情况分析-经营状况分析-具体分析>不能为空");
        }
        // 第二还款来源分析-担保人分析-主体资格是否发生重大变化
        String NP_C_3_01_01 = this.getSpecificContent("NP_C_3_01_01", map);
        // 第二还款来源分析-担保人分析-是否发生歇业、解散、停业整顿、被吊销营业执照或被撤销等情况
        String NP_C_3_01_02 = this.getSpecificContent("NP_C_3_01_02", map);
        // 第二还款来源分析-担保人分析-注册资本、经营住所、经营范围、法定代表人是否发生变化
        String NP_C_3_01_03 = this.getSpecificContent("NP_C_3_01_03", map);
        // 第二还款来源分析-担保人分析-担保人具备偿还租金意愿
        String NP_C_3_01_04 = this.getSpecificContent("NP_C_3_01_04", map);
        if (Objects.equals(NP_C_3_01_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_04, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 第二还款来源分析-担保人分析-若发生重大变化，具体情况
            String NP_C_3_01_05 = this.getSpecificContent("NP_C_3_01_05", map);
            if (StrUtil.isBlank(NP_C_3_01_05)) {
                throw new MithrasException("<第二还款来源分析-担保人分析-若发生重大变化，具体情况>不能为空");
            }
        }
        // 租赁物情况-租赁物是否正常使用
        String NP_C_4_01 = this.getSpecificContent("NP_C_4_01", map);
        // 租赁物情况-承租人是否对租赁物计提折旧
        String NP_C_4_02 = this.getSpecificContent("NP_C_4_02", map);
        // 租赁物情况-租赁物是否再次销售/转让
        String NP_C_4_03 = this.getSpecificContent("NP_C_4_03", map);
        // 租赁物情况-租赁物是否转租
        String NP_C_4_04 = this.getSpecificContent("NP_C_4_04", map);
        // 租赁物情况-租赁物是否发生过重大停产停运、重大故障、重大维修情况
        String NP_C_4_05 = this.getSpecificContent("NP_C_4_05", map);
        // 租赁物情况-租赁物是否发生过升级换代、重大改造
        String NP_C_4_06 = this.getSpecificContent("NP_C_4_06", map);
        // 租赁物情况-租赁物是否抵押/质押/留置
        String NP_C_4_07 = this.getSpecificContent("NP_C_4_07", map);
        // 租赁物情况-租赁物是否用于投资入股、抵偿
        String NP_C_4_08 = this.getSpecificContent("NP_C_4_08", map);
        // 租赁物情况-租赁物是否用于诉讼担保/保全等
        String NP_C_4_09 = this.getSpecificContent("NP_C_4_09", map);
        // 租赁物情况-承租人是否对租赁物定期保养、维护
        String NP_C_4_10 = this.getSpecificContent("NP_C_4_10", map);
        // 租赁物情况-租赁物的位置是否被移动
        String NP_C_4_11 = this.getSpecificContent("NP_C_4_11", map);
        // 租赁物情况-是否存在其他损害租赁物所有权行为
        String NP_C_4_12 = this.getSpecificContent("NP_C_4_12", map);
        if (Objects.equals(NP_C_4_01, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_02, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_04, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_05, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_06, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_07, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_08, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_09, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_10, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_11, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_12, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 租赁物情况-租赁物情况补充说明
            String NP_C_4_13 = this.getSpecificContent("NP_C_4_13", map);
            if (StrUtil.isBlank(NP_C_4_13)) {
                throw new MithrasException("<租赁物情况-租赁物情况补充说明>不能为空");
            }
        }
    }


    private void checkContentNonPublicV1(List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> map = contentList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e));
        // 有权机构审批意见落实情况-是否落实审批条件
        String NP_C_1_01_01 = this.getSpecificContent("NP_C_1_01_01", map);
        if (Objects.equals(NP_C_1_01_01, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 有权机构审批意见落实情况-若未落实，具体原因
            String NP_C_1_01_02 = this.getSpecificContent("NP_C_1_01_02", map);
            if (StrUtil.isBlank(NP_C_1_01_02)) {
                throw new MithrasException("<有权机构审批意见落实情况-若未落实，具体原因>不能为空");
            }
        }
        // 客户经营情况分析-主体资格分析-主体资格是否发生重大变化
        String NP_C_2_01_01 = this.getSpecificContent("NP_C_2_01_01", map);
        // 客户经营情况分析-主体资格分析-是否发生歇业、解散、停业整顿、被吊销营业执照或被撤销等情况
        String NP_C_2_01_02 = this.getSpecificContent("NP_C_2_01_02", map);
        // 客户经营情况分析-主体资格分析-注册资本、经营住所、经营范围、法定代表人是否发生变化
        String NP_C_2_01_03 = this.getSpecificContent("NP_C_2_01_03", map);
        // 客户经营情况分析-主体资格分析-承租人具备偿还租金意愿
        String NP_C_2_01_04 = this.getSpecificContent("NP_C_2_01_04", map);
        if (Objects.equals(NP_C_2_01_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_2_01_04, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 客户经营情况分析-主体资格分析-若发生重大变化，具体情况
            String NP_C_1_01_05 = this.getSpecificContent("NP_C_1_01_05", map);
            if (StrUtil.isBlank(NP_C_1_01_05)) {
                throw new MithrasException("<客户经营情况分析-主体资格分析-若发生重大变化，具体情况>不能为空");
            }
        }
        // 客户经营情况分析-行业分析
        String NP_C_2_02 = this.getSpecificContent("NP_C_2_02", map);
        if (StrUtil.isBlank(NP_C_2_02)) {
            throw new MithrasException("<客户经营情况分析-行业分析>不能为空");
        }
        // 客户经营情况分析-财务状况分析-重点财务情况
        String NP_C_2_03_02 = this.getSpecificContent("NP_C_2_03_02", map);
        if (StrUtil.isBlank(NP_C_2_03_02)) {
            throw new MithrasException("<客户经营情况分析-财务状况分析-重点财务情况>不能为空");
        }
        // 客户经营情况分析-经营状况分析-具体分析
        String NP_C_2_04_02 = this.getSpecificContent("NP_C_2_04_02", map);
        if (StrUtil.isBlank(NP_C_2_04_02)) {
            throw new MithrasException("<客户经营情况分析-经营状况分析-具体分析>不能为空");
        }
        // 客户经营状态分析-其他情况搜集与分析-是否出现重大经济纠纷、诉讼、破产等情况
        String NP_C_2_05_01 = this.getSpecificContent("NP_C_2_05_01", map);
        if (Objects.equals(NP_C_2_05_01, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 客户经营状态分析-其他情况搜集与分析-具体分析
            String NP_C_2_05_02 = this.getSpecificContent("NP_C_2_05_02", map);
            if (StrUtil.isBlank(NP_C_2_05_02)) {
                throw new MithrasException("<客户经营状态分析-其他情况搜集与分析-具体分析>不能为空");
            }
        }
        // 客户经营状态分析-租赁资金使用情况
        String NP_C_2_07 = this.getSpecificContent("NP_C_2_07", map);
        if (StrUtil.isBlank(NP_C_2_07)) {
            throw new MithrasException("<客户经营分析状态-租赁资金使用情况>不能为空");
        }
        // 第二还款来源分析-担保人分析-主体资格是否发生重大变化
        String NP_C_3_01_01 = this.getSpecificContent("NP_C_3_01_01", map);
        // 第二还款来源分析-担保人分析-是否发生歇业、解散、停业整顿、被吊销营业执照或被撤销等情况
        String NP_C_3_01_02 = this.getSpecificContent("NP_C_3_01_02", map);
        // 第二还款来源分析-担保人分析-注册资本、经营住所、经营范围、法定代表人是否发生变化
        String NP_C_3_01_03 = this.getSpecificContent("NP_C_3_01_03", map);
        // 第二还款来源分析-担保人分析-担保人具备偿还租金意愿
        String NP_C_3_01_04 = this.getSpecificContent("NP_C_3_01_04", map);
        if (Objects.equals(NP_C_3_01_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_3_01_04, YesOrNoNumberEnum.NO.getCode().toString())) {
            // 第二还款来源分析-担保人分析-若发生重大变化，具体情况
            String NP_C_3_01_05 = this.getSpecificContent("NP_C_3_01_05", map);
            if (StrUtil.isBlank(NP_C_3_01_05)) {
                throw new MithrasException("<第二还款来源分析-担保人分析-若发生重大变化，具体情况>不能为空");
            }
        }
        // 第二还款来源分析-担保人分析-担保人经营情况分析
        String NP_C_3_02_01 = this.getSpecificContent("NP_C_3_02_01", map);
        if (StrUtil.isBlank(NP_C_3_02_01)) {
            throw new MithrasException("<第二还款来源分析-担保人分析-担保人经营情况分析>不能为空");
        }
        // 第二还款来源分析-担保人分析-抵质押物分析
        String NP_C_3_02 = this.getSpecificContent("NP_C_3_02", map);
        if (StrUtil.isBlank(NP_C_3_02)) {
            throw new MithrasException("<第二还款来源分析-担保人分析-抵质押物分析>不能为空");
        }
        // 租赁物情况-租赁物是否正常使用
        String NP_C_4_01 = this.getSpecificContent("NP_C_4_01", map);
        // 租赁物情况-承租人是否对租赁物计提折旧
        String NP_C_4_02 = this.getSpecificContent("NP_C_4_02", map);
        // 租赁物情况-租赁物是否再次销售/转让
        String NP_C_4_03 = this.getSpecificContent("NP_C_4_03", map);
        // 租赁物情况-租赁物是否转租
        String NP_C_4_04 = this.getSpecificContent("NP_C_4_04", map);
        // 租赁物情况-租赁物是否发生过重大停产停运、重大故障、重大维修情况
        String NP_C_4_05 = this.getSpecificContent("NP_C_4_05", map);
        // 租赁物情况-租赁物是否发生过升级换代、重大改造
        String NP_C_4_06 = this.getSpecificContent("NP_C_4_06", map);
        // 租赁物情况-租赁物是否抵押/质押/留置
        String NP_C_4_07 = this.getSpecificContent("NP_C_4_07", map);
        // 租赁物情况-租赁物是否用于投资入股、抵偿
        String NP_C_4_08 = this.getSpecificContent("NP_C_4_08", map);
        // 租赁物情况-租赁物是否用于诉讼担保/保全等
        String NP_C_4_09 = this.getSpecificContent("NP_C_4_09", map);
        // 租赁物情况-承租人是否对租赁物定期保养、维护
        String NP_C_4_10 = this.getSpecificContent("NP_C_4_10", map);
        // 租赁物情况-租赁物的位置是否被移动
        String NP_C_4_11 = this.getSpecificContent("NP_C_4_11", map);
        // 租赁物情况-是否存在其他损害租赁物所有权行为
        String NP_C_4_12 = this.getSpecificContent("NP_C_4_12", map);
        if (Objects.equals(NP_C_4_01, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_02, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_04, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_05, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_06, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_07, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_08, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_09, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_10, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(NP_C_4_11, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(NP_C_4_12, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 租赁物情况-租赁物情况补充说明
            String NP_C_4_13 = this.getSpecificContent("NP_C_4_13", map);
            if (StrUtil.isBlank(NP_C_4_13)) {
                throw new MithrasException("<租赁物情况-租赁物情况补充说明>不能为空");
            }
        }
    }

    private void checkContentPublicV2(List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> map = contentList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e));
        // 当地区域经济情况-承租人所在区域GDP、一般公共预算收入是否较上一年度下滑超过20%
        String P_C_1_01 = this.getSpecificContent("P_C_1_01", map);
        // 当地区域经济情况-承租人所在区域内是否有融资主体出现违约行为
        String P_C_1_02 = this.getSpecificContent("P_C_1_02", map);
        if (Objects.equals(P_C_1_01, YesOrNoNumberEnum.YES.getCode().toString()) || Objects.equals(P_C_1_02, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 当地区域经济情况-当地区域经济情况补充说明
            String P_C_1_03 = this.getSpecificContent("P_C_1_03", map);
            if (StrUtil.isBlank(P_C_1_03)) {
                throw new MithrasException("<当地区域经济情况-当地区域经济情况补充说明>不能为空");
            }
        }
        // 承租人经营情况-承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况
        String P_C_2_01 = this.getSpecificContent("P_C_2_01", map);
        // 承租人经营情况-承租人本期是否出现住所、通讯地址、联系人、联系方式变更
        String P_C_2_02 = this.getSpecificContent("P_C_2_02", map);
        // 承租人经营情况-承租人主要职能定位及经营业务是否发生重大变化
        String P_C_2_03 = this.getSpecificContent("P_C_2_03", map);
        // 承租人经营情况-承租人融资渠道是否通畅
        String P_C_2_04 = this.getSpecificContent("P_C_2_04", map);
        // 承租人经营情况-是否存在被关闭或划转兼并的明确安排
        String P_C_2_05 = this.getSpecificContent("P_C_2_05", map);
        if (Objects.equals(P_C_2_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_2_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_2_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_2_04, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(P_C_2_05, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 承租人经营情况-承租人经营情况补充说明
            String P_C_2_06 = this.getSpecificContent("P_C_2_06", map);
            if (StrUtil.isBlank(P_C_2_06)) {
                throw new MithrasException("<承租人经营情况-承租人经营情况补充说明>不能为空");
            }
        }
        // 承租人经营情况-承租人主要财务数据
        String P_C_2_07 = this.getSpecificContent("P_C_2_07", map);
        if (StrUtil.isBlank(P_C_2_07)) {
            throw new MithrasException("<承租人经营情况-承租人主要财务数据>不能为空");
        }
        // 担保人经营情况-担保人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况
        String P_C_3_01 = this.getSpecificContent("P_C_3_01", map);
        // 担保人经营情况-担保人本期是否出现住所、通讯地址、联系人、联系方式变更
        String P_C_3_02 = this.getSpecificContent("P_C_3_02", map);
        // 担保人经营情况-担保人主要职能定位及经营业务是否发生重大变化
        String P_C_3_03 = this.getSpecificContent("P_C_3_03", map);
        // 担保人经营情况-担保人融资渠道是否通畅
        String P_C_3_04 = this.getSpecificContent("P_C_3_04", map);
        // 担保人经营情况-是否存在被关闭或划转兼并的明确安排
        String P_C_3_05 = this.getSpecificContent("P_C_3_05", map);
        if (Objects.equals(P_C_3_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_3_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_3_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_3_04, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(P_C_3_05, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 担保人经营情况-担保人经营情况补充说明
            String P_C_3_06 = this.getSpecificContent("P_C_3_06", map);
            if (StrUtil.isBlank(P_C_3_06)) {
                throw new MithrasException("<担保人经营情况-担保人经营情况补充说明>不能为空");
            }
        }
        // 租赁物-承租人是否将租赁物进行再次销售、转让
        String P_C_4_01 = this.getSpecificContent("P_C_4_01", map);
        // 租赁物-承租人是否将租赁物进行了转租
        String P_C_4_02 = this.getSpecificContent("P_C_4_02", map);
        // 租赁物-承租人是否将租赁物进行再次抵押、质押
        String P_C_4_03 = this.getSpecificContent("P_C_4_03", map);
        // 租赁物-承租人是否将租赁物进行了投资入股、抵偿
        String P_C_4_04 = this.getSpecificContent("P_C_4_04", map);
        // 租赁物-承租人是否将租赁物进行诉讼担保、是否对租赁物进行了保全担保等处置行为
        String P_C_4_05 = this.getSpecificContent("P_C_4_05", map);
        // 租赁物-承租人是否以其他任何方式进行了侵害出租人对租赁设备的所有权的行为
        String P_C_4_06 = this.getSpecificContent("P_C_4_06", map);
        // 租赁物-租赁物是否能够正常使用
        String P_C_4_07 = this.getSpecificContent("P_C_4_07", map);
        // 租赁物-租赁物是否发生过升级换代、改造
        String P_C_4_08 = this.getSpecificContent("P_C_4_08", map);
        // 租赁物-租赁物的位置是否被移动
        String P_C_4_09 = this.getSpecificContent("P_C_4_09", map);
        // 租赁物-租赁物是否发生过重大停产停运、重大故障、维修情况
        String P_C_4_10 = this.getSpecificContent("P_C_4_10", map);
        if (Objects.equals(P_C_4_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_04, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_05, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_06, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_07, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(P_C_4_08, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_09, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_10, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 租赁物-租赁物情况补充说明
            String P_C_4_11 = this.getSpecificContent("P_C_4_11", map);
            if (StrUtil.isBlank(P_C_4_11)) {
                throw new MithrasException("<租赁物-租赁物情况补充说明>不能为空");
            }
        }
    }

    private void checkContentPublicV3(List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> map = contentList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e, (a, b) -> a));
        // 当地区域经济情况-承租人所在区域GDP、一般公共预算收入是否较上一年度下滑超过20%
        String P_C_1_01 = this.getSpecificContent("P_C_1_01", map);
        // 当地区域经济情况-承租人所在区域内是否有融资主体出现违约行为
        String P_C_1_02 = this.getSpecificContent("P_C_1_02", map);
        if (Objects.equals(P_C_1_01, YesOrNoNumberEnum.YES.getCode().toString()) || Objects.equals(P_C_1_02, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 当地区域经济情况-当地区域经济情况补充说明
            String P_C_1_03 = this.getSpecificContent("P_C_1_03", map);
            if (StrUtil.isBlank(P_C_1_03)) {
                throw new MithrasException("<当地区域经济情况-当地区域经济情况补充说明>不能为空");
            }
        }
        // 承租人经营情况-承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况
        String P_C_2_01 = this.getSpecificContent("P_C_2_01", map);
        // 承租人经营情况-承租人本期是否出现住所、通讯地址、联系人、联系方式变更
        String P_C_2_02 = this.getSpecificContent("P_C_2_02", map);
        // 承租人经营情况-承租人主要职能定位及经营业务是否发生重大变化
        String P_C_2_03 = this.getSpecificContent("P_C_2_03", map);
        // 承租人经营情况-承租人融资渠道是否通畅
        String P_C_2_04 = this.getSpecificContent("P_C_2_04", map);
        // 承租人经营情况-是否存在被关闭或划转兼并的明确安排
        String P_C_2_05 = this.getSpecificContent("P_C_2_05", map);
        if (Objects.equals(P_C_2_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_2_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_2_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_2_04, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(P_C_2_05, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 承租人经营情况-承租人经营情况补充说明
            String P_C_2_06 = this.getSpecificContent("P_C_2_06", map);
            if (StrUtil.isBlank(P_C_2_06)) {
                throw new MithrasException("<承租人经营情况-承租人经营情况补充说明>不能为空");
            }
        }
        // 承租人经营情况-承租人主要财务数据
        String P_C_2_07 = this.getSpecificContent("P_C_2_07", map);
        if (StrUtil.isBlank(P_C_2_07)) {
            throw new MithrasException("<承租人经营情况-承租人主要财务数据>不能为空");
        }
        // 担保人经营情况-担保人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况
        String P_C_3_01 = this.getSpecificContent("P_C_3_01", map);
        // 担保人经营情况-担保人本期是否出现住所、通讯地址、联系人、联系方式变更
        String P_C_3_02 = this.getSpecificContent("P_C_3_02", map);
        // 担保人经营情况-担保人主要职能定位及经营业务是否发生重大变化
        String P_C_3_03 = this.getSpecificContent("P_C_3_03", map);
        // 担保人经营情况-担保人融资渠道是否通畅
        String P_C_3_04 = this.getSpecificContent("P_C_3_04", map);
        // 担保人经营情况-是否存在被关闭或划转兼并的明确安排
        String P_C_3_05 = this.getSpecificContent("P_C_3_05", map);
        if (Objects.equals(P_C_3_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_3_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_3_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_3_04, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(P_C_3_05, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 担保人经营情况-担保人经营情况补充说明
            String P_C_3_06 = this.getSpecificContent("P_C_3_06", map);
            if (StrUtil.isBlank(P_C_3_06)) {
                throw new MithrasException("<担保人经营情况-担保人经营情况补充说明>不能为空");
            }
        }
        // 租赁物-承租人是否将租赁物进行再次销售、转让
        String P_C_4_01 = this.getSpecificContent("P_C_4_01", map);
        // 租赁物-承租人是否将租赁物进行了转租
        String P_C_4_02 = this.getSpecificContent("P_C_4_02", map);
        // 租赁物-承租人是否将租赁物进行再次抵押、质押
        String P_C_4_03 = this.getSpecificContent("P_C_4_03", map);
        // 租赁物-承租人是否将租赁物进行了投资入股、抵偿
        String P_C_4_04 = this.getSpecificContent("P_C_4_04", map);
        // 租赁物-承租人是否将租赁物进行诉讼担保、是否对租赁物进行了保全担保等处置行为
        String P_C_4_05 = this.getSpecificContent("P_C_4_05", map);
        // 租赁物-承租人是否以其他任何方式进行了侵害出租人对租赁设备的所有权的行为
        String P_C_4_06 = this.getSpecificContent("P_C_4_06", map);
        // 租赁物-租赁物是否能够正常使用
        String P_C_4_07 = this.getSpecificContent("P_C_4_07", map);
        // 租赁物-租赁物是否发生过升级换代、改造
        String P_C_4_08 = this.getSpecificContent("P_C_4_08", map);
        // 租赁物-租赁物的位置是否被移动
        String P_C_4_09 = this.getSpecificContent("P_C_4_09", map);
        // 租赁物-租赁物是否发生过重大停产停运、重大故障、维修情况
        String P_C_4_10 = this.getSpecificContent("P_C_4_10", map);
        if (Objects.equals(P_C_4_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_04, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_05, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_06, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_07, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(P_C_4_08, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_09, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_10, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 租赁物-租赁物情况补充说明
            String P_C_4_11 = this.getSpecificContent("P_C_4_11", map);
            if (StrUtil.isBlank(P_C_4_11)) {
                throw new MithrasException("<租赁物-租赁物情况补充说明>不能为空");
            }
        }

        // 检查内容@客户经营情况分析 - 其它情况搜集与分析@是否出现重大经济纠纷、诉讼、破产等情况
        String P_C_2_05_01 = this.getSpecificContent("P_C_2_05_01", map);
        // 检查内容@客户经营情况分析 - 其它情况搜集与分析@具体分析
        String P_C_2_05_02 = this.getSpecificContent("P_C_2_02", map);
    }

    private void checkContentPublicV1(List<NewAfterLeaseCheckReportDetail.FieldData> contentList) {
        Map<String, NewAfterLeaseCheckReportDetail.FieldData> map = contentList.stream().collect(Collectors.toMap(NewAfterLeaseCheckReportDetail.FieldData::getFieldName, e -> e));
        // 当地区域经济情况-承租人所在区域GDP、一般公共预算收入是否较上一年度下滑超过20%
        String P_C_1_01 = this.getSpecificContent("P_C_1_01", map);
        // 当地区域经济情况-承租人所在区域内是否有融资主体出现违约行为
        String P_C_1_02 = this.getSpecificContent("P_C_1_02", map);
        if (Objects.equals(P_C_1_01, YesOrNoNumberEnum.YES.getCode().toString()) || Objects.equals(P_C_1_02, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 当地区域经济情况-当地区域经济情况补充说明
            String P_C_1_03 = this.getSpecificContent("P_C_1_03", map);
            if (StrUtil.isBlank(P_C_1_03)) {
                throw new MithrasException("<当地区域经济情况-当地区域经济情况补充说明>不能为空");
            }
        }
        // 承租人经营情况-承租人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况
        String P_C_2_01 = this.getSpecificContent("P_C_2_01", map);
        // 承租人经营情况-承租人本期是否出现住所、通讯地址、联系人、联系方式变更
        String P_C_2_02 = this.getSpecificContent("P_C_2_02", map);
        // 承租人经营情况-承租人主要职能定位及经营业务是否发生重大变化
        String P_C_2_03 = this.getSpecificContent("P_C_2_03", map);
        // 承租人经营情况-承租人融资渠道是否通畅
        String P_C_2_04 = this.getSpecificContent("P_C_2_04", map);
        // 承租人经营情况-是否存在被关闭或划转兼并的明确安排
        String P_C_2_05 = this.getSpecificContent("P_C_2_05", map);
        if (Objects.equals(P_C_2_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_2_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_2_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_2_04, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(P_C_2_05, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 承租人经营情况-承租人经营情况补充说明
            String P_C_2_06 = this.getSpecificContent("P_C_2_06", map);
            if (StrUtil.isBlank(P_C_2_06)) {
                throw new MithrasException("<承租人经营情况-承租人经营情况补充说明>不能为空");
            }
        }
        // 担保人经营情况-担保人本期是否出现变更股东、注册资本、经营范围、法定代表人等情况
        String P_C_3_01 = this.getSpecificContent("P_C_3_01", map);
        // 担保人经营情况-担保人本期是否出现住所、通讯地址、联系人、联系方式变更
        String P_C_3_02 = this.getSpecificContent("P_C_3_02", map);
        // 担保人经营情况-担保人主要职能定位及经营业务是否发生重大变化
        String P_C_3_03 = this.getSpecificContent("P_C_3_03", map);
        // 担保人经营情况-担保人融资渠道是否通畅
        String P_C_3_04 = this.getSpecificContent("P_C_3_04", map);
        // 担保人经营情况-是否存在被关闭或划转兼并的明确安排
        String P_C_3_05 = this.getSpecificContent("P_C_3_05", map);
        if (Objects.equals(P_C_3_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_3_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_3_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_3_04, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(P_C_3_05, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 担保人经营情况-担保人经营情况补充说明
            String P_C_3_06 = this.getSpecificContent("P_C_3_06", map);
            if (StrUtil.isBlank(P_C_3_06)) {
                throw new MithrasException("<担保人经营情况-担保人经营情况补充说明>不能为空");
            }
        }
        // 租赁物-承租人是否将租赁物进行再次销售、转让
        String P_C_4_01 = this.getSpecificContent("P_C_4_01", map);
        // 租赁物-承租人是否将租赁物进行了转租
        String P_C_4_02 = this.getSpecificContent("P_C_4_02", map);
        // 租赁物-承租人是否将租赁物进行再次抵押、质押
        String P_C_4_03 = this.getSpecificContent("P_C_4_03", map);
        // 租赁物-承租人是否将租赁物进行了投资入股、抵偿
        String P_C_4_04 = this.getSpecificContent("P_C_4_04", map);
        // 租赁物-承租人是否将租赁物进行诉讼担保、是否对租赁物进行了保全担保等处置行为
        String P_C_4_05 = this.getSpecificContent("P_C_4_05", map);
        // 租赁物-承租人是否以其他任何方式进行了侵害出租人对租赁设备的所有权的行为
        String P_C_4_06 = this.getSpecificContent("P_C_4_06", map);
        // 租赁物-租赁物是否能够正常使用
        String P_C_4_07 = this.getSpecificContent("P_C_4_07", map);
        // 租赁物-租赁物是否发生过升级换代、改造
        String P_C_4_08 = this.getSpecificContent("P_C_4_08", map);
        // 租赁物-租赁物的位置是否被移动
        String P_C_4_09 = this.getSpecificContent("P_C_4_09", map);
        // 租赁物-租赁物是否发生过重大停产停运、重大故障、维修情况
        String P_C_4_10 = this.getSpecificContent("P_C_4_10", map);
        if (Objects.equals(P_C_4_01, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_02, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_03, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_04, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_05, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_06, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_07, YesOrNoNumberEnum.NO.getCode().toString())
                || Objects.equals(P_C_4_08, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_09, YesOrNoNumberEnum.YES.getCode().toString())
                || Objects.equals(P_C_4_10, YesOrNoNumberEnum.YES.getCode().toString())) {
            // 租赁物-租赁物情况补充说明
            String P_C_4_11 = this.getSpecificContent("P_C_4_11", map);
            if (StrUtil.isBlank(P_C_4_11)) {
                throw new MithrasException("<租赁物-租赁物情况补充说明>不能为空");
            }
        }
    }

    private void checkSummaryPublicV1(List<NewAfterLeaseCheckReportDetail.FieldData> list) {

    }

    private void checkSummaryPublicV2(List<NewAfterLeaseCheckReportDetail.FieldData> list) {

    }

    private void checkSummaryNonPublicV1(List<NewAfterLeaseCheckReportDetail.FieldData> list) {

    }

    private void checkSummaryNonPublicV2(List<NewAfterLeaseCheckReportDetail.FieldData> list) {

    }
}
