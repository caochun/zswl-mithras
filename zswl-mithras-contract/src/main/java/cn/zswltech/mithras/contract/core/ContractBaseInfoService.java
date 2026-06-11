package cn.zswltech.mithras.contract.core;

import cn.zswltech.mithras.dto.contract.ContractCanChangeRSP;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.dto.contract.ContractConstraintREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.*;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.dto.persistence.OcContractListDto;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.overdue.domain.acl.ClientOverdueInfoDto;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractClientInfo;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractGuarantorInfo;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractLesseeInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author vico
 * @description 合同基本信息表
 * @date 2022-08-12
 */
public interface ContractBaseInfoService extends IService<ContractBaseInfo> {
    ContractBaseInfo getValidOneByContractCode(String contractCode);

    List<ContractBaseInfo> listAllStartRent();

    ContractBaseInfoAddRSP add(ContractBaseInfoAddREQ req);

    void modify(ContractBaseInfoModifyREQ req);

    void modifyLeaseItem(ContractBaseInfoModifyREQ req);

    void modifyLeaseItemByProjReviewId(Long projReviewId, String LeaseItems);

    void renewLeader(Long contractId);

    void updatePay(Long id, Long paymentPlanAmount, LocalDate paymentPlanDate);

    Page<ContractBaseInfoListRSP> list(ContractBaseInfoListREQ req);

    List<ContractBaseInfoLib> list(Long clientId);

    /**
     * 保理业务立项专用接口
     *
     * @param clientIds
     * @return
     */
    List<ContractBaseInfo> list(Set<Long> clientIds);

    Boolean remove(ContractBaseInfoRemoveREQ req);

    ContractBaseInfoDetailRSP detail(ContractBaseInfoDetailREQ req);

    ContractBaseInfoDetailRSP editionDetail(ContractBaseInfoDetailREQ req);

    void updateActualLeaseDate(LocalDate actualLeaseDate, Long contractId);

    void updateContractStatus(ContractStatus status, ContractProcessStatusEnum contractProcessStatus, Long contractId);

    /**
     * 更新合同流程状态
     * status 变更类型
     * changeStatus 变更子类型
     * contractId 合同id
     **/
    void updateContractProcessStatus(String status, String changeStatus, Long contractId);

    ContractCanChangeRSP canUpdateContractProcessStatus(List<String> status, Long contractId);

    /**
     * 判断是否有其他约束，如展期需租后先变更
     **/
    void constraint(ContractConstraintREQ req);

    Long getRemainAvailableQuota(Long ownId, Integer isLoop, List<ContractBaseInfo> contractBaseInfos);

    Long getGroupCreditStockRiskExposure(Long clientId);

    /**
     * @author: jackerhe
     * 计算客户风险敞口, clientId 用户id mainId 合同传合同ID
     * module 传入项目则风险敞口计算时会加入本次项目金额， 传合同同理加本次合同金额 不传计算除mainId合同外金额
     **/
    Long getStockRiskExposure(Long clientId, Long mainId, String module);

    Long getStockRiskExposure(Long clientId, Long mainId, String module, LocalDate confirmDate);

    Long getAssetBalance(List<Long> contractIdList, Map<Long, Long> startRentContractMap);

    Map<Long, Long> getStockRiskExposureByClients(Set<Long> clientIds);

    /**
     * 获取合同剩余风险敞口
     **/
    Map<Long, Long> getStockRiskExposureByContracts(Set<Long> contractIds);

    List<ContractBaseInfo> selectListByProjId(Long projId);

    void contractSettle(Long contractId);

    void updateActualFinishDate(Long contractId, LocalDate actualFinishDate);

    List<ContractBaseInfo> listByClients(List<Long> clientIdList);

    /**
     * 查找合同下租金最晚计划收取时间
     **/
    LocalDate getContractExpirationDate(List<Long> contractIds);

    /**
     * 查找合同下租金表最晚计划收取时间
     **/
    Map<Long, LocalDate> getContractExpirationDateByRent(List<Long> contractIds);

    /**
     * 查找合同下第一笔付款时间
     **/
    LocalDate getFistPaymentDate(Long contractId);

    Map<Long, ContractBaseInfo> getMapByContractIds(Collection<Long> contractIds);

    List<ContractBaseInfo> listByProjReviewIds(List<Long> projReviewIds);

    List<ContractBaseInfo> listInRentContract(Long clientId);

    /**
     * 比对合同下承租人及担保人最新的工商信息
     * @param contractId 合同id
     * @param isHistory 是否使用历史数据比对，true不查询天眼查，只比对历史数据
     **/
    List<ContractCompareBusinessRSP> compareBusiness(Long contractId, boolean isHistory);

    //获取合同是否为存量
    Integer getStockContractFlag(Long projReviewId);

    /**
     * 调整实际租金表
     * @param contractBaseInfo 合同
     * @param paymentId 付款申请号
     * @return boolean
     */
    boolean autoAdjustmentRentActual(ContractBaseInfo contractBaseInfo, Long paymentId);

    void copyLendingMaterial(Long paymentId);

    boolean compareActualIRR(Long receiptId, BigDecimal lowestIrr);

    /**
     * 逾期催收-返回指定客户下的合同id,Code列表
     * @param clientId
     * @return
     */
    Map<Long, String> contractPulldown(Long clientId);

    /**
     * 逾期催详情页-合同列表
     * @param clientId
     * @return
     */
    List<OcContractListDto> ocContractList(Long clientId);

    /**
     * 逾期催收详情页-导出合同列表
     * @param clientId
     */
    void contractListExport(Long clientId);

    /**
     * 逾期催收-获取合同下的承租人信息
     * @param contractIds
     * @return
     */
    List<ContractLesseeInfo> getContractLessees(List<Long> contractIds);

    /**
     * 逾期催收-获取合同下的承租人信息(适用合同下多个借据情况)
     * @param contractIds
     * @return
     */
    List<ContractLesseeInfo> getContractLessees1(List<Long> contractIds);

    /**
     * 逾期催收-获取合同下的担保人信息
     * @param contractIds
     * @return
     */
    List<ContractGuarantorInfo> getContractGuarantors(List<Long> contractIds);

    /**
     * 诉讼登记-查询承租人和担保人为指定客户的合同
     * @param clientId
     * @return
     */
    List<ContractBaseInfo> listByClientId(Long clientId);

    /**
     * 诉讼登记-查询指定合同下的所有承租人和担保人
     */
    List<ContractClientInfo> listClientsByContractId(Set<Long> contractIds);

    /**
     * 诉讼登记-客户逾期信息展示
     * @param clientId
     */
    ClientOverdueInfoDto clientOverdueInfo(Long clientId);

    /**
     * 获取合同下剩余名义价款 合同下名义价款 - 核销名义价款
     **/
    Map<Long, Long> getContractNominalPriceRemain(List<Long> contractIds, LocalDate endDate);

    /**
     *  直接融资和间接融资 查询未锁定合同
     */
    List<ContractBaseInfo> contractSearchList(String contractCode);
}
