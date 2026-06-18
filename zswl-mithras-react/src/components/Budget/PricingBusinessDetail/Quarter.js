import { observer } from '@zswl/admin'
import _, { uniqueId } from 'lodash'
import { AmountColumn } from '@/components/Format'
import { Form, TableStore } from '@zswl/components'
import { useMemo } from 'react'
import newFtpQuarterlyBasePricingApi from '@/api/budget/pricing/ftp/newFtpQuarterlyBasePricingApi'
import FormatTable from './components/FormatTable'
import TextAreaEditable from './components/TextAreaEditable'
import { REGIONAL, ASSET_INDUSTRY } from './enum'
import newFtpBaseInfoApi from '@/api/budget/pricing/ftp/newFtpBaseInfoApi'
import newFtpMonthlyGuidanceExtApi from '@/api/budget/pricing/ftp/newFtpMonthlyGuidanceExtApi'
import QuarterPricingExt from './QuarterPricingExt'

function Index({ mainId, businessVersion, descChange, canEdit, setSubmitDisabled, isV3 }) {
  const amountChange = _.debounce(async (val, params) => {
    const { dataSource, dataIndex, notAmount } = params
    if (dataSource[dataIndex] === val * 10000) {
      setSubmitDisabled(false)
      return
    }
    const value = notAmount ? val : val * 10000
    await newFtpQuarterlyBasePricingApi
      .postPricingModify({
        id: dataSource[`${dataIndex}Id`],
        value,
      })
      .finally((v) => setSubmitDisabled(false))
  }, 1000)
  const wrapItemProps = {
    inputConfig: {
      step: 0.01,
      onChange: amountChange,
      min: -Infinity,
    },
  }

  const columnsV3 = [
    {
      title: '资产行业分类',
      dataIndex: 'assetIndustry',
      editable: false,
      width: 140,
      onCell: (record, index) => {
        if ([0, 3, 6, 9].includes(index)) return { rowSpan: 3 }
        if ([1, 2, 4, 5, 7, 8, 10, 11].includes(index)) return { rowSpan: 0 }
        return {
          rowSpan: 1,
        }
      },
      fixed: 'left',
    },
    {
      title: '地区分类',
      dataIndex: 'regional',
      width: 160,
      editable: false,
      fixed: 'left',
    },
    {
      title: '上市公司/国有企业',
      children: [
        AmountColumn({
          title: '一年内（含）',
          dataIndex: 'col0',
          editable: true,
          width: 160,
          wrapItemProps,
        }),
        AmountColumn({
          title: '1-3年期(含)',
          dataIndex: 'col1',
          editable: true,
          width: 160,
          wrapItemProps,
        }),
        AmountColumn({
          title: '3年以上',
          dataIndex: 'col2',
          editable: true,
          width: 160,
          wrapItemProps,
        }),
      ],
    },
    {
      title: '其他上市公司',
      children: [
        AmountColumn({
          title: '一年内（含）',
          dataIndex: 'col3',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
        AmountColumn({
          title: '1-3年期(含)',
          dataIndex: 'col4',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
        AmountColumn({
          title: '3年以上',
          dataIndex: 'col5',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
      ],
    },
    {
      title: '其他',
      children: [
        AmountColumn({
          title: '一年内（含）',
          dataIndex: 'col6',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
        AmountColumn({
          title: '1-3年期(含)',
          dataIndex: 'col7',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
        AmountColumn({
          title: '3年以上',
          dataIndex: 'col8',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
      ],
    },
  ]
  const columns = [
    {
      title: '资产行业分类',
      dataIndex: 'assetIndustry',
      editable: false,
      width: 140,
      onCell: (record, index) => {
        if ([0, 3, 6, 9].includes(index)) return { rowSpan: 3 }
        if ([1, 2, 4, 5, 7, 8, 10, 11].includes(index)) return { rowSpan: 0 }
        return {
          rowSpan: 1,
        }
      },
      fixed: 'left',
    },
    {
      title: '地区分类',
      dataIndex: 'regional',
      width: 160,
      editable: false,
      fixed: 'left',
    },
    {
      title: ' 一年内（含）',
      children: [
        AmountColumn({
          title: '上市公司',
          dataIndex: 'col0',
          editable: true,
          width: 160,
          wrapItemProps,
        }),
        AmountColumn({
          title: '国有企业',
          dataIndex: 'col1',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
        AmountColumn({
          title: '其他',
          dataIndex: 'col2',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
      ],
    },
    {
      title: ' 1-3年期(含)',
      children: [
        AmountColumn({
          title: '上市公司',
          dataIndex: 'col3',
          editable: true,
          width: 160,
          wrapItemProps,
        }),
        AmountColumn({
          title: '国有企业',
          dataIndex: 'col4',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
        AmountColumn({
          title: '其他',
          dataIndex: 'col5',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
      ],
    },
    {
      title: ' 3年以上',
      children: [
        AmountColumn({
          title: '上市公司',
          dataIndex: 'col6',
          editable: true,
          width: 160,
          wrapItemProps,
        }),
        AmountColumn({
          title: '国有企业',
          dataIndex: 'col7',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
        AmountColumn({
          title: '其他',
          dataIndex: 'col8',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
      ],
    },
  ]
  const isFormApproval = !canEdit
  const table = useMemo(
    () =>
      new TableStore({
        pagination: false,
        request: async (searchParams) => {
          const api = isFormApproval
            ? newFtpBaseInfoApi.postCompareQuarterly
            : newFtpQuarterlyBasePricingApi.postPricingDetail
          const res = await api({ mainId, version: businessVersion })
          const tableList = []
          // 循环取 row 1- 9 的数据
          for (let i = 1; i <= 9; i++) {
            const row = {}
            const data = (isFormApproval ? res[`row${i}`]?.diffValueList : res[`row${i}`]) ?? []
            data.forEach((item, index) => {
              row[`col${index}`] = item.value
              row[`col${index}Id`] = item.id
            })

            row.assetIndustry = i < 9 ? ASSET_INDUSTRY[Math.ceil((i - 1) / 3)] : '/'
            row.regional = i < 13 ? REGIONAL[(i - 1) % 3] : '/'
            row.id = uniqueId()
            tableList.push(row)
          }

          return tableList ?? []
        },
      }),
    []
  )

  return (
    <>
      <FormatTable
        columns={isV3 ? columnsV3 : columns}
        store={table}
        editable={canEdit}
        rowKey="id"
      />
      <Form.Item name="QUARTERLY_PRICING" noStyle>
        <TextAreaEditable
          isEdit={canEdit}
          onFocus={() => setSubmitDisabled(true)}
          rows={6}
          onBlur={(e) => descChange(e, 'QUARTERLY_PRICING')}
        />
      </Form.Item>
      <QuarterPricingExt
        descChange={descChange}
        setSubmitDisabled={setSubmitDisabled}
        mainId={mainId}
        canEdit={canEdit}
      />

      <div className="z-sub-title" style={{ marginBottom: 12 }}>
        补充说明
      </div>
      <Form.Item name="QUARTERLY_SUPPLEMENT">
        <TextAreaEditable
          isEdit={canEdit}
          rows={4}
          onFocus={() => setSubmitDisabled(true)}
          placeholder="请输入补充说明"
          max={1000}
          onBlur={(e) => descChange(e, 'QUARTERLY_SUPPLEMENT')}
        />
      </Form.Item>
    </>
  )
}

export default observer(Index)
