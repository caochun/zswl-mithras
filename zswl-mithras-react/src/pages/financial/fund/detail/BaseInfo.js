import EditDescription from '@/components/Table/EditDescription'
import ALL_COLUMNS from '../Column'
import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { useRef } from 'react'
import FormOrg from '../Component/FormOrg'
import { MatchOptionColumn } from '@/components/Format'
import { App } from '@zswl/components'
import FormGuarantee from '../Component/FormGuarantee'

function Index({ detail, saveData, isLog, canEdit = true, initEdit, isOtherChange }) {
  const ref = useRef()
  const isYT = detail.businessType === 'SYNDICATIONS'
  const { fundFinancingBizTypeEnum } = App.getData().optionsType

  const nameColumns = [
    isYT
      ? {
          title: '融资机构',
          dataIndex: 'organizationInfoList',
          span: 2,
          editable: isOtherChange
            ? false
            : ({ organizationInfoList }) => {
                return <FormOrg listName="organizationInfoList" value={organizationInfoList} />
              },
          render: (val) => <FormOrg.Detail value={val} />,
        }
      : {
          title: '融资机构',
          dataIndex: 'organizationId',
          span: 2,
          editable: false,
        },
    { title: '融资编号', editable: false },
    !isYT && { title: '总授信额度(元)', editable: false },
    !isYT && { title: '剩余授信额度(元)', editable: false },
    '融资期限类型',
    MatchOptionColumn({
      title: '业务类型',
      editable: true,
      dataIndex: 'businessType',
      matchOption: isYT
        ? fundFinancingBizTypeEnum.filter((v) => v.value === 'SYNDICATIONS')
        : fundFinancingBizTypeEnum.filter((v) => v.value !== 'SYNDICATIONS'),
    }),
    '是否期初一次性收息',
    {
      title: '增信方式',
      editable: false,
      render: (val) => <FormGuarantee.Detail value={val} isYT={isYT} />,
    },
    '资金用途',
    '备注',
    '担保方',
    {
      title: '资金经理(基本信息)',
      rename: '资金经理',
    },

    '所属部门',
    '部门负责人',
    '分管领导',
  ]
  const columns = getDescColumns(ALL_COLUMNS, nameColumns)

  return (
    <EditDescription
      detail={detail}
      saveData={saveData}
      canEdit={canEdit}
      isLog={isLog}
      initEdit={initEdit}
      columns={columns}
      ref={ref}
    />
  )
}

export default observer(Index)
