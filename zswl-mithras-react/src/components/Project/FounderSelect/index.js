import { Select } from '@zswl/components'
import { useEffect, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import Api from '../api'

const FounderSelect = ({ queryParams, referer, ...params }) => {
  const [founderList, setFounderList] = useState()
  const searchFounder = _debounce(async (e, val) => {
    const res = await Api.searchFounder({ name: e, ...val }, { referer })
    if (res) {
      setFounderList(res)
    }
  }, 500)
  useEffect(() => {
    searchFounder('', queryParams)
  }, [])
  return (
    <Select
      options={founderList}
      labelInValue
      placeholder="请选择风控经理！"
      filterOption={false}
      showSearch
      onSearch={(e) => {
        searchFounder(e, queryParams)
      }}
      {...params}
    />
  )
}

export default FounderSelect
