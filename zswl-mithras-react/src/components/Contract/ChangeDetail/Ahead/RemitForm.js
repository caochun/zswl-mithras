import { Row, Col, Space, Input, Tooltip } from 'antd'
import { observer } from '@zswl/admin'
import FormAmount from '@/components/Form/FormAmount'
import { Form, Select, App } from '@zswl/components'
import IconFont from '@/components/Icon'
import { rules, hasValue } from '@/utils'
import mathjs from '@/utils/math'
import styles from './index.less'
import { useEffect } from 'react'

const { Item } = Form

export const LossItem = observer(({ store, data }) => {
  const form = Form.useFormInstance()
  const { isEarlySettle, calcTipArr } = store

  useEffect(() => {
    const { loss, lossDeratePercent, lossDerateType, applyDerateAmount } = data
    form.setFieldsValue({
      loss,
      lossDeratePercent,
      lossDerateType,
      applyDerateAmount,
    })
  }, [form, data])

  return (
    <Space direction="vertical" className={styles.itemRow}>
      <FormAmount.Item
        style={{ width: 320 }}
        label={'提前终止补偿金'}
        readOnly
        disabled
        name="loss"
        min={-100000000}
        addonAfter={
          <Tooltip title={`点击获取最新值：${calcTipArr[isEarlySettle]}`}>
            <IconFont
              type="icon-icon_calculator"
              className={styles.calcIcon}
              onClick={() => store.calcLoss()}
            />
          </Tooltip>
        }
      ></FormAmount.Item>
      <Item noStyle dependencies={['loss', 'lossDeratePercent', 'lossDerateType']}>
        {({ getFieldsValue, setFieldValue }) => {
          const { lossDerateType, lossDeratePercent, loss } = getFieldsValue(true)
          if (!hasValue(loss)) {
            return null
          }
          const mapPenaltyValue = {
            NONE: 0,
            ALL: loss,
            PERCENT: mathjs.format(
              mathjs
                .chain(loss || 0)
                .multiply(lossDeratePercent || 0)
                .divide(100 * 10000)
                .done()
            ),
          }

          const curLossDerateAmount = mapPenaltyValue[lossDerateType]

          if (lossDerateType !== 'FIXED') {
            setFieldValue(
              'applyDerateAmount',
              hasValue(curLossDerateAmount) ? Math.abs(curLossDerateAmount) : curLossDerateAmount
            )
          }

          return (
            <Space>
              <Item label="减免方式" name="lossDerateType" rules={[rules.required('请选择')]}>
                <Select options="derateTypeEnum" style={{ width: 140 }}></Select>
              </Item>
              {lossDerateType === 'PERCENT' && (
                <FormAmount.Item
                  label={'减免比例'}
                  name="lossDeratePercent"
                  placeholder="减免比例"
                  addonAfter="%"
                ></FormAmount.Item>
              )}
              <Item noStyle dependencies={['lossDerateType', 'lossDeratePercent']}>
                {({ getFieldValue }) => {
                  const lossDerateType = getFieldValue('lossDerateType')
                  if (lossDerateType) {
                    return (
                      <FormAmount.Item
                        label={'减免金额'}
                        name="applyDerateAmount"
                        disabled={lossDerateType !== 'FIXED'}
                      ></FormAmount.Item>
                    )
                  }
                }}
              </Item>
            </Space>
          )
        }}
      </Item>
    </Space>
  )
})

export const PenaltyItem = observer(({ store, data }) => {
  const form = Form.useFormInstance()

  useEffect(() => {
    const { penalty, penaltyDeratePercent, penaltyDerateType, penaltyDerateAmount } = data
    form.setFieldsValue({
      penalty,
      penaltyDeratePercent,
      penaltyDerateType,
      penaltyDerateAmount,
    })
  }, [form, data])

  return (
    <Space className={styles.itemRow} direction="vertical">
      <Item noStyle>
        <FormAmount.Item
          label={'违约金'}
          name="penalty"
          readOnly
          disabled
          min={-100000000}
        ></FormAmount.Item>
      </Item>
      <Item noStyle dependencies={['penaltyDerateType', 'penaltyDeratePercent']}>
        {({ getFieldsValue, setFieldValue }) => {
          const { penaltyDerateType, penalty, penaltyDeratePercent } = getFieldsValue(true)
          const mapLossValue = {
            NONE: 0,
            ALL: penalty,
            PERCENT: mathjs.format(
              mathjs
                .chain(penalty || 0)
                .multiply(penaltyDeratePercent || 0)
                .divide(100 * 10000)
                .done()
            ),
            FIXED: undefined,
          }
          const curPenaltyDerateAmount = mapLossValue[penaltyDerateType]

          if (penaltyDerateType !== 'FIXED') {
            setFieldValue(
              'penaltyDerateAmount',
              hasValue(curPenaltyDerateAmount)
                ? Math.abs(curPenaltyDerateAmount)
                : curPenaltyDerateAmount
            )
          }
          if (penalty === 0) {
            setFieldValue('penaltyDerateType', 'NONE')
          }
          return (
            <Space>
              <Item label="减免方式" name="penaltyDerateType" rules={[rules.required('请选择')]}>
                <Select
                  options="derateTypeEnum"
                  style={{ width: 130 }}
                  disabled={penalty === 0}
                ></Select>
              </Item>
              {penaltyDerateType === 'PERCENT' && (
                <FormAmount.Item
                  label={'减免比例'}
                  name="penaltyDeratePercent"
                  addonAfter="%"
                  style={{ width: 220 }}
                ></FormAmount.Item>
              )}
              <Item noStyle dependencies={['penaltyDerateType', 'penaltyDeratePercent']}>
                {({ getFieldValue }) => {
                  const penaltyDerateType = getFieldValue('penaltyDerateType')
                  if (penaltyDerateType) {
                    return (
                      <FormAmount.Item
                        label={'减免金额'}
                        name="penaltyDerateAmount"
                        disabled={penaltyDerateType !== 'FIXED'}
                        style={{ width: 200 }}
                      ></FormAmount.Item>
                    )
                  }
                }}
              </Item>
            </Space>
          )
        }}
      </Item>
    </Space>
  )
})

export const EarnestMoneyItem = ({ data }) => {
  const form = Form.useFormInstance()

  useEffect(() => {
    const { isEarnestMoneyDeduction, earnestMoneyDeductionAmount, earnestMoneyBalance } = data
    form.setFieldsValue({
      isEarnestMoneyDeduction: earnestMoneyBalance === 0 ? 0 : isEarnestMoneyDeduction,
      earnestMoneyDeductionAmount,
    })
  }, [form, data])

  return (
    <Space className={styles.itemRow}>
      <Item label="是否抵扣" name="isEarnestMoneyDeduction" rules={[rules.required('请选择')]}>
        <Select
          options="yesOrNo"
          placeholder="是否抵扣"
          // disabled={data.earnestMoneyDeductionAmount === 0}
        ></Select>
      </Item>
      <Item noStyle dependencies={['isEarnestMoneyDeduction']}>
        {({ getFieldValue, setFieldValue }) => {
          const isEarnestMoneyDeduction = getFieldValue('isEarnestMoneyDeduction')
          if (isEarnestMoneyDeduction === 0) {
            setFieldValue('earnestMoneyDeductionAmount', 0)
          }
          return (
            <FormAmount.Item
              label={'抵扣金额'}
              name="earnestMoneyDeductionAmount"
              disabled={isEarnestMoneyDeduction === 0}
            ></FormAmount.Item>
          )
        }}
      </Item>
    </Space>
  )
}

export const LossRender = ({ data, type }) => {
  const { lossDerateType, loss, lossDeratePercent, applyDerateAmount } = data
  let diffLossMap = {
    NONE: loss,
    ALL: loss - applyDerateAmount,
    PERCENT: loss - applyDerateAmount,
    FIXED: loss - applyDerateAmount,
  }[lossDerateType]

  return (
    <Space>
      <div>{App.matchOption('derateTypeEnum', lossDerateType)?.label}</div>
      <div>
        <FormAmount.Format value={diffLossMap}></FormAmount.Format>
      </div>
      <div>
        <div>{lossDerateType === 'NONE' && null}</div>
        <div>
          {['FIXED', 'PERCENT', 'ALL'].includes(lossDerateType) && (
            <span>
              <span>
                （减免前金额：
                <FormAmount.Format value={loss}></FormAmount.Format>，
              </span>
              {lossDerateType === 'PERCENT' && (
                <span>
                  减免比例：
                  <FormAmount.Format value={lossDeratePercent} suffix="%"></FormAmount.Format> ，
                </span>
              )}
              <span>
                已减免金额：
                <FormAmount.Format value={applyDerateAmount}></FormAmount.Format>）
              </span>
            </span>
          )}
        </div>
      </div>
    </Space>
  )
}

export const PenaltyRender = ({ data }) => {
  const { penaltyDerateType, penalty, penaltyDeratePercent, penaltyDerateAmount } = data
  let diffPenaltyMap = {
    NONE: penalty,
    ALL: penalty - penaltyDerateAmount,
    PERCENT: penalty - penaltyDerateAmount,
    FIXED: penalty - penaltyDerateAmount,
  }[penaltyDerateType]

  return (
    <Space>
      <div>{App.matchOption('derateTypeEnum', penaltyDerateType)?.label}</div>
      <div>
        <FormAmount.Format value={diffPenaltyMap}></FormAmount.Format>
      </div>
      <div>
        <div>{penaltyDerateType === 'NONE' && null}</div>
        <div>
          {['FIXED', 'PERCENT', 'ALL'].includes(penaltyDerateType) && (
            <span>
              <span>
                （减免前金额：
                <FormAmount.Format value={penalty}></FormAmount.Format>，
              </span>
              {penaltyDerateType === 'PERCENT' && (
                <span>
                  减免比例：
                  <FormAmount.Format value={penaltyDeratePercent} suffix="%"></FormAmount.Format> ，
                </span>
              )}
              <span>
                已减免金额：
                <FormAmount.Format value={penaltyDerateAmount}></FormAmount.Format>）
              </span>
            </span>
          )}
        </div>
      </div>
    </Space>
  )
}
