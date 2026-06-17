import { Select } from '@zswl/components'
import { forwardRef, useEffect, useImperativeHandle, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import Api from '../api'

const ClientSelect = ({ queryParams, referer, needInit = true, ...params }, ref) => {
  const [clientList, setClientList] = useState()
  const [loading, setLoading] = useState(false)
  const searchClient = _debounce(async (e, val) => {
    setLoading(true)
    const { list } = await Api.getClientList({ clientName: e, effected: true, ...val }, { referer })
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
      labelInValue
      filterOption={false}
      style={{ maxWidth: '100%' }}
      fieldNames={{ label: 'clientName', value: 'id' }}
      showSearch
      onSearch={(e) => {
        setClientList([])
        searchClient(e, queryParams)
      }}
      {...params}
    />
  )
}

export default forwardRef(ClientSelect)
