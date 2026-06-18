import { AmountColumn, AmountFormat, FiledFormat } from '@/components/Format'
import { getQuery, observer } from '@zswl/admin'
import FormatTable from './components/FormatTable'
import { Input } from 'antd'
import { useMemo } from 'react'
import moment from 'moment'

function Index({ detail, columnProps, canEdit = false, month }) {
  const { month: queryMonth } = getQuery()
  const titleMoment = useMemo(() => {
    return moment(month ?? queryMonth).format('yyyy年 MM 月')
  })
  const guidanceColumns = [
    {
      title: `${titleMoment}票据买入/卖出指导报价`,
      children: [
        AmountColumn({
          title: '卖出价',
          dataIndex: 'sellingPrice',
          // render,
          editable: true,
          ...columnProps,
        }),
        {
          title: '买入价',
          dataIndex: 'buyingPrice',
          render: (val) => <FiledFormat value={val} />,
          editable: (dataSource, index) => {
            const onBlur = columnProps?.wrapItemProps?.inputConfig?.onBlur
            const dataIndex = 'buyingPrice'
            return {
              element: (
                <Input
                  onBlur={(e) =>
                    onBlur && onBlur(e, { dataSource, dataIndex, index, notAmount: true })
                  }
                />
              ),
            }
          },
        },
      ],
    },
  ]

  const creditTermColumns = [
    {
      title: `${titleMoment}FTP收益指导报价`,
      children: [
        AmountColumn({
          title: '1年内(含)',
          dataIndex: 'oneYear',
          // render,
          width: 120,
          editable: true,
          ...columnProps,
        }),
        AmountColumn({
          title: '1-3年(含)',
          dataIndex: 'oneToThreeYear',
          // render,
          width: 120,
          editable: true,
          ...columnProps,
        }),
        AmountColumn({
          title: '3年以上',
          dataIndex: 'moreThanThreeYear',
          // render,
          width: 120,
          editable: true,
          ...columnProps,
        }),
      ],
    },
  ]

  return (
    <>
      <FormatTable
        dataSource={detail}
        columns={creditTermColumns}
        needClass={false}
        className="titleNoColor"
        rowKey={'id'}
        scroll={false}
        editable={canEdit}
      />
      <FormatTable
        dataSource={detail}
        columns={guidanceColumns}
        needClass={false}
        rowKey={'id'}
        className="titleNoColor"
        scroll={false}
        editable={canEdit}
      />
    </>
  )
}

export default observer(Index)
