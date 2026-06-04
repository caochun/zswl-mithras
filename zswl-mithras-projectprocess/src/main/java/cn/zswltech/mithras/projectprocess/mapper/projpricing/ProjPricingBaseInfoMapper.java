package cn.zswltech.mithras.projectprocess.mapper.projpricing;

import cn.zswltech.mithras.projectprocess.mapper.dto.ProjPricingListSelectDTO;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 项目定价基本信息表 Mapper 接口
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
public interface ProjPricingBaseInfoMapper extends CustomBaseMapper<ProjPricingBaseInfo> {

    Page<ProjPricingBaseInfo> myList(Page<Object> objectPage, @Param("dto") ProjPricingListSelectDTO dto);

    /**
     * 更新冗余字段
     * @param projectId
     * @param declaredAmount
     */
    void updateDeclaredAmount(@Param("projectId") Long projectId, @Param("declaredAmount")Long declaredAmount);

    /**
     * 当用户变更客户信息时，系统需判断该客户是否存在审批中或审批同意的项目评审，如存在，则客户信息变更需走简易审批。
     * 如该客户不存在项目评审数据，或存在评审但评审流程状态均为审批拒绝，则都视为评审前的客户，其信息变更无需审批。
     * @param clientId
     * @return
     */
    int clientRelatedProjPricingCount(@Param("clientId") Long clientId);

    /**
     * 查看版本表，如果项目评审里某个版本里有该客户信息，该客户也不能删
     * @param clientId
     * @return
     */
    int clientRelatedProjPricingLibCount(@Param("clientId") Long clientId);

    List<ProjPricingBaseInfo> listRelationByClients(Long clientId);
}
