package cn.zswltech.mithras.application.orchestration.adapter.workflow;
import cn.zswltech.mithras.workflow.process.prepare.RentCollectionMonthDetailService;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.afterlease.genhtml.PaymentNoticeHtmlRender;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.workflow.persistence.model.RentCollectionMonthDetail;
import cn.zswltech.mithras.contract.versioning.service.ContractLeasePriceLibService;
import cn.zswltech.mithras.foundation.util.FreeMarkerUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.workflow.process.prepare.ProcessPrepareRenderPort;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.afterlease.genhtml.PaymentNoticeHtmlRender.PAYMENT_DOCX_NAME;
import static cn.zswltech.mithras.afterlease.genhtml.PaymentNoticeHtmlRender.TEMPLATE_FILE_NAME;

/**
 * @author luyi
 */
@Component
public class CommonProcessPrepareRenderAdapter implements ProcessPrepareRenderPort {
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Autowired
    private ContractLeasePriceLibService leasePriceLibService;
    @Autowired
    private ContractTenantryLibMapper contractTenantryLibMapper;
    @SneakyThrows
    @Override
    public byte[] render(Long detailId) {
        // 生成
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FreeMarkerUtil.generate(outputStream, TEMPLATE_FILE_NAME, buildRenderMap(detailId));
        return outputStream.toByteArray();
    }

    @SneakyThrows
    @Override
    public void downDocx(OutputStream outputStream, Long detailId) {
        HashMap<String, Object> map = buildRenderMap(detailId);
        XWPFTemplate template = XWPFTemplate.compile(PaymentNoticeHtmlRender.class.getResourceAsStream(PAYMENT_DOCX_NAME)).render(map);
        template.writeAndClose(outputStream);
    }

    private HashMap<String, Object> buildRenderMap(Long detailId) {
        RentCollectionMonthDetail detail = getBean(RentCollectionMonthDetailService.class).getById(detailId);
        // 捞数据
        Client client = getBean(ClientMapper.class).selectById(detail.getClientId());
        //查还款流水（collection_base_info ）的id
        Long collectionId = detail.getCollectionId();
        //根据还款流水查合同id
        Long contractId = getBean(CollectionBaseInfoService.class).getById(collectionId).getContractId();
        //再根据合同id反查所有还款流水中最后的期项
        Integer lastPhase = getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId,contractId).orderByDesc(CollectionBaseInfo::getPhase)).get(0).getPhase();
        //判断当前是否最后期项，然后赋值名义价款
        Long lNominalPrice = null;
        //
        String version = "";//版本号
        //取最新合同版本号
        CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, contractId)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        StringBuilder tenantrys = new StringBuilder();//拼接后的承租人
        if (ObjectUtil.isNotNull(commonVersion)) {
            version = commonVersion.getVersion();
            //如果是最后期项
            if(Objects.equals(lastPhase, detail.getPhase())){
                //取最新报价方案
                ContractLeasePrice contractLeasePrice = leasePriceLibService.getByVersion(contractId, commonVersion.getVersion());
                lNominalPrice = contractLeasePrice.getNominalPrice();
            }

            //主承租人
            ContractTenantryLib contractTenantryLib = contractTenantryLibMapper.selectOne(Wrappers.<ContractTenantryLib>lambdaQuery()
                            .eq(ContractTenantryLib::getContractId, contractId)
                            .eq(ContractTenantryLib::getVersion, version)
                            .eq(ContractTenantryLib::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                    //.eq(ContractTenantryLib::getIsReport, YesOrNoNumberEnum.YES.getCode())
            );
            if(ObjectUtil.isNotNull(contractTenantryLib)){
                tenantrys.append(contractTenantryLib.getLesseeName());
                // 联合承租人
                List<ContractTenantryLib> contractTenantryLibList = new ArrayList<>();
                contractTenantryLibList = contractTenantryLibMapper.selectList(Wrappers.<ContractTenantryLib>lambdaQuery()
                                .eq(ContractTenantryLib::getContractId, contractId)
                                .eq(ContractTenantryLib::getVersion, version)
                                .eq(ContractTenantryLib::getLesseeType, LesseeTypeEnum.JOINT_LESSEE.name())
                        //.eq(ContractTenantryLib::getIsReport, YesOrNoNumberEnum.YES.getCode())
                );
                //如果有联合承租人，拼接联合承租人名称
                if(!contractTenantryLibList.isEmpty()){
                    for(ContractTenantryLib unitContractTenantryLib:contractTenantryLibList){
                        //拼接主承租人和联合承租人
                        tenantrys.append("、").append(unitContractTenantryLib.getLesseeName());
                    }
                }
            }
        }

        // 如果未查到主承租人，取默认值
        if(tenantrys.length()==0 && Objects.nonNull(client)){
            tenantrys.append(client.getClientName());
        }

        UserVO userVO = getBean(UserService.class).getUserInfoById(detail.getSponsorId()).getData();
        String sponsorPhone = getBean(UserService.class).getRealPhone(detail.getSponsorId());
        // 填充渲染map
        HashMap<String, Object> renderMap = new HashMap<>();
        renderMap.put("clientName", tenantrys);
        renderMap.put("contractCode", detail.getContractCode());
        renderMap.put("phase", detail.getPhase());
        renderMap.put("planCollectionDate", LocalDateTimeUtil.format(detail.getRepayDate(), "yyyy/MM/dd"));
        renderMap.put("planCollectionDateFormal", LocalDateTimeUtil.format(detail.getRepayDate(), "【yyyy】年【MM】月【dd】日"));
        renderMap.put("planCollectionAmount", Optional.ofNullable(detail.getRent()).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        renderMap.put("principal", Optional.ofNullable(detail.getPrincipal()).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        renderMap.put("interest", Optional.ofNullable(detail.getInterest()).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        //最后期项，名义价款取最新报价方案中的名义价款，否则展示“/”
        if(lNominalPrice!=null){
            renderMap.put("nominalPrice", Optional.of(lNominalPrice).map(BigDecimal::new).map(b -> b.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)).map(BigDecimal::toPlainString).orElse("0"));
        }else{
            renderMap.put("nominalPrice","/");
        }
//        renderMap.put("comment", StringUtils.isNotBlank(comment) ? comment : "/");
        renderMap.put("accountName", Optional.ofNullable(detail.getBankAccountName()).orElse(""));
        renderMap.put("accountBank", Optional.ofNullable(detail.getBankName()).orElse(""));
        renderMap.put("accountNumber", Optional.ofNullable(detail.getBankAccountNumber()).orElse(""));

        renderMap.put("sponsorUserName", userVO.getUserName());
        renderMap.put("sponsorTelephone", sponsorPhone);
        renderMap.put("noticeDateFormal", LocalDateTimeUtil.format(LocalDate.now(), "【yyyy】年【MM】月【dd】日"));
        return renderMap;
    }
}
