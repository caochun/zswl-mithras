
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import { Checkbox, message } from 'antd'
import { App } from '@zswl/components'
import Api from '@/api/contract/component/ApplicationDetail/ContractTextType/api'
import styles from './index.less'
import { hasPermission } from '@/utils'

// 项目经理节点(发起人)和【运营管理（经办）】审批节点可编辑

function ContractTextType({ id, taskActivityId, canEditFlag }) {
  const { contractTextStandard, contractTextNonstandard } = App.getData().optionsType

  const canEdit = taskActivityId
    ? ['userTask_startUser', 'userTask_yunYingGuanLi'].includes(taskActivityId)
    : canEditFlag

  const [detail, setDetail] = useState({})

  const getInitTextType = (allTypeList, initTypeList) => {
    let allTypeValueList = allTypeList.map((item) => item.value)
    if (initTypeList?.every((type) => allTypeValueList.includes(type))) return initTypeList
    return []
  }

  const getData = async () => {
    const res = await Api.postInfoGet({ contractId: id })
    setDetail({
      standardContractTypes: getInitTextType(contractTextStandard, res.textTypeList),
      notStandardContractTypes: getInitTextType(contractTextNonstandard, res.textTypeList),
    })
  }
  const saveData = async (value) => {
    const { standardContractTypes = [], notStandardContractTypes = [] } = value

    if (standardContractTypes?.length === 0 && notStandardContractTypes?.length === 0) {
      message.info('请选择合同文本类型')
      await Promise.reject()
    }

    if (notStandardContractTypes?.length && standardContractTypes?.length) {
      message.info('“标准文本”和“非标准文本”两者互斥不可同时选择')
      await Promise.reject()
    }
    await Api.postInfoSave({
      contractId: id,
      textTypeList: []
        .concat(standardContractTypes)
        .concat(notStandardContractTypes)
        .filter(Boolean),
    })
    getData()
  }

  // 项目经理 不能选中 “运营部认定的其他情况”
  const getContractTextNonstandard = () => {
    return contractTextNonstandard?.map((item) => {
      if (item.value === 'NONSTANDARD_OTHER') {
        return {
          ...item,
          disabled: !['userTask_yunYingGuanLi'].includes(taskActivityId),
        }
      }
      return item
    })
  }

  useEffect(() => {
    getData()
  }, [id])

  return (
    <EditDescription
      className={styles.wrap}
      title="合同文本类型"
      detail={detail}
      saveData={saveData}
      canEdit={canEdit}
      columns={[
        {
          title: '标准合同',
          dataIndex: 'standardContractTypes',
          requiredMark: true,
          span: 2,
          editable: (record, rowIndex) => {
            return {
              element: <Checkbox.Group options={contractTextStandard}></Checkbox.Group>,
            }
          },
          requiredMark: true,
          render: (value) => {
            return (
              <Checkbox.Group
                options={contractTextStandard}
                value={value}
                disabled={!canEdit}
              ></Checkbox.Group>
            )
          },
        },
        {
          title: '非标准合同',
          dataIndex: 'notStandardContractTypes',
          span: 2,
          requiredMark: true,
          editable: (record, rowIndex) => {
            return {
              element: <Checkbox.Group options={getContractTextNonstandard()}></Checkbox.Group>,
            }
          },
          render: (value) => {
            return (
              <Checkbox.Group
                options={getContractTextNonstandard()}
                value={value}
                disabled={!canEdit}
              ></Checkbox.Group>
            )
          },
        },
      ]}
    />
  )
}

export default observer(ContractTextType)
