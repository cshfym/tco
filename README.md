Fetch model list:
  1.Cron wakes up
  2. Iterates list of available makes (noting manufacturer)
    2a. Iterates available years
      2a1. Calls https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getModels&make=<MAKE_NAME>&year=<YEAR>
      2a2. Persists model with FK on make

Fetch trim list by model and year:
  1. Cron wakes up
  2. Iterates list of available models
   2a. Iterates available years
     2a1. Calls https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getTrims&year=<YEAR>&model=<MODEL_NAME>
     2a2. Persists trim with FK on model


 67.177.50.13
