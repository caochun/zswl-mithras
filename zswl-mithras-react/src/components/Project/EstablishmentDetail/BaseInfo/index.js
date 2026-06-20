import React, { useEffect, useMemo, useState } from 'react'
import { Button, message } from 'antd'
import { Select, Form } from '@zswl/components'
import { observer } from '@zswl/admin'
import styles from './index.less'
import Lease from './FormConfig/Lease'
import TurnLease from './FormConfig/TurnLease'
import Factoring from './FormConfig/Factoring'
import AssignmentOfClaims from './FormConfig/AssignmentOfClaims'
import { amountFormat, formatNull, hasValue } from '@/utils'
import mathjs from '@/utils/math'

export {
  Lease as EstablishmentLeaseBaseInfo,
  AssignmentOfClaims as EstablishmentAssignmentOfClaimsBaseInfo,
  TurnLease as EstablishmentTurnLeaseBaseInfo,
  Factoring as EstablishmentFactoringBaseInfo,
}

const BasicInformation = ({
  id,
  compareData = {},
  canEdit = true,
  isProjSponsor,
  parentStore: store,
}) => {
  const [form] = Form.useForm()
  const {
    bizType,
    baseInfoShowValue,
    setBaseInfoShowValue,
    page,
    updateInfo,
    newDetail,
    getProjectDataDetail,
  } = store || {}
  const projectBaseInfoDetail = page?.getData() || {}
  const [detailShowData, setDetailShowData] = useState({})

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
  useEffect(() => {
    if (projectBaseInfoDetail) {
      const currentDetailData = {}
      Object.keys(projectBaseInfoDetail).forEach((item) => {
        currentDetailData[item] = formatNull(projectBaseInfoDetail[item])
      })
      const {
        lesseeInfo,
        guaranteeInfo,
        pledgorInfo,
        mortgagorInfo,
        debtorInfo,
        supplierInfo,
        projSponsorUserId,
        projSponsorUserName,
        projCosponsorUserIds,
        projCosponsorUserNames,
        riskControlManagerId,
        riskControlManagerName,
        transferorClientId,
        transferorClientName,
        creditorInfo,
        province,
        city,
        district,
        evaluationSubjectId,
        evaluationSubjectName,
      } = currentDetailData
      const currentFormValue = {
        ...currentDetailData,
        supplierInfo: supplierInfo?.split(',')?.map((item) => ({ clientName: item })),
        lesseeInfo: formatFormListItemValue(lesseeInfo),
        guaranteeInfo: formatFormListItemValue(guaranteeInfo),
        pledgorInfo: formatFormListItemValue(pledgorInfo),
        mortgagorInfo: formatFormListItemValue(mortgagorInfo),
        debtorInfo: formatFormListItemValue(debtorInfo),
        creditorInfo: formatFormListItemValue(creditorInfo),
        evaluationSubjectId: evaluationSubjectId
          ? {
              value: evaluationSubjectId,
              label: evaluationSubjectName,
            }
          : undefined,
        area: [province, city, district].filter((item) => item),
        projSponsorUserId: projSponsorUserId
          ? {
              value: projSponsorUserId,
              label: projSponsorUserName,
            }
          : undefined,

        projCosponsorUserIds: projCosponsorUserIds?.map((item, index) => {
          return {
            value: item,
            label: projCosponsorUserNames[index],
          }
        }),
        riskControlManagerId: riskControlManagerId
          ? {
              value: riskControlManagerId,
              label: riskControlManagerName,
            }
          : undefined,
        transferorClientId: transferorClientId
          ? {
              value: transferorClientId,
              label: transferorClientName,
            }
          : undefined,

        riskControlManagerId: riskControlManagerId?.map((item, index) => {
          return {
            value: item,
            label: riskControlManagerName[index],
          }
        }),
        ...newDetail,
      }
      form.setFieldsValue(currentFormValue)
      setDetailShowData(currentFormValue)
      // 更新 存续租赁合同
      // priceStore.getProjectQSDetail(id)
    }
  }, [JSON.stringify(projectBaseInfoDetail), newDetail])

  const CurrentDom = useMemo(() => {
    if (!bizType) {
      return null
    }
    const Dom = {
      ZL: (
        <Lease
          showValue={baseInfoShowValue}
          form={form}
          detail={detailShowData}
          compareChangeList={compareData.baseInfo}
        />
      ),
      BL: (
        <Factoring
          showValue={baseInfoShowValue}
          form={form}
          detail={detailShowData}
          compareChangeList={compareData.baseInfo}
        />
      ),
      ZZ: (
        <TurnLease
          showValue={baseInfoShowValue}
          form={form}
          detail={detailShowData}
          compareChangeList={compareData.baseInfo}
        />
      ),
      ZR: (
        <AssignmentOfClaims
          showValue={baseInfoShowValue}
          form={form}
          detail={detailShowData}
          compareChangeList={compareData.baseInfo}
        />
      ),
    }
    return Dom[bizType]
  }, [bizType, baseInfoShowValue, form, detailShowData, compareData])

  const onSave = async () => {
    const values = await form.validateFields().catch((e) => {
      form.scrollToField(e.errorFields[0]?.name, {
        behavior(actions) {
          actions.forEach(({ el, top, left }) => {
            el.scrollTop = top + 100
            el.scrollLeft = left
          })
        },
      })
    })
    let flag = true
    const getClientId = (items, needAppendClientType) => {
      return items
        ?.map((item) => {
          if (!needAppendClientType && !item.clientType && hasValue(item.clientId)) {
            flag = false
          }
          return {
            ...item,
            clientId: item.clientId?.value,
            clientType: needAppendClientType ? 'CORPORATION' : item.clientType, // 自动为承租人添加类型默认为法人
            // clientType: item.clientType === 'NO-CORPORATION' ? null : item.clientType,
            stockRiskExposure: mathjs.toNonExponentialPlus(
              mathjs.format(mathjs.multiply(item.stockRiskExposure, 10000))
            ),
          }
        })
        .filter((i) => i.clientId || i.clientName)
    }

    const [province, city, district] = values.area ?? []

    const formData = {
      ...values,
      creditorStockRiskExposure: mathjs.toNonExponentialPlus(
        mathjs.format(mathjs.multiply(values.creditorStockRiskExposure, 10000))
      ),
      province,
      city,
      district,
      lesseeInfo: getClientId(values.lesseeInfo, true),
      guaranteeInfo: getClientId(values.guaranteeInfo),
      pledgorInfo: getClientId(values.pledgorInfo),
      mortgagorInfo: getClientId(values.mortgagorInfo),
      debtorInfo: getClientId(values.debtorInfo),
      creditorInfo: getClientId(values.creditorInfo, true),
      supplierInfo: values?.supplierInfo?.map(({ clientName }) => clientName).join(','),
      projSponsorUserId: values.projSponsorUserId?.value,
      evaluationSubjectId: values.evaluationSubjectId?.value,
      projCosponsorUserIds: values.projCosponsorUserIds?.map((item) => {
        return item?.value
      }),
      // creditorClientId: values.creditorClientId?.value,
      bizDeptLeaderId: detailShowData.bizDeptLeaderId,
      bizDivisionLeaderId: detailShowData.bizDivisionLeaderId,
      bizDeptId: detailShowData.bizDeptId,
      riskControlManagerId: values.riskControlManagerId?.map((item) => {
        return +item?.value
      }),
      transferorClientId: values.transferorClientId?.value,
      id,
      ...newDetail,
    }
    if (!flag) {
      message.info('请选择类型')
      return
    }
    store.postProjectBaseInfoModify(formData, () => {
      page.init()
      setBaseInfoShowValue(true)
      getProjectDataDetail(id)
    })
  }
  useEffect(() => {
    return () => {
      setBaseInfoShowValue?.(true)
      form?.resetFields()
    }
  }, [])

  return (
    <div className={styles.wrap}>
      <Form form={form} scrollToFirstError>
        {canEdit && (
          <div className={styles.editWrap}>
            {baseInfoShowValue ? (
              <Button
                type="primary"
                disabled={!isProjSponsor}
                onClick={() => {
                  setBaseInfoShowValue(false)
                }}
              >
                编辑
              </Button>
            ) : (
              <>
                <Button type="primary" onClick={updateInfo} style={{ marginRight: 8 }}>
                  更新评级信息
                </Button>
                <Button
                  style={{ marginRight: 8 }}
                  onClick={() => {
                    form.setFieldsValue(detailShowData)
                    setBaseInfoShowValue(true)
                  }}
                >
                  取消
                </Button>
                <Button type="primary" onClick={onSave}>
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

export default observer(BasicInformation)
