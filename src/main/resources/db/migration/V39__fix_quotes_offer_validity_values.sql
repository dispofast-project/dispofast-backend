-- Legacy rows stored the raw number of days instead of the OfferValidity
-- enum constant name (e.g. '15' instead of 'DIAS_15'), which breaks
-- Hibernate's EnumType.STRING mapping when reading these quotes.
UPDATE quotes SET offer_validity = 'DIAS_15' WHERE offer_validity = '15';
UPDATE quotes SET offer_validity = 'DIAS_30' WHERE offer_validity = '30';
UPDATE quotes SET offer_validity = 'DIAS_45' WHERE offer_validity = '45';
UPDATE quotes SET offer_validity = 'DIAS_60' WHERE offer_validity = '60';
UPDATE quotes SET offer_validity = NULL WHERE offer_validity = '0';
