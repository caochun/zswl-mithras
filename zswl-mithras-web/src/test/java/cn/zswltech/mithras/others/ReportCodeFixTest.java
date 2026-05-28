package cn.zswltech.mithras.others;

import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.report.mapper.draft.CrGuarantorDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrMortgageDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.CrPledgeDraftMapper;
import cn.zswltech.mithras.report.mapper.draft.model.CrGuarantorDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrMortgageDraft;
import cn.zswltech.mithras.report.mapper.draft.model.CrPledgeDraft;
import cn.zswltech.mithras.report.mapper.formal.CrGuarantorMapper;
import cn.zswltech.mithras.report.mapper.formal.CrMortgageMapper;
import cn.zswltech.mithras.report.mapper.formal.CrPledgeMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrGuarantor;
import cn.zswltech.mithras.report.mapper.formal.model.CrMortgage;
import cn.zswltech.mithras.report.mapper.formal.model.CrPledge;
import cn.zswltech.mithras.report.mapper.fullsnap.CrGuarantorFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrMortgageFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.CrPledgeFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrGuarantorFullSnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrMortgageFullSnap;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrPledgeFullSnap;
import cn.zswltech.mithras.report.mapper.procsnap.CrGuarantorProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrMortgageProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.CrPledgeProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrGuarantorProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrMortgageProcSnap;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrPledgeProcSnap;
import cn.zswltech.mithras.report.util.ReportBizUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * TODO
 *
 * @author wangchuanhao
 * @date 2023/2/13 9:43 AM
 */
public class ReportCodeFixTest extends ApplicationTest {

    @Resource
    private CrGuarantorMapper crGuarantorMapper;
    @Resource
    private CrGuarantorDraftMapper crGuarantorDraftMapper;
    @Resource
    private CrGuarantorProcSnapMapper crGuarantorProcSnapMapper;
    @Resource
    private CrGuarantorFullSnapMapper crGuarantorFullSnapMapper;
    @Resource
    private CrPledgeMapper crPledgeMapper;
    @Resource
    private CrPledgeDraftMapper crPledgeDraftMapper;
    @Resource
    private CrPledgeProcSnapMapper crPledgeProcSnapMapper;
    @Resource
    private CrPledgeFullSnapMapper crPledgeFullSnapMapper;
    @Resource
    private CrMortgageMapper crMortgageMapper;
    @Resource
    private CrMortgageDraftMapper crMortgageDraftMapper;
    @Resource
    private CrMortgageProcSnapMapper crMortgageProcSnapMapper;
    @Resource
    private CrMortgageFullSnapMapper crMortgageFullSnapMapper;

    @Test
    public void fix() {
        List<CrGuarantor> crGuarantorList = crGuarantorMapper.selectList(Wrappers.<CrGuarantor>lambdaQuery().isNotNull(CrGuarantor::getGuaranteContractCode));
        for (CrGuarantor crGuarantor : crGuarantorList) {
            crGuarantor.setGuaranteContractCode(contractCodeAddPaymentSeq(crGuarantor.getGuaranteContractCode(), crGuarantor.getPaymentApplyCode()));
            crGuarantor.setGuaranteContractCode2(ReportBizUtil.handleGuaranteContractCode(crGuarantor.getGuaranteContractCode()));
            crGuarantorMapper.updateById(crGuarantor);
        }
        List<CrGuarantorDraft> crGuarantorDraftList = crGuarantorDraftMapper.selectList(Wrappers.<CrGuarantorDraft>lambdaQuery().isNotNull(CrGuarantorDraft::getGuaranteContractCode));
        for (CrGuarantorDraft crGuarantor : crGuarantorDraftList) {
            crGuarantor.setGuaranteContractCode(contractCodeAddPaymentSeq(crGuarantor.getGuaranteContractCode(), crGuarantor.getPaymentApplyCode()));
            crGuarantor.setGuaranteContractCode2(ReportBizUtil.handleGuaranteContractCode(crGuarantor.getGuaranteContractCode()));
            crGuarantorDraftMapper.updateById(crGuarantor);
        }
        List<CrGuarantorProcSnap> crGuarantorProcSnapList = crGuarantorProcSnapMapper.selectList(Wrappers.<CrGuarantorProcSnap>lambdaQuery().isNotNull(CrGuarantorProcSnap::getGuaranteContractCode));
        for (CrGuarantorProcSnap crGuarantor : crGuarantorProcSnapList) {
            crGuarantor.setGuaranteContractCode(contractCodeAddPaymentSeq(crGuarantor.getGuaranteContractCode(), crGuarantor.getPaymentApplyCode()));
            crGuarantor.setGuaranteContractCode2(ReportBizUtil.handleGuaranteContractCode(crGuarantor.getGuaranteContractCode()));
            crGuarantorProcSnapMapper.updateById(crGuarantor);
        }
        List<CrGuarantorFullSnap> crGuarantorFullSnapList = crGuarantorFullSnapMapper.selectList(Wrappers.<CrGuarantorFullSnap>lambdaQuery().isNotNull(CrGuarantorFullSnap::getGuaranteContractCode));
        for (CrGuarantorFullSnap crGuarantor : crGuarantorFullSnapList) {
            crGuarantor.setGuaranteContractCode(contractCodeAddPaymentSeq(crGuarantor.getGuaranteContractCode(), crGuarantor.getPaymentApplyCode()));
            crGuarantor.setGuaranteContractCode2(ReportBizUtil.handleGuaranteContractCode(crGuarantor.getGuaranteContractCode()));
            crGuarantorFullSnapMapper.updateById(crGuarantor);
        }

        List<CrMortgage> crMortgageList = crMortgageMapper.selectList(Wrappers.lambdaQuery());
        for (CrMortgage crMortgage : crMortgageList) {
            crMortgage.setMortgageContractCode(contractCodeAddPaymentSeq(crMortgage.getMortgageContractCode(), crMortgage.getPaymentApplyCode()));
            crMortgage.setMortgageContractCode2(ReportBizUtil.handleGuaranteContractCode(crMortgage.getMortgageContractCode()));
            crMortgageMapper.updateById(crMortgage);
        }
        List<CrMortgageDraft> crMortgageDraftList = crMortgageDraftMapper.selectList(Wrappers.lambdaQuery());
        for (CrMortgageDraft crMortgage : crMortgageDraftList) {
            crMortgage.setMortgageContractCode(contractCodeAddPaymentSeq(crMortgage.getMortgageContractCode(), crMortgage.getPaymentApplyCode()));
            crMortgage.setMortgageContractCode2(ReportBizUtil.handleGuaranteContractCode(crMortgage.getMortgageContractCode()));
            crMortgageDraftMapper.updateById(crMortgage);
        }
        List<CrMortgageProcSnap> crMortgageProcSnapList = crMortgageProcSnapMapper.selectList(Wrappers.lambdaQuery());
        for (CrMortgageProcSnap crMortgage : crMortgageProcSnapList) {
            crMortgage.setMortgageContractCode(contractCodeAddPaymentSeq(crMortgage.getMortgageContractCode(), crMortgage.getPaymentApplyCode()));
            crMortgage.setMortgageContractCode2(ReportBizUtil.handleGuaranteContractCode(crMortgage.getMortgageContractCode()));
            crMortgageProcSnapMapper.updateById(crMortgage);
        }
        List<CrMortgageFullSnap> crMortgageFullSnapList = crMortgageFullSnapMapper.selectList(Wrappers.lambdaQuery());
        for (CrMortgageFullSnap crMortgage : crMortgageFullSnapList) {
            crMortgage.setMortgageContractCode(contractCodeAddPaymentSeq(crMortgage.getMortgageContractCode(), crMortgage.getPaymentApplyCode()));
            crMortgage.setMortgageContractCode2(ReportBizUtil.handleGuaranteContractCode(crMortgage.getMortgageContractCode()));
            crMortgageFullSnapMapper.updateById(crMortgage);
        }

        List<CrPledge> crPledgeList = crPledgeMapper.selectList(Wrappers.lambdaQuery());
        for (CrPledge crPledge : crPledgeList) {
            crPledge.setPledgeContractCode(contractCodeAddPaymentSeq(crPledge.getPledgeContractCode(), crPledge.getPaymentApplyCode()));
            crPledge.setPledgeContractCode2(ReportBizUtil.handleGuaranteContractCode(crPledge.getPledgeContractCode()));
            crPledgeMapper.updateById(crPledge);
        }
        List<CrPledgeDraft> crPledgeDraftList = crPledgeDraftMapper.selectList(Wrappers.lambdaQuery());
        for (CrPledgeDraft crPledge : crPledgeDraftList) {
            crPledge.setPledgeContractCode(contractCodeAddPaymentSeq(crPledge.getPledgeContractCode(), crPledge.getPaymentApplyCode()));
            crPledge.setPledgeContractCode2(ReportBizUtil.handleGuaranteContractCode(crPledge.getPledgeContractCode()));
            crPledgeDraftMapper.updateById(crPledge);
        }
        List<CrPledgeProcSnap> crPledgeProcSnapList = crPledgeProcSnapMapper.selectList(Wrappers.lambdaQuery());
        for (CrPledgeProcSnap crPledge : crPledgeProcSnapList) {
            crPledge.setPledgeContractCode(contractCodeAddPaymentSeq(crPledge.getPledgeContractCode(), crPledge.getPaymentApplyCode()));
            crPledge.setPledgeContractCode2(ReportBizUtil.handleGuaranteContractCode(crPledge.getPledgeContractCode()));
            crPledgeProcSnapMapper.updateById(crPledge);
        }
        List<CrPledgeFullSnap> crPledgeFullSnapList = crPledgeFullSnapMapper.selectList(Wrappers.lambdaQuery());
        for (CrPledgeFullSnap crPledge : crPledgeFullSnapList) {
            crPledge.setPledgeContractCode(contractCodeAddPaymentSeq(crPledge.getPledgeContractCode(), crPledge.getPaymentApplyCode()));
            crPledge.setPledgeContractCode2(ReportBizUtil.handleGuaranteContractCode(crPledge.getPledgeContractCode()));
            crPledgeFullSnapMapper.updateById(crPledge);
        }

    }

    public static String contractCodeAddPaymentSeq(String originContractCode, String paymentCode) {
        if (StringUtils.isBlank(originContractCode) || StringUtils.isBlank(paymentCode)) {
            return originContractCode;
        }
        String[] paymentSplitArray = paymentCode.split("-");
        String paymentSeq = paymentSplitArray[paymentSplitArray.length-1];
        if (!NumberUtil.isInteger(paymentSeq)) {
            return originContractCode;
        }
        int splitIndex = originContractCode.lastIndexOf(')');
        if (splitIndex == -1) {
            splitIndex = originContractCode.lastIndexOf('）');
        }
        if (splitIndex == -1) {
            return String.format("%s-%s", originContractCode, paymentSeq);
        }
        return String.format("%s-%s%s", originContractCode.substring(0, splitIndex), paymentSeq, originContractCode.substring(splitIndex));
    }

}
