package cn.zswltech.mithras.service.gendoc.render.overduecollect;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.gendoc.AbstractBasicRender;
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
public class CollectionLetterRender extends AbstractBasicRender<ContractLesseeInfo> {

    private static final String FILE_NAME_SUFFIX = "催收函" + GlobalConstants.OFFICE_WORD_SUFFIX;


    @Override
    public String render(OutputStream outputStream, ContractLesseeInfo contractLesseeInfo) throws Exception{

        String templatePath = "/doc/催收函模版.docx";
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

        String fileName =  contractLesseeInfo.getContractCode() + contractLesseeInfo.getLesseeName() + FILE_NAME_SUFFIX;

        // 渲染
        XWPFTemplate template = XWPFTemplate.compile(CollectionLetterRender.class.getResourceAsStream(templatePath)).render(renderMap);
        template.writeAndClose(outputStream);
        return fileName;
    }
}
