package cn.zswltech.mithras.service.service.newftp.service.drift;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.newftp.FtpValue;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftDetailRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingDetailRsp;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpBusinessVersion;
import cn.zswltech.mithras.projectprocess.enums.newftp.RegionalClassify;
import cn.zswltech.mithras.service.enums.newftp.CustomerEntityClassify;
import cn.zswltech.mithras.ftp.newftp.enums.*;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.ftp.newftp.convert.NewFtpQuarterlyBasePricingConfigConverter;
import cn.zswltech.mithras.ftp.newftp.fms.DefaultNewFtpStateMachine;
import cn.zswltech.mithras.ftp.newftp.fms.NewFtpContext;
import cn.zswltech.mithras.ftp.newftp.fms.NewFtpEvent;
import cn.zswltech.mithras.ftp.newftp.mapper.config.NewFtpQuarterlyBasePricingTemplateConfigMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpQuarterlyBasePricingDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpMonthlyGuidanceTemplateConfig;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpParameterSettingConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.*;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpMonthlyGuidanceLib;
import cn.zswltech.mithras.ftp.newftp.service.drift.NewFtpDescriptionTextDraftService;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpMonthlyGuidanceTemplateConfigService;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpParameterSettingConfigService;
import cn.zswltech.mithras.service.service.newftp.service.lib.NewFtpMonthlyGuidanceLibService;
import cn.zswltech.mithras.service.util.FlowUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 季度指导基础定价
 * @date 2023-05-21
 */
@Service
@Slf4j
public class NewFtpQuarterlyBasePricingDraftService
        extends ServiceImpl<NewFtpQuarterlyBasePricingDraftMapper, NewFtpQuarterlyBasePricingDraft> {
    @Resource
    private NewFtpBaseInfoService baseInfoService;
    @Resource
    private NewFtpQuarterlyBasePricingTemplateConfigMapper quarterlyBasePricingTemplateConfigMapper;
    @Resource
    private NewFtpQuarterlyBasePricingConfigConverter baseConverter;
    @Resource
    private NewFtpMonthlyGuidanceDraftService monthlyGuidanceDraftService;
    @Resource
    private NewFtpParameterSettingConfigService newFtpParameterSettingConfigService;
    @Resource
    private DefaultNewFtpStateMachine defaultNewFtpStateMachine;
    @Resource
    private NewFtpDescriptionTextDraftService newFtpDescriptionTextDraftService;
    @Resource
    private NewFtpMonthlyGuidanceDraftService newFtpMonthlyGuidanceDraftService;
    @Resource
    private NewFtpMonthlyGuidanceExtDraftService newFtpMonthlyGuidanceExtDraftService;
    @Resource
    private NewFtpBaseInfoService newFtpBaseInfoService;
    @Resource
    private FileTemplateService fileTemplateService;
    @Resource
    private NewFtpMonthlyGuidanceLibService newFtpMonthlyGuidanceLibService;

    public static final Map<String, String> ROW_KEYS = new HashMap<>();
    public final String MSG = "不存在历史生效数据";

    static {
        ROW_KEYS.put("row1", "ENCOURAGE_INTERVENTION:ZHEJIANG");
        ROW_KEYS.put("row2", "ENCOURAGE_INTERVENTION:ENCOURAGE");
        ROW_KEYS.put("row3", "ENCOURAGE_INTERVENTION:OTHER");
        ROW_KEYS.put("row4", "MODERATE_SUPPORT:ZHEJIANG");
        ROW_KEYS.put("row5", "MODERATE_SUPPORT:ENCOURAGE");
        ROW_KEYS.put("row6", "MODERATE_SUPPORT:OTHER");
        ROW_KEYS.put("row7", "CAUTIOUS_SUPPORT:ZHEJIANG");
        ROW_KEYS.put("row8", "CAUTIOUS_SUPPORT:ENCOURAGE");
        ROW_KEYS.put("row9", "CAUTIOUS_SUPPORT:OTHER");
    }

    public void add(Long mainId) {
        //取存量生效的【FTP成本定价】的平均值，在平均值的基础上，加上参数【最低综合补偿率】设置的数值。
        NewFtpBaseInfo baseInfo = baseInfoService.getById(mainId);
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        this.remove(Wrappers.<NewFtpQuarterlyBasePricingDraft>lambdaQuery()
                .eq(NewFtpQuarterlyBasePricingDraft::getFtpId, baseInfo.getId()));
        List<NewFtpMonthlyGuidanceLib> result = new LinkedList<>();
        if (PricingFrequencyEnum.MONTHLY.name().equals(baseInfo.getPricingFrequency())) {
            //取就近3个月数据， 若没有则只取一个, 一月份需要特殊处理
            List<NewFtpBaseInfo> ftpBaseInfoList;
            if (baseInfo.getMonth().getMonth().getValue() == 1) {
                //1月份
                ftpBaseInfoList = baseInfoService.list(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                        .eq(NewFtpBaseInfo::getFtpRecordStatus, RecordStatus.TAKE_EFFECT.name())
                        .eq(NewFtpBaseInfo::getPricingFrequency, PricingFrequencyEnum.MONTHLY.name())
                        .eq(NewFtpBaseInfo::getFtpBusinessVersion, baseInfo.getFtpBusinessVersion())
                        .between(NewFtpBaseInfo::getMonth,
                                baseInfo.getMonth().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()),
                                baseInfo.getMonth().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth())));
            } else if (baseInfo.getMonth().getMonth().getValue() < 4) {
                //2-4月份
                ftpBaseInfoList = baseInfoService.list(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                        .eq(NewFtpBaseInfo::getFtpRecordStatus, RecordStatus.TAKE_EFFECT.name())
                        .eq(NewFtpBaseInfo::getPricingFrequency, PricingFrequencyEnum.MONTHLY.name())
                        .eq(NewFtpBaseInfo::getFtpBusinessVersion, baseInfo.getFtpBusinessVersion())
                        .between(NewFtpBaseInfo::getMonth,
                                baseInfo.getMonth().with(TemporalAdjusters.firstDayOfYear()),
                                baseInfo.getMonth().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth())));
            } else {
                //4-12月份
                ftpBaseInfoList = baseInfoService.list(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                        .eq(NewFtpBaseInfo::getFtpRecordStatus, RecordStatus.TAKE_EFFECT.name())
                        .eq(NewFtpBaseInfo::getPricingFrequency, PricingFrequencyEnum.MONTHLY.name())
                        .eq(NewFtpBaseInfo::getFtpBusinessVersion, baseInfo.getFtpBusinessVersion())
                        .between(NewFtpBaseInfo::getMonth,
                                baseInfo.getMonth().minusMonths(3).with(TemporalAdjusters.firstDayOfMonth()),
                                baseInfo.getMonth().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth())));
            }

            if (ObjectUtil.isEmpty(ftpBaseInfoList)) {
                //3. 如果某季度或某月份不存在生效的 ftp 成本定价数据，则取上个月的数据。
                ftpBaseInfoList = baseInfoService.list(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                        .eq(NewFtpBaseInfo::getFtpBusinessVersion, baseInfo.getFtpBusinessVersion())
                        .eq(NewFtpBaseInfo::getPricingFrequency, PricingFrequencyEnum.MONTHLY.name()));
                if (CollUtil.isEmpty(ftpBaseInfoList)) {
                    throw new MithrasException(MSG);
                }
            }

            ftpBaseInfoList.sort(Comparator.comparing(NewFtpBaseInfo::getMonth));
            NewFtpBaseInfo newFtpBaseInfo = ftpBaseInfoList.get(ftpBaseInfoList.size() - 1);
            List<NewFtpMonthlyGuidanceLib> libList = newFtpMonthlyGuidanceLibService.list(Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                    .eq(NewFtpMonthlyGuidanceLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, RiskIndustryClassify.INDUSTRY.name())
                    .eq(NewFtpMonthlyGuidanceDraft::getFtpId, newFtpBaseInfo.getId()));
            if (ObjectUtil.isEmpty(libList)) {
                throw new MithrasException(MSG);
            }
            //计算平均值
            extracted(result, libList);

        } else if (PricingFrequencyEnum.QUARTER.name().equals(baseInfo.getPricingFrequency())) {
            //取当前季度的平均值，若没有，则取去年整年的季度平均值
            List<NewFtpBaseInfo> ftpBaseInfoList;
            if (baseInfo.getMonth().getMonth().getValue() == 1) {
                ftpBaseInfoList = baseInfoService.list(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                        .eq(NewFtpBaseInfo::getFtpRecordStatus, RecordStatus.TAKE_EFFECT.name())
                        .eq(NewFtpBaseInfo::getPricingFrequency, PricingFrequencyEnum.QUARTER.name())
                        .eq(NewFtpBaseInfo::getFtpBusinessVersion, baseInfo.getFtpBusinessVersion())
                        .between(NewFtpBaseInfo::getMonth,
                                baseInfo.getMonth().minusYears(1).with(TemporalAdjusters.firstDayOfYear()),
                                baseInfo.getMonth().minusYears(1).with(TemporalAdjusters.lastDayOfYear())));
            } else {
                ftpBaseInfoList = baseInfoService.list(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                        .eq(NewFtpBaseInfo::getFtpRecordStatus, RecordStatus.TAKE_EFFECT.name())
                        .eq(NewFtpBaseInfo::getPricingFrequency, PricingFrequencyEnum.QUARTER.name())
                        .eq(NewFtpBaseInfo::getFtpBusinessVersion, baseInfo.getFtpBusinessVersion())
                        .between(NewFtpBaseInfo::getMonth,
                                baseInfo.getMonth().with(TemporalAdjusters.firstDayOfYear()),
                                baseInfo.getMonth().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth())));
            }
            if (ObjectUtil.isEmpty(ftpBaseInfoList)) {
                //3. 如果某季度或某月份不存在生效的 ftp 成本定价数据，则取上个月的数据。
                ftpBaseInfoList = baseInfoService.list(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                        .eq(NewFtpBaseInfo::getFtpBusinessVersion, baseInfo.getFtpBusinessVersion())
                        .eq(NewFtpBaseInfo::getPricingFrequency, PricingFrequencyEnum.MONTHLY.name()));
                if (CollUtil.isEmpty(ftpBaseInfoList)) {
                    throw new MithrasException(MSG);
                }
            }
            ftpBaseInfoList.sort(Comparator.comparing(NewFtpBaseInfo::getMonth));
            NewFtpBaseInfo newFtpBaseInfo = ftpBaseInfoList.get(ftpBaseInfoList.size() - 1);

            List<NewFtpMonthlyGuidanceLib> list = newFtpMonthlyGuidanceLibService.list(Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                    .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, RiskIndustryClassify.INDUSTRY.name())
                    .in(NewFtpMonthlyGuidanceDraft::getFtpId, ftpBaseInfoList.stream().map(NewFtpBaseInfo::getId).collect(Collectors.toList()))
                    .eq(NewFtpMonthlyGuidanceLib::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(NewFtpMonthlyGuidanceDraft::getFtpId, newFtpBaseInfo.getId())
                    .orderByAsc(NewFtpMonthlyGuidanceDraft::getTemplateId));
            //计算平均值
            extracted(result, list);
        } else {
            throw new MithrasException("定价频率有误，请检查后重试！");
        }

        //基础数据需要加上参数设置里面的值
        Map<String, Map<String, NewFtpParameterSettingConfig>> settingMap = new HashMap<>(8);
        Map<String, List<NewFtpParameterSettingConfig>> tmpMap = newFtpParameterSettingConfigService.list(Wrappers.<NewFtpParameterSettingConfig>lambdaQuery()
                        .eq(NewFtpParameterSettingConfig::getCategory, ParamCategory.MINIMUM_COMPREHENSIVE_COMPENSATION_RATE.name()))
                .stream().collect(Collectors.groupingBy(NewFtpParameterSettingConfig::getParamName));
        tmpMap.forEach((k, v) -> settingMap.put(k, v.stream()
                .collect(Collectors.toMap(NewFtpParameterSettingConfig::getParamOtherName,
                        Function.identity(), (a, b) -> a))));

        SpringContextHolder.getBean(NewFtpQuarterlyBasePricingDraftService.class)
                .saveBatch(buildDraftList(settingMap, result, baseInfo));
    }

    private void extracted(List<NewFtpMonthlyGuidanceLib> result, List<NewFtpMonthlyGuidanceLib> resData) {
        Map<Long, List<NewFtpMonthlyGuidanceLib>> templateListMap = resData.stream().collect(Collectors.groupingBy(NewFtpMonthlyGuidanceDraft::getTemplateId));
        templateListMap.forEach((k, v) ->
        {
            NewFtpMonthlyGuidanceLib guidanceLib = v.get(0);
            NewFtpMonthlyGuidanceLib lib = new NewFtpMonthlyGuidanceLib();
            lib.setAssetIndustryClassify(guidanceLib.getAssetIndustryClassify());
            lib.setRegionalClassify(guidanceLib.getRegionalClassify());
            lib.setTermRange(guidanceLib.getTermRange());
            lib.setCustomerEntityClassify(guidanceLib.getCustomerEntityClassify());
            //在平均值的基础上，加上参数【最低综合补偿率】设置的数值
            long sum = v.stream().mapToLong(NewFtpMonthlyGuidanceDraft::getValue).sum();
            long value = BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(v.size()), 20, RoundingMode.HALF_UP).longValue();
            lib.setValue(Integer.parseInt(value + ""));
            lib.setLocation(guidanceLib.getLocation());
            lib.setTemplateId(guidanceLib.getTemplateId());
            result.add(lib);
        });
    }

    private List<NewFtpQuarterlyBasePricingDraft> buildDraftList(Map<String, Map<String, NewFtpParameterSettingConfig>> settingMap,
                                                                 List<NewFtpMonthlyGuidanceLib> list, NewFtpBaseInfo newFtpBaseInfo) {
        if (CollUtil.isEmpty(list)) {
//            return Collections.emptyList();
            // 没有数据的话就用模板初始化一份空的吧
            // 翻老代码，季度最低收益率里的模板id是月度定价的，但看数据库表是有定义季度最低收益率自己的模板表的（可能是为了保证模板一致），此处保留原始逻辑，季度最低收益率也用月度定价模板
            List<NewFtpMonthlyGuidanceTemplateConfig> templateConfigList = SpringUtil.getBean(NewFtpMonthlyGuidanceTemplateConfigService.class).list(
                    Wrappers.<NewFtpMonthlyGuidanceTemplateConfig>lambdaQuery()
                            .eq(NewFtpMonthlyGuidanceTemplateConfig::getRiskIndustryClassify, RiskIndustryClassify.INDUSTRY.name())
                            .eq(NewFtpMonthlyGuidanceTemplateConfig::getFtpBusinessVersion, newFtpBaseInfo.getFtpBusinessVersion())
                            .orderByAsc(NewFtpMonthlyGuidanceTemplateConfig::getId)
            );
            return templateConfigList.stream().map(template -> {
                NewFtpQuarterlyBasePricingDraft draft = BeanUtil.copyProperties(template, NewFtpQuarterlyBasePricingDraft.class);
                draft.setTemplateId(template.getId());
                draft.setFtpId(newFtpBaseInfo.getId());
                return draft;
            }).collect(Collectors.toList());
        }
        // 有数据的话用现有数据计算
        List<NewFtpQuarterlyBasePricingDraft> dataList = new ArrayList<>(list.size());
        list.forEach(dto -> {
            NewFtpQuarterlyBasePricingDraft draft = new NewFtpQuarterlyBasePricingDraft();
            //set value
            draft.setFtpId(newFtpBaseInfo.getId());
            draft.setAssetIndustryClassify(dto.getAssetIndustryClassify());
            draft.setRegionalClassify(dto.getRegionalClassify());
            draft.setTermRange(dto.getTermRange());
            draft.setCustomerEntityClassify(dto.getCustomerEntityClassify());
            //在平均值的基础上，加上参数【最低综合补偿率】设置的数值
            Map<String, NewFtpParameterSettingConfig> m = settingMap.get(EnterpriseTypeEnum.valueOf(dto.getCustomerEntityClassify()).getDisplay());
            Integer value = Optional.ofNullable(m).map(e -> e.get(RegionalClassify.valueOf(dto.getRegionalClassify()).display()).getValue()).orElse(0);
            draft.setValue(dto.getValue() + value);
            draft.setLocation(dto.getLocation());
            draft.setTemplateId(dto.getTemplateId());
            dataList.add(draft);
        });
        return dataList;
    }


    @Transactional(rollbackFor = Throwable.class)
    public void modify(NewFtpQuarterlyBasePricingModifyREQ req) {
        NewFtpQuarterlyBasePricingDraft pricing = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(pricing)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        ProcessResp relatedProcess = baseInfoService.findRelatedProcess(pricing.getFtpId());
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(relatedProcess);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
        pricing.setValue(req.getValue());
        baseMapper.updateById(pricing);

        NewFtpBaseInfo baseInfo = baseInfoService.getById(pricing.getFtpId());
        defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.MODIFY_SAVE, baseInfo.getProcessStatus()));
    }

    public NewFtpQuarterlyBasePricingDetailRsp detail(Long mainId) {

        List<NewFtpQuarterlyBasePricingDraft> all = baseMapper.selectList(
                Wrappers.<NewFtpQuarterlyBasePricingDraft>lambdaQuery()
                        .eq(NewFtpQuarterlyBasePricingDraft::getFtpId, mainId));
        Map<String, List<NewFtpQuarterlyBasePricingDraft>> rows = all.stream()
                .collect(Collectors.groupingBy(pricing -> String.join(":",
                        pricing.getAssetIndustryClassify(), pricing.getRegionalClassify())));

        NewFtpQuarterlyBasePricingDetailRsp rsp = new NewFtpQuarterlyBasePricingDetailRsp();

        ROW_KEYS.forEach((field, key) -> {
            try {
                List<FtpValue> rowData = rows.get(key).stream()
                        .sorted(Comparator.comparing(NewFtpQuarterlyBasePricingDraft::getTemplateId))
                        .map(mg -> new FtpValue().setId(mg.getId()).setValue(mg.getValue()))
                        .collect(Collectors.toList());
                ReflectUtil.setFieldValue(rsp, field, rowData);
            } catch (Exception e) {
                log.error("设置字段值失败", e);
            }
        });
        return rsp;

    }

    private List<NewFtpQuarterlyBasePricingDraft> getNewFtpQuarterlyByMainId(Long mainId) {
        return baseMapper.selectList(
                Wrappers.<NewFtpQuarterlyBasePricingDraft>lambdaQuery()
                        .eq(NewFtpQuarterlyBasePricingDraft::getFtpId, mainId));
    }

    @SneakyThrows
    public void exportExcel(Long mainId, OutputStream outputStream) {
        NewFtpBaseInfo baseInfo = newFtpBaseInfoService.getById(mainId);
        FtpBusinessVersion ftpBusinessVersion = FtpBusinessVersion.getByName(baseInfo.getFtpBusinessVersion());
        if (Objects.isNull(ftpBusinessVersion)) {
            throw new MithrasException("FTP业务定价版本不存在");
        }
        switch (ftpBusinessVersion) {
            case V2: {
                this.exportExcelByTemplate(baseInfo, outputStream, "ftp_2.0_month.xlsx", "ftp_2.0_quarterly.xlsx");
                break;
            }
            case V3: {
                this.exportExcelByTemplate(baseInfo, outputStream, "ftp_3.0_month.xlsx", "ftp_3.0_quarterly.xlsx");
            }
        }
    }

    public void calculate(NewFtpBaseInfo baseInfo) {
        add(baseInfo.getId());
    }

    private void exportExcelByTemplate(NewFtpBaseInfo baseInfo, OutputStream outputStream, String monthlyTemplateName, String quarterlyTemplateName) {
        Long mainId = baseInfo.getMainId();
        //导出月度
        Map<String, String> paramMap =
                newFtpMonthlyGuidanceDraftService.getByMainId(mainId).stream().
                        collect(Collectors.toMap(base -> String.valueOf(LongUtil.null2zero(base.getTemplateId())), base -> LongUtil.tenThousand2Dollar(LongUtil.null2zero(base.getValue()).toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString(), (a, b) -> a));
        Map<String, String> quarterParamMap = null;
        NewFtpDescriptionTextDraft quarterDescriptionText = null;
        InputStream in;
        //下半部分
        NewFtpMonthlyGuidanceExtDraftDetailRSP detail = newFtpMonthlyGuidanceExtDraftService.detail(mainId);
        Field[] fields = ReflectUtil.getFields(NewFtpMonthlyGuidanceExtDraftDetailRSP.class);
        for (Field field : fields) {
            if (field.getType().equals(Long.class)) {
                paramMap.put(field.getName(), LongUtil.tenThousand2Dollar(LongUtil.null2zero((Long) ReflectUtil.getFieldValue(detail, field.getName())).toString()).setScale(2,
                        BigDecimal.ROUND_HALF_UP).toPlainString());
            } else if (field.getType().equals(Integer.class)) {
                paramMap.put(field.getName(), LongUtil.tenThousand2Dollar(LongUtil.null2zero((Integer) ReflectUtil.getFieldValue(detail, field.getName())).toString()).setScale(2,
                        BigDecimal.ROUND_HALF_UP).toPlainString());
            } else {
                paramMap.put(field.getName(), String.valueOf(ReflectUtil.getFieldValue(detail, field.getName())));
            }
        }
        //单位
        NewFtpDescriptionTextDraft monthText = newFtpDescriptionTextDraftService.getOne(Wrappers.<NewFtpDescriptionTextDraft>lambdaQuery()
                .eq(NewFtpDescriptionTextDraft::getFtpId, mainId)
                .eq(NewFtpDescriptionTextDraft::getDescType, DescriptionType.MONTHLY_GUIDANCE.name())
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotEmpty(monthText)) {
            paramMap.put("unit", monthText.getDescContent());
        }
        paramMap.put("year", String.valueOf(baseInfo.getMonth().getYear()));
        paramMap.put("month", String.valueOf(baseInfo.getMonth().getMonthValue()));
        //sheet 只识别字符串
        if (ObjectUtil.equals(baseInfo.getPricingFrequency(), PricingFrequencyEnum.QUARTER.name())) {
            quarterParamMap = getNewFtpQuarterlyByMainId(mainId).stream().collect(Collectors.toMap(base -> String.valueOf(LongUtil.null2zero(base.getTemplateId())), base -> LongUtil.tenThousand2Dollar(LongUtil.null2zero(base.getValue()).toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString(), (a, b) -> a));
            quarterDescriptionText = newFtpDescriptionTextDraftService.getOne(Wrappers.<NewFtpDescriptionTextDraft>lambdaQuery()
                    .eq(NewFtpDescriptionTextDraft::getFtpId, mainId)
                    .eq(NewFtpDescriptionTextDraft::getDescType, DescriptionType.QUARTERLY_PRICING.name())
                    .last(StringUtil.mysqlLimitOne()));
            quarterParamMap.put("year", String.valueOf(baseInfo.getMonth().getYear()));
            quarterParamMap.put("month", String.valueOf(baseInfo.getMonth().getMonthValue()));
            // 填充集团控股公司季度最低收益率
            NewFtpQuarterlyBasePricingExtDraft extDraft = SpringUtil.getBean(NewFtpQuarterlyBasePricingExtDraftService.class).findByFtpId(mainId);
            quarterParamMap.put("threeYear", Optional.ofNullable(extDraft).map(e -> BigDecimal.valueOf(e.getThreeYear()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            quarterParamMap.put("threeToFiveYear", Optional.ofNullable(extDraft).map(e -> BigDecimal.valueOf(e.getThreeToFiveYear()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            quarterParamMap.put("moreThanFiveYear", Optional.ofNullable(extDraft).map(e -> BigDecimal.valueOf(e.getMoreThanFiveYear()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString()).orElse(null));
            NewFtpDescriptionTextDraft quarterExtDescriptionText = newFtpDescriptionTextDraftService.getOne(Wrappers.<NewFtpDescriptionTextDraft>lambdaQuery()
                    .eq(NewFtpDescriptionTextDraft::getFtpId, mainId)
                    .eq(NewFtpDescriptionTextDraft::getDescType, DescriptionType.QUARTERLY_PRICING_EXT.name())
                    .last(StringUtil.mysqlLimitOne()));
            quarterParamMap.put("unitExt", Optional.ofNullable(quarterExtDescriptionText).map(NewFtpDescriptionTextDraft::getDescContent).orElse(null));
        }
        try {
            if (Objects.nonNull(quarterDescriptionText) && ObjectUtil.isNotEmpty(quarterParamMap)) {
                quarterParamMap.put("unit", quarterDescriptionText.getDescContent());
                in = fileTemplateService.getTemplate("ftp季度指导报价", quarterlyTemplateName);
            } else {
                in = fileTemplateService.getTemplate("ftp月度指导报价", monthlyTemplateName);
            }
            ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(in).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(0).build();
            excelWriter.fill(paramMap, writeSheet);
            if (ObjectUtil.isNotEmpty(quarterParamMap)) {
                WriteSheet quarterWriteSheet = EasyExcel.writerSheet(1).build();
                excelWriter.fill(quarterParamMap, quarterWriteSheet);
            }
            excelWriter.finish();
            outputStream.flush();
        } catch (Exception e) {
            log.error("NewFtpQuarterlyBasePricingService exportV1Excel error", e);
        }
    }
}
