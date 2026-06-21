import FormatTable from './components/FormatTable'
import { observer } from '@zswl/admin'
import _, { uniqueId } from 'lodash'
import { AmountColumn, AmountFormat, FiledFormat } from '@/components/Format'
import { App, TableStore } from '@zswl/components'
import { hasValue } from '@/utils'
import { forwardRef, useImperativeHandle, useMemo } from 'react'
import newFtpMonthlyDeductionApi from '@/api/budget/pricing/ftp/newFtpMonthlyDeductionApi'
import newFtpBaseInfoApi from '@/api/budget/pricing/ftp/newFtpBaseInfoApi'

const render = (val) => {
  const value = val?.value ?? val
  return <AmountFormat value={value} isChange={val?.isChange} />
}
const matchOptionRender = (val) => {
  const value = val?.value ? val?.value : val
  const title = App.matchOption('termRange', value).label
  return <FiledFormat title={title} />
}
const PercentageRender = (val) => {
  const value = val?.value ? val?.value : val
  const newValue = hasValue(value) ? `${value / 10000}%` : '-'
  return <FiledFormat title={newValue} isChange={val?.isChange} />
}

function BudgetPricingBusinessCreditTermTable({ detail, mainId, canEdit, businessVersion, setSubmitDisabled, isV3 }, ref) {
  const isFormApproval = !canEdit

  const creditTermStore = useMemo(() => {
    return new TableStore({
      pagination: false,
      request: async (params) => {
        if (detail) return detail
        const api = isFormApproval
          ? newFtpBaseInfoApi.postCompareDeduction
          : newFtpMonthlyDeductionApi.postDeductionList
        const res = await api({ mainId, version: businessVersion })
        return res.map(({ id, ...rest }) => ({ id: id?.value ?? id ?? uniqueId(), ...rest }))
      },
    })
  }, [mainId, detail])

  useImperativeHandle(ref, () => ({
    creditTermStore,
  }))

  const amountChange = _.debounce(async (val, params) => {
    const { dataSource, dataIndex, notAmount } = params
    if (dataSource[dataIndex] === val * 10000) {
      setSubmitDisabled(false)
      return
    }
    const value = notAmount ? val : val * 10000

    await newFtpMonthlyDeductionApi
      .postDeductionModify({
        id: dataSource.id,
        ...dataSource,
        [dataIndex]: value,
      })
      .finally((v) => setSubmitDisabled(false))
    creditTermStore.search()
  }, 1500)

  const amountDefault = {
    editable: true,
    render,
    wrapItemProps: {
      inputConfig: {
        step: 0.01,
        onChange: amountChange,
        min: -Infinity,
      },
    },
  }
  const V3Columns = [
    { title: '公共事业类', dataIndex: 'publicUtilities', assetDataIndex: 'Public' },
    { title: '民生消费类', dataIndex: 'civilConsumption', assetDataIndex: 'Civil' },
    { title: '国有产业类', dataIndex: 'stateOwnedIndustry', assetDataIndex: 'StateOwned' },
  ].map(({ title, dataIndex, assetDataIndex }) => ({
    title,
    children: [
      AmountColumn({
        title: '资产行业计价',
        dataIndex: `asset${assetDataIndex}`,
        ...amountDefault,
        width: 200,
      }),
      {
        title: '地区分类计价',
        children: [
          AmountColumn({
            title: '浙江地区',
            dataIndex: `${dataIndex}RegionZhejiang`,
            ...amountDefault,
          }),
          AmountColumn({
            title: '鼓励支持类地区（除浙江）',
            dataIndex: `${dataIndex}RegionEncourage`,
            ...amountDefault,
            width: 220,
          }),
          AmountColumn({
            title: '其他地区',
            dataIndex: `${dataIndex}RegionOther`,
            ...amountDefault,
          }),
        ],
      },
    ],
  }))
  const columns = [
    {
      title: '期限',
      dataIndex: 'termRange',
      ...amountDefault,
      matchOption: 'termRange',
      render: matchOptionRender,
      width: 160,
      fixed: 'left',
      editable: false,
    },
    {
      title: '成本费用计价',
      children: [
        //  融资成本	担保成本	小计
        AmountColumn({ title: '融资成本', dataIndex: 'financingCost', ...amountDefault }),
        AmountColumn({
          title: '担保成本',
          dataIndex: 'guaranteeCost',
          ...amountDefault,
        }),
        AmountColumn({
          title: '小计',
          dataIndex: 'subtotalCost',
          render,
          width: 80,
          editable: false,
        }),
      ],
    },
    {
      title: '金融市场波动计价',
      children: [
        //国股银票转贴现利率	权重	1年期shibor利率	权重	同期LPR利率	权重	融资成本趋势	权重	小计
        AmountColumn({
          title: '10年期国债收益率',
          dataIndex: 'treasuryBondYield',
          ...amountDefault,
          width: 160,
        }),
        AmountColumn({
          title: '权重',
          dataIndex: 'treasuryBondYieldWeight',
          ...amountDefault,
          render: (val) => PercentageRender(val),
        }),
        AmountColumn({
          title: '1年期shibor利率',
          dataIndex: 'shiborRate',
          ...amountDefault,
          width: 140,
        }),
        AmountColumn({
          title: '权重',
          dataIndex: 'shiborRateWeight',
          ...amountDefault,
          render: (val) => PercentageRender(val),
        }),
        AmountColumn({
          title: '同期LPR利率',
          dataIndex: 'lprRate',
          ...amountDefault,
          width: 120,
        }),
        AmountColumn({
          title: '权重',
          dataIndex: 'lprRateWeight',
          ...amountDefault,
          render: (val) => PercentageRender(val),
        }),
        AmountColumn({
          title: '融资成本趋势',
          dataIndex: 'financingCostTrends',
          ...amountDefault,
          width: 120,
        }),
        AmountColumn({
          title: '权重',
          dataIndex: 'financingCostTrendsWeight',
          ...amountDefault,
          render: (val) => PercentageRender(val),
        }),
        AmountColumn({ title: '小计', dataIndex: 'subtotalRate', render, width: 80 }),
      ],
    },

    {
      title: isV3 ? '其他产业类' : '产业类',
      children: [
        {
          title: '资产行业计价',
          children: [
            //鼓励介入类	适度支持类	谨慎支持类
            AmountColumn({
              title: '鼓励介入类',
              dataIndex: 'assetEncourage',
              ...amountDefault,
              width: 120,
            }),
            AmountColumn({
              title: '适度支持类',
              dataIndex: 'assetModerate',
              ...amountDefault,
              width: 120,
            }),
            AmountColumn({
              title: '谨慎支持类',
              ...amountDefault,
              dataIndex: 'assetCautious',
              width: 120,
            }),
          ],
        },
        {
          title: '地区分类计价',
          children: [
            //鼓励介入类	适度支持类	谨慎支持类
            AmountColumn({
              title: '浙江地区',
              dataIndex: 'industryRegionZhejiang',
              ...amountDefault,
            }),
            AmountColumn({
              title: '鼓励支持类地区（除浙江）',
              dataIndex: 'industryRegionEncourage',
              ...amountDefault,
              width: 210,
            }),
            AmountColumn({
              title: '其他地区',
              dataIndex: 'industryRegionOther',
              ...amountDefault,
            }),
          ],
        },
        {
          title: '客户主体计价',
          children: [
            //国有/上市公司	其他类
            AmountColumn({
              title: isV3 ? '上市公司/国有企业' : '上市公司',
              width: 200,
              dataIndex: isV3 ? 'customerListedStateOwned' : 'customerListed',
              ...amountDefault,
            }),
            AmountColumn({
              title: isV3 ? '其他上市企业' : '国有企业',
              width: 200,
              dataIndex: isV3 ? 'customerOtherListed' : 'customerStateOwned',
              ...amountDefault,
            }),
            AmountColumn({
              title: '其他类',
              dataIndex: 'customerOther',
              ...amountDefault,
            }),
          ],
        },
      ],
    },

    !isV3 && {
      title: '公共事业类（含民生消费类）',
      children: [
        {
          title: '地区分类计价',
          children: [
            AmountColumn({
              title: '浙江地区',
              dataIndex: 'publicUtilitiesRegionZhejiang',
              ...amountDefault,
            }),
            AmountColumn({
              title: '鼓励支持类地区（除浙江）',
              dataIndex: 'publicUtilitiesRegionEncourage',
              ...amountDefault,
              width: 220,
            }),
            AmountColumn({
              title: '其他地区',
              dataIndex: 'publicUtilitiesRegionOther',
              ...amountDefault,
            }),
          ],
        },
      ],
    },
    ...(isV3 ? V3Columns : []),
  ]

  return (
    <FormatTable
      store={creditTermStore}
      rowKey="id"
      columns={columns}
      needClass={false}
      scroll={{ x: 2400 }}
      columnWidth={100}
      editable={canEdit}
    />
  )
}

export default observer(forwardRef(BudgetPricingBusinessCreditTermTable))
