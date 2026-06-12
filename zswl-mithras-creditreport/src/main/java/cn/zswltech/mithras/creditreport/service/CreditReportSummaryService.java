package cn.zswltech.mithras.creditreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.creditreport.CreditReportBaseDetailREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportSummaryAddREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportSummaryDetailRSP;
import cn.zswltech.mithras.dto.creditreport.CreditReportSummaryListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportSummaryModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportSummaryRemoveREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.creditreport.mapper.CreditReportSummaryMapper;
import cn.zswltech.mithras.creditreport.dto.credit.EB01;
import cn.zswltech.mithras.creditreport.dto.credit.EB01A;
import cn.zswltech.mithras.creditreport.dto.credit.EB01B;
import cn.zswltech.mithras.creditreport.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.creditreport.model.CreditReportSummary;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @description 征信报告-信息概要表
* @author vico
* @date 2025-11-14
*/
@Service
public class CreditReportSummaryService extends ServiceImpl<CreditReportSummaryMapper, CreditReportSummary> implements CreditReportParseInterface {

    @Resource
    private CreditReportSummaryMapper creditReportSummaryMapper;

    public CreditReportSummary buildBean(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId) {
        if (ObjectUtil.isEmpty(xjCreditReportJsonDTO) || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument())
        || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBA()) || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBA().getEB01())) {
            return null;
        }

        //信息概要表
        EB01 eb01 = xjCreditReportJsonDTO.getDocument().getEBA().getEB01();
        //信贷交易提示信息段
        EB01A eb01A = eb01.getEB01A();
        //非信贷交易及公共信息提示信息段
        EB01B eb01B = eb01.getEB01B();
        CreditReportSummary bean = new CreditReportSummary();
        //bean.setCreditCode(creditCode);
        bean.setCreditReportId(creditReportId);
        bean.setCreditReportClientId(creditReportClientId);
        if (ObjectUtil.isNotEmpty(eb01A)) {
            //首次有信贷交易的年份
            bean.setFirstCredityear(eb01A.getEB01AR01());
            //发生信贷交易的机构数
            bean.setCreditOrganizationNumber(eb01A.getEB01AS01());
            //当前有未结清 信贷交易的机构数
            bean.setUnsettledCreditOrganizationNumber(eb01A.getEB01AS02());
            //首次有相关还款责任的年份
            bean.setFirstRepaymentResponsibilityYear(eb01A.getEB01AR02());
            //借贷交易-余额
            bean.setLoanTransactionBalance(eb01A.getEB01AJ01());
            //借贷交易-被追偿余额
            bean.setLoanTransactionRecoveryBalance(eb01A.getEB01AJ02());
            //借贷交易-关注类余额
            bean.setLoanTransactionFocusBalance(eb01A.getEB01AJ03());
            //借贷交易-不良类余额
            bean.setLoanTransactionBadBalance(eb01A.getEB01AJ04());
            //担保交易-余额
            bean.setGuaranteeTransactionBalance(eb01A.getEB01AJ05());
            //担保交易-关注类余额
            bean.setGuaranteeTransactionFocusBalance(eb01A.getEB01AJ06());
            //担保交易-不良类余额
            bean.setGuaranteeTransactionBadBalance(eb01A.getEB01AJ07());
        }
        //非信贷交易及公共信息提示信息段
        if(ObjectUtil.isNotEmpty(eb01B)) {
            //非信贷交易账户数
            bean.setNonCreditTransactionNumber(eb01B.getEB01BS01());
            //欠税记录条数
            bean.setTaxArrearsRecordsNumber(eb01B.getEB01BS02());
            //民事判决记录条数
            bean.setCivilJudgmentRecordsNumber(eb01B.getEB01BS03());
            //强制执行记录条数
            bean.setMandatoryExecutionRecordsNumber(eb01B.getEB01BS04());
            //行政处罚记录条数
            bean.setAdministrativePenaltyRecordsNumber(eb01B.getEB01BS05());
        }

        return bean;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(CreditReportSummaryAddREQ req) {
        CreditReportSummary info = BeanUtil.copyProperties(req, CreditReportSummary.class);
        creditReportSummaryMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(CreditReportSummaryModifyREQ req) {
        CreditReportSummary originalInfo = creditReportSummaryMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CreditReportSummary info = BeanUtil.copyProperties(req, CreditReportSummary.class);
        creditReportSummaryMapper.updateById(info);
    }

    public Page<CreditReportSummary> list(CreditReportSummaryListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(CreditReportSummaryRemoveREQ req) {
        CreditReportSummary originalInfo = creditReportSummaryMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        creditReportSummaryMapper.deleteById(req.getId());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void execute(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId) {
        CreditReportSummary bean = buildBean(xjCreditReportJsonDTO, creditReportId, creditReportClientId);
        if (ObjectUtil.isNotEmpty(bean)) {
            save(bean);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void clear(Long creditReportClientId) {
        creditReportSummaryMapper.delete(Wrappers.<CreditReportSummary>lambdaQuery().eq(CreditReportSummary::getCreditReportClientId, creditReportClientId));
    }

    public CreditReportSummaryDetailRSP detail(CreditReportBaseDetailREQ req) {
        CreditReportSummary one = this.getOne(Wrappers.<CreditReportSummary>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getCreditReportId()), CreditReportSummary::getCreditReportId, req.getCreditReportId())
                .eq(ObjectUtil.isNotEmpty(req.getCreditReportClientId()), CreditReportSummary::getCreditReportClientId, req.getCreditReportClientId())
                .last(StringUtil.mysqlLimitOne()));
        return BeanUtil.copyProperties(one, CreditReportSummaryDetailRSP.class);
    }
}
