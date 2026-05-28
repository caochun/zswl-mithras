import { useEffect, useMemo, useState, useRef, useCallback } from 'react'
import { Tabs, Spin } from 'antd'
import { DoubleLeftOutlined, DoubleRightOutlined } from '@ant-design/icons'
import { observer, getQuery } from '@zswl/admin'
import Operation from './ZTabs/Operation'
import KeyInfo from './ZTabs/KeyInfo'
import DetailInfo from './ZTabs/DetailInfo'
import MaterialList from './ZTabs/MaterialList'
import MinutesOfReviewMeeting from './ZTabs/MinutesOfReviewMeeting'
import FlowChart from './ZTabs/FlowChart'
import RelevantFiles from './ZTabs/RelevantFiles'
import { FlowDataProvider } from '@/pages/process/Detail/Context'
import ToSnapShoot from '@/pages/process/components/ToSnapShoot'
import Store from './store'
import styles from './index.less'
import { isRiskManager } from '@/utils/auth'

// 右侧审批操作的宽度
const OPERATE_WIDTH = 400
// 已经开发了“关键信息”的流程 moduleKey
const flowMainModuleKey = ['PROJ_REVIEW', 'PAYMENT', 'CONTRACT', 'RATING_CLIENT']

function Index(props) {
  const {
    id,
    pathname,
    query: { tab },
    phaseType,
  } = props
  const store = useMemo(() => {
    return new Store()
  }, [])
  const [materialObj, setMaterialObj] = useState(null);
  const [showOperate, setShowOperate] = useState(true)
  const [isEditing, setEditing] = useState(false)
  const callbackRef = useRef(null);
  const diff = getQuery('diff')
  const curTab = getQuery('curTab')
  const { detailData, isMeetingShowFlow } = store
  const { canEditFlag: nodeCanEditFlag, mainModule, operateTabShowFlag, uiVersion, taskActivityId } = detailData
  const noOperate = getQuery('nav') == 'myquery'
  const isRiskManagerProj = ['PROJ_REVIEW'].includes(mainModule) && 'userTask_riskManager' === taskActivityId

  // 节点布局控制,uiVersion:0老的布局，1新的布局
  const isNewLayout = uiVersion === 1
  // const list = ["ProjReviewModifyFlow"]
  // "项目评审变更 ProjReviewModifyFlow"
  // const MinutesOfReviewMeetingShow = list.indexOf(modelKey) !== -1
  // const [rightWidth, setRightWidth] = useState(noOperate || !isNewLayout ? 0 : OPERATE_WIDTH)
  const [rightWidth, setRightWidth] = useState(0)

  useEffect(() => {
    setRightWidth(noOperate || !isNewLayout ? 0 : OPERATE_WIDTH)
  }, [isNewLayout, noOperate])

  const hasKeyInfo = flowMainModuleKey.includes(mainModule)

  // 申请中不能编辑、我的撤回能编辑、审批撤回发起人节点能编辑
  // 我收到的、流程查询 都不能编辑
  const canEditFlag = useMemo(() => {
    // 我的申请、审批结束、待提交
    if (tab === 'apply' || tab === 'finish') return false
    // 我的撤回
    else if (tab === 'revocation') return true
    // 审批退回,发起人节点
    else if (tab === 'sendback') return nodeCanEditFlag
    // 流出查询、我收到的
    return false
  }, [nodeCanEditFlag, tab])

  useEffect(() => {
    diff && store.queryDetail(id, diff)
  }, [id, diff])

  const handleFold = (flag) => {
    setShowOperate(flag)
    setRightWidth(flag ? OPERATE_WIDTH : 0)
  }

  // 流程标题
  const ProcessTitle = useMemo(() => {
    if (detailData.mainModule === 'COLLECTION') return detailData.processName
    const processClientName = detailData.clientName ? `-${detailData.clientName}` : ''
    return (
      <div style={{ marginBottom: 10 }}>
        <div>
          {detailData.modelName}
          {processClientName}
        </div>
      </div>
    )
  }, [JSON.stringify(detailData)])

  // 审批操作
  const approvalOperation = useMemo(() => {
    if (!isNewLayout) {
      return <Operation />
    }

    return (
      <div
        className={styles.right}
        style={{
          width: rightWidth,
          marginLeft: rightWidth ? 15 : 0,
          display: rightWidth ? 'block' : 'none',
        }}
      >
        <Tabs
          tabBarExtraContent={<ToSnapShoot />}
          destroyInactiveTabPane={false}
          defaultActiveKey="1"
          items={[
            {
              label: '审批操作',
              key: '1',
              children: <Operation />,
            },
          ].filter(Boolean)}
        ></Tabs>
      </div>
    )
  }, [rightWidth, isNewLayout])

  // tab 右侧操作
  const tabBarExtra = useMemo(() => {
    if (noOperate || !isNewLayout) return <ToSnapShoot />
    return (
      <div>
        {showOperate ? (
          <DoubleRightOutlined onClick={() => handleFold(false)} />
        ) : (
          <DoubleLeftOutlined onClick={() => handleFold(true)} />
        )}
      </div>
    )
  }, [showOperate, noOperate])

  // tab 配置项
  const tabItems = useMemo(() => {
    const baseItems = [
      {
        label: hasKeyInfo ? '关键信息' : '审批记录',
        key: 'KeyInfo',
        Comp: <KeyInfo />,
        isShow: isNewLayout,
      },
      {
        label: '详细信息',
        key: 'DetailInfo',
        Comp: <DetailInfo />,
        isShow: true,
      },
      {
        label: '资料清单',
        key: 'MaterialList',
        Comp: <MaterialList />,
        isShow: isRiskManagerProj,
      },
      {
        label: '评审会纪要',
        key: 'MinutesOfReviewMeeting',
        Comp: (
          <MinutesOfReviewMeeting
            contentType="page"
            detailData={detailData}
            projReviewType={
              ['ProjReviewModifyFlow', 'ProjReviewCreateFlow'].includes(detailData.modelKey)
                ? 'PROJ_REVIEW_BASE'
                : 'GROUP_CREDIT_REVIEW'
            }
          />
        ),
        isShow: isMeetingShowFlow,
      },
      {
        label: '相关文件',
        key: 'RelevantFiles',
        Comp: <RelevantFiles />,
        isShow: isNewLayout && hasKeyInfo,
      },
      {
        label: '流程图',
        key: 'FlowChart',
        Comp: <FlowChart />,
        isShow: isNewLayout,
      },
      {
        label: '审批操作',
        key: 'ApprovalOperation',
        Comp: approvalOperation,
        isShow: !isNewLayout,
      },
    ]
    const rect = []
    baseItems.map((item) => {
      if (item.isShow) {
        rect.push({
          label: item.label,
          key: item.key,
          children: (
            <>
              {ProcessTitle}
              {item.Comp}
            </>
          ),
        })
      }
    })
    return rect
  }, [isNewLayout, hasKeyInfo, approvalOperation, isMeetingShowFlow])
  // 注册回调函数：执行方组件调用，将自身的回调存入 ref
  const registerCallback = useCallback((callback) => {
    callbackRef.current = callback;
  }, []);
  // 触发回调函数：触发方组件调用，执行已注册的回调
  const triggerCallback = useCallback((params) => {
    // 检查回调是否存在，避免调用空函数
    if (typeof callbackRef.current === 'function') {
      const isPrevent = callbackRef.current(params);
      return isPrevent === true;
    } else {
      console.warn('未注册回调函数');
      return false;
    }
  }, []);
  return (
    <div className={styles.page}>
      {Object.keys(detailData).length === 0 ? (
        <Spin />
      ) : (
        <FlowDataProvider
          value={{
            id,
            curTab,
            detailData,
            hasKeyInfo,
            canEditFlag,
            noOperate,
            isNewLayout,
            isRiskManagerProj,
            materialObj,
            setMaterialObj,
            // 我发起的、我收到的、流程查询，用于回调跳转对应的列表页
            pathname: pathname.split('/detail')?.[0],
            setEditing,
            isEditing,
            registerCallback,
            triggerCallback,
          }}
        >
          <div className={styles.content}>
            <div className={styles.left} style={{ width: `calc(100% - ${rightWidth}px)` }}>
              <div className={styles.detailInfo}>
                <Tabs
                  tabBarExtraContent={tabBarExtra}
                  destroyInactiveTabPane={false}
                  defaultActiveKey="1"
                  items={tabItems}
                ></Tabs>
              </div>
            </div>
            {isNewLayout && operateTabShowFlag && !noOperate && approvalOperation}
          </div>
        </FlowDataProvider>
      )}
    </div>
  )
}
export default observer(Index)
