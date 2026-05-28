import { Button, Table, TableStore, SearchBar } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from './ColumnStat'
import { useEffect, useMemo, useState } from 'react'
import { Tag, message } from 'antd'
import Api from './api'
import EditModal from '../org/EditModal'
import styles from './index.less'
import { Checkbox } from 'antd'
import _ from 'lodash'
import { saveServer } from '@/utils'

function Index({ time = {}, store }) {
  const timeFrom = time.timeFrom && moment(time.timeFrom).format('YYYY-MM-DD')
  const timeTo = time.timeTo && moment(time.timeTo).format('YYYY-MM-DD')
  const [groupMap, setGroupMap] = useState({})
  const [lackBalance, setLackBalance] = useState(false)
  const [mismatchBalance, setMismatchBalance] = useState(false)

  const $table = useMemo(
    () =>
      new TableStore({
        request: async (searchData) => {
          try {
            if (!timeFrom || !timeTo) return
            const res = await Api.postCashInOutStat({
              ...searchData,
              dateFrom: timeFrom,
              dateTo: timeTo,
            })
            const flatRes = getFlatData(res)
            const fileds = ['financialChannel']
            fileds.map((field) => {
              calcGroupCell(flatRes, field)
            })

            return flatRes ?? []
          } catch (err) {
            return []
          }
        },
      }),
    [timeFrom, timeTo]
  )

  const columns = useMemo(() => {
    const nameColumns = [
      {
        title: '资金端',
        children: [
          {
            title: '融资渠道',
            width: 280,
            onCell: (record, index) => renderGroupCell('financialChannel', index),
          },
          {
            title: '融资编码',
          },
          {
            title: '融资总额（万元）',
          },
          {
            title: '现金流出时间',
          },
          {
            title: '现金流出总金额（万元）',
          },
          {
            title: '现金流出本金（万元）',
          },
          {
            title: '现金流出利息（万元）',
          },
        ],
      },
      {
        title: '流动性盈缺',
      },
      {
        title: '期限错配',
      },
      {
        title: '资产端',
        children: [
          '项目名称',
          '合同编号',
          '合同总金额（万元）',
          '现金流入时间',
          '现金流入总金额（万元）',
          '现金流入本金（万元）',
          '现金流入利息（万元）',
        ],
      },
    ]
    return getTableColumns(ALL_COLUMNS, nameColumns, false)
  }, [groupMap])

  const [downloadLoading, setDownloadLoading] = useState(false)
  const download = async () => {
    const params = $table.getParams()
    setDownloadLoading(true)

    return await Api.postCashInOutDownLoad({
      ...params,
      dateFrom: timeFrom,
      dateTo: timeTo,
    })
      .then((res) => {
        if (res.code && res.code !== 200) message.error(res.msg)
        setDownloadLoading(false)
      })
      .catch((err) => {
        setDownloadLoading(false)
      })
  }

  const getFlatData = (data) => {
    const allList = []
    data?.map((item) => {
      if (item?.inRecordList?.length === 0) {
        allList.push({
          ...item,
          id: JSON.stringify(item),
        })
      } else {
        item?.inRecordList?.map((i) => {
          allList.push({
            ...item,
            ...i,
            id: JSON.stringify({ ...item, ...i }),
          })
        })
      }
    })
    return allList
  }

  const calcGroupCell = (data, field) => {
    const grouped = _.groupBy(data, field)
    const groupLengths = _.mapValues(grouped, (group) => group.length)
    const groupLengthsArr = Object.values(groupLengths)
    const _groupMap = {}
    groupLengthsArr.reduce((prev, cur, index) => {
      _groupMap[prev] = groupLengthsArr[index]
      return prev + cur
    }, 0)
    // setGroupMap({ [field]: _groupMap })
    setGroupMap((prev) => {
      return {
        ...prev,
        [field]: _groupMap,
      }
    })
  }

  const renderGroupCell = (field, index) => {
    if (groupMap?.[field]?.[index]) {
      return { rowSpan: groupMap[field][index] }
    }
    return { rowSpan: 0 }
  }

  const handleChange = (e, name) => {
    const value = e.target.checked
    if (name === 'lackBalance') {
      setLackBalance(value)
    }
    if (name === 'mismatchBalance') {
      setMismatchBalance(value)
    }
  }

  useEffect(() => {
    $table.search({ lackBalance: lackBalance || '', mismatchBalance: mismatchBalance || '' })
  }, [lackBalance, mismatchBalance])

  return (
    <div style={{ margin: '12px 0' }}>
      <p>
        <Tag style={{ margin: '0 6px' }}>
          {moment(timeFrom).format('YYYY-MM-DD')}~{moment(timeTo).format('YYYY-MM-DD')}
        </Tag>
        流动性统计
      </p>
      <Table
        columnsFilter={'financial_liquidityRisk_StatisticsTable'}
        onFilter={(key,val) => saveServer('financial_liquidityRisk_StatisticsTable',val)}
        bordered
        pagination={false}
        columnWidth={190}
        store={$table}
        editable={false}
        scroll={{
          x: 1600,
          y: 500,
        }}
        className={styles.tableBorder}
        actions={[
          <Checkbox onChange={(e) => handleChange(e, 'lackBalance')} checked={lackBalance}>
            流动性盈缺
          </Checkbox>,
          <Checkbox onChange={(e) => handleChange(e, 'mismatchBalance')} checked={mismatchBalance}>
            期限错配
          </Checkbox>,
        ]}
        extra={[
          <Button key="download" loading={downloadLoading} onClick={download} type="primary">
            下载
          </Button>,
        ]}
        columns={columns}
      />
      <EditModal store={store} />
    </div>
  )
}

export default observer(Index)
