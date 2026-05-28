package cn.zswltech.mithras.service.mapper;

import cn.zswltech.mithras.service.mapper.model.ExceptionRequestInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description 异常请求记录表
* @author vico
* @date 2023-03-31
*/
public interface ExceptionRequestInfoMapper extends BaseMapper<ExceptionRequestInfo> {
    List<ExceptionRequestInfo> listLastRequest(@Param("platform") String platform, @Param("businessIds") List<String> businessIds);
}