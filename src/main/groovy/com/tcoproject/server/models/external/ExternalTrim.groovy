package com.tcoproject.server.models.external

/**
 * Conforms to https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getTrims[Params]
 * Params:
   body=[Coupe, Sedan, SUV, Pickup, Crossover, Minivan, etc.]
   doors=[number of doors]
   drive=[Front, Rear, AWD, 4WD, etc]
   engine_position=[Front, Middle, Rear]
   engine_type=[V, in-line, etc]
   fuel_type=[Gasoline, Diesel, etc]
   full_results=[1 by default. Set to 0 to include only basic year / make /model / trim data (improves load times)]
   keyword=[Keyword search. Searches year, make, model, and trim values]
   make=[Make ID]
   min_cylinders=[Minimum Number of cylinders]
   min_lkm_hwy=[Maximum fuel efficiency (highway, l/100km)]
   min_power=[Minimum engine power (PS)]
   min_top_speed=[Minimum Top Speed (km/h)]
   min_torque=[Minimum Torque (nm)]
   min_weight=[Minimum Weight (kg)]
   min_year=[Earliest Model Year]
   max_cylinders=[Maximum Number of cylinders]
   max_lkm_hwy=[Minimum fuel efficiency (highway, l/100km)]
   max_power=[Minimum engine power (HP)]
   max_top_speed=[Maximum Top Speed (km/h)]
   max_torque=[Maximum Torque (nm)]
   max_weight=[Maximum Weight (kg)]
   max_year=[Latest Model Year]
   model=[Model Name]
   seats=[Number of Seats]
   sold_in_us=[1(sold in US), 0(not sold in US)]
   year=[Model Year]
 */
class ExternalTrim {

    String model_id
    String model_make_id
    String model_name
    String model_trim
    String model_year
    String model_body
    String model_engine_position
    String model_engine_cc
    String model_engine_cyl
    String model_engine_type
    String model_engine_valves_per_cyl
    String model_engine_power_ps
    String model_engine_power_rpm
    String model_engine_torque_nm
    String model_engine_torque_rpm
    String model_engine_bore_mm
    String model_engine_stroke_mm
    String model_engine_compression
    String model_engine_fuel
    String model_top_speed_kph
    String model_0_to_100_kph
    String model_drive
    String model_transmission_type
    String model_seats
    String model_doors
    String model_weight_kg
    String model_length_mm
    String model_width_mm
    String model_height_mm
    String model_wheelbase_mm
    String model_lkm_hwy
    String model_lkm_mixed
    String model_lkm_city
    String model_fuel_cap_l
    String model_sold_in_us
    String model_co2
    String model_make_display
    String make_display
    String make_country

}
