/**
 * The parameters related to User acquisition metadata.
 */
export interface PlaytimeParams {
    /**
     * User acquisition network where the user came from.
     */
    uaNetwork?: string | null;

    /**
     * User acquisition channel where the user came from; e.g. video, offerwall.
     */
    uaChannel?: string | null;

    /**
     * User acquisition sub ID in encrypted form where the user came from.
     */
    uaSubPublisherEncrypted?: string | null;

    /**
     * User acquisition sub ID in clear text where the user came from.
     */
    uaSubPublisherCleartext?: string | null;

    /**
     * The placement of the Playtime experience; e.g. “home screen”, “more options menu”.
     */
    placement?: string | null;

    /**
     * Promotion tag to trigger user-level promotions.
     */
    promotionTag?: string | null;
}
