package cn.zswltech.mithras.creditreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportUnsettledSummaryRemoveREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.creditreport.enums.CreditReportBusinessTypeEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportPaymentGuaranteeModuleEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportPaymentModuleEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportQualityClassificationEnum;
import cn.zswltech.mithras.creditreport.mapper.CreditReportUnsettledSummaryMapper;
import cn.zswltech.mithras.creditreport.dto.credit.EB02;
import cn.zswltech.mithras.creditreport.dto.credit.EB02A;
import cn.zswltech.mithras.creditreport.dto.credit.EB02AH;
import cn.zswltech.mithras.creditreport.dto.credit.EB03;
import cn.zswltech.mithras.creditreport.dto.credit.EB03A;
import cn.zswltech.mithras.creditreport.dto.credit.EB03AH;
import cn.zswltech.mithras.creditreport.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.creditreport.model.CreditReportUnsettledSummary;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
* @description 征信报告-未结清信贷及授信信息表
* @author vico
* @date 2025-11-14
*/
@Service
public class CreditReportUnsettledSummaryService extends ServiceImpl<CreditReportUnsettledSummaryMapper, CreditReportUnsettledSummary> implements CreditReportParseInterface {

    @Resource
    private CreditReportUnsettledSummaryMapper creditReportUnsettledSummaryMapper;

    public List<CreditReportUnsettledSummary> buildBeanList(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId) {
        if (ObjectUtil.isEmpty(xjCreditReportJsonDTO) || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument())) {
            return null;
        }

        //借贷交易汇总信息单元
        EB02 eb02 = null;
        if (ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBB())
                || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBB().getEB02())) {
        } else {
            eb02 = xjCreditReportJsonDTO.getDocument().getEBB().getEB02();
        }
        List<CreditReportUnsettledSummary> beans = new ArrayList<>();

        //未结清借贷交易汇总信息段
        if (ObjectUtil.isNotEmpty(eb02)) {
            EB02A eb02A = eb02.getEB02A();
            if (ObjectUtil.isNotEmpty(eb02A)) {
                //其他借贷交易分类汇总信息
                List<EB02AH> eb02AH = eb02A.getEB02AH();
                if (ObjectUtil.isNotEmpty(eb02AH)) {
                    eb02AH.forEach(e -> {
                        CreditReportUnsettledSummary bean = new CreditReportUnsettledSummary();
                        //bean.setCreditCode(creditCode);
                        bean.setCreditReportId(creditReportId);
                        bean.setCreditReportClientId(creditReportClientId);
                        bean.setPaymentModule(CreditReportBusinessTypeEnum.OUT_STANDING_LOAD_TRANSACTIONS.name());
                        //业务类型
                        bean.setPaymentType(Optional.ofNullable(CreditReportPaymentModuleEnum.finaByCode(e.getEB02AD01())).map(CreditReportPaymentModuleEnum::name).orElse(String.valueOf(e.getEB02AD01())));
                        //资产质量分类
                        bean.setFundClassification(Optional.ofNullable(CreditReportQualityClassificationEnum.finaByCode(e.getEB02AD02())).map(CreditReportQualityClassificationEnum::name).orElse(String.valueOf(e.getEB02AD02())));
                        //账户数
                        bean.setAccountNumber(e.getEB02AS04());
                        //账户余额
                        bean.setAccountAmount(e.getEB02AJ06());
                        beans.add(bean);
                    });
                }
            }
        }
        //未结清担保交易汇总信息段
        EB03 eb03 = null;
        if (ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBC())
                || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBC().getEB03())) {
        } else {
            eb03 = xjCreditReportJsonDTO.getDocument().getEBC().getEB03();
        }
        List<String> guarantee = CreditReportPaymentGuaranteeModuleEnum.getGuarantee();
        if (ObjectUtil.isNotEmpty(eb03)) {
            EB03A eb03A = eb03.getEB03A();
            if (ObjectUtil.isNotEmpty(eb03A)) {
                //其他借贷交易分类汇总信息
                List<EB03AH> eb03AH = eb03A.getEB03AH();
                if (ObjectUtil.isEmpty(eb03AH)) {
                    return null;
                }
                eb03AH.forEach(e -> {
                    CreditReportUnsettledSummary bean = new CreditReportUnsettledSummary();
                    //bean.setCreditCode(creditCode);
                    bean.setCreditReportId(creditReportId);
                    bean.setCreditReportClientId(creditReportClientId);
                    //业务类型
                    bean.setPaymentType(Optional.ofNullable(CreditReportPaymentGuaranteeModuleEnum.finaByCode(e.getEB03AD01())).map(CreditReportPaymentGuaranteeModuleEnum::name).orElse(String.valueOf(e.getEB03AD01())));
                    //
                    if (guarantee.contains(bean.getPaymentType())) {
                        bean.setPaymentModule(CreditReportBusinessTypeEnum.SECURED_TRANSACTION_SUMMARY_UNIT_OTHER.name());
                    } else {
                        bean.setPaymentModule(CreditReportBusinessTypeEnum.SECURED_TRANSACTION_SUMMARY_UNIT.name());
                    }
                    //资产质量分类
                    bean.setFundClassification(Optional.ofNullable(CreditReportQualityClassificationEnum.finaByCode(e.getEB03AD02())).map(CreditReportQualityClassificationEnum::name).orElse(String.valueOf(e.getEB03AD02())));
                    //账户数
                    bean.setAccountNumber(e.getEB03AS02());
                    //账户余额
                    bean.setAccountAmount(e.getEB03AJ01());
                    beans.add(bean);
                });
            }
        }
        return beans;
    }


    @Transactional(rollbackFor = Throwable.class)
    public void modify(CreditReportUnsettledSummaryModifyREQ req) {
        CreditReportUnsettledSummary originalInfo = creditReportUnsettledSummaryMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CreditReportUnsettledSummary info = BeanUtil.copyProperties(req, CreditReportUnsettledSummary.class);
        creditReportUnsettledSummaryMapper.updateById(info);
    }

    public List<CreditReportUnsettledSummary> list(CreditReportUnsettledSummaryListREQ req) {
        return this.list(Wrappers.<CreditReportUnsettledSummary>lambdaQuery()
        .eq(ObjectUtil.isNotEmpty(req.getCreditReportId()), CreditReportUnsettledSummary::getCreditReportId, req.getCreditReportId())
                        .eq(ObjectUtil.isNotEmpty(req.getCreditReportClientId()), CreditReportUnsettledSummary::getCreditReportClientId, req.getCreditReportClientId())
        );
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(CreditReportUnsettledSummaryRemoveREQ req) {
        CreditReportUnsettledSummary originalInfo = creditReportUnsettledSummaryMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        creditReportUnsettledSummaryMapper.deleteById(req.getId());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void execute(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId) {
        List<CreditReportUnsettledSummary> beanList = buildBeanList(xjCreditReportJsonDTO, creditReportId, creditReportClientId);
        if (ObjectUtil.isNotEmpty(beanList)) {
            saveBatch(beanList);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void clear(Long creditReportClientId) {
        creditReportUnsettledSummaryMapper.delete(Wrappers.<CreditReportUnsettledSummary>lambdaQuery().eq(CreditReportUnsettledSummary::getCreditReportClientId, creditReportClientId));
    }

}
