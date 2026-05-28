/* prettier-ignore-start */
import { http } from '@zswl/admin'

export default {
  // 首页
  postList: (params) => http.post('/ledger/page', params),
  // 详情
  postContractDetail: (params) => http.post('/ledger/detail/contract', params),
  // 首页列表下载
  postListDown: (params) =>
    http.post('/ledger/download', params, {
      type: 'download',
      timeout: 0,
    }),
  // 租赁物查重-查询
  postCheckLeaseDetail: (params) => http.post('/ledger/detail/check-repeat/select', params),
  // 详情页-中登网查重保存
  postCheckLeaseModify: (params) => http.post('/ledger/detail/check-repeat/save', params),

  // 租赁物元数据保存
  postLeaseMeatDataModify: (params) => http.post('/ledger/detail/leaseitem/metadata/save', params),
  // 租赁物元数据信息
  postLeaseMeatData: (params) => http.post('/ledger/detail/leaseitem/metadata/get', params),

  // 租赁物清单-列表
  postLeaseItemPageList: (params) => http.post('/ledger/detail/leaseitem/pagelist', params),
  // 租赁物清单-导入
  postLeaseItemImport: (params) =>
    http.post('/ledger/detail/leaseitem/import', params, {
      type: 'upload',
      timeout: 0,
    }),
  // 租赁物清单-导出
  postLeaseItemExport: (params) =>
    http.post('/ledger/detail/leaseitem/export', params, {
      type: 'download',
      timeout: 0,
    }),
  // 租赁物清单-批量删除
  postLeaseItemRemove: (params) => http.post('/ledger/detail/leaseitem/remove', params),
  // 租赁物总金额-保存
  postLeaseItemTotalamountSave: (params) =>
    http.post('/ledger/detail/leaseitem/totalamount/save', params),
  // 租赁物清单-模板下载
  postLeaseItemTemplateDownload: (params) =>
    http.post('/ledger/detail/leaseitem/template/download', params, {
      type: 'download',
      timeout: 0,
    }),
  // 租赁物审核模板
  postLeaseFileTemplateList: (params) =>
    http.post('/ledger/detail/check-repeat/template/download', params, {}),
  // 租赁物查重
  postLeaseCheckDuplicate: (params) =>
    http.post('/ledger/detail/leaseitem/dedup', params, {}),
}
