package edu.bupt.tarecruitment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillNormalizerTest {

    private SkillNormalizer normalizer;

    @BeforeEach
    void setUp() {
        normalizer = new SkillNormalizer();
    }

    // ---------------------------------------- normalize basic

    @Test
    void normalizeReturnsSameForCanonicalLabel() {
        assertEquals("java", normalizer.normalize("java"));
    }

    @Test
    void normalizeHandlesCaseInsensitivity() {
        assertEquals("java", normalizer.normalize("Java"));
        assertEquals("java", normalizer.normalize("JAVA"));
    }

    @Test
    void normalizeTrimsWhitespace() {
        assertEquals("java", normalizer.normalize("  Java  "));
    }

    @Test
    void normalizeMapsJ2seToJava() {
        assertEquals("java", normalizer.normalize("J2SE"));
    }

    @Test
    void normalizeMapsMysqlToSql() {
        assertEquals("sql", normalizer.normalize("MySQL"));
    }

    @Test
    void normalizeMapsTeachingToTutoring() {
        assertEquals("tutoring", normalizer.normalize("Teaching"));
    }

    @Test
    void normalizeMapsCommunicationSkillsToCommunication() {
        assertEquals("communication", normalizer.normalize("Communication Skills"));
    }

    @Test
    void normalizeReturnsCleanedStringForUnknownSkill() {
        assertEquals("blockchain", normalizer.normalize("Blockchain"));
    }

    @Test
    void normalizeReturnsEmptyForNull() {
        assertEquals("", normalizer.normalize(null));
    }

    @Test
    void normalizeReturnsEmptyForBlank() {
        assertEquals("", normalizer.normalize("   "));
    }

    // ---------------------------------------- areEquivalent

    @Test
    void equivalentReturnsTrueForSynonyms() {
        assertTrue(normalizer.areEquivalent("Java", "J2SE"));
        assertTrue(normalizer.areEquivalent("MySQL", "SQL"));
        assertTrue(normalizer.areEquivalent("Teaching", "Tutoring"));
    }

    @Test
    void equivalentReturnsTrueForSameSkillDifferentCase() {
        assertTrue(normalizer.areEquivalent("python", "Python"));
    }

    @Test
    void equivalentReturnsFalseForDifferentSkills() {
        assertFalse(normalizer.areEquivalent("Java", "Python"));
    }

    @Test
    void equivalentReturnsFalseForUnrelatedSkills() {
        assertFalse(normalizer.areEquivalent("Blockchain", "Cooking"));
    }

    // ---------------------------------------- testing tools / DevOps synonyms

    @Test
    void normalizeHandlesGitSynonyms() {
        assertEquals("git", normalizer.normalize("GitHub"));
        assertEquals("git", normalizer.normalize("Version Control"));
    }

    @Test
    void normalizeHandlesTestingSynonyms() {
        assertEquals("testing", normalizer.normalize("JUnit"));
        assertEquals("testing", normalizer.normalize("Unit Testing"));
        assertEquals("testing", normalizer.normalize("QA"));
    }

    @Test
    void normalizeHandlesMachineLearning() {
        assertEquals("machine_learning", normalizer.normalize("Machine Learning"));
        assertEquals("machine_learning", normalizer.normalize("ML"));
        assertEquals("machine_learning", normalizer.normalize("Deep Learning"));
        assertEquals("machine_learning", normalizer.normalize("AI"));
    }
}
