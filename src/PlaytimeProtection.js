import PlaytimeProtectionSpec from './specs/NativePlaytimeProtection';
import { wrapPlaytimePromise } from './PlaytimeException';

export const FaceVerificationResult = Object.freeze({
  SUCCESS: 'SUCCESS',
  ALREADY_VERIFIED: 'ALREADY_VERIFIED',
  CANCELLED: 'CANCELLED',
  NOT_INITIALIZED: 'NOT_INITIALIZED',
  TOS_NOT_ACCEPTED: 'TOS_NOT_ACCEPTED',
  LIVENESS_CHECK_FAILED: 'LIVENESS_CHECK_FAILED',
  PENDING_REVIEW: 'PENDING_REVIEW',
  MAX_ATTEMPTS_REACHED: 'MAX_ATTEMPTS_REACHED',
});

export const FaceVerificationStatus = Object.freeze({
  VERIFIED: 'VERIFIED',
  NOT_VERIFIED: 'NOT_VERIFIED',
  NOT_INITIALIZED: 'NOT_INITIALIZED',
  TOS_NOT_ACCEPTED: 'TOS_NOT_ACCEPTED',
  PENDING_REVIEW: 'PENDING_REVIEW',
  MAX_ATTEMPTS_REACHED: 'MAX_ATTEMPTS_REACHED',
});

const PlaytimeProtection = {
  /**
   * @deprecated faceVerification is deprecated.
   */
  faceVerification: function() {
    return wrapPlaytimePromise(PlaytimeProtectionSpec.faceVerification());
  },

  /**
   * @deprecated faceVerificationStatus is deprecated.
   */
  faceVerificationStatus: function() {
    return wrapPlaytimePromise(PlaytimeProtectionSpec.faceVerificationStatus());
  },
};

export default PlaytimeProtection;
