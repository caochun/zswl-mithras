import { ClientSelect, FounderSelect, OrgSelect } from '@/components'
import { InputEditable } from '@/components/Format'
import { rules } from '@/utils'
import { App, Select } from '@zswl/components'

const { optionsType } = App.getData()

const ALL_COLUMNS = [
  {
    title: '模版名称',
    dataIndex: 'templateName',
    width: 180,
    fixed: 'left',
    editable: InputEditable,
  },
  {
    title: '业务类型',
    width: 140,
    dataIndex: 'bizType',
    matchOption: 'projEstablishBizType',
  },
  { title: '模版状态', width: 100, dataIndex: 'status', matchOption: 'archiveTemplateStatusEnum' },
  {
    title: '创建时间',
    width: 180,
    dataIndex: 'createTime',
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          createTime: undefined,
          createTimeFrom: startDataTime?.format('yyyy-MM-DD 00:00:00'),
          createTimeTo: endDataTime?.format('yyyy-MM-DD 23:59:59'),
        }
      },
    },
  },
  {
    title: '更新时间',
    width: 200,
    dataIndex: 'updateTime',
    type: 'rangePicker',
    itemProps: {
      transform: (val) => {
        const [startDataTime, endDataTime] = val || []
        return {
          updateTime: undefined,
          updateTimeFrom: startDataTime?.format('yyyy-MM-DD 00:00:00'),
          updateTimeTo: endDataTime?.format('yyyy-MM-DD 23:59:59'),
        }
      },
    },
  },
  {
    title: '项目名称',
    dataIndex: 'projName',
    width: 300,
    fixed: 'left',
    editable: InputEditable,
  },
  {
    title: '业务主办',
    dataIndex: 'projSponsorUserId',
    // render: (val, { projSponsorUserName }) => <FiledFormat title={projSponsorUserName} />,
    requiredMark: true,
    width: 130,
    editable: {
      element: (
        <FounderSelect functionCode="selectfounder-adjust" params={{ job: 'projmanager' }} />
      ),
      rules: [rules.required()],
    },
  },
  {
    title: '项目类别',
    requiredMark: true,
    dataIndex: 'projectType',
    matchOption: 'projectType',
    span: 1,
    editable: {
      element: (
        <Select options={optionsType.projectType?.filter((item) => item.label != '公用事业类')} />
      ),
      rules: [rules.required()],
    },
  },
  {
    title: '归档编号',
    dataIndex: 'archivesCode',
    width: 140,
  },
  {
    title: '业务部门',
    dataIndex: 'bizDeptId',
    requiredMark: true,
    width: 140,
    editable: {
      element: <OrgSelect />,
      rules: [rules.required()],
      functionCode: 'selectorgs-adjust',
    },
  },
  { title: '文件类型', dataIndex: 'fileType' },
  {
    title: '客户名称',
    dataIndex: 'clientId',
    width: 160,
    editable: {
      element: <ClientSelect canJump={false} />,
      functionCode: 'clientlist-adjust',
    },
  },
  {
    title: '归档状态',
    dataIndex: 'status',
    matchOption: 'archivingStatus',
    width: 120,
  },
  {
    title: '审批状态',
    dataIndex: 'flowStatus',
    matchOption: 'archivesFlowStatusEnum',
    width: 160,
  },
  { title: '必传文档情况', dataIndex: 'requiredSituation', width: 120 },
  { title: '操作', dataIndex: 'id', width: 130 },
]

export default ALL_COLUMNS
