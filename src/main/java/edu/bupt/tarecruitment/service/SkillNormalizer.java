package edu.bupt.tarecruitment.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalises skill labels so that synonyms and variants are treated as equivalent.
 * This is the structured-logic component of the AI matching pipeline: rather than
 * relying on a black-box model, it uses a curated synonym map plus fuzzy string
 * normalisation to make skill comparison more intelligent than exact-string equality.
 *
 * Examples:
 *   "java"          -> "java"
 *   "J2SE"          -> "java"
 *   "sql"           -> "sql"
 *   "MySQL"         -> "sql"
 *   "communication" -> "communication"
 *   "Communication Skills" -> "communication"
 */
public class SkillNormalizer {

    private static final Map<String, String> SYNONYM_MAP = new HashMap<>();

    static {
        // Programming languages & frameworks
        addSynonyms("java", "java", "j2se", "j2ee", "jdk", "jre", "java se", "core java", "java programming");
        addSynonyms("python", "python", "python3", "py");
        addSynonyms("javascript", "javascript", "js", "ecmascript", "es6", "typescript", "ts");
        addSynonyms("cpp", "c++", "cpp", "cplusplus");
        addSynonyms("csharp", "c#", "csharp", "dotnet", ".net");
        addSynonyms("html_css", "html", "css", "html5", "css3", "html/css", "web design");

        // Databases
        addSynonyms("sql", "sql", "mysql", "postgresql", "postgres", "sqlite", "oracle sql",
                "database", "databases", "db", "rdbms", "relational database");

        // Data & ML
        addSynonyms("data_analysis", "data analysis", "data analytics", "excel", "spreadsheet",
                "data processing", "statistics", "stats");
        addSynonyms("machine_learning", "machine learning", "ml", "deep learning", "dl",
                "neural network", "neural networks", "ai", "artificial intelligence");

        // Soft skills
        addSynonyms("communication", "communication", "communication skills", "public speaking",
                "presentation", "presentation skills", "interpersonal");
        addSynonyms("tutoring", "tutoring", "teaching", "mentoring", "coaching", "instruction",
                "grading", "marking");
        addSynonyms("teamwork", "teamwork", "team work", "collaboration", "team player",
                "cooperative");
        addSynonyms("leadership", "leadership", "management", "project management", "pm");

        // Tools & DevOps
        addSynonyms("git", "git", "github", "gitlab", "version control", "svn");
        addSynonyms("linux", "linux", "unix", "ubuntu", "centos", "shell", "bash");
        addSynonyms("docker", "docker", "container", "containerization", "kubernetes", "k8s");

        // Testing
        addSynonyms("testing", "testing", "test", "unit testing", "unit test", "junit",
                "integration testing", "qa", "quality assurance", "software testing");
    }

    /**
     * Register multiple raw labels as synonyms of a single canonical form.
     */
    private static void addSynonyms(String canonical, String... rawLabels) {
        for (String label : rawLabels) {
            SYNONYM_MAP.put(label.toLowerCase().trim(), canonical);
        }
    }

    /**
     * Normalise a skill label to its canonical form.
     * 1. lowercase + trim
     * 2. look up in synonym map
     * 3. if no synonym match, return the cleaned string itself
     */
    public String normalize(String skill) {
        if (skill == null || skill.isBlank()) {
            return "";
        }
        String cleaned = skill.toLowerCase().trim();
        return SYNONYM_MAP.getOrDefault(cleaned, cleaned);
    }

    /**
     * Check whether two skill labels are semantically equivalent
     * after normalisation.
     */
    public boolean areEquivalent(String skillA, String skillB) {
        return normalize(skillA).equals(normalize(skillB));
    }
}
