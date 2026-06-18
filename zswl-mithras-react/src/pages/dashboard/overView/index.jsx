import { Page } from '@zswl/components'
import { history, observer } from '@zswl/admin'
import { FullScreenBtn } from '@zswl/charts'
import { useMemo, useState } from 'react'
import AssetsView from './AssetsView'
import ProjectOperation from './ProjectOperation'
import AssetsCustomer from './AssetsCustomer'
import BusinessTransformation from './BusinessTransformation'
import Level5Classify from './Level5Classify'
import OverdueProject from './OverdueProject'
import AchievementAnalyze from './AchievementAnalyze'
import DeptPerformanceSort from './DeptPerformanceSort'
import ConversionRates from './ConversionRates'
import {
  DashboardOperationAgingStatistics as AgingStatistics,
  DashboardOperationCapacityAnalysis as CapacityAnalysis,
} from '@/components/Dashboard/DashboardEntries'
import { AnchorScrollNav } from '@/components'
import { getUserInfo } from '@/utils'
import Store from './Store'
import styles from './index.less'
import IndustryIndexComparison from './IndustryIndexComparison'
import { Button } from 'antd'

const App = () => {
  const is_wujie = getUserInfo().id === 49
  const allAnchorList = [
    { label: '资产总览', component: <AssetsView />, key: 'AssetsOverview' },
    { label: '资产及客户分布', component: <AssetsCustomer />, key: 'AssetsClientDistribution' },
    { label: '业绩分析', component: <AchievementAnalyze />, key: 'AchievementAnalyze' },
    { label: '部门业绩排名', component: <DeptPerformanceSort />, key: 'DeptPerformanceSort' },
    { label: '业务转化情况', component: <ConversionRates />, key: 'BusinessTrans' },
    {
      label: '业务转化漏斗',
      component: <BusinessTransformation />,
      key: 'BusinessTransformFunnel',
    },
    {
      label: '产能分析',
      component: <CapacityAnalysis />,
      key: 'CapacityAnalysis',
    },
    // 非伍杰
    {
      label: '项目运营效率',
      component: <ProjectOperation />,
      key: 'ProjectOperationEfficiency',
    },
    // 伍杰
    {
      label: '项目运营效率',
      component: <AgingStatistics title="项目运营效率" />,
      key: 'TimelinessStatistics',
    },
    { label: '资产五级分类', component: <Level5Classify />, key: 'AssetsFiveClassify' },
    { label: '逾期项目信息', component: <OverdueProject />, key: 'OverdueProject' },
    {
      label: '行业指标对比',
      component: <IndustryIndexComparison />,
      key: 'IndustryIndexComparison',
    },
  ]

  const store = useMemo(() => {
    return new Store()
  }, [])
  const { pageData } = store

  const [anchorConfig, setAnchorConfig] = useState({
    offsetTop: 120,
  })

  const [affixConfig, setAffixConfig] = useState({})

  const authAnchorList = pageData.map((item) => {
    const current = allAnchorList.find((config) => config.key === item.dashboardKey)
    if (current) {
      return {
        ...current,
        label: item.dashboardDisplay,
        dataDate: item.dataDate || '-',
      }
    }
  })

  const getFullScreenContainerEl = () => {
    return document.getElementById('dashboardOverView')
  }

  const toggleStatus = (status) => {
    if (status) {
      setAffixConfig({
        target: getFullScreenContainerEl,
      })
      setAnchorConfig({
        offsetTop: 100,
        getContainer: getFullScreenContainerEl,
        key: Date.now(),
      })
    } else {
      setAffixConfig({})
      setAnchorConfig({
        offsetTop: 120,
        key: Date.now(),
      })
    }
  }

  return (
    <Page className={styles.page} store={store.page}>
      <div id="dashboardOverView">
        <AnchorScrollNav
          extra={[
            <Button
              type="link"
              onClick={() => {
                history.push('/implant/jfBulletinBoard')
              }}
            >
              行业对标分析看板
            </Button>,
          ]}
          align={'left'}
          anchorList={authAnchorList}
          affixConfig={affixConfig}
          anchorConfig={anchorConfig}
          childrenStyle={{
            marginBottom: 20,
            background: '#fff',
            borderRadius: 14,
          }}
        ></AnchorScrollNav>
      </div>
    </Page>
  )
}

export default observer(App)
