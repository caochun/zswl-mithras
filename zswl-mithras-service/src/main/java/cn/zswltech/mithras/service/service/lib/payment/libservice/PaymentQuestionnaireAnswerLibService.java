package cn.zswltech.mithras.service.service.lib.payment.libservice;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.payment.version.PaymentVersionListRSP;
import cn.zswltech.mithras.dto.version.*;
import cn.zswltech.mithras.common.constant.MithrasConstants;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentQuestionnaireAnswerLib;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.payment.handler.PaymentAbstractHandler;
import cn.zswltech.mithras.service.service.lib.payment.libservice.impl.PaymentBaseInfoLibServiceImpl;
import cn.zswltech.mithras.service.util.VersionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 18:01
 */
@Service
public interface PaymentQuestionnaireAnswerLibService extends IService<PaymentQuestionnaireAnswerLib> {

}
