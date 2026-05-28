package cn.zswltech.mithras.service.service.email;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.afterlease.RentCollectionBaseInfo;
import cn.zswltech.mithras.service.enums.EmailType;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.gendoc.render.overduecollect.CollectionLetterRender;
import cn.zswltech.mithras.service.mapper.email.EmailSendFailLogMapper;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataBankAccount;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.overdue.domain.acl.ContractLesseeInfo;
import cn.zswltech.mithras.service.overdue.domain.collection.CollectLetterCode;
import cn.zswltech.mithras.service.overdue.domain.collection.CollectionRepository;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import groovy.util.logging.Slf4j;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum.CARRY_INTEREST;
import static cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum.EFFECT;
import static cn.zswltech.mithras.service.job.NextMonthRentNotify.FinancingType.indirect;

/**
 * @description: 邮件类
 * @author: ylzhang5
 * @date: 2025/12/01
 * @version: 1.0
 */
@Slf4j
@Service
public class CollectionRentEmailHandler extends AbstractSendEmailHandler<RentCollectionBaseInfo>{

    private static final Logger log = LoggerFactory.getLogger(AbstractSendEmailHandler.class);
    public static final String COLLECTION_DOCX_NAME = "/doc/催收函模版-新.docx";//文档模板路径
    private final static String title = "租金逾期催收通知";
    private File[] files;

    @Resource
    private CollectionRepository collectionRepository;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    protected String getSystemUrl() {
        return "";
    }

    @Override
    protected String generateEmailTitle() {
        return title;
    }

    @Override
    protected String generateTable(RentCollectionBaseInfo businessData) {
        return "";
    }

    /**
     * 是否html格式
     */
    @Override
    protected Boolean getIsHtml() {
        return Boolean.TRUE;
    }

    /**
     * 生成附件
     * @param businessData
     * @return
     */
    @Override
    public File[] getFiles(RentCollectionBaseInfo businessData) {
        List<File> fileList = new ArrayList<>();
        List<Long> contractIds;
        String contractCode = "";//合同编号
        try{
            File file = null;
            String phase = String.valueOf(businessData.getPhase());//期项
            LocalDate now = LocalDate.now();
            contractIds = new ArrayList<>();
            contractIds.add(businessData.getContractId());
            contractCode = businessData.getContractCode();
            Integer collectLetterIndex = collectionRepository.findCollectLetterIndex(now.getYear());
            // 生成初始催收函编号
            CollectLetterCode collectLetterCode = new CollectLetterCode(now.getYear(),collectLetterIndex);
            Map<String, List<ContractLesseeInfo>> lesseeInfos =
                    contractBaseInfoService.getContractLessees1(contractIds).stream()
                            .collect(Collectors.groupingBy(ContractLesseeInfo::getContractCode));
            // 承租人生成催收函
            if(lesseeInfos.isEmpty()){
                this.files = null;
            }else{
                //适用收款明细ID筛选，避免合同下多笔借据情况
                List<ContractLesseeInfo> lesseeInfos1 = lesseeInfos.get(contractCode).stream().filter(w-> Objects.equals(w.getCollectionId(), businessData.getCollectionId())).collect(Collectors.toList());
                String lesseeName = "";//承租人姓名
                for (ContractLesseeInfo contractLesseeInfo : lesseeInfos1) {
                    lesseeName = lesseeName + "、" + contractLesseeInfo.getLesseeName();
                }
                lesseeName = lesseeName.substring(1);
                ContractLesseeInfo contractLesseeInfo = lesseeInfos1.get(0);
                contractLesseeInfo.setLetterCode(collectLetterCode.getCode());
                contractLesseeInfo.setGenDate(now);
                contractLesseeInfo.setLesseeName(lesseeName);
                contractLesseeInfo.setPhases(phase);
                try{
                    file = render(contractLesseeInfo);
                }catch (Exception e){
                    log.error("生成催收函发生未知异常[{}]", contractLesseeInfo.getContractCode());
                    throw new MithrasException("生成催收函发生未知异常");
                }
                fileList.add(file);
            }
        }catch(Exception e){
            log.error("合同"+businessData.getContractId()+"的附件生成失败！"+e);
        }
        return fileList.toArray(new File[0]);
    }

    @Override
    public boolean needHandle(EmailType emailType) {
        return false;
    }

    @Override
    public EmailType getEmailType() {
        return EmailType.COLLECTIO_NRENT_NOTICE;
    }

    @Override
    public String generateEmailHtml(String table, String systemUrl, RentCollectionBaseInfo businessData) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>租金逾期催收通知</title>\n" +
                "    <style>\n" +
                "        /* 基础样式重置 */\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        body {\n" +
                "            font-family: 'PingFang SC', 'Microsoft YaHei', Helvetica, Arial, sans-serif;\n" +
                "            background: linear-gradient(180deg, #f8fafc 0%, #eef2f7 100%);\n" +
                "            color: #334155;\n" +
                "            line-height: 1.8;\n" +
                "            padding: 40px 0;\n" +
                "        }\n" +
                "        /* 核心通知容器 - 增强质感 */\n" +
                "        .notice-wrapper {\n" +
                "            max-width: 850px;\n" +
                "            margin: 0 auto;\n" +
                "            background: #fff;\n" +
                "            padding: 50px;\n" +
                "            border-radius: 16px;\n" +
                "            box-shadow: 0 8px 32px rgba(149, 157, 165, 0.1);\n" +
                "            border: 1px solid #f0f4f9;\n" +
                "            position: relative;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        /* 顶部品牌装饰条 */\n" +
                "        .notice-wrapper::before {\n" +
                "            content: '';\n" +
                "            position: absolute;\n" +
                "            top: 0;\n" +
                "            left: 0;\n" +
                "            width: 100%;\n" +
                "            height: 6px;\n" +
                "            background: linear-gradient(90deg, #1890ff, #4299e1);\n" +
                "        }\n" +
                "        /* 标题样式 - 优化排版 */\n" +
                "        .notice-title {\n" +
                "            font-size: 24px;\n" +
                "            color: #1e293b;\n" +
                "            border-bottom: 2px solid #e8f4ff;\n" +
                "            padding-bottom: 20px;\n" +
                "            margin-bottom: 35px;\n" +
                "            font-weight: 700;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "        }\n" +
                "        .notice-title::before {\n" +
                "            content: '';\n" +
                "            display: inline-block;\n" +
                "            width: 4px;\n" +
                "            height: 20px;\n" +
                "            background: #1890ff;\n" +
                "            margin-right: 12px;\n" +
                "            border-radius: 2px;\n" +
                "        }\n" +
                "        /* 正文样式 - 提升阅读体验 */\n" +
                "        .notice-content {\n" +
                "            font-size: 16px;\n" +
                "            color: #475569;\n" +
                "            letter-spacing: 0.5px;\n" +
                "        }\n" +
                "        .notice-content p {\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        /* 关键信息高亮 - 优化视觉层次 */\n" +
                "        .key-info {\n" +
                "            color: #1890ff;\n" +
                "            font-weight: 600;\n" +
                "            padding: 2px 8px;\n" +
                "            background: #f0f8fb;\n" +
                "            border-radius: 4px;\n" +
                "            margin: 0 2px;\n" +
                "        }\n" +
                "        /* 附件提示 - 增强视觉区分 */\n" +
                "        .attachment-tip {\n" +
                "            margin-top: 25px;\n" +
                "            padding: 16px 20px;\n" +
                "            background-color: #f5fafe;\n" +
                "            border-left: 4px solid #4299e1;\n" +
                "            font-size: 15px;\n" +
                "            color: #27374d;\n" +
                "            border-radius: 8px;\n" +
                "            display: flex;\n" +
                "            align-items: flex-start;\n" +
                "        }\n" +
                "        .attachment-tip strong {\n" +
                "            color: #1e293b;\n" +
                "            margin-right: 8px;\n" +
                "        }\n" +
                "        .attachment-tip::before {\n" +
                "            content: '\uD83D\uDCA1';\n" +
                "            font-size: 18px;\n" +
                "            margin-right: 10px;\n" +
                "            margin-top: 2px;\n" +
                "        }\n" +
                "        /* 响应式适配 - 移动端优化 */\n" +
                "        @media (max-width: 768px) {\n" +
                "            body {\n" +
                "                padding: 20px 0;\n" +
                "            }\n" +
                "            .notice-wrapper {\n" +
                "                margin: 0 15px;\n" +
                "                padding: 30px 20px;\n" +
                "                border-radius: 12px;\n" +
                "            }\n" +
                "            .notice-title {\n" +
                "                font-size: 20px;\n" +
                "                margin-bottom: 25px;\n" +
                "                padding-bottom: 15px;\n" +
                "            }\n" +
                "            .notice-content {\n" +
                "                font-size: 15px;\n" +
                "            }\n" +
                "            .attachment-tip {\n" +
                "                padding: 14px 16px;\n" +
                "                font-size: 14px;\n" +
                "            }\n" +
                "        }\n" +
                "        /* 打印样式适配 */\n" +
                "        @media print {\n" +
                "            body {\n" +
                "                background: #fff;\n" +
                "                padding: 0;\n" +
                "            }\n" +
                "            .notice-wrapper {\n" +
                "                box-shadow: none;\n" +
                "                border: none;\n" +
                "                padding: 0;\n" +
                "            }\n" +
                "            .notice-wrapper::before {\n" +
                "                display: none;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"notice-wrapper\">\n" +
                "        \n" +
                "        <!-- 核心正文 -->\n" +
                "        <div class=\"notice-content\">\n" +
                "            <p>致<span class=\"key-info\">"+businessData.getLesseeName()+"</span>：</p>\n" +
                "            <p>您好！</p>\n" +
                "            <p>贵方与我司签订的融资租赁合同<span class=\"key-info\">"+businessData.getContractCode()+"</span>项下第<span class=\"key-info\">"+businessData.getPhase()+"</span>期租金已于<span class=\"key-info\">"+businessData.getPlanYear()+"年"+businessData.getPlanMonth()+"月"+businessData.getPlanDay()+"日</span>到期，我方尚未收到该期租金，已构成逾期。具体信息详见附件。</p>\n" +
                "        </div>\n" +
                "\n" +
                "         </div>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * 生成催收函
     * @return File
     */
    public File render(ContractLesseeInfo contractLesseeInfo) throws Exception{

        Map<String, Object> renderMap = new HashMap<>(64);
        // 填充所需数据
        renderMap.put("letterCode", contractLesseeInfo.getLetterCode());
        renderMap.put("lesseeName",contractLesseeInfo.getLesseeName());
        renderMap.put("address",contractLesseeInfo.getAddress());
        renderMap.put("contractCode",contractLesseeInfo.getContractCode());
        if (ObjectUtil.isNotEmpty(contractLesseeInfo.getPhases())) {
            String phases = Arrays.stream(contractLesseeInfo.getPhases().split(","))
                    .filter(ObjectUtil::isNotEmpty).map(Integer::valueOf)
                    .sorted().map(String::valueOf).collect(Collectors.joining(","));
            renderMap.put("phases", phases);
        } else {
            renderMap.put("phases", "");
        }
        LocalDate genDate = contractLesseeInfo.getGenDate();
        renderMap.put("year",genDate.getYear());
        renderMap.put("month",genDate.getMonthValue());
        renderMap.put("day",genDate.getDayOfMonth());

        BigDecimal overdueAmount;
        BigDecimal lateCharge;
        if (ObjectUtil.isNotEmpty(contractLesseeInfo.getOverdueAmount())) {
            overdueAmount = BigDecimal.valueOf(contractLesseeInfo.getOverdueAmount()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP);
        } else {
            overdueAmount = BigDecimal.ZERO;
        }
        if (ObjectUtil.isNotEmpty(contractLesseeInfo.getLateCharge())) {
            lateCharge = BigDecimal.valueOf(contractLesseeInfo.getLateCharge()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP);
        } else {
            lateCharge = BigDecimal.ZERO;
        }
        renderMap.put("lateCharge", lateCharge.toString());
        renderMap.put("overdueAmount", overdueAmount.toString());
        renderMap.put("totalAmount", overdueAmount.add(lateCharge).toString());
        renderMap.put("projectSponsorName",contractLesseeInfo.getProjectSponsorName());
        renderMap.put("projectSponsorPhone",contractLesseeInfo.getProjectSponsorPhone());
        renderMap.put("overdueDays", contractLesseeInfo.getOverdueDays());
        //合同融资关联
        Map<Long, PledgeInfo> contractPledgeMap = contractBankMapByFinancing(contractLesseeInfo.getId());
        Map<Long, PledgeInfo> directContractPledgeMap = contractBankMapByDirectFinancing(contractLesseeInfo.getId());
        //默认银行账户
        BaseDataBankAccount defaultBank = defaultBank();
        PledgeInfo pledgeInfo = this.ensurePledgeInfo(contractPledgeMap.get(contractLesseeInfo.getId()), directContractPledgeMap.get(contractLesseeInfo.getId()));
        //如果不存在关联的融资信息，使用默认的银行信息
        if (pledgeInfo == null) {
            renderMap.put("accountBank", defaultBank.getAccountBank());
            renderMap.put("accountNumber", defaultBank.getAccountNumber());
        }else{
            renderMap.put("accountBank", pledgeInfo.getBankName());
            renderMap.put("accountNumber", pledgeInfo.getAccountNumber());
        }

        // 渲染
        XWPFTemplate template = XWPFTemplate.compile(Objects.requireNonNull(CollectionLetterRender.class.getResourceAsStream(COLLECTION_DOCX_NAME))).render(renderMap);
        NiceXWPFDocument niceXWPFDocument = template.getXWPFDocument();
        File tempFile = File.createTempFile("催收函",".docx");
        tempFile.deleteOnExit();
        // 2. 定义无随机数的目标文件名（自定义规则）
        String customFileName = contractLesseeInfo.getContractCode()+contractLesseeInfo.getLesseeName().split("、")[0]+"催收函.docx"; // 无随机数
        // 目标路径：和原临时文件同目录 + 自定义文件名
        File customTempFile = new File(tempFile.getParent(), customFileName);
        // 3. 确保目标文件不存在（避免覆盖），然后重命名
        if (customTempFile.exists()) {
            // 可选：删除已有文件，或抛异常（根据业务）
            customTempFile.delete();
        }
        boolean renamed = tempFile.renameTo(customTempFile);
        if (!renamed) {
            throw new MithrasException("文件重命名失败");
        }
        // 4. 给新文件添加「退出时删除」标记（原标记随原文件失效）
        customTempFile.deleteOnExit();
        try(FileOutputStream outputStream = new FileOutputStream(customTempFile)){
            niceXWPFDocument.write(outputStream);
        }finally {
            niceXWPFDocument.close();
        }
        return customTempFile;
    }

    /**
     * 批量查询合同的融资关联情况
     * 只查询融资未到期的
     *
     * @param contractId
     * @return
     */
    Map<Long, PledgeInfo> contractBankMapByFinancing(Long contractId) {
        if (contractId!=null) {
            //间接融资
            List<FundFinancingPledgeInfo> pledgeInfoList = getBean(FundFinancingPledgeInfoService.class).list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                    .eq(FundFinancingPledgeInfo::getContractId, contractId)
            );
            if (!pledgeInfoList.isEmpty()) {
                List<FundFinancingBaseInfo> fundFinancingBaseInfos = getBean(FundFinancingBaseInfoService.class).listByIds(
                        pledgeInfoList.stream().map(FundFinancingPledgeInfo::getFinancingId).collect(Collectors.toList()));
                //过滤，有效的融资记录
                List<Long> financingIds = fundFinancingBaseInfos.stream()
                        .filter(e -> equalsAny(e.getFinancingStatus(), EFFECT.name(), CARRY_INTEREST.name()))
                        .map(FundFinancingBaseInfo::getId).collect(Collectors.toList());
                //借据
                if (!financingIds.isEmpty()) {
                    List<Long> receiptRepayIds = getBean(FundReceiptRepayBaseInfoService.class).list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                            .in(FundReceiptRepayBaseInfo::getFinancingId, financingIds)
                            .isNull(FundReceiptRepayBaseInfo::getFinancingType)
                    ).stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
                    if (!receiptRepayIds.isEmpty()) {
                        //现金流，继续过滤，融资未到期的记录，
                        QueryWrapper<FundReceiptRepayCashFlow> wrapper = new QueryWrapper<>();
                        wrapper.in("receipt_repay_id", receiptRepayIds);
                        wrapper.groupBy("financing_id");
                        wrapper.select("max(repay_date) as repay_date, financing_id");
                        Map<Long, LocalDate> financingMapRepayDate = getBean(FundReceiptRepayCashFlowService.class).list(wrapper)
                                .stream()
                                .filter(e -> !e.getRepayDate().isBefore(LocalDate.now()))
                                .collect(Collectors.toMap(FundReceiptRepayCashFlow::getFinancingId, FundReceiptRepayCashFlow::getRepayDate));
                        //最终过滤 这里需要判断优先取监管户
                        Map<Long, PledgeInfo> contractBankMap = pledgeInfoList.stream()
                                .filter(e -> financingMapRepayDate.containsKey(e.getFinancingId()))
                                // 在这里就要判断监管户了
                                .collect(Collectors.groupingBy(FundFinancingPledgeInfo::getContractId))
                                .entrySet().stream().map(e -> {
                                    List<FundFinancingPledgeInfo> value = e.getValue();
                                    if (CollUtil.isNotEmpty(value) && value.size() > 1) {
                                        // 尝试寻找监管户，没有的话，就随机取一个
                                        long count = value.stream().filter(a -> Objects.equals(a.getIsSupervise(), Boolean.TRUE)).count();
                                        if (count > 0) {
                                            return value.stream().filter(a -> Objects.equals(a.getIsSupervise(), Boolean.TRUE)).findFirst().get();
                                        } else {
                                            return value.get(0);
                                        }
                                    } else {
                                        return value.get(0);
                                    }
                                }).map(e -> new PledgeInfo().setPledgeId(e.getId())
                                        .setFinancingType(indirect.name())
                                        .setBankName(e.getAccountBank())
                                        .setAccountName(e.getAccountName())
                                        .setContractId(e.getContractId())
                                        .setAccountNumber(e.getAccountNumber())
                                        .setIsSupervise(Objects.equals(e.getIsSupervise(), Boolean.TRUE) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode()))
                                .collect(Collectors.toMap(e -> e.contractId, e -> e, (v1, v2) -> v1));
                        return contractBankMap;
                    }
                }
            }
        }
        return MapUtil.empty();
    }

    /**
     * 查询合同的融资关联情况（直融）
     * 只查询融资未到期的
     *
     * @param contractId
     * @return
     */
    Map<Long, PledgeInfo> contractBankMapByDirectFinancing(Long contractId) {
        if (contractId!=null) {
            //直接融资
            List<FundDirectFinancingPledgeInfo> pledgeInfoList = getBean(FundDirectFinancingPledgeInfoService.class).list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                    .eq(FundDirectFinancingPledgeInfo::getContractId, contractId)
            );
            if (!pledgeInfoList.isEmpty()) {
                List<FundDirectFinancingBaseInfo> fundFinancingBaseInfos = getBean(FundDirectFinancingBaseInfoService.class).listByIds(
                        pledgeInfoList.stream().map(FundDirectFinancingPledgeInfo::getFinancingId).collect(Collectors.toList()));
                //过滤，有效的融资记录
                List<Long> financingIds = fundFinancingBaseInfos.stream()
                        .filter(e -> equalsAny(e.getFinancingStatus(), CARRY_INTEREST.name()))
                        .map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toList());
                if (!financingIds.isEmpty()) {

                    //借据
                    List<Long> receiptRepayIds = getBean(FundReceiptRepayBaseInfoService.class).list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                            .in(FundReceiptRepayBaseInfo::getFinancingId, financingIds)
                            .eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT")
                    ).stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
                    if (!receiptRepayIds.isEmpty()) {
                        //现金流，继续过滤，融资未到期的记录，
                        QueryWrapper<FundReceiptRepayCashFlow> wrapper = new QueryWrapper<>();
                        wrapper.in("receipt_repay_id", receiptRepayIds);
                        wrapper.groupBy("financing_id");
                        wrapper.select("max(repay_date) as repay_date, financing_id");
                        Map<Long, LocalDate> financingMapRepayDate = getBean(FundReceiptRepayCashFlowService.class).list(wrapper)
                                .stream()
                                .filter(e -> !e.getRepayDate().isBefore(LocalDate.now()))
                                .collect(Collectors.toMap(FundReceiptRepayCashFlow::getFinancingId, FundReceiptRepayCashFlow::getRepayDate));
                        //最终过滤
                        Map<Long, PledgeInfo> contractBankMap = pledgeInfoList.stream()
                                .filter(e -> financingMapRepayDate.containsKey(e.getFinancingId()))
                                .map(e -> new PledgeInfo().setPledgeId(e.getId())
                                        .setBankName(e.getAccountBank())
                                        .setAccountName(e.getAccountName())
                                        .setContractId(e.getContractId())
                                        .setAccountNumber(e.getAccountNumber())
                                        .setIsSupervise(Objects.equals(e.getIsSupervise(), Boolean.TRUE) ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode()))
                                .collect(Collectors.toMap(e -> e.contractId, e -> e, (v1, v2) -> v1));
                        return contractBankMap;
                    }
                }
            }
        }
        return MapUtil.empty();
    }

    private PledgeInfo ensurePledgeInfo(PledgeInfo indirectPledge, PledgeInfo directPledge) {
        if (Objects.isNull(indirectPledge) && Objects.isNull(directPledge)) {
            return null;
        } else if (Objects.isNull(directPledge)) {
            return indirectPledge;
        } else if (Objects.isNull(indirectPledge)) {
            return directPledge;
        } else {
            List<PledgeInfo> candidateList = ListUtil.toList(directPledge, indirectPledge);
            // 排序，监管为1，非监管为0，优先监管
            candidateList.sort(Comparator.comparing(PledgeInfo::getIsSupervise).reversed());
            return candidateList.get(0);
        }
    }

    public BaseDataBankAccount defaultBank() {
        //产品要求直接写死
        BaseDataBankAccount bank = new BaseDataBankAccount();
        bank.setAccountNumber("1202 0212 1990 0394 595");
        bank.setAccountBank("中国工商银行杭州市武林支行");
        bank.setAccountName("浙江浙商融资租赁有限公司");
        return bank;
    }

    @Data
    @Accessors(chain = true)
    public static class PledgeInfo {
        private String financingType;
        private Long contractId;
        private Long pledgeId;
        private String bankName;
        private String accountName;
        private String accountNumber;
        private Integer isSupervise;
    }
}
