// QuizView.swift — interactive multiple-choice quiz for the Learn module.
//
// Fetches 5 MCQ questions from /api/learn/quiz, presents them one at a time,
// shows a per-question explanation after answering, and saves the final score
// to @AppStorage so the Settings tab can display a progress badge.

import SwiftUI

// MARK: - QuizView

/// Full-screen quiz experience loaded from the QuickTrade Learn API.
struct QuizView: View {

    let difficulty: String

    // ── State ──────────────────────────────────────────────────────────────
    @State private var questions: [QuizQuestion] = []
    @State private var currentIndex = 0
    @State private var selectedOption: Int? = nil
    @State private var isAnswered = false
    @State private var score = 0
    @State private var isFinished = false
    @State private var isLoading = true

    /// @AppStorage key includes difficulty so each level has its own progress.
    @AppStorage private var bestScore: Int
    @Environment(\.dismiss) private var dismiss

    init(difficulty: String) {
        self.difficulty = difficulty
        self._bestScore = AppStorage(wrappedValue: 0, "quiz_best_\(difficulty.lowercased())")
    }

    // ── Body ───────────────────────────────────────────────────────────────

    var body: some View {
        NavigationView {
            Group {
                if isLoading {
                    ProgressView("Loading quiz…")
                } else if isFinished {
                    ResultView(score: score, total: questions.count,
                               difficulty: difficulty, onRestart: restart)
                } else if questions.isEmpty {
                    Text("No questions available for \(difficulty).")
                        .foregroundColor(.secondary)
                } else {
                    QuestionCard(
                        question: questions[currentIndex],
                        questionNumber: currentIndex + 1,
                        total: questions.count,
                        selectedOption: $selectedOption,
                        isAnswered: $isAnswered,
                        onAnswer: { idx in
                            selectedOption = idx
                            isAnswered = true
                            if idx == questions[currentIndex].correctIndex {
                                score += 1
                            }
                        },
                        onNext: advance
                    )
                }
            }
            .navigationTitle("\(difficulty) Quiz")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Close") { dismiss() }
                }
            }
            .task { await loadQuiz() }
        }
    }

    // MARK: Helpers

    private func loadQuiz() async {
        isLoading = true
        questions = (try? await APIService.shared.fetchQuiz(difficulty: difficulty, count: 5)) ?? []
        isLoading = false
    }

    private func advance() {
        selectedOption = nil
        isAnswered = false
        if currentIndex < questions.count - 1 {
            currentIndex += 1
        } else {
            isFinished = true
            if score > bestScore { bestScore = score }
        }
    }

    private func restart() {
        score = 0
        currentIndex = 0
        selectedOption = nil
        isAnswered = false
        isFinished = false
        Task { await loadQuiz() }
    }
}

// MARK: - QuestionCard

/// One multiple-choice question card.
private struct QuestionCard: View {
    let question: QuizQuestion
    let questionNumber: Int
    let total: Int
    @Binding var selectedOption: Int?
    @Binding var isAnswered: Bool
    let onAnswer: (Int) -> Void
    let onNext: () -> Void

    var body: some View {
        VStack(spacing: 20) {

            // Progress bar
            ProgressView(value: Double(questionNumber - 1), total: Double(total))
                .padding(.horizontal)

            Text("Question \(questionNumber) of \(total)")
                .font(.caption).foregroundColor(.secondary)

            // Question text
            Text(question.question)
                .font(.title3).bold()
                .multilineTextAlignment(.center)
                .padding(.horizontal)

            // Answer options
            VStack(spacing: 12) {
                ForEach(question.options.indices, id: \.self) { idx in
                    OptionButton(
                        text: question.options[idx],
                        state: optionState(for: idx),
                        isDisabled: isAnswered
                    ) {
                        onAnswer(idx)
                    }
                }
            }
            .padding(.horizontal)

            // Explanation (shown after answering)
            if isAnswered {
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Image(systemName: selectedOption == question.correctIndex
                              ? "checkmark.circle.fill" : "xmark.circle.fill")
                            .foregroundColor(selectedOption == question.correctIndex ? .green : .red)
                        Text(selectedOption == question.correctIndex ? "Correct!" : "Incorrect")
                            .font(.headline)
                            .foregroundColor(selectedOption == question.correctIndex ? .green : .red)
                    }
                    Text(question.explanation)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(12)
                .padding(.horizontal)

                Button("Next →", action: onNext)
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.black)
                    .cornerRadius(12)
                    .padding(.horizontal)
            }

            Spacer()
        }
        .padding(.top)
    }

    private func optionState(for index: Int) -> OptionState {
        guard isAnswered else { return .normal }
        if index == question.correctIndex { return .correct }
        if index == selectedOption { return .incorrect }
        return .normal
    }
}

// MARK: - OptionButton

private enum OptionState { case normal, correct, incorrect }

private struct OptionButton: View {
    let text: String
    let state: OptionState
    let isDisabled: Bool
    let action: () -> Void

    private var background: Color {
        switch state {
        case .correct:   return .green.opacity(0.2)
        case .incorrect: return .red.opacity(0.2)
        case .normal:    return Color(.systemGray6)
        }
    }

    private var borderColor: Color {
        switch state {
        case .correct:   return .green
        case .incorrect: return .red
        case .normal:    return .clear
        }
    }

    var body: some View {
        Button(action: action) {
            Text(text)
                .font(.subheadline)
                .multilineTextAlignment(.leading)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
                .background(background)
                .cornerRadius(10)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(borderColor, lineWidth: 2)
                )
        }
        .disabled(isDisabled)
        .foregroundColor(.primary)
    }
}

// MARK: - ResultView

private struct ResultView: View {
    let score: Int
    let total: Int
    let difficulty: String
    let onRestart: () -> Void
    @Environment(\.dismiss) private var dismiss

    private var fraction: Double { total > 0 ? Double(score) / Double(total) : 0 }
    private var emoji: String {
        if fraction >= 0.8 { return "🎉" }
        if fraction >= 0.6 { return "👍" }
        return "📚"
    }

    var body: some View {
        VStack(spacing: 24) {
            Text(emoji).font(.system(size: 64))
            Text("Quiz Complete!").font(.title).bold()
            Text("\(score) / \(total) correct")
                .font(.title2).foregroundColor(fraction >= 0.6 ? .green : .orange)
            Text(fraction >= 0.8 ? "Excellent work!" :
                 fraction >= 0.6 ? "Good job! Keep practising." :
                 "Keep studying — you've got this!")
                .font(.subheadline).foregroundColor(.secondary)
                .multilineTextAlignment(.center)

            Button("Try Again") { onRestart() }
                .font(.headline).foregroundColor(.white)
                .frame(maxWidth: .infinity).padding()
                .background(Color.black).cornerRadius(12)
                .padding(.horizontal)

            Button("Done") { dismiss() }
                .foregroundColor(.secondary)
        }
        .padding()
    }
}
