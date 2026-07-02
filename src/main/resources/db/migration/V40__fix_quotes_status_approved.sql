-- Legacy rows used 'APPROVED' for an accepted quote, but the QuoteStatus
-- enum constant is ACCEPTED, which breaks Hibernate's EnumType.STRING mapping.
UPDATE quotes SET status = 'ACCEPTED' WHERE status = 'APPROVED';
