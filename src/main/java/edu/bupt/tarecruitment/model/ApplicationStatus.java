package edu.bupt.tarecruitment.model;

/**
 * Responsibility: define persisted Application status values.
 * Current baseline:
 * - the minimal confirmed status set is SUBMITTED, APPROVED, REJECTED.
 * - [待确认] Full status transition rules.
 */
public enum ApplicationStatus {
    SUBMITTED,
    APPROVED,
    REJECTED
}