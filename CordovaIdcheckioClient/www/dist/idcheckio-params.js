"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.CISType = exports.FileSize = exports.FeedbackLevel = exports.Language = exports.CheckType = exports.FaceDetection = exports.Codeline = exports.ScanBothSides = exports.ConfirmationType = exports.IDCheckioOrientation = exports.Environment = exports.DocumentType = exports.OnlineConfig = exports.IntegrityCheck = exports.Extraction = exports.IDCheckioParamsBuilder = void 0;

function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var IDCheckioParams = function IDCheckioParams() {
  var _ref = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {},
      docType = _ref.docType,
      orientation = _ref.orientation,
      confirmationType = _ref.confirmationType,
      useHd = _ref.useHd,
      integrityCheck = _ref.integrityCheck,
      scanBothSides = _ref.scanBothSides,
      sideOneExtraction = _ref.sideOneExtraction,
      sideTwoExtraction = _ref.sideTwoExtraction,
      language = _ref.language,
      manualButtonTimer = _ref.manualButtonTimer,
      feedbackLevel = _ref.feedbackLevel,
      adjustCrop = _ref.adjustCrop,
      maxPictureFilesize = _ref.maxPictureFilesize,
      token = _ref.token,
      confirmAbort = _ref.confirmAbort,
      onlineConfig = _ref.onlineConfig;

  _classCallCheck(this, IDCheckioParams);

  this.docType = docType;
  this.orientation = orientation;
  this.confirmationType = confirmationType;
  this.useHd = useHd;
  this.integrityCheck = integrityCheck;
  this.scanBothSides = scanBothSides;
  this.sideOneExtraction = sideOneExtraction;
  this.sideTwoExtraction = sideTwoExtraction;
  this.language = language;
  this.manualButtonTimer = manualButtonTimer;
  this.feedbackLevel = feedbackLevel;
  this.adjustCrop = adjustCrop;
  this.maxPictureFilesize = maxPictureFilesize;
  this.token = token;
  this.confirmAbort = confirmAbort;
  this.onlineConfig = onlineConfig;
};

var IDCheckioParamsBuilder = /*#__PURE__*/function () {
  function IDCheckioParamsBuilder() {
    _classCallCheck(this, IDCheckioParamsBuilder);
  }

  _createClass(IDCheckioParamsBuilder, [{
    key: "setDocType",
    value: function setDocType(docType) {
      if (Object.values(DocumentType).includes(docType)) {
        this.docType = docType;
        return this;
      } else {
        throw new Error("DocumentType value is incorrect");
      }
    }
  }, {
    key: "setOrientation",
    value: function setOrientation(orientation) {
      if (Object.values(IDCheckioOrientation).includes(orientation)) {
        this.orientation = orientation;
        return this;
      } else {
        throw new Error("IDCheckioOrientation value is incorrect");
      }
    }
  }, {
    key: "setConfirmationType",
    value: function setConfirmationType(confirmationType) {
      if (Object.values(ConfirmationType).includes(confirmationType)) {
        this.confirmationType = confirmationType;
        return this;
      } else {
        throw new Error("ConfirmationType value is incorrect");
      }
    }
  }, {
    key: "setUseHd",
    value: function setUseHd(useHd) {
      if (typeof useHd == "boolean") {
        this.useHd = useHd;
        return this;
      } else {
        throw new Error("useHd must be a boolean");
      }
    }
  }, {
    key: "setIntegrityCheck",
    value: function setIntegrityCheck(integrityCheck) {
      if (_typeof(integrityCheck) == "object") {
        this.integrityCheck = integrityCheck;
        return this;
      } else {
        throw new Error("integrityCheck must be an IntegrityCheck");
      }
    }
  }, {
    key: "setScanBothSides",
    value: function setScanBothSides(scanBothSides) {
      if (Object.values(ScanBothSides).includes(scanBothSides)) {
        this.scanBothSides = scanBothSides;
        return this;
      } else {
        throw new Error("ScanBothSides value is incorrect");
      }
    }
  }, {
    key: "setSideOneExtraction",
    value: function setSideOneExtraction(sideOneExtraction) {
      if (_typeof(sideOneExtraction) == "object") {
        this.sideOneExtraction = sideOneExtraction;
        return this;
      } else {
        throw new Error("sideOneExtraction must be an Extraction ");
      }
    }
  }, {
    key: "setSideTwoExtraction",
    value: function setSideTwoExtraction(sideTwoExtraction) {
      if (_typeof(sideTwoExtraction) == "object") {
        this.sideTwoExtraction = sideTwoExtraction;
        return this;
      } else {
        throw new Error("sideTwoExtraction must be an Extraction ");
      }
    }
  }, {
    key: "setLanguage",
    value: function setLanguage(language) {
      if (Object.values(Language).includes(language)) {
        this.language = language;
        return this;
      } else {
        throw new Error("Language value is incorrect");
      }
    }
  }, {
    key: "setManualButtonTimer",
    value: function setManualButtonTimer(manualButtonTimer) {
      if (typeof manualButtonTimer == "number") {
        this.manualButtonTimer = manualButtonTimer;
        return this;
      } else {
        throw new Error("manualButtonTimer must be a number");
      }
    }
  }, {
    key: "setFeedbackLevel",
    value: function setFeedbackLevel(feedbackLevel) {
      if (Object.values(FeedbackLevel).includes(feedbackLevel)) {
        this.feedbackLevel = feedbackLevel;
        return this;
      } else {
        throw new Error("FeedbackLevel value is incorrect");
      }
    }
  }, {
    key: "setAdjustCrop",
    value: function setAdjustCrop(adjustCrop) {
      if (typeof adjustCrop == "boolean") {
        this.adjustCrop = adjustCrop;
        return this;
      } else {
        throw new Error("adjustCrop must be a boolean");
      }
    }
  }, {
    key: "setMaxPictureFilesize",
    value: function setMaxPictureFilesize(maxPictureFilesize) {
      if (Object.values(FileSize).includes(maxPictureFilesize)) {
        this.maxPictureFilesize = maxPictureFilesize;
        return this;
      } else {
        throw new Error("FileSize value is incorrect");
      }
    }
  }, {
    key: "setToken",
    value: function setToken(token) {
      if (typeof token == "string") {
        this.token = token;
        return this;
      } else {
        throw new Error("token must be a string");
      }
    }
  }, {
    key: "setConfirmAbort",
    value: function setConfirmAbort(confirmAbort) {
      if (typeof confirmAbort == "boolean") {
        this.confirmAbort = confirmAbort;
        return this;
      } else {
        throw new Error("confirmAbort must be a boolean");
      }
    }
  }, {
    key: "setOnlineConfig",
    value: function setOnlineConfig(onlineConfig) {
      if (_typeof(onlineConfig) == "object") {
        this.onlineConfig = onlineConfig;
        return this;
      } else {
        throw new Error("onlineConfig must be an Extraction ");
      }
    }
  }, {
    key: "build",
    value: function build() {
      return new IDCheckioParams({
        docType: this.docType,
        adjustCrop: this.adjustCrop,
        confirmAbort: this.confirmAbort,
        feedbackLevel: this.feedbackLevel,
        confirmationType: this.confirmationType,
        integrityCheck: this.integrityCheck,
        language: this.language,
        manualButtonTimer: this.manualButtonTimer,
        maxPictureFilesize: this.maxPictureFilesize,
        onlineConfig: this.onlineConfig,
        orientation: this.orientation,
        scanBothSides: this.scanBothSides,
        sideOneExtraction: this.sideOneExtraction,
        sideTwoExtraction: this.sideTwoExtraction,
        token: this.token,
        useHd: this.useHd
      });
    }
  }]);

  return IDCheckioParamsBuilder;
}();

exports.IDCheckioParamsBuilder = IDCheckioParamsBuilder;

var Extraction = function Extraction(codeline, faceDetection) {
  _classCallCheck(this, Extraction);

  if (Object.values(Codeline).includes(codeline)) {
    this.codeline = codeline;
  } else {
    throw new Error("Codeline value is incorrect");
  }

  if (Object.values(FaceDetection).includes(faceDetection)) {
    this.faceDetection = faceDetection;
  } else {
    throw new Error("FaceDetection value is incorrect");
  }
};

exports.Extraction = Extraction;

var IntegrityCheck = function IntegrityCheck(readEmrtd) {
  _classCallCheck(this, IntegrityCheck);

  if (typeof readEmrtd == "boolean") {
    this.readEmrtd = readEmrtd;
  } else {
    throw new Error("readEmrtd must be a boolean");
  }
};

exports.IntegrityCheck = IntegrityCheck;

var OnlineConfig = function OnlineConfig() {
  var _ref2 = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {},
      _ref2$isReferenceDocu = _ref2.isReferenceDocument,
      isReferenceDocument = _ref2$isReferenceDocu === void 0 ? false : _ref2$isReferenceDocu,
      _ref2$checkType = _ref2.checkType,
      checkType = _ref2$checkType === void 0 ? CheckType.CHECK_FULL : _ref2$checkType,
      _ref2$cisType = _ref2.cisType,
      cisType = _ref2$cisType === void 0 ? null : _ref2$cisType,
      _ref2$folderUid = _ref2.folderUid,
      folderUid = _ref2$folderUid === void 0 ? null : _ref2$folderUid,
      _ref2$biometricConsen = _ref2.biometricConsent,
      biometricConsent = _ref2$biometricConsen === void 0 ? null : _ref2$biometricConsen,
      _ref2$enableManualAna = _ref2.enableManualAnalysis,
      enableManualAnalysis = _ref2$enableManualAna === void 0 ? false : _ref2$enableManualAna;

  _classCallCheck(this, OnlineConfig);

  this.isReferenceDocument = isReferenceDocument;
  this.checkType = checkType;
  this.cisType = cisType;
  this.folderUid = folderUid;
  this.biometricConsent = biometricConsent;
  this.enableManualAnalysis = enableManualAnalysis;
};
/*
 * Enumeration
 */


exports.OnlineConfig = OnlineConfig;
var DocumentType = {
  DISABLED: "DISABLED",
  ID: "ID",
  LIVENESS: "LIVENESS",
  A4: "A4",
  FRENCH_HEALTH_CARD: "FRENCH_HEALTH_CARD",
  BANK_CHECK: "BANK_CHECK",
  OLD_DL_FR: "OLD_DL_FR",
  PHOTO: "PHOTO",
  VEHICLE_REGISTRATION: "VEHICLE_REGISTRATION",
  SELFIE: "SELFIE"
};
exports.DocumentType = DocumentType;
var Environment = {
  DEMO: "DEMO",
  PROD: "PROD"
};
exports.Environment = Environment;
var IDCheckioOrientation = {
  PORTRAIT: "PORTRAIT",
  LANDSCAPE: "LANDSCAPE"
};
exports.IDCheckioOrientation = IDCheckioOrientation;
var ConfirmationType = {
  DATA_OR_PICTURE: "DATA_OR_PICTURE",
  CROPPED_PICTURE: "CROPPED_PICTURE",
  NONE: "NONE"
};
exports.ConfirmationType = ConfirmationType;
var ScanBothSides = {
  ENABLED: "ENABLED",
  FORCED: "FORCED",
  DISABLED: "DISABLED"
};
exports.ScanBothSides = ScanBothSides;
var Codeline = {
  DISABLED: "DISABLED",
  ANY: "ANY",
  DECODED: "DECODED",
  VALID: "VALID",
  REJECT: "REJECT"
};
exports.Codeline = Codeline;
var FaceDetection = {
  ENABLED: "ENABLED",
  DISABLED: "DISABLED"
};
exports.FaceDetection = FaceDetection;
var CheckType = {
  CHECK_FULL: "CHECK_FULL",
  CHECK_FAST: "CHECK_FAST"
};
exports.CheckType = CheckType;
var Language = {
  fr: "fr",
  en: "en",
  pl: "pl",
  es: "es",
  ro: "ro",
  cs: "cs",
  pt: "pt"
};
exports.Language = Language;
var FeedbackLevel = {
  ALL: "ALL",
  GUIDELINE: "GUIDELINE",
  ERROR: "ERROR"
};
exports.FeedbackLevel = FeedbackLevel;
var FileSize = {
  ONE_MEGA_BYTE: "ONE_MEGA_BYTE",
  TWO_MEGA_BYTES: "TWO_MEGA_BYTES",
  THREE_MEGA_BYTES: "THREE_MEGA_BYTES",
  FOUR_MEGA_BYTES: "FOUR_MEGA_BYTES",
  FIVE_MEGA_BYTES: "FIVE_MEGA_BYTES",
  SIX_MEGA_BYTES: "SIX_MEGA_BYTES",
  SEVEN_MEGA_BYTES: "SEVEN_MEGA_BYTES",
  HEIGHT_MEGA_BYTES: "HEIGHT_MEGA_BYTES"
};
exports.FileSize = FileSize;
var CISType = {
  ID: "ID",
  IBAN: "IBAN",
  CHEQUE: "CHEQUE",
  TAX_SHEET: "TAX_SHEET",
  PAY_SLIP: "PAY_SLIP",
  ADDRESS_PROOF: "ADDRESS_PROOF",
  CREDIT_CARD: "CREDIT_CARD",
  PORTRAIT: "PORTRAIT",
  LEGAL_ENTITY: "LEGAL_ENTITY",
  CAR_REGISTRATION: "CAR_REGISTRATION",
  LIVENESS: "LIVENESS",
  OTHER: "OTHER"
};
exports.CISType = CISType;