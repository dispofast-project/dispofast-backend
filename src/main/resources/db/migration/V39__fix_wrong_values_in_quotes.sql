UPDATE quotes SET offer_validity = 'DIAS_15' WHERE offer_validity = '15';
UPDATE quotes SET offer_validity = 'DIAS_30' WHERE offer_validity = '30';
UPDATE quotes SET offer_validity = 'DIAS_45' WHERE offer_validity = '45';
UPDATE quotes SET offer_validity = 'DIAS_60' WHERE offer_validity = '60';
UPDATE quotes SET offer_validity = NULL WHERE offer_validity = '0';