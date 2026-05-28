/* prettier-ignore-start */
import * as Types from './interface/vehicleCertificateApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 替换车证
  postVehicleReUpload: (
    data: Types.VehicleReUploadRequest
  ): Promise<Types.VehicleReUploadResponse> => http.post('/lease/vehicle/reUpload', data, { mock }),

  // 统计各状态车证数量
  postVehicleCount: (data: Types.VehicleCountRequest): Promise<Types.VehicleCountResponse> =>
    http.post('/lease/vehicle/count', data, { mock }),

  // 车证上传\/追加车证\/重新上传
  postVehicleUpload: (data: Types.VehicleUploadRequest): Promise<Types.VehicleUploadResponse> =>
    http.post('/lease/vehicle/upload', data, {
      mock,
      type: 'upload',
      timeout: 0,
    }),

  // 车证下载
  postVehicleExportExcel: (
    data: Types.VehicleExportExcelRequest
  ): Promise<Types.VehicleExportExcelResponse> =>
    http.post('/lease/vehicle/exportExcel', data, { mock, type: 'download' }),

  // 车证分页列表
  postVehicleList: (data: Types.VehicleListRequest): Promise<Types.VehicleListResponse> =>
    http.post('/lease/vehicle/list', data, { mock }),

  // 车证批量修改
  postVehicleUpdate: (data: Types.VehicleUpdateRequest): Promise<Types.VehicleUpdateResponse> =>
    http.post('/lease/vehicle/update', data, { mock }),

  // 车证批量删除
  postVehicleDelete: (data: Types.VehicleDeleteRequest): Promise<Types.VehicleDeleteResponse> =>
    http.post('/lease/vehicle/delete', data, { mock }),

  // 车证解锁
  postVehicleUnlock: (data: Types.VehicleUnlockRequest): Promise<Types.VehicleUnlockResponse> =>
    http.post('/lease/vehicle/unlock', data, { mock }),

  // 车证锁定
  postVehicleLock: (data: Types.VehicleLockRequest): Promise<Types.VehicleLockResponse> =>
    http.post('/lease/vehicle/lock', data, { mock }),
}

/* prettier-ignore-end */
