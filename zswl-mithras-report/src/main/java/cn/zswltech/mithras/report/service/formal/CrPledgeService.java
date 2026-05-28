package cn.zswltech.mithras.report.service.formal;

import cn.zswltech.mithras.report.mapper.formal.CrPledgeMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrPledge;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-质押表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrPledgeService extends ServiceImpl<CrPledgeMapper, CrPledge> implements IService<CrPledge> {

}
