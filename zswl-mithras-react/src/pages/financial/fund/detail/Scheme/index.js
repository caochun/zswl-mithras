import { observer } from '@zswl/admin'
import {
  useEffect,
  useState,
  forwardRef,
  useImperativeHandle,
  useRef,
  useMemo,
  useCallback,
} from 'react'
import Api from '@/api/financial/fundApi'
import { compareDetail, getDescColumns, hasValue } from '@/utils'
import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from './Column'
import { create, all } from 'mathjs'
import { message } from 'antd'

const mathjs = create(all, {
  number: 'BigNumber',
  precision: 20,
})

function Index(
  {
    financingId,
    isFormApproval,
    businessVersion,
    canEdit = true,
    initEdit,
    detail,
    isLog,
    baseStore,
    isOtherChange,
  },
  ref
) {
  const baseInfoDetail = baseStore.page.getData()
  const baseInfoData = isFormApproval ? baseInfoDetail.newDetail : baseInfoDetail.detail

  const editRef = useRef()
  const [detailData, setDetailData] = useState(null)
  const [showInterestRateType, setShowInterestRateType] = useState(true)

  const handleCalc = useCallback(() => {
    const editForm = editRef.current.form
    const { setFieldValue, getFieldsValue } = editForm

    // 借款年利率+（保理手续费+开户许可证费+其他费用）/融资金额/融资期限*12
    const {
      lprRatePercent,
      lprAddPercent,
      serviceChargeAmount,
      licenseAmount,
      otherAmount,
      financingAmount,
      financingMonth,
    } = getFieldsValue(true) || {}

    if (
      hasValue(lprRatePercent) &&
      hasValue(lprAddPercent) &&
      hasValue(financingAmount) &&
      hasValue(financingMonth)
    ) {
      try {
        const result1 = mathjs
          .chain(serviceChargeAmount || 0)
          .add(licenseAmount || 0)
          .add(otherAmount || 0)
          .done()

        const result2 = mathjs
          .chain(result1)
          .divide(financingAmount || 0)
          .divide(financingMonth || 0)
          .multiply(12)
          .done()

        const result = mathjs
          .chain((lprRatePercent || 0) / 10000)
          .add((lprAddPercent || 0) / 10000)
          .add(result2)
          .done()

        setFieldValue('comprehensiveInterestRate', Number(result).toFixed(2))
      } catch (error) {
        // 错误处理逻辑
        console.log(error)
      }
    } else {
      message.info('请先填写借款年利率、融资金额、融资期限')
    }
  }, [])

  const columns = useMemo(() => {
    return getDescColumns(
      ALL_COLUMNS({ isLog, handleCalc, showInterestRateType, setShowInterestRateType }),
      ALL_COLUMNS({
        isLog,
        handleCalc,
        showInterestRateType,
        setShowInterestRateType,
        baseInfoDetail: baseInfoData,
        isOtherChange,
      })
    )
  }, [isLog, handleCalc, showInterestRateType, setShowInterestRateType, baseInfoData])

  useEffect(() => {
    if (detailData) {
      setShowInterestRateType(detailData.detail?.interestRateType === 'FLOAT')
    }
  }, [detailData])

  useImperativeHandle(ref, () => ({
    reload: async () => {
      await getData(financingId)
    },
  }))

  const getData = async (id) => {
    if (!isFormApproval) {
      const res = await Api.postPlanDetail({ financingId: id })
      setDetailData({ detail: res })
    } else {
      const res = await Api.postPlanDetailCompare({ financingId: id, businessVersion })
      setDetailData(compareDetail(res))
    }
  }

  useEffect(() => {
    financingId && getData(financingId)
  }, [financingId])

  const saveData = async (data) => {
    const id = detail?.id ?? detailData?.detail?.id ?? detailData?.newDetail?.id
    const guaranteeInfoList = data?.guaranteeInfoList ?? []
    const orgList = guaranteeInfoList.map(
      ({ guaranteeAgencyId, organizationId }) => `${guaranteeAgencyId}-${organizationId}`
    )
    const hasRepeat = [...new Set(orgList)].length !== orgList.length
    if (hasRepeat) {
      message.error('担保机构不能重复添加')
      return Promise.reject()
    }

    await Api.postPlanModify({ ...data, id })
    await getData(financingId)
    baseStore?.page?.init()
  }

  if (!detailData && !detail) {
    return null
  }

  return !isFormApproval ? (
    <EditDescription
      title="融资方案"
      detail={detail ?? detailData.detail}
      saveData={saveData}
      canEdit={canEdit}
      isLog={isLog}
      initEdit={initEdit}
      columns={columns}
      ref={editRef}
    />
  ) : (
    <EditDescription
      title="融资方案"
      detail={detail ?? detailData.newDetail}
      isLog={isLog ?? detailData.isLog}
      saveData={saveData}
      canEdit={canEdit}
      initEdit={initEdit}
      columns={columns}
      ref={editRef}
    />
  )
}

export default observer(forwardRef(Index))
