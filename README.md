# Raw listing - Make, Model, Trim
select 
  mk.name as make,
  m.name as model, m.year,
  t.name as trim, t.body
from trim t
join model m on t.model_id = m.id
join make mk on m.make_id = mk.id
order by m.year, mk.name, m.name, t.name
limit 10000
;

# Count of Trim by Make, Model
select 
  mk.name as make,
  m.name as model, m.year,
  count(distinct(t.id)) as trim_count
from trim t
join model m on t.model_id = m.id
join make mk on m.make_id = mk.id
group by mk.id, m.id
;

#Count of Trim by Make
select 
  distinct(mk.name), 
  count(t.id) as trim_count
  from make mk
join model m on m.make_id = mk.id
left join trim t on t.model_id = m.id
where mk.is_common = 1
group by mk.name
order by trim_count desc, mk.name
;

#Price Data
select 
  m.year,
  mk.name as make, 
  m.name as model, 
  pd.source, pd.retail_price, pd.suggested_price, pd.is_base_model_price
from price_data pd
join model m on pd.model_id = m.id
join make mk on m.make_id = mk.id
order by m.year desc, m.name, m.name
;



