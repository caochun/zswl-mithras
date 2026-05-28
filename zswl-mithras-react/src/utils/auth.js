import { getLocalStorage } from '@zswl/admin'
import { Access } from '@zswl/components'

//orgType 部门类型：0 公司， 1 业务部门，2 领导层

// 获取用户信息
export const getUserInfo = () => getLocalStorage('userInfo') || {}

// 管理员账号
// export const isAdminAccount = () => getUserInfo().account === 'admin'

// 当前登录用户是主办
export const userIsProjSponsor = (projSponsorUserId) => {
  return projSponsorUserId === getUserInfo().id
}

// 获取登陆用户岗位
export const getUserJobs = () => {
  const { jobsName } = getUserInfo()
  const allJob = []
  jobsName?.map((item) => {
    allJob.push(item.jobNames)
  })
  return allJob.flat()
}

// 获取登陆用户角色
export const getUserRoles = () => {
  const { orgRolesName } = getUserInfo()
  const allRoles = []
  orgRolesName?.map((item) => {
    allRoles.push(item.roles)
  })
  return allRoles.flat()
}

// 通过 name判断是否定价委员会
export const isPricingDept = () => {
  const { jobsName } = getUserInfo()
  return jobsName.some((item) => item?.orgName === '定价委员会')
}
// 判断是否哪个部门
export const isDept = (id) => {
  const { jobsName } = getUserInfo()
  return jobsName.some((item) => item?.orgId === id)
}
// 是否为风控部门
export const isRiskDept = () => {
  const { jobsName } = getUserInfo()
  return jobsName.some((item) => item?.orgId === 3)
}
// 是否为财务部门
export const isFinicalDept = () => {
  const { jobsName } = getUserInfo()
  return jobsName.some((item) => item?.orgId === 13)
}
// 是否为综合管理部门（信科部）
export const isCompositeDept = () => {
  const { jobsName } = getUserInfo()
  return jobsName.some((item) => item?.orgId === 14)
}
// 是否为资金部门
export const isFundDept = () => {
  const { jobsName } = getUserInfo()
  return jobsName.some((item) => item?.orgId === 22)
}
// 是否为法律合规部门
export const isLawDept = () => {
  const { jobsName } = getUserInfo()
  return jobsName.some((item) => item?.orgId === 23)
}
// 是否为运营部门
export const isOperationDept = () => {
  const { jobsName } = getUserInfo()
  return jobsName.some((item) => item?.orgId === 24)
}
// 是否为人事部门
export const isHrDept = () => {
  const { jobsName } = getUserInfo()
  return jobsName.some((item) => item?.orgId === 36)
}

// 是否为业务部门
export const isBaseinessDept = () => {
  const { jobsName } = getUserInfo()
  return jobsName.some((item) => item?.orgType === 1)
}
// 领导层，通过角色ID 判断
export const isLeader = () => {
  const userId = getUserInfo().id
  // ['jifei', 'liyan', 'wujie', 'lusuping']
  return [49, 50, 52, 128, 198].includes(userId)
}

// 业务部门-负责人
export const isBusinesshead = (deptId) => {
  const { jobsName } = getUserInfo()
  let isBusinessheadFlag = false
  jobsName?.forEach((item) => {
    if (item.orgId == deptId) {
      isBusinessheadFlag = item.jobNames.some((i) => i.jobCode === 'businesshead')
    }
  })
  return isBusinessheadFlag
}

// 判断岗位
export const hasJob = (jobCode) => {
  const job = getUserJobs().filter((item) => item.jobCode === jobCode)
  return job?.length > 0
}

// admin岗位
export const isAdminAccount = () => hasJob('admin') || getUserInfo().account === 'admin'
// 运营经办
export const isYunYingGuanLi = () => hasJob('yunYingGuanLi')
// 运营管理部负责人
export const isYunYingBuFuZeRen = () => hasJob('headofyyglb')
// 合同结清发起人
export const isContractSettlement = (id) => getUserInfo().id === id;

// 财务经理 or 会计
export const isFinancialManager = () => hasJob('financialmanager')

// 运营经理
export const isOperationmanagementagent = () => hasJob('operationManagement')
// 法务经理
export const isLegalmanager = () => hasJob('legalmanager')
// 项目经理
export const isProjmanager = () => hasJob('projmanager')
// 综合信息岗
export const isInformationpost = () => hasJob('Informationpost')
// 资产管理岗
export const isAssetJon = () => hasJob('assetmanagement')
// 评审会秘书
export const isSecretaryjury = () => hasJob('secretaryjury')
// 风委会秘书
export const isRisksecretary = () => hasJob('risksecretary')
// 董事会秘书
export const isBoardsecretary = () => hasJob('boardsecretary')
// 财务主管
export const isFinancialOfficer = () => hasJob('financialofficer')
// 风控经理
export const isRiskManager = () => hasJob('riskmanager')


// 风委会秘书
export const isRiskSecretary = () => hasJob('risksecretary')
// 团队长
export const isTeamleader = () => hasJob('teamleader')

// 管理员账号、资产管理岗 - 租后检查
export const isAssetJonAndAdmin = () => isAssetJon() || isAdminAccount()

// 功能权限
export const hasPermission = (value) => Access.validate(value)
