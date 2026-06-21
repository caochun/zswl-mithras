
import ALL_COLUMNS from './Column'
import { getDescColumns } from '@/utils'
import { forwardRef, useEffect, useImperativeHandle, useMemo, useRef, useState } from 'react'
import { App, Select } from '@zswl/components'
import trackingApi from '@/api/trackEvent/trackingApi'
import { getLocalStorage } from '@zswl/admin'

function TrackEventTrackingTask({ dataSource, canEdit = true }, ref) {
  const descRef = useRef()

  const userInfo = getLocalStorage('userInfo')
  const [detail, setDetail] = useState({
    createByName: userInfo.userName,
    ...dataSource,
  })

  const [processorList, setProcessorList] = useState([])
  const processorChange = (value) => {
    const processorDept = processorList.find((item) => item.id === value).orgs
    setDetail({ ...detail, processorDept })
  }
  const getProcessor = async () => {
    const res = await trackingApi.getTrackEventQueryProcessor({})
    setProcessorList(res)
    return res
  }
  const nameColumns = [
    { title: '任务名称' },
    { title: '任务类型' },
    { title: '提出人', editable: false },
    { title: '计划日期' },
    { title: '起租后X自然日' },
    {
      title: '处理人',
      editable: {
        element: (
          <Select
            options={getProcessor}
            onChange={processorChange}
            fieldNames={{ label: 'name', value: 'id' }}
            allowClear
          />
        ),
        rules: [{ required: true, message: '请选择处理人' }],
      },
    },
    '处理人岗位',
    { title: '提醒频率' },
    { title: '任务内容', required: true, requiredMark: true },
  ]

  const columns = getDescColumns(ALL_COLUMNS, nameColumns)
  useImperativeHandle(ref, () => ({
    decs: descRef.current,
    submit: async () => {
      const data = await descRef.current.validateFields()
      return { ...detail, ...data }
    },
    setValues: (values) => {
      setDetail(values)
    },
  }))
  return (
    <EditDescription
      detail={detail}
      hiddenButton
      initEdit={canEdit}
      canEdit={canEdit}
      columns={columns}
      title="跟踪任务"
      ref={descRef}
    />
  )
}

export default forwardRef(TrackEventTrackingTask)
