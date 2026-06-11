package cn.zswltech.mithras.contract.mapper.contract;

import cn.zswltech.mithras.dto.contract.text.ContractTextManageListREQ;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTextManage;
import cn.zswltech.mithras.contract.core.dto.ContractTextManageBO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotNull;

/**
* @author bigbear
* @description 针对表【contract_text_manage(合同文本管理表)】的数据库操作Mapper
* @createDate 2024-11-18 16:52:38
* @Entity cn.zswltech.mithras.contract.mapper.model.contract.ContractTextManage
*/
public interface ContractTextManageMapper extends BaseMapper<ContractTextManage> {

    Page<ContractTextManageBO> list(@Param("page") Page<ContractTextManageBO> page, @Param("req") ContractTextManageListREQ req);

    ContractTextManage selectByContractId(@NotNull @Param("contractId") Long contractId);
}




