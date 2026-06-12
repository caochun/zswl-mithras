package cn.zswltech.mithras.third.ep.persistence.mapper;

import cn.zswltech.mithras.third.ep.persistence.model.EpCaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 张欣
 */
@Mapper
public interface EpCaseInfoMapper extends BaseMapper<EpCaseInfo> {


    EpCaseInfo findCaseInfo(Long id);

}
