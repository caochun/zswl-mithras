import { Select } from '@zswl/components'
import { forwardRef, useEffect, useImperativeHandle, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import Api from '@/api/project/projReviewMeetingMinute'
import Store from '../../store'
// http://localhost:3000/process/query/detail/10863394?typeId=approval&businessKey=4018&diff=processInstanceId&nav=myquery
const ClientSelect = ({ queryParams, referer, needInit = true, id,...params }, ref) => {
  const [clientList, setClientList] = useState()
  const [loading, setLoading] = useState(false)
  const searchClient = _debounce(async (e, val) => {
    setLoading(true)
    const store = new Store();
    // 解构出方法
    const { onCustomers } = store
    const list = await onCustomers(queryParams.businessKey,queryParams?.id)
    setLoading(false)
    if (list) {
      setClientList(list)
    }
  }, 500)
  useImperativeHandle(ref, () => ({
    searchClient,
  }))
  useEffect(() => {
    needInit && searchClient('', queryParams)
  }, [needInit])

  return (
    <Select
      options={clientList}
      loading={loading}
      style={{ maxWidth: '100%' }}
      fieldNames={{ label:'clientName', value: 'clientId'  }}
      onSearch={(e) => {
        setClientList([])
        searchClient(e, queryParams)
      }}
      {...params}
    />
  )
}

export default forwardRef(ClientSelect)
