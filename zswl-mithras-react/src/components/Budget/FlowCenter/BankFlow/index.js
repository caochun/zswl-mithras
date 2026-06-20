import { Badge, Radio } from 'antd'
import BusinessTable from './BusinessTable'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import BaoRon from './BaoRon'
import guaranteedFinancialFlow from '@/api/budget/flowCenter/guaranteedFinancialFlow'
import styles from './index.less'

const Index = () => {
  const [type, setType] = useState('PROCESSING_CENTER_WRITE')
  const [count, setCount] = useState(0)
  const getCount = async () => {
    const { notIgnoredCount } = await guaranteedFinancialFlow.postRecordCount({})
    setCount(notIgnoredCount)
  }
  useEffect(() => {
    getCount()
  }, [])

  const TYPE_OPTIONS = [
    { label: '收款核销', value: 'PROCESSING_CENTER_WRITE' },
    { label: '处理中心', value: 'PROCESSING_CENTER' },
    { label: '项目端', value: 'PROCESSED_PROJ_SIDE' },
    { label: '资金端', value: 'PROCESSED_FUNDS_END' },
    { label: '无需处理', value: 'NO_PROCESSING_REQUIRE' },
    {
      label: (
        <Badge count={count} offset={[10, -5]}>
          保融流水
        </Badge>
      ),
      value: 'BAO_RON',
    },
  ]

  return (
    <>
      <Radio.Group
        options={TYPE_OPTIONS}
        onChange={(e) => setType(e.target.value)}
        optionType="button"
        buttonStyle="solid"
        value={type}
        className={styles.badge}
        style={{ marginBottom: 8 }}
      />
      {type === 'BAO_RON' ? <BaoRon getCount={getCount} /> : <BusinessTable type={type} />}
    </>
  )
}

export default observer(Index)
