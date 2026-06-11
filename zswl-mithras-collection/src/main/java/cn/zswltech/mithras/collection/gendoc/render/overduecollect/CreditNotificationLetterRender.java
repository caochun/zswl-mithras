package cn.zswltech.mithras.collection.gendoc.render.overduecollect;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.contract.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractLesseeInfo;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:  催收函生成
 * @author: zhaozhengkang
 * @date: 2024/10/29 14:55
 */
@Component
@Slf4j
public class CreditNotificationLetterRender extends AbstractBasicRender<ContractLesseeInfo> {

    private static final String FILE_NAME_SUFFIX = "征信告知书" + GlobalConstants.OFFICE_WORD_SUFFIX;


    @Override
    public String render(OutputStream outputStream, ContractLesseeInfo contractLesseeInfo) throws Exception{

        String templatePath = "/doc/征信告知书模版.docx";
        Map<String, Object> renderMap = new HashMap<>(64);
        // 填充所需数据
        renderMap.put("lesseeName",contractLesseeInfo.getLesseeName());
        renderMap.put("contractCode",contractLesseeInfo.getContractCode());
        renderMap.put("overdueDays", contractLesseeInfo.getOverdueDays());
        if (ObjectUtil.isNotEmpty(contractLesseeInfo.getPhases())) {
            String phases = Arrays.stream(contractLesseeInfo.getPhases().split(","))
                    .filter(ObjectUtil::isNotEmpty).map(Integer::valueOf)
                    .sorted().map(String::valueOf).collect(Collectors.joining(","));
            renderMap.put("phases", phases);
        }
        LocalDate genDate = contractLesseeInfo.getGenDate();
        renderMap.put("year",genDate.getYear());
        renderMap.put("month",genDate.getMonthValue());
        renderMap.put("day",genDate.getDayOfMonth());
        if (ObjectUtil.isNotEmpty(contractLesseeInfo.getOverdueAmount())) {
            BigDecimal overdueAmount = BigDecimal.valueOf(contractLesseeInfo.getOverdueAmount()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP);
            renderMap.put("overdueAmount", overdueAmount.toString());
        }

        String fileName =  contractLesseeInfo.getContractCode() + contractLesseeInfo.getLesseeName() + FILE_NAME_SUFFIX;

        // 渲染
        XWPFTemplate template = XWPFTemplate.compile(CreditNotificationLetterRender.class.getResourceAsStream(templatePath)).render(renderMap);
        template.writeAndClose(outputStream);
        return fileName;
    }
}
