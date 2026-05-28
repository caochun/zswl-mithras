package cn.zswltech.mithras.others.service.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 修复数据
 *
 * @author wangchuanhao
 * @date 2022/12/13 9:48 AM
 */
public interface FixDataMapper {

    int fixProcessInstanceName(@Param("procDefIdList") List<String> procDefIdList, @Param("processInstanceName") String processInstanceName, @Param("businessKey") Long businessKey);

}
