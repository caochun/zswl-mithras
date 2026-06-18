import { Button, Page, Table, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { Form, InputNumber, Tag, Typography, message } from 'antd'
import Api from '@/api/liquidity/liquidityRiskApi'
import { FormAmount } from '@/components/Form'
import styles from './index.less'
import useSummer from './useSummer'
import { saveServer } from '@/utils'

const { Text } = Typography

function Index({ time = {}, store }) {
  const { days, timeFrom, timeTo } = time
  const { overdueRate, setOverdueRate, reload } = store
  const [sumData, setSumData] = useState({})
  const [pageSum, setPageSum] = useState({})

  const inflowTable = useMemo(
    () =>
      new TableStore({
        request: async ({ timeFrom, timeTo, ...searchData }) => {
          try {
            const res = await Api.postInflowDetail({
              ...searchData,
              timeFrom,
              timeTo,
            })
            setSumData(res?.sum ?? {})
            setPageSum(res?.pageSum ?? {})
            return res?.detailBodies ?? []
          } catch (error) {
            return []
          }
        },
      }),
    []
  )

  useEffect(() => {
    if (timeFrom && timeTo) {
      inflowTable.setParams({ estimatedOverdueRate: overdueRate, timeFrom, timeTo, page: 1 })
      inflowTable.search()
    }
  }, [timeFrom, timeTo])
  const handleReload = () => {
    inflowTable.setParams({ estimatedOverdueRate: overdueRate, timeFrom, timeTo })
    inflowTable.search()
    reload()
  }
  // 预计逾期率

  const [downloadLoading, setDownloadLoading] = useState(false)
  const download = async ({ key }) => {
    if (!key) return

    const needPage = key === 'download'
    const params = inflowTable.getParams()
    setDownloadLoading(true)
    try {
      return await Api.getInflowDownload({
        ...params,
        timeFrom,
        timeTo,
        estimatedOverdueRate: overdueRate,
        needPage,
      }).then((res) => {
        if (res.code && res.code !== 200) message.error(res.msg)
        setDownloadLoading(false)
      })
    } catch (error) {
      console.log('error: ', error)
      setDownloadLoading(false)
    }
  }
  const columns = useMemo(() => {
    const assetsColumns = [
      '项目名称',
      '合同编号',
      '合同总金额（万元）',
      '现金流入时间',
      '本金（万元）',
      '利息（万元）',
      '首期租金（万元）',
      '保证金（万元）',
      '服务费/咨询费/手续费/其他（万元）',
      '合计金额（万元）',
      '预计流入现金流合计（万元）',
    ]
    return getTableColumns(ALL_COLUMNS, assetsColumns, false)
  }, [])
  const summary = useSummer({ sumData, pageSum, columns })

  return (
    <div>
      <Table
        columnsFilter={'financial_liquidityRisk_InflowTable'}
        onFilter={(key,val) => saveServer('financial_liquidityRisk_InflowTable',val)}
        store={inflowTable}
        editable={false}
        autoRequest={false}
        scroll={{
          x: 1200,
        }}
        className={styles.tableBorder}
        columnWidth={140}
        actions={
          <p style={{ margin: 0 }}>
            <Tag style={{ margin: '0 6px' }}>
              {moment(timeFrom).format('YYYY-MM-DD')}~{moment(timeTo).format('YYYY-MM-DD')}
            </Tag>
            内现金流流入明细
          </p>
        }
        extra={[
          <div key="yuji">
            预计逾期率
            <FormAmount
              style={{ margin: '0 6px' }}
              min={0}
              max={100}
              value={overdueRate}
              initFormat={1}
              onChange={(value) => setOverdueRate(value)}
            />
            %
          </div>,
          <Button key="reload" onClick={handleReload}>
            刷新
          </Button>,
          <Button.Download
            key="downloadItems"
            onClick={download}
            loading={downloadLoading}
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
    </div>
  )
}

export default observer(Index)
