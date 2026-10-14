import Foundation
import React
import PlaytimeSDK

@objc public class PlaytimeSdkImpl: NSObject {

  /// Keeps the registered listener alive while the SDK holds it weakly.
  private var eventListener: AnyObject?

  /// Set by the Objective-C module to forward Playtime SDK events to the bridge's event
  /// emitter as a device event. A publisher's JS listener object cannot cross the bridge,
  /// so delivery goes through this closure instead.
  @objc public var onEvent: ((NSDictionary) -> Void)?

  fileprivate static let isoFormatter: ISO8601DateFormatter = {
    let formatter = ISO8601DateFormatter()
    formatter.formatOptions = [.withInternetDateTime]
    return formatter
  }()

  /// Initialize the Playtime SDK with the given options.
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

  /// Present a screen view with the Playtime catalog.
  /// - Parameters:
  ///   - catalogOptions: Options for the catalog request.
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

  /// Get the status of the SDK.
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

  /// Deinitialize the SDK. This releases the resources taken up by internal components.
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

  /// Records that a catalog entry point became visible. This mandatory event is used for analytics to understand conversion rates from catalog visibility to catalog opens. Pass the placement for the entry point that is visible; use different placements when the app has multiple catalog entry points.
  /// - Parameters:
  ///   - catalogPlacement: The placement for the visible catalog entry point.
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

  /// Register a listener to receive Playtime SDK events.
  ///
  /// A publisher's JS listener object cannot cross the bridge, so events are delivered as
  /// device events instead: the registered proxy forwards each event to `onEvent`, which the
  /// Objective-C module wires to the bridge's event emitter. The JS NativeEventEmitter then
  /// relays it to the publisher's `didReceive` callback.
  @objc public func addEventListener(_ resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock) {
    Task { @MainActor in
      let listener = PlaytimeEventListenerProxy { [weak self] payload in
        self?.onEvent?(payload)
      }

      do {
        try await Playtime.addEventListener(eventListener: listener)
        self.eventListener = listener
        resolve(nil)
      } catch {
        self.eventListener = nil
        ErrorsUtil.reject(reject, context: "Error adding event listener", error)
      }
    }
  }

  /// Remove the currently registered Playtime SDK event listener.
  @objc public func removeEventListener(_ resolve: @escaping RCTPromiseResolveBlock,
                                        reject: @escaping RCTPromiseRejectBlock) {
    Task {
      do {
        try await Playtime.removeEventListener()
        self.eventListener = nil
        resolve(nil)
      } catch {
        ErrorsUtil.reject(reject, context: "Error removing event listener", error)
      }
    }
  }
}

@MainActor
private final class PlaytimeEventListenerProxy: NSObject, PlaytimeEventListener {

  private let handler: (NSDictionary) -> Void

  init(handler: @escaping (NSDictionary) -> Void) {
    self.handler = handler
    super.init()
  }

  func didReceive(_ event: PlaytimeEvent) {
    let payload: NSDictionary = [
      "name": event.name,
      "occurredAt": PlaytimeSdkImpl.isoFormatter.string(from: event.occurredAt)
    ]
    handler(payload)
  }
}
