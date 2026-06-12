package cn.zswltech.mithras.blackgray.persistence.mapper;

import cn.zswltech.gruul.dao.dal.tkmybatis.IMapper;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBusinessTypeRsp;
import cn.zswltech.mithras.blackgray.persistence.model.BlackGrayBusinessDict;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlackGrayBusinessDictMapper extends IMapper<BlackGrayBusinessDict> {
    //获取子公司机构树
    List<BlackGrayBusinessTypeRsp> getTree(@Param("orgId") Long orgId, @Param("zeroIds") List<Long> zeroIds, @Param("businessType") String businessType);

    List<BlackGrayBusinessTypeRsp> getBaseDict(@Param("businessType") String businessType);
}