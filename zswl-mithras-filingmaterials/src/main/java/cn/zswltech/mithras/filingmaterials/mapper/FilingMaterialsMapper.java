package cn.zswltech.mithras.filingmaterials.mapper;

import cn.zswltech.mithras.dto.filingmaterials.FilingMaterialsConfigDTO;
import cn.zswltech.mithras.dto.filingmaterials.OtherPageSelectDTO;
import cn.zswltech.mithras.filingmaterials.mapper.model.FilingMaterials;
import cn.zswltech.mithras.filingmaterials.mapper.model.FundDirectFinancingFilingMaterialsQuery;
import cn.zswltech.mithras.filingmaterials.mapper.model.FundDirectFinancingFilingMaterialsResult;
import cn.zswltech.mithras.filingmaterials.mapper.model.FundFinancingFilingMaterialsQuery;
import cn.zswltech.mithras.filingmaterials.mapper.model.FundFinancingFilingMaterialsResult;
import cn.zswltech.mithras.filingmaterials.mapper.model.ProjFilingMaterialsQuery;
import cn.zswltech.mithras.filingmaterials.mapper.model.ProjFilingMaterialsResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 资料归档dao
 *
 * @author lllin
 * @create: 2025-12-03
 **/
public interface FilingMaterialsMapper extends BaseMapper<FilingMaterials> {

    List<FilingMaterialsConfigDTO> queryFilingMaterialsConfig(@Param("filingType") String filingType);

    List<FilingMaterialsConfigDTO> queryDocxFilingMaterialsConfig(@Param("filingType") String filingType);

    List<FilingMaterialsConfigDTO> queryRequireFilingMaterialsConfig(@Param("filingType") String filingType);

    Page<FilingMaterials> otherPageList(Page<FilingMaterials> page,
                                 @Param("dto") OtherPageSelectDTO selectDTO);

    List<FundDirectFinancingFilingMaterialsResult> queryFundDirectFinancingFilingMaterials(@Param("req") FundDirectFinancingFilingMaterialsQuery req);

    List<FundFinancingFilingMaterialsResult> queryFundFinancingFilingMaterials(@Param("req") FundFinancingFilingMaterialsQuery req);

    Page<ProjFilingMaterialsResult> queryProjFilingMaterials(Page<ProjFilingMaterialsResult> page,
                                                             @Param("req") ProjFilingMaterialsQuery req);
}
