package cn.zswltech.mithras.creditreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.creditreport.CreditReportRecordDetailsAddREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportRecordDetailsListREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportRecordDetailsModifyREQ;
import cn.zswltech.mithras.dto.creditreport.CreditReportRecordDetailsRemoveREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.creditreport.enums.CreditReportDistributionMethodTypeEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportFiveClassificationEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportGuaranteeMethodEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportLastRepaymentTypeEnum;
import cn.zswltech.mithras.creditreport.enums.CreditReportRecordBusinessTypeEnum;
import cn.zswltech.mithras.creditreport.mapper.CreditReportRecordDetailsMapper;
import cn.zswltech.mithras.creditreport.dto.credit.ED01;
import cn.zswltech.mithras.creditreport.dto.credit.ED01A;
import cn.zswltech.mithras.creditreport.dto.credit.ED01B;
import cn.zswltech.mithras.creditreport.dto.credit.ED01BH;
import cn.zswltech.mithras.creditreport.dto.credit.ED01C;
import cn.zswltech.mithras.creditreport.dto.credit.ED01CH;
import cn.zswltech.mithras.creditreport.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.creditreport.model.CreditReportRecordDetails;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
* @description 征信报告-信贷记录明细表
* @author vico
* @date 2025-11-28
*/
@Service
public class CreditReportRecordDetailsService extends ServiceImpl<CreditReportRecordDetailsMapper, CreditReportRecordDetails> implements CreditReportParseInterface {

    @Resource
    private CreditReportRecordDetailsMapper creditReportRecordDetailsMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(CreditReportRecordDetailsAddREQ req) {
        CreditReportRecordDetails info = BeanUtil.copyProperties(req, CreditReportRecordDetails.class);
        creditReportRecordDetailsMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(CreditReportRecordDetailsModifyREQ req) {
        CreditReportRecordDetails originalInfo = creditReportRecordDetailsMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CreditReportRecordDetails info = BeanUtil.copyProperties(req, CreditReportRecordDetails.class);
        creditReportRecordDetailsMapper.updateById(info);
    }

    public List<CreditReportRecordDetails> list(CreditReportRecordDetailsListREQ req) {
        return this.list(Wrappers.<CreditReportRecordDetails>lambdaQuery()
                //.ne(CreditReportRecordDetails::getFiveClassification, CreditReportFiveClassificationEnum.NORMAL.name())
                //.gt(CreditReportRecordDetails::getTotalOverdueAmount, 0)
        .eq(CreditReportRecordDetails::getCreditReportId, req.getCreditReportId())
        .eq(CreditReportRecordDetails::getCreditReportClientId, req.getCreditReportClientId()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(CreditReportRecordDetailsRemoveREQ req) {
        CreditReportRecordDetails originalInfo = creditReportRecordDetailsMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        creditReportRecordDetailsMapper.deleteById(req.getId());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void execute(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId) {
        List<CreditReportRecordDetails> beanList = buildBeanList(xjCreditReportJsonDTO, creditReportId, creditReportClientId);
        if (ObjectUtil.isNotEmpty(beanList)) {
            saveBatch(beanList);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void clear(Long creditReportClientId) {
        creditReportRecordDetailsMapper.delete(Wrappers.<CreditReportRecordDetails>lambdaQuery().eq(CreditReportRecordDetails::getCreditReportClientId, creditReportClientId));
    }

    public List<CreditReportRecordDetails> buildBeanList(XJCreditReportJsonDTO xjCreditReportJsonDTO, Long creditReportId, Long creditReportClientId) {
        if (ObjectUtil.isEmpty(xjCreditReportJsonDTO) || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument())
                || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEDA()) ||
                ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEDA().getED01())
                || ObjectUtil.isEmpty(xjCreditReportJsonDTO.getDocument().getEDA().getED01())) {
            return null;
        }

        //授信协议汇总信息单元
        List<ED01> ed01s = xjCreditReportJsonDTO.getDocument().getEDA().getED01();
        List<CreditReportRecordDetails> beans = new ArrayList<>();
        ED01A ed01A;
        ED01B ed01B;
        ED01C ed01C;
        List<ED01BH> ed01BH;
        List<String> ED01CQ01;
        ED01BH ed01BH1;
        if (ObjectUtil.isNotEmpty(ed01s)) {
            for (ED01 ed01 : ed01s) {
                CreditReportRecordDetails bean = new CreditReportRecordDetails();
                // bean.setCreditCode(creditCode);
                bean.setCreditReportId(creditReportId);
                bean.setCreditReportClientId(creditReportClientId);
                ed01A = ed01.getED01A();
                if (ObjectUtil.isNotEmpty(ed01A)) {
                    //账户编号
                    bean.setAccountNumber(ed01A.getED01AI01());
                    //授信机构
                    bean.setCreditorInstitution(getCreditorInstitution(ed01A.getED01AD04(), ed01A.getED01AI02()));
                    //业务种类
                    bean.setBusinessType(Optional.ofNullable(CreditReportRecordBusinessTypeEnum.finaByCode(ed01A.getED01AD05())).map(CreditReportRecordBusinessTypeEnum::name).orElse(String.valueOf(ed01A.getED01AD05())));
                    //开立日期
                    bean.setOpeningDate(ed01A.getED01AR01());
                    //到期日
                    bean.setExpirationDate(ed01A.getED01AR02());
                    //币种
                    bean.setCurrency(ed01A.getED01AD07());
                    //借款金额
                    bean.setLoanAmount(ed01A.getED01AJ01());
                    //发放形式

                    bean.setDistributionMethod(Optional.ofNullable(CreditReportDistributionMethodTypeEnum.finaByCode(ed01A.getED01AD10())).map(CreditReportDistributionMethodTypeEnum::name).orElse(String.valueOf(ed01A.getED01AD10())));
                    //担保方式
                    bean.setGuaranteeMethod(Optional.ofNullable(CreditReportGuaranteeMethodEnum.finaByCode(ed01A.getED01AD08())).map(CreditReportGuaranteeMethodEnum::name).orElse(String.valueOf(ed01A.getED01AD08())));
                    //授信协议编号
                    bean.setCreditAgreementNumber(ed01A.getED01AI03());
                    //信息报告日期
                    bean.setInformationReportDate(ed01A.getED01AR04());
                }
                //还款表现信息段
                ed01B = ed01.getED01B();
                if (ObjectUtil.isNotEmpty(ed01B)) {
                    ed01BH = ed01B.getED01BH();
                    if (ObjectUtil.isNotEmpty(ed01BH)) {
                        ed01BH1 = ed01BH.get(0);
                        //余额
                        bean.setBalance(ed01BH1.getED01BJ01());
                        //五级分类
                        bean.setFiveClassification(Optional.ofNullable(CreditReportFiveClassificationEnum.finaByCode(ed01BH1.getED01BD01())).map(CreditReportFiveClassificationEnum::name).orElse(String.valueOf(ed01BH1.getED01BD01())));
                        bean.setTotalOverdueAmount(ed01BH1.getED01BJ04());
                        bean.setOverduePrincipal(ed01BH1.getED01BJ05());
                        //逾期月数
                        bean.setOverdueMonth(ed01BH1.getED01BS02());
                        //最近一次还款日期
                        bean.setLastRepaymentDate(ed01BH1.getED01BR04());
                        //最近一次还款总额
                        bean.setLastRepaymentAmount(ed01BH1.getED01BJ02());
                        //最近一次还款形式
                        bean.setLastRepaymentType(Optional.ofNullable(CreditReportLastRepaymentTypeEnum.finaByCode(ed01BH1.getED01BD02())).map(CreditReportLastRepaymentTypeEnum::name).orElse(String.valueOf(ed01BH1.getED01BD02())));
                    }
                }
                ed01C = ed01.getED01C();
                if (ObjectUtil.isNotEmpty(ed01C) && ObjectUtil.isNotEmpty(ed01C.getED01CH())) {
                    ED01CQ01 = ed01C.getED01CH().stream().map(ED01CH::getED01CQ01).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
                    //特定交易提示
                    bean.setSpecificTransactionPrompts(String.join("/", ED01CQ01));
                }
                beans.add(bean);
            }
        }
        return beans;
    }

    private String getCreditorInstitution(String ED01AD04, String ED01AI02) {
        if (ObjectUtil.isEmpty(ED01AI02)) {
            return null;
        }
        return startsWithLetter(ED01AI02) ? ED01AI02 : ED01AD04 + ED01AI02;
    }

    private boolean startsWithLetter(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return Pattern.matches("^[a-zA-Z].*", str);
    }
}
