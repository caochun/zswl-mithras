package cn.zswltech.mithras.service.fund.direct.service;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingPropertyListREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundFinancingPropertyRSP;
import cn.zswltech.mithras.dto.property.PutPropertyBaseInfoListREQ;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.excel.exporter.PropertyExcelManagerExporter;
import cn.zswltech.mithras.service.excel.model.PropertyExcelModel;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingPledgeInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.PropertyMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.fund.FundFinancingCreditRefService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ylzhang5
 * @description 直接融资/间接融资-投放资产明细
 * @date 20251210
 */
@Service
public class FundDirectFinancingPropertyService
        extends ServiceImpl<FundDirectFinancingPledgeInfoMapper, FundDirectFinancingPledgeInfo> {

    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private PropertyMapper propertyMapper;
    @Resource
    private PropertyExcelManagerExporter propertyExcelManagerExporter;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundOrganizationService organizationService;

    /**
     * 直融/间融投放资产明细查询
     * @param req
     * @return
     */
    public PageR<FundFinancingPropertyRSP> list(FundFinancingPropertyListREQ req) {
        try{
            //返回的请求体列表
            List<FundFinancingPropertyRSP> rspList = new ArrayList<>();
            //融资id
            Long financingId = req.getFinancingId();
            //查询融资编号
            String financingCode = "";
            //查询直融编号
            if(fundDirectFinancingBaseInfoService.getById(financingId)!=null){
                financingCode = fundDirectFinancingBaseInfoService.getById(financingId).getFinancingCode();
            }
            //查询间融编号
            if(fundFinancingBaseInfoService.getById(financingId)!=null){
                financingCode = fundFinancingBaseInfoService.getById(financingId).getFinancingCode();
            }
            //直融间融都不是，返回空列表
            if(financingCode.isEmpty()){
                return PageR.empty(req.getPage(), req.getPageSize());
            }
            rspList = propertyMapper.getPropertyListByFinancingCode(financingCode);
            if(rspList.isEmpty()){
                return PageR.empty(req.getPage(), req.getPageSize());
            }
            //插入业务类型名称、处理金额
            for(FundFinancingPropertyRSP fundFinancingPropertyRSP:rspList){
                fundFinancingPropertyRSP.setContractAmount(fundFinancingPropertyRSP.getContractAmount());
                fundFinancingPropertyRSP.setPutoutAmount(fundFinancingPropertyRSP.getPutoutAmount());
                fundFinancingPropertyRSP.setBizTypeName(Arrays.stream(ProjectBizType.values()).collect(Collectors.toMap(ProjectBizType::name,ProjectBizType::display)).get(fundFinancingPropertyRSP.getBizType()));
            }
            Page<FundFinancingPropertyRSP> page = new Page<>(req.getPage(), req.getPageSize());
            page.setRecords(rspList);
            return PageR.of(page, rspList);
        }catch(Exception e){
            log.error("获取投放资产明细出错：",e);
            return PageR.empty(req.getPage(), req.getPageSize());
        }
    }

    /**
     * 查询投放资产列表
     * @param req
     * @return
     */
    public PageR<FundFinancingPropertyRSP> putPropertyList(PutPropertyBaseInfoListREQ req) {
        try{
            //返回的请求体列表
            List<FundFinancingPropertyRSP> rspList = new ArrayList<>();
            rspList = propertyMapper.getPropertyList(req);
            if(rspList.isEmpty()){
                return PageR.empty(req.getPage(), req.getPageSize());
            }
            //插入业务类型名称、处理金额、融资Id、融资机构
            String financingId = "";
            for(FundFinancingPropertyRSP fundFinancingPropertyRSP:rspList){
                //根据融资编号判断直融、间融
                if(fundFinancingPropertyRSP.getFinancingCode().startsWith("ZR")){
                    financingId = fundFinancingPropertyRSP.getDirectId();
                    //直融根据融资编号取产品名称作为融资机构
                    FundDirectFinancingBaseInfo baseInfo =
                            fundDirectFinancingBaseInfoService.getById(financingId);
                    fundFinancingPropertyRSP.setFinancingOrg(baseInfo.getProductName());
                    //查询融资金额（因为直融表中存的融资金额没有成10000，在此处*10000方便后续统一处理）
                    fundFinancingPropertyRSP.setFinancingAmount(new BigDecimal(baseInfo.getFinancingAmount()*10000));
                }
                if(fundFinancingPropertyRSP.getFinancingCode().startsWith("DK")){
                    //间融取融资机构名称
                    financingId = fundFinancingPropertyRSP.getFinanId();
                    List<FundFinancingCreditRef> fundFinancingCreditRefs =  financingCreditRefService.queryByFinancingId(Long.parseLong(financingId));
                    if(!fundFinancingCreditRefs.isEmpty()){
                        Long orgId = fundFinancingCreditRefs.get(0).getOrganizationId();
                        Set<Long> orgIds = new HashSet<>();
                        orgIds.add(orgId);
                        Map<Long, String> orgIdNameMap = organizationService.getNamesByIds(orgIds);
                        fundFinancingPropertyRSP.setFinancingOrg(orgIdNameMap.get(orgId));
                        //取融资金额
                        FundFinancingBaseInfo baseInfo = fundFinancingBaseInfoService.getById(financingId);
                        fundFinancingPropertyRSP.setFinancingAmount(new BigDecimal(baseInfo.getFinancingAmount()));
                    }
                }
                //插入融资编号（判断直融或间融之后）
                fundFinancingPropertyRSP.setFinancingId(financingId);
                //处理融资金额、出款金额、合同金额、业务类型
                fundFinancingPropertyRSP.setFinancingAmount(fundFinancingPropertyRSP.getFinancingAmount());
                fundFinancingPropertyRSP.setPutoutAmount(fundFinancingPropertyRSP.getPutoutAmount());
                fundFinancingPropertyRSP.setContractAmount(fundFinancingPropertyRSP.getContractAmount());
                fundFinancingPropertyRSP.setBizTypeName(Arrays.stream(ProjectBizType.values()).collect(Collectors.toMap(ProjectBizType::name,ProjectBizType::display)).get(fundFinancingPropertyRSP.getBizType()));
            }
            Page<FundFinancingPropertyRSP> page = new Page<>(req.getPage(), req.getPageSize());
            page.setRecords(rspList);
            return PageR.of(page, rspList);
        }catch(Exception e){
            log.error("获取投放资产列表出错：",e);
            return PageR.empty(req.getPage(), req.getPageSize());
        }
    }

    /**
     * 导出投放资产列表
     * @param req
     * @return
     */
    public void putPropertyListDownload(PutPropertyBaseInfoListREQ req, ServletOutputStream outputStream) {
        try{
            List<FundFinancingPropertyRSP> rspList = new ArrayList<>();
            rspList = propertyMapper.getPropertyList(req);
            if(rspList.isEmpty()){
                throw new MithrasException("数据不存在");
            }
            //插入业务类型名称、处理金额、融资Id
            String financingId = "";
            for(FundFinancingPropertyRSP fundFinancingPropertyRSP:rspList){
                //根据融资编号判断直融、间融
                if(fundFinancingPropertyRSP.getFinancingCode().startsWith("ZR")){
                    financingId = fundFinancingPropertyRSP.getDirectId();
                    //直融根据融资编号取产品名称作为融资机构
                    FundDirectFinancingBaseInfo baseInfo =
                            fundDirectFinancingBaseInfoService.getById(financingId);
                    fundFinancingPropertyRSP.setFinancingOrg(baseInfo.getProductName());
                    //查询融资金额（因为直融表中存的融资金额没有成10000，在此处*10000方便后续统一处理）
                    fundFinancingPropertyRSP.setFinancingAmount(new BigDecimal(baseInfo.getFinancingAmount()*10000));
                }
                if(fundFinancingPropertyRSP.getFinancingCode().startsWith("DK")){
                    //间融取融资机构名称
                    financingId = fundFinancingPropertyRSP.getFinanId();
                    List<FundFinancingCreditRef> fundFinancingCreditRefs =  financingCreditRefService.queryByFinancingId(Long.parseLong(financingId));
                    if(!fundFinancingCreditRefs.isEmpty()){
                        Long orgId = fundFinancingCreditRefs.get(0).getOrganizationId();
                        Set<Long> orgIds = new HashSet<>();
                        orgIds.add(orgId);
                        Map<Long, String> orgIdNameMap = organizationService.getNamesByIds(orgIds);
                        fundFinancingPropertyRSP.setFinancingOrg(orgIdNameMap.get(orgId));
                        //取融资金额
                        FundFinancingBaseInfo baseInfo = fundFinancingBaseInfoService.getById(financingId);
                        fundFinancingPropertyRSP.setFinancingAmount(new BigDecimal(baseInfo.getFinancingAmount()));
                    }
                }
                //插入融资编号（判断直融或间融之后）
                fundFinancingPropertyRSP.setFinancingId(financingId);
                //处理出款金额、合同金额、业务类型
                fundFinancingPropertyRSP.setFinancingAmount(fundFinancingPropertyRSP.getFinancingAmount().divide(new BigDecimal("10000")));
                fundFinancingPropertyRSP.setPutoutAmount(fundFinancingPropertyRSP.getPutoutAmount().divide(new BigDecimal("10000")));
                fundFinancingPropertyRSP.setContractAmount(fundFinancingPropertyRSP.getContractAmount().divide(new BigDecimal("10000")));
                fundFinancingPropertyRSP.setBizTypeName(Arrays.stream(ProjectBizType.values()).collect(Collectors.toMap(ProjectBizType::name,ProjectBizType::display)).get(fundFinancingPropertyRSP.getBizType()));
            }
            //封装数据
            List<PropertyExcelModel> excelModelList = rspList.stream().map(item -> {
                PropertyExcelModel propertyExcelModel = new PropertyExcelModel();
                BeanUtil.copyProperties(item, propertyExcelModel);

                propertyExcelModel.setFinancingCode(item.getFinancingCode());
                propertyExcelModel.setFinancingOrg(item.getFinancingOrg());
                propertyExcelModel.setFinancingAmount(item.getFinancingAmount());
                propertyExcelModel.setContractCode(item.getContractCode());
                propertyExcelModel.setClientName(item.getClientName());
                propertyExcelModel.setProjName(item.getProjName());
                propertyExcelModel.setBizTypeName(item.getBizTypeName());
                propertyExcelModel.setContractAmount(item.getContractAmount());
                propertyExcelModel.setAccountBank(item.getAccountBank());
                propertyExcelModel.setAccountNumber(item.getAccountNumber());
                propertyExcelModel.setPutoutAmount(item.getPutoutAmount());
                propertyExcelModel.setPutoutDate(item.getPutoutDate());
                return propertyExcelModel;
            }).collect(Collectors.toList());

            propertyExcelManagerExporter.exportExcel(excelModelList, outputStream);
        }catch(Exception e){
            log.error("获取投放资产列表出错：",e);
        }
    }
}