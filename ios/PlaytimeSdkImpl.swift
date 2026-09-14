import Foundation
import React
import PlaytimeSDK

@objc public class PlaytimeSdkImpl: NSObject {

  @objc public func initPlaytime(_ options: NSDictionary,
                                 resolve: @escaping RCTPromiseResolveBlock,
                                 reject: @escaping RCTPromiseRejectBlock) {
    Task {
      let initOptions: InitOptions
      do {
        initOptions = try RNPlaytimeHelpers.initOptions(from: options as! [AnyHashable: Any])
      } catch {
        ErrorsUtil.reject(reject, context: "Invalid parameters for initPlaytime", error)
        return
      }

      do {
        try await Playtime.initPlaytime(options: initOptions)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Error initializing SDK", error)
      }
    }
  }

  @objc public func showCatalog(_ options: NSDictionary,
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock) {
    Task {
      let catalogOptions: CatalogOptions
      do {
        catalogOptions = try CatalogOptions(JSONObject: options as! [AnyHashable: Any])
      } catch {
        ErrorsUtil.reject(reject, context: "Invalid parameters for showCatalog", error)
        return
      }

      do {
        try await Playtime.showCatalog(catalogOptions: catalogOptions)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Playtime catalog error", error)
      }
    }
  }

  @objc public func getStatus(_ resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock) {
    Task {
      do {
        let response = try await Playtime.getStatus()
        resolve(response.toJSONObject())
      } catch {
        ErrorsUtil.reject(reject, context: "Error getting status", error)
      }
    }
  }

  @objc public func teardown(_ resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock) {
    Task {
      do {
        try await Playtime.teardown()
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Error deinitializing SDK", error)
      }
    }
  }

  @objc public func executeImpressionEvent(_ catalogPlacement: NSDictionary,
                                           resolve: @escaping RCTPromiseResolveBlock,
                                           reject: @escaping RCTPromiseRejectBlock) {
    Task {
      let placement: CatalogPlacement
      do {
        placement = try CatalogPlacement(JSONObject: catalogPlacement as! [AnyHashable: Any])
      } catch {
        ErrorsUtil.reject(reject, context: "Invalid parameters for executeImpressionEvent", error)
        return
      }

      do {
        try await Playtime.executeImpressionEvent(catalogPlacement: placement)
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Error executing impression event", error)
      }
    }
  }
}
