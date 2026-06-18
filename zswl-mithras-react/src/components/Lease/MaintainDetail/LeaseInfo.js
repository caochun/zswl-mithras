import EditDescription from '@/components/Table/EditDescription'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import { Checkbox, message } from 'antd'
import { App } from '@zswl/components'
import Api from '@/api/lease/maintainApi'
import { hasPermission } from '@/utils'

function Index({ id, canEdit = true, baseStore = {} }) {
  const { leaseItemManagerOwnershipFileType, leaseItemManagerLeaseItemViewType } =
    App.getData().optionsType

  const [detail, setDetail] = useState({})

  const getData = async () => {
    const res = await Api.postLeaseMeatData({ id })
    setDetail(res)
    baseStore?.leaseTypeChange(res?.leaseItemTypes)
  }
  const saveData = async (value) => {
    const { leaseItemTypes, ownershipFileTypes, valueIdentificationFiles } = value
    if (!leaseItemTypes || leaseItemTypes.length === 0) {
      message.info('请选择租赁物类型')
      await Promise.reject()
    }
    if (!ownershipFileTypes || ownershipFileTypes.length === 0) {
      message.info('请选择权属文件类型')
      await Promise.reject()
    }

    await Api.postLeaseMeatDataModify({
      id,
      leaseItemTypes,
      ownershipFileTypes,
      valueIdentificationFiles,
    })
    baseStore?.leaseTypeChange(leaseItemTypes)
    getData()
  }
  useEffect(() => {
    getData()
  }, [id])

  return (
    <EditDescription
      title="租赁物信息"
      detail={detail}
      saveData={saveData}
      canEdit={canEdit && hasPermission('ledgerdetailleaseitemmetadatasave')}
      columns={[
        {
          title: '租赁物类型',
          dataIndex: 'leaseItemTypes',
          requiredMark: true,
          span: 2,
          editable: (record, rowIndex) => {
            return {
              element: (
                <Checkbox.Group
                  options={leaseItemManagerLeaseItemViewType}
                  // onChange={baseStore.leaseTypeChange}
                ></Checkbox.Group>
              ),
            }
          },
          requiredMark: true,
          render: (value) => {
            const labels = value?.map(
              (value) =>
                leaseItemManagerLeaseItemViewType.find((item) => item.value === value)?.label
            )
            return labels?.join('、') ?? '-'
          },
        },
        {
          title: '权属认定文件',
          dataIndex: 'ownershipFileTypes',
          span: 2,
          requiredMark: true,
          editable: (record, rowIndex) => {
            return {
              element: (
                <Checkbox.Group options={leaseItemManagerOwnershipFileType}></Checkbox.Group>
              ),
            }
          },
          render: (value) => {
            const labels = value?.map(
              (value) =>
                leaseItemManagerOwnershipFileType.find((item) => item.value === value)?.label
            )
            return labels?.join('、') ?? '-'
          },
        },
      ]}
    />
  )
}

export default observer(Index)
