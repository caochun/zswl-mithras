package cn.zswltech.mithras.collection.gendoc.render.overduecollect;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.contract.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractGuarantorInfo;
import cn.zswltech.mithras.foundation.port.ClientNameResolver;
import com.alibaba.fastjson.JSON;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:  催收函生成
 * @author: zhaozhengkang
 * @date: 2024/10/29 14:55
 */
@Component
@Slf4j
public class JointLiabilityNoticeRender extends AbstractBasicRender<ContractGuarantorInfo> {

    private static final String FILE_NAME_SUFFIX = "履行连带责任保证通知书" + GlobalConstants.OFFICE_WORD_SUFFIX;

    @Resource
    private ClientNameResolver clientNameResolver;


    @Override
    public String render(OutputStream outputStream, ContractGuarantorInfo contractGuarantorInfo) throws Exception{

        String templatePath = "/doc/履行连带责任保证通知书模版.docx";
        Map<String, Object> renderMap = new HashMap<>(64);
        // 填充所需数据
        renderMap.put("lesseeNames",contractGuarantorInfo.getLesseeNames());
        renderMap.put("letterCode", contractGuarantorInfo.getLetterCode());
        renderMap.put("contractCode",contractGuarantorInfo.getContractCode());
        if (ObjectUtil.isNotEmpty(contractGuarantorInfo.getPhases())) {
            String phases = Arrays.stream(contractGuarantorInfo.getPhases().split(","))
                    .filter(ObjectUtil::isNotEmpty).map(Integer::valueOf)
                    .sorted().map(String::valueOf).collect(Collectors.joining(","));
            renderMap.put("phases", phases);
        }
        LocalDate genDate = contractGuarantorInfo.getGenDate();
        renderMap.put("year",genDate.getYear());
        renderMap.put("month",genDate.getMonthValue());
        renderMap.put("day",genDate.getDayOfMonth());

        BigDecimal overdueAmount;
        BigDecimal lateCharge;
        if (ObjectUtil.isNotEmpty(contractGuarantorInfo.getOverdueAmount())) {
            overdueAmount = BigDecimal.valueOf(contractGuarantorInfo.getOverdueAmount()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP);
        } else {
            overdueAmount = BigDecimal.ZERO;
        }
        if (ObjectUtil.isNotEmpty(contractGuarantorInfo.getLateCharge())) {
            lateCharge = BigDecimal.valueOf(contractGuarantorInfo.getLateCharge()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP);
        } else {
            lateCharge = BigDecimal.ZERO;
        }
        renderMap.put("overdueAmount", overdueAmount.toString());
        renderMap.put("lateCharge",lateCharge.toString());
        renderMap.put("totalAmount", overdueAmount.add(lateCharge).toString());
        renderMap.put("overdueDays", contractGuarantorInfo.getOverdueDays());


        String sign = "NORMAL".equals(contractGuarantorInfo.getGuarantorType()) ?
                "自然人保证人：（签字并按指印）" : "法人保证人：（盖章并法定代表人签字）";
        renderMap.put("sign", sign);

        List<Long> gIds = JSON.parseArray(contractGuarantorInfo.getGuarantorIds(), Long.class);
        Map<Long, String> gNames = clientNameResolver.clientId2Name(gIds);
        renderMap.put("guarantorNames", String.join(",", gNames.values()));
        renderMap.put("guarantorContractCode", contractGuarantorInfo.getGuarantorContractCode());

        String fileName =  contractGuarantorInfo.getContractCode() + String.join("_", gNames.values())+ FILE_NAME_SUFFIX;

        // 渲染
        XWPFTemplate template = XWPFTemplate.compile(JointLiabilityNoticeRender.class.getResourceAsStream(templatePath)).render(renderMap);
        template.writeAndClose(outputStream);
        return fileName;
    }

}
