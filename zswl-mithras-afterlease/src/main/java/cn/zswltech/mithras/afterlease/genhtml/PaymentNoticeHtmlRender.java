package cn.zswltech.mithras.afterlease.genhtml;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.afterlease.application.PaymentNoticeRenderData;
import cn.zswltech.mithras.afterlease.application.PaymentNoticeRenderDataPort;
import cn.zswltech.mithras.afterlease.mapper.RentCollectionEmailRecordMapper;
import cn.zswltech.mithras.afterlease.model.RentCollectionEmailRecord;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.util.FreeMarkerUtil;
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
    private RentCollectionEmailRecordMapper rentCollectionEmailRecordMapper;
    @Resource
    private PaymentNoticeRenderDataPort paymentNoticeRenderDataPort;

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
        PaymentNoticeRenderData data = paymentNoticeRenderDataPort.load(collectionId, bankId);
        // 填充渲染map
        HashMap<String, Object> renderMap = new HashMap<>();
        renderMap.put("clientName", data.getClientName());
        renderMap.put("contractCode", data.getContractCode());
        renderMap.put("phase", data.getPhase());
        renderMap.put("planCollectionDate", LocalDateTimeUtil.format(data.getPlanCollectionDate(), "yyyy/MM/dd"));
        renderMap.put("planCollectionDateFormal", LocalDateTimeUtil.format(data.getPlanCollectionDate(), "【yyyy】年【MM】月【dd】日"));
        renderMap.put("planCollectionAmount", moneyToWan(data.getPlanCollectionAmount()));
        renderMap.put("principal", moneyToWan(data.getPrincipal()));
        renderMap.put("interest", moneyToWan(data.getInterest()));
        renderMap.put("comment", StringUtils.isNotBlank(comment) ? comment : "/");
        renderMap.put("accountName", Optional.ofNullable(data.getAccountName()).orElse(""));
        renderMap.put("accountBank", Optional.ofNullable(data.getAccountBank()).orElse(""));
        renderMap.put("accountNumber", Optional.ofNullable(data.getAccountNumber()).orElse(""));

        renderMap.put("sponsorUserName", data.getSponsorUserName());
        renderMap.put("sponsorTelephone", data.getSponsorTelephone());
        renderMap.put("noticeDateFormal", LocalDateTimeUtil.format(LocalDate.now(), "【yyyy】年【MM】月【dd】日"));
        return renderMap;
    }

    private String moneyToWan(Long amount) {
        return Optional.ofNullable(amount)
                .map(BigDecimal::new)
                .map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP))
                .map(BigDecimal::toPlainString)
                .orElse("0");
    }

}
