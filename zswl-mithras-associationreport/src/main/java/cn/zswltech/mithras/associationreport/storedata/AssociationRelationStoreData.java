package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.service.RiskMetricFactorMergeService;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationRelationService;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpShareholderInfoMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationRelation;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.customer.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.mapper.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.dashboard.application.GuanYuanOperationService;
import cn.zswltech.mithras.dashboard.application.guanyuandata.ProjectSituationDTO;
import cn.zswltech.mithras.basedata.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * @author dingqi
 * @date 2025/4/21
 * @description
 */
@Slf4j
@Component
public class AssociationRelationStoreData extends AbstractDataStore<AssociationRelation> {
    @Resource
    private CorpShareholderInfoMapper corpShareholderInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private RiskMetricFactorMergeService riskMetricFactorMergeService;
    @Resource
    private GuanYuanOperationService guanYuanOperationService;

    @Override
    public boolean storeFromSystemJobCheck(int year, int period) {
        LocalDate dataDate  = DateUtil.ensureQuarterLastDay(year, period);
        Map<String, Long> assetMap = riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, RiskMetricFactorTable.CAPITAL_BALANCE.display, dataDate.getYear(), dataDate.getMonthValue());
        // 项目情况表
        List<ProjectSituationDTO> projectSituationList = guanYuanOperationService.listProjectSituation(dataDate);
        boolean condition1 = CollectionUtil.isNotEmpty(assetMap);
        boolean condition2 = CollectionUtil.isNotEmpty(projectSituationList);
        log.info("金融局报送【关联方信息汇总表】自动取值-前置数据校验结果:资产负债表 = {}, 项目情况表 = {}", condition1, condition2);
        return condition1 && condition2;
    }

    @Override
    protected List<AssociationRelation> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【关联方信息汇总表】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 5) {
            // 5行是因为序号行存在单元格合并，3、4、5三行合成了1行
            throw new MithrasException("<关联方信息汇总表>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "融资租赁公司关联方信息汇总表");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<关联方信息汇总表>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationRelation> list = new LinkedList<>();
        for (int i = 5; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            if (Objects.equals("合计", row.get(0))) {
                break;
            }
            try {
                list.add(this.convert(i -4, row, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【关联方信息汇总表】-第{}行数据处理异常", (i-4), e);
                throw new MithrasException("数据处理发生异常");
            }
        }
        return list;
    }

    @Override
    protected List<AssociationRelation> parseFromSystemData(AssociationReport associationReport) {
        LocalDate metricDate = this.ensureMetricDate(associationReport);
        // 从项目情况表取关联方交易的客户数据
        List<ProjectSituationDTO> todoList = guanYuanOperationService.listProjectSituation(metricDate);
        if (CollectionUtil.isEmpty(todoList)) {
            return Collections.emptyList();
        }
        // 取我方股东方列表
        SystemConfigDO systemConfigDO = systemConfigService.getConfig("shareholder_list").getData();
        List<String> myShareholderList = JSONUtil.toList(systemConfigDO.getConfigValue(), String.class);
        // 取资产负债表：所有者权益（或股东权益）合计@期末余额
        Map<String, Long> assetValueMap = riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, RiskMetricFactorTable.CAPITAL_BALANCE.display, metricDate.getYear(), metricDate.getMonthValue());
        Long v = assetValueMap.get("所有者权益（或股东权益）合计@期末余额");
        // 处理数据
        List<AssociationRelation> result = new LinkedList<>();
        for (ProjectSituationDTO projectSituationDTO : todoList) {
            if (Objects.equals(projectSituationDTO.getIsRelated(), YesOrNoNumberEnum.NO.getCode())) {
                continue;
            }
            Client client = clientMapper.selectById(projectSituationDTO.getClientId());
            Client belongGroupClient = clientMapper.selectById(Optional.ofNullable(projectSituationDTO.getBelongGroupClientId()).orElse(0L));
            long remainingPrincipal = projectSituationDTO.getPrincipalBalance();
            if (remainingPrincipal <= 0) {
                continue;
            }
            AssociationRelation associationRelation = new AssociationRelation();
            // 关联方
            associationRelation.setRelpName(projectSituationDTO.getClientName());
            String shareholderName = this.myShareholder(client, myShareholderList);
            if (StrUtil.isBlank(shareholderName)) {
                associationRelation.setCorpShahRelpFlag(YesOrNoNumberEnum.NO.getCode().toString());
            } else {
                associationRelation.setCorpShahRelpFlag(YesOrNoNumberEnum.YES.getCode().toString());
                associationRelation.setCorpShahName(shareholderName);
            }
            associationRelation.setOnblRelpLeasBalSrlp(Util.millimeterLong2WanBigDecimal(remainingPrincipal));
            if (Objects.nonNull(v) && v != 0) {
                associationRelation.setOnblOnarSrlp(BigDecimal.valueOf(remainingPrincipal).divide(BigDecimal.valueOf(v), 8, RoundingMode.HALF_UP));
            }
            long riskExposure = projectSituationDTO.getPrincipalBalance() - projectSituationDTO.getMarginBalance();
            if (riskExposure < 0) {
                riskExposure = 0;
            }
            associationRelation.setCredExpsSrlp(Util.millimeterLong2WanBigDecimal(riskExposure));
            long marginBalance = projectSituationDTO.getMarginBalance();
            if (marginBalance >= 0) {
                associationRelation.setDeitOthSrlp(Util.millimeterLong2WanBigDecimal(marginBalance));
            }
            // 关联方所属集团
            if (Objects.isNull(belongGroupClient)) {
                continue;
            }
            associationRelation.setGrlpName(belongGroupClient.getClientName());
            // 所属集团对应字段统一取对应单一关联方
            associationRelation.setOnblRelpLeasBalGrlp(associationRelation.getOnblRelpLeasBalSrlp());
            associationRelation.setOnblOnarGrlp(associationRelation.getOnblOnarSrlp());
            associationRelation.setCredExpsGrlp(associationRelation.getCredExpsSrlp());
            associationRelation.setDeitOthGrlp(associationRelation.getDeitOthSrlp());
            result.add(associationRelation);
        }
        return result;
    }

    @Override
    protected void check(List<AssociationRelation> dataList) {
        for (AssociationRelation associationRelation : dataList) {
            // 是否为本公司关联股东方取值字典范围
            if (Objects.equals(associationRelation.getCorpShahRelpFlag(), DICT_UNKNOWN_CODE)) {
                throw new MithrasException(String.format("第%s行数据（不含表头）的<是否为本公司关联股东方>非法", associationRelation.getRowNum()));
            }
            // 是本公司关联股东方时，本公司股东名称必填
            if (Objects.nonNull(associationRelation.getCorpShahRelpFlag()) && Objects.equals(associationRelation.getCorpShahRelpFlag(), String.valueOf(YesOrNoNumberEnum.YES.getCode()))) {
                if (StrUtil.isBlank(associationRelation.getCorpShahName())) {
                    throw new MithrasException(String.format("第%s行数据（不含表头）的<本公司关联股东名称>不能为空", associationRelation.getRowNum()));
                }
            }
        }
    }

    @Override
    protected IService<AssociationRelation> serviceBean() {
        return SpringUtil.getBean(AssociationRelationService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0012;
    }

    private AssociationRelation convert(int rowNum, List<Object> row, Map<String, Map<String, String>> dictNameMap) {
        AssociationRelation associationRelation = new AssociationRelation();
        associationRelation.setRowNum(rowNum);
        // 序号
        associationRelation.setOnum(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));
        // 关联方名称
        associationRelation.setRelpName(Optional.ofNullable(row.get(1)).map(Object::toString).orElse(null));
        // 是否为本公司股东关联方
        if (row.get(2) != null && StrUtil.isNotBlank(row.get(2).toString())) {
            YesOrNoNumberEnum item = YesOrNoNumberEnum.findByChinese(row.get(2).toString());
            if (Objects.nonNull(item)) {
                associationRelation.setCorpShahRelpFlag(item.getCode().toString());
            } else {
                associationRelation.setCorpShahRelpFlag(DICT_UNKNOWN_CODE);
            }
        }
        // 本公司股东名称
        associationRelation.setCorpShahName(Optional.ofNullable(row.get(3)).map(Object::toString).orElse(null));
        // 表内业务-关联方租赁余额_单一关联方
        associationRelation.setOnblRelpLeasBalSrlp(Optional.ofNullable(row.get(4)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 表内业务-占净资产比例_单一关联方
        associationRelation.setOnblOnarSrlp(Optional.ofNullable(row.get(5)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 表外业务-担保_单一关联方
        associationRelation.setOfblGuarSrlp(Optional.ofNullable(row.get(6)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 表外业务-其他_单一关联方
        associationRelation.setOfblOthSrlp(Optional.ofNullable(row.get(7)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 扣减项-合格质物_单一关联方
        associationRelation.setDeitQulfSbimSrlp(Optional.ofNullable(row.get(8)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 扣减项-合格保证_单一关联方
        associationRelation.setDeitQulfAsueSrlp(Optional.ofNullable(row.get(9)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 扣减项-其他_单一关联方
        associationRelation.setDeitOthSrlp(Optional.ofNullable(row.get(10)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 信用风险敞口_单一关联方
        associationRelation.setCredExpsSrlp(Optional.ofNullable(row.get(11)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 所在集团名称_关联方所在集团
        associationRelation.setGrlpName(Optional.ofNullable(row.get(12)).map(Object::toString).orElse(null));
        // 表内业务-关联方租赁余额_关联方所在集团
        associationRelation.setOnblRelpLeasBalGrlp(Optional.ofNullable(row.get(13)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 表内业务-占净资产比例_关联方所在集团
        associationRelation.setOnblOnarGrlp(Optional.ofNullable(row.get(14)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 表外业务-担保_关联方所在集团
        associationRelation.setOfblGuarGrlp(Optional.ofNullable(row.get(15)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 表外业务-其他_关联方所在集团
        associationRelation.setOfblOthGrlp(Optional.ofNullable(row.get(16)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 扣减项-合格质物_关联方所在集团
        associationRelation.setDeitQulfSbimGrlp(Optional.ofNullable(row.get(17)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 扣减项-合格保证_关联方所在集团
        associationRelation.setDeitQulfAsueGrlp(Optional.ofNullable(row.get(18)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 扣减项-其他_关联方所在集团
        associationRelation.setDeitOthGrlp(Optional.ofNullable(row.get(19)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 信用风险敞口_关联方所在集团
        associationRelation.setCredExpsGrlp(Optional.ofNullable(row.get(20)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        return associationRelation;
    }

    private String myShareholder(Client client, List<String> myShareholderList) {
        if (myShareholderList.contains(client.getClientName())) {
            return client.getClientName();
        }
        // 查询客户股东方
        List<CorpShareholderInfo> corpShareholderInfoList = corpShareholderInfoMapper.selectList(
                Wrappers.<CorpShareholderInfo>lambdaQuery().eq(ClientBaseModel::getClientId, client.getId())
        );
        if (CollectionUtil.isNotEmpty(corpShareholderInfoList)) {
            for (CorpShareholderInfo corpShareholderInfo : corpShareholderInfoList) {
                if (myShareholderList.contains(corpShareholderInfo.getShareholderName())) {
                    return corpShareholderInfo.getShareholderName();
                }
            }
        }
        return null;
    }
}
