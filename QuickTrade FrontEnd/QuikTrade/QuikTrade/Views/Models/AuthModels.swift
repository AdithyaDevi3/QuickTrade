// AuthModels.swift — Codable structs for the /api/auth/* endpoints.

import Foundation

struct AuthResponse: Codable {
    let token: String
    let email: String
}
