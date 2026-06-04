package cn.zswltech.mithras.service.genhtml;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.RentCollectionEmailRecordMapper;
import cn.zswltech.mithras.basedata.mapper.BaseDataBankAccountMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.RentCollectionEmailRecord;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.util.FreeMarkerUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;

/**
 * 生成html,docx代码
 *
 * @author wangchuanhao
 * @date 2022/11/18 2:15 PM
 */
@Component
public class PaymentNoticeHtmlRender {

    public static final String TEMPLATE_FILE_NAME = "支付通知书.html";
    public static final String PAYMENT_DOCX_NAME = "/doc/支付通知书.docx";

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private BaseDataBankAccountMapper baseDataBankAccountMapper;
    @Resource
    private RentCollectionEmailRecordMapper rentCollectionEmailRecordMapper;

    public String render(OutputStream outputStream, Long collectionId, String comment, Long bankId) throws Exception {
        // 生成
        FreeMarkerUtil.generate(outputStream, TEMPLATE_FILE_NAME, buildRenderMap(collectionId, comment, bankId));
        return "支付通知书" + GlobalConstants.OFFICE_HTML_SUFFIX;
    }

    @SneakyThrows
    public void downDocx(OutputStream outputStream, Long collectionId, String accountName, String accountBank, String accountNumber) {
        RentCollectionEmailRecord rentCollectionEmailRecord = rentCollectionEmailRecordMapper.selectOne(Wrappers.<RentCollectionEmailRecord>lambdaQuery()
                .eq(RentCollectionEmailRecord::getCollectionId, collectionId));
        HashMap<String, Object> stringObjectHashMap;
        if (ObjectUtil.isNotEmpty(rentCollectionEmailRecord)) {
            stringObjectHashMap = buildRenderMap(collectionId, rentCollectionEmailRecord.getComment(), rentCollectionEmailRecord.getBankId());
            stringObjectHashMap.put("noticeDateFormal", LocalDateTimeUtil.format(rentCollectionEmailRecord.getCreateTime(), "【yyyy】年【MM】月【dd】日"));
        } else {
            stringObjectHashMap = buildRenderMap(collectionId, null, null);
        }
        //
        if (isNotBlank(accountName)) {
            stringObjectHashMap.put("accountName", accountName);
        }
        if (isNotBlank(accountBank)) {
            stringObjectHashMap.put("accountBank", accountBank);
        }
        if (isNotBlank(accountNumber)) {
            stringObjectHashMap.put("accountNumber", accountNumber);
        }
        XWPFTemplate template = XWPFTemplate.compile(PaymentNoticeHtmlRender.class.getResourceAsStream(PAYMENT_DOCX_NAME)).render(stringObjectHashMap);
        template.writeAndClose(outputStream);
    }

    private HashMap<String, Object> buildRenderMap(Long collectionId, String comment, Long bankId) {
        // 捞数据
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(collectionId);
        if (Objects.isNull(collectionBaseInfo)) {
            throw new MithrasException("收款信息不存在");
        }
        Client client = clientMapper.selectById(collectionBaseInfo.getClientId());
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibHandler.queryLatestDataByOriginId(collectionBaseInfo.getContractId());
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfoLib.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfoLib.getProjSponsorUserId());
        BaseDataBankAccount account = Optional.ofNullable(bankId).map(bId -> baseDataBankAccountMapper.selectById(bId)).orElse(new BaseDataBankAccount());
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
        renderMap.put("comment", StringUtils.isNotBlank(comment) ? comment : "/");
        renderMap.put("accountName", Optional.ofNullable(account.getAccountName()).orElse(""));
        renderMap.put("accountBank", Optional.ofNullable(account.getAccountBank()).orElse(""));
        renderMap.put("accountNumber", Optional.ofNullable(account.getAccountNumber()).orElse(""));

        renderMap.put("sponsorUserName", userVO.getUserName());
        renderMap.put("sponsorTelephone", sponsorPhone);
        renderMap.put("noticeDateFormal", LocalDateTimeUtil.format(LocalDate.now(), "【yyyy】年【MM】月【dd】日"));
        return renderMap;
    }

}
