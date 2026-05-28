package cn.zswltech.mithras.service.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.contract.ContractModelEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName ContractUtil
 * @Description
 * @Author jackerhe
 * @Date 2022/8/15 11:24 上午
 * @Version 1.0
 **/
public class ContractUtil {
    public static void main(String[] args) {
        String ss = "2022CGCJX0005-01";
        int index = ss.lastIndexOf("-");
        String s = ss.substring(index);
        System.out.println(s);
    }

    public static String format(String code) {
        if (StrUtil.isBlank(code)) {
            return code;
        }
        return code.replaceAll("（", "(").replaceAll("）", ")").trim();
    }

    /**
     * 生成子合同编号
     *
     * @param contractModelEnum 合同类型
     * @param contractCode      融资租赁合同编号（主合同编号）
     * @param num               子合同编号数量
     * @return 子合同编号列表
     */
    public static List<String> generateSubContractCode(ContractModelEnum contractModelEnum, String contractCode, int num) {
        if (StrUtil.isBlank(contractCode)) {
            throw new MithrasException("融资租赁合同编号为空");
        }
        if (num <= 0) {
            throw new MithrasException("未指定需要生成的子合同编号数量");
        }
        int index1 = contractCode.indexOf("】");
        int index2 = contractCode.indexOf("字");
        String part1 = contractCode.substring(0, index1 + 1);
        if (num == 1) {
            String part2 = contractCode.substring(index2);
            return Collections.singletonList(part1 + contractModelEnum.display + part2);
        } else {
            int index3 = contractCode.indexOf(")");
            String part2 = contractCode.substring(index2, index3);
            String part3 = contractCode.substring(index3);
            List<String> list = new ArrayList<>(num);
            for (int i = 1; i <= num; i++) {
                list.add(part1 + contractModelEnum.display + part2 + "-" + NumberUtil.decimalFormat("00", i) + part3);
            }
            return list;
        }
    }

    public static String generateMainCode(Integer sequence, ProjectBizType projectBizType, String leaseType) {
        String nowDateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String type;
        String versionPre = "";
        String[] split;
        switch (projectBizType) {
            case ZL:
                type = "租";
                if (LeaseType.hui_zu.name().equals(leaseType)) {
                    versionPre = "A-";
                } else if (LeaseType.zhi_zu.name().equals(leaseType)) {
                    versionPre = "C-";
                } else if (LeaseType.jyx_zu.name().equals(leaseType)) {
                    versionPre = "D-";
                }
                break;
            case BL:
                type = "保理";
                versionPre = "B-";
                break;
            case ZZ:
                type = "转租";
                versionPre = "Z-";
                break;
            case ZR:
                type = "转让";
                versionPre = "ZR-";
                break;
            default:
                throw new MithrasException("无此类型业务" + projectBizType);
        }
        return String.format("浙商租【%s】%s字第(%s%04d)号", nowDateString, type, versionPre, sequence);
    }

    public static String generateReceiptCode(String contractCode, int sequence, boolean isZhiZu) {
        // 复用付款编号规则
        String s = PaymentBaseInfoService.generatePaymentCode(contractCode, sequence);
        if (isZhiZu) {
            String[] split = s.split("-");
            return split[0] + "-HZ";
        } else {
            return s;
        }
    }
}
