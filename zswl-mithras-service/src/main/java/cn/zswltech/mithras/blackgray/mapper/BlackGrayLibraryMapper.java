package cn.zswltech.mithras.blackgray.mapper;

import cn.zswltech.mithras.blackgray.dto.req.BlackGrayGroupListREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayLibraryDistinctListREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayGroupListRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayLibCountDTO;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayLibraryDistinctListRSP;
import cn.zswltech.mithras.blackgray.model.BlackGrayLibrary;
import cn.zswltech.mithras.blackgray.vo.BlackGrayLibAllCountVo;
import cn.zswltech.mithras.blackgray.vo.GroupCompanyInStockCountVo;
import cn.zswltech.gruul.dao.dal.tkmybatis.IMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Repository
public interface BlackGrayLibraryMapper extends IMapper<BlackGrayLibrary> {

    // 查询对应金融机构本月黑灰名单数
    HashMap selectBlackGrayCount(@Param("enterpriseName") List<String> enterpriseName, @Param("timePoint") String timePoint);

    HashMap selectBlackGrayCountZl(@Param("enterpriseName") List<String> enterpriseName, @Param("timePoint") String timePoint);

    List<HashMap> selectTop5Black(@Param("enterpriseName") List<String> enterpriseName, @Param("timePoint") String timePoint);
    List<HashMap> selectTop5BlackZl(@Param("enterpriseName") List<String> enterpriseName, @Param("timePoint") String timePoint);

    List<HashMap> selectTop5White(@Param("enterpriseName") List<String> enterpriseName, @Param("timePoint") String timePoint);
    List<HashMap> selectTop5WhiteZl(@Param("enterpriseName") List<String> enterpriseName, @Param("timePoint") String timePoint);

    /**
     * 黑灰名单库企业数量与黑灰类型统计，按照企业维度分组聚合
     * sql较慢，xxl-job定时查询缓存
     */
    List<BlackGrayLibCountDTO> blackGrayCountByApplyOrg(@Param("businessType") String businessType);

    /**
     * 全量统计黑灰名单企业数量与黑灰类型
     */
    List<BlackGrayLibAllCountVo> countAll(@Param("businessType") String businessType);

    /**
     * 单一企业维度黑灰名单企业列表查询
     */
    List<BlackGrayLibrary> singleEntList(@Param("req") BlackGrayLibraryDistinctListREQ req,
                                         @Param("queryAllOrg") boolean queryAllOrg,
                                         @Param("orgCodes") Collection<String> orgCodes);

    /**
     * 单一集团维度黑灰名单集团列表
     */
    List<BlackGrayGroupListRSP> groupList(@Param("req") BlackGrayGroupListREQ req);

    /**
     * 集团下属企业在库数统计
     *
     * @param groupNames   集团主企业名称集合
     * @param businessType
     * @return map key=groupName, value=下属企业在库数
     */
    List<GroupCompanyInStockCountVo> groupCompanyInStockCount(@Param("groupNames") Collection<String> groupNames,
                                                              @Param("businessType") String businessType);

    /**
     * 集团下属企业在库信息列表
     */
    List<BlackGrayLibraryDistinctListRSP> groupCompanyStockList(@Param("groupName") String groupName,
                                                                @Param("businessType") String businessType);


    List<BlackGrayLibrary> selectAllList();

    String selectSpcificClient(@Param("getEnterpriseCode") String getEnterpriseCode,@Param("orgCode") String orgCode);

    int completeWarehouseOut(@Param("applyReasonType") String applyReasonType);
}