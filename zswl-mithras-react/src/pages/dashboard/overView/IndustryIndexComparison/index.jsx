import { observer } from '@zswl/admin'
import ReactECharts from 'echarts-for-react'
import { Select, Button, message } from 'antd'
import { SettingOutlined, TableOutlined } from '@ant-design/icons'
import { useEffect, useMemo, useState } from 'react'
import DetailTable from './DetailTable'
import CompanyConfig from './CompanyConfig'
import Title from '@/components/Dashboard/OverviewTitle'
import styles from './index.less'
import peerComparisonController from '@/api/workbench/peerComparisonController'
import { Drawer, ModalStore } from '@zswl/components'
import RadarChart from './RadarChart'
import ProgressChart from './ProgressChart'

// 处理后端数据，转换为图表所需格式
const transformData = (response) => {
  if (!response?.peers?.length) return []

  const { peers, range } = response
  const {
    minRoa,
    maxRoa,
    minRoe,
    maxRoe,
    minTotalAssets,
    maxTotalAssets,
    minNetAssets,
    maxNetAssets,
    minNetProfit,
    maxNetProfit,
  } = range

  // 标准化函数：将值映射到 0-1 范围
  const normalize = (value, min, max) => {
    // 处理最大值等于最小值的情况
    if (max === min) return 1
    return (Number(value) - Number(min)) / (Number(max) - Number(min))
  }

  const dataSource = []
  peers.forEach((peer) => {
    // ROA
    dataSource.push({
      company: peer.enterpriseName,
      indicator: 'ROA',
      value: normalize(peer.roa, minRoa, maxRoa),
      rawValue: peer.roa, // 保存原始值用于tooltip显示
    })

    // ROE
    dataSource.push({
      company: peer.enterpriseName,
      indicator: 'ROE',
      value: normalize(peer.roe, minRoe, maxRoe),
      rawValue: peer.roe,
    })

    // 总资产
    dataSource.push({
      company: peer.enterpriseName,
      indicator: '总资产',
      value: normalize(peer.totalAssets, minTotalAssets, maxTotalAssets),
      rawValue: peer.totalAssets,
    })

    // 净资产
    dataSource.push({
      company: peer.enterpriseName,
      indicator: '净资产',
      value: normalize(peer.netAssets, minNetAssets, maxNetAssets),
      rawValue: peer.netAssets,
    })

    // 净利润
    dataSource.push({
      company: peer.enterpriseName,
      indicator: '净利润',
      value: normalize(peer.netProfit, minNetProfit, maxNetProfit),
      rawValue: peer.netProfit,
    })

    // 杠杆率 - 使用当前数据集的最大最小值
    const leverageValues = peers.map((p) => Number(p.leverageRatio))
    const minLeverage = Math.min(...leverageValues)
    const maxLeverage = Math.max(...leverageValues)
    dataSource.push({
      company: peer.enterpriseName,
      indicator: '杠杆率',
      value: normalize(peer.leverageRatio, minLeverage, maxLeverage),
      rawValue: peer.leverageRatio,
    })
  })

  return dataSource
}

const IndustryIndexComparison = () => {
  const [year, setYear] = useState('2023-12-31')
  const [isTableVisible, setIsTableVisible] = useState(false)
  const [dataSource, setDataSource] = useState([])
  const getCompanies = async (busiDate) => {
    const res = await peerComparisonController.postPeerComparison({
      busiDate,
    })
    setDataSource(res)
  }

  const [yearOptions, setYearOptions] = useState([])

  const handleChangeYear = async (value) => {
    setYear(value)
    await getCompanies(value)
  }

  const getYears = async () => {
    const res = await peerComparisonController.postComparisonLatestYear({})
    setYearOptions(
      res.map((item) => ({ label: `${moment(item).format('YYYY')}年度`, value: item }))
    )
    handleChangeYear(res[0])
  }
  useEffect(() => {
    getYears()
  }, [])
  const configModal = useMemo(
    () =>
      new ModalStore({
        onOpen: async () => {
          const res = await peerComparisonController.postComparisonEnterpriseState({
            busiDate: year,
          })
          return res
        },
        onFinish: async (data) => {
          await peerComparisonController.postComparisonConfig({
            enterpriseCodes: data.enterpriseCodes,
          })
          await getCompanies(year)
          message.success('保存成功')
          configModal.close()
        },
      }),
    []
  )
  const detailDrawer = Drawer.useStore()
  return (
    <>
      <Title title={'行业指标对比'} />
      <div className={styles.container}>
        <div className={styles.header}>
          <div></div>
          <div className={styles.actions}>
            <Select
              value={year}
              onChange={handleChangeYear}
              options={yearOptions}
              style={{ width: 120 }}
            />
            <Button
              icon={<SettingOutlined />}
              onClick={() => configModal.open()}
              style={{ marginLeft: 16 }}
            >
              对标公司配置
            </Button>
            <Button
              icon={<TableOutlined />}
              onClick={() => detailDrawer.open({ busiDate: moment(year) })}
              style={{ marginLeft: 16 }}
            >
              数据明细表
            </Button>
          </div>
        </div>

        <div className={styles.content}>
          <div className={styles.charts}>
            <div className={styles.radarChart}>
              <RadarChart dataSource={dataSource} />
            </div>
            <div className={styles.barChart}>
              <ProgressChart dataSource={dataSource} />
            </div>
          </div>
        </div>

        <CompanyConfig configModal={configModal} />

        <DetailTable store={detailDrawer} />
      </div>
    </>
  )
}

export default observer(IndustryIndexComparison)
