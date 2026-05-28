import { baseURL } from '@/utils'

// const base = baseURL()
// const index = base.lastIndexOf('/')
// const prefix = base.slice(0, index)

const prefix = window.location.origin

export const rzyLink = {
  经销商维护: {
    path: '/rzy/jxswh',
    url: `${prefix}/core/modules/PRJ/MFTER_BP/MFTPRJ101/hls_bp_master_modify_entrance.lview?function_group_id=10696&function_code=MFTPRJ101`,
  },

  经销商查询: {
    path: '/rzy/jxscx',
    url: `${prefix}/core/modules/PRJ/MFTER_BP/MFTPRJ103/hls_bp_master_query_entrance.lview?function_group_id=10700&function_code=MFTPRJ103`,
  },

  厂商客户管理: {
    path: '/rzy/cskhgl',
    url: `${prefix}/core/modules/PRJ/MFTER_BP/MFTPRJ102/mft_bp_master_query.lview?function_group_id=10697&function_code=MFTPRJ102`,
  },

  厂商授信额度管理: {
    path: '/rzy/cssxedgl',
    url: `${prefix}/core/modules/PRJ/MFTER_BP/MFTPRJ104/bp_credit_entrance.lview?function_group_id=10701&function_code=MFTPRJ104`,
  },

  厂商产品定义: {
    path: '/rzy/cscpdy',
    url: `${prefix}/core/modules/PRJ/MFTER_BP/MFTPRJ201/product_definition.lview?function_group_id=10699&function_code=MFTPRJ201`,
  },

  进件管理: {
    path: '/rzy/jjgl',
    url: `${prefix}/core/modules/PRJ/MFTER_BP/MFTPRJ301/manufacturer_project_homepage.lview?function_group_id=10704&function_code=MFTPRJ301`,
  },

  进件记录: {
    path: '/rzy/jjjl',
    url: `${prefix}/core/modules/PRJ/MFTER_BP/MFTPRJ302/interface_record_home.lview?function_group_id=10703&function_code=MFTPRJ302`,
  },

  厂商合同管理: {
    path: '/rzy/cshtgl',
    url: `${prefix}/core/modules/CONT/MFT_CONTRACT/CONT101/con_contract_query.lview?function_group_id=10706&function_code=MFTCONT101`,
  },

  厂商合同盖章: {
    path: '/rzy/cshtgz',
    url: `${prefix}/core/modules/CONT/MFT_CONTRACT/CONT102/con_contract_query.lview?function_group_id=10708&function_code=MFTCONT102`,
  },

  厂商电子合同盖章: {
    path: '/rzy/csdzhtgz',
    url: `${prefix}/core/modules/CONT/MFT_CONTRACT/CONT103/mft_digital_con_contract_sign.lview?function_group_id=10720&function_code=MFTCONT103`,
  },

  付款申请维护: {
    path: '/rzy/jzswh',
    url: `${prefix}/core/modules/CSH/CSH_TRX_MFTER/CSH401/csh_payment_req_maintain.lview?function_group_id=10711&function_code=CSH401`,
  },

  付款支付: {
    path: '/rzy/fkzf',
    url: `${prefix}/core/modules/CSH/CSH_TRX_MFTER/CSH402/csh_payment_req_pay_entrance.lview?function_group_id=10715&function_code=CSH402`,
  },

  付款反冲: {
    path: '/rzy/fkfc',
    url: `${prefix}/core/modules/CSH/CSH_TRX_MFTER/CSH403/csh_payment_req_recoil_entrance.lview?function_group_id=10717&function_code=CSH403`,
  },
}

const getMenu = () => {
  const menu = []
  Object.keys(rzyLink).map((key) => {
    menu.push({
      title: key,
      path: rzyLink[key].path,
    })
  })
  return menu
}

export const rzyMemu = {
  title: '厂商管理',
  path: '/rzy',
  icon: 'icon-a-icon_projectmanage',
  children: getMenu(),
}
