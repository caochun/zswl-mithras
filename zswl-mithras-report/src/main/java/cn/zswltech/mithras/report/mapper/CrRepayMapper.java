package cn.zswltech.mithras.report.mapper;

import cn.zswltech.mithras.report.mapper.dto.CrRepayDTO;
import cn.zswltech.mithras.report.mapper.query.CrRepayQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 还款查询
 *
 * @author wangchuanhao
 * @date 2023/1/13 11:32 AM
 */
public interface CrRepayMapper {

    /**
     * 统计在流程中的数据
     * @return
     */
    int countInProcessData();

    Page<CrRepayDTO> draftList(Page page, @Param("query") CrRepayQuery query);

    Page<CrRepayDTO> procSnapList(Page page, @Param("query") CrRepayQuery query);

    Page<CrRepayDTO> effectList(Page page, @Param("query") CrRepayQuery query);

    Page<CrRepayDTO> fullSnapList(Page page, @Param("query") CrRepayQuery query);

}
