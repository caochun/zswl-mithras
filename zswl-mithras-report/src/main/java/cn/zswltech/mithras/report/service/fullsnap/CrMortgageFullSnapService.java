package cn.zswltech.mithras.report.service.fullsnap;

import cn.zswltech.mithras.report.mapper.fullsnap.CrMortgageFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrMortgageFullSnap;
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
public class CrMortgageFullSnapService extends ServiceImpl<CrMortgageFullSnapMapper, CrMortgageFullSnap> implements IService<CrMortgageFullSnap> {

}
