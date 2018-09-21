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
  t.name,
  pd.trim_display_name, pd.source, pd.retail_price, pd.suggested_price  
from price_data pd
join model m on pd.model_id = m.id
join make mk on m.make_id = mk.id
left join trim t on t.id = pd.trim_id
order by m.year desc, m.name, m.name
;

# Missing Price Data by Make > 1999
select
  mk.name as make,
  m.name as model, m.year,
  t.name as trim, t.body,
  pd.retail_price, pd.suggested_price, pd.date_created as price_date
from trim t
join model m on t.model_id = m.id
join make mk on m.make_id = mk.id
left join price_data pd on pd.model_id = m.id
where mk.name = 'Nissan'
and m.year > 1999
and retail_price is null
and suggested_price is null
order by m.year desc, mk.name, m.name, t.name
limit 10000
;

# Count of price data by make, model
select 
  distinct(mk.name), 
  count(t.id) as trim_count,
  count(pd.id) as price_count
  from make mk
join model m on m.make_id = mk.id
left join trim t on t.model_id = m.id
left join price_data pd on pd.model_id = m.id
where mk.is_common = 1
group by mk.name
order by price_count desc, trim_count desc
;

