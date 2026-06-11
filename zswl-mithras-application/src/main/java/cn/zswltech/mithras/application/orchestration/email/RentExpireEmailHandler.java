package cn.zswltech.mithras.application.orchestration.email;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.dto.afterlease.RentCollectionBaseInfo;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.message.enums.EmailType;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.basedata.mapper.BaseDataBankAccountMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.message.service.email.AbstractSendEmailHandler;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import groovy.util.logging.Slf4j;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import static cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum.CARRY_INTEREST;
import static cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum.EFFECT;
import static cn.zswltech.mithras.application.orchestration.job.NextMonthRentNotify.FinancingType.indirect;

/**
 * @description: 邮件类
 * @author: ylzhang5
 * @date: 2025/12/01
 * @version: 1.0
 */
@lombok.extern.slf4j.Slf4j
@Slf4j
@Service
public class RentExpireEmailHandler extends AbstractSendEmailHandler<RentCollectionBaseInfo>{

    private final static String title = "租金到期通知";
    public static final String PAYMENT_DOCX_NAME = "/doc/支付通知书.docx";//文档模板路径
    private File[] files;

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Autowired
    private ContractLeasePriceLibService leasePriceLibService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

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
        try{
            HashMap<String, Object> stringObjectHashMap;
            //查询数据
            stringObjectHashMap = buildRenderMap(businessData.getCollectionId(), "");
            XWPFTemplate template = XWPFTemplate.compile(RentExpireEmailHandler.class.getResourceAsStream(PAYMENT_DOCX_NAME)).render(stringObjectHashMap);
            NiceXWPFDocument niceXWPFDocument = template.getXWPFDocument();
            File tempFile = File.createTempFile("支付通知书",".docx");
            tempFile.deleteOnExit();
            // 2. 定义无随机数的目标文件名（自定义规则）
            String customFileName = businessData.getContractCode()+businessData.getClientName()+"支付通知书.docx"; // 无随机数
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
            fileList.add(customTempFile);
        }catch(Exception e){
            log.error("合同"+businessData.getContractId()+"的附件生成失败！");
        }
        return fileList.toArray(new File[0]);
    }

    @Override
    public boolean needHandle(EmailType emailType) {
        return false;
    }

    @Override
    public EmailType getEmailType() {
        return EmailType.COLLECTIO_EXPIRE_NOTICE;
    }


    /**
     * 生成邮件正文
     */
    @Override
    public String generateEmailHtml(String table, String systemUrl, RentCollectionBaseInfo businessData) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>租金到期通知</title>\n" +
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
                "        <!-- 核心正文 -->\n" +
                "        <div class=\"notice-content\">\n" +
                "            <p>致<span class=\"key-info\">"+businessData.getClientName()+"</span>：</p>\n" +
                "            <p>您好！</p>\n" +
                "            <p>贵方与我司签订的融资租赁合同<span class=\"key-info\">"+businessData.getContractCode()+"</span>项下第<span class=\"key-info\">"+businessData.getPhase()+"</span>期租金将于<span class=\"key-info\">"+businessData.getPlanYear()+"年"+businessData.getPlanMonth()+"月"+businessData.getPlanDay()+"日</span>到期，请按照合同约定及时付款。具体信息详见附件。</p>\n" +
                "        </div>\n" +
                "\n" +
                "         </div>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * 抓取数据
     * @param collectionId
     * @param comment
     * @return
     */
    private HashMap<String, Object> buildRenderMap(Long collectionId, String comment) {
        // 捞数据
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getId, collectionId));
        if (Objects.isNull(collectionBaseInfo)) {
            throw new MithrasException("收款信息不存在");
        }
        Client client = clientMapper.selectById(collectionBaseInfo.getClientId());
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibHandler.queryLatestDataByOriginId(collectionBaseInfo.getContractId());
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfoLib.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfoLib.getProjSponsorUserId());
        //查询名义价款
        //根据还款流水查合同id
        Long contractId = getBean(CollectionBaseInfoService.class).getById(collectionId).getContractId();
        //再根据合同id反查所有还款流水中最后的期项
        Integer lastPhase = getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId,contractId).orderByDesc(CollectionBaseInfo::getPhase)).get(0).getPhase();
        //判断当前是否最后期项，然后赋值名义价款
        Long lNominalPrice = null;
        //如果是最后期项
        if(Objects.equals(lastPhase, collectionBaseInfo.getPhase())){
            //取最新合同版本号
            CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                    .eq(CommonVersion::getMainId, contractId)
                    .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                    .eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name())
                    .orderByDesc(CommonVersion::getVersion)
                    .last("LIMIT 1"));
            if (ObjectUtil.isNull(commonVersion)) {
                throw new MithrasException("此合同没有版本数据");
            }
            //取最新报价方案
            ContractLeasePrice contractLeasePrice = leasePriceLibService.getByVersion(contractId, commonVersion.getVersion());
            lNominalPrice = contractLeasePrice.getNominalPrice();
        }

        // 填充渲染map
        HashMap<String, Object> renderMap = new HashMap<>();
        renderMap.put("clientName", client.getClientName());
        renderMap.put("contractCode", collectionBaseInfo.getContractCode());
        renderMap.put("phase", collectionBaseInfo.getPhase());
        renderMap.put("planCollectionDate", LocalDateTimeUtil.format(collectionBaseInfo.getPlanCollectionDate(), "yyyy/MM/dd"));
        renderMap.put("planCollectionDateFormal", LocalDateTimeUtil.format(collectionBaseInfo.getPlanCollectionDate(), "【yyyy】年【MM】月【dd】日"));
        renderMap.put("planCollectionAmount", Optional.ofNullable(collectionBaseInfo.getPlanCollectionAmount()).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        renderMap.put("principal", Optional.ofNullable(collectionBaseInfo.getPrincipal()).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        renderMap.put("interest", Optional.ofNullable(collectionBaseInfo.getInterest()).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        //最后期项，名义价款取最新报价方案中的名义价款，否则展示“/”
        if(lNominalPrice!=null){
            renderMap.put("nominalPrice", Optional.of(lNominalPrice).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        }else{
            renderMap.put("nominalPrice","/");
        }
        renderMap.put("comment", StringUtils.isNotBlank(comment) ? comment : "/");
        //合同融资关联
        Map<Long, PledgeInfo> contractPledgeMap = contractBankMapByFinancing(contractId);
        Map<Long, PledgeInfo> directContractPledgeMap = contractBankMapByDirectFinancing(contractId);
        //默认银行账户
        BaseDataBankAccount defaultBank = defaultBank();
        PledgeInfo pledgeInfo = this.ensurePledgeInfo(contractPledgeMap.get(contractId), directContractPledgeMap.get(contractId));
        //如果不存在关联的融资信息，使用默认的银行信息
        if (pledgeInfo == null) {
            renderMap.put("accountName", defaultBank.getAccountName());
            renderMap.put("accountBank", defaultBank.getAccountBank());
            renderMap.put("accountNumber", defaultBank.getAccountNumber());
        }else{
            renderMap.put("accountName", pledgeInfo.getAccountName());
            renderMap.put("accountBank", pledgeInfo.getBankName());
            renderMap.put("accountNumber", pledgeInfo.getAccountNumber());
        }


        renderMap.put("sponsorUserName", userVO.getUserName());
        renderMap.put("sponsorTelephone", sponsorPhone);
        renderMap.put("noticeDateFormal", LocalDateTimeUtil.format(LocalDate.now(), "【yyyy】年【MM】月【dd】日"));
        return renderMap;
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
