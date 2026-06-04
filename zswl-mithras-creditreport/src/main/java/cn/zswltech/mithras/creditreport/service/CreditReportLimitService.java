package cn.zswltech.mithras.creditreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportLimitRemoveREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.creditreport.mapper.CreditReportLimitMapper;
import cn.zswltech.mithras.creditreport.mapper.dto.credit.EB04;
import cn.zswltech.mithras.creditreport.mapper.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportLimit;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @description 征信报告-信用额度表
* @author vico
* @date 2025-11-14
*/
@Service
public class CreditReportLimitService extends ServiceImpl<CreditReportLimitMapper, CreditReportLimit> implements CreditReportParseInterface {

    @Resource
    private CreditReportLimitMapper creditReportLimitMapper;

    public CreditReportLimit buildBean(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId) {
        if (ObjectUtil.isEmpty(xjCreditReportJsonDTO) || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument())
                || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBD()) || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEBD().getEB04())) {
            return null;
        }

        //授信协议汇总信息单元
        EB04 eb04 = xjCreditReportJsonDTO.getDocument().getEBD().getEB04();

        CreditReportLimit bean = new CreditReportLimit();
        //bean.setCreditCode(creditCode);
        bean.setCreditReportId(creditReportId);
        bean.setCreditReportClientId(creditReportClientId);
        if (ObjectUtil.isNotEmpty(eb04)) {
            bean.setTotalAmount(eb04.getEB040J01());
            //非循环-已用额度
            bean.setUsedAmount(eb04.getEB040J02());
            //非循环-剩余可用额度
            bean.setRemainingAvailableAmount(eb04.getEB040J03());
            //循环-已用额度
            bean.setCycleTotalAmount(eb04.getEB040J04());
            //循环-已用额度
            bean.setCycleUsedAmount(eb04.getEB040J05());
            //循环-剩余可用额度
            bean.setCycleRemainingAvailableAmount(eb04.getEB040J06());
        }
        return bean;
    }


    @Transactional(rollbackFor = Throwable.class)
    public void modify(CreditReportLimitModifyREQ req) {
        CreditReportLimit originalInfo = creditReportLimitMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CreditReportLimit info = BeanUtil.copyProperties(req, CreditReportLimit.class);
        creditReportLimitMapper.updateById(info);
    }

    public Page<CreditReportLimit> list(CreditReportLimitListREQ req) {
        return this.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<CreditReportLimit>lambdaQuery()
        .eq(ObjectUtil.isNotEmpty(req.getCreditReportId()), CreditReportLimit::getCreditReportId, req.getCreditReportId())
        .eq(ObjectUtil.isNotEmpty(req.getCreditReportClientId()), CreditReportLimit::getCreditReportClientId, req.getCreditReportClientId()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(CreditReportLimitRemoveREQ req) {
        CreditReportLimit originalInfo = creditReportLimitMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        creditReportLimitMapper.deleteById(req.getId());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void execute(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId) {
        CreditReportLimit creditReportLimit = buildBean(xjCreditReportJsonDTO, creditReportId, creditReportClientId);
        if (ObjectUtil.isNotEmpty(creditReportLimit)) {
           save(creditReportLimit);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void clear(Long creditReportClientId) {
        creditReportLimitMapper.delete(Wrappers.<CreditReportLimit>lambdaQuery().eq(CreditReportLimit::getCreditReportClientId, creditReportClientId));
    }
}