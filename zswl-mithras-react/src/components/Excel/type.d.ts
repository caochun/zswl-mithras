export interface DownLoadExcelProps {
  // antd 的columns
  columns: Array<{
    title: string
    dataIndex: string
    width: number
    render: (value: any, record: object) => any
  }>
  // 数据源
  dataSource: Record<string, any>
  // 文件名，默认：当前时间字符串
  fileName?: string
  // 导出的文件类型，默认：xlsx
  format?: 'xlsx' | 'csv'
  sheetName?: string
}

export interface DownLoadExcelListProps {
  columns: Array<{
    title: string
    dataIndex: string
    width: number
    render: (value: any, record: object) => any
  }>
  dataSource: Record<string, any>
  sheetName?: string
}
export interface DownLoadMoreExcelList {
  excelList: DownLoadExcelListProps[]
  fileName: string
  format?: 'xlsx' | 'csv'
}

export interface DownLoadZipProps {
  // 压缩包名称
  zipName: string
  files: Array<{
    // 每个文件名
    filename: string
    // 文件夹名
    folderName?: string
    sheets: Array<Pick<DownLoadExcelProps, 'columns' | 'dataSource'>>
  }>
}

export interface DownLoadExcelChunkedProps extends DownLoadExcelProps {
  // 每个工作簿的最大行数（默认 10000）
  chunkSize?: number
}
