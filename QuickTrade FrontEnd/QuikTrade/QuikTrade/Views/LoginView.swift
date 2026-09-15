// LoginView.swift — email/password login and registration, gates ContentView.

import SwiftUI

struct LoginView: View {
    @EnvironmentObject var auth: AuthViewModel

    @State private var email = ""
    @State private var password = ""
    @State private var isSignup = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Spacer()

                Image(systemName: "chart.line.uptrend.xyaxis")
                    .font(.system(size: 48))
                    .foregroundColor(.blue)

                Text("QuikTrade")
                    .font(.largeTitle).bold()

                Text(isSignup ? "Create an account to start paper trading with $100,000 in virtual cash."
                               : "Log in to your paper-trading account.")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)

                VStack(spacing: 12) {
                    TextField("Email", text: $email)
                        .textContentType(.emailAddress)
                        .keyboardType(.emailAddress)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                        .padding()
                        .background(Color(.secondarySystemBackground))
                        .clipShape(RoundedRectangle(cornerRadius: 10))

                    SecureField("Password (min 8 characters)", text: $password)
                        .textContentType(isSignup ? .newPassword : .password)
                        .padding()
                        .background(Color(.secondarySystemBackground))
                        .clipShape(RoundedRectangle(cornerRadius: 10))
                }
                .padding(.horizontal)

                if let error = auth.errorMessage {
                    Text(error).font(.footnote).foregroundColor(.red)
                }

                Button {
                    Task {
                        if isSignup {
                            await auth.register(email: email, password: password)
                        } else {
                            await auth.login(email: email, password: password)
                        }
                    }
                } label: {
                    if auth.isLoading {
                        ProgressView().frame(maxWidth: .infinity)
                    } else {
                        Text(isSignup ? "Sign Up" : "Log In").frame(maxWidth: .infinity)
                    }
                }
                .buttonStyle(.borderedProminent)
                .padding(.horizontal)
                .disabled(email.isEmpty || password.isEmpty || auth.isLoading)

                Button(isSignup ? "Already have an account? Log in" : "New here? Create an account") {
                    isSignup.toggle()
                    auth.errorMessage = nil
                }
                .font(.footnote)

                Spacer()
                Spacer()
            }
        }
    }
}

#Preview {
    LoginView().environmentObject(AuthViewModel())
}
