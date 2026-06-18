
import ALL_COLUMNS from './Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { forwardRef, useEffect, useImperativeHandle, useMemo, useRef, useState } from 'react'
import { Form, Select } from '@zswl/components'
import trackingApi from '@/api/trackEvent/trackingApi'
import contractApi from '@/api/contract/baseInfo'
import selectApi from '@/api/common/selectApi'
import _ from 'lodash'

function Index({ dataSource, canEdit = true, params }, ref) {
  const disabled = !_.isEmpty(params)
  useEffect(() => {
    if (params?.contractCode) {
      contractChange(params.contractCode, 'contractCode')
    } else if (params?.projName) {
      contractChange(params.projName, 'projName')
    } else if (params?.clientId) {
      contractChange(params.clientId, 'clientId')
    }
  }, [params])
  const descRef = useRef()
  const [detail, setDetail] = useState({
    ...dataSource,
  })
  useImperativeHandle(ref, () => ({
    decs: descRef.current,
    submit: async () => {
      if (disabled) return detail
      const data = await descRef.current.validateFields()
      return { ...detail, ...data }
    },
    setValues: (values) => {
      setTimeout(() => {
        // descRef.current.form.setFieldsValue(values)
        setDetail(values)
      }, 10)
    },
  }))

  const contractChange = async (value, field) => {
    const res = await trackingApi.postTrackEventContractInfo({
      [field]: value,
      bizSource: params?.bizSource ?? 'LEDGER',
      bizId: params?.bizId,
    })
    descRef.current.form.setFieldsValue(res)
    setDetail(res)
  }

  const getProj = async () => {
    const res = await contractApi.postProjList({ pageSize: 1000 }, 'contractreviewquery_trackevent')

    return res.map((v) => ({ label: v.projName, value: v.projName }))
  }
  const getContract = async () => {
    const res = await trackingApi.getTrackEventContractCodeList({})
    return res.map((v) => ({ label: v, value: v }))
  }

  const getClient = async () => {
    const res = await selectApi.getClientList(
      { pageSize: 5000 },
      { functionCode: 'clientlist-trackevent' }
    )
    return res.list.map((v) => ({ label: v.clientName, value: v.id }))
  }
  const nameColumns = [
    {
      title: '合同编号',
      span: 2,
      editable: disabled
        ? false
        : {
            element: (
              <Select
                options={getContract}
                onChange={(val) => contractChange(val, 'contractCode')}
                disabled={disabled}
                allowClear
              />
            ),
          },
    },
    {
      title: '项目名称',
      editable: disabled
        ? false
        : {
            element: (
              <Select
                onChange={(val) => contractChange(val, 'projName')}
                options={getProj}
                disabled={disabled}
                allowClear
              />
            ),
          },
    },
    { title: '项目编号', editable: false },
    { title: '业务类型', editable: false },
    { title: '租赁类型', editable: false },
    {
      title: '客户名称',
      editable: disabled
        ? false
        : {
            element: (
              <Select
                options={getClient}
                onChange={(val) => contractChange(val, 'clientId')}
                disabled={disabled}
                allowClear
              />
            ),
          },
    },
    { title: '风控行业分类', editable: false },
    { title: '项目主办', editable: false },
    { title: '项目协办', editable: false },
    { title: '业务部门', editable: false },
    { title: '业务部门负责人', editable: false },
  ]

  const columns = getDescColumns(ALL_COLUMNS, nameColumns)
  return (
    <EditDescription
      detail={detail}
      hiddenButton
      canEdit={canEdit && !disabled}
      initEdit={canEdit}
      columns={columns}
      ref={descRef}
      style={{ marginBottom: 12 }}
    />
  )
}

export default forwardRef(Index)
