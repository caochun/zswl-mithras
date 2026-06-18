import { Access, Button, Page } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import UnifiedToDo from './UnifiedToDo'
import ProjectView from './ProjectView'
import ProjectView2 from './ProjectView2'
import CustomerView from './CustomerView'
import CustomerView2 from './CustomerView2'
import MyAchievement from './MyAchievement'
import AchievementRank from './AchievementRank'
import FinancingView from './FinancingView'
import OperationView from './OperationView'
import AnchorScrollNav from '@/components/AnchorScrollNav'
import {
  isLeader,
  isBaseinessDept,
  isHrDept,
  isCompositeDept,
  isOperationDept,
  isLawDept,
  isFinicalDept,
  isFundDept,
  isRiskDept,
  getUserInfo,
  setLocalColumnsFilter,
  CARD_COLUMNS_FILTER,
  TABLE_COLUMNS_FILTER,
} from '@/utils'
import styles from './index.less'
import userCustomConfigApi from '@/api/dashboard/userCustomConfigApi'
import { initData } from './initData'
import { handleFeikongJump } from './utils'

/**
 * 部门模块配置
 * 区分部门：jobsName 的 orgId
 */

// 业务部：统一待办（待办、我发起的、在办、已办、流程抄送、消息）、项目视图（项目阶段、项目信息）、客户视图（客户一览、客户舆情）、我的业绩（部门业绩、个人业绩）
// 经营层：统一待办（待办、我发起的、在办、已办、流程抄送、消息）、项目视图（项目阶段、项目信息）、业绩排名（部门间排名、部门内排名）
//2 信息科技：统一待办（待办、我发起的、在办、已办、流程抄送、消息）
//36 人事部：统一待办（待办、我发起的、在办、已办、流程抄送、消息）

//3 风控部：统一待办（待办、我发起的、在办、已办、流程抄送、消息、待处理舆情）、项目视图（项目阶段、项目信息）、客户视图（客户一览、客户舆情）
//23 法律合规部：统一待办（待办、我发起的、在办、已办、流程抄送、消息、待处理舆情）、项目视图（项目阶段2、项目信息）、客户视图（租后管理）

//24 运营部：统一待办（待办、我发起的、在办、已办、流程抄送、消息）、我的保单（即将到期保单、逾期保单）、流程分析（流程审批时效、流程退回率、运营效率分析、流程统计）
//13 财务部：统一待办（待办、我发起的、在办、已办、流程抄送、消息）、项目视图2（投放情况、计划执行情况、项目信息）、融资视图
//22 资金部：统一待办（待办、我发起的、在办、已办、流程抄送、消息）、项目视图2（投放情况、计划执行情况、项目信息）、融资视图2

function mergeObjects (obj1, obj2) {
  for (let key in obj2) {
    if (!obj1.hasOwnProperty(key)) {
      obj1[key] = obj2[key]
    }
  }
  return obj1
}

const getServerConfig = async () => {
  const objectData = []
  const ArrayData = []
  const res = await userCustomConfigApi.queryCustomConfig({})
  res.forEach((item) => {
    try {
      const parsedValue = JSON.parse(item.configValue)
      if (Array.isArray(parsedValue) && parsedValue !== null) {
        ArrayData.push(item)
      } else {
        objectData.push(item)
      }
    } catch (error) {
      ArrayData.push(item)
    }
  })
  const newtCardLocal = ArrayData.reduce((pre, cur) => {
    pre[cur.configKey] = JSON.parse(cur.configValue)
    return pre
  }, {})

  const newTableLocal = objectData.reduce((pre, cur) => {
    pre[cur.configKey] = JSON.parse(cur.configValue)
    return pre
  }, {})
  window.localStorage.setItem(CARD_COLUMNS_FILTER, JSON.stringify(newtCardLocal))
  window.localStorage.setItem(
    TABLE_COLUMNS_FILTER,
    JSON.stringify(mergeObjects(newTableLocal, initData))
  )
}
const App = () => {
  const is_wujie = getUserInfo().id === 49
  const [show, setShow] = useState(false)
  const [feikongVisible, setFeikongVisible] = useState(false)

  const allAnchorList = [
    {
      label: '统一待办',
      component: <UnifiedToDo />,
      key: 'UnifiedToDo',
      iconType: 'icon-tongyidaiban',
    },
    {
      label: '项目视图',
      component: <ProjectView />,
      key: 'ProjectView',
      iconType: 'icon-xiangmushitu',
    },
    {
      label: '项目视图2', // 财务部、资金部
      rename: '项目视图',
      component: <ProjectView2 />,
      key: 'ProjectView2',
      iconType: 'icon-xiangmushitu',
    },
    {
      label: '客户视图',
      component: <CustomerView />,
      key: 'CustomerView',
      iconType: 'icon-fangke',
    },
    {
      label: '客户视图2', // 法律合规部
      rename: '客户视图',
      component: <CustomerView2 />,
      key: 'CustomerView2',
      iconType: 'icon-fangke',
    },
    {
      label: '我的业绩',
      component: <MyAchievement />,
      key: 'MyAchievement',
      iconType: 'icon-yejipaiming',
    },
    {
      label: '业绩排名',
      component: <AchievementRank />,
      key: 'AchievementRank',
      iconType: 'icon-yejipaiming',
    },
    {
      label: '融资视图', // 财务、资金部门
      component: <FinancingView />,
      key: 'FinancingView',
      iconType: 'icon-rongzishitu',
    },
    {
      label: '运营视图', // 运营视图
      component: <OperationView />,
      key: 'OperationView',
      iconType: 'icon-rongzishitu',
    },
    // 费控系统 SSO：由 getAuthAnchorList 注入 onAnchorClick / isContentHide，仅展示在锚点栏
    {
      label: '费控系统',
      key: 'Feikong',
      iconType: 'icon-rongzishitu',
      isFeikongEntry: true,
      component: null,
    },
  ]

  // 待时间，需要优化
  const getAuthAnchorList = useMemo(() => {
    let moduleNames = ['统一待办']
    if (isLeader()) {
      // 领导层
      moduleNames = ['统一待办', '项目视图', '业绩排名']
      // 伍杰（生产环境该客户id=49） 需要看到资金部的视图（ '项目视图2', '融资视图'）
      if (is_wujie) {
        moduleNames = ['统一待办', '项目视图', '融资视图', '业绩排名']
      }
    } else if (isBaseinessDept()) {
      // 业务部
      moduleNames = ['统一待办', '项目视图', '客户视图', '我的业绩']
    } else if (isRiskDept()) {
      // 风控部门
      moduleNames = ['统一待办', '项目视图', '客户视图']
    } else if (isLawDept()) {
      // 法律合规部
      moduleNames = ['统一待办', '项目视图', '客户视图2']
    } else if (isFinicalDept()) {
      // 财务部
      moduleNames = ['统一待办', '项目视图2', '融资视图']
    } else if (isFundDept()) {
      // 资金部
      moduleNames = ['统一待办', '项目视图2', '融资视图']
    } else if (isOperationDept()) {
      // 运营部
      moduleNames = ['统一待办', '运营视图']
    } else if (isCompositeDept()) {
      // 综合管理部门（信科部）
      moduleNames = ['统一待办']
    } else if (isHrDept()) {
      // 人事部门
      moduleNames = ['统一待办']
    }
    const list = moduleNames
      .map((name) => {
        const current = allAnchorList.find((config) => config.label === name)
        if (current) {
          return {
            ...current,
            label: current.rename || name,
          }
        }
      })
      .filter(Boolean)
    const feikong = allAnchorList.find((c) => c.isFeikongEntry)
    console.log('Access', Access.validate('oauthAuthorize'));
    if (feikong) {
      list.push({
        ...feikong,
        onAnchorClick: handleFeikongJump,
        isContentHide: true,
        isHide: !Access.validate('oauthAuthorize'),
      })
    }
    return list
  }, [feikongVisible])

  useEffect(() => {
    getServerConfig()
    setTimeout(() => {
      setShow(true)
    }, 200)
  }, [])


  const goToLease = () => {
    history.push('/implant/dashBulletinBoard')
  }
  const isPre = __ENV__ === 'preSvc'
  return (
    <>
      {show && (
        <Page className={styles.page}>
          <AnchorScrollNav
            anchorList={getAuthAnchorList}
            anchorConfig={{ offsetTop: 120 }}
            anchorContentStyle={{ marginBottom: 0 }}
            childrenStyle={{}}
            extra={
              isPre && (
                <Button type="link" onClick={goToLease}>
                  租赁看板
                </Button>
              )
            }
          />
        </Page>
      )}
    </>
  )
}

export default observer(App)
