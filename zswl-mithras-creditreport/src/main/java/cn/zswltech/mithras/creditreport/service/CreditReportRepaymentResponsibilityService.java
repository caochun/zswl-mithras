package cn.zswltech.mithras.creditreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportRepaymentResponsibilityRemoveREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.creditreport.enums.CreditReportRepaymentLiabilityEnum;
import cn.zswltech.mithras.creditreport.mapper.CreditReportRepaymentResponsibilityMapper;
import cn.zswltech.mithras.creditreport.mapper.dto.credit.EB05A;
import cn.zswltech.mithras.creditreport.mapper.dto.credit.EB05AH;
import cn.zswltech.mithras.creditreport.mapper.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportRepaymentResponsibility;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
* @description 征信报告-相关还款责任信息概要表
* @author vico
* @date 2025-11-14
*/
@Service
public class CreditReportRepaymentResponsibilityService extends ServiceImpl<CreditReportRepaymentResponsibilityMapper, CreditReportRepaymentResponsibility> implements CreditReportParseInterface {

    @Resource
    private CreditReportRepaymentResponsibilityMapper creditReportRepaymentResponsibilityMapper;


    public List<CreditReportRepaymentResponsibility> buildBeanList(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId) {
        if (ObjectUtil.isEmpty(xjCreditReportJsonDTO) || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument())
                || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBE()) ||
                ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBE().getEB05())
        || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBE().getEB05().getEB05A())) {
            return null;
        }

        //授信协议汇总信息单元
        EB05A eb05A1 = xjCreditReportJsonDTO.getDocument().getEBE().getEB05().getEB05A();

        List<CreditReportRepaymentResponsibility> beans = new ArrayList<>();

        if (ObjectUtil.isNotEmpty(eb05A1)) {
            List<EB05AH> eb05AH = eb05A1.getEB05AH();
            if (ObjectUtil.isNotEmpty(eb05AH)) {
                eb05AH.forEach(e -> {
                    CreditReportRepaymentResponsibility bean = new CreditReportRepaymentResponsibility();
                   // bean.setCreditCode(creditCode);
                    bean.setCreditReportId(creditReportId);
                    bean.setCreditReportClientId(creditReportClientId);
                    bean.setResponsibilityType(Optional.ofNullable(CreditReportRepaymentLiabilityEnum.finaByCode(e.getEB05AD01())).map(CreditReportRepaymentLiabilityEnum::name).orElse(String.valueOf(e.getEB05AD01())));
                    //被追偿业务-还款责任金额
                    bean.setRecoverableRepaymentResponsibilityAmount(e.getEB05AJ01());
                    //被追偿业务-账户数
                    bean.setRecoverableAccountNumber(e.getEB05AS02());
                    //被追偿业务-余额
                    bean.setRecoverableBalance(e.getEB05AJ02());
                    //其他借贷交易-还款责任金额
                    bean.setOtherRepaymentResponsibilityAmount(e.getEB05AJ03());
                    //其他借贷交易-账户数
                    bean.setOtherAccountNumber(e.getEB05AS03());
                    //其他借贷交易-余额
                    bean.setOtherBalance(e.getEB05AJ04());
                    //其他借贷交易-关注类余额
                    bean.setOtherFocusBalance(e.getEB05AJ05());
                    //其他借贷交易-不良类余额
                    bean.setOtherBadBalance(e.getEB05AJ06());
                    beans.add(bean);
                });

            }
        }
        return beans;
    }


    @Transactional(rollbackFor = Throwable.class)
    public void modify(CreditReportRepaymentResponsibilityModifyREQ req) {
        CreditReportRepaymentResponsibility originalInfo = creditReportRepaymentResponsibilityMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CreditReportRepaymentResponsibility info = BeanUtil.copyProperties(req, CreditReportRepaymentResponsibility.class);
        creditReportRepaymentResponsibilityMapper.updateById(info);
    }

    public Page<CreditReportRepaymentResponsibility> list(CreditReportRepaymentResponsibilityListREQ req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CreditReportRepaymentResponsibility>lambdaQuery()
        .eq(ObjectUtil.isNotEmpty(req.getCreditReportClientId()), CreditReportRepaymentResponsibility::getCreditReportId, req.getCreditReportId())
                .eq(ObjectUtil.isNotEmpty(req.getCreditReportClientId()), CreditReportRepaymentResponsibility::getCreditReportClientId, req.getCreditReportClientId()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(CreditReportRepaymentResponsibilityRemoveREQ req) {
        CreditReportRepaymentResponsibility originalInfo = creditReportRepaymentResponsibilityMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        creditReportRepaymentResponsibilityMapper.deleteById(req.getId());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void execute(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId) {
        List<CreditReportRepaymentResponsibility> beanList = buildBeanList(xjCreditReportJsonDTO, creditReportId, creditReportClientId);
        if (ObjectUtil.isNotEmpty(beanList)) {
            saveBatch(beanList);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void clear(Long creditReportClientId) {
        creditReportRepaymentResponsibilityMapper.delete(Wrappers.<CreditReportRepaymentResponsibility>lambdaQuery().eq(CreditReportRepaymentResponsibility::getCreditReportClientId, creditReportClientId));
    }
}