update collection_base_info set penalty_interest = 0 where id = 28885;

update collection_base_info set penalty_interest = 0 where id = 30152;
update collection_base_info set penalty_interest_deduction_amount = 0 where id = 30815;
update collection_base_info set penalty_interest_deduction_amount = 129300 where id = 22057;
update collection_base_info set penalty_interest_deduction_amount = 18895400 where id = 22467;
update collection_base_info set penalty_interest_deduction_amount = 91183000 where id = 23267;
update collection_base_info set penalty_interest_deduction_amount = 1808300 where id = 23983;
update collection_base_info set penalty_interest_deduction_amount = 37358300 where id = 24829;
update collection_base_info set penalty_interest_deduction_amount = 657800 where id = 24859;
update collection_base_info set penalty_interest_deduction_amount = 71967800 where id = 25248;
update collection_base_info set penalty_interest_deduction_amount = 2009600 where id = 27898;
update collection_base_info set penalty_interest_deduction_amount = 39406500 where id = 27965;
update collection_base_info set penalty_interest = 1695800, collection_penalty_interest = 1695800 where id = 24863;

update collection_record_info set collection_date = '2023-10-09 00:00:00' where id in (10265,10266);
