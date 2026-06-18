import React, { useEffect, useMemo, useState } from 'react'
import { DatePicker, Descriptions, Modal, ModalStore, Table } from '@zswl/components'
import styles from './styles.less'
import { getQuery, observer } from '@zswl/admin'
import { Amount, DetailLayout } from '@/components'
import dayjs from 'dayjs'
import { AmountColumn } from '@/components/Format'
import { Divider } from 'antd'
import { saveServer } from '@/utils'

import customerUnifiedViewController from '@/api/customerView/riskAreaApi'
// 评价详情弹窗组件
const RatingDetailModal = observer(({ store }) => {
  const { rateDetail } = store.getInitialValues() ?? {}
  const ratingItems = [
    AmountColumn({ title: '区域最终得分', dataIndex: 'finalScore', initFormat: 1 }),
    AmountColumn({ title: '地级市评级得分', dataIndex: 'ratingScore', initFormat: 1 }),
    { title: '地级市初始级别', dataIndex: 'initialLevel' },
    { title: '级别调整', dataIndex: 'adjustingLevel' },
    { title: '调整后级别', dataIndex: 'adjustedLevel' },
    AmountColumn({ title: '一般公共预算收入(亿元)', dataIndex: 'gpBudgetRevenue', initFormat: 1 }),
    AmountColumn({ title: 'GDP(亿元)', dataIndex: 'gdp', initFormat: 1 }),
    AmountColumn({
      title: 'GDP第二产业占比',
      dataIndex: 'secondaryIndustryRt',
      initFormat: 1,
      suffix: '%',
    }),
    AmountColumn({
      title: '城镇居民人均可支配收入(元)',
      dataIndex: 'updi',
      initFormat: 1,
    }),
    AmountColumn({
      title: '人口同比变化',
      dataIndex: 'residentPopYoyRatio',
      initFormat: 1,
      suffix: '%',
    }),
    AmountColumn({
      title: '房屋平均单价(二手房)(元/㎡)',
      dataIndex: 'houseAvgPriceSc',
      initFormat: 1,
    }),
    AmountColumn({
      title: '房屋平均单价(新房)(元/㎡)',
      dataIndex: 'houseAvgPriceNew',
      initFormat: 1,
    }),
    AmountColumn({
      title: '政府性基金收入(亿元)',
      dataIndex: 'governmentFundIncome',
      initFormat: 1,
    }),
    AmountColumn({
      title: '税收收入占比',
      dataIndex: 'taxIncomeRatio',
      initFormat: 1,
      suffix: '%',
    }),
    AmountColumn({ title: '财政平衡性', dataIndex: 'budgetBalance', initFormat: 1 }),
    AmountColumn({
      title: '负债率',
      dataIndex: 'debtRatio',
      initFormat: 1,
      suffix: '%',
    }),
    AmountColumn({
      title: '债务率',
      dataIndex: 'debtRate',
      initFormat: 1,
      suffix: '%',
    }),
    AmountColumn({ title: '广义城投债务倍数', dataIndex: 'debtLargeRt', initFormat: 1 }),
    AmountColumn({
      title: '政府透明度',
      dataIndex: 'governmentTransparency',
      initFormat: 1,
      suffix: '%',
    }),
    AmountColumn({
      title: '财政收入占GDP比例',
      dataIndex: 'comprehensiveGdpRt',
      initFormat: 1,
      suffix: '%',
    }),
  ]

  return (
    <Modal title="评价详情" store={store} width={1000} onOk={() => store.close()}>
      <Table
              columnsFilter={'components_AreaData_1'}
              onFilter={(key,val) => saveServer('components_AreaData_1',val)}
        columns={ratingItems}
        dataSource={[rateDetail]}
        pagination={false}
        scroll={{ x: 'max-content' }}
      />
    </Modal>
  )
})

const FinancialDesc = () => {
  const { uscc, enterpriseName } = getQuery()
  const [year, setYear] = useState(dayjs('2023'))
  // 经济指标数据
  const [economicData, setEconomicData] = useState([])
  const getEconomicData = async () => {
    const res = await customerUnifiedViewController.postDetailAreaEconomy({
      uscc,
      dataYear: year.year(),
    })
    setEconomicData(res)
  }
  useEffect(() => {
    getEconomicData()
  }, [year])

  const economicItems = [
    { label: '常住人口(万人)', dataIndex: 'residentPop' },
    { label: '户籍人口(万人)', dataIndex: 'householdRegisteredPop' },
    AmountColumn({ label: 'GDP(万元)', dataIndex: 'gdp', initFormat: 1 }),
    AmountColumn({ label: '人均GDP(元)', dataIndex: 'perCapitaGdp', initFormat: 1 }),
    AmountColumn({ label: 'GDP增速', dataIndex: 'gdpGrowth', initFormat: 1, suffix: '%' }),
    AmountColumn({ label: '第一产业GDP(万元)', dataIndex: 'primarySectorGdp', initFormat: 1 }),
    AmountColumn({ label: '第二产业GDP(万元)', dataIndex: 'secondSectorGdp', initFormat: 1 }),
    AmountColumn({ label: '第三产业GDP(万元)', dataIndex: 'tertiarySectorGdp', initFormat: 1 }),
    AmountColumn({
      label: '一般公共预算收入(万元)',
      dataIndex: 'generalPublicBudgetIncome',
      initFormat: 1,
    }),
    AmountColumn({
      label: '一般公共预算支出(万元)',
      dataIndex: 'generalPublicBudgetExpend',
      initFormat: 1,
    }),
    AmountColumn({
      label: '税收收入占比',
      dataIndex: 'taxIncomeRatio',
      initFormat: 1,
      suffix: '%',
    }),
    AmountColumn({ label: '转移性收入(万元)', dataIndex: 'transferIncome', initFormat: 1 }),
    AmountColumn({ label: '上级补助收入(万元)', dataIndex: 'higherAuthorityGrant', initFormat: 1 }),
    AmountColumn({ label: '转移支付收入(万元)', dataIndex: 'transferPayIncome', initFormat: 1 }),
    AmountColumn({ label: '政府性基金支出(万元)', dataIndex: 'govFundExpend', initFormat: 1 }),
    AmountColumn({ label: '政府性基金收入(万元)', dataIndex: 'govFundIncome', initFormat: 1 }),
    AmountColumn({ label: '土地出让收入(万元)', dataIndex: 'landTransferIncome', initFormat: 1 }),
    AmountColumn({
      label: '国有资本经营收入(万元)',
      dataIndex: 'stateCapitalOperatingIncome',
      initFormat: 1,
    }),
    AmountColumn({
      label: '国有资本经营支出(万元)',
      dataIndex: 'stateCapitalOperatingExpend',
      initFormat: 1,
    }),
    AmountColumn({
      label: '地方政府债务余额(万元)',
      dataIndex: 'localGovDebtBalance',
      initFormat: 1,
    }),
    AmountColumn({
      label: '地方政府债务限额(万元)',
      dataIndex: 'localGovDebtQuota',
      initFormat: 1,
    }),
    AmountColumn({
      label: '政府一般债务余额(万元)',
      dataIndex: 'govGeneralDebtBalance',
      initFormat: 1,
    }),
    AmountColumn({
      label: '政府专项债务余额(万元)',
      dataIndex: 'govSpecialDebtBalance',
      initFormat: 1,
    }),
    AmountColumn({
      label: '本年土地成交价款(万元)',
      dataIndex: 'thisYearLandTradeAmount',
      initFormat: 1,
    }),
    AmountColumn({
      label: '商品房销售面积(万m^2)',
      dataIndex: 'commerceHouseSalesArea',
      initFormat: 1,
    }),
    AmountColumn({ label: '商品房销售额(万元)', dataIndex: 'commerceHouseSales', initFormat: 1 }),
    AmountColumn({
      label: '金融机构各项存款余额(万元)',
      dataIndex: 'finInstCnyDepositBalance',
      initFormat: 1,
    }),
    AmountColumn({
      label: '金融机构各项贷款余额(万元)',
      dataIndex: 'finInstCnyLoanBalance',
      initFormat: 1,
    }),
    AmountColumn({ label: '固定资产投资(万元)', dataIndex: 'fixedAssetsInvest', initFormat: 1 }),
    AmountColumn({
      label: '固定资产投资增速',
      dataIndex: 'fixedAssetsInvestGrowth',
      initFormat: 1,
      suffix: '%',
    }),
    AmountColumn({ label: '房地产投资(万元)', dataIndex: 'realEstateDevInvest', initFormat: 1 }),
    AmountColumn({
      label: '房地产投资增速',
      dataIndex: 'realEstateDevInvestGrowth',
      initFormat: 1,
      suffix: '%',
    }),
    AmountColumn({
      label: '社会消费品零售总额(万元)',
      dataIndex: 'goodsRetailSales',
      initFormat: 1,
    }),
    AmountColumn({ label: '工业总产值(万元)', dataIndex: 'industrialOutput', initFormat: 1 }),
    AmountColumn({ label: '工业增加值(万元)', dataIndex: 'industrialAddedValue', initFormat: 1 }),
    AmountColumn({ label: '进口总额(万美元)', dataIndex: 'totalImportUsd', initFormat: 1 }),
    AmountColumn({ label: '出口总额(万美元)', dataIndex: 'totalExportUsd', initFormat: 1 }),
    AmountColumn({ label: '进出口总额(万美元)', dataIndex: 'totalImportExportUsd', initFormat: 1 }),
    AmountColumn({
      label: '社会消费品零售总额增速',
      dataIndex: 'goodsRetailSalesGrowth',
      initFormat: 1,
      suffix: '%',
    }),
  ]
  return (
    <Descriptions
      title="经济数据"
      column={2}
      items={economicItems}
      labelStyle={{ width: 200, padding: 20 }}
      contentStyle={{ width: 200 }}
      dataSource={economicData}
      extra={
        <div>
          数据年份：
          <DatePicker.YearPicker value={year} onChange={(value) => setYear(value)} />
        </div>
      }
    />
  )
}

// 新增财务指标组件
const FinancialIndicators = () => {
  const [year, setYear] = useState(dayjs('2023'))
  const { uscc } = getQuery()
  // 计算指标数据
  const [financialData, setFinancialData] = useState([])

  const getFinancialData = async () => {
    const res = await customerUnifiedViewController.postDetailCtzReginEconomy({
      uscc,
      dataYear: year.year(),
    })
    setFinancialData(res)
  }

  useEffect(() => {
    getFinancialData()
  }, [year])
  // 根据后端接口定义修改 items 字段
  const financialItems = [
    AmountColumn({ label: '地区城投债余额(亿元)', dataIndex: 'ctzDebtBalance', initFormat: 1 }),
    AmountColumn({ label: '地区城投平台数量(个)', dataIndex: 'ctzComCount', initFormat: 1 }),
    AmountColumn({ label: '地区隐性债务(亿元)', dataIndex: 'hideDebt', initFormat: 1 }),
    AmountColumn({
      label: '地区隐性债务率',
      dataIndex: 'hideDebtRate',
      suffix: '%',
      initFormat: 1,
    }),
    AmountColumn({
      label: '财政自给率',
      dataIndex: 'finSelfSufficiencyRate',
      suffix: '%',
      initFormat: 1,
    }),
    AmountColumn({
      label: '负债率',
      dataIndex: 'debtRatio',
      suffix: '%',
      initFormat: 1,
    }),
    AmountColumn({
      label: '债务率',
      dataIndex: 'debtRate',
      suffix: '%',
      initFormat: 1,
    }),
    AmountColumn({
      label: '债务率(宽口径，本级)',
      dataIndex: 'debtRateLocal',
      suffix: '%',
      initFormat: 1,
    }),
    AmountColumn({
      label: '债务率(宽口径，全辖)',
      dataIndex: 'debtRateFull',
      suffix: '%',
      initFormat: 1,
    }),
    AmountColumn({
      label: '税收收入占比',
      dataIndex: 'taxRevenueRate',
      suffix: '%',
      initFormat: 1,
    }),
    AmountColumn({
      label: '一般公共预算收入增速',
      dataIndex: 'gpBudgetRevenueAdd',
      suffix: '%',
      initFormat: 1,
    }),
    { label: '地方综合财力(亿元)', dataIndex: 'comprehensiveResources' },
  ]

  return (
    <Descriptions
      title="计算指标"
      column={2}
      items={financialItems}
      dataSource={financialData}
      labelStyle={{ width: 200, padding: 20 }}
      contentStyle={{ width: 200 }}
      extra={
        <div>
          数据年份：
          <DatePicker.YearPicker value={year} onChange={(value) => setYear(value)} />
        </div>
      }
    />
  )
}

const AreaInfo = () => {
  const [year, setYear] = useState(dayjs())
  const modal = useMemo(() => new ModalStore(), [])
  const { uscc } = getQuery()
  const [areaData, setAreaData] = useState([])
  const getList = async () => {
    const res = await customerUnifiedViewController.postAreaRating({
      uscc,
      dataYear: year.year(),
    })
    setAreaData(res)
  }
  useEffect(() => {
    getList()
  }, [year])
  return (
    <>
      <div className={styles.areaInfo}>
        <div className={styles.location}>
          <img src={'/public/assets/risk/customerView/icon-location.svg'} alt="位置" />
          <span>所属区域：</span>
          <span>
            {areaData?.rateDetail?.province ?? '-'} / {areaData?.rateDetail?.city ?? '-'}
          </span>
        </div>
        <div className={styles.yearPicker}>
          {/* <span>数据年份：</span>
          <DatePicker.YearPicker value={year} onChange={(value) => setYear(value)} /> */}
        </div>
      </div>
      {/* <div className={styles.header}>
        <div
          className={styles.ratingDetail}
          onClick={() => modal.open({ rateDetail: areaData?.rateDetail })}
        >
          评价详情
        </div>
        <div className={styles.ratingInfo}>
          <div className={styles.rating}>
            <img src={require('/public/assets/risk/customerView/icon-grade.png')} alt="评级" />
            <div className={styles.grade}>
              <span className={styles.value}>{areaData?.rateResult ?? '-'}</span>
              <div>内部评级</div>
            </div>
          </div>
          <Divider type="vertical" style={{ height: 'auto' }} />
          <div className={styles.rating}>
            <img src={'/public/assets/risk/customerView/icon-range.svg'} alt="得分" />
            <div className={styles.score}>
              <span className={styles.value}>{areaData?.rank ?? '-'}</span>
              <div>排名</div>
            </div>
          </div>
        </div>
      </div> */}

      {/* 评价详情弹窗 */}
      <RatingDetailModal store={modal} />
    </>
  )
}
const AreaData = ({ data }) => {
  const anchorList = [{ label: '区域评价' }, { label: '经济数据' }, { label: '计算指标' }]

  return (
    <div className={styles.areaData}>
      <DetailLayout anchorList={anchorList} moduleName="policyManage">
        <AreaInfo />
        <FinancialDesc />
        <FinancialIndicators />
      </DetailLayout>
    </div>
  )
}

export default observer(AreaData)
