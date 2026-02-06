/**
 * The gender of the user.
 */
export type PlaytimeGender = 'male' | 'female' | 'unknown';

/**
 * Additional user information.
 */
export interface PlaytimeUserProfile {
    /**
     * User gender. Valid options are male, female, unknown.
     */
    gender?: PlaytimeGender | null;

    /**
     * ISO8601 timestamp designating user’s birthday, e.g. 2025-06-26T14:45:30.123Z.
     */
    birthday?: string | null;
}
