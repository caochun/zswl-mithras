package cn.zswltech.mithras.others;

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
 * @date 2023/4/23 7:59 PM
 */
public class FixCrContractCode extends ApplicationTest {

    @Resource
    private CrPledgeMapper crPledgeMapper;
    @Resource
    private CrPledgeDraftMapper crPledgeDraftMapper;
    @Resource
    private CrGuarantorMapper crGuarantorMapper;
    @Resource
    private CrGuarantorDraftMapper crGuarantorDraftMapper;
    @Resource
    private CrMortgageMapper crMortgageMapper;
    @Resource
    private CrMortgageDraftMapper crMortgageDraftMapper;

    @Test
    public void fix() {
        List<CrPledge> crPledgeList = crPledgeMapper.selectList(Wrappers.lambdaQuery());
        crPledgeList.forEach(c -> {
            if (StringUtils.isBlank(c.getPledgeContractCode()) || !c.getPaymentApplyCode().endsWith("-HZ")) {
                return;
            }
            String[] ss = c.getPledgeContractCode().split("-");
            ss[ss.length - 1] = "HZ)号";
            c.setPledgeContractCode(String.join("-", ss));
            c.setPledgeContractCode2(ReportBizUtil.handlePledgeContractCode(c.getPledgeContractCode()));
            crPledgeMapper.updateById(c);
        });
        List<CrPledgeDraft> crPledgeDraftList = crPledgeDraftMapper.selectList(Wrappers.lambdaQuery());
        crPledgeDraftList.forEach(c -> {
            if (StringUtils.isBlank(c.getPledgeContractCode()) || !c.getPaymentApplyCode().endsWith("-HZ")) {
                return;
            }
            String[] ss = c.getPledgeContractCode().split("-");
            ss[ss.length - 1] = "HZ)号";
            c.setPledgeContractCode(String.join("-", ss));
            c.setPledgeContractCode2(ReportBizUtil.handlePledgeContractCode(c.getPledgeContractCode()));
            crPledgeDraftMapper.updateById(c);
        });
        List<CrGuarantor> crGuarantorList = crGuarantorMapper.selectList(Wrappers.lambdaQuery());
        crGuarantorList.forEach(c -> {
            if (StringUtils.isBlank(c.getGuaranteContractCode()) || !c.getPaymentApplyCode().endsWith("-HZ")) {
                return;
            }
            String[] ss = c.getGuaranteContractCode().split("-");
            ss[ss.length - 1] = "HZ)号";
            c.setGuaranteContractCode(String.join("-", ss));
            c.setGuaranteContractCode2(ReportBizUtil.handleGuaranteContractCode(c.getGuaranteContractCode()));
            crGuarantorMapper.updateById(c);
        });
        List<CrGuarantorDraft> crGuarantorDraftList = crGuarantorDraftMapper.selectList(Wrappers.lambdaQuery());
        crGuarantorDraftList.forEach(c -> {
            if (StringUtils.isBlank(c.getGuaranteContractCode()) || !c.getPaymentApplyCode().endsWith("-HZ")) {
                return;
            }
            String[] ss = c.getGuaranteContractCode().split("-");
            ss[ss.length - 1] = "HZ)号";
            c.setGuaranteContractCode(String.join("-", ss));
            c.setGuaranteContractCode2(ReportBizUtil.handleGuaranteContractCode(c.getGuaranteContractCode()));
            crGuarantorDraftMapper.updateById(c);
        });
        List<CrMortgage> crMortgageList = crMortgageMapper.selectList(Wrappers.lambdaQuery());
        crMortgageList.forEach(c -> {
            if (StringUtils.isBlank(c.getMortgageContractCode()) || !c.getPaymentApplyCode().endsWith("-HZ")) {
                return;
            }
            String[] ss = c.getMortgageContractCode().split("-");
            ss[ss.length - 1] = "HZ)号";
            c.setMortgageContractCode(String.join("-", ss));
            c.setMortgageContractCode2(ReportBizUtil.handleMortgageContractCode(c.getMortgageContractCode()));
            crMortgageMapper.updateById(c);
        });
        List<CrMortgageDraft> crMortgageDraftList = crMortgageDraftMapper.selectList(Wrappers.lambdaQuery());
        crMortgageDraftList.forEach(c -> {
            if (StringUtils.isBlank(c.getMortgageContractCode()) || !c.getPaymentApplyCode().endsWith("-HZ")) {
                return;
            }
            String[] ss = c.getMortgageContractCode().split("-");
            ss[ss.length - 1] = "HZ)号";
            c.setMortgageContractCode(String.join("-", ss));
            c.setMortgageContractCode2(ReportBizUtil.handleMortgageContractCode(c.getMortgageContractCode()));
            crMortgageDraftMapper.updateById(c);
        });
    }

}
