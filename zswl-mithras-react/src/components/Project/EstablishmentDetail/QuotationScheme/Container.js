import React, { useEffect, useMemo, useState } from 'react'
import { Descriptions, Input, Button, InputNumber, Space, Col, Row, message } from 'antd'
import { Form } from '@zswl/components'
import { observer, toJS } from '@zswl/admin'
import styles from './index.less'
import Store from './store'
import Lease from './FormConfig/Lease'
import mathjs from '@/utils/math'

import Factoring from './FormConfig/Factoring'
import AssignmentOfClaims from './FormConfig/AssignmentOfClaims'
import { formatNull, hasValue, numToFixed } from '@/utils'

const modifyMap = {
  ZL: 'leasePriceModifyREQ',
  BL: 'factoringPriceModifyREQ',
  ZZ: 'leasePriceModifyREQ',
  ZR: 'aocPriceModifyREQ',
}
const QuotationScheme = ({
  projectId,
  type,
  title,
  showValue,
  setShowValue,
  compareData,
  canEdit,
  isProjSponsor,
  isNormal,
  store,
  rootStore,
}) => {
  const [form] = Form.useForm()
  const { projectQSDetail, postProjectQSModify, getProjectQSDetail } = store
  const [detailShowData, setDetailShowData] = useState({})
  const CurrentDom = useMemo(() => {
    if (!type) {
      return null
    }
    const Dom = {
      ZL: (
        <Lease
          showValue={showValue}
          form={form}
          detail={detailShowData}
          noTitle={!!title}
          isNormal={isNormal}
          compareChangeList={compareData?.qs?.ZL}
        />
      ),
      BL: (
        <Factoring
          showValue={showValue}
          form={form}
          detail={detailShowData}
          noTitle={!!title}
          compareChangeList={compareData?.qs?.BL}
        />
      ),
      ZZ: (
        <Lease
          showValue={showValue}
          form={form}
          detail={detailShowData}
          noTitle={!!title}
          compareChangeList={compareData?.qs?.ZZ}
        />
      ),
      ZR: (
        <AssignmentOfClaims
          showValue={showValue}
          form={form}
          detail={detailShowData}
          noTitle={!!title}
          compareChangeList={compareData?.qs?.ZR}
        />
      ),
    }
    return Dom[type]
  }, [type, showValue, form, detailShowData, compareData])

  useEffect(() => {
    if (projectQSDetail) {
      const currentDetailData = {}
      Object.keys(projectQSDetail).forEach((item) => {
        currentDetailData[item] = formatNull(projectQSDetail[item])
      })
      const formatPercent = (val) => {
        return hasValue(val)
          ? mathjs.toNonExponentialPlus(mathjs.format(mathjs.divide(val, 10000)))
          : undefined
      }
      const value = {
        ...currentDetailData,
        leaseRatePercent: numToFixed(formatPercent(currentDetailData.leaseRatePercent)),
        irrPercent: numToFixed(formatPercent(currentDetailData.irrPercent)),
        factoringRatePercent: numToFixed(formatPercent(currentDetailData.factoringRatePercent)),
        aocRatePercent: numToFixed(formatPercent(currentDetailData.aocRatePercent)),
        aocFinancingProportion: formatPercent(currentDetailData.aocFinancingProportion),
        factoringFinancingProportion: formatPercent(currentDetailData.factoringFinancingProportion),
        applyCreditAmount: formatPercent(currentDetailData.applyCreditAmount),
        earnestMoney: formatPercent(currentDetailData.earnestMoney),
        downPayment: formatPercent(currentDetailData.downPayment),
        consultingFee: formatPercent(currentDetailData.consultingFee),
        nominalPrice: formatPercent(currentDetailData.nominalPrice),
        commission: formatPercent(currentDetailData.commission),
        firstInstallmentInterest: formatPercent(currentDetailData.firstInstallmentInterest),
      }
      form.setFieldsValue(value)
      setDetailShowData(value)
    }
  }, [projectQSDetail, form])

  const onSave = (values) => {
    if (!!title && !showValue) {
      message.info('报价方案未保存，请先保存')
      return
    }
    const formatNum = (key) => {
      if (hasValue(values[key])) {
        const v = values[key].toString().replace(/,/g, '')
        return mathjs.toNonExponentialPlus(mathjs.format(mathjs.multiply(v, 10000)))
      }

      return values[key]
    }

    const params = {
      [modifyMap[type]]: {
        ...values,
        projEstablishId: projectId,
        rateType:
          (hasValue(values.leaseRatePercent) ||
            hasValue(values.factoringRatePercent) ||
            hasValue(values.aocRatePercent)) &&
          values.rateType
            ? values.rateType
            : null,
        leaseRatePercent:
          hasValue(values.leaseRatePercent) && values.rateType
            ? formatNum('leaseRatePercent')
            : null,
        irrPercent: formatNum('irrPercent'),
        factoringRatePercent:
          hasValue(values.factoringRatePercent) && values.rateType
            ? formatNum('factoringRatePercent')
            : null,
        factoringFinancingProportion: formatNum('factoringFinancingProportion'),
        aocRatePercent:
          hasValue(values.aocRatePercent) && values.rateType ? formatNum('aocRatePercent') : null,
        aocFinancingProportion: formatNum('aocFinancingProportion'),
        applyCreditAmount: formatNum('applyCreditAmount'),
        earnestMoney: formatNum('earnestMoney'),
        downPayment: formatNum('downPayment'),
        consultingFee: formatNum('consultingFee'),
        nominalPrice: formatNum('nominalPrice'),
        commission: formatNum('commission'),
        firstInstallmentInterest: formatNum('firstInstallmentInterest'),
        id: detailShowData.id,
      },
    }
    postProjectQSModify(params, () => {
      getProjectQSDetail(projectId)
      setShowValue(true)
    })
  }

  return (
    <div className={styles.wrap}>
      {title}
      <Form
        form={form}
        onFinish={onSave}
        initialValues={{
          interestWay: 'ACTUAL_RATE',
        }}
      >
        {canEdit && (
          <div className={styles.editWrap}>
            {showValue ? (
              <Button
                type="primary"
                disabled={!isProjSponsor}
                onClick={() => {
                  setShowValue(false)
                }}
              >
                编辑
              </Button>
            ) : (
              <>
                <Button
                  style={{ marginRight: 8 }}
                  onClick={() => {
                    form.setFieldsValue(detailShowData)
                    setShowValue(true)
                  }}
                >
                  取消
                </Button>
                <Button type="primary" htmlType="submit">
                  保存
                </Button>
              </>
            )}
          </div>
        )}

        {CurrentDom}
      </Form>
    </div>
  )
}

export default observer(QuotationScheme)
