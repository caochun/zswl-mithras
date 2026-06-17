import { useEffect, forwardRef, useState, useImperativeHandle, useMemo } from 'react'
import { observer } from '@zswl/admin'
import Lease from '@/components/Project/PriceDetail/BaseInfo/FormConfig/Lease'
import AssignmentOfClaims from '@/components/Project/PriceDetail/BaseInfo/FormConfig/AssignmentOfClaims'
import TurnLease from '@/components/Project/PriceDetail/BaseInfo/FormConfig/TurnLease'
import Factoring from '@/components/Project/PriceDetail/BaseInfo/FormConfig/Factoring'
import { Skeleton } from 'antd'
import styles from './index.less'
import mathjs from '@/utils/math'

const Index = ({ id, detail, canEdit = true, rootStore: store, isFormAdjust }, ref) => {
  const { bizType, postProjectBaseInfoModify, page } = store || {}

  const [pageDetail, setPageDetail] = useState({})

  useEffect(() => {
    if (detail) {
      setPageDetail({
        ...detail,
      })
    } else {
      const pageData = page.getData()
      if (pageData) {
        setPageDetail({ ...pageData })
      }
    }
  }, [detail, JSON.stringify(page.getData())])

  useEffect(() => {
    if (isFormAdjust) {
      page.init({ id })
    }
  }, [id, isFormAdjust])

  useImperativeHandle(ref, () => ({
    getPageDetail: () => {
      return pageDetail
    },
  }))

  const formatFormListItemValue = (values) => {
    const data = values?.map((item) => {
      return {
        ...item,
        clientId: {
          label: item.clientName,
          value: item.clientId,
        },
        stockRiskExposure: mathjs.toNonExponentialPlus(
          mathjs.format(mathjs.divide(item.stockRiskExposure, 10000))
        ),
      }
    })
    return data && data.length > 0 ? data : [{}]
  }

  const {
    lesseeInfo,
    guaranteeInfo,
    pledgorInfo,
    mortgagorInfo,
    supplierInfo,
    debtorInfo,
    creditorInfo,
    province,
    city,
    district,
    leaseTypes,
    zrTypes,
    factoringTypes,
  } = pageDetail
  console.log('pageDetail: ', pageDetail)

  const commonProps = {
    detail: {
      ...pageDetail,
      leaseTypes: leaseTypes?.filter(Boolean),
      zrTypes: zrTypes?.filter(Boolean),
      factoringTypes: factoringTypes?.filter(Boolean),
      area: [province, city, district].filter((item) => item),
      lesseeInfo: formatFormListItemValue(lesseeInfo),
      guaranteeInfo: formatFormListItemValue(guaranteeInfo),
      pledgorInfo: formatFormListItemValue(pledgorInfo),
      mortgagorInfo: formatFormListItemValue(mortgagorInfo),
      debtorInfo: formatFormListItemValue(debtorInfo),
      creditorInfo: formatFormListItemValue(creditorInfo),
      supplierInfo: supplierInfo?.split(',')?.map((item) => ({ clientName: item })),
    },
    canEdit,
    saveData: postProjectBaseInfoModify,
    store: store,
    isLog: page?.getData()?.isLog,
    initEdit: page?.getParams()?.newProject,
  }

  useEffect(() => {
    store?.setEvaluationSubjectIdValue(pageDetail.evaluationSubjectId)
  }, [store, JSON.stringify(pageDetail)])

  const CurrentDom = () => {
    if (!bizType) {
      return <Skeleton />
    }
    const Dom = {
      ZL: <Lease {...commonProps} />,
      ZZ: <TurnLease {...commonProps} />,
      BL: <Factoring {...commonProps} />,
      ZR: <AssignmentOfClaims {...commonProps} />,
    }
    return Dom[bizType]
  }

  return <div className={styles.desc}>{CurrentDom()}</div>
}

export default observer(forwardRef(Index))
