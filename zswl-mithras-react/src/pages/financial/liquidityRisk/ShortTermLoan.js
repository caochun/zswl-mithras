import { Button, Page, Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { DatePicker, Radio, Tag, Typography, message } from 'antd'
import Api from '@/api/financial/liquidity/liquidityRiskApi'
import _ from 'lodash'
import { FinancialOrgEditModal as EditModal } from '@/components/Financial/OrgEditModalEntries'
import styles from './index.less'
import useSummer from './useSummer'
import { saveServer } from '@/utils'

const { Text } = Typography

function Index({ day, store }) {
  const [time, setTime] = useState([])
  const [sumData, setSumData] = useState({})
  const [pageSum, setPageSum] = useState({})
  const shortTermTable = useMemo(
    () =>
      new TableStore({
        request: async (searchData) => {
          try {
            const fromTime = time[0].format('YYYY-MM-DD')
            const toTime = time[1].format('YYYY-MM-DD')
            const res = await Api.postLoanDetail({ fromTime, toTime, ...searchData })
            setSumData(res?.sum ?? {})
            setPageSum(res?.pageSum ?? {})
            return res?.detailBodies ?? []
          } catch (error) {
            return []
          }
        },
      }),
    [time]
  )
  const columns = useMemo(() => {
    const assetsColumns = [
      {
        title: '融资机构',
        actions: ({ organizationName: name, isRepayment, organizationId }) =>
          (name ?? []).map((name, index) => ({
            name: (
              <div>
                {name}
                <Tag color={isRepayment ? 'green' : 'red'} style={{ marginLeft: 6 }}>
                  {isRepayment ? '已还款' : '未还款'}
                </Tag>
              </div>
            ),
            onClick: () => store.createModal.open({ id: organizationId[index] }),
          })),
        width: 180,
      },
      '融资编码',
      '融资总额（万元）',
      {
        title: '还本日',
        dataIndex: 'repaymentDate',
        render: (val, { repaymentDate, remainingDays }) => {
          return (
            <div>
              {repaymentDate}
              <Tag color={'red'} style={{ marginLeft: 6 }}>
                剩余 {remainingDays} 天
              </Tag>
            </div>
          )
        },
        width: 160,
      },
      '应还本金（万元）',
      '应还利息（万元）',
      '合计还款总金额（万元）',
    ]
    return getTableColumns(ALL_COLUMNS, assetsColumns, false)
  }, [])
  const summary = useSummer({ sumData, pageSum, columns })

  useEffect(() => {
    if (!_.isEmpty(time)) {
      const fromTime = time[0].format('YYYY-MM-DD')
      const toTime = time[1].format('YYYY-MM-DD')
      shortTermTable.search({ fromTime, toTime })
    }
  }, [time])
  const [downloadLoading, setDownloadLoading] = useState(false)
  const download = async ({ key }) => {
    if (!key) return

    const needPage = key === 'download'
    const params = shortTermTable.getParams()

    const fromTime = time?.[0]?.format('YYYY-MM-DD')
    const toTime = time?.[1]?.format('YYYY-MM-DD')
    if (!fromTime || !toTime) {
      message.warn('请先选择时间')
      return
    }
    setDownloadLoading(true)
    return await Api.getLoanDownload({
      ...params,
      fromTime,
      toTime,
      needPage,
    })
      .then((res) => {
        if (res.code && res.code !== 200) message.error(res.msg)
        setDownloadLoading(false)
      })
      .catch((err) => {
        console.log('err: ', err)
        message.error(err || '下载失败')
        setDownloadLoading(false)
      })
  }
  return (
    <div>
      <Table
        columnsFilter={'financial_liquidityRisk_ShortTermLoan'}
        onFilter={(key,val) => saveServer('financial_liquidityRisk_ShortTermLoan',val)}
        store={shortTermTable}
        editable={false}
        scroll={{
          x: 1200,
        }}
        columnWidth={120}
        className={styles.tableBorder}
        autoRequest={false}
        actions={
          <p style={{ margin: 0 }}>
            <DatePicker.RangePicker value={time} onChange={setTime} style={{ marginRight: 12 }} />
            内短期贷款明细
          </p>
        }
        extra={[
          <Button.Download
            key="download"
            loading={downloadLoading}
            onClick={download}
            items={[
              { name: '下载当前页', key: 'download' },
              { name: '下载所有数据', key: 'downloadAll' },
            ]}
          >
            下载
          </Button.Download>,
        ]}
        columns={columns}
        summary={summary}
      />
      <EditModal store={store} />
    </div>
  )
}

export default observer(Index)
