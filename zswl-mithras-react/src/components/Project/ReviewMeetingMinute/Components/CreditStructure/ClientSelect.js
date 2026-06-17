import { Select } from '@zswl/components'
import { forwardRef, useEffect, useImperativeHandle,useMemo, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import Api from '../../api'
import Store from '../../store'

// http://localhost:3000/process/query/detail/10863394?typeId=approval&businessKey=4018&diff=processInstanceId&nav=myquery
const ClientSelect = ({ queryParams, referer, needInit = true, value ,id,...params }, ref) => {
  const [clientList, setClientList] = useState()
  const [loading, setLoading] = useState(false)
  const [obj, seObj] = useState()
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
  useEffect(() => {
    if (value !== undefined && value !== null) {
    let data = JSON.parse(JSON.stringify(value))
      data = data.map((item,i)=> {
        return {...item,label:item.clientName,value: item.clientName}
      })
      seObj(data)
    }
    return ()=>{
      seObj(null)
      }
  }, [value])
  return (
    <Select
      options={clientList}
      loading={loading}
      mode={'multiple'}
      labelInValue
      // style={{ maxWidth: '100%' }}
      fieldNames={{label:'clientName', value: 'clientName'}}
      onSearch={(e) => {
        setClientList([])
        searchClient(e, queryParams)
      }}
      value={obj}
      {...params}
    />
  )
}

export default forwardRef(ClientSelect)
 