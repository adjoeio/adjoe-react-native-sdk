import Foundation
import React
import PlaytimeSDK

@objc public class PlaytimeStudioImpl: NSObject {

  @objc public func updateTosConsent(_ action: Double,
                                     resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock) {
    Task {
      let consentAction: TosConsentAction = (Int(action) == 1) ? .withdraw : .accept

      do {
        try await PlaytimeStudio.updateTosConsent(action: consentAction)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Error updating terms of service consent", error)
      }
    }
  }

  @objc public func getCampaigns(_ options: NSDictionary,
                                 resolve: @escaping RCTPromiseResolveBlock,
                                 reject: @escaping RCTPromiseRejectBlock) {
    Task {
      let campaignOptions: CampaignOptions

      do {
        campaignOptions = try CampaignOptions(JSONObject: options as! [AnyHashable: Any])
      } catch {
        ErrorsUtil.reject(reject, context: "Invalid parameters for getCampaigns", error)
        return
      }

      do {
        let response = try await PlaytimeStudio.getCampaigns(campaignOptions: campaignOptions)
        resolve(response.toJSONObject())
      } catch {
        ErrorsUtil.reject(reject, context: "Error getting campaigns", error)
      }
    }
  }

  @objc public func getOfferDetails(_ tokens: [String],
                                    campaignOptions options: NSDictionary,
                                    resolve: @escaping RCTPromiseResolveBlock,
                                    reject: @escaping RCTPromiseRejectBlock) {
    Task {
      let campaignOptions: CampaignOptions

      do {
        campaignOptions = try CampaignOptions(JSONObject: options as! [AnyHashable: Any])
      } catch {
        ErrorsUtil.reject(reject, context: "Invalid parameters for getOfferDetails", error)
        return
      }

      if tokens.isEmpty {
        ErrorsUtil.reject(reject, function: "getOfferDetails", reason: "tokens array is required")
        return
      }

      do {
        let response = try await PlaytimeStudio.getOfferDetails(tokens: tokens, campaignOptions: campaignOptions)
        resolve(response.toJSONObject())
      } catch {
        ErrorsUtil.reject(reject, context: "Error getting offer details", error)
      }
    }
  }

  @objc public func getInstalledCampaigns(_ options: NSDictionary,
                                          resolve: @escaping RCTPromiseResolveBlock,
                                          reject: @escaping RCTPromiseRejectBlock) {
    Task {
      do {
        let installedOptions = try InstalledCampaignOptions(JSONObject: options as! [AnyHashable: Any])
        let response = try await PlaytimeStudio.getInstalledCampaigns(options: installedOptions)
        resolve(response.toJSONObject())
      } catch {
        ErrorsUtil.reject(reject, context: "Error getting installed campaigns", error)
      }
    }
  }

  @objc public func openInStore(_ campaignDictionary: NSDictionary,
                                useInlineInstall: Bool,
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock) {
    Task {
      let campaign: PlaytimeCampaign

      do {
        campaign = try PlaytimeCampaign(JSONObject: campaignDictionary as! [AnyHashable: Any])
      } catch {
        ErrorsUtil.reject(reject, context: "Invalid parameters for openInStore", error)
        return
      }

      do {
        try await PlaytimeStudio.openInStore(campaign: campaign, useInlineInstall: useInlineInstall)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Open store error", error)
      }
    }
  }

  @objc public func openInstalledCampaign(_ campaignDictionary: NSDictionary,
                                          resolve: @escaping RCTPromiseResolveBlock,
                                          reject: @escaping RCTPromiseRejectBlock) {
    Task {
      let campaign: PlaytimeCampaign

      do {
        campaign = try PlaytimeCampaign(JSONObject: campaignDictionary as! [AnyHashable: Any])
      } catch {
        ErrorsUtil.reject(reject, context: "Invalid parameters for openInstalledCampaign", error)
        return
      }

      do {
        try await PlaytimeStudio.openInstalledCampaign(campaign: campaign)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Open installed campaign error", error)
      }
    }
  }

  @objc public func openChatbot(_ campaignDictionary: NSDictionary?,
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock) {
    Task {
      var campaign: PlaytimeCampaign?

      if let campaignDictionary = campaignDictionary {
        do {
          campaign = try PlaytimeCampaign(JSONObject: campaignDictionary as! [AnyHashable: Any])
        } catch {
          ErrorsUtil.reject(reject, context: "Invalid parameters for openChatbot", error)
          return
        }
      }

      let resolvedCampaign = campaign

      do {
        try await PlaytimeStudio.openChatbot(campaign: resolvedCampaign)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Open chatbot error", error)
      }
    }
  }

  @objc public func resetRewardsConnect(_ resolve: @escaping RCTPromiseResolveBlock,
                                        reject: @escaping RCTPromiseRejectBlock) {
    Task {
      do {
        try await PlaytimeStudio.resetRewardsConnect()
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Error resetting rewards connect", error)
      }
    }
  }

  @objc public func executeCampaignImpression(_ campaignDictionary: NSDictionary,
                                              impressionType: Double,
                                              resolve: @escaping RCTPromiseResolveBlock,
                                              reject: @escaping RCTPromiseRejectBlock) {
    Task {
      let campaign: PlaytimeCampaign

      do {
        campaign = try PlaytimeCampaign(JSONObject: campaignDictionary as! [AnyHashable: Any])
      } catch {
        ErrorsUtil.reject(reject, context: "Invalid parameters for executeCampaignImpression", error)
        return
      }

      let type: PlaytimeImpressionType = (Int(impressionType) == 1) ? .engaged : .default

      do {
        try await PlaytimeStudio.executeCampaignImpression(campaign: campaign, impressionType: type)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Execute campaign impression error", error)
      }
    }
  }

  @objc public func executeOfferImpression(_ token: String,
                                           appID: String,
                                           resolve: @escaping RCTPromiseResolveBlock,
                                           reject: @escaping RCTPromiseRejectBlock) {
    Task {
      do {
        try await PlaytimeStudio.executeOfferImpression(token: token, appID: appID)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Error executing offer impression", error)
      }
    }
  }

  @objc public func registerRewardsConnect(_ resolve: @escaping RCTPromiseResolveBlock,
                                           reject: @escaping RCTPromiseRejectBlock) {
    Task {
      do {
        try await PlaytimeStudio.registerRewardsConnect()
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Error registering for rewards connect", error)
      }
    }
  }

  @objc public func showCampaignDetails(_ campaignDictionary: NSDictionary,
                                        resolve: @escaping RCTPromiseResolveBlock,
                                        reject: @escaping RCTPromiseRejectBlock) {
    Task {
      let campaign: PlaytimeCampaign

      do {
        campaign = try PlaytimeCampaign(JSONObject: campaignDictionary as! [AnyHashable: Any])
      } catch {
        ErrorsUtil.reject(reject, context: "Invalid parameters for showCampaignDetails", error)
        return
      }

      do {
        try await PlaytimeStudio.showCampaignDetails(campaign: campaign)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Showing campaign details error", error)
      }
    }
  }

  @objc public func showOfferDetails(_ token: String,
                                     appID: String,
                                     resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock) {
    Task {
      do {
        try await PlaytimeStudio.showOfferDetails(token: token, appID: appID)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Showing offer details error", error)
      }
    }
  }

  @objc public func showInstalledCampaigns(_ resolve: @escaping RCTPromiseResolveBlock,
                                           reject: @escaping RCTPromiseRejectBlock) {
    Task {
      do {
        try await PlaytimeStudio.showInstalledCampaigns()
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Error showing installed campaigns", error)
      }
    }
  }

  @objc public func showPermissionsPrompt(_ resolve: @escaping RCTPromiseResolveBlock,
                                          reject: @escaping RCTPromiseRejectBlock) {
    ErrorsUtil.rejectUnsupported("showPermissionsPrompt", reject)
  }
}
