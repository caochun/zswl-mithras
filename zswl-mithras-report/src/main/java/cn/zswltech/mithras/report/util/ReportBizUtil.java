package cn.zswltech.mithras.report.util;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.dto.report.ReportListBaseREQ;
import cn.zswltech.mithras.dto.report.account.AccountListREQ;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.biz.AccountRepayRateEnum;
import cn.zswltech.mithras.report.enums.biz.RepayCalcTypeEnum;
import cn.zswltech.mithras.report.enums.common.QueryChannel;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

/**
 * 业务工具类
 *
 * @author wangchuanhao
 * @date 2022/12/21 2:56 PM
 */
public class ReportBizUtil {

    /**
     * 中文正则
     */
    private static final String REGEX_CHINESE = "[\\u4e00-\\u9fa5]";


    public static String removeSpecialChar(String origin) {
        if (StringUtils.isBlank(origin)) {
            return origin;
        }
        // 去除换行符
        String fixString = origin.replaceAll("\n|\r", "");
        // 去除首尾空格
        fixString = fixString.trim();
        // 去除制表符
        fixString = fixString.replaceAll("\t", "");
        return fixString;
    }

    /**
     * 处理证件号，我不是很了解规则，但客户要求在我们这边处理，那就处理一下
     * 证件类型：
     * 2护照：前3位为国籍代码，最多12位
     * 5港澳居民来往内地通行证：保留前9位
     * 6台湾同胞来往内地通行证：保留前8位
     *
     * @param certType
     * @param certCode
     * @return
     */
    public static String handleCertCode(String certType, String certCode) {
        if (StringUtils.isBlank(certType) || StringUtils.isBlank(certCode)) {
            return certCode;
        }
        if (Objects.equals("2", certType)) {
            return Optional.ofNullable(certCode).map(c -> c.length() <= 12 ? c : c.substring(0, 12)).orElse(null);
        } else if (Objects.equals("5", certType)) {
            return Optional.ofNullable(certCode).map(c -> c.length() <= 9 ? c : c.substring(0, 9)).orElse(null);
        } else if (Objects.equals("6", certType)) {
            return Optional.ofNullable(certCode).map(c -> c.length() <= 8 ? c : c.substring(0, 8)).orElse(null);
        }
        return certCode;
    }

    /**
     * 合同编号处理 去除中文 加前缀
     *
     * @param guaranteContractCode
     * @return
     */
    public static String handleGuaranteContractCode(String guaranteContractCode) {
        if (StringUtils.isBlank(guaranteContractCode)) {
            return guaranteContractCode;
        }
        return "B" + guaranteContractCode.replaceAll(REGEX_CHINESE, "").replaceAll("（|）|\\(|\\)|【|】|s|[|]", "");

    }

    public static String handlePledgeContractCode(String pledgeContractCode) {
        if (StringUtils.isBlank(pledgeContractCode)) {
            return pledgeContractCode;
        }
        String prefix = "Z";
        if (pledgeContractCode.contains("应收")) {
            prefix = "AR";
        } else if (pledgeContractCode.contains("股质")) {
            prefix = "STK";
        }
        return prefix + pledgeContractCode.replaceAll(REGEX_CHINESE, "").replaceAll("（|）|\\(|\\)|【|】|s|[|]", "");
    }

    public static String handleMortgageContractCode(String mortgageContractCode) {
        if (StringUtils.isBlank(mortgageContractCode)) {
            return mortgageContractCode;
        }
        return "D" + mortgageContractCode.replaceAll(REGEX_CHINESE, "").replaceAll("（|）|\\(|\\)|【|】|s|[|]", "");
    }

    /**
     * 校验渠道及必填参数
     */
    public static void checkListREQ(ReportListBaseREQ req) {
        QueryChannel channel = QueryChannel.of(req.getChannel());
        if (Objects.isNull(channel)) {
            throw new MithrasException("查询渠道不合法" + req.getChannel());
        }
        switch (channel) {
            case PROC:
                Util.errMissingParam(StringUtils.isBlank(req.getProcBusinessKey()), "流程业务主键不能为空");
                break;
            case PROC_BATCH:
                Util.errMissingParam(Objects.isNull(req.getBatchId()), "批次id不能为空");
                break;
            case EFFECT:
                if (!(req instanceof AccountListREQ) ) {
                    // 账户表不校验 导出接口也不校验
                    // Util.errMissingParam(Objects.isNull(req.getAccountId()), "账户id不能为空");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 生成批次号
     *
     * @param batchSeq
     * @return
     */
    public static String genBatchNo(int batchSeq) {
        String nowDateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("%s%02d", nowDateString, batchSeq);
    }

    /**
     * 自定义保证合同编号的生成规则
     *
     * @param contractCode 需要处理的编号
     * @param index        当前下表
     * @return String  保证合同编号
     */
    public static String getContractCodeUtil(String contractCode, Integer index) {
        if (StringUtils.isBlank(contractCode)) {
            return contractCode;
        }
        int splitIndex = contractCode.lastIndexOf(')');
        if (splitIndex == -1) {
            splitIndex = contractCode.lastIndexOf('）');
        }
        if (splitIndex == -1) {
            return contractCode;
        }
        return String.format("%s-0%s%s", contractCode.substring(0, splitIndex), index, contractCode.substring(splitIndex));
    }

    public static String contractCodeAddPaymentSeq(String originContractCode, String paymentCode) {
        if (StringUtils.isBlank(originContractCode) || StringUtils.isBlank(paymentCode)) {
            return originContractCode;
        }
        String[] paymentSplitArray = paymentCode.split("-");
        String paymentSeq = paymentSplitArray[paymentSplitArray.length - 1];
        if (!NumberUtil.isInteger(paymentSeq) && !"HZ".equals(paymentSeq)) {
            return originContractCode;
        }
        int splitIndex = originContractCode.lastIndexOf(')');
        if (splitIndex == -1) {
            splitIndex = originContractCode.lastIndexOf('）');
        }
        if (splitIndex == -1) {
            return String.format("%s-%s", originContractCode, paymentSeq);
        }
        String format = String.format("%s-%s%s", originContractCode.substring(0, splitIndex), paymentSeq, originContractCode.substring(splitIndex));
        return format.replace(" ", "").replace("[", "【").replace("]", "】").trim();
    }

    /**
     * paymentCode增强 直租的paymentCode用HZ结尾
     *
     * @param paymentCode
     * @param leaseType
     * @return
     */
    public static String bizCalPaymentCode(String paymentCode, String leaseType) {
        if (LeaseType.zhi_zu.name().equals(leaseType) && !paymentCode.endsWith("HZ")) {
            String[] paymentSplitArray = paymentCode.split("-");
            paymentSplitArray[paymentSplitArray.length - 1] = "HZ";
            return String.join("-", paymentSplitArray);
        }
        return paymentCode;
    }

    public static String getRepayRate(RepayCalcTypeEnum calcTypeEnum) {
        String res = "";
        if(Objects.isNull(calcTypeEnum)){
            return res;
        }
        switch (calcTypeEnum) {
            case DEBJ:
            case DEBX:
            case BGZHK:
            case QTDQHB: {
                res = AccountRepayRateEnum.LRREGULAR.getValue();
                break;
            }
            case DQYCHBFX:
            case YXFXDQHB:{
                res = AccountRepayRateEnum.NON_STAGES.getValue();
                break;
            }
            default: break;
        }
        return res;
    }
    public static String getVersion(String lastVersion){
        int versionNum = 1;
        if(CharSequenceUtil.isBlank(lastVersion)){
            return String.format("%04d%s", versionNum, LocalDateTimeUtil.format(LocalDate.now(), ReportConstants.DATE_FORMAT));
        }
        String substring = lastVersion.substring(0, 4);
        versionNum = Integer.parseInt(substring.substring(substring.lastIndexOf("0")));
        return String.format("%04d%s", versionNum + 1, LocalDateTimeUtil.format(LocalDate.now(), ReportConstants.DATE_FORMAT));
    }
}
