import { Button, Page, Table, TableStore } from '@zswl/components'
import { observer, history } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { useCallback, useMemo, useState } from 'react'
import { Radio, Tag, Typography, message } from 'antd'
import Api from '@/api/financial/liquidity/outflowOfFunds'
import EditModal from '../org/EditModal'
import styles from './index.less'
import useSummer from './useSummer'
import { saveServer } from '@/utils'

const { Text } = Typography

const options = [
  { label: '资金端', value: '1' },
  { label: '资产端', value: '2' },
]
function Index({ time = {}, store }) {
  const [port, setPort] = useState('1')
  const portChange = (e) => {
    setPort(e.target.value)
  }
  const [sumData, setSumData] = useState({})
  const [pageSum, setPageSum] = useState({})
  const { days, timeFrom, timeTo } = time
  const outflowTable = useMemo(
    () =>
      new TableStore({
        request: async (searchData) => {
          const api = port === '2' ? Api.postAssetsOutflowList : Api.postFundsOutflowList
          try {
            const res = await api({ ...searchData, timeFrom, timeTo })
            setSumData(res?.sum || {})
            setPageSum(res?.pageSum || {})
            return res?.records ?? []
          } catch (err) {
            console.log(err)
            return []
          }
        },
      }),
    [port, time]
  )
  const columns = useMemo(() => {
    const nameColumns = [
      '融资渠道',
      '融资编码',
      '融资总额（万元）',
      '现金流出时间',
      { title: '本金（万元）', dataIndex: 'principle' },
      '利息（万元）',
      { title: '预计流出现金流合计（万元）', dataIndex: 'estimateCashOutflowAmount' },
    ]
    const assetsColumns = [
      '项目名称',
      '合同编号',
      { title: '合同总金额（万元）', dataIndex: 'contractAmount' },
      '现金流出时间',
      '保证金（万元）',
      '预计流出现金流合计（万元）',
    ]
    const newColumns = port === '1' ? nameColumns : assetsColumns
    return getTableColumns(ALL_COLUMNS, newColumns, false)
  }, [port])
  const summary = useSummer({ pageSum, sumData, columns })

  const [downloadLoading, setDownloadLoading] = useState(false)
  const download = async ({ key }) => {
    if (!key) return

    const needPage = key === 'download'
    const params = outflowTable.getParams()
    setDownloadLoading(true)

    return await Api.postOutflowExport({
      ...params,
      timeFrom,
      timeTo,
      needPage,
    })
      .then((res) => {
        if (res.code && res.code !== 200) message.error(res.msg)
        setDownloadLoading(false)
      })
      .catch((err) => {
        setDownloadLoading(false)
      })
  }
  return (
    <div style={{ margin: '12px 0' }}>
      <p>
        <Tag style={{ margin: '0 6px' }}>
          {moment(timeFrom).format('YYYY-MM-DD')}~{moment(timeTo).format('YYYY-MM-DD')}
        </Tag>
        内现金流流出明细
      </p>
      <Table
        columnsFilter={'financial_liquidityRisk_OutFlowTable'}
        onFilter={(key,val) => saveServer('financial_liquidityRisk_OutFlowTable',val)}
        store={outflowTable}
        editable={false}
        scroll={{
          x: 1400,
        }}
        columnWidth={120}
        className={styles.tableBorder}
        actions={[
          <Radio.Group
            options={options}
            onChange={portChange}
            value={port}
            optionType="button"
            buttonStyle="outline"
            key="port"
          />,
        ]}
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
