"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.paramsList = exports.ParamsListItem = exports.paramsAttachment = exports.paramsIban = exports.paramsVehicleRegistration = exports.paramsAddressProof = exports.paramsSelfie = exports.paramsFrenchHealthCard = exports.paramsLiveness = exports.paramsIDAnalyze = exports.paramsID = void 0;

var _idcheckioParams = require("./idcheckio-params.js");

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var paramsID = new _idcheckioParams.IDCheckioParamsBuilder().setDocType(_idcheckioParams.DocumentType.ID).setOrientation(_idcheckioParams.IDCheckioOrientation.PORTRAIT).setIntegrityCheck(new _idcheckioParams.IntegrityCheck(true)).setUseHd(false).setConfirmationType(_idcheckioParams.ConfirmationType.DATA_OR_PICTURE).setScanBothSides(_idcheckioParams.ScanBothSides.ENABLED).setSideOneExtraction(new _idcheckioParams.Extraction(_idcheckioParams.Codeline.VALID, _idcheckioParams.FaceDetection.ENABLED)).setSideTwoExtraction(new _idcheckioParams.Extraction(_idcheckioParams.Codeline.REJECT, _idcheckioParams.FaceDetection.DISABLED)).setLanguage(_idcheckioParams.Language.fr).setManualButtonTimer(10).setMaxPictureFilesize(_idcheckioParams.FileSize.TWO_MEGA_BYTES).setFeedbackLevel(_idcheckioParams.FeedbackLevel.ALL).setAdjustCrop(false).setConfirmAbort(false).setOnlineConfig(new _idcheckioParams.OnlineConfig({
  checkType: _idcheckioParams.CheckType.CHECK_FAST,
  isReferenceDocument: true
})).build();
exports.paramsID = paramsID;
var paramsIDAnalyze = new _idcheckioParams.IDCheckioParamsBuilder().setDocType(_idcheckioParams.DocumentType.ID).setOrientation(_idcheckioParams.IDCheckioOrientation.PORTRAIT).setUseHd(false).setConfirmationType(_idcheckioParams.ConfirmationType.DATA_OR_PICTURE).setScanBothSides(_idcheckioParams.ScanBothSides.ENABLED).setSideOneExtraction(new _idcheckioParams.Extraction(_idcheckioParams.Codeline.ANY, _idcheckioParams.FaceDetection.ENABLED)).setSideTwoExtraction(new _idcheckioParams.Extraction(_idcheckioParams.Codeline.ANY, _idcheckioParams.FaceDetection.ENABLED)).setMaxPictureFilesize(_idcheckioParams.FileSize.TWO_MEGA_BYTES).build();
exports.paramsIDAnalyze = paramsIDAnalyze;
var paramsLiveness = new _idcheckioParams.IDCheckioParamsBuilder().setDocType(_idcheckioParams.DocumentType.LIVENESS).setOrientation(_idcheckioParams.IDCheckioOrientation.PORTRAIT).setConfirmAbort(true).build();
exports.paramsLiveness = paramsLiveness;
var paramsFrenchHealthCard = new _idcheckioParams.IDCheckioParamsBuilder().setDocType(_idcheckioParams.DocumentType.FRENCH_HEALTH_CARD).setConfirmationType(_idcheckioParams.ConfirmationType.DATA_OR_PICTURE).setOrientation(_idcheckioParams.IDCheckioOrientation.PORTRAIT).build();
exports.paramsFrenchHealthCard = paramsFrenchHealthCard;
var paramsSelfie = new _idcheckioParams.IDCheckioParamsBuilder().setDocType(_idcheckioParams.DocumentType.SELFIE).setConfirmationType(_idcheckioParams.ConfirmationType.DATA_OR_PICTURE).setOrientation(_idcheckioParams.IDCheckioOrientation.PORTRAIT).build();
exports.paramsSelfie = paramsSelfie;
var paramsAddressProof = new _idcheckioParams.IDCheckioParamsBuilder().setDocType(_idcheckioParams.DocumentType.A4).setConfirmationType(_idcheckioParams.ConfirmationType.DATA_OR_PICTURE).setOrientation(_idcheckioParams.IDCheckioOrientation.PORTRAIT).setUseHd(true).setOnlineConfig(new _idcheckioParams.OnlineConfig({
  cisType: _idcheckioParams.CISType.ADDRESS_PROOF
})).build();
exports.paramsAddressProof = paramsAddressProof;
var paramsVehicleRegistration = new _idcheckioParams.IDCheckioParamsBuilder().setDocType(_idcheckioParams.DocumentType.VEHICLE_REGISTRATION).setConfirmationType(_idcheckioParams.ConfirmationType.DATA_OR_PICTURE).setOrientation(_idcheckioParams.IDCheckioOrientation.PORTRAIT).setSideOneExtraction(new _idcheckioParams.Extraction(_idcheckioParams.Codeline.VALID, _idcheckioParams.FaceDetection.DISABLED)).build();
exports.paramsVehicleRegistration = paramsVehicleRegistration;
var paramsIban = new _idcheckioParams.IDCheckioParamsBuilder().setDocType(_idcheckioParams.DocumentType.PHOTO).setConfirmationType(_idcheckioParams.ConfirmationType.DATA_OR_PICTURE).setOrientation(_idcheckioParams.IDCheckioOrientation.PORTRAIT).setUseHd(true).setOnlineConfig(new _idcheckioParams.OnlineConfig({
  cisType: _idcheckioParams.CISType.IBAN
})).build();
exports.paramsIban = paramsIban;
var paramsAttachment = new _idcheckioParams.IDCheckioParamsBuilder().setDocType(_idcheckioParams.DocumentType.PHOTO).setConfirmationType(_idcheckioParams.ConfirmationType.DATA_OR_PICTURE).setOrientation(_idcheckioParams.IDCheckioOrientation.PORTRAIT).setUseHd(true).setAdjustCrop(true).setOnlineConfig(new _idcheckioParams.OnlineConfig({
  cisType: _idcheckioParams.CISType.OTHER
})).build();
exports.paramsAttachment = paramsAttachment;

var ParamsListItem = function ParamsListItem() {
  var _ref = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {},
      name = _ref.name,
      params = _ref.params,
      isOnline = _ref.isOnline,
      _ref$isUpload = _ref.isUpload,
      isUpload = _ref$isUpload === void 0 ? false : _ref$isUpload;

  _classCallCheck(this, ParamsListItem);

  this.name = name;
  this.params = params;
  this.isOnline = isOnline;
  this.isUpload = isUpload;
};

exports.ParamsListItem = ParamsListItem;
var paramsList = [new ParamsListItem({
  name: "ID Offline",
  params: paramsID,
  isOnline: false
}), new ParamsListItem({
  name: "ID Online",
  params: paramsID,
  isOnline: true
}), new ParamsListItem({
  name: "Liveness Online",
  params: paramsLiveness,
  isOnline: true
}), new ParamsListItem({
  name: "French health card Online",
  params: paramsFrenchHealthCard,
  isOnline: true
}), new ParamsListItem({
  name: "Selfie Online",
  params: paramsSelfie,
  isOnline: true
}), new ParamsListItem({
  name: "Address proof Online",
  params: paramsAddressProof,
  isOnline: true
}), new ParamsListItem({
  name: "Vehicle registration Online",
  params: paramsVehicleRegistration,
  isOnline: true
}), new ParamsListItem({
  name: "Iban Online",
  params: paramsIban,
  isOnline: true
}), new ParamsListItem({
  name: "ID Analyze",
  params: paramsIDAnalyze,
  isOnline: true,
  isUpload: true
}), new ParamsListItem({
  name: "Attachment",
  params: paramsAttachment,
  isOnline: true
})];
exports.paramsList = paramsList;