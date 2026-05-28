import { Select } from '@zswl/components'
import { useEffect, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import Api from './api'

const OrgsSelect = ({ queryParams, referer, ...params }) => {
  const [OrgsList, setOrgsList] = useState()
  const searchOrgs = _debounce(async (e, val) => {
    const res = await Api.searchOrgs({ name: e, ...val }, { referer })
    if (res) {
      setOrgsList(res)
    }
  }, 500)
  useEffect(() => {
    searchOrgs('', queryParams)
  }, [])
  return (
    <Select
      options={OrgsList}
      labelInValue
      placeholder="请选择业务部门！"
      filterOption={false}
      showSearch
      onSearch={(e) => {
        searchOrgs(e, queryParams)
      }}
      {...params}
    />
  )
}

export default OrgsSelect
