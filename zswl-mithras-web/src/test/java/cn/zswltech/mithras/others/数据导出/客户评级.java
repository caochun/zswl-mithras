package cn.zswltech.mithras.others.数据导出;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.gruul.web.api.intercept.WhiteListUtil;
import cn.zswltech.mithras.dto.rating.RatingParamFieldApprovalRSP;
import cn.zswltech.mithras.dto.rating.RatingParamInfoDuoApprovalRSP;
import cn.zswltech.mithras.dto.rating.RatingParamInfoREQ;
import cn.zswltech.mithras.dto.rating.ratingclient.*;
import cn.zswltech.mithras.factory.model.RatingClientAreaIndicatorConfig;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.controller.client.CorpCommerceInfoController;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.client.IndustryType;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2025/4/29 11:34
 * @description
 */
public class 客户评级 extends ApplicationTest {

    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private ClientService clientService;
    @Resource
    private CorpCommerceInfoController corpCommerceInfoController;
    @Resource
    private CorpCommerceInfoService commerceInfoService;
    @Resource
    private IndustryTypeMapper industryTypeMapper;

    private static final Map<String, String> categoryNameMap = new HashMap<>();

    static {
        for (RatingClientAreaIndicatorConfig.DmIndicatorCode aEnum : RatingClientAreaIndicatorConfig.DmIndicatorCode.values()) {
            categoryNameMap.put(aEnum.getDisplay(), aEnum.name());
        }
    }

    @Test
    public void 客户评级() throws UnsupportedEncodingException, FileNotFoundException {
        // 白名单设置超级管理员
        WhiteListUtil.setWhiteListFlagHolder(Boolean.TRUE);
        RatingClientPageREQ req = new RatingClientPageREQ();
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        req.setRatingStatus(true);
        List<RatingClientPageRSP> list = ratingClientService.ratingClientPage(req).getList();
        // 五个大类
        Map<String, List<RatingClientPageRSP>> listMap = list.stream().collect(Collectors.groupingBy(RatingClientPageRSP::getModelName));
        // 分类组装数据
        List<RatingClientPageRSP> pageRSPS = listMap.get("政信主体区县级服务");
        extracted2(pageRSPS);

        List<RatingClientPageRSP> clientPageRSPS1 = listMap.get("政信主体地级市服务");
        extracted1(clientPageRSPS1);

        List<RatingClientPageRSP> clientPageRSPS = listMap.get("非制造业服务");
        extracted3(clientPageRSPS);

        List<RatingClientPageRSP> pageRSPList = listMap.get("制造业风控场景服务");
        extracted4(pageRSPList);

        List<RatingClientPageRSP> clientPageRSPS2 = listMap.get("新能源模型服务");
        extracted5(clientPageRSPS2);
    }

    @Test
    @SneakyThrows
    //导出，一类数据只可导出一批
    public void exportClient() {
        List<Client> list = clientService.list(Wrappers.<Client>lambdaQuery()
        .eq(Client::getClientType, ClientType.CORPORATION.name()));
        List<DataExcelModel> dataExcelModelList = new LinkedList<>();
        Map<Long, CorpCommerceInfo> collect = commerceInfoService.list().stream().collect(Collectors.toMap(CorpCommerceInfo::getClientId, e -> e, (a, b) -> b));

        for(Client client : list) {
            dataExcelModelList.add(cc(client, collect.get(client.getId())));
        }
        ExcelUtil.getWriter(true).write(dataExcelModelList.stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList()), true).flush(createFileOutputStream("/Users/vico/Documents/融租易拉取数据任务/客户风控行业分类.xlsx"));
    }

    /**
     * 创建文件输出流（自动创建目录）
     */
    private static FileOutputStream createFileOutputStream(String filePath) throws IOException {
        File file = new File(filePath);

        // 确保父目录存在
        File parentDir = file.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("无法创建目录: " + parentDir.getAbsolutePath());
        }

        // 创建文件输出流（覆盖模式）
        return new FileOutputStream(file);
    }

    private DataExcelModel cc (Client client, CorpCommerceInfo commerceInfo) {
        if(ObjectUtil.isEmpty(commerceInfo)) {
            return null;
        }
        DataExcelModel dataExcelModel = new DataExcelModel();
        dataExcelModel.setClientName(client.getClientName());
        dataExcelModel.setRiskControlIndustryClassify(Optional.ofNullable(RiskControlIndustryClassify.of(commerceInfo.getRiskControlIndustryClassify())).map(RiskControlIndustryClassify::display).orElse(null));

        // 找到行业分类及其父分类
        if (StrUtil.isNotBlank(commerceInfo.getIndustryType())) {
            int length = commerceInfo.getIndustryType().length();
            List<String> industryTypeCodes = new LinkedList<>();
            // code的父子关系呈现位数关系，eg: A A01 A011 A0111
            // 查询条件不特殊处理A0，不存在这样的数据，多带一个无效条件问题不大
            for (int i = length; i > 0; i--) {
                String s = commerceInfo.getIndustryType().substring(0, i);
                industryTypeCodes.add(s);
            }
            LambdaQueryWrapper<IndustryType> query = Wrappers.lambdaQuery();
            query.in(IndustryType::getCode, industryTypeCodes);
            List<IndustryType> industryTypeList = industryTypeMapper.selectList(query);
            industryTypeList.sort(Comparator.comparing(IndustryType::getLevel));
            List<String> result = new ArrayList<>(industryTypeList.size());
            StringBuilder builder = new StringBuilder();
            for (IndustryType industryType : industryTypeList) {
//                    if (Objects.equals(rsp.getIndustryType(), industryType.getCode())) {
//                        rsp.setIndustryTypeName(industryType.getDisplay());
//                    }
                if (builder.length() != 0){
                    builder.append("/");
                }
                builder.append(industryType.getDisplay());
                result.add(industryType.getCode());
            }
            dataExcelModel.setIndustryTypeName(builder.toString());
        }
        return dataExcelModel;
    }

    @Data
    public class DataExcelModel {
        private String clientName;
        private String riskControlIndustryClassify;
        private String industryTypeName;
    }


    private void extracted1(List<RatingClientPageRSP> pageRSPS) throws FileNotFoundException {
        ExcelWriter w = ExcelUtil.getWriter();
        w.writeHeadRow(ListUtil.of("模型名称", "客户名称", "客户编号", "评级结果", "发起时间", "发起机构", "发起人", "区域得分", "主体得分",
                "债券发行情况", "主体评级", "管理层级", "平台地位", "城投成色",
                "GDP（单位：亿元）",
                "GDP：二三产业占比(%)	",
                "城镇居民人均可支配收入（单位：元）",
                "房屋平均单价(元/㎡)（年末，新房）",
                "政府性基金收入（单位：亿元）",
                "财政收入占GDP比例",
                "一般公共预算收入（单位：亿元）",
                "税收收入占比(%)"	,
                "财政平衡性",
                "负债率",
                "债务率",
                "广义城投债务倍数",
                "政府透明度(%)"));
        List<List<Object>> list1 = new ArrayList<>();
        for (RatingClientPageRSP rsp : pageRSPS) {
            RatingParamInfoREQ req = new RatingParamInfoREQ();
            req.setId(rsp.getId());
            RatingParamInfoDuoApprovalRSP paramedInfo = ratingClientService.paramInfo(req);
            // 找详情
            RatingClientReportREQ req1 = new RatingClientReportREQ();
            req1.setId(rsp.getId());
            RatingClientReportRSP ratingClientReportRSP = ratingClientService.clientReport(req1);

//            // 区域评级里面还有主体的评级，需要挪过来
//            List<RatingQuantitativeRSP> collect = ratingClientReportRSP.getQuantitativeList().stream().filter(e ->!e.getIsAreaModelIndex())
//                    .collect(Collectors.toList());
//            ratingClientReportRSP.getQuantitativeList().removeIf(e -> !e.getIsAreaModelIndex());
//            collect.forEach(e -> {
//                RatingQualitativeRSP qualitativeRSP = new RatingQualitativeRSP();
//                qualitativeRSP.setFieldName(e.getFieldName());
//                qualitativeRSP.setValue(e.getValue());
//                qualitativeRSP.setFieldComment(e.getFieldComment());
//                qualitativeRSP.setFieldScore(e.getFieldScore());
//                ratingClientReportRSP.getQualitativeList().add(qualitativeRSP);
//            });

            List<Object> objects = new ArrayList<>();
            objects.add(rsp.getModelName());
            objects.add(rsp.getClientName());
            objects.add(rsp.getClientCode());
            objects.add(rsp.getFinalScore());
            objects.add(rsp.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objects.add(rsp.getStartOrg());
            objects.add(rsp.getCreateByName());
            objects.add(Optional.of(ratingClientReportRSP.getRatingScoreRSP()).map(RatingClientAbstractRSP::getAreaScore).orElse(null));
            objects.add(Optional.of(ratingClientReportRSP.getRatingScoreRSP()).map(RatingClientAbstractRSP::getSubjectScore).orElse(null));
            String[] tmpList = new String[]{"债券发行情况", "主体评级", "管理层级", "平台地位", "城投成色"};
            for (String s : tmpList) {
                List<RatingQualitativeRSP> collected = ratingClientReportRSP.getQualitativeList()
                        .stream().filter(a -> a.getFieldComment().contains(s)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collected)) {
                    objects.add(collected.get(0).getValue());
                }else {
                    objects.add(null);
                }
            }

            String[] tmpList1 = new String[]{
                    "GDP（单位：亿元）",
                    "GDP：二三产业占比(%)",
                    "城镇居民人均可支配收入（单位：元）",
                    "房屋平均单价(元/㎡)（年末，新房）",
                    "政府性基金收入（单位：亿元）",
                    "财政收入占GDP比例",
                    "一般公共预算收入（单位：亿元）",
                    "税收收入占比(%)"	,
                    "财政平衡性",
                    "负债率",
                    "债务率",
                    "广义城投债务倍数",
                    "政府透明度(%)"};
            List<RatingParamFieldApprovalRSP> 系统取数指标 = new ArrayList<>();
            Map<String, List<RatingParamFieldApprovalRSP>> 定量指标 = paramedInfo.getInfo().get("定量指标");
            if (CollUtil.isNotEmpty(定量指标)){
                系统取数指标 = 定量指标.get("系统取数指标");
            }
            for (String s : tmpList1) {
                RatingParamFieldApprovalRSP ratingParamFieldApprovalRSP = 系统取数指标.stream()
                        .filter(x -> x.getFieldComment().equals(s)).findFirst().orElse(null);
                if (Objects.nonNull(ratingParamFieldApprovalRSP)) {
                    objects.add(ratingParamFieldApprovalRSP.getFieldValue());
                }else {
                    objects.add(null);
                }
            }
            if (CollUtil.isNotEmpty(objects)) {
                list1.add(objects);
            }
        }
        File file = new File("/Users/bigbear/Documents/" + "政信主体地级市服务" + ".xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        w.write(list1);
        w.flush(fileOutputStream, true);
    }

    private void extracted2(List<RatingClientPageRSP> pageRSPS) throws FileNotFoundException {
        ExcelWriter w = ExcelUtil.getWriter();
        w.writeHeadRow(ListUtil.of("模型名称", "客户名称", "客户编号", "评级结果", "发起时间", "发起机构", "发起人", "区域得分", "主体得分",
                "债券发行情况", "主体评级", "管理层级", "平台地位", "城投成色",
                "负债率", "城镇居民人均可支配收入（单位：元）", "广义城投债务倍数", "税收收入占比(%)", "政府性基金收入(亿元)", "政府透明度(%)", "GDP（单位：亿元）", "人口同比变化(%)", "财政平衡性", "房屋平均单价(元/㎡) （年末，二手房）", "一般公共预算收入(亿元)", "债务率", "GDP：第二产业占比(%)"));
        List<List<Object>> list1 = new ArrayList<>();
        for (RatingClientPageRSP rsp : pageRSPS) {
            // 找详情
            RatingClientReportREQ req1 = new RatingClientReportREQ();
            req1.setId(rsp.getId());
            RatingClientReportRSP ratingClientReportRSP = ratingClientService.clientReport(req1);

//            // 区域评级里面还有主体的评级，需要挪过来
//            List<RatingQuantitativeRSP> collect = ratingClientReportRSP.getQuantitativeList().stream().filter(e ->!e.getIsAreaModelIndex())
//                    .collect(Collectors.toList());
//            ratingClientReportRSP.getQuantitativeList().removeIf(e -> !e.getIsAreaModelIndex());
//            collect.forEach(e -> {
//                RatingQualitativeRSP qualitativeRSP = new RatingQualitativeRSP();
//                qualitativeRSP.setFieldName(e.getFieldName());
//                qualitativeRSP.setValue(e.getValue());
//                qualitativeRSP.setFieldComment(e.getFieldComment());
//                qualitativeRSP.setFieldScore(e.getFieldScore());
//                ratingClientReportRSP.getQualitativeList().add(qualitativeRSP);
//            });

            List<Object> objects = new ArrayList<>();
            objects.add(rsp.getModelName());
            objects.add(rsp.getClientName());
            objects.add(rsp.getClientCode());
            objects.add(rsp.getFinalScore());
            objects.add(rsp.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objects.add(rsp.getStartOrg());
            objects.add(rsp.getCreateByName());
            objects.add(Optional.of(ratingClientReportRSP.getRatingScoreRSP()).map(RatingClientAbstractRSP::getAreaScore).orElse(null));
            objects.add(Optional.of(ratingClientReportRSP.getRatingScoreRSP()).map(RatingClientAbstractRSP::getSubjectScore).orElse(null));

            String[] tmpList = new String[]{"债券发行情况", "主体评级", "管理层级", "平台地位", "城投成色"};
            ratingClientReportRSP.getQualitativeList().removeIf(a -> !ListUtil.of(tmpList).contains(a.getFieldComment()));
            for (String s : tmpList) {
                List<RatingQualitativeRSP> collected = ratingClientReportRSP.getQualitativeList()
                        .stream().filter(a -> a.getFieldComment().contains(s)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collected)) {
                    objects.add(collected.get(0).getValue());
                }
            }

            String[] tmpList1 = new String[]{"负债率", "城镇居民人均可支配收入（单位：元）", "广义城投债务倍数", "税收收入占比(%)", "政府性基金收入（单位：亿元）",
                    "政府透明度(%)", "GDP（单位：亿元）", "人口同比变化(%)", "财政平衡性", "房屋平均单价(元/㎡)（年末，二手房）", "一般公共预算收入（单位：亿元）", "债务率",
                    "GDP：第二产业占比(%)"};
            // 删除不在上面的
            ratingClientReportRSP.getQuantitativeList().removeIf(e -> !ListUtil.of(tmpList1).contains(e.getFieldComment()));

            RatingParamInfoREQ req = new RatingParamInfoREQ();
            req.setId(rsp.getId());
            RatingParamInfoDuoApprovalRSP paramedInfo = ratingClientService.paramInfo(req);
            List<RatingParamFieldApprovalRSP> 系统取数指标 = new ArrayList<>();
            Map<String, List<RatingParamFieldApprovalRSP>> 定量指标 = paramedInfo.getInfo().get("定量指标");
            if (CollUtil.isNotEmpty(定量指标)){
                系统取数指标 = 定量指标.get("系统取数指标");
            }
            for (String s : tmpList1) {
                RatingParamFieldApprovalRSP ratingParamFieldApprovalRSP = 系统取数指标.stream()
                        .filter(x -> x.getFieldComment().equals(s)).findFirst().orElse(null);
                if (Objects.nonNull(ratingParamFieldApprovalRSP)) {
                    objects.add(ratingParamFieldApprovalRSP.getFieldValue());
                }else {
                    objects.add(null);
                }
            }
            if (CollUtil.isNotEmpty(objects)) {
                list1.add(objects);
            }
        }
        File file = new File("/Users/bigbear/Documents/" + "政信主体区县级服务" + ".xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        w.write(list1);
        w.flush(fileOutputStream, true);
    }

    private void extracted3(List<RatingClientPageRSP> pageRSPS) throws FileNotFoundException {
        ExcelWriter w = ExcelUtil.getWriter();
        w.writeHeadRow(ListUtil.of("模型名称", "客户名称", "客户编号", "评级结果", "发起时间", "发起机构", "发起人", "定性得分", "定量得分",
                "资产负债率",
                "应收账款周转率",
                "权益收益率",
                "速动比率",
                "利息保障倍数",
                "净利润",
                "资产总计",
                "营业收入",
                "经营活动产生的现金流量净额与负债比率",
                "净借款权益比2",
                "净利润增长率",
                "EBIT短期借款比",
                "权益增长率",
                "融资能力",
                "会计师事务所的品牌",
                "实控人经验",
                "核心管理团队的稳定性",
                "产品或服务优势",
                "上游客户议价能力",
                "企业性质",
                "对外（含并表范围）担保占净资产的比例",
                "区域风险",
                "国家政策与地方政府对行业支持力度",
                "诉讼和法律纠纷",
                "监管处罚情况"
        ));
        List<List<Object>> list1 = new ArrayList<>();
        for (RatingClientPageRSP rsp : pageRSPS) {
            // 找详情
            RatingClientReportREQ req1 = new RatingClientReportREQ();
            req1.setId(rsp.getId());
            RatingClientReportRSP ratingClientReportRSP = ratingClientService.clientReport(req1);


            List<Object> objects = new ArrayList<>();
            objects.add(rsp.getModelName());
            objects.add(rsp.getClientName());
            objects.add(rsp.getClientCode());
            objects.add(rsp.getFinalScore());
            objects.add(rsp.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objects.add(rsp.getStartOrg());
            objects.add(rsp.getCreateByName());
            objects.add(Optional.of(ratingClientReportRSP.getRatingScoreRSP()).map(RatingClientAbstractRSP::getQualitativeScore).orElse(null));
            objects.add(Optional.of(ratingClientReportRSP.getRatingScoreRSP()).map(RatingClientAbstractRSP::getQuantitativeScore).orElse(null));

            String[] tmpList = new String[]{"资产负债率",
                    "应收账款周转率",
                    "权益收益率",
                    "速动比率",
                    "利息保障倍数",
                    "净利润",
                    "资产总计",
                    "营业收入",
                    "经营活动产生的现金流量净额与负债比率",
                    "净借款权益比2",
                    "净利润增长率",
                    "EBIT短期借款比",
                    "权益增长率"};
            for (String s : tmpList) {
                List<RatingQuantitativeRSP> collected = ratingClientReportRSP.getQuantitativeList()
                        .stream().filter(a -> a.getFieldComment().contains(s)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collected)) {
                    objects.add(collected.get(0).getValue());
                }else {
                    objects.add(null);
                }
            }

            String[] tmpList1 = new String[]{"融资能力",
                    "会计师事务所的品牌",
                    "实控人经验",
                    "核心管理团队的稳定性",
                    "产品或服务优势",
                    "上游客户议价能力",
                    "企业性质",
                    "对外（含并表范围）担保占净资产的比例",
                    "区域风险",
                    "国家政策与地方政府对行业支持力度",
                    "诉讼和法律纠纷",
                    "监管处罚情况"};
            for (String s : tmpList1) {
                List<RatingQualitativeRSP> collect1 = ratingClientReportRSP.getQualitativeList()
                        .stream().filter(a -> a.getFieldComment().contains(s)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect1)) {
                    objects.add(collect1.get(0).getValue());
                }else {
                    objects.add(null);
                }
            }
            if (CollUtil.isNotEmpty(objects)) {
                list1.add(objects);
            }
        }
        File file = new File("/Users/bigbear/Documents/" + "非制造业服务" + ".xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        w.write(list1);
        w.flush(fileOutputStream, true);
    }

    private void extracted4(List<RatingClientPageRSP> pageRSPS) throws FileNotFoundException {
        List<List<Object>> list1 = new ArrayList<>();
        for (RatingClientPageRSP rsp : pageRSPS) {
            // 找详情
            RatingClientReportREQ req1 = new RatingClientReportREQ();
            req1.setId(rsp.getId());
            RatingClientReportRSP ratingClientReportRSP = ratingClientService.clientReport(req1);
            ExcelWriter w = ExcelUtil.getWriter();
            w.writeHeadRow(ListUtil.of("模型名称", "客户名称", "客户编号", "评级结果", "发起时间", "发起机构", "发起人", "定性得分", "定量得分",
                    "资产负债率",
                    "应收账款周转率",
                    "权益收益率",
                    "速动比率",
                    "流动比率",
                    "净利润",
                    "存货周转率",
                    "资产总计",
                    "营业收入",
                    "经营活动产生的现金流量净额与负债比率",
                    "净借款权益比2",
                    "毛利率",
                    "EBIT短期借款比",
                    "权益增长率",
                    "融资能力",
                    "会计师事务所的品牌",
                    "实控人经验",
                    "核心管理团队的稳定性",
                    "市场占有率",
                    "产品或服务优势",
                    "上游客户议价能力",
                    "企业性质",
                    "企业开始取得营业收入至今的运营年限",
                    "区域风险",
                    "国家政策与地方政府对行业支持力度",
                    "诉讼和法律纠纷",
                    "监管处罚情况"
            ));

            List<Object> objects = new ArrayList<>();
            objects.add(rsp.getModelName());
            objects.add(rsp.getClientName());
            objects.add(rsp.getClientCode());
            objects.add(rsp.getFinalScore());
            objects.add(rsp.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objects.add(rsp.getStartOrg());
            objects.add(rsp.getCreateByName());
            objects.add(Optional.of(ratingClientReportRSP.getRatingScoreRSP()).map(RatingClientAbstractRSP::getQualitativeScore).orElse(null));
            objects.add(Optional.of(ratingClientReportRSP.getRatingScoreRSP()).map(RatingClientAbstractRSP::getQuantitativeScore).orElse(null));

            String[] tmpList = new String[]{"资产负债率",
                    "应收账款周转率",
                    "权益收益率",
                    "速动比率",
                    "流动比率",
                    "净利润",
                    "存货周转率",
                    "资产总计",
                    "营业收入",
                    "经营活动产生的现金流量净额与负债比率",
                    "净借款权益比2",
                    "毛利率",
                    "EBIT短期借款比",
                    "权益增长率"};
            for (String s : tmpList) {
                List<RatingQuantitativeRSP> collected = ratingClientReportRSP.getQuantitativeList()
                        .stream().filter(a -> a.getFieldComment().contains(s)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collected)) {
                    objects.add(collected.get(0).getValue());
                }else {
                    objects.add(null);
                }
            }

            String[] tmpList1 = new String[]{"融资能力",
                    "会计师事务所的品牌",
                    "实控人经验",
                    "核心管理团队的稳定性",
                    "市场占有率",
                    "产品或服务优势",
                    "上游客户议价能力",
                    "企业性质",
                    "企业开始取得营业收入至今的运营年限",
                    "区域风险",
                    "国家政策与地方政府对行业支持力度",
                    "诉讼和法律纠纷",
                    "监管处罚情况"};
            for (String s : tmpList1) {
                List<RatingQualitativeRSP> collect1 = ratingClientReportRSP.getQualitativeList()
                        .stream().filter(a -> a.getFieldComment().contains(s)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect1)) {
                    objects.add(collect1.get(0).getValue());
                }else {
                    objects.add(null);
                }
            }

            if (CollUtil.isNotEmpty(objects)) {
                list1.add(objects);
            }
            File file = new File("/Users/bigbear/Documents/" + "制造业风控场景服务" + ".xlsx");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            w.write(list1);
            w.flush(fileOutputStream, true);
        }
    }

    private void extracted5(List<RatingClientPageRSP> pageRSPS) throws FileNotFoundException {
        List<List<Object>> list1 = new ArrayList<>();
        for (RatingClientPageRSP rsp : pageRSPS) {
            // 找详情
            RatingClientReportREQ req1 = new RatingClientReportREQ();
            req1.setId(rsp.getId());
            RatingClientReportRSP ratingClientReportRSP = ratingClientService.clientReport(req1);
            ExcelWriter w = ExcelUtil.getWriter();
            w.writeHeadRow(ListUtil.of("模型名称", "客户名称", "客户编号", "评级结果", "发起时间", "发起机构", "发起人", "定性得分", "定量得分",
                    "租金覆盖度",
                    "偿债保障比率",
                    "自有资本金到位情况",
                    "自有资本金占比",
                    "并网情况",
                    "装机容量",
                    "发电小时数情况",
                    "自用电消纳率",
                    "基础设备情况",
                    "业主单位背景",
                    "实际控制人背景",
                    "实际控制人行业经验",
                    "实际控制人运营能力",
                    "实控人信用记录",
                    "企业信用记录",
                    "区域风险",
                    "地方政府对行业支持情况"
            ));
            w.writeSecHeadRow(ListUtil.of("模型名称", "客户名称", "客户编号", "评级结果", "发起时间", "发起机构", "发起人", "定性得分", "定量得分",
                    "租金覆盖度",
                    "偿债保障比率",
                    "自有资本金到位情况",
                    "自有资本金占比",
                    "并网情况",
                    "装机容量",
                    "发电小时数情况",
                    "自用电消纳率",
                    "基础设备情况",
                    "业主单位背景",
                    "实际控制人背景",
                    "实际控制人行业经验",
                    "实际控制人运营能力",
                    "实控人信用记录",
                    "企业信用记录",
                    "区域风险",
                    "地方政府对行业支持情况"
            ));

            List<Object> objects = new ArrayList<>();
            objects.add(rsp.getModelName());
            objects.add(rsp.getClientName());
            objects.add(rsp.getClientCode());
            objects.add(rsp.getFinalScore());
            objects.add(rsp.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            objects.add(rsp.getStartOrg());
            objects.add(rsp.getCreateByName());
            objects.add(Optional.of(ratingClientReportRSP.getRatingScoreRSP()).map(RatingClientAbstractRSP::getQualitativeScore).orElse(null));
            objects.add(Optional.of(ratingClientReportRSP.getRatingScoreRSP()).map(RatingClientAbstractRSP::getQuantitativeScore).orElse(null));

            String[] tmpList = new String[]{
                    "租金覆盖度",
                    "偿债保障比率",
                    "自有资本金到位情况",
                    "自有资本金占比"};
            for (String s : tmpList) {
                List<RatingQuantitativeRSP> collected = ratingClientReportRSP.getQuantitativeList()
                        .stream().filter(a -> a.getFieldComment().contains(s)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collected)) {
                    objects.add(collected.get(0).getValue());
                }else {
                    objects.add(null);
                }
            }

            String[] tmpList1 = new String[]{"并网情况",
                    "装机容量",
                    "发电小时数情况",
                    "自用电消纳率",
                    "基础设备情况",
                    "业主单位背景",
                    "实际控制人背景",
                    "实际控制人行业经验",
                    "实际控制人运营能力",
                    "实控人信用记录",
                    "企业信用记录",
                    "区域风险",
                    "地方政府对行业支持情况"};
            for (String s : tmpList1) {
                List<RatingQualitativeRSP> collect1 = ratingClientReportRSP.getQualitativeList()
                        .stream().filter(a -> a.getFieldComment().contains(s)).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect1)) {
                    objects.add(collect1.get(0).getValue());
                }else {
                    objects.add(null);
                }
            }
            if (CollUtil.isNotEmpty(objects)) {
                list1.add(objects);
            }
            File file = new File("/Users/bigbear/Documents/" + "新能源模型服务" + ".xlsx");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            w.write(list1);
            w.flush(fileOutputStream, true);
        }
    }
}
