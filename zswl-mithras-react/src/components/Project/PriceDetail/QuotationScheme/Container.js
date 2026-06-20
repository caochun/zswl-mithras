import React, { forwardRef, useEffect, useImperativeHandle, useMemo, useState } from 'react'
import { Button, message } from 'antd'
import { Form } from '@zswl/components'
import { observer, toJS, getQuery } from '@zswl/admin'
import styles from './index.less'
import Lease from './FormConfig/Lease'
import Store from './store'
import Factoring from './FormConfig/Factoring'
import AssignmentOfClaims from './FormConfig/AssignmentOfClaims'
import { formatNull, hasValue, numToFixed } from '@/utils'
import moment from 'moment'
import mathjs from '@/utils/math'

const modifyMap = {
  ZL: 'leasePriceModifyREQ',
  BL: 'factoringPriceModifyREQ',
  ZZ: 'leasePriceModifyREQ',
  ZR: 'aocPriceModifyREQ',
}
const QuotationScheme = (
  {
    type,
    title,
    showValue,
    setShowValue,
    compareData = {},
    canEdit = true,
    isProjSponsor,
    rootStore,
    store,
  },
  ref
) => {
  const [form] = Form.useForm()
  const QSShowValue = rootStore?.QSShowValue
  const projectId = rootStore?.page?.getParams().id
  const { projectQSDetail, postProjectQSModify, getProjectQSDetail } = store
  const [detailShowData, setDetailShowData] = useState({})
  const isFormApproval = getQuery('typeId') == 'approval'
  const { modelKey } = rootStore?.page.getParams()
  const isCreate = modelKey === 'ProjReviewPricingApprovalFlow'
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
          id={projectId}
          isLog={projectQSDetail?.isLog}
        />
      ),
      BL: (
        <Factoring
          showValue={showValue}
          form={form}
          detail={detailShowData}
          isLog={projectQSDetail?.isLog}
          noTitle={!!title}
          id={projectId}
        />
      ),
      ZZ: (
        <Lease
          showValue={showValue}
          form={form}
          detail={detailShowData}
          noTitle={!!title}
          id={projectId}
          isLog={projectQSDetail?.isLog}
        />
      ),
      ZR: (
        <AssignmentOfClaims
          showValue={showValue}
          form={form}
          detail={detailShowData}
          id={projectId}
          isLog={projectQSDetail?.isLog}
          noTitle={!!title}
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
        // 流程中的使用 projectApprovalAmount
        approvedAmount:
          isFormApproval && isCreate
            ? formatPercent(currentDetailData.projectApprovalAmount)
            : formatPercent(currentDetailData.approvedAmount),
        // projectApprovalAmount: formatPercent(currentDetailData.projectApprovalAmount),
        earnestMoney: formatPercent(currentDetailData.earnestMoney),
        downPayment: formatPercent(currentDetailData.downPayment),
        consultingFee: formatPercent(currentDetailData.consultingFee),
        nominalPrice: formatPercent(currentDetailData.nominalPrice),
        commission: formatPercent(currentDetailData.commission),
        firstInstallmentInterest: formatPercent(currentDetailData.firstInstallmentInterest),
        plannedStartingDate: currentDetailData.plannedStartingDate
          ? moment(currentDetailData.plannedStartingDate)
          : undefined,
      }
      form.setFieldsValue(value)
      setDetailShowData(value)
    }
  }, [projectQSDetail, form])

  useImperativeHandle(ref, () => ({
    ...form,
  }))
  const onSave = async (isChange = false) => {
    const values = await form.validateFields()
    if (!isChange && !!title && !QSShowValue) {
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
        projectId,
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
        approvedAmount: formatNum('approvedAmount'),
        // projectApprovalAmount: formatNum('projectApprovalAmount'),
        earnestMoney: formatNum('earnestMoney'),
        downPayment: formatNum('downPayment'),
        consultingFee: formatNum('consultingFee'),
        nominalPrice: formatNum('nominalPrice'),
        commission: formatNum('commission'),
        firstInstallmentInterest: formatNum('firstInstallmentInterest'),
        plannedStartingDate: moment(values.plannedStartingDate).format('yyyy-MM-DD'),
        id: detailShowData.id,
      },
    }
    return postProjectQSModify(params, () => {
      if (!isChange) {
        getProjectQSDetail(projectId)
        setShowValue(true)
      }
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
                <Button type="primary" onClick={() => onSave(false)}>
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

export default observer(forwardRef(QuotationScheme))
