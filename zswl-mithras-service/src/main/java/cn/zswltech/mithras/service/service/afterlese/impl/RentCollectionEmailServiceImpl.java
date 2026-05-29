package cn.zswltech.mithras.service.service.afterlese.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailDetailRSP;
import cn.zswltech.mithras.dto.afterlease.RentCollectionEmailSendREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListRSP;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.basedata.BaseDataBankAccountStatusEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPayAccount;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPayAccountService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.genhtml.PaymentNoticeHtmlRender;
import cn.zswltech.mithras.service.mapper.afterlease.RentCollectionEmailHtmlStoreMapper;
import cn.zswltech.mithras.service.mapper.afterlease.RentCollectionEmailRecordMapper;
import cn.zswltech.mithras.service.mapper.basedata.BaseDataBankAccountMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.service.mapper.model.afterlease.RentCollectionEmailHtmlStore;
import cn.zswltech.mithras.service.mapper.model.afterlease.RentCollectionEmailRecord;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataBankAccount;
import cn.zswltech.mithras.service.mapper.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.basedata.BaseDataBankAccountService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPayAccountService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.CorpContactInfoLibHandlerImpl;
import cn.zswltech.mithras.service.service.lib.contract.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.EmailUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * 租金催收发送邮件
 *
 * @author wangchuanhao
 * @date 2022/11/18 2:31 PM
 */
@Service
@Slf4j
public class RentCollectionEmailServiceImpl {

    private static final String MATERIALS_TYPE = "EMAIL";
    private static final String TMP_BUSINESS_TYPE = "TMP";
    private static final int TEMPLATE_VERSION = 1;

    @Value("${mithras.backendHost}")
    private String backendHost;

    @Resource
    private PaymentNoticeHtmlRender paymentNoticeHtmlRender;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private RentCollectionEmailRecordMapper rentCollectionEmailRecordMapper;
    @Resource
    private CorpContactInfoLibHandlerImpl corpContactInfoLibHandler;
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource
    private ContractTenantryLibMapper contractTenantryLibMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private BaseDataBankAccountMapper baseDataBankAccountMapper;
    @Resource
    private RentCollectionEmailHtmlStoreMapper rentCollectionEmailHtmlStoreMapper;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private FundFinancingPayAccountService fundFinancingPayAccountService;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Resource
    private FundDirectFinancingPayAccountService fundDirectFinancingPayAccountService;


    /**
     * 发送邮件
     *
     * @param req
     */
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void sendEmail(RentCollectionEmailSendREQ req) {
        if (!Validator.isEmail(req.getReceiverMail())) {
            throw new MithrasException("收件邮箱格式有误");
        }
        BaseDataBankAccount account = baseDataBankAccountMapper.selectById(req.getBankId());
        if (Objects.isNull(account)) {
            throw new MithrasException("收款账号选取有误");
        }
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(req.getCollectionId());
        if (Objects.isNull(collectionBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem())) {
            throw new MithrasException("仅租金可发送提醒邮件");
        }
        // 7天内才发送
        if (Objects.nonNull(collectionBaseInfo.getPlanCollectionDate())
                && (collectionBaseInfo.getPlanCollectionDate().toEpochDay() - LocalDate.now().toEpochDay() > 7
                || collectionBaseInfo.getPlanCollectionDate().toEpochDay() - LocalDate.now().toEpochDay() < 0
        )) {
            throw new MithrasException("仅租金到期7天内可发送提醒邮件");
        }
        if (CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus())) {
            throw new MithrasException("核销完毕后不可发送提醒邮件");
        }
        RentCollectionEmailRecord rentCollectionEmailRecord = rentCollectionEmailRecordMapper.selectOne(Wrappers.<RentCollectionEmailRecord>lambdaQuery()
                .eq(RentCollectionEmailRecord::getCollectionId, req.getCollectionId())
                .last("LIMIT 1")
        );
        if (Objects.nonNull(rentCollectionEmailRecord)) {
            // 发过邮件
            throw new MithrasException("每期收款仅允许发送一次租金支付通知书");
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String fileName = paymentNoticeHtmlRender.render(os, req.getCollectionId(), req.getComment(), req.getBankId());
//            Long fileId = materialsListService.add(IoUtil.toStream(os.toByteArray()), fileName, req.getCollectionId(), MATERIALS_TYPE, TMP_BUSINESS_TYPE);
            String htmlKey = RandomUtil.randomString(32);
            RentCollectionEmailRecord emailRecord = RentCollectionEmailRecord.builder()
                    .collectionId(req.getCollectionId())
//                    .fileId(fileId)
                    .comment(req.getComment())
                    .receiverMail(req.getReceiverMail())
                    .title(req.getTitle())
                    .mailContent(os.toString())
                    .bankId(req.getBankId())
                    .htmlKey(htmlKey)
                    .build();
            rentCollectionEmailRecordMapper.insert(emailRecord);
            // html store
            RentCollectionEmailHtmlStore htmlStore = RentCollectionEmailHtmlStore.builder()
                    .collectionId(req.getCollectionId())
                    .htmlKey(htmlKey)
                    .htmlData(os.toString())
                    .build();
            rentCollectionEmailHtmlStoreMapper.insert(htmlStore);

            // 更新收款主表
            LambdaUpdateWrapper<CollectionBaseInfo> cbiUpdateWrapper = new LambdaUpdateWrapper<>();
            cbiUpdateWrapper.eq(CollectionBaseInfo::getId, req.getCollectionId());
            cbiUpdateWrapper.set(CollectionBaseInfo::getEmailNoticeCount, 1);
            collectionBaseInfoMapper.update(null, cbiUpdateWrapper);

            EmailUtil.send(req.getReceiverMail(), req.getTitle(), os.toString(), true);
        }
    }

    /**
     * 生成邮件
     *
     * @param collectionId
     * @param comment
     * @return
     */
    @SneakyThrows
    public Long genEmail(Long collectionId, String comment, Long bankId) {
        //  此处逻辑干掉 之后不走文件交互 走直接预览html逻辑
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String fileName = paymentNoticeHtmlRender.render(os, collectionId, comment, bankId);
        return materialsListService.add(IoUtil.toStream(os.toByteArray()), fileName, collectionId, MATERIALS_TYPE, TMP_BUSINESS_TYPE);
    }

    @Resource
    private BaseDataBankAccountService baseDataBankAccountService;

    /**
     * 详情
     *
     * @param collectionId
     * @return
     */
    public RentCollectionEmailDetailRSP detail(Long collectionId) {
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(collectionId);
        if (Objects.isNull(collectionBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        RentCollectionEmailDetailRSP rsp = new RentCollectionEmailDetailRSP();
        RentCollectionEmailRecord rentCollectionEmailRecord = rentCollectionEmailRecordMapper.selectOne(Wrappers.<RentCollectionEmailRecord>lambdaQuery()
                .eq(RentCollectionEmailRecord::getCollectionId, collectionId)
                .last("LIMIT 1")
        );
        rsp.setTimeAvaliableFlag(!CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus())
                && Objects.nonNull(collectionBaseInfo.getPlanCollectionDate())
                && collectionBaseInfo.getPlanCollectionDate().toEpochDay() - LocalDate.now().toEpochDay() <= 60
                && collectionBaseInfo.getPlanCollectionDate().toEpochDay() - LocalDate.now().toEpochDay() >= 0
        );
        if (Objects.nonNull(rentCollectionEmailRecord)) {
            // 发过邮件
            rsp.setTitle(rentCollectionEmailRecord.getTitle());
            rsp.setComment(rentCollectionEmailRecord.getComment());
            rsp.setReceiverMail(rentCollectionEmailRecord.getReceiverMail());
            rsp.setBankId(rentCollectionEmailRecord.getBankId());
            rsp.setSendFlag(true);
            rsp.setSendTime(rentCollectionEmailRecord.getCreateTime());
            if (StringUtils.isNotBlank(rentCollectionEmailRecord.getHtmlKey())) {
                rsp.setHtmlPreviewUrl(genHtmlPreviewUrl(rentCollectionEmailRecord.getHtmlKey()));
            }
            if (Objects.nonNull(rentCollectionEmailRecord.getBankId())) {
                BaseDataBankAccount account = baseDataBankAccountMapper.selectById(rentCollectionEmailRecord.getBankId());
                if (Objects.nonNull(account)) {
                    rsp.setAccountBank(account.getAccountBank());
                    rsp.setAccountName(account.getAccountName());
                    rsp.setAccountNumber(account.getAccountNumber());
                }
            }
        } else {
            // 没发过邮件
            rsp.setTitle("租金支付通知书");
            // 计算收信邮箱
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibHandler.queryLatestDataByOriginId(collectionBaseInfo.getContractId());
            // 找到主承租人或第一债权人 的 不为空 的 联系人
            ContractTenantryLib contractTenantry = contractTenantryLibMapper.selectOne(Wrappers.<ContractTenantryLib>lambdaQuery()
                    .eq(ContractTenantryLib::getContractId, collectionBaseInfo.getContractId())
                    .eq(ContractTenantryLib::getLesseeId, contractBaseInfoLib.getClientId())
                    .eq(ContractTenantryLib::getVersion, contractBaseInfoLib.getVersion())
                    .isNotNull(ContractTenantryLib::getContactId)
                    .last("LIMIT 1")
            );
            if (Objects.nonNull(contractTenantry)) {
                CorpContactInfoLib corpContactInfoLib = corpContactInfoLibHandler.queryLatestDataByLibId(contractTenantry.getContactId());
                rsp.setReceiverMail(Optional.ofNullable(corpContactInfoLib).map(CorpContactInfoLib::getMail).orElse(null));
            }
            rsp.setSendFlag(false);
            rsp.setHtmlPreviewUrl(genEmailHtml(collectionId, null, null));
            // 判断合同有没有被质押，如果被质押需提供默认的还款账户信息
            BaseDataBankAccountListRSP accountListRSP = new BaseDataBankAccountListRSP();

            List<FundFinancingPledgeInfo> contractPledgeList = fundFinancingPledgeInfoService.findContractPledgeList(collectionBaseInfo.getContractId());
            if (ObjectUtil.isNotEmpty(contractPledgeList)) {
                FundFinancingPledgeInfo fundFinancingPledgeInfo = contractPledgeList.get(0);
                accountListRSP.setAccountBank(fundFinancingPledgeInfo.getAccountBank());
                accountListRSP.setAccountName(fundFinancingPledgeInfo.getAccountName());
                accountListRSP.setAccountNumber(fundFinancingPledgeInfo.getAccountNumber());
            } else {
                List<FundDirectFinancingPledgeInfo> directPledge = fundDirectFinancingPledgeInfoService.findContractPledgeList(collectionBaseInfo.getContractId());
                if (ObjectUtil.isNotEmpty(directPledge)) {
                    FundDirectFinancingPledgeInfo fundDirectFinancingPledgeInfo = directPledge.get(0);
                    accountListRSP.setAccountBank(fundDirectFinancingPledgeInfo.getAccountBank());
                    accountListRSP.setAccountName(fundDirectFinancingPledgeInfo.getAccountName());
                    accountListRSP.setAccountNumber(fundDirectFinancingPledgeInfo.getAccountNumber());
                }
            }

            // 若无账户则使用默认账户
            if(StringUtils.isEmpty(accountListRSP.getAccountNumber())){
                accountListRSP.setAccountBank("中国工商银行杭州市武林支行");
                accountListRSP.setAccountName("浙江浙商融资租赁有限公司");
                accountListRSP.setAccountNumber("1202 0212 1990 0394 595");
            }

            BaseDataBankAccount bankAccount = getBean(BaseDataBankAccountService.class).getOne(Wrappers.<BaseDataBankAccount>lambdaQuery()
//                        .eq(BaseDataBankAccount::getAccountStatus, BaseDataBankAccountStatusEnum.NORMAL.name())
                        .eq(BaseDataBankAccount::getAccountBank, accountListRSP.getAccountBank())
                        .eq(BaseDataBankAccount::getAccountName, accountListRSP.getAccountName())
                        .and(i -> i.or().eq(BaseDataBankAccount::getAccountNumber, accountListRSP.getAccountNumber())
                                .or().eq(BaseDataBankAccount::getAccountNumber, StringUtils.replace(accountListRSP.getAccountNumber()," ",""))));
            if(bankAccount != null){
                rsp.setAccountBank(bankAccount.getAccountBank());
                rsp.setAccountName(bankAccount.getAccountName());
                rsp.setAccountNumber(bankAccount.getAccountNumber());
                rsp.setBankId(bankAccount.getId());
            }else{
                log.error("未查询到账户");
            }
        }
        return rsp;
    }

    @SneakyThrows
//    @Transactional(rollbackFor = Exception.class)
    public String genEmailHtml(Long collectionId, String comment, Long bankId) {
        boolean defaultFlag = StringUtils.isBlank(comment) && Objects.isNull(bankId);
        if (defaultFlag) {
            RentCollectionEmailHtmlStore defaultStore = rentCollectionEmailHtmlStoreMapper.selectOne(Wrappers.<RentCollectionEmailHtmlStore>lambdaQuery()
                    .eq(RentCollectionEmailHtmlStore::getCollectionId, collectionId)
                    .eq(RentCollectionEmailHtmlStore::getDefaultFlag, 1)
                    .eq(RentCollectionEmailHtmlStore::getVersion, TEMPLATE_VERSION)
                    .last("LIMIT 1")
            );
            if (Objects.nonNull(defaultStore)) {
                return genHtmlPreviewUrl(defaultStore.getHtmlKey());
            }
        }
        // 判断有没有默认的
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String fileName = paymentNoticeHtmlRender.render(os, collectionId, comment, bankId);
        RentCollectionEmailHtmlStore htmlStore = RentCollectionEmailHtmlStore.builder()
                .collectionId(collectionId)
                .htmlKey(RandomUtil.randomString(32))
                .htmlData(os.toString())
                .defaultFlag(defaultFlag ? 1 : 0)
                .version(TEMPLATE_VERSION)
                .build();
        rentCollectionEmailHtmlStoreMapper.insert(htmlStore);
        return genHtmlPreviewUrl(htmlStore.getHtmlKey());
    }

    /**
     * 邮件html预览
     *
     * @param htmlKey
     * @return
     */
    public byte[] htmlPreview(String htmlKey) {
        RentCollectionEmailHtmlStore htmlStore = rentCollectionEmailHtmlStoreMapper.selectOne(Wrappers.<RentCollectionEmailHtmlStore>lambdaQuery()
                .eq(RentCollectionEmailHtmlStore::getHtmlKey, htmlKey)
                .last("LIMIT 1")
        );
        if (Objects.isNull(htmlStore)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        return htmlStore.getHtmlData().getBytes(StandardCharsets.UTF_8);
    }

    private String genHtmlPreviewUrl(String htmlKey) {
        return String.format("%s/rent/collection/email/htmlPreview?htmlKey=%s", backendHost, htmlKey);
    }

}
