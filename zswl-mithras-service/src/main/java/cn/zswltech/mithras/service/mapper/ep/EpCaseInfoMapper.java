package cn.zswltech.mithras.service.mapper.ep;

import cn.zswltech.mithras.service.mapper.model.ep.EpCaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 张欣
 */
@Mapper
public interface EpCaseInfoMapper extends BaseMapper<EpCaseInfo> {


    EpCaseInfo findCaseInfo(Long id);

}
