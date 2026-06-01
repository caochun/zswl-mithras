package cn.zswltech.mithras.service.service.newftp.service.drift;

import cn.zswltech.mithras.service.enums.newftp.DescriptionType;
import cn.zswltech.mithras.service.service.newftp.mapper.draft.NewFtpDescriptionTextDraftMapper;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpDescriptionTextDraft;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description ftp主表
 * @date 2023-05-21
 */
@Service
public class NewFtpDescriptionTextDraftService extends ServiceImpl<NewFtpDescriptionTextDraftMapper, NewFtpDescriptionTextDraft> {

    public void add(LocalDate month, Long mainId) {
        List<NewFtpDescriptionTextDraft> toBeInsert = new ArrayList<>();
        NewFtpDescriptionTextDraft text1 = new NewFtpDescriptionTextDraft();
        text1.setFtpId(mainId);
        text1.setDescType(DescriptionType.MONTHLY_DEDUCTION.name());
        text1.setDescContent(String.format(DescriptionType.MONTHLY_DEDUCTION.getDesc(), getDynamicDesc(month)));
        toBeInsert.add(text1);
        NewFtpDescriptionTextDraft text2 = new NewFtpDescriptionTextDraft();
        text2.setFtpId(mainId);
        text2.setDescType(DescriptionType.MONTHLY_GUIDANCE.name());
        text2.setDescContent(DescriptionType.MONTHLY_GUIDANCE.getDesc());
        toBeInsert.add(text2);
        NewFtpDescriptionTextDraft text3 = new NewFtpDescriptionTextDraft();
        text3.setFtpId(mainId);
        text3.setDescType(DescriptionType.MONTHLY_SUPPLEMENT.name());
        text3.setDescContent("");
        toBeInsert.add(text3);

        if (isQuarterStart(month)) {
            NewFtpDescriptionTextDraft text4 = new NewFtpDescriptionTextDraft();
            text4.setFtpId(mainId);
            text4.setDescType(DescriptionType.QUARTERLY_SUPPLEMENT.name());
            text4.setDescContent("");
            toBeInsert.add(text4);

            NewFtpDescriptionTextDraft text5 = new NewFtpDescriptionTextDraft();
            text5.setFtpId(mainId);
            text5.setDescType(DescriptionType.QUARTERLY_PRICING.name());
            text5.setDescContent(DescriptionType.QUARTERLY_PRICING.getDesc());
            toBeInsert.add(text5);
        }
        saveBatch(toBeInsert);
    }

    private String getDynamicDesc(LocalDate month) {
        int monthValue = month.getMonthValue();
        if (monthValue == 1) {
            return "去年全年";
        }
        return "<1月>-<" + (monthValue - 1) + "月>";
    }

    private boolean isQuarterStart(LocalDate month) {
        int monthValue = month.getMonthValue();
        return monthValue == 1 || monthValue == 4 || monthValue == 7 || monthValue == 10;
    }
}
