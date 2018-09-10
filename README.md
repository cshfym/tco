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
  mk.name as make,
  count(distinct(t.id)) as trim_count
from trim t
join model m on t.model_id = m.id
join make mk on m.make_id = mk.id
group by mk.id
;



