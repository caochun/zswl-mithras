package cn.zswltech.mithras.third.providence.util;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.providence.entity.BillOverdue;
import cn.zswltech.mithras.third.providence.enums.OrgTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 票据逾期名单 PDF 解析。
 */
@Slf4j
public final class BillOverduePdfParser {

    private BillOverduePdfParser() {
    }

    public static List<BillOverdue> parse(MultipartFile multipartFile, String busiDate) {
        List<BillOverdue> billOverdueList = new ArrayList<>();
        try {
            InputStream inputStream = multipartFile.getInputStream();
            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setSortByPosition(true);
            pdfTextStripper.setStartPage(1);
            pdfTextStripper.setEndPage(document.getNumberOfPages());
            String content = pdfTextStripper.getText(document);

            String[] lines = content.split("\\r?\\n");

            boolean isCompanyTable = false;
            boolean isFinancialTable = false;
            String regex = ".*[\\u4E00-\\u9FFF].*";
            List<String> info = Arrays.asList(new String[4]);
            boolean overRow = false;

            for (String line : lines) {
                if (line.contains("企业名称") && line.contains("统一社会信用代码")) {
                    isCompanyTable = true;
                    isFinancialTable = false;
                    continue;
                } else if (line.contains("金融机构名称") && line.contains("金融机构行号")) {
                    isCompanyTable = false;
                    isFinancialTable = true;
                    continue;
                }

                if (isCompanyTable && line.matches(regex)) {
                    String[] fields = line.split(" ");
                    if (fields.length < 2) {
                        if (ObjectUtil.isNull(info.get(1))) {
                            info.set(1, fields[0]);
                            overRow = true;
                            continue;
                        } else {
                            String s = info.get(1) + fields[0];
                            info.set(1, s);
                            billOverdueList.add(new BillOverdue()
                                    .setSeqNo(Integer.parseInt(info.get(0)))
                                    .setBusiDate(busiDate)
                                    .setOrgCode(info.get(2))
                                    .setOrgName(info.get(1))
                                    .setOrgType(OrgTypeEnum.ENTERPRISE.getCode())
                                    .setOverdueStartDate(info.get(3)));
                            log.info("info:{}", info);
                            info.set(0, null);
                            info.set(1, null);
                            info.set(2, null);
                            info.set(3, null);
                            overRow = false;
                            continue;
                        }
                    }
                    if (overRow) {
                        info.set(0, fields[0]);
                        info.set(2, fields[1]);
                        fields[6] = fields[6].endsWith("日") ? fields[6].replace("日", "") : fields[6];
                        fields[6] = fields[6].length() == 1 ? "0" + fields[6] : fields[6];
                        info.set(3, fields[2] + "-" + fields[4] + "-" + fields[6]);
                        continue;
                    }
                    fields[7] = fields[7].endsWith("日") ? fields[7].replace("日", "") : fields[7];
                    fields[7] = fields[7].length() == 1 ? "0" + fields[7] : fields[7];
                    String overdueStartDate = fields[3] + "-" + fields[5] + "-" + fields[7];
                    billOverdueList.add(new BillOverdue()
                            .setSeqNo(Integer.parseInt(fields[0]))
                            .setBusiDate(busiDate)
                            .setOrgCode(fields[2])
                            .setOrgName(fields[1])
                            .setOrgType(OrgTypeEnum.ENTERPRISE.getCode())
                            .setOverdueStartDate(overdueStartDate));
                } else if (isFinancialTable && line.matches(regex)) {
                    String[] fields = line.split(" ");
                    fields[7] = fields[7].length() == 1 ? "0" + fields[7] : fields[7];
                    String column4 = fields[3] + "-" + fields[5] + "-" + fields[7];
                    billOverdueList.add(new BillOverdue()
                            .setSeqNo(Integer.parseInt(fields[0]))
                            .setBusiDate(busiDate)
                            .setOrgCode(fields[2])
                            .setOrgName(fields[1])
                            .setOrgType(OrgTypeEnum.FINANCIAL_INSTITUTION.getCode())
                            .setOverdueStartDate(column4));
                }
            }
        } catch (Exception e) {
            log.error("票据逾期pdf解析失败", e);
            throw new RuntimeException(e);
        }
        return billOverdueList;
    }
}
