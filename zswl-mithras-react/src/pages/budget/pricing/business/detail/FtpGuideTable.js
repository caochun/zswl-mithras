import { observer } from '@zswl/admin'
import _ from 'lodash'
import { AmountColumn } from '@/components/Format'
import { TableStore } from '@zswl/components'
import { useMemo } from 'react'
import newFtpMonthlyGuidanceApi from '@/api/newFtp/newFtpMonthlyGuidanceApi'
import FormatTable from '../components/FormatTable'

const bottomCell = (record, index) => {
  if ([9, 10, 11, 12, 13, 14].includes(index)) return { colSpan: 0 }
  return {
    rowSpan: 1,
  }
}

function Index({ table, canEdit = false, detail, setSubmitDisabled, isV3, publicDetail }) {
  const amountChange = _.debounce(async (val, params) => {
    const { dataSource, dataIndex, notAmount } = params
    if (dataSource[dataIndex] === val * 10000) {
      setSubmitDisabled(false)
      return
    }
    const value = notAmount ? val : val * 10000

    await newFtpMonthlyGuidanceApi
      .postGuidanceModify({
        id: dataSource[`${dataIndex}Id`],
        value,
      })
      .finally((v) => setSubmitDisabled(false))
    table.search()
  }, 2000)
  const wrapItemProps = {
    inputConfig: {
      step: 0.01,
      onChange: amountChange,
    },
  }
  const v1Columns = [
    {
      title: '风控行业分类',
      dataIndex: 'riskIndustry',
      editable: false,
      width: 150,
      fixed: 'left',
      onCell: (record, index) => {
        if (index === 0) return { rowSpan: 9 }
        if (index === 9) return { rowSpan: 3 }
        if ([1, 2, 3, 4, 5, 6, 7, 8, 10, 11].includes(index)) return { rowSpan: 0 }
        return {
          rowSpan: 1,
        }
      },
    },
    {
      title: '资产行业分类',
      dataIndex: 'assetIndustry',
      editable: false,
      width: 120,
      fixed: 'left',
      onCell: (record, index) => {
        if ([0, 3, 6, 9].includes(index)) return { rowSpan: 3 }
        if ([1, 2, 4, 5, 7, 8, 10, 11].includes(index)) return { rowSpan: 0 }
        return {
          rowSpan: 1,
        }
      },
    },
    {
      title: '地区分类',
      dataIndex: 'regional',
      width: 140,
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
          width: 100,
          onCell: (record, index) => {
            if ([9, 10, 11, 12, 13].includes(index)) return { colSpan: 3 }
            return { rowSpan: 1 }
          },
          wrapItemProps,
        }),
        AmountColumn({
          title: '国有企业',
          dataIndex: 'col1',
          editable: true,
          width: 100,
          wrapItemProps,
        }),
        AmountColumn({
          title: '其他',
          dataIndex: 'col2',
          editable: true,
          width: 100,
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
          width: 100,
          wrapItemProps,
        }),
        AmountColumn({
          title: '国有企业',
          dataIndex: 'col4',
          editable: true,
          width: 100,
          onCell: bottomCell,
          wrapItemProps,
        }),
        AmountColumn({
          title: '其他',
          dataIndex: 'col5',
          editable: true,
          width: 100,
          onCell: bottomCell,
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
          width: 100,
          onCell: (record, index) => {
            if ([9, 10, 11, 12, 13, 14].includes(index)) return { colSpan: 3 }
            return {
              rowSpan: 1,
            }
          },
          wrapItemProps,
        }),
        AmountColumn({
          title: '国有企业',
          dataIndex: 'col7',
          editable: true,
          width: 100,
          onCell: bottomCell,
          wrapItemProps,
        }),
        AmountColumn({
          title: '其他',
          dataIndex: 'col8',
          editable: true,
          width: 100,
          onCell: bottomCell,
          wrapItemProps,
        }),
      ],
    },
  ]
  const v2Columns = [
    {
      title: '风控行业分类',
      dataIndex: 'riskIndustry',
      editable: false,
      width: 150,
      fixed: 'left',
      onCell: (record, index) => {
        if (index === 0) return { rowSpan: 9 }
        if ([9, 10].includes(index)) return { colSpan: 1, rowSpan: 1 }
        if ([1, 2, 3, 4, 5, 6, 7, 8].includes(index)) return { rowSpan: 0 }
        return {
          rowSpan: 1,
        }
      },
    },
    {
      title: '资产行业分类',
      dataIndex: 'assetIndustry',
      editable: false,
      width: 120,
      fixed: 'left',
      onCell: (record, index) => {
        if ([0, 3, 6].includes(index)) return { rowSpan: 3 }
        if ([1, 2, 4, 5, 7, 8].includes(index)) return { rowSpan: 0 }
        return {
          rowSpan: 1,
        }
      },
    },
    {
      title: '地区分类',
      dataIndex: 'regional',
      width: 140,
      editable: false,
      fixed: 'left',
      onCell: (record, index) => {
        return {
          rowSpan: 1,
        }
      },
    },
    {
      title: '上市公司/国有企业',
      children: [
        AmountColumn({
          title: '一年内（含）',
          dataIndex: 'col0',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
        AmountColumn({
          title: '1-3年（含）',
          dataIndex: 'col1',
          editable: true,
          width: 120,
          wrapItemProps,
        }),
        AmountColumn({
          title: '3年以上',
          dataIndex: 'col2',
          editable: true,
          width: 120,
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
          title: '1-3年（含）',
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
          title: '1-3年（含）',
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

  const store = useMemo(() => {
    if (table) return table
    return new TableStore({
      pagination: false,
      request: () => {
        return detail
      },
    })
  }, [detail, table])
  return (
    <>
      <FormatTable
        needClass={false}
        columns={isV3 ? v2Columns : v1Columns}
        store={store}
        editable={canEdit}
        rowKey={'id'}
      />
      {isV3 && (
        <FormatTable
          needClass={false}
          columns={[
            {
              title: '公共事业类（民生消费类）',
              children: [
                {
                  title: '风控行业分类',
                  dataIndex: 'riskIndustry',
                  editable: false,
                  onCell: (record, index) => {
                    if ([0, 3, 6].includes(index)) return { rowSpan: 3 }
                    if ([1, 2, 4, 5, 7, 8].includes(index)) return { rowSpan: 0 }
                    return {
                      rowSpan: 1,
                    }
                  },
                },
                {
                  title: '地区分类',
                  dataIndex: 'regional',
                  editable: false,
                  onCell: (record, index) => {
                    return {
                      rowSpan: 1,
                      colSpan: 1,
                    }
                  },
                },
                AmountColumn({
                  title: '一年内（含）',
                  dataIndex: 'col0',
                  editable: true,
                  wrapItemProps,
                }),
                AmountColumn({
                  title: '1-3年（含）',
                  dataIndex: 'col1',
                  editable: true,
                  wrapItemProps,
                }),
                AmountColumn({
                  title: '3年以上',
                  dataIndex: 'col2',
                  editable: true,
                  wrapItemProps,
                }),
              ],
            },
          ]}
          dataSource={publicDetail}
          editable={canEdit}
          rowKey={'id'}
        />
      )}
    </>
  )
}

export default observer(Index)
