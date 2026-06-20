import { Input, InputNumber, message, Tooltip } from 'antd'
import { dateRangeTransform } from '@/utils/transform'
import { InputEditable } from '@/components/Format'
import { rangePresets, rules } from '@/utils'
import { FiledFormat } from '@/components/Format'
import { FounderSelect } from '@/components/Select'
const { TextArea } = Input

const ALL_COLUMNS = [
  {
    title: '机构名称',
    dataIndex: 'organizationName',
    fixed: 'left',
    width: 300,
    editable: InputEditable(),
  },
  { title: '机构简称', dataIndex: 'abbreviation' },
  { title: '机构编号', dataIndex: 'organizationCode', fixed: 'left', width: 160 },
  { title: '机构类型', dataIndex: 'organizationType', matchOption: 'organizationType' },
  { title: '联系人', dataIndex: 'contactName' },
  {
    title: '创建人',
    dataIndex: 'createByName',
    editable: {
      element: (
        <FounderSelect
          params={{
            job: 'moneymanager',
          }}
        />
      ),
    },
    render: (val, record) => <FiledFormat title={record.createByName} />,
  },
  {
    title: '创建日期',
    width: 200,
    dataIndex: 'createTime',
    type: 'rangePicker',
    dateFormat: 'yyyy-MM-DD HH:mm:ss',
    ranges: rangePresets,
    itemProps: {
      transform: (val) => dateRangeTransform(val, 'createDateFrom', 'createDateTo'),
    },
  },
  { title: '更新日期', width: 200, dataIndex: 'updateTime', dateFormat: 'yyyy-MM-DD HH:mm:ss' },
]
export default ALL_COLUMNS
