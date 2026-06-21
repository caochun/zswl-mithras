import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import TimeSelect from './TimeSelect'
import LiquidityChart from './LiquidityChart'
import InflowTable from './InflowTable'
import ShortTermLoan from './ShortTermLoan'
import OutFlowTable from './OutFlowTable'
import StatisticsTable from './StatisticsTable'
import { SettingOutlined } from '@ant-design/icons'
import styles from './index.less'
import BaseModal from './BaseModal'
import Store from './store'
import { useMemo, useState } from 'react'
import moment from 'moment'

function FinancialLiquidityRisk({ path }) {
  const store = useMemo(() => new Store(), [])
  const { time, setTime } = store
  const newTime = useMemo(() => {
    const [timeFrom, timeTo] = time?.split('~')
    const date = {
      timeFrom: timeFrom && moment(timeFrom).format('YYYY-MM-DD 00:00:00'),
      timeTo: timeTo && moment(timeTo).format('YYYY-MM-DD 23:59:59'),
      days: TimeSelect.getTNum(time),
    }
    store.newTime = date
    return date
  }, [time])
  return (
    <Page>
      <div className={styles.flex}>
        <TimeSelect onChange={setTime} />
        <Button icon={<SettingOutlined />} type="text" size={'middle'} onClick={store.openModal}>
          数据设置
        </Button>
      </div>
      <LiquidityChart time={newTime} store={store} />
      <InflowTable time={newTime} store={store} />
      <OutFlowTable time={newTime} store={store} />
      <ShortTermLoan time={newTime} store={store} />
      <BaseModal store={store.baseModal} time={newTime} />
      <StatisticsTable time={newTime} store={store} />
    </Page>
  )
}

export default observer(FinancialLiquidityRisk)
