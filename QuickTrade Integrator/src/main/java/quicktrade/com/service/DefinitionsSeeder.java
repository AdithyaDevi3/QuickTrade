package quicktrade.com.service;

import org.springframework.stereotype.Service;
import quicktrade.com.entity.DefinitionDTO;
import quicktrade.com.entity.QuizQuestionDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DefinitionsSeeder — provides the canonical set of financial term definitions
 * for all three difficulty levels used by the iOS learning module.
 *
 * <p>Definitions are stored in-memory (no database table required) and returned
 * directly by the REST layer.  The full set of 23 terms mirrors the existing
 * iOS SettingsView so both the app glossary and the API stay in sync.</p>
 */
@Service
public class DefinitionsSeeder {

    private static final List<DefinitionDTO> ALL_DEFINITIONS;

    static {
        List<DefinitionDTO> defs = new ArrayList<>();

        // ── Beginner (7 terms) ────────────────────────────────────────────────
        defs.add(new DefinitionDTO(
                "Current Price",
                "The most recent price at which a stock was traded in the market.",
                "Apple (AAPL) is currently trading at $185.40, meaning the last executed trade was at that price.",
                "Used to calculate portfolio value and decide entry/exit points.",
                "beginner"
        ));
        defs.add(new DefinitionDTO(
                "Market Cap",
                "The total market value of all outstanding shares of a company, calculated as share price × total shares.",
                "If a company has 1 billion shares and each costs $50, market cap = $50 billion.",
                "Used to classify companies: Large-cap (>$10B), Mid-cap ($2–10B), Small-cap (<$2B).",
                "beginner"
        ));
        defs.add(new DefinitionDTO(
                "P/E Ratio",
                "Price-to-Earnings ratio. Measures how much investors pay per dollar of earnings.",
                "A stock trading at $100 with EPS of $5 has a P/E of 20 — investors pay $20 per $1 of earnings.",
                "A high P/E can mean growth expectations are priced in; a low P/E may indicate undervaluation.",
                "beginner"
        ));
        defs.add(new DefinitionDTO(
                "EPS",
                "Earnings Per Share — a company's net profit divided by the number of outstanding shares.",
                "If a company earns $1 billion and has 500 million shares, EPS = $2.00.",
                "Rising EPS generally indicates improving profitability and is watched closely around earnings seasons.",
                "beginner"
        ));
        defs.add(new DefinitionDTO(
                "Dividend Yield",
                "Annual dividend payment per share divided by the current share price, expressed as a percentage.",
                "A stock paying $4/year in dividends priced at $100 has a 4% dividend yield.",
                "Higher yield can signal income stability, but unsustainably high yields may warn of a dividend cut.",
                "beginner"
        ));
        defs.add(new DefinitionDTO(
                "52-Week High/Low",
                "The highest and lowest prices at which a stock has traded over the past 52 weeks.",
                "If AAPL traded between $130 and $200 over the past year, those are its 52-week low and high.",
                "Breakouts above the 52-week high often attract momentum traders; drops near lows may signal support.",
                "beginner"
        ));
        defs.add(new DefinitionDTO(
                "Volume",
                "The total number of shares traded during a specific time period.",
                "If 10 million TSLA shares changed hands today, today's volume is 10 million.",
                "Volume spikes often accompany significant price moves and can confirm trend strength.",
                "beginner"
        ));

        // ── Intermediate (8 terms) ────────────────────────────────────────────
        defs.add(new DefinitionDTO(
                "P/S Ratio",
                "Price-to-Sales ratio. Compares a company's market cap to its annual revenue.",
                "A $5B market cap company with $1B in revenue has a P/S of 5.",
                "Useful for valuing growth companies that are not yet profitable.",
                "intermediate"
        ));
        defs.add(new DefinitionDTO(
                "P/B Ratio",
                "Price-to-Book ratio. Compares market value to the company's book (net asset) value.",
                "A stock at $30 with book value of $15/share has a P/B of 2.",
                "Low P/B can indicate undervaluation; heavily used in value investing screens.",
                "intermediate"
        ));
        defs.add(new DefinitionDTO(
                "ROE",
                "Return on Equity — net income divided by shareholders' equity, measuring how efficiently equity is used.",
                "Net income of $200M on equity of $1B gives ROE of 20%.",
                "Consistently high ROE (>15%) often signals a competitive moat and strong management.",
                "intermediate"
        ));
        defs.add(new DefinitionDTO(
                "Revenue Growth",
                "The rate at which a company's top-line sales have increased over a period.",
                "Revenue increasing from $100M to $120M year-over-year = 20% growth.",
                "Sustained revenue growth is a primary driver of long-term stock appreciation.",
                "intermediate"
        ));
        defs.add(new DefinitionDTO(
                "Debt-to-Equity",
                "Total liabilities divided by shareholders' equity — a leverage ratio.",
                "A company with $500M debt and $1B equity has D/E of 0.5.",
                "Higher D/E amplifies both gains and losses; sectors like utilities naturally carry more debt.",
                "intermediate"
        ));
        defs.add(new DefinitionDTO(
                "Dividend Payout Ratio",
                "The percentage of earnings paid out as dividends to shareholders.",
                "A company earning $4/share and paying $2/share has a payout ratio of 50%.",
                "Very high ratios (>80%) may signal an unsustainable dividend; low ratios leave room for growth.",
                "intermediate"
        ));
        defs.add(new DefinitionDTO(
                "Beta",
                "Measures a stock's volatility relative to the overall market. Beta >1 = more volatile; <1 = less volatile.",
                "A beta of 1.5 means the stock tends to move 50% more than the market on a given day.",
                "High-beta stocks amplify portfolio swings; low-beta stocks provide stability during corrections.",
                "intermediate"
        ));
        defs.add(new DefinitionDTO(
                "Moving Averages",
                "The average closing price of a stock over a specific number of past days (e.g., SMA-20 or SMA-50).",
                "A 50-day SMA averages the closing prices of the last 50 trading days.",
                "Price crossing above the 50-day SMA is a common bullish signal; below is bearish.",
                "intermediate"
        ));

        // ── Advanced (8 terms) ────────────────────────────────────────────────
        defs.add(new DefinitionDTO(
                "Enterprise Value",
                "Total value of a business (market cap + debt − cash). Represents acquisition cost.",
                "Market cap $10B + debt $2B − cash $1B = EV of $11B.",
                "EV/EBITDA is a common multiple used in M&A and buyout valuation.",
                "advanced"
        ));
        defs.add(new DefinitionDTO(
                "ROA",
                "Return on Assets — net income divided by total assets, measuring asset efficiency.",
                "Net income $100M on total assets $1B gives ROA of 10%.",
                "Asset-heavy industries (manufacturing, banking) naturally have lower ROA than software businesses.",
                "advanced"
        ));
        defs.add(new DefinitionDTO(
                "Interest Coverage",
                "EBIT divided by interest expense — measures ability to service debt.",
                "EBIT of $500M with interest expense of $50M = coverage ratio of 10×.",
                "A ratio below 3× is often considered a warning sign of financial distress.",
                "advanced"
        ));
        defs.add(new DefinitionDTO(
                "Quick Ratio",
                "(Cash + Short-term investments + Receivables) / Current liabilities — stricter than current ratio.",
                "If a company has $200M in liquid assets and $150M in current liabilities, quick ratio = 1.33.",
                "Quick ratio < 1 may mean the company cannot cover short-term obligations without selling inventory.",
                "advanced"
        ));
        defs.add(new DefinitionDTO(
                "Asset Turnover",
                "Revenue divided by total assets — measures how efficiently assets generate revenue.",
                "A company with $500M revenue and $1B assets has asset turnover of 0.5.",
                "High turnover is valuable in low-margin businesses; retailers aim for high asset turnover.",
                "advanced"
        ));
        defs.add(new DefinitionDTO(
                "Inventory Turnover",
                "Cost of goods sold divided by average inventory — measures how quickly inventory sells.",
                "COGS of $600M with average inventory of $100M = turnover of 6× per year.",
                "Low turnover can indicate slow-moving inventory or demand weakness.",
                "advanced"
        ));
        defs.add(new DefinitionDTO(
                "RSI",
                "Relative Strength Index — a momentum oscillator measuring speed and change of price movements, 0–100.",
                "An RSI above 70 suggests overbought conditions; below 30 suggests oversold.",
                "RSI divergence from price action can be an early signal of trend reversal.",
                "advanced"
        ));
        defs.add(new DefinitionDTO(
                "Alpha",
                "The excess return of an investment relative to a benchmark index after adjusting for risk.",
                "A fund returning 12% when the S&P 500 returned 10% generated alpha of +2%.",
                "Positive alpha means the investment outperformed on a risk-adjusted basis.",
                "advanced"
        ));

        ALL_DEFINITIONS = Collections.unmodifiableList(defs);
    }

    /**
     * Returns all definitions for the specified difficulty level.
     *
     * @param difficulty "beginner", "intermediate", or "advanced" (case-insensitive)
     * @return matching definitions, or all definitions if difficulty is null/blank
     */
    public List<DefinitionDTO> getDefinitions(String difficulty) {
        if (difficulty == null || difficulty.isBlank()) {
            return ALL_DEFINITIONS;
        }
        String level = difficulty.toLowerCase();
        return ALL_DEFINITIONS.stream()
                .filter(d -> d.getDifficulty().equalsIgnoreCase(level))
                .collect(Collectors.toList());
    }

    /**
     * Generates a shuffled set of multiple-choice quiz questions from the
     * definitions for the given difficulty level.
     *
     * <p>Each question asks "What is [term]?" with the correct definition as one
     * of four options.  Distractor options are drawn from the same difficulty
     * pool so they are plausible but distinct.</p>
     *
     * @param difficulty "beginner", "intermediate", or "advanced"
     * @param count      maximum number of questions to return
     * @return list of quiz questions (may be fewer than {@code count} if not enough definitions)
     */
    public List<QuizQuestionDTO> generateQuiz(String difficulty, int count) {
        List<DefinitionDTO> pool = getDefinitions(difficulty);
        if (pool.isEmpty()) return List.of();

        List<DefinitionDTO> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled);
        int actual = Math.min(count, shuffled.size());

        List<QuizQuestionDTO> questions = new ArrayList<>();
        for (int i = 0; i < actual; i++) {
            DefinitionDTO correct = shuffled.get(i);

            // Pick 3 distractor definitions from the pool (excluding the correct one)
            List<DefinitionDTO> distractors = shuffled.stream()
                    .filter(d -> !d.getTerm().equals(correct.getTerm()))
                    .limit(3)
                    .collect(Collectors.toList());

            if (distractors.size() < 3) continue; // Skip if not enough distractors

            List<String> options = new ArrayList<>();
            options.add(correct.getDefinition());
            distractors.forEach(d -> options.add(d.getDefinition()));
            Collections.shuffle(options);

            int correctIndex = options.indexOf(correct.getDefinition());

            questions.add(new QuizQuestionDTO(
                    "What is " + correct.getTerm() + "?",
                    options,
                    correctIndex,
                    correct.getExample(),
                    correct.getTerm(),
                    difficulty
            ));
        }
        return questions;
    }
}
