import { Select } from '@zswl/components'
import { forwardRef, useEffect, useImperativeHandle, useState } from 'react'
import { debounce as _debounce } from 'lodash'
import Api from '@/api/project/projectSelectApi'

const ProjectSelect = ({ queryParams, referer, ...params }, ref) => {
  const [projectList, setProjectList] = useState()
  const searchProject = _debounce(async (e, val) => {
    let res = []
    if (referer === 'price') {
      // 定价部分
      res = await Api.postQueryEffect({ projVagueName: e, ...val }, { referer })
    } else {
      // 评审部分
      res = await Api.getList({ projVagueName: e, ...val }, { referer })
    }
    if (res) {
      setProjectList(res)
    } else {
      setProjectList([])
    }
  }, 500)
  useImperativeHandle(ref, () => ({
    searchProject,
    getSelectedData: (id) => {
      return projectList.find((item) => item.id === id)
    },
  }))
  useEffect(() => {
    searchProject('', queryParams)
  }, [])

  return (
    <Select
      options={projectList}
      labelInValue
      filterOption={false}
      style={{ maxWidth: '100%' }}
      fieldNames={{ label: 'projName', value: 'id' }}
      showSearch
      onSearch={(e) => {
        searchProject(e, queryParams)
      }}
      {...params}
    />
  )
}

export default forwardRef(ProjectSelect)
