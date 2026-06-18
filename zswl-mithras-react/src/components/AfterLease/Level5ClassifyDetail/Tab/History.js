import { observer } from '@zswl/admin'
import { Table, App } from '@zswl/components'
import { useMemo } from 'react'
import { Badge, Space } from 'antd'
import { AmountFormat } from '@/components/Format'
import { hasValue, getKeyOptionsLabelMapPlus } from '@/utils'
import { levelColor } from '../../Level5ClassifyConfig'
import styles from '../index.less'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  const { optionsType } = App.getData()

  const getStatusColor = (value) => {
    const index = optionsType.assetClassifyResultEnum.findIndex((item) => item.value === value)
    return levelColor[index]
  }
  const columns = useMemo(() => {
    const baseColumns = [
      {
        title: '分类时间',
        dataIndex: 'createTime',
      },
      {
        title: '存量风险敞口(元)',
        dataIndex: 'stockRiskExposure',
        align: 'right',
        render: (val) => {
          return hasValue(val) ? <AmountFormat value={val}></AmountFormat> : '-'
        },
      },
      {
        title: '逾期金额(元)',
        dataIndex: 'overdueAmount',
        align: 'right',
        render: (val) => {
          return hasValue(val) ? <AmountFormat value={val}></AmountFormat> : '-'
        },
      },
      {
        title: '逾期天数',
        dataIndex: 'overdueDays',
        render: (val) => {
          return val || '-'
        },
      },
      {
        title: '定性调整',
        dataIndex: 'qualitativeAdjust',
        render: (val) => {
          return ['否', '是'][val] || '-'
        },
      },
      {
        title: '分类结果',
        dataIndex: 'classifyResult',
        render: (val) => {
          return hasValue(val) ? (
            <Space>
              <Badge color={getStatusColor(val)} text={''} />
              {getKeyOptionsLabelMapPlus('assetClassifyResultEnum')[val]}
            </Space>
          ) : (
            '-'
          )
        },
      },
    ]

    return baseColumns
  }, [])
  return (
    <div className={styles.wrap}>
      <Table onFilter={(key,val) => saveServer('detail_Tab_History',val)} columnsFilter={'detail_Tab_History'} resizable scroll={{ x: 1200 }} store={store?.historyTable} columns={columns} />
    </div>
  )
}

export default observer(Index)
