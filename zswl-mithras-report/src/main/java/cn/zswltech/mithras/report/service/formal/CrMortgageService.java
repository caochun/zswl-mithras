package cn.zswltech.mithras.report.service.formal;

import cn.zswltech.mithras.report.mapper.formal.CrMortgageMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrMortgage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-抵押表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrMortgageService extends ServiceImpl<CrMortgageMapper, CrMortgage> implements IService<CrMortgage> {

}
