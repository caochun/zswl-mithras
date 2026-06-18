import EditDescription from '@/components/Table/EditDescription'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import { ProjectAllotColumns as ALL_COLUMNS } from '@/components/Kpi/ProjectAllotEntries'
import { Button } from '@zswl/components'
import { compareDetail, compareTableData, getDescColumns } from '@/utils'
import Api from '@/api/kpi/projectAllot'

const Index = ({ projectDistributionId, source, businessVersion, listName }) => {
  const nameColumns = [{ title: '分润比', rename: '人员分润比' }].filter(Boolean)
  const bmnameColumns = ['部门分润比', '部门投放分配比'].filter(Boolean)
  const baseInfo_columns = getDescColumns(
    ALL_COLUMNS({
      source,
    }),
    nameColumns
  )
  const bmBaseInfo_columns = getDescColumns(
    ALL_COLUMNS({
      source,
    }),
    bmnameColumns
  )
  const [detail, setDetail] = useState({})
  const getBaseInfo = async () => {
    const weightInfoList = await Api.postProjectPrev({
      projectDistributionId,
      version: businessVersion,
    })
    setDetail({ weightInfoList })
  }
  const getBmBaseInfo = async () => {
    let deptWeightInfoList = await Api.postDeptWeightPrev({
      projectDistributionId,
      version: businessVersion,
    })
    if (deptWeightInfoList && deptWeightInfoList.length > 0) {
      deptWeightInfoList = deptWeightInfoList.map((item, i) => {
        if (!item.weightType) {
          item.weightType = 'BUSINESS_DEPT'
        }
        if (!item.weightTypeName) {
          item.weightTypeName = '业务部门'
        }
        return item
      })
    }
    let deptLaunchWeightInfoList = await Api.postDeptLaunchWeightPrev({
      projectDistributionId,
      version: businessVersion,
    })
    if (deptLaunchWeightInfoList?.length > 0) {
      deptLaunchWeightInfoList = deptLaunchWeightInfoList.map((item, i) => {
        if (!item.weightType) {
          item.weightType = 'BUSINESS_DEPT'
        }
        if (!item.weightTypeName) {
          item.weightTypeName = '业务部门'
        }
        return item
      })
    }
    setDetail({ deptWeightInfoList, deptLaunchWeightInfoList })
  }
  useEffect(() => {
    if (listName === 'deptWeightInfoList') {
      getBmBaseInfo()
    } else {
      getBaseInfo()
    }
  }, [projectDistributionId])

  return (
    <div>
      <EditDescription
        title={<h5 style={{ margin: '12px 0 0 0' }}>变更前分配信息</h5>}
        detail={detail}
        canEdit={false}
        columns={listName === 'deptWeightInfoList' ? bmBaseInfo_columns : baseInfo_columns}
      />
    </div>
  )
}

export default observer(Index)
