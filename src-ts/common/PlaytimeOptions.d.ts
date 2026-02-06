import { PlaytimeExtension } from "./PlaytimeExtension";
import { PlaytimeParams } from "./PlaytimeParams";
import { PlaytimeUserProfile } from "./PlaytimeUserProfile";

/**
 * The options passed to Playtime methods.
 */
export interface PlaytimeOptions {
    /**
     * A custom identifier you must assign to each user.
     */
    userId?: string | null;

    /**
     * The SDK hash for initialization.
     */
    sdkHash?: string | null;

    /**
     * User profile for targeting.
     */
    userProfile?: PlaytimeUserProfile | null;

    /**
     * User acquisition parameters.
     */
    params?: PlaytimeParams | null;

    /**
     * Extension IDs visible in S2S payouts.
     */
    extensions?: PlaytimeExtension | null;

    /**
     * A list of tokens.
     */
    tokens?: string[] | null;
}
