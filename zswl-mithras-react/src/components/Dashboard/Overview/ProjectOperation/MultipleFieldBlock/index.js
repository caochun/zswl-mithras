import { AmountFormat } from '@/components/Format'
import { observer } from '@zswl/admin'
import FieldBlock from '../FieldBlock'
import styles from './index.less'
import { hasValue } from '@/utils'

const Value = ({ data, initFormat = 1, unit = '' }) => {
  const value = data?.value ?? data
  return hasValue(value) ? (
    <AmountFormat value={value} initFormat={initFormat} unit={unit}></AmountFormat>
  ) : (
    '-'
  )
}

const getUnit = (data) => {
  return data?.unit ? `(${data?.unit})` : ''
}
const Index = ({ data = {} }) => {
  return (
    <>
      <div className={styles.row}>
        <FieldBlock
          value={<Value data={data.average}></Value>}
          title={`历史平均 ${getUnit(data.average)}`}
        />
        <FieldBlock
          value={<Value data={data.averageThisMonth ?? ''}></Value>}
          title={`本月平均 ${getUnit(data.averageThisMonth)}`}
        />
      </div>
      <div className={styles.row}>
        <FieldBlock
          value={<Value data={data.chainRatio} unit={data.chainRatio?.unit}></Value>}
          pureValue={data.chainRatio?.value}
          title={'环比(上月)'}
        />
        <FieldBlock
          value={<Value data={data.yearOnYearBasis} unit={data.yearOnYearBasis?.unit}></Value>}
          pureValue={data.yearOnYearBasis?.value}
          title={'同比(上年)'}
        />
      </div>
      {data.stageName !== '访客' && (
        <div className={styles.row}>
          <FieldBlock value={<Value data={data.quantity}></Value>} title={'数量'} />
          <FieldBlock
            value={<Value data={data.financeAmount}></Value>}
            title={`融资额 ${getUnit(data.financeAmount)}`}
          />
        </div>
      )}
    </>
  )
}

export default observer(Index)
