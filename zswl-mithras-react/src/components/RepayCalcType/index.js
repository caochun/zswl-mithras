import { Form, Select, App } from '@zswl/components'
import styles from './index.less'

const { Item } = Form

const Index = ({ fieldName }) => {
  return (
    <div className={styles.rowWrap}>
      <Item noStyle dependencies={['interestWay']}>
        {({ getFieldValue }) => {
          const IS_FLAT_RATE = getFieldValue('interestWay') === 'FLAT_RATE'
          const optionsType = App.getData().optionsType
          const rentalCalcTypeEnum = IS_FLAT_RATE
            ? optionsType.repayCalcType.filter((item) => item.label !== '等额本金')
            : optionsType.repayCalcType
          return (
            <Item name={fieldName} rules={[{ required: true, message: '请选择!' }]}>
              <Select options={rentalCalcTypeEnum} placeholder="请选择"></Select>
            </Item>
          )
        }}
      </Item>
    </div>
  )
}

export default Index
