package cn.zswltech.mithras.contract.mapper.contract;

import cn.zswltech.mithras.dto.contract.ContractInfo;
import cn.zswltech.mithras.dto.liquidityrisk.ContractLastDate;
import cn.zswltech.mithras.dto.liquidityrisk.ContractLastDateDTO;
import cn.zswltech.mithras.contract.mapper.dto.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.contract.mapper.query.ContractPrincipalQuery;
import cn.zswltech.mithras.contract.overdue.domain.acl.ClientOverdueInfoDto;
import cn.zswltech.mithras.contract.overdue.domain.acl.ClientRole;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractGuarantorInfo;
import cn.zswltech.mithras.contract.overdue.domain.acl.ContractLesseeInfo;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.OverdueCollection;
import cn.zswltech.mithras.service.mapper.dto.ClientMaxLeaseMonthDTO;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import cn.zswltech.mithras.contract.application.dto.ContractPrincipalBO;
import cn.zswltech.mithras.contract.pricing.dto.ContractPriceQueryDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
* @description 合同基本信息表
* @author vico
* @date 2022-08-12
*/
@Repository
public interface ContractBaseInfoMapper extends CustomBaseMapper<ContractBaseInfo> {

    Page<ContractBaseInfo> myList(Page<ProjReviewBaseInfo> page,
                                    @Param("dto") ContractListSelectDTO selectDTO);
    List<ContractInfo> selectIds (@Param("ids") List<Long> ids);

    /**
     * 付款申请查询合同 不分页专用
     * @param selectDTO
     * @return
     */
    List<ContractBaseInfo> fuzzyList(@Param("dto") ContractListSelectDTO selectDTO);

    int updateApplyCreditAmount(@Param("id") Long id, @Param("applyCreditAmount") Long applyCreditAmount);

    Page<ContractLastDate> ContractRentLastDate(Page<ContractLastDate> page,@Param("dto") ContractLastDateDTO dto);

    List<ContractLastDate> ContractRentActualMaxDate(@Param("ids") List<Long> ids);

    List<ClientMaxLeaseMonthDTO> clientMaxLeaseMonth(@Param("ids") Collection<Long> contractIds);

    List<ContractPrincipalBO> listContractPrincipal(@Param("query") ContractPrincipalQuery query);

    List<ContractRentLastTimeDTO> getContractExpirationDateByRent(@Param("ids")List<Long> contractIds);

    List<OverdueCollection> getOverdueCollection(@Param("clientIds") Collection<Long> clientIds);

    List<OcContractListDto> ocContractList(@Param("clientId") Long clientId);
    List<OcContractDto> ocContractListByClientId(@Param("clientId") Long clientId);

    List<ContractLesseeInfo> getContractLessees(@Param("contractIds") List<Long> contractIds);

    List<ContractLesseeInfo> getContractLessees1(@Param("contractIds") List<Long> contractIds);

    List<ContractGuarantorInfo> getContractGuarantors(@Param("contractIds")List<Long> contractIds);

    List<ContractBaseInfo> listByClientIds(@Param("clientId")Long clientId);

    List<ClientRole> listClientsByContractId(@Param("contractIds")Collection<Long> contractIds);

    ClientOverdueInfoDto clientOverdueInfo(@Param("clientId")Long clientId);

    List<ContractPriceQueryDto> queryPriceDtoList(@Param("contractIds") List<Long> contractIds);

    List<ContractBaseInfo> contractSearchList(String contractCode);
}
