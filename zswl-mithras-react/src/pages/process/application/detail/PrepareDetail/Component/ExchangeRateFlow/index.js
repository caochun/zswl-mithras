import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import ExchangeRate from '@/pages/budget/exchangeRate/index'
import moment from 'moment'

const Index = ({ params }) => {
  const { id, detail = {} } = params
  const { applyTime } = detail
  const year = moment(applyTime).year()
  const month = moment(applyTime).month() + 1

  return <ExchangeRate params={{ id }} query={{ year, month }} />
}
export default observer(Index)
