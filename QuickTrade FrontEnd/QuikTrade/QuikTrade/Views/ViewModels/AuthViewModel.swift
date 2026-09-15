// AuthViewModel.swift — app-wide authentication state, shared via @EnvironmentObject.
//
// Holds the JWT in Keychain (not @AppStorage) and gates ContentView's tab bar
// behind a login/signup flow.

import Foundation

@MainActor
final class AuthViewModel: ObservableObject {

    @Published var isAuthenticated: Bool
    @Published var email: String?
    @Published var errorMessage: String?
    @Published var isLoading = false

    init() {
        isAuthenticated = KeychainHelper.loadToken() != nil
    }

    func register(email: String, password: String) async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }
        do {
            let response = try await APIService.shared.register(email: email, password: password)
            KeychainHelper.save(token: response.token)
            self.email = response.email
            isAuthenticated = true
        } catch {
            errorMessage = (error as? LocalizedError)?.errorDescription ?? "Registration failed."
        }
    }

    func login(email: String, password: String) async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }
        do {
            let response = try await APIService.shared.login(email: email, password: password)
            KeychainHelper.save(token: response.token)
            self.email = response.email
            isAuthenticated = true
        } catch {
            errorMessage = (error as? LocalizedError)?.errorDescription ?? "Invalid email or password."
        }
    }

    func logout() {
        KeychainHelper.deleteToken()
        email = nil
        isAuthenticated = false
    }
}
