// LearningModels.swift — Codable structs for the Learn module endpoints.
//
// Maps to the JSON returned by `GET /api/learn/definitions` and
// `GET /api/learn/quiz`.  Also provides `QuizAttempt` for tracking the
// user's progress locally via @AppStorage.

import Foundation

// MARK: - Definition

/// A financial glossary entry returned by `GET /api/learn/definitions`.
struct Definition: Codable, Identifiable {
    var id: String { term }
    let term: String
    let definition: String
    let example: String
    let application: String
    /// "beginner", "intermediate", or "advanced".
    let difficulty: String
}

// MARK: - Quiz

/// A multiple-choice quiz question returned by `GET /api/learn/quiz`.
struct QuizQuestion: Codable, Identifiable {
    var id: String { question }
    let question: String
    /// Exactly four answer strings.
    let options: [String]
    /// Zero-based index of the correct answer in `options`.
    let correctIndex: Int
    /// Explanation shown after the user answers.
    let explanation: String
    /// The glossary term this question tests.
    let relatedTerm: String
    let difficulty: String
}

// MARK: - Quiz attempt

/// Persisted record of one quiz session stored in UserDefaults via @AppStorage.
/// Encode/decode with `JSONEncoder`/`JSONDecoder`.
struct QuizAttempt: Codable {
    let difficulty: String
    let score: Int
    let totalQuestions: Int
    let date: Date

    /// Convenience initialiser.
    init(difficulty: String, score: Int, total: Int) {
        self.difficulty = difficulty
        self.score = score
        self.totalQuestions = total
        self.date = .now
    }

    /// Fraction correct, 0.0 – 1.0.
    var fraction: Double { totalQuestions > 0 ? Double(score) / Double(totalQuestions) : 0 }

    /// Human-readable result, e.g. "4 / 5".
    var displayScore: String { "\(score) / \(totalQuestions)" }
}
